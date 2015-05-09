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
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity implements TextWatcher {
	private EditText ETInputPhoneNum;
	private EditText ETInputPwd;
	private TextView TVLogin;
	private TextView TVFogetPwd;
	private TextView TVGoToRegi;
	private RelativeLayout ShowPwd;
	private ImageView IVPwdIcon;

	private boolean isHiddenPwd = true;
	boolean hasInputPhoneNum = false;
	boolean hasInputPwd = false;
	
	private URL loginURL;
	private RequestQueue mQueue;

	private String phoneNum, password;

	private String HOST = "http://zone.icloudoor.com/icloudoor-web";

	private int loginStatusCode;

	private String sid;
	private int isLogin = 0;
	
	private int setPersonal;
	
	private String name;
	private String nickname;
	private String id;
	private String birth;
	private int sex, provinceId, cityId, districtId;
	private String portraitUrl, userId;
	private int userStatus;

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
		ShowPwd = (RelativeLayout) findViewById(R.id.show_pwd);
		IVPwdIcon = (ImageView) findViewById(R.id.btn_show_pwd);
		IVPwdIcon.setImageResource(R.drawable.hide_pwd);
		
		TVLogin.setTextColor(0xFF999999);
		TVLogin.setEnabled(false);
		
		ETInputPhoneNum.addTextChangedListener(this); 
		ETInputPwd.addTextChangedListener(this);
		
		sid = loadSid();

		ShowPwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isHiddenPwd) {
					isHiddenPwd = false;
					ETInputPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
					IVPwdIcon.setImageResource(R.drawable.show_pwd);
				} else {
					isHiddenPwd = true;
					ETInputPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
					IVPwdIcon.setImageResource(R.drawable.hide_pwd);
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
				
				Toast.makeText(getApplicationContext(), R.string.login_ing, Toast.LENGTH_SHORT).show();
				
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
								
								if (loginStatusCode == -71) {
									Toast.makeText(getApplicationContext(), R.string.login_fail,
											Toast.LENGTH_SHORT).show();
								} else if (loginStatusCode == 1) {

									isLogin = 1;
									SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);
									Editor editor = loginStatus.edit();
									editor.putInt("LOGIN", isLogin);
									editor.putString("PHONENUM", phoneNum);
									editor.putString("PASSWARD", password);
									editor.commit();

									Intent intent = new Intent();
									
									SharedPreferences personalInfo = getSharedPreferences("PERSONSLINFO", MODE_PRIVATE);
									setPersonal = personalInfo.getInt("SETINFO", 0);
									
									
									try {
										JSONObject data = response.getJSONObject("data");
										JSONObject info = data.getJSONObject("info");
										
										name = info.getString("userName");
										nickname = info.getString("nickname");
										id = info.getString("idCardNo");
										birth = info.getString("birthday");
										sex = info.getInt("sex");
										provinceId = info.getInt("provinceId");
										cityId = info.getInt("cityId");
										districtId = info.getInt("districtId");
										
										portraitUrl = info.getString("portraitUrl");
										userId = info.getString("userId");
										userStatus = info.getInt("userStatus");
										
										editor.putString("NAME", name);
										editor.putString("NICKNAME", nickname);
										editor.putString("ID", id);
										editor.putString("BIRTH", birth);
										editor.putInt("PROVINCE", provinceId);
										editor.putInt("CITY", cityId);
										editor.putInt("DIS", districtId);
										editor.putString("URL", portraitUrl);
										editor.putString("USERID", userId);
										editor.putInt("STATUS", userStatus);
										editor.commit();
										
									} catch (JSONException e) {
										e.printStackTrace();
									}					
									
									if(setPersonal == 1 || (!name.equals(null) && !nickname.equals(null) 
											&& !id.equals(null) && !birth.equals(null))){
										intent.setClass(getApplicationContext(), CloudDoorMainActivity.class);
									} else{
										intent.setClass(getApplicationContext(), SetPersonalInfo.class);
									}
									
									startActivity(intent);

									finish();
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
						map.put("mobile", phoneNum);
						map.put("password", password);
						return map;
					}
				};
				mQueue.add(mJsonRequest);
			}

		});
	}

	@Override
	protected void onResume() {
	    super.onResume();
	    ETInputPhoneNum.setText("");
	    ETInputPwd.setText("");
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
		if(ETInputPhoneNum.getText().toString().length() > 10 && ETInputPwd.getText().toString().length() > 7){
			TVLogin.setTextColor(0xFF000000);
			TVLogin.setEnabled(true);
		} else {
			TVLogin.setTextColor(0xFF999999);
			TVLogin.setEnabled(false);
		}
	}

}