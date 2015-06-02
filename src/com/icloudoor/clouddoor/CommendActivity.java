package com.icloudoor.clouddoor;

import com.umeng.message.PushAgent;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CommendActivity extends Activity {

	private RelativeLayout back;
	
	private  WebView praiseWebView;
	private String sid;
	
	private int TYPE_GOOD=1;
	
	private WebSettings webSetting;
	private String url = "https://zone.icloudoor.com/icloudoor-web/user/prop/zone/cp/page.do";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commend);

		final TextView Title = (TextView) findViewById(R.id.page_title);
		
		back = (RelativeLayout) findViewById(R.id.btn_back);
		
		PushAgent.getInstance(this).onAppStart();
		sid = loadSid();
		praiseWebView = (WebView) findViewById(R.id.id_praise);
		praiseWebView.addJavascriptInterface(new close(), "cloudoorNative");
		webSetting = praiseWebView.getSettings();

		webSetting.setUseWideViewPort(true);
		webSetting.setJavaScriptEnabled(true);
		webSetting.setLoadWithOverviewMode(true);
		webSetting.setSupportZoom(false);
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		webSetting.setLoadsImagesAutomatically(true);
		webSetting.setBuiltInZoomControls(true);
		praiseWebView.loadUrl(url + "?sid=" + sid + "&type=" + TYPE_GOOD);
		
		WebChromeClient wcc = new WebChromeClient(){
			@Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Title.setText(title);
			}
		};
		
		praiseWebView.setWebChromeClient(wcc);
		
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StringBuilder sb = new StringBuilder();
				String metho = "backPagePop();";
				sb.append("javascript:").append(metho);
				praiseWebView.loadUrl(sb.toString());
			}

		});
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

	public class close {

		@JavascriptInterface
		public void closeWebBrowser() {
			CommendActivity.this.finish();
		}
	}
}
