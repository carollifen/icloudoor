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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
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
	
	private final String TAG = this.getClass().getSimpleName();
	
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
	
	private String name = null;
	private String nickname = null;
	private String id = null;
	private String birth = null;
	private int sex = 0, provinceId = 0, cityId = 0, districtId = 0;
	private String portraitUrl, userId;
	private int userStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.login);

		registerReceiver(mConnectionStatusReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
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
		
		sid = loadSid("SID");

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
                if ("NET_WORKS".equals(loadSid("NETSTATE"))) {
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
                                            saveSid("SID", sid);
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
                                            editor.putInt("SEX", sex);
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

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent = new Intent();

                                                SharedPreferences personalInfo = getSharedPreferences("PERSONSLINFO", MODE_PRIVATE);
                                                setPersonal = personalInfo.getInt("SETINFO", 1);

                                                Log.e(TAG, "setPersonal : " + String.valueOf(setPersonal));

                                                if (setPersonal == 0 || name.length() == 0 || sex == 0 || provinceId == 0 || cityId == 0 || districtId == 0 || birth.length() == 0 || id.length() == 0) {
                                                    intent.setClass(getApplicationContext(), SetPersonalInfo.class);
                                                } else if (setPersonal == 1) {
                                                    intent.setClass(getApplicationContext(), CloudDoorMainActivity.class);
                                                }

                                                startActivity(intent);

                                                finish();
                                            }
                                        }, 1000);

//									Intent intent = new Intent();
//									
//									SharedPreferences personalInfo = getSharedPreferences("PERSONSLINFO", MODE_PRIVATE);
//									setPersonal = personalInfo.getInt("SETINFO", 0);
//									
//									if(name.length() == 0 || sex == 0 || provinceId == 0 || cityId == 0 || districtId == 0 || birth.length() == 0 || id.length() == 0){
//										intent.setClass(getApplicationContext(), SetPersonalInfo.class);
//									} else {
//										intent.setClass(getApplicationContext(), CloudDoorMainActivity.class);
//									}
//									
//									startActivity(intent);
//
//									finish();

                                    } else if (loginStatusCode == -71) {
                                        Toast.makeText(getApplicationContext(), R.string.login_fail,
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
                }else {
                    if (getApplicationContext() != null) {
                        Toast.makeText(getApplicationContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                    }
                }
            }
		});
	}

	@Override
	protected void onResume() {
	    super.onResume();
	    ETInputPhoneNum.setText("");
	    ETInputPwd.setText("");
	}

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mConnectionStatusReceiver);
	}
	
	public void saveSid(String key, String value) {
		SharedPreferences savedSid = getSharedPreferences("SAVEDSID",
				MODE_PRIVATE);
		Editor editor = savedSid.edit();
		editor.putString(key, value);
		editor.commit();
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
		if(ETInputPhoneNum.getText().toString().length() > 10 && ETInputPwd.getText().toString().length() > 7){
			TVLogin.setTextColor(0xFF000000);
			TVLogin.setEnabled(true);
		} else {
			TVLogin.setTextColor(0xFF999999);
			TVLogin.setEnabled(false);
		}
	}
	
	public BroadcastReceiver mConnectionStatusReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            // an Intent broadcast.

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (networkInfo.isAvailable()) {
                    saveSid("NETSTATE", "NET_WORKS");
                    Log.i("NOTICE", "The Net is available!");
                }
                NetworkInfo.State state = connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE).getState();
                if (NetworkInfo.State.CONNECTED == state) {
                    Log.i("NOTICE", "GPRS is OK!");
                    NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                }
                state = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                if (NetworkInfo.State.CONNECTED == state) {
                    Log.i("NOTICE", "WIFI is OK!");
                    NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                }
            } else {
                saveSid("NETSTATE", "NET_NOT_WORK");
//                Toast.makeText(context, R.string.no_network, Toast.LENGTH_LONG).show();
            }
        }
	};

}