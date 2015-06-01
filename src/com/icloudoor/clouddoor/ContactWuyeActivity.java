package com.icloudoor.clouddoor;

import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

public class ContactWuyeActivity extends Activity {

	private WebView webview;
	private RelativeLayout back;
	
	private String sid;
	private URL newurl;
	private String url = "https://zone.icloudoor.com/icloudoor-web/user/prop/zone/contact/page.do";
	private RequestQueue requestQueue;
	
	private String phonenum;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		setContentView(R.layout.activity_contact_wuye);

		back = (RelativeLayout) findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		webview = (WebView) findViewById(R.id.webview);

		sid = loadSid();

		webview.getSettings().setJavaScriptEnabled(true);
		webview.addJavascriptInterface(new Contact(), "cloudoorNative");
		webview.loadUrl(url + "?sid=" + sid);

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

	public class Contact {

		@JavascriptInterface
		public void callout(final String phone) {
			runOnUiThread(new Runnable() {
				public void run() {
					JSONObject jsObj;
					try {
						jsObj = new JSONObject(phone);
						String phonenum = jsObj.getString("phoneNum");
						startActivity(new Intent(Intent.ACTION_DIAL,
								Uri.parse("tel:" + phonenum)));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

		}

	}

}
