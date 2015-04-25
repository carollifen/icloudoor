package com.icloudoor.clouddoor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class SignActivity extends Activity{
	private ImageView IvSignSwitch;
	private ImageView back;
	private boolean useSign;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.set_detail_set_sign);
		
		useSign = true;
		
		back = (ImageView) findViewById(R.id.btn_back);
		IvSignSwitch = (ImageView) findViewById(R.id.btn_sign_switch);
		IvSignSwitch.setImageResource(R.drawable.btn_yes);
		IvSignSwitch.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(useSign){
					IvSignSwitch.setImageResource(R.drawable.btn_no);
					useSign = false;
				}else{
					IvSignSwitch.setImageResource(R.drawable.btn_yes);
					useSign = true;
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