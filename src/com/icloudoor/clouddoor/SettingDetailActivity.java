package com.icloudoor.clouddoor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingDetailActivity extends Activity {
	private RelativeLayout TVBtnResetPwd;
	private TextView TVBtnChangePhone;
	
	private ImageView IVSetDetailShake;
	private ImageView IVSetDetailSound;
	private ImageView IVSetDetailDisturb;
	private ImageView IVSwitchCar;
	private ImageView IVSwitchMan;
	
	private RelativeLayout IVBack;
	
	private int canShake, haveSound, canDisturb, switchToCar;
	private MyBtnOnClickListener mMyBtnOnClickListener;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		setContentView(R.layout.set_detail);
		
		TVBtnResetPwd = (RelativeLayout) findViewById(R.id.btn_reset_pwd);
		TVBtnChangePhone = (TextView) findViewById(R.id.btn_change_phone);
		
		IVSetDetailShake = (ImageView) findViewById(R.id.btn_set_detail_shake);
		IVSetDetailSound = (ImageView) findViewById(R.id.btn_set_detail_sound);
		IVSetDetailDisturb = (ImageView) findViewById(R.id.btn_set_detail_disturb);
		IVSwitchCar = (ImageView) findViewById(R.id.btn_switch_car);
		IVSwitchMan = (ImageView) findViewById(R.id.btn_switch_man);
		IVBack = (RelativeLayout) findViewById(R.id.btn_back_set_detail);
		
		mMyBtnOnClickListener = new MyBtnOnClickListener();
		
		TVBtnResetPwd.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(v.getContext(), ResetPwdActivity.class);
				startActivity(intent);
			}
			
		});
//		TVBtnChangePhone.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(v.getContext(), ChangePhoneActivity.class);
//				startActivity(intent);
//			}
//			
//		});
		IVBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//to save the setting detail
				SharedPreferences setting = getSharedPreferences("SETTING",
						MODE_PRIVATE);
				Editor editor = setting.edit();
				editor.putInt("chooseCar", switchToCar);
				editor.putInt("disturb", canDisturb);
				editor.putInt("sound", haveSound);
				editor.putInt("shake", canShake);
				editor.commit();
				finish();		
			}
			
		});
		
		InitBtns();
		IVSetDetailShake.setOnClickListener(mMyBtnOnClickListener);
		IVSetDetailSound.setOnClickListener(mMyBtnOnClickListener);
		IVSetDetailDisturb.setOnClickListener(mMyBtnOnClickListener);
		IVSwitchCar.setOnClickListener(mMyBtnOnClickListener);
		IVSwitchMan.setOnClickListener(mMyBtnOnClickListener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences homeKeyEvent = getSharedPreferences("HOMEKEY", 0);
		int homePressed = homeKeyEvent.getInt("homePressed", 0);

		SharedPreferences setSign = getSharedPreferences("SETTING", 0);
		int useSign = setSign.getInt("useSign", 0);
		if (homePressed == 1 && useSign == 1) {
			Intent intent = new Intent();
			intent.setClass(SettingDetailActivity.this,
					VerifyGestureActivity.class);
			startActivity(intent);
		}
	}
	
	public void InitBtns(){
		
		SharedPreferences setting = getSharedPreferences("SETTING", 0);		
		canShake = setting.getInt("shake", 0);
		haveSound = setting.getInt("sound", 1);
		canDisturb = setting.getInt("disturb", 1);
		switchToCar = setting.getInt("chooseCar", 1);
		
		if(canShake == 1)
			IVSetDetailShake.setImageResource(R.drawable.btn_yes);
		else
			IVSetDetailShake.setImageResource(R.drawable.btn_no);
		
		if(haveSound == 1)
			IVSetDetailSound.setImageResource(R.drawable.btn_yes);
		else
			IVSetDetailSound.setImageResource(R.drawable.btn_no);

		if(canDisturb == 1)
			IVSetDetailDisturb.setImageResource(R.drawable.btn_yes);
		else
			IVSetDetailDisturb.setImageResource(R.drawable.btn_no);
		
		if(switchToCar == 1){
			IVSwitchCar.setImageResource(R.drawable.icon_car);
			IVSwitchMan.setImageResource(R.drawable.icon_ren_gray);
		}else{
			IVSwitchCar.setImageResource(R.drawable.icon_car_gray);
			IVSwitchMan.setImageResource(R.drawable.icon_ren);
		}		
	}
	
	public class MyBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btn_set_detail_shake:
				if(canShake == 1){
					IVSetDetailShake.setImageResource(R.drawable.btn_no);
					canShake = 0;
				}else{
					IVSetDetailShake.setImageResource(R.drawable.btn_yes);
					canShake = 1;
				}
				break;
			case R.id.btn_set_detail_sound:
				if(haveSound == 1){
					IVSetDetailSound.setImageResource(R.drawable.btn_no);
					haveSound = 0;
				}else{
					IVSetDetailSound.setImageResource(R.drawable.btn_yes);
					haveSound = 1;
				}
				break;
			case R.id.btn_set_detail_disturb:
				if(canDisturb == 1){
					IVSetDetailDisturb.setImageResource(R.drawable.btn_no);
					canDisturb = 0;
				}else{
					IVSetDetailDisturb.setImageResource(R.drawable.btn_yes);
					canDisturb = 1;
				}
				break;
			case R.id.btn_switch_car:
			case R.id.btn_switch_man:
				if(switchToCar == 1){
					switchToCar = 0;
					IVSwitchCar.setImageResource(R.drawable.icon_car_gray);
					IVSwitchMan.setImageResource(R.drawable.icon_ren);
				}else{
					switchToCar = 1;
					IVSwitchCar.setImageResource(R.drawable.icon_car);
					IVSwitchMan.setImageResource(R.drawable.icon_ren_gray);
				}
				break;
			}
		}
		
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { 
            //do something here
            SharedPreferences setting = getSharedPreferences("SETTING",
                    MODE_PRIVATE);
            Editor editor = setting.edit();
            editor.putInt("chooseCar", switchToCar);
            editor.putInt("disturb", canDisturb);
            editor.putInt("sound", haveSound);
            editor.putInt("shake", canShake);
            editor.commit();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}