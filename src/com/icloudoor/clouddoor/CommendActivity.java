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

public class CommendActivity extends Activity {

	private RelativeLayout back;
	
	private  WebView praiseWebView;
	private String sid;
	
	private int TYPE_GOOD=1;
	
	private WebSettings webSetting;
	private String url = "http://zone.icloudoor.com/icloudoor-web/user/prop/zone/cp/page.do";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commend);

		back = (RelativeLayout) findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		PushAgent.getInstance(this).onAppStart();
		sid = loadSid();
		praiseWebView = (WebView) findViewById(R.id.id_praise);

		webSetting = praiseWebView.getSettings();

		webSetting.setUseWideViewPort(true);
		webSetting.setJavaScriptEnabled(true);
		webSetting.setLoadWithOverviewMode(true);
		webSetting.setSupportZoom(false);
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		webSetting.setLoadsImagesAutomatically(true);
		webSetting.setBuiltInZoomControls(true);
		praiseWebView.loadUrl(url + "?sid=" + sid + "&type=" + TYPE_GOOD);
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
