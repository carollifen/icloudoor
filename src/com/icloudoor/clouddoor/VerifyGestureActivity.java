package com.icloudoor.cloudoor;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.Volley;
import com.icloudoor.cloudoor.CloudDoorMainActivity.Broadcast;
import com.icloudoor.cloudoor.SetGestureDrawLineView.SetGestureCallBack;
import com.umeng.common.message.Log;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class VerifyGestureActivity extends Activity {

	private FrameLayout mGestureContainer;
	private SetGestureContentView mGestureContentView;
	private String gesturePwd;
	private RequestQueue mQueue;
	private String sid;
	
	private Broadcast mFinishActivityBroadcast;

	
	private TextView phoneNum;
	private String phone;
    private URL logOutURL;
	
	private TextView IVmanageGesture;
 	private TextView IVpswLogin;
	
 	private int times=5;
 	
 	private TextView textTip;
 	
 	private RelativeLayout mback;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_verify_gesture);
		
		IntentFilter intentFilter = new IntentFilter();
	    intentFilter.addAction("com.icloudoor.clouddoor.ACTION_FINISH");
	    mFinishActivityBroadcast = new Broadcast();
	    registerReceiver(mFinishActivityBroadcast, intentFilter);

		mQueue = Volley.newRequestQueue(this);
		
		textTip=(TextView) findViewById(R.id.tip_text);
		textTip.setText(getString(R.string.input_gesture));
		textTip.setTextColor(0xFF666666);
		textTip.setTextSize(17);
		
		registerReceiver(KillVerifyActivityBroadcast,new IntentFilter("KillVerifyActivity"));

	 	IVpswLogin=(TextView) findViewById(R.id.sign_set_account);

	 	IVpswLogin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyDialog dialog = new MyDialog(VerifyGestureActivity.this,R.style.add_dialog,
						getString(R.string.login_pwd), new MyDialog.OnCustomDialogListener() {
							@Override
							public void back(int haveset) {
								
								SharedPreferences firstLoginShare=getSharedPreferences("FIRSTLOGINSHARE", 0);
								Editor mEditor=firstLoginShare.edit();
								mEditor.putBoolean("FIRSTLOGIN", true).commit();

								Intent intent = new Intent();
								intent.setAction("com.icloudoor.clouddoor.ACTION_FINISH");
								sendBroadcast(intent);

								Intent cloudIntent = new Intent(
										VerifyGestureActivity.this,
										CloudDoorMainActivity.class);
								startActivity(cloudIntent);
								VerifyGestureActivity.this.finish();
							}
						});
				dialog.show();
			}
	 		
	 	});
		
		mGestureContainer = (FrameLayout) findViewById(R.id.sign_verify_gesture_container);
		
		gesturePwd = loadSign(); 
		
		mGestureContentView = new SetGestureContentView(this, true, gesturePwd, new SetGestureCallBack() {

			@Override
			public void onGestureCodeInput(String inputCode) {

			}

			@Override
			public void checkedSuccess() {
				Toast.makeText(VerifyGestureActivity.this, R.string.sign_verify_success, Toast.LENGTH_SHORT).show();
				
				SharedPreferences homeKeyEvent = getSharedPreferences("HOMEKEY", 0);
				int homePressed = homeKeyEvent.getInt("homePressed", 0);
				
				if(homePressed == 0){
					Intent intent = new Intent();
					intent.setClass(VerifyGestureActivity.this, CloudDoorMainActivity.class);
					startActivity(intent);
				
				VerifyGestureActivity.this.finish();
				} else {
					homePressed = 0;
					
					Editor editor = homeKeyEvent.edit();
					editor.putInt("homePressed", homePressed);
					editor.commit();

					VerifyGestureActivity.this.finish();
				}
				
			}

			@Override
			public void checkedFail() {
				Toast.makeText(VerifyGestureActivity.this, R.string.sign_verify_fail, Toast.LENGTH_SHORT).show();
				times--;
				if(times>0)
				{
					textTip.setText(getString(R.string.wrong_gesture)+times+getString(R.string.times));
					textTip.setTextColor(0xFFEE2C2C);
					textTip.setTextSize(17);
				}
				else{
					textTip.setText(getString(R.string.no_more_try_input_pwd));
					textTip.setTextColor(0xFFEE2C2C);
					textTip.setTextSize(17);
					 if ("NET_WORKS".equals(loadSid("NETSTATE"))) {
		                    sid = loadSid("SID");

		                    try {
		                        logOutURL = new URL("http://test.zone.icloudoor.com/icloudoor-web" + "/user/manage/logout.do"
		                                + "?sid=" + sid);
		                    } catch (MalformedURLException e) {
		                        e.printStackTrace();
		                    }
		                    MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
		                            Method.POST, logOutURL.toString(), null,
		                            new Response.Listener<JSONObject>() {

		                                @Override
		                                
		                                public void onResponse(JSONObject response) {
		                                    try {
		                                    	Log.e("logout", response.toString()+"sdmfl;knsad;lfglsadjm'l");
//		                                        if (response.getString("sid") != null) {
//		                                            sid = response.getString("sid");
		                                            saveSid("SID", null);
//		                                        }
		                                           
		                                 //   int    statusCode = response.getInt("code");
		                                    if(response.getInt("code")==1)

		                                    {  int  isLogin = 0;
		                                        SharedPreferences loginStatus = VerifyGestureActivity.this.getSharedPreferences("LOGINSTATUS", 0);
		                                        Editor editor1 = loginStatus.edit();
		                                        editor1.putInt("LOGIN", isLogin);
		                                        editor1.commit();
		 
		                                        Intent intent3 = new Intent();
		                                        Bundle bundle = new Bundle();
		                                        bundle.putString("phone", loginStatus.getString("PHONENUM", ""));
		                                        intent3.putExtras(bundle);
		                                     //   intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		                                        intent3.setClass(VerifyGestureActivity.this, Login.class);
		                                        startActivity(intent3);
		                                        Intent broadcastIntent = new Intent();
		                                        broadcastIntent.setAction("com.icloudoor.clouddoor.ACTION_FINISH");
		                                        sendBroadcast(broadcastIntent);
		                                        VerifyGestureActivity.this.finish();      }                             
		                                     
		                                    } catch (JSONException e) {
		                                        e.printStackTrace();
		                                    }
		                                }
		                            }, new Response.ErrorListener() {

		                        @Override
		                        public void onErrorResponse(VolleyError error) {
		                        }
		                    });
		                    mQueue.add(mJsonRequest);
		                 
		                
		                }
				
				}
				mGestureContentView.clearDrawlineState(1000L);
			}
			
		});
		mGestureContentView.setParentView(mGestureContainer);
	}

	private BroadcastReceiver KillVerifyActivityBroadcast = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("KillVerifyActivity")) {
				VerifyGestureActivity.this.finish();
			}
		}
	};
	
	class Broadcast extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			VerifyGestureActivity.this.finish();
		}
		
	}
	
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(KillVerifyActivityBroadcast);
		unregisterReceiver(mFinishActivityBroadcast);
	}
	
	public String loadSign(){
		SharedPreferences loadSign = getSharedPreferences("SAVESIGN", 0);
		return loadSign.getString("SIGN", null);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& KeyEvent.KEYCODE_BACK == keyCode) {
			Intent intent = new Intent();
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setAction(Intent.ACTION_MAIN);
			startActivity(intent);
//			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void saveSid(String key, String value) {
		if(VerifyGestureActivity.this != null){
			SharedPreferences savedSid = VerifyGestureActivity.this.getSharedPreferences(
					"SAVEDSID", 0);
			Editor editor = savedSid.edit();
			editor.putString(key, value);
			editor.commit();
		}
	}

	public String loadSid(String key) {
		SharedPreferences loadSid = VerifyGestureActivity.this.getSharedPreferences(
				"SAVEDSID", 0);
		return loadSid.getString(key, null);
	}

}
