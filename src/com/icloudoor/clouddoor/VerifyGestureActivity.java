package com.icloudoor.clouddoor;

import com.icloudoor.clouddoor.SetGestureDrawLineView.SetGestureCallBack;

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
	
	private TextView phoneNum;
	private String phone;
	
	private TextView IVmanageGesture;
 	private TextView IVpswLogin;
	
 	private int times=3;
 	
 	private TextView textTip;
 	
 	private RelativeLayout mback;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		setContentView(R.layout.activity_verify_gesture);
		
//		mback=(RelativeLayout) findViewById(R.id.verify_gesture_btn_back);
		
		textTip=(TextView) findViewById(R.id.tip_text);
		
		textTip.setText(getString(R.string.input_gesture));
		textTip.setTextColor(0xFF666666);
		textTip.setTextSize(17);
		
//		mback.setOnClickListener(this);
		
		registerReceiver(KillVerifyActivityBroadcast,new IntentFilter("KillVerifyActivity"));
	 //	IVmanageGesture=(TextView) findViewById(R.id.sign_set_manage);
	 	IVpswLogin=(TextView) findViewById(R.id.sign_set_account);
	 	//IVmanageGesture.setOnClickListener(this);
	 	IVpswLogin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyDialog dialog = new MyDialog(VerifyGestureActivity.this,R.style.add_dialog,
						getString(R.string.login_pwd), new MyDialog.OnCustomDialogListener() {
							@Override
							public void back(int haveset) {
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
		
//		phoneNum = (TextView) findViewById(R.id.sign_verify_person_phone);
//		SharedPreferences loginStatus = getSharedPreferences(
//				"LOGINSTATUS", MODE_PRIVATE);
//		phone = loginStatus.getString("PHONENUM", null);
//		changeNum();
//		phoneNum.setText(phone);
		
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
					
//					Intent intent = new Intent();
//					intent.setClass(VerifyGestureActivity.this, CloudDoorMainActivity.class);
//					startActivity(intent);
					
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
	
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		if (v.getId() == R.id.verify_gesture_btn_back) {
//		
//			VerifyGestureActivity.this.finish();
//		} 
//		
//		if (v.getId() == R.id.sign_set_account) {
//			MyDialog dialog = new MyDialog(VerifyGestureActivity.this,R.style.add_dialog,
//					getString(R.string.login_pwd), new MyDialog.OnCustomDialogListener() {
//						@Override
//						public void back(int haveset) {
//							Intent cloudIntent = new Intent(
//									VerifyGestureActivity.this,
//									CloudDoorMainActivity.class);
//							startActivity(cloudIntent);
//							VerifyGestureActivity.this.finish();
//						}
//					});
//			dialog.show();
//		}
//	}
	
//	public void changeNum(){
//		if(phone != null){	
//			StringBuilder sb = new StringBuilder(phone); 
//			sb.setCharAt(3, '*');
//			sb.setCharAt(4, '*');
//			sb.setCharAt(5, '*'); 
//			sb.setCharAt(6, '*');
//			phone = sb.toString();
//		}
//	}
	
	
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(KillVerifyActivityBroadcast);
	}
	
	public String loadSign(){
		SharedPreferences loadSign = getSharedPreferences("SAVESIGN", 0);
		return loadSign.getString("SIGN", null);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& KeyEvent.KEYCODE_BACK == keyCode) {
//			Intent intent = new Intent();
//			intent.addCategory(Intent.CATEGORY_HOME);
//			intent.setAction(Intent.ACTION_MAIN);
//			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
