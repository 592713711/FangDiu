package com.watch.customer.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.uacent.watchapp.R;
import com.watch.customer.util.CommonUtil;
import com.watch.customer.util.HttpUtil;
import com.watch.customer.util.JsonUtil;
import com.watch.customer.util.ThreadPoolManager;

import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;


/**
 * Created by Administrator on 16-3-7.
 */
public class InfoActivity  extends BaseActivity  {
    WebView webView;
    String mHtml;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                webView.loadUrl(HttpUtil.SERVER + "/ble" + "/" + mHtml );
            } else if (msg.what == 1) {
                webView.loadUrl("file:///android_res/raw/" + mHtml);
            }
        }
    };

    class MyWebviewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            //  super.onReceivedError(view, request, error);
            Log.e("hjq", "load error " + error);
            webView.loadUrl("file:///android_res/raw/" + mHtml);
        }

        @Override
        public void onReceivedHttpError(
                WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            Log.e("hjq", "load error " + errorResponse);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info);

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new MyWebviewClient());

        WebSettings settings = webView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        String country = getResources().getConfiguration().locale.getCountry();
        Log.e("hjq", "country = " + country);

        if ("CN".equals(country)) {
            mHtml = "usage_cn.html";
        } else {
            mHtml = "usage_en.html";
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ThreadPoolManager.getInstance().addTask(new Runnable() {
                    @Override
                    public void run() {
                        String result = null;
                        int what = 0;
                        try {
                            result = HttpUtil.get(HttpUtil.SERVER + "/ble" + "/" + mHtml);
                            Log.e("hjq", "result = " + result);
                        } catch (IOException e) {
                            e.printStackTrace();
                            result = e.getMessage();
                            what = 1;
                        }

                        Message msg = new Message();
                        msg.obj = result;
                        msg.what = what;
                        mHandler.sendMessage(msg);
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
