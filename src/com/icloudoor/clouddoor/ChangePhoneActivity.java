package com.icloudoor.cloudoor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChangePhoneActivity extends Activity {
	private TextView MyNum;
	private TextView TVBtnChangePhone;
	private RelativeLayout IVBack;
	
	private String phoneNum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		setContentView(R.layout.set_detail_change_phone);
		
		SharedPreferences loginStatus = getSharedPreferences(
				"LOGINSTATUS", MODE_PRIVATE);
		phoneNum = loginStatus.getString("PHONENUM", null);
		changeNum();
		
		IVBack = (RelativeLayout) findViewById(R.id.btn_back_change_phone);
		MyNum = (TextView) findViewById(R.id.my_phone_num);
		MyNum.setText(phoneNum);
		
		TVBtnChangePhone = (TextView) findViewById(R.id.btn_change_phone1);
		TVBtnChangePhone.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(v.getContext(), ChangePhoneActivityOldNum.class);
				startActivity(intent);
			}
			
		});
		IVBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(v.getContext(), SettingDetailActivity.class);
				startActivity(intent);
			}
			
		});
	}
	
	public void changeNum(){
		if(phoneNum != null){	
			StringBuilder sb = new StringBuilder(phoneNum); 
			sb.setCharAt(3, '*');
			sb.setCharAt(4, '*');
			sb.setCharAt(5, '*'); 
			sb.setCharAt(6, '*');
			phoneNum = sb.toString();
		}
	}
}