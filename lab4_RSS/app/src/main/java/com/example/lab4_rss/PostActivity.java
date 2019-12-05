package com.example.lab4_rss;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;


public class PostActivity extends AppCompatActivity {
    private WebView webView;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        post = getIntent().getParcelableExtra("post");

        webView = findViewById(R.id.webview);

        showPost();

    }

    private void showPost(){
        WebSettings webSett = webView.getSettings();
        webSett.setDefaultTextEncodingName("UTF-8");
        webSett.setBuiltInZoomControls(true);
        webView.loadUrl(post.Link);
    }

}
