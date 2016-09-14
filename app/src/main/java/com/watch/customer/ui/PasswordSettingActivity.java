package com.watch.customer.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uacent.watchapp.R;
import com.watch.customer.passlock.AbstractAppLock;
import com.watch.customer.passlock.AppLockManager;
import com.watch.customer.passlock.InputPasswordActivity;
import com.watch.customer.passlock.VerifyPasswordActivity;


/**
 * Created by Administrator on 16-3-21.
 */
public class PasswordSettingActivity extends BaseActivity {
    private static final int SETUP_PASSWORD = 1;
    private static final int VERIFY_PASSWORD = 2;
    private static final int TURN_OFF_PASSWORD = 3;

    private TextView txtChangePassword;
    private TextView txtTurnOnPassword;
    AbstractAppLock appLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.password_list_activity);

        appLock = AppLockManager.getInstance().getCurrentAppLock();

        RelativeLayout rl_password_status = (RelativeLayout) findViewById(R.id.ll_turn_password);
        rl_password_status.setOnClickListener(this);

        txtTurnOnPassword = (TextView) findViewById(R.id.text_turn_on_password);
        txtChangePassword = (TextView) findViewById(R.id.text_change_password);
        if (!appLock.isPasswordLocked()) {
            txtChangePassword.setTextColor(getResources().getColor(R.color.TextColorDisable));
            txtTurnOnPassword.setText(R.string.str_turn_on_pass);
        } else {
            txtTurnOnPassword.setText(R.string.str_turn_off_pass);
            RelativeLayout rl_change_password = (RelativeLayout) findViewById(R.id.ll_change_password);
            rl_change_password.setOnClickListener(this);
            txtChangePassword.setTextColor(getResources().getColor(R.color.TextColorBlack));
        }

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_turn_password: {
                if (!appLock.isPasswordLocked()) {
                    Intent intent = new Intent(this, InputPasswordActivity.class);
                    intent.putExtra("mode", InputPasswordActivity.MODE_INPUT);
                    intent.putExtra("title", txtTurnOnPassword.getText());
                    startActivityForResult(intent, SETUP_PASSWORD);
                } else {
                    Intent intent = new Intent(this, InputPasswordActivity.class);
                    intent.putExtra("mode", InputPasswordActivity.MODE_VERIFY);
                    intent.putExtra("title", txtTurnOnPassword.getText());
                    startActivityForResult(intent, TURN_OFF_PASSWORD);
                }

                break;
            }

            case R.id.ll_change_password: {
                Intent intent = new Intent(this, InputPasswordActivity.class);
                intent.putExtra("mode", InputPasswordActivity.MODE_VERIFY);
                intent.putExtra("title", txtChangePassword.getText());
                startActivityForResult(intent, VERIFY_PASSWORD);
                break;
            }

            case R.id.iv_back: {
                finish();
                break;
            }

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("hjq", " request code =" + requestCode + " result = " + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == SETUP_PASSWORD) {
                int val = data.getIntExtra("password_status", 0);

                if (val == 0) {
                    txtChangePassword.setTextColor(getResources().getColor(R.color.TextColorDisable));
                    txtTurnOnPassword.setText(R.string.str_turn_on_pass);
                } else {
                    txtTurnOnPassword.setText(R.string.str_turn_off_pass);
                    RelativeLayout rl_change_password = (RelativeLayout) findViewById(R.id.ll_change_password);
                    rl_change_password.setOnClickListener(this);
                    txtChangePassword.setTextColor(getResources().getColor(R.color.TextColorBlack));
                }

                txtChangePassword.invalidate();
                txtTurnOnPassword.invalidate();
            } else if (requestCode == VERIFY_PASSWORD) {
                int val = data.getIntExtra("password_status", 0);

                if (val == 1) {
                    Intent intent = new Intent(this, InputPasswordActivity.class);
                    intent.putExtra("mode", InputPasswordActivity.MODE_INPUT);
                    startActivityForResult(intent, SETUP_PASSWORD);
                } else {
                    Toast.makeText(this, R.string.str_pass_error, Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == TURN_OFF_PASSWORD) {
                int val = data.getIntExtra("password_status", 0);

                if (val == 1) {
                    appLock.setPassword(null);

                    txtTurnOnPassword = (TextView) findViewById(R.id.text_turn_on_password);
                    txtChangePassword = (TextView) findViewById(R.id.text_change_password);

                    txtTurnOnPassword.setText(R.string.str_turn_on_pass);
                    RelativeLayout rl_change_password = (RelativeLayout) findViewById(R.id.ll_change_password);
                    rl_change_password.setOnClickListener(null);
                    txtChangePassword.setTextColor(getResources().getColor(R.color.TextColorDisable));
                } else {
                    Toast.makeText(this, R.string.str_pass_error, Toast.LENGTH_SHORT).show();
                }

                txtChangePassword.invalidate();
                txtTurnOnPassword.invalidate();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
