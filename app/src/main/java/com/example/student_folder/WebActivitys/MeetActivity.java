package com.example.student_folder.WebActivitys;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.student_folder.R;

public class MeetActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet);

        webView = (WebView) findViewById(R.id.webview_meet);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://meet.google.com/");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

    }
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }
}