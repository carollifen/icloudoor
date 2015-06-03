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
import com.icloudoor.clouddoor.RegisterActivity.TimeCount;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ForgetPwdActivity extends Activity implements TextWatcher {
	private String TAG = this.getClass().getSimpleName();
	
	private TextView TVGetCertiCode;
	private EditText ETInputPhoneNum;
	private EditText ETInputCertiCode;
	private TextView TVGotoNext;
	private URL requestCertiCodeURL, verifyCertiCodeURL;
	private RequestQueue mQueue;
	private RelativeLayout BtnBack;
	
	private TimeCount counter;
	
	private int RequestCertiStatusCode;
	private int ConfirmCertiStatusCode;
	private String sid = null;

	private String HOST = "http://zone.icloudoor.com/icloudoor-web";
	
	// for new ui
	private RelativeLayout phoneLayout;
	private RelativeLayout phoneInputLayout;
	private RelativeLayout getCertiCodeLayout;
	private RelativeLayout inputCertiCodeLayout;
	private RelativeLayout nextLayout;
	
	private boolean isBackKey;
		
	//
	private int loginStatusCode;
	private URL loginURL = null;
	
	private int isLogin;
	
	private int setPersonal;
	
	private String name = null;
	private String nickname = null;
	private String id = null;
	private String birth = null;
	private int sex = 0, provinceId = 0, cityId = 0, districtId = 0;
	private String portraitUrl, userId;
	private int userStatus;
	
	private String phoneNum;
	private String password;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		setContentView(R.layout.find_pwd);
		
		isBackKey = false;
		
		mQueue = Volley.newRequestQueue(this);
		
		ETInputPhoneNum = (EditText) findViewById(R.id.regi_input_phone_num);
		ETInputCertiCode = (EditText) findViewById(R.id.regi_input_certi_code);
		TVGotoNext = (TextView) findViewById(R.id.btn_regi_next_step);
		TVGetCertiCode = (TextView) findViewById(R.id.btn_regi_get_certi_code);
		
		// for new ui
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		
		phoneLayout = (RelativeLayout) findViewById(R.id.phone_input_get_certi_layout);
		phoneInputLayout = (RelativeLayout) findViewById(R.id.phone_input_layout);
		getCertiCodeLayout = (RelativeLayout) findViewById(R.id.get_certi_layout);
		inputCertiCodeLayout = (RelativeLayout) findViewById(R.id.input_certi_layout);
		nextLayout = (RelativeLayout) findViewById(R.id.next_step_btn_layout);
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) phoneLayout.getLayoutParams();
		RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) phoneInputLayout.getLayoutParams();
		RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) getCertiCodeLayout.getLayoutParams();
		RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) inputCertiCodeLayout.getLayoutParams();
		RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams) nextLayout.getLayoutParams();
		params.width = screenWidth - 48*2;
		params1.width = (screenWidth - 48*2 - 8)*2/3;
		params2.width = (screenWidth - 48*2 - 8)*1/3;
		params3.width = screenWidth - 48*2;
		params4.width = screenWidth - 48*2;
		
		phoneLayout.setLayoutParams(params);
		phoneInputLayout.setLayoutParams(params1);
		getCertiCodeLayout.setLayoutParams(params2);
		inputCertiCodeLayout.setLayoutParams(params3);
		nextLayout.setLayoutParams(params4);
		
		phoneInputLayout.setBackgroundResource(R.drawable.shape_left_corner);
		inputCertiCodeLayout.setBackgroundResource(R.drawable.shape_input_certi_code);
		
		getCertiCodeLayout.setBackgroundResource(R.drawable.shape_right_corner);
		getCertiCodeLayout.setEnabled(false);
		
		nextLayout.setEnabled(false);
		nextLayout.setBackgroundResource(R.drawable.shape_next_disable);
		//
		
		TVGotoNext.setTextColor(0xFF999999);
//		TVGotoNext.setEnabled(false);
		
		ETInputPhoneNum.addTextChangedListener(this);
		ETInputCertiCode.addTextChangedListener(this);		
		
		BtnBack = (RelativeLayout) findViewById(R.id.btn_back);
		BtnBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				isBackKey = true;
				finish();
			}
			
		});
		
		sid = loadSid();
		
		counter = new TimeCount(60000, 1000);
		getCertiCodeLayout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				counter.start();
				
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
								if (RequestCertiStatusCode == -20) {
									Toast.makeText(getApplicationContext(),
											R.string.send_too_many_a_day, Toast.LENGTH_SHORT)
											.show();
								} else if (RequestCertiStatusCode == -21) {
									Toast.makeText(getApplicationContext(),
											R.string.send_too_frequently, Toast.LENGTH_SHORT)
											.show();
								}
							}
						}, 
						new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								Log.e(TAG, error.toString());
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
			}
			
		});
		nextLayout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
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
								if (ConfirmCertiStatusCode == 1) {
									Intent intent = new Intent();
									intent.setClass(getApplicationContext(), ForgetPwdComplete.class);
									startActivityForResult(intent, 0);
								} else if (ConfirmCertiStatusCode == -30) {
									Toast.makeText(getApplicationContext(),
											R.string.input_wrong_certi_code, Toast.LENGTH_SHORT)
											.show();
								} else if (ConfirmCertiStatusCode == -31) {
									Toast.makeText(getApplicationContext(), R.string.certi_code_overdue,
											Toast.LENGTH_SHORT).show();
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
			}
			
		});
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
         if(requestCode == 0 && resultCode == RESULT_OK) {
        	SharedPreferences RegiPhone = getSharedPreferences("REGIPHONE", 0);
         	Editor editor = RegiPhone.edit();
         	editor.putString("PHONE", ETInputPhoneNum.getText().toString());
         	editor.commit();
         	
         	setResult(RESULT_OK);
         	// TODO
            /*
             * automatically login and jump to MainActivity()
             */
            LoginAuto();
         	
            finish();
        }
    }
	
	public void LoginAuto(){
		
		sid = loadSid();

		try {
			loginURL = new URL(HOST + "/user/manage/login.do" + "?sid=" + sid);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		if ("NET_WORKS".equals(loadSid("NETSTATE"))) {
			Toast.makeText(getApplicationContext(), R.string.login_ing,
					Toast.LENGTH_SHORT).show();

			try {
				loginURL = new URL(HOST + "/user/manage/login.do"
						+ "?sid=" + sid);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
			SharedPreferences RegiPhone = getSharedPreferences("REGIPHONE", 0);
			
			phoneNum = RegiPhone.getString("PHONE", "");
			password = RegiPhone.getString("PWD", "");
			MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(Method.POST, loginURL.toString(), null,
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

							if (loginStatusCode == 1) {

								isLogin = 1;
								SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);
								Editor editor = loginStatus.edit();
								
								editor.putInt("LOGIN", isLogin);
								editor.putString("PHONENUM", phoneNum);
								editor.putString("PASSWARD", password);
								editor.commit();

								SharedPreferences firstLoginShare=getSharedPreferences("FIRSTLOGINSHARE", 0);
								Editor mEditor=firstLoginShare.edit();
								mEditor.putBoolean("FIRSTLOGIN", true).commit();
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
									userStatus = info.getInt("userStatus");     //1 for not approved user; 2 for approved user

									editor.putString("NAME", name);
									editor.putString("NICKNAME",nickname);
									editor.putString("ID", id);
									editor.putString("BIRTH", birth);
									editor.putInt("SEX", sex);
									editor.putInt("PROVINCE",provinceId);
									editor.putInt("CITY", cityId);
									editor.putInt("DIS", districtId);
									editor.putString("URL", portraitUrl);
									editor.putString("USERID", userId);
									editor.putInt("STATUS", userStatus);
									editor.commit();
									
									//
									Intent intent = new Intent();

									SharedPreferences personalInfo = getSharedPreferences("PERSONSLINFO", MODE_PRIVATE);
									setPersonal = personalInfo.getInt("SETINFO", 1);

									if (setPersonal == 0 || name.length() == 0 || sex == 0 || provinceId == 0 || cityId == 0 || districtId == 0 || birth.length() == 0 || id.length() == 0) {
										Log.e("jump to set", "in login activity");
										
										if(userStatus == 2) {
											intent.setClass(ForgetPwdActivity.this, SetPersonalInfo.class);
											startActivity(intent);
										} else if(userStatus == 1) {
											intent.setClass(ForgetPwdActivity.this, SetPersonalInfoNotCerti.class);
											startActivity(intent);
										}
											
									}

									if (setPersonal == 1) {
										intent.setClass(ForgetPwdActivity.this, CloudDoorMainActivity.class);
										startActivity(intent);
									}

									finish();
									

								} catch (JSONException e) {
									e.printStackTrace();
								}


								SharedPreferences downPic = getSharedPreferences("DOWNPIC", 0);
								Editor editor1 = downPic.edit();
								editor1.putInt("PIC", 0);
								editor1.commit();
								
							} else if (loginStatusCode == -71) {
								Toast.makeText(getApplicationContext(),
										R.string.login_fail,
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
					map.put("mobile", phoneNum);
					map.put("password", password);
					return map;
				}
			};
			mQueue.add(mJsonRequest);
		} else {
			if (getApplicationContext() != null) {
				Toast.makeText(getApplicationContext(),
						R.string.no_network, Toast.LENGTH_SHORT).show();
			}
		}
		
		
	}
	
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
		}
		@Override
		public void onFinish() {
			getCertiCodeLayout.setEnabled(true);
			getCertiCodeLayout.setBackgroundResource(R.drawable.shape_right_corner);
			TVGetCertiCode.setText(getString(R.string.get_certi_code_again));
//			TVGetCertiCode.setTextSize(17);
//			TVGetCertiCode.setEnabled(true);
//			TVGetCertiCode.setBackgroundResource(R.drawable.btn_certi);
		}
		@Override
		public void onTick(long millisUntilFinished){
			getCertiCodeLayout.setEnabled(false);
			getCertiCodeLayout.setBackgroundResource(R.drawable.shape_right_corner_pressed);
//			TVGetCertiCode.setEnabled(false);
//			TVGetCertiCode.setBackgroundResource(R.drawable.btn_certi_counter);
//			TVGetCertiCode.setTextSize(16);
			TVGetCertiCode.setText(getString(R.string.have_send) + '\n' + "(" + millisUntilFinished /1000+")");
		}
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    ETInputPhoneNum.setText("");
	    ETInputCertiCode.setText("");
	    
	    if (isBackKey == false) {
			SharedPreferences tempInfo = getSharedPreferences("tempInfo", 0);
			ETInputPhoneNum.setText(tempInfo.getString("phone", ""));
			ETInputCertiCode.setText(tempInfo.getString("code", ""));

			Editor editor = tempInfo.edit();
			editor.putString("phone", "");
			editor.putString("code", "");
			editor.commit();
		}
	}
	
	public void onPause(){
		super.onPause();
		
		Log.e(TAG, "onpause");
		
		Log.e(TAG, String.valueOf(isBackKey));
		
		if (isBackKey == false) {
			Log.e(TAG, "saving");
			SharedPreferences tempInfo = getSharedPreferences("tempInfo", 0);
			Editor editor = tempInfo.edit();
			editor.putString("phone", ETInputPhoneNum.getText().toString());
			editor.putString("code", ETInputCertiCode.getText().toString());
			editor.commit();
		} else {
			isBackKey = false;
		}

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
	
	public String loadSid(String key) {
		SharedPreferences loadSid = getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString(key, null);
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
		if(ETInputPhoneNum.getText().toString().length() > 10){
			getCertiCodeLayout.setEnabled(true);
		}else{
			getCertiCodeLayout.setEnabled(false);
		}
		
		if(ETInputPhoneNum.getText().toString().length() > 10 && ETInputCertiCode.getText().toString().length() > 4){
			nextLayout.setEnabled(true);
			nextLayout.setBackgroundResource(R.drawable.selector_next_step);
			TVGotoNext.setTextColor(0xFF0065a1);
//			TVGotoNext.setEnabled(true);
		} else {
			nextLayout.setEnabled(false);
			nextLayout.setBackgroundResource(R.drawable.shape_next_disable);
			TVGotoNext.setTextColor(0xFF999999);
//			TVGotoNext.setEnabled(false);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& KeyEvent.KEYCODE_BACK == keyCode) {
			Log.e(TAG, "backkey");

			isBackKey = true;

			Log.e(TAG, String.valueOf(isBackKey));
		}
		return super.onKeyDown(keyCode, event);
	}
}