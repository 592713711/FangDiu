package com.watch.customer.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.uacent.watchapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 16-3-21.
 */
public class InputPasswordActivity extends BaseActivity {
    List<EditText> mEditTextList = new ArrayList<EditText>(4);
    List<TextWatcher> mWatcherList = new ArrayList<TextWatcher>(4);

    int mMode;
    static final int MODE_VERIFY = 1;
    static final int MODE_INPUT = 2;
    static final int MODE_REINPUT = 3;
    private SharedPreferences mSharedPreferences;
    char[] mPassword = new char[4];
    char[] mPassword2 = new char[4];
    TextView mTextPrompt;
    TextView mTextFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mMode = intent.getIntExtra("mode", MODE_INPUT);
        String title = intent.getStringExtra("title");

        setContentView(R.layout.input_password_activity);

        EditText editText1 = (EditText) findViewById(R.id.editText1);
        EditText editText2 = (EditText) findViewById(R.id.editText2);
        EditText editText3 = (EditText) findViewById(R.id.editText3);
        EditText editText4 = (EditText) findViewById(R.id.editText4);

        mEditTextList.add(editText1);
        mEditTextList.add(editText2);
        mEditTextList.add(editText3);
        mEditTextList.add(editText4);

        TextWatcher watcher1 = new TextFilter(editText1);
        TextWatcher watcher2 = new TextFilter(editText2);
        TextWatcher watcher3 = new TextFilter(editText3);
        TextWatcher watcher4 = new TextFilter(editText4);
        mWatcherList.add(watcher1);
        mWatcherList.add(watcher2);
        mWatcherList.add(watcher3);
        mWatcherList.add(watcher4);

        for (int i = 0; i < mEditTextList.size(); i++) {
            mEditTextList.get(i).addTextChangedListener(mWatcherList.get(i));
            mEditTextList.get(i).setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        }

        mTextPrompt = (TextView) findViewById(R.id.tv_password_prompt);
        mTextFeedback = (TextView) findViewById(R.id.tv_password_feedback);

        mSharedPreferences = getSharedPreferences("watch_app_preference", 0);
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_text);
        if (mMode == MODE_VERIFY) {
            tvTitle.setText("Welcome back");
        }

        if (title != null) {
            tvTitle.setText(title);
        }

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: {
                finish();
                break;
            }

            default:
                break;
        }

        super.onClick(v);
    }

    private class TextFilter implements TextWatcher {
        private EditText mEditText;

        public TextFilter(EditText editText) {
            mEditText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            int index = getEditTextIndex();
            if (index < 0) {
                Log.e("hjq", "input passowrd edittext index is -1");
                return;
            }
            String s = mEditText.getText().toString();

            if (s.length() == 0) {

            } else {
                char c = s.charAt(0);
                if (mMode == MODE_INPUT || mMode == MODE_VERIFY) {
                    mPassword[index] = c;
                } else if (mMode == MODE_REINPUT) {
                    mPassword2[index] = c;
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() == 0) {
                return;
            }

            if (forwardFocus()) {

            } else {
                if (mMode == MODE_VERIFY) {
                    String oldPass = mSharedPreferences.getString("password", "");
                    String newPass = new String(mPassword);
                    boolean result = oldPass.equals(newPass);

                    Intent intent = new Intent();
                    Bundle b = new Bundle();
                    b.putBoolean("verify_status", result);
                    intent.putExtras(b);
                    //设置返回数据
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (mMode == MODE_INPUT) {
                    mMode = MODE_REINPUT;
                    resetEditTextControlls();
                    mTextPrompt.setText("Re-enter your password");
                } else if (mMode == MODE_REINPUT) {
                    String pass1 = new String(mPassword);
                    String pass2 = new String(mPassword2);

                    Log.d("hjq", "pass1 = " + pass1 + " pass2 = " + pass2);

                    if (pass1.equals(pass2)) {
                        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                        mEditor.putString("password", pass1);
                        mEditor.putInt("password_status", 1);
                        mEditor.apply();

                        Intent intent = new Intent();
                        Bundle b = new Bundle();
                        b.putInt("password_status", 1);
                        intent.putExtras(b);
                        //设置返回数据
                        setResult(RESULT_OK, intent);

                        finish();
                    } else {
                        mTextFeedback.setVisibility(View.VISIBLE);
                        mTextFeedback.setText("Not match, try again.");
                        mMode = MODE_INPUT;
                        resetEditTextControlls();
                    }
                }
            }
        }

        int getEditTextIndex() {
            int i;

            for (i = 0; i < mEditTextList.size(); i++) {
                if (mEditTextList.get(i) == mEditText) {
                    return i;
                }
            }

            return -1;
        }

        // 返回 true 表示未输完四个数字,返回 false 表示已经输完成4个数字
        boolean forwardFocus() {
            int i;
            EditText next = null;

            for (i = 0; i < mEditTextList.size() - 1; i++) {
                if (mEditTextList.get(i) == mEditText) {
                    next = mEditTextList.get(i + 1);
                    break;
                }
            }

            if (next != null) {
                next.requestFocus();
            }

            if (i == mEditTextList.size() - 1) {
                return false;
            } else {
                return true;
            }
        }
    }

    private void resetEditTextControlls() {
        if (mMode == MODE_INPUT) {
            resetPassword(mPassword2);
            resetPassword(mPassword);
        } else if (mMode == MODE_REINPUT) {
            resetPassword(mPassword2);
        }

        for (int i = 0; i < mEditTextList.size(); i++) {
            mEditTextList.get(i).removeTextChangedListener(mWatcherList.get(i));
            mEditTextList.get(i).setText("");
            mEditTextList.get(i).addTextChangedListener(mWatcherList.get(i));
        }


        EditText first = mEditTextList.get(0);
        first.requestFocus();
    }

    private void resetPassword(char[] pass) {
        int i;
        for (i = 0; i < pass.length; i++) {
            pass[i] = ' ';
        }
    }
}
