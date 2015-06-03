package com.icloudoor.clouddoor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ResetPwdActivity extends Activity implements TextWatcher {
	private EditText ETInputOldPwd;
	private EditText ETInputNewPwd;
	private EditText ETConfirmNewPwd;
	private LinearLayout TVResetDone;
	private LinearLayout IVBack;
	private URL resetPwdURL;
	private RequestQueue mQueue;
	private String oldPwd, newPwd, confirmPwd;

	private int statusCode;
	private String HOST = "https://zone.icloudoor.com/icloudoor-web";
	private String sid = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		setContentView(R.layout.set_detail_reset_pwd);

		mQueue = Volley.newRequestQueue(this);

		IVBack = (LinearLayout) findViewById(R.id.btn_back_reset_pwd);
		ETInputOldPwd = (EditText) findViewById(R.id.input_old_pwd);
		ETInputNewPwd = (EditText) findViewById(R.id.input_new_pwd);
		ETConfirmNewPwd = (EditText) findViewById(R.id.confirm_new_pwd);
		TVResetDone = (LinearLayout) findViewById(R.id.reset_pwd_done);
		
//		TVResetDone.setTextColor(0xFFcccccc);
		TVResetDone.setEnabled(false);
		
		ETInputOldPwd.addTextChangedListener(this);
		ETInputNewPwd.addTextChangedListener(this);
		ETConfirmNewPwd.addTextChangedListener(this);
		
		sid = loadSid();

		IVBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(v.getContext(), SettingDetailActivity.class);
//				startActivity(intent);
				finish();
			}

		});

		TVResetDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					resetPwdURL = new URL(HOST
							+ "/user/manage/changePassword.do" + "?sid=" + sid);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

				oldPwd = ETInputOldPwd.getText().toString();
				newPwd = ETInputNewPwd.getText().toString();
				confirmPwd = ETConfirmNewPwd.getText().toString();
				if (newPwd.equals(confirmPwd)) {
					MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
							Method.POST, resetPwdURL.toString(), null,
							new Response.Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject response) {
									try {
										statusCode = response.getInt("code");
									} catch (JSONException e) {
										e.printStackTrace();
									}
									
									if (statusCode == 1) {
										try {
											if (response.getString("sid") != null) {
												sid = response.getString("sid");
												saveSid(sid);
											}
										} catch (JSONException e) {
											e.printStackTrace();
										}
										Toast.makeText(getApplicationContext(), R.string.forget_success, Toast.LENGTH_SHORT).show();
										
//										Intent intent = new Intent();
//										intent.setClass(getApplicationContext(),
//												SettingDetailActivity.class);
//										startActivity(intent);
										
										finish();
									}else if (statusCode == -41) {
										Toast.makeText(getApplicationContext(), R.string.weak_pwd, Toast.LENGTH_SHORT).show();
									}else if (statusCode == -51) {
										Toast.makeText(getApplicationContext(), R.string.wrong_old_pwd, Toast.LENGTH_SHORT).show();
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
							map.put("oldPassword", oldPwd);
							map.put("newPassword", confirmPwd);
							return map;
						}
					};
					mQueue.add(mJsonRequest);
				} else {
					Toast.makeText(v.getContext(), R.string.diff_pwd,
							Toast.LENGTH_SHORT).show();
				}
			}

		});

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
			intent.setClass(ResetPwdActivity.this, VerifyGestureActivity.class);
			startActivity(intent);
		}
	}
	
	public void saveSid(String sid) {
		SharedPreferences savedSid = getSharedPreferences("SAVEDSID",
				MODE_PRIVATE);
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
		
		String temp = s.toString();
		
		if(temp.length() > 1){
			String tem = temp.substring(temp.length()-1, temp.length());
			char[] temC = tem.toCharArray();
			int mid = temC[0];
			
			if((mid>=48 && mid<=57) || (mid>=65&&mid<=90) || (mid>97&&mid<=122)){
				
			}else{
				s.delete(temp.length()-1, temp.length());
				Toast.makeText(this, R.string.input_wrong, Toast.LENGTH_SHORT).show();
			}
		}else if(temp.length() == 1){
			char[] temC = temp.toCharArray();
			int mid = temC[0];
			
			if((mid>=48 && mid<=57) || (mid>=65&&mid<=90) || (mid>97&&mid<=122)){
				
			}else{
				s.clear();
				Toast.makeText(this, R.string.input_wrong, Toast.LENGTH_SHORT).show();
			}
		}
		
		if(ETInputNewPwd.getText().toString().length() > 5 && ETConfirmNewPwd.getText().toString().length() > 5 && ETInputOldPwd.getText().toString().length() > 5){
//			TVResetDone.setTextColor(0xFFffffff);
			TVResetDone.setEnabled(true);
		} else {
//			TVResetDone.setTextColor(0xFFcccccc);
			TVResetDone.setEnabled(false);
		}
	}	
	
}