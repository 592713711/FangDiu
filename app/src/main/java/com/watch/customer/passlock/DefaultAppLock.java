package com.watch.customer.passlock;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.uacent.watchapp.BuildConfig;
import com.watch.customer.util.StringUtils;

import java.util.Arrays;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DefaultAppLock extends AbstractAppLock {

    private Application currentApp; //Keep a reference to the app that invoked the locker
    private SharedPreferences settings;
    private Date lostFocusDate;

    boolean mFirstInput = true;

    public DefaultAppLock(Application currentApp) {
        super();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(currentApp);

        this.settings = settings;
        this.currentApp = currentApp;
    }

    public void enable(){
    	if (android.os.Build.VERSION.SDK_INT < 14)
    		return;

        if( isPasswordLocked() ) {
            currentApp.unregisterActivityLifecycleCallbacks(this);
            currentApp.registerActivityLifecycleCallbacks(this);
        }
    }

    public void disable( ){
    	if (android.os.Build.VERSION.SDK_INT < 14)
    		return;

        currentApp.unregisterActivityLifecycleCallbacks(this);
    }

    public void forcePasswordLock(){
        lostFocusDate = null;
    }

    public boolean verifyPassword( String password ) {
    	String storedPassword = "";
        String newpass = "";
        Log.e("hjq", "password = " + password);
        if (settings.contains(BuildConfig.PASSWORD_PREFERENCE_KEY)) {
    		storedPassword = settings.getString(BuildConfig.PASSWORD_PREFERENCE_KEY, "");
            password = BuildConfig.PASSWORD_SALT + password +  BuildConfig.PASSWORD_SALT;
            newpass = encryptPassword(password);
    	}
//        Log.e("hjq", "password = " + password + " newpass = " + newpass + " stored = " + storedPassword);
//        printChars(newpass);
//        printChars(storedPassword);

        if (storedPassword.equals(newpass)) {
            lostFocusDate = new Date();
            mFirstInput = false;
            return true;
        } else {
            return false;
        }
    }

    void printChars(String s) {
        byte[] array = s.getBytes();
        StringBuilder sb = new StringBuilder();
        char[] chars = "0123456789ABCDEF".toCharArray();
        for (int i = 0; i < array.length; i++) {
            int x = (int)array[i];

            char c = chars[x / 16];

            sb.append("0x");
            sb.append(c);
            c = chars[x%16];
            sb.append(c);
            sb.append(" ");
        }

        Log.e("hjq", "sb =\"" + sb + "\"" );
    }

    public boolean setPassword(String password){
        SharedPreferences.Editor editor = settings.edit();
        Log.e("hjq", "password = " + password);
        if (password == null) {
            editor.remove(BuildConfig.PASSWORD_PREFERENCE_KEY);
            editor.apply();
            this.disable();
        } else {
            password = BuildConfig.PASSWORD_SALT + password +  BuildConfig.PASSWORD_SALT;
            password = encryptPassword(password);
       //     printChars(password);
            editor.putString(BuildConfig.PASSWORD_PREFERENCE_KEY, password);
            editor.apply();
            this.enable();
        }

        return true;
    }

    //Check if we need to show the lock screen at startup
    public boolean isPasswordLocked(){

    	if (settings.contains(BuildConfig.PASSWORD_PREFERENCE_KEY))
    		return true;

    	return false;
    }

    private String encryptPassword(String clearText) {
        try {
            DESKeySpec keySpec = new DESKeySpec(
                    BuildConfig.PASSWORD_ENC_SECRET.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            String encrypedPwd = Base64.encodeToString(cipher.doFinal(clearText
                    .getBytes("UTF-8")), Base64.NO_WRAP);
            return encrypedPwd;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("hjq", "encrypt password failed");
        }
        return clearText;
    }

    private String decryptPassword(String encryptedPwd) {
        try {
            DESKeySpec keySpec = new DESKeySpec(BuildConfig.PASSWORD_ENC_SECRET.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] encryptedWithoutB64 = Base64.decode(encryptedPwd, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] plainTextPwdBytes = cipher.doFinal(encryptedWithoutB64);
            return new String(plainTextPwdBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedPwd;
    }

    private boolean mustShowUnlockSceen() {

        if (!isPasswordLocked()) {
            return false;
        }

        if (lostFocusDate == null ) {
            return true; //first startup or when we forced to show the password
        }

        int currentTimeOut = lockTimeOut; //get a reference to the current password timeout and reset it to default
        lockTimeOut = DEFAULT_TIMEOUT;
        Date now = new Date();
        long now_ms = now.getTime();
        long lost_focus_ms = lostFocusDate.getTime();
        int secondsPassed = (int) (now_ms - lost_focus_ms)/(1000);
        secondsPassed = Math.abs(secondsPassed); //Make sure changing the clock on the device to a time in the past doesn't by-pass PIN Lock
        if (secondsPassed >= currentTimeOut || mFirstInput) {
            lostFocusDate = null;
            return true;
        }

        return false;
    }

    @Override
    public void onActivityPaused(Activity arg0) {
        if (arg0.getClass() == VerifyPasswordActivity.class) {
            return;
        }

        if( ( this.appLockDisabledActivities != null ) && Arrays.asList(this.appLockDisabledActivities).contains( arg0.getClass().getName() ) )
     	   return;

        lostFocusDate = new Date();
    }

    @Override
    public void onActivityResumed(Activity arg0) {
        if (arg0.getClass() == VerifyPasswordActivity.class) {
            return;
        }

       if ((this.appLockDisabledActivities != null ) && Arrays.asList(this.appLockDisabledActivities).contains(arg0.getClass().getName())) {
           return;
       }

        boolean result = mustShowUnlockSceen();
        Log.e("hjq", "onActivityResumed = " + arg0 + " result =" + result);
        if (result) {
            //uhhh ohhh!
            Intent i = new Intent(arg0.getApplicationContext(), VerifyPasswordActivity.class);
            i.putExtra("mode", VerifyPasswordActivity.MODE_VERIFY_UNTIL_OK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            arg0.getApplication().startActivity(i);
        }

    }

    @Override
    public void onActivityCreated(Activity arg0, Bundle arg1) {
    }

    @Override
    public void onActivityDestroyed(Activity arg0) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity arg0, Bundle arg1) {
    }

    @Override
    public void onActivityStarted(Activity arg0) {
    }

    @Override
    public void onActivityStopped(Activity arg0) {
    }
}
