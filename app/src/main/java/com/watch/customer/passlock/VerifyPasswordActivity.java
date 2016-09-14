package com.watch.customer.passlock;

import android.os.Bundle;
import android.view.KeyEvent;

/**
 * Created by Administrator on 16-3-26.
 */
public class VerifyPasswordActivity extends InputPasswordActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            // 具体的操作代码
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

}
