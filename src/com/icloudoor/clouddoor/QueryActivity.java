package com.icloudoor.clouddoor;

import com.umeng.message.PushAgent;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;

public class QueryActivity extends Activity {

	private RelativeLayout back;
	
	private WebView surveyWebView;
	private String sid;
	private String url = "http://zone.icloudoor.com/icloudoor-web/user/prop/zone/survey/page.do";
	private String phonenum;

	private WebSettings webSetting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query);

		sid = loadSid();
		
		back = (RelativeLayout) findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		PushAgent.getInstance(this).onAppStart();
		surveyWebView = (WebView) findViewById(R.id.id_survey);

		webSetting = surveyWebView.getSettings();

		webSetting.setUseWideViewPort(true);
		webSetting.setJavaScriptEnabled(true);
		webSetting.setLoadWithOverviewMode(true);
		webSetting.setSupportZoom(false);
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		webSetting.setLoadsImagesAutomatically(true);
		webSetting.setBuiltInZoomControls(true);

		surveyWebView.loadUrl(url + "?sid=" + sid);
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
