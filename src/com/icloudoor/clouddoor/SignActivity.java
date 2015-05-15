package com.icloudoor.clouddoor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SignActivity extends Activity{
	private ImageView IvSignSwitch;
	private RelativeLayout back;
	private RelativeLayout changeSign;
	private RelativeLayout forgetSign;
	private int useSign;
	private int haveSet;
	
	LayoutInflater inflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.set_detail_set_sign);
		
		inflater = LayoutInflater.from(this);
		
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
						startActivityForResult(intent, 0);
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == 0 && resultCode == RESULT_OK) {
			Log.e("Test Sign", "onActivityResult");
			setContentView(R.layout.set_detail_set_sign_now);

			back = (RelativeLayout) findViewById(R.id.btn_back);
			IvSignSwitch = (ImageView) findViewById(R.id.btn_sign_switch);

			if(useSign == 1)
				IvSignSwitch.setImageResource(R.drawable.btn_yes);
			else
				IvSignSwitch.setImageResource(R.drawable.btn_no);
			IvSignSwitch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (useSign == 1) {
						IvSignSwitch.setImageResource(R.drawable.btn_no);
						useSign = 0;

						SharedPreferences setting = getSharedPreferences(
								"SETTING", MODE_PRIVATE);
						Editor editor = setting.edit();
						editor.putInt("useSign", useSign);
						editor.commit();
					} else {
						IvSignSwitch.setImageResource(R.drawable.btn_yes);
						useSign = 1;

						SharedPreferences setting = getSharedPreferences(
								"SETTING", MODE_PRIVATE);
						Editor editor = setting.edit();
						editor.putInt("useSign", useSign);
						editor.commit();

					}
				}

			});
			back.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}

			});
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		Log.e("Test Sign", "onResume");

		
		SharedPreferences setSign = getSharedPreferences("SETSIGN", 0);
		haveSet = setSign.getInt("HAVESETSIGN", 0);
		
		if(haveSet == 1){
			setContentView(R.layout.set_detail_set_sign_now);

			back = (RelativeLayout) findViewById(R.id.btn_back);
			IvSignSwitch = (ImageView) findViewById(R.id.btn_sign_switch);
			changeSign = (RelativeLayout) findViewById(R.id.btn_change_sign);
			forgetSign = (RelativeLayout) findViewById(R.id.btn_forget_sign);

			if(useSign == 1)
				IvSignSwitch.setImageResource(R.drawable.btn_yes);
			else
				IvSignSwitch.setImageResource(R.drawable.btn_no);
			IvSignSwitch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (useSign == 1) {
						IvSignSwitch.setImageResource(R.drawable.btn_no);
						useSign = 0;

						SharedPreferences setting = getSharedPreferences(
								"SETTING", MODE_PRIVATE);
						Editor editor = setting.edit();
						editor.putInt("useSign", useSign);
						editor.commit();
					} else {
						IvSignSwitch.setImageResource(R.drawable.btn_yes);
						useSign = 1;

						SharedPreferences setting = getSharedPreferences(
								"SETTING", MODE_PRIVATE);
						Editor editor = setting.edit();
						editor.putInt("useSign", useSign);
						editor.commit();

					}
				}

			});
			back.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}

			});
			
			changeSign.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Log.e("TEsT", "changeSign clicked!");
					Intent confirmIntent=new Intent(SignActivity.this,ConfirmGestureActivity.class);
					startActivity(confirmIntent);
				}
				
			});
			
			forgetSign.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.e("TEsT", "forgetSign clicked!");
					MyDialog myDialog = new MyDialog(SignActivity.this, getString(R.string.login_pwd),
							new MyDialog.OnCustomDialogListener() {

								@Override
								public void back(int haveset) {
									SignActivity.this.haveSet = haveset;
									SharedPreferences setSign = getSharedPreferences(
											"SETSIGN", 0);
									Editor editor = setSign.edit();
									editor.putInt("HAVESETSIGN",
											SignActivity.this.haveSet);
									editor.commit();
									Log.e("forgetOncreate",
											String.valueOf(haveSet));
									useSign = 0;
									SharedPreferences setting = getSharedPreferences(
											"SETTING", MODE_PRIVATE);
									Editor useSigneditor = setting.edit();
									useSigneditor.putInt("useSign", useSign);
									useSigneditor.commit();

									setContentView(R.layout.set_detail_set_sign);
									back = (RelativeLayout) findViewById(R.id.btn_back);
									IvSignSwitch = (ImageView) findViewById(R.id.btn_sign_switch);

									useSign = setting.getInt("useSign", 0);
									if (useSign == 1)
										IvSignSwitch
												.setImageResource(R.drawable.btn_yes);
									else
										IvSignSwitch
												.setImageResource(R.drawable.btn_no);
									IvSignSwitch
											.setOnClickListener(new android.view.View.OnClickListener() {

												@Override
												public void onClick(View v) {
													// TODO Auto-generated
													// method stub
													if (useSign == 1) {
														IvSignSwitch
																.setImageResource(R.drawable.btn_no);
														useSign = 0;

														SharedPreferences setting = getSharedPreferences(
																"SETTING",
																MODE_PRIVATE);
														Editor editor = setting
																.edit();
														editor.putInt(
																"useSign",
																useSign);
														editor.commit();
													} else {
														IvSignSwitch
																.setImageResource(R.drawable.btn_yes);
														useSign = 1;

														SharedPreferences setting = getSharedPreferences(
																"SETTING",
																MODE_PRIVATE);
														Editor editor = setting
																.edit();
														editor.putInt(
																"useSign",
																useSign);
														editor.commit();

														if (haveSet == 0) {
															Intent intent = new Intent();
															intent.setClass(
																	SignActivity.this,
																	SetGestureActivity.class);
															startActivity(intent);
														}
													}
												}

											});

									back.setOnClickListener(new android.view.View.OnClickListener() {

										@Override
										public void onClick(View v) {
											finish();
										}

									});

								}
							});

					// SignActivity.this.onPause();
					myDialog.show();
				}

			});
			
		
		}else if(haveSet==0) {
			setContentView(R.layout.set_detail_set_sign);
			back = (RelativeLayout) findViewById(R.id.btn_back);
			IvSignSwitch = (ImageView) findViewById(R.id.btn_sign_switch);
			
			SharedPreferences setting = getSharedPreferences(
					"SETTING", MODE_PRIVATE);
			useSign = setting.getInt("useSign", 0);
			if(useSign == 1)
				IvSignSwitch.setImageResource(R.drawable.btn_yes);
			else
				IvSignSwitch.setImageResource(R.drawable.btn_no);
			IvSignSwitch.setOnClickListener(new android.view.View.OnClickListener(){
				
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
						
						if(haveSet == 0	) 
						{
							Intent intent = new Intent();
							intent.setClass(SignActivity.this, SetGestureActivity.class);
							startActivity(intent);
						}
					}
				}
				
			});
			
			back.setOnClickListener(new android.view.View.OnClickListener(){

				@Override
				public void onClick(View v) {
					finish();
				}
				
			});
			
			
		}
	}
	
	
}