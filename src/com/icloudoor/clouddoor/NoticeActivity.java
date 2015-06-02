package com.icloudoor.clouddoor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.message.PushAgent;

public class NoticeActivity extends Activity {

	private SharedPreferences noticeUrlShare;
	private Editor noticeUrlEditor;

	private WebView anouncePageWebView;
	private RelativeLayout back;

	private String sid;
	private String str;
	private WebSettings anouncewebSetting;

	private WebView anounceDetailWebView;
	private String pageurl = "https://zone.icloudoor.com/icloudoor-web/user/prop/zone/notice/page.do";
	private String HOST = "https://zone.icloudoor.com/icloudoor-web";
	private String detailurl = "https://zone.icloudoor.com/icloudoor-web/user/prop/zone/notice/detail.do";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);
		noticeUrlShare = getApplicationContext().getSharedPreferences("noticeUrlShare", 0);
		noticeUrlEditor = noticeUrlShare.edit();
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);

		final TextView Title = (TextView) findViewById(R.id.page_title);
		
		back = (RelativeLayout) findViewById(R.id.btn_back);

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

		if (noticeUrlShare.getString("NOTICEURL", null) != null) {

			anouncePageWebView.loadUrl(HOST + noticeUrlShare.getString("NOTICEURL", null) + "&sid=" + sid);
			noticeUrlEditor.putString("NOTICEURL", null).commit();

		} else {
			anouncePageWebView.loadUrl(pageurl + "?sid=" + sid);
		}

		anouncePageWebView.setWebViewClient(new webViewClient());
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (anouncePageWebView.canGoBack())
					anouncePageWebView.goBack();
				else
					finish();
			}

		});
		
		WebChromeClient wcc = new WebChromeClient(){
			@Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Title.setText(title);
			}
		};
		anouncePageWebView.setWebChromeClient(wcc);
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
