package com.icloudoor.clouddoor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TestWebViewActivity extends Activity {

	private WebView webview;
	private String sid;
	private String url = "http://zone.icloudoor.com/icloudoor-web/user/prop/cp/page.do";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.activity_test_web_view);
		
		webview = (WebView) findViewById(R.id.wv);
		
		sid = loadSid();
		
		webview.getSettings().setJavaScriptEnabled(true);
		webview.loadUrl(url + "?sid=" + sid + "&type=" + "1");
		
		webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {

			}

			@Override
			public void onProgressChanged(WebView view, int progress) {

			}

		});
		webview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
	}

	public String loadSid() {
		SharedPreferences loadSid = getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}
	
}
