package com.android.phikaso.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.phikaso.R;

public class NumberSearchActivity extends Activity {
    private WebView numberSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_number_search);

        numberSearch = findViewById(R.id.numberSearch);

        numberSearch.getSettings().setJavaScriptEnabled(true);
        numberSearch.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        numberSearch.setWebChromeClient(new WebChromeClient());
        numberSearch.setWebViewClient(new WebViewClient());
        numberSearch.getSettings().setAllowFileAccessFromFileURLs(true);
        numberSearch.getSettings().setAllowUniversalAccessFromFileURLs(true);
        numberSearch.loadUrl("file:///android_asset/numberSearch.html");
    }
}
