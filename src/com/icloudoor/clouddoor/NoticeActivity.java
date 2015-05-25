package com.icloudoor.clouddoor;

import com.umeng.message.PushAgent;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class NoticeActivity extends Activity {

	private WebView anouncePageWebView;

	private String sid;
	private WebSettings anouncewebSetting;
	private WebSettings detailwebSetting;
	private WebView anounceDetailWebView;
	private String pageurl = "http://zone.icloudoor.com/icloudoor-web/user/prop/zone/notice/page.do";

	private String detailurl = "http://zone.icloudoor.com/icloudoor-web/user/prop/zone/notice/detail.do";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);

		PushAgent.getInstance(this).onAppStart();
		anouncePageWebView = (WebView) findViewById(R.id.id_public_anounce_page);
		anounceDetailWebView = (WebView) findViewById(R.id.id_public_anounce_detail);

		anouncewebSetting = anouncePageWebView.getSettings();

		anouncewebSetting.setUseWideViewPort(true);
		anouncewebSetting.setJavaScriptEnabled(true);
		anouncewebSetting.setLoadWithOverviewMode(true);
		anouncewebSetting.setSupportZoom(false);
		anouncewebSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		anouncewebSetting.setLoadsImagesAutomatically(true);
		anouncewebSetting.setBuiltInZoomControls(true);

		detailwebSetting = anounceDetailWebView.getSettings();

		detailwebSetting.setUseWideViewPort(true);
		detailwebSetting.setJavaScriptEnabled(true);
		detailwebSetting.setLoadWithOverviewMode(true);
		detailwebSetting.setSupportZoom(false);
		detailwebSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		detailwebSetting.setLoadsImagesAutomatically(true);
		detailwebSetting.setBuiltInZoomControls(true);
		sid = loadSid();

		anouncePageWebView.loadUrl(pageurl + "?sid=" + sid);
		anounceDetailWebView.loadUrl(detailurl + "?sid=" + sid);

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