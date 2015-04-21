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
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {
	private EditText ETInputPhoneNum;
	private EditText ETInputPwd;
	private TextView TVLogin;
	private TextView TVFogetPwd;
	private TextView TVGoToRegi;
	private ImageView IVShowPwd;

	private boolean isHiddenPwd = true;

	private URL loginURL;
	private RequestQueue mQueue;

	private String phoneNum, password;

	private String HOST = "http://zone.icloudoor.com/icloudoor-web";

	private int loginStatusCode;

	private String sid;
	private int isLogin = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.login);

		mQueue = Volley.newRequestQueue(this);

		ETInputPhoneNum = (EditText) findViewById(R.id.login_input_phone_num);
		ETInputPwd = (EditText) findViewById(R.id.login_input_pwd);
		TVLogin = (TextView) findViewById(R.id.btn_login);
		TVFogetPwd = (TextView) findViewById(R.id.login_foget_pwd);
		TVGoToRegi = (TextView) findViewById(R.id.login_go_to_regi);
		IVShowPwd = (ImageView) findViewById(R.id.btn_show_pwd);

		sid = loadSid();

		IVShowPwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isHiddenPwd) {
					isHiddenPwd = false;
					ETInputPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				} else {
					isHiddenPwd = true;
					ETInputPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}

			}

		});

		TVGoToRegi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(v.getContext(), RegisterActivity.class);
				startActivity(intent);
			}

		});
		TVFogetPwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(v.getContext(), ForgetPwdActivity.class);
				startActivity(intent);
			}

		});
		TVLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					loginURL = new URL(HOST + "/user/manage/login.do" + "?sid="
							+ sid);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				phoneNum = ETInputPhoneNum.getText().toString();
				password = ETInputPwd.getText().toString();
				MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
						Method.POST, loginURL.toString(), null,
						new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								try {
									if (response.getString("sid") != null) {
										sid = response.getString("sid");
										saveSid(sid);
									}
									loginStatusCode = response.getInt("code");
								} catch (JSONException e) {
									e.printStackTrace();
								}
								Log.e("TEST", response.toString());
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
						map.put("mobile", phoneNum);
						map.put("password", password);
						return map;
					}
				};
				mQueue.add(mJsonRequest);

				if (loginStatusCode == -71) {
					Toast.makeText(v.getContext(), R.string.login_fail,
							Toast.LENGTH_SHORT).show();
				} else if (loginStatusCode == 1) {

					isLogin = 1;
					SharedPreferences loginStatus = getSharedPreferences(
							"LOGINSTATUS", MODE_PRIVATE);
					Editor editor = loginStatus.edit();
					editor.putInt("LOGIN", isLogin);
					editor.putString("PHONENUM", phoneNum);
					editor.commit();

					Intent intent = new Intent();
					intent.setClass(v.getContext(), CloudDoorMainActivity.class);
					startActivity(intent);

					finish();
				}
			}

		});
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

}