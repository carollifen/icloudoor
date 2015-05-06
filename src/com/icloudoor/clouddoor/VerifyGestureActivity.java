package com.icloudoor.clouddoor;

import com.icloudoor.clouddoor.SetGestureDrawLineView.SetGestureCallBack;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class VerifyGestureActivity extends Activity implements OnClickListener {

	private FrameLayout mGestureContainer;
	private SetGestureContentView mGestureContentView;
	private String gesturePwd;
	
	private TextView phoneNum;
	private String phone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.activity_verify_gesture);
		
		phoneNum = (TextView) findViewById(R.id.sign_verify_person_phone);
		SharedPreferences loginStatus = getSharedPreferences(
				"LOGINSTATUS", MODE_PRIVATE);
		phone = loginStatus.getString("PHONENUM", null);
		changeNum();
		phoneNum.setText(phone);
		
		mGestureContainer = (FrameLayout) findViewById(R.id.sign_verify_gesture_container);
		
		gesturePwd = loadSign();  //获取保存过的手势密码
		
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
				mGestureContentView.clearDrawlineState(1500L);
			}
			
		});
		mGestureContentView.setParentView(mGestureContainer);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	public void changeNum(){
		if(phone != null){	
			StringBuilder sb = new StringBuilder(phone); 
			sb.setCharAt(3, '*');
			sb.setCharAt(4, '*');
			sb.setCharAt(5, '*'); 
			sb.setCharAt(6, '*');
			phone = sb.toString();
		}
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
		}
		return super.onKeyDown(keyCode, event);
	}

}
