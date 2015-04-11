package com.icloudoor.clouddoor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingDetailActivity extends Activity {
	private TextView TVBtnResetPwd;
	private TextView TVBtnChangePhone;
	
	private ImageView IVSetDetailShake;
	private ImageView IVSetDetailSound;
	private ImageView IVSetDetailDisturb;
	private ImageView IVSwitchCar;
	private ImageView IVSwitchMan;
	
	private ImageView IVBack;
	
	private boolean canShake, haveSound, canDisturb, switchToCar;
	private MyBtnOnClickListener mMyBtnOnClickListener;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.set_detail);
		
		TVBtnResetPwd = (TextView) findViewById(R.id.btn_reset_pwd);
		TVBtnChangePhone = (TextView) findViewById(R.id.btn_change_phone);
		
		IVSetDetailShake = (ImageView) findViewById(R.id.btn_set_detail_shake);
		IVSetDetailSound = (ImageView) findViewById(R.id.btn_set_detail_sound);
		IVSetDetailDisturb = (ImageView) findViewById(R.id.btn_set_detail_disturb);
		IVSwitchCar = (ImageView) findViewById(R.id.btn_switch_car);
		IVSwitchMan = (ImageView) findViewById(R.id.btn_switch_man);
		
		mMyBtnOnClickListener = new MyBtnOnClickListener();
		
		TVBtnResetPwd.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(v.getContext(), ResetPwdActivity.class);
				startActivity(intent);
			}
			
		});
		TVBtnChangePhone.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(v.getContext(), ChangePhoneActivity.class);
				startActivity(intent);
			}
			
		});
		
		InitBtns();
		IVSetDetailShake.setOnClickListener(mMyBtnOnClickListener);
		IVSetDetailSound.setOnClickListener(mMyBtnOnClickListener);
		IVSetDetailDisturb.setOnClickListener(mMyBtnOnClickListener);
		IVSwitchCar.setOnClickListener(mMyBtnOnClickListener);
		IVSwitchMan.setOnClickListener(mMyBtnOnClickListener);
	}
	
	public void InitBtns(){
		canShake = true;
		haveSound = true;
		canDisturb = true;
		switchToCar = false;
		
		IVSetDetailShake.setImageResource(R.drawable.btn_yes);
		IVSetDetailSound.setImageResource(R.drawable.btn_yes);
		IVSetDetailDisturb.setImageResource(R.drawable.btn_yes);
		IVSwitchCar.setImageResource(R.drawable.icon_car_gray);
		IVSwitchMan.setImageResource(R.drawable.icon_ren);
	}
	
	public class MyBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn_set_detail_shake:
				if(canShake){
					IVSetDetailShake.setImageResource(R.drawable.btn_no);
					canShake = false;
				}else{
					IVSetDetailShake.setImageResource(R.drawable.btn_yes);
					canShake = true;
				}
				break;
			case R.id.btn_set_detail_sound:
				if(haveSound){
					IVSetDetailSound.setImageResource(R.drawable.btn_no);
					haveSound = false;
				}else{
					IVSetDetailSound.setImageResource(R.drawable.btn_yes);
					haveSound = true;
				}
				break;
			case R.id.btn_set_detail_disturb:
				if(canDisturb){
					IVSetDetailDisturb.setImageResource(R.drawable.btn_no);
					canDisturb = false;
				}else{
					IVSetDetailDisturb.setImageResource(R.drawable.btn_yes);
					canDisturb = true;
				}
				break;
			case R.id.btn_switch_car:
			case R.id.btn_switch_man:
				if(switchToCar){
					switchToCar = false;
					IVSwitchCar.setImageResource(R.drawable.icon_car_gray);
					IVSwitchMan.setImageResource(R.drawable.icon_ren);
				}else{
					switchToCar = true;
					IVSwitchCar.setImageResource(R.drawable.icon_car);
					IVSwitchMan.setImageResource(R.drawable.icon_ren_gray);
				}
				break;
			}
		}
		
	}
}