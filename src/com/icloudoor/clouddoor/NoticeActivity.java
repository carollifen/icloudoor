package com.icloudoor.clouddoor;

import com.umeng.message.PushAgent;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

public class NoticeActivity extends Activity {

	private WebView anouncePageWebView;
	private RelativeLayout back;

	private String sid;
	private WebSettings anouncewebSetting;
	private String pageurl = "http://zone.icloudoor.com/icloudoor-web/user/prop/zone/notice/page.do";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);
		
		back = (RelativeLayout) findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});

		PushAgent.getInstance(this).onAppStart();
		anouncePageWebView = (WebView) findViewById(R.id.id_public_anounce_page);

		anouncewebSetting = anouncePageWebView.getSettings();

		anouncewebSetting.setUseWideViewPort(true);
		anouncewebSetting.setJavaScriptEnabled(true);
		anouncewebSetting.setLoadWithOverviewMode(true);
		anouncewebSetting.setSupportZoom(false);
		anouncewebSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		anouncewebSetting.setLoadsImagesAutomatically(true);
		anouncewebSetting.setBuiltInZoomControls(true);

		anouncePageWebView.setWebViewClient(new webViewClient()); 
		
		sid = loadSid();

		anouncePageWebView.loadUrl(pageurl + "?sid=" + sid);

	}

	class webViewClient extends WebViewClient{        //override shouldOverrideUrlLoading method to use the webview to response when click the link     
		@Override     
		public boolean shouldOverrideUrlLoading(WebView view, String url) {         
			view.loadUrl(url);          
			return true;  
		}
	}
	
	public void saveSid(String sid) {
		SharedPreferences savedSid = getApplicationContext()
				.getSharedPreferences("SAVEDSID", 0);
		Editor editor = savedSid.edit();
		editor.putString("SID", sid);
		editor.commit();
	}

	public String loadSid() {
		SharedPreferences loadSid = getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}

}
