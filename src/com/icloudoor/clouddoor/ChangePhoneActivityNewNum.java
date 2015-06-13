package com.icloudoor.cloudoor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChangePhoneActivityNewNum extends Activity {
	private TextView TVBtnChangePhone;
	private RelativeLayout IVBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		setContentView(R.layout.set_detail_change_phone2);
		
		IVBack = (RelativeLayout) findViewById(R.id.btn_back_change_phone);
		TVBtnChangePhone = (TextView) findViewById(R.id.btn_change_phone3);
		TVBtnChangePhone.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(v.getContext(), ChangePhoneActivityCertiCode.class);
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
}