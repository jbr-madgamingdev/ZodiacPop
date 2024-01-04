package com.nativegame.animalspop.helpers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nativegame.animalspop.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WebActivity extends Activity {

    private WebView webView;
    private String loadUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getStringExtra("url");
        Log.e("TAG", "url  =" + url);

        if (TextUtils.isEmpty(url)) {
            finish();
        }
        com.alibaba.fastjson.JSONObject object = JSON.parseObject(url);
        loadUrl = object.getString("url");
        Log.e("TAG", "loadUrl  =" + loadUrl);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        webView = new WebView(this);
        setSetting();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                Log.e("TAG", " url  = " + url);
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    WebActivity.this.finish();
                    return true;
                } catch (Exception e) {
                    return true;
                }

            }

        });
        webView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        webView.loadUrl(loadUrl);
        TextView textView = new TextView(this);
        textView.setOnClickListener(view -> {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        });
        textView.setText("X ");
        textView.setTextColor(Color.RED);
        textView.setTextSize(25);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 20, 20, 10);
        textView.setLayoutParams(layoutParams);
        textView.setId(View.generateViewId());
        ((RelativeLayout.LayoutParams) (textView.getLayoutParams())).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        relativeLayout.addView(webView);
        relativeLayout.addView(textView);
        setContentView(relativeLayout);
    }

    private void setSetting() {
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setDomStorageEnabled(true);
        setting.setCacheMode(WebSettings.LOAD_DEFAULT);
        setting.setAllowContentAccess(true);
        setting.setDatabaseEnabled(true);
        setting.setGeolocationEnabled(true);
        setting.setUseWideViewPort(true);
        setting.setUserAgentString(setting.getUserAgentString().replaceAll("; wv", ""));
        setting.setMediaPlaybackRequiresUserGesture(false);
        setting.setSupportZoom(false);
        try {
            Class<?> clazz = setting.getClass();
            Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
            method.invoke(setting, true);

        } catch (IllegalArgumentException | NoSuchMethodException | IllegalAccessException
                 | InvocationTargetException e) {
            e.printStackTrace();
        }
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(url);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}