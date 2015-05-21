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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
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
	private String HOST = "http://zone.icloudoor.com/icloudoor-web";
	private String sid = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		setContentView(R.layout.find_pwd_complete);
		
		ETInputPwd = (EditText) findViewById(R.id.forget_pwd_input_new_pwd);
		ETConfirmPwd = (EditText) findViewById(R.id.forget_pwd_confirm_pwd);
		TVConfirm = (TextView) findViewById(R.id.forget_pwd_confirm);
		
		TVConfirm.setTextColor(0xFFcccccc);
		TVConfirm.setEnabled(false);
		
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
		
		TVConfirm.setOnClickListener(new OnClickListener(){

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
		if(ETConfirmPwd.getText().toString().length() > 7 && ETInputPwd.getText().toString().length() > 7){
			TVConfirm.setTextColor(0xFFffffff);
			TVConfirm.setEnabled(true);
		} else {
			TVConfirm.setTextColor(0xFFcccccc);
			TVConfirm.setEnabled(false);
		}
	}
	
}