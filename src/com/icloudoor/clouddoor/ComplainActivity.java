package com.icloudoor.clouddoor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class ComplainActivity extends Activity {

	private WebView complainWebView;
	private String sid;

	private int TYPE_BAD = 2;

	private WebSettings webSetting;
	private String url = "http://zone.icloudoor.com/icloudoor-web/user/prop/zone/cp/page.do";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_complain);

		complainWebView = (WebView) findViewById(R.id.id_complain);
		webSetting = complainWebView.getSettings();

		webSetting.setUseWideViewPort(true);
		webSetting.setJavaScriptEnabled(true);
		webSetting.setLoadWithOverviewMode(true);
		webSetting.setSupportZoom(false);
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		webSetting.setLoadsImagesAutomatically(true);
		webSetting.setBuiltInZoomControls(true);

		sid = loadSid();

		complainWebView.loadUrl(url + "?sid=" + sid + "&type=" + TYPE_BAD);
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
