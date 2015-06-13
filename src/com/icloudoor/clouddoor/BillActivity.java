package com.icloudoor.cloudoor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BillActivity extends Activity {

	private RelativeLayout back;
	private WebView billWebView;
	private String sid;
	private String url = "http://test.zone.icloudoor.com/icloudoor-web/user/prop/zone/payment/bill.do";
	private WebSettings webSetting;
	private Broadcast mFinishActivityBroadcast;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bill);
		
		mFinishActivityBroadcast=	new Broadcast();
		 IntentFilter intentFilter = new IntentFilter();
		    intentFilter.addAction("com.icloudoor.clouddoor.ACTION_FINISH");
		    registerReceiver(mFinishActivityBroadcast, intentFilter);


		
		final TextView Title = (TextView) findViewById(R.id.page_title);
		
		back = (RelativeLayout) findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		sid = loadSid();
		
		billWebView = (WebView) findViewById(R.id.id_costlist);
		webSetting = billWebView.getSettings();

		webSetting.setUseWideViewPort(true);
		webSetting.setJavaScriptEnabled(true);
		webSetting.setLoadWithOverviewMode(true);
		webSetting.setSupportZoom(false);
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		webSetting.setLoadsImagesAutomatically(true);
		webSetting.setBuiltInZoomControls(true);

		billWebView.loadUrl(url + "?sid=" + sid);
		
		WebChromeClient wcc = new WebChromeClient(){
			@Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Title.setText(title);
			}
			
			@Override
			public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
				// TODO Auto-generated method stub
				if (consoleMessage.message().contains("backPagePop is not defined")) {
					finish();
				}
				return super.onConsoleMessage(consoleMessage);
			}
		};
		
		billWebView.setWebChromeClient(wcc);
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
	
	@Override
	public void onResume() {
		super.onResume();
		
		SharedPreferences homeKeyEvent = getSharedPreferences("HOMEKEY", 0);
		int homePressed = homeKeyEvent.getInt("homePressed", 0);

		SharedPreferences Sign = getSharedPreferences("SETTING", 0);
		int usesign = Sign.getInt("useSign", 0);

		if (homePressed == 1 && usesign == 1) {
			Intent intent = new Intent();
			intent.setClass(BillActivity.this, VerifyGestureActivity.class);
			startActivity(intent);
		}	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mFinishActivityBroadcast);
		
	}
	
	class Broadcast extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			BillActivity.this.finish();
		}
		
	}
}
