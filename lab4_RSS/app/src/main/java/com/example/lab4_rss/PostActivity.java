package com.example.lab4_rss;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


public class PostActivity extends AppCompatActivity {
    private WebView webView;
    private Post post;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        post = getIntent().getParcelableExtra("post");

        webView = findViewById(R.id.webview);

        progressBar = findViewById(R.id.progressBar);
        showPost();
    }

    private void showPost(){
        WebSettings webSett = webView.getSettings();
        webSett.setDefaultTextEncodingName("UTF-8");
        webSett.setBuiltInZoomControls(true);
        webSett.setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                setTitle("Loading...");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                setTitle(view.getTitle());
            }
        });
        webView.loadUrl(post.Link);
    }

}
