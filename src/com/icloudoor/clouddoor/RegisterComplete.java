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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterComplete extends Activity implements TextWatcher {
	private TextView TVRegiComplete;
	private EditText ETInputPwd;
	private EditText ETConfirmPwd;
	private URL registerURL;
	private RequestQueue mQueue;
	private String inputPwd, confirmPwd;
	private RelativeLayout BtnBack;

	private int statusCode;
	private String HOST = "http://zone.icloudoor.com/icloudoor-web";
	private String sid = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		setContentView(R.layout.register_complete);

		mQueue = Volley.newRequestQueue(this);

		ETInputPwd = (EditText) findViewById(R.id.regi_input_pwd);
		ETConfirmPwd = (EditText) findViewById(R.id.regi_input_pwd_again);
		TVRegiComplete = (TextView) findViewById(R.id.btn_regi_complete);
		
		TVRegiComplete.setTextColor(0xFFcccccc);
		TVRegiComplete.setEnabled(false);
		
		ETInputPwd.addTextChangedListener(this);
		ETConfirmPwd.addTextChangedListener(this);
		
		BtnBack = (RelativeLayout) findViewById(R.id.btn_back);
		BtnBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), RegisterActivity.class);
				startActivity(intent);
				
				RegisterComplete.this.finish();
			}
			
		});
				
		sid = loadSid();
		
		TVRegiComplete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					registerURL = new URL(HOST + "/user/manage/createUser.do"
							+ "?sid=" + sid);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

				inputPwd = ETInputPwd.getText().toString();
				confirmPwd = ETConfirmPwd.getText().toString();
				if (inputPwd.equals(confirmPwd)) {
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
									Log.e("TEST",
											"statusCode: "
													+ String.valueOf(statusCode));
									Log.e("TEST",
											"response: " + response.toString());
									try {
										Log.e("TEST",
												"sid: "
														+ response
																.getString("sid"));
									} catch (JSONException e) {
										e.printStackTrace();
									}

									if (statusCode == 1) {
//										Intent intent = new Intent();
//										intent.setClass(getApplicationContext(), Login.class);
//										startActivity(intent);
										
										SharedPreferences personalInfo = getSharedPreferences("PERSONSLINFO", MODE_PRIVATE);
										Editor editor = personalInfo.edit();
										editor.putInt("SETINFO", 0);
										editor.commit();
										
										setResult(RESULT_OK);
										finish();
									} else if (statusCode == -40) {
										Toast.makeText(getApplicationContext(),
												R.string.phone_num_have_been_registerred,
												Toast.LENGTH_SHORT).show();
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
							map.put("password", confirmPwd);
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
		if(ETInputPwd.getText().toString().length() > 7 && ETConfirmPwd.getText().toString().length() > 7){
			TVRegiComplete.setTextColor(0xFFffffff);
			TVRegiComplete.setEnabled(true);
		} else {
			TVRegiComplete.setTextColor(0xFFcccccc);
			TVRegiComplete.setEnabled(false);
		}
	}
}