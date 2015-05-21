package com.icloudoor.clouddoor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.icloudoor.clouddoor.SetGestureDrawLineView.SetGestureCallBack;


public class SetGestureActivity extends Activity implements OnClickListener {

	private FrameLayout mGestureContainer;
	private SetGestureContentView mGestureContentView;
	private String mParamSetUpcode = null;
	private boolean mIsFirstInput = true;
	private String mFirstPassword = null;
	private String mConfirmPassword = null;
	private LockIndicatorView mLockIndicator;
	private int haveSet = 0;
	
//	private TextView TVSignManage;
//	private TextView TVAccount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		setContentView(R.layout.activity_set_gesture);
		
//		TVSignManage = (TextView) findViewById(R.id.sign_set_manage);
//		TVAccount = (TextView) findViewById(R.id.sign_set_account);
//		TVSignManage.setOnClickListener(this);
//		TVAccount.setOnClickListener(this);
		
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
					Toast.makeText(SetGestureActivity.this, R.string.sign_input_again, Toast.LENGTH_SHORT).show();
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
						
						setResult(RESULT_OK);
						
						SetGestureActivity.this.finish();
					} else {
						mGestureContentView.clearDrawlineState(1300L);
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
		
	}
	
	private void updateCodeList(String inputCode) {
		// update the gesture 
		mLockIndicator.setPath(inputCode);
	}
	
	private boolean isInputPassValidate(String inputPassword) {
		if (TextUtils.isEmpty(inputPassword) || inputPassword.length() < 4) {
			return false;
		}
		return true;
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
