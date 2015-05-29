package com.icloudoor.clouddoor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.umeng.message.PushAgent;

public class QueryActivity extends Activity {

	private RelativeLayout back;

	private SharedPreferences queryShare;
	private Editor queryEditor;

	private WebView surveyWebView;
	private String sid;
	private String url = "http://zone.icloudoor.com/icloudoor-web/user/prop/zone/survey/page.do";

	private String HOST = "http://zone.icloudoor.com/icloudoor-web";
	private String phonenum;

	private WebSettings webSetting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query);

		queryShare = getApplicationContext().getSharedPreferences("queryShare",
				0);
		queryEditor = queryShare.edit();

		sid = loadSid();

		back = (RelativeLayout) findViewById(R.id.btn_back);

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

		if (queryShare.getString("QUERYURL", null) != null) {
			surveyWebView.loadUrl(HOST + queryShare.getString("QUERYURL", null)
					+ "?sid=" + sid);
			queryEditor.putString("QUERYURL", null).commit();
		} else {
			surveyWebView.loadUrl(url + "?sid=" + sid);
		}

		surveyWebView.setWebViewClient(new webViewClient());

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (surveyWebView.canGoBack())
					surveyWebView.goBack();
				else
					finish();
			}

		});
	}

	class webViewClient extends WebViewClient { // override
												// shouldOverrideUrlLoading
												// method to use the webview to
												// response when click the link
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
