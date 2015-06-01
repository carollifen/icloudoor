package com.icloudoor.clouddoor;

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

public class PayActivity extends Activity {

	private RelativeLayout back;
	private WebView payWebView;
	private String sid;
	private String url = "https://zone.icloudoor.com/icloudoor-web/user/prop/zone/payment/pay.do";
	private WebSettings webSetting;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);
		
		sid = loadSid();
		
		payWebView = (WebView) findViewById(R.id.id_pay);
		webSetting = payWebView.getSettings();

		webSetting.setUseWideViewPort(true);
		webSetting.setJavaScriptEnabled(true);
		webSetting.setLoadWithOverviewMode(true);
		webSetting.setSupportZoom(false);
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		webSetting.setLoadsImagesAutomatically(true);
		webSetting.setBuiltInZoomControls(true);
		
		payWebView.setWebViewClient(new webViewClient()); 

		payWebView.loadUrl(url + "?sid=" + sid);
		
		back = (RelativeLayout) findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(payWebView.canGoBack())
					payWebView.goBack();
				else 
					finish();
			}
			
		});
	}
	
	class webViewClient extends WebViewClient{        //override shouldOverrideUrlLoading method to use the webview to response when click the link     
		@Override     
		public boolean shouldOverrideUrlLoading(WebView view, String url) {         
			view.loadUrl(url);          
			return true;  
		}
	}
	
	public void saveSid(String sid) {
		SharedPreferences savedSid = getApplicationContext().getSharedPreferences("SAVEDSID",
				0);
		Editor editor = savedSid.edit();
		editor.putString("SID", sid);
		editor.commit();
	}
	

	public String loadSid() {
		SharedPreferences loadSid = getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}
}
