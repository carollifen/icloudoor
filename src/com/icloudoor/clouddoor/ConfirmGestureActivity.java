package com.icloudoor.clouddoor;

import com.icloudoor.clouddoor.ForGetDialog.ForGetDialogInterface;
import com.icloudoor.clouddoor.SetGestureDrawLineView.SetGestureCallBack;

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
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmGestureActivity extends Activity implements OnClickListener {
	private FrameLayout mGestureContainer;
	private SetGestureContentView mGestureContentView;
	private String gesturePwd;
	private TextView confirmWithPsw;
	
	private RelativeLayout mback;
	
	private Broadcast mFinishActivityBroadcast;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		
		setContentView(R.layout.activity_confirmgesture);
		
		mFinishActivityBroadcast=	new Broadcast();
		 IntentFilter intentFilter = new IntentFilter();
		    intentFilter.addAction("com.icloudoor.clouddoor.ACTION_FINISH");
		    registerReceiver(mFinishActivityBroadcast, intentFilter);





		
		mback=(RelativeLayout) findViewById(R.id.confirm_gesture_btn_back);
		
		mback.setOnClickListener(this);
		
		mGestureContainer = (FrameLayout) findViewById(R.id.sign_confirm_gesture_container);
		
		confirmWithPsw=(TextView) findViewById(R.id.confirm_with_passw);
		
		confirmWithPsw.setOnClickListener(this);
		
		gesturePwd = loadSign(); //get the saved pws
		
		registerReceiver(KillConfirmActivityBroadcast, new IntentFilter("KillConfirmActivity"));
		
		mGestureContentView = new SetGestureContentView(this, true, gesturePwd, new SetGestureCallBack() {

			@Override
			public void onGestureCodeInput(String inputCode) {
				
			}

			@Override
			public void checkedSuccess() {
				Toast.makeText(ConfirmGestureActivity.this, R.string.reset_the_gesture_pwd, Toast.LENGTH_SHORT).show();
				
				
					Intent intent = new Intent();
					intent.setClass(ConfirmGestureActivity.this, SetGestureActivity.class);
					startActivity(intent);
				
					ConfirmGestureActivity.this.finish();
				
			}

			@Override
			public void checkedFail() {
				Toast.makeText(ConfirmGestureActivity.this, R.string.sign_verify_fail, Toast.LENGTH_SHORT).show();
				mGestureContentView.clearDrawlineState(1500L);
			}
			
		});
		mGestureContentView.setParentView(mGestureContainer);
	}
	
	
	public String loadSign(){
		SharedPreferences loadSign = getSharedPreferences("SAVESIGN", 0);
		return loadSign.getString("SIGN", null);
	}

	private  BroadcastReceiver KillConfirmActivityBroadcast=new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action =intent.getAction();
			if(action.equals("KillConfirmActivity"))
			{
				ConfirmGestureActivity.this.finish();
			}
		}
		
	};
	
	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences homeKeyEvent = getSharedPreferences("HOMEKEY", 0);
		int homePressed = homeKeyEvent.getInt("homePressed", 0);

		SharedPreferences setSign = getSharedPreferences("SETTING", 0);
		int useSign = setSign.getInt("useSign", 0);

		if (homePressed == 1 && useSign == 1) {
			Intent intent = new Intent();
			intent.setClass(ConfirmGestureActivity.this,
					VerifyGestureActivity.class);
			startActivity(intent);
		}
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v.getId()==R.id.confirm_gesture_btn_back)
		{
			ConfirmGestureActivity.this.finish();
		}
		if(v.getId()==R.id.confirm_with_passw)
		{
			
			new ForGetDialog(ConfirmGestureActivity.this, R.style.add_dialog, null, new ForGetDialogInterface() {
				
				@Override
				public void back(int haveset) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(ConfirmGestureActivity.this,SetGestureActivity.class);
					startActivity(intent);
				}
			}).show();
			
//			MyDialog mdialog =new MyDialog(ConfirmGestureActivity.this,R.style.add_dialog, getString(R.string.login_pwd) , new MyDialog.OnCustomDialogListener() {
//				
//				@Override
//				public void back(int haveset) {
//					
//					SharedPreferences setSign = getSharedPreferences("SETSIGN", 0);
//					Editor editor = setSign.edit();
//					editor.putInt("HAVESETSIGN", 0);
//					editor.commit();
//					SharedPreferences setting = getSharedPreferences(
//							"SETTING", MODE_PRIVATE);
//					Editor useSigneditor = setting.edit();
//					useSigneditor.putInt("useSign", 0);
//					useSigneditor.commit();
//				}
//			});
//			mdialog.show();
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mFinishActivityBroadcast);
		unregisterReceiver(KillConfirmActivityBroadcast);
		
	}
	
	class Broadcast extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			ConfirmGestureActivity.this.finish();
		}
		
	}
	
}
