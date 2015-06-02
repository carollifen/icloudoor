package com.icloudoor.clouddoor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ForgetPwdComplete extends Activity implements TextWatcher{
	private EditText ETInputPwd;
	private EditText ETConfirmPwd;
	private TextView TVConfirm;
	private String inputPwd, confirmPwd;
	private RelativeLayout BtnBack;
	
	private URL registerURL;
	private RequestQueue mQueue;	
	private int statusCode;
	private String HOST = "https://zone.icloudoor.com/icloudoor-web";
	private String sid = null;
	
	// for new ui
	private RelativeLayout pwdLayout;
	private RelativeLayout pwdAgainLayout;
	private RelativeLayout forgetCompleteLayout;
//	private RelativeLayout ShowPwd;
//	private ImageView IVPwdIcon;
//	private boolean isHiddenPwd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		setContentView(R.layout.find_pwd_complete);
		
		ETInputPwd = (EditText) findViewById(R.id.forget_pwd_input_new_pwd);
		ETConfirmPwd = (EditText) findViewById(R.id.forget_pwd_input_new_pwd_again);
		TVConfirm = (TextView) findViewById(R.id.forget_pwd_confirm);
		
		//for new ui
				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				int screenWidth = dm.widthPixels;
				
//				ShowPwd = (RelativeLayout) findViewById(R.id.show_pwd);
//				IVPwdIcon = (ImageView) findViewById(R.id.btn_show_pwd);
				pwdLayout = (RelativeLayout) findViewById(R.id.regi_input_pwd_layout);
				pwdAgainLayout = (RelativeLayout) findViewById(R.id.regi_input_pwd_again_layout);
				forgetCompleteLayout = (RelativeLayout) findViewById(R.id.regi_complete_layout);
				
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) pwdLayout.getLayoutParams();
				RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) forgetCompleteLayout.getLayoutParams();
				RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) pwdAgainLayout.getLayoutParams();
				params.width = screenWidth - 48*2;
				params1.width = screenWidth - 48*2;
				params2.width = screenWidth - 48*2;
				pwdLayout.setLayoutParams(params);
				forgetCompleteLayout.setLayoutParams(params1);
				pwdAgainLayout.setLayoutParams(params2);
				
				pwdLayout.setBackgroundResource(R.drawable.shape_input_certi_code);
				pwdAgainLayout.setBackgroundResource(R.drawable.shape_input_certi_code);
				forgetCompleteLayout.setBackgroundResource(R.drawable.shape_regi_complete_disable);
				
				TVConfirm.setTextColor(0xFF999999);
				forgetCompleteLayout.setEnabled(false);
				
//				isHiddenPwd = true;
//				IVPwdIcon.setImageResource(R.drawable.hide_pwd_new);
//				ShowPwd.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						if (isHiddenPwd) {
//							isHiddenPwd = false;
//							ETInputPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//							IVPwdIcon.setImageResource(R.drawable.show_pwd_new);
//						} else {
//							isHiddenPwd = true;
//							ETInputPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
//							IVPwdIcon.setImageResource(R.drawable.hide_pwd_new);
//						}
//
//					}
//
//				});
				
				//
		
		TVConfirm.setTextColor(0xFF999999);
		forgetCompleteLayout.setEnabled(false);
		
		ETInputPwd.addTextChangedListener(this);
		ETConfirmPwd.addTextChangedListener(this);
		
		BtnBack = (RelativeLayout) findViewById(R.id.btn_back);
		BtnBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), ForgetPwdActivity.class);
				startActivity(intent);
				
				ForgetPwdComplete.this.finish();
			}
			
		});
		
		mQueue = Volley.newRequestQueue(this);
		sid = loadSid();
		
		forgetCompleteLayout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				try {
					registerURL = new URL(HOST + "/user/manage/changePassword2.do"
							+ "?sid=" + sid);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
				inputPwd = ETInputPwd.getText().toString();
				confirmPwd = ETConfirmPwd.getText().toString();
				if(inputPwd.equals(confirmPwd)){
					MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
							Method.POST, registerURL.toString(), null,
							new Response.Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject response) {
									try {
										if (response.getString("sid") != null) {
											sid = response.getString("sid");
											saveSid(sid);
										}
										statusCode = response.getInt("code");
									} catch (JSONException e) {
										e.printStackTrace();
									}
									Log.e("TEST", "statusCode: " + String.valueOf(statusCode));
									Log.e("TEST", "response: " + response.toString());
									try {
										Log.e("TEST", "sid: " + response.getString("sid"));
									} catch (JSONException e) {
										e.printStackTrace();
									}

									if (statusCode == 1) {
//										Intent intent = new Intent();
//										intent.setClass(getApplicationContext(), Login.class);
//										startActivity(intent);
										
										setResult(RESULT_OK);
										finish();
									} else if (statusCode == -41) {
										Toast.makeText(getApplicationContext(), R.string.weak_pwd,
												Toast.LENGTH_SHORT).show();
									}
								}
							}, new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {

								}
							}) {
						@Override
						protected Map<String, String> getParams()
								throws AuthFailureError {
							Map<String, String> map = new HashMap<String, String>();
							map.put("newPassword", confirmPwd);
							return map;
						}
					};
					mQueue.add(mJsonRequest);
				}else
					Toast.makeText(v.getContext(), R.string.diff_pwd, Toast.LENGTH_SHORT).show();
			}
			
		});
	}
	
	public void saveSid(String sid) {
		SharedPreferences savedSid = getSharedPreferences("SAVEDSID", MODE_PRIVATE);
		Editor editor = savedSid.edit();
		editor.putString("SID", sid);
		editor.commit();
	}
	
	public String loadSid() {
		SharedPreferences loadSid = getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}
	   
	@Override
	public void afterTextChanged(Editable s) {
		if(ETInputPwd.getText().toString().length() > 5 && ETConfirmPwd.getText().toString().length() > 5){
			TVConfirm.setTextColor(getResources().getColorStateList(R.color.text_confirm_pwd));
			forgetCompleteLayout.setEnabled(true);
			forgetCompleteLayout.setBackgroundResource(R.drawable.selector_next_step);
		} else {
			TVConfirm.setTextColor(0xFF999999);
			forgetCompleteLayout.setEnabled(false);
			forgetCompleteLayout.setBackgroundResource(R.drawable.shape_regi_complete_disable);
		}
	}
	
}