package com.example.rssclientest;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class Web_view extends Activity {
	private WebView webView;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_view);
		
		Bundle bundle = this.getIntent().getExtras();
		String postContent = bundle.getString("content");
		
		webView = (WebView) this.findViewById(R.id.webview);
		webView.loadData(postContent, "text/html; charset=utf-8", "utf-8");
	}
}
