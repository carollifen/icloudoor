package com.icloudoor.clouddoor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.toolbox.*;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ForgetPwdActivity extends Activity{
	private TextView TVGetCertiCode;
	private EditText ETInputPhoneNum;
	private EditText ETInputCertiCode;
	private TextView TVGotoNext;
	private URL requestCertiCodeURL, verifyCertiCodeURL;
	private RequestQueue mQueue;
	
	private int RequestCertiStatusCode;
	private int ConfirmCertiStatusCode;
	private String sid = null;

	private String HOST = "http://zone.icloudoor.com/icloudoor-web";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.find_pwd);
		
		mQueue = Volley.newRequestQueue(this);
		
		ETInputPhoneNum = (EditText) findViewById(R.id.forget_pwd_input_phone_num);
		ETInputCertiCode = (EditText) findViewById(R.id.forget_pwd_input_certi_code);
		TVGotoNext = (TextView) findViewById(R.id.forget_pwd_goto_next);
		TVGetCertiCode = (TextView) findViewById(R.id.forget_pwd_get_certi_code);
		
		sid = loadSid();
		
		TVGetCertiCode.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				try {
					requestCertiCodeURL = new URL(HOST+"/user/manage/sendVerifyCode.do"+"?sid="+sid);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
				MyJsonObjectRequest  mJsonRequest = new MyJsonObjectRequest(Method.POST, requestCertiCodeURL.toString(), 
						null, new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								try {
									if (response.getString("sid") != null) 
										sid = response.getString("sid");
									RequestCertiStatusCode = response
											.getInt("code");
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}, 
						new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								
							}
						}){
					@Override
					protected Map<String, String> getParams() throws AuthFailureError {
						Map<String, String> map = new HashMap<String, String>();
						map.put("mobile", ETInputPhoneNum.getText().toString());
						return map;
					}
				};
				mQueue.add(mJsonRequest);
				
				if (RequestCertiStatusCode == -20) {
					Toast.makeText(v.getContext(),
							R.string.send_too_many_a_day, Toast.LENGTH_SHORT)
							.show();
				} else if (RequestCertiStatusCode == -21) {
					Toast.makeText(v.getContext(),
							R.string.send_too_frequently, Toast.LENGTH_SHORT)
							.show();
				}
			}
			
		});
		TVGotoNext.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(v.getContext(), ForgetPwdNewPwd.class);
//				startActivity(intent);
				try {
					verifyCertiCodeURL = new URL(HOST
							+ "/user/manage/confirmVerifyCode.do" + "?sid="
							+ sid);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				MyJsonObjectRequest  mJsonRequest = new MyJsonObjectRequest(Method.POST, verifyCertiCodeURL.toString(), 
						null, new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								try {
									if (response.getString("sid") != null) {
										sid = response.getString("sid");
										saveSid(sid);
									}
									ConfirmCertiStatusCode = response
											.getInt("code");
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}, 
						new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								
							}
						}){
					@Override
					protected Map<String, String> getParams() throws AuthFailureError {
						Map<String, String> map = new HashMap<String, String>();
						map.put("verifyCode", ETInputCertiCode.getText().toString());
						return map;
					}
				};
				mQueue.add(mJsonRequest);
				
				if (ConfirmCertiStatusCode == 1) {
					Intent intent = new Intent();
					intent.setClass(v.getContext(), RegisterComplete.class);
					startActivity(intent);
				} else if (ConfirmCertiStatusCode == -30) {
					Toast.makeText(v.getContext(),
							R.string.input_wrong_certi_code, Toast.LENGTH_SHORT)
							.show();
				} else if (ConfirmCertiStatusCode == -31) {
					Toast.makeText(v.getContext(), R.string.certi_code_overdue,
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
}