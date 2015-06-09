package com.icloudoor.clouddoor;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

public class cloudApplication extends Application {
	private PushAgent mPushAgent;
	private SharedPreferences noticeUrlShare;
	private Editor noticeUrlEditor;
	
	private SharedPreferences queryShare;
	private Editor queryEditor;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mPushAgent = PushAgent.getInstance(getApplicationContext());
		mPushAgent.enable();
		
		UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
		    @Override
		    public void dealWithCustomAction(Context context, UMessage msg) {
		    	
		    	noticeUrlShare=context.getSharedPreferences("noticeUrlShare", 0);
		    	noticeUrlEditor=noticeUrlShare.edit();
		    	queryShare=context.getSharedPreferences("queryShare", 0);
		    	queryEditor=queryShare.edit();
		    	Log.e("push", msg.custom.toString());
		    	try {
					JSONObject customJson=new JSONObject(msg.custom.toString());

					Intent intent=new Intent();
					if(customJson.getString("url").indexOf("?")!=-1)
					{
					intent.setClass(context, NoticeActivity.class);
					noticeUrlEditor.putString("NOTICEURL", customJson.getString("url")).commit();
					}
					else
					{
						intent.setClass(context, QueryActivity.class);
						queryEditor.putString("QUERYURL",  customJson.getString("url")).commit();
					}
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		       // Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
		       // WebView webview=new WebView(getApplicationContext());
		        
		    }
		};
		mPushAgent.setNotificationClickHandler(notificationClickHandler);
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
	}

}
