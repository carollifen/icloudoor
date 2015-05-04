package com.icloudoor.clouddoor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SignActivity extends Activity{
	private ImageView IvSignSwitch;
	private RelativeLayout back;
	private int useSign;
	private int haveSet;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.set_detail_set_sign);
		
		SharedPreferences setSign = getSharedPreferences("SETSIGN", 0);
		haveSet = setSign.getInt("HAVESETSIGN", 0);
		
		back = (RelativeLayout) findViewById(R.id.btn_back);
		IvSignSwitch = (ImageView) findViewById(R.id.btn_sign_switch);
		
		SharedPreferences setting = getSharedPreferences("SETTING", 0);
		useSign = setting.getInt("useSign", 0);
		if(useSign == 1)
			IvSignSwitch.setImageResource(R.drawable.btn_yes);
		else
			IvSignSwitch.setImageResource(R.drawable.btn_no);
			
		IvSignSwitch.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(useSign == 1){
					IvSignSwitch.setImageResource(R.drawable.btn_no);
					useSign = 0;
					
					SharedPreferences setting = getSharedPreferences("SETTING",
							MODE_PRIVATE);
					Editor editor = setting.edit();
					editor.putInt("useSign", useSign);
					editor.commit();
				}else{
					IvSignSwitch.setImageResource(R.drawable.btn_yes);
					useSign = 1;
					
					SharedPreferences setting = getSharedPreferences("SETTING",
							MODE_PRIVATE);
					Editor editor = setting.edit();
					editor.putInt("useSign", useSign);
					editor.commit();
					
					if(haveSet == 0) {
						Intent intent = new Intent();
						intent.setClass(SignActivity.this, SetGestureActivity.class);
						startActivity(intent);
					}
				}
			}
			
		});
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
	}
	
}