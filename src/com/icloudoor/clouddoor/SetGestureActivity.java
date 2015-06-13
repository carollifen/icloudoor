package com.icloudoor.cloudoor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.icloudoor.cloudoor.R.id;
import com.icloudoor.cloudoor.SetGestureDrawLineView.SetGestureCallBack;


public class SetGestureActivity extends Activity implements OnClickListener {

	private FrameLayout mGestureContainer;
	private SetGestureContentView mGestureContentView;
	private String mParamSetUpcode = null;
	private boolean mIsFirstInput = true;
	private String mFirstPassword = null;
	private String mConfirmPassword = null;
	private LockIndicatorView mLockIndicator;
	private int haveSet = 0;
	
	private RelativeLayout mback;
	
	private TextView textTip;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_set_gesture);
		
		mback=(RelativeLayout) findViewById(R.id.set_gesture_btn_back);
		mback.setOnClickListener(this);

		textTip=(TextView) findViewById(R.id.text_tip);
		setUpViews();
	}
	
	private void setUpViews() {
		mLockIndicator = (LockIndicatorView) findViewById(R.id.lock_indicator);
		mGestureContainer = (FrameLayout) findViewById(R.id.gesture_container);
		// init the viewGroup
		mGestureContentView = new SetGestureContentView(this, false, "", new SetGestureCallBack() {
			@Override
			public void onGestureCodeInput(String inputCode) {
				if (!isInputPassValidate(inputCode)) {
					mGestureContentView.clearDrawlineState(0L);
					return;
				}
				if (mIsFirstInput) {
					mFirstPassword = inputCode;
					updateCodeList(inputCode);
					textTip.setText(getString(R.string.draw_gesture_again));
					textTip.setTextColor(0xFF666666);
					textTip.setTextSize(17);
					mGestureContentView.clearDrawlineState(0L);
				} else {
					if (inputCode.equals(mFirstPassword)) {
						Toast.makeText(SetGestureActivity.this, R.string.sign_set_success, Toast.LENGTH_SHORT).show();
						mGestureContentView.clearDrawlineState(0L);
						
						saveSign(mFirstPassword);  
						
						haveSet = 1;  
						SharedPreferences setSign = getSharedPreferences("SETSIGN", 0);
						Editor set = setSign.edit();
						set.putInt("HAVESETSIGN", haveSet);
						set.commit();
						SharedPreferences setSign1 = getSharedPreferences("SETTING", 0);
						Editor set1 = setSign1.edit();
                        set1.putInt("useSign", 1);
						set1.commit();

						setResult(RESULT_OK);
						
						SetGestureActivity.this.finish();
					} else {
						
						textTip.setText(getString(R.string.draw_diff_gesture));
						textTip.setTextColor(0xFFEE2C2C);
						textTip.setTextSize(17);
						mGestureContentView.clearDrawlineState(1000L);
					}
				}
				mIsFirstInput = false;
			}

			@Override
			public void checkedSuccess() {
				
			}

			@Override
			public void checkedFail() {
				
			}
		});
		// to show the gesture
		mGestureContentView.setParentView(mGestureContainer);
		updateCodeList("");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.set_gesture_btn_back)
		{
			SetGestureActivity.this.finish();
		}
	}
	
	private void updateCodeList(String inputCode) {
		// update the gesture 
		mLockIndicator.setPath(inputCode);
	}
	
	private boolean isInputPassValidate(String inputPassword) {
		if (TextUtils.isEmpty(inputPassword) || inputPassword.length() < 4) {
			
			textTip.setText(getString(R.string.at_least_four_points));
			textTip.setTextColor(0xFFEE2C2C);
			textTip.setTextSize(17);
			return false;
		}
		
		textTip.setText(getString(R.string.draw_gesture));
		textTip.setTextColor(0xFF666666);
		textTip.setTextSize(17);
		return true;
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
			intent.setClass(SetGestureActivity.this,
					VerifyGestureActivity.class);
			startActivity(intent);
		}
	}
	
	public void saveSign(String signPwd) {
		SharedPreferences saveSign = getSharedPreferences("SAVESIGN", 0);
		Editor editor = saveSign.edit();
		editor.putString("SIGN", signPwd);
		editor.commit();
	}	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		SharedPreferences setting = getSharedPreferences("SETTING",
				MODE_PRIVATE);
		Editor useSigneditor = setting.edit();
		useSigneditor.putInt("useSign", 0);
		useSigneditor.commit();
		return super.onKeyDown(keyCode, event);

	}
}
