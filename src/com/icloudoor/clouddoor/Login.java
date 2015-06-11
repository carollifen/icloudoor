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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity implements TextWatcher {
	
	private String TAG = this.getClass().getSimpleName();
	
	private EditText ETInputPhoneNum;
	private EditText ETInputPwd;
	private TextView TVLogin;
	private TextView TVFogetPwd;
	private TextView TVGoToRegi;
	private RelativeLayout ShowPwd;
	private ImageView IVPwdIcon;
    private ProgressBar pbLoginBar;
	private boolean isHiddenPwd = true;

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
	
	// for new ui
	private RelativeLayout phoneLayout;
	private RelativeLayout pwdLayout;
	private RelativeLayout loginLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		setContentView(R.layout.login);
        pbLoginBar = (ProgressBar) findViewById(R.id.loginBar);
        pbLoginBar.setVisibility(View.INVISIBLE);
		registerReceiver(mConnectionStatusReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
		setupUI(findViewById(R.id.main));
		
		mQueue = Volley.newRequestQueue(this);

		ETInputPhoneNum = (EditText) findViewById(R.id.login_input_phone_num);
		ETInputPwd = (EditText) findViewById(R.id.login_input_pwd);
		TVLogin = (TextView) findViewById(R.id.btn_login);
		TVFogetPwd = (TextView) findViewById(R.id.login_foget_pwd);
		TVGoToRegi = (TextView) findViewById(R.id.login_go_to_regi);
		ShowPwd = (RelativeLayout) findViewById(R.id.show_pwd);
		IVPwdIcon = (ImageView) findViewById(R.id.btn_show_pwd);
		IVPwdIcon.setImageResource(R.drawable.hide_pwd_new);
		
		// for new ui
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		
		phoneLayout = (RelativeLayout) findViewById(R.id.phone_input_layout);
		pwdLayout = (RelativeLayout) findViewById(R.id.pwd_input_layout);
		loginLayout = (RelativeLayout) findViewById(R.id.login_btn_layout);
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) phoneLayout.getLayoutParams();
		RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) pwdLayout.getLayoutParams();
		RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) loginLayout.getLayoutParams();
		params.width = screenWidth - 48*2;
		params1.width = screenWidth - 48*2;
		params2.width = screenWidth - 48*2;
		phoneLayout.setLayoutParams(params);
		pwdLayout.setLayoutParams(params1);
		loginLayout.setLayoutParams(params2);
		
		phoneLayout.setBackgroundResource(R.drawable.shape_login_input_normal);
		pwdLayout.setBackgroundResource(R.drawable.shape_login_input_normal);
		loginLayout.setBackgroundResource(R.drawable.shape_login_btn_disable);
		
		ETInputPhoneNum.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus){
					phoneLayout.setBackgroundResource(R.drawable.shape_login_input);
				}else{
					phoneLayout.setBackgroundResource(R.drawable.shape_login_input_normal);
                    if (ETInputPhoneNum.getText().toString().length() != 11){
                        Toast.makeText(getApplicationContext(), R.string.error_phonenumb_over, Toast.LENGTH_SHORT).show();
                    }
				}
			}
			
		});
		ETInputPwd.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus){
					pwdLayout.setBackgroundResource(R.drawable.shape_login_input);
				}else{
					pwdLayout.setBackgroundResource(R.drawable.shape_login_input_normal);
				}
			}
			
		});
		
		TVLogin.setTextColor(0xFFf3f3f3);
		loginLayout.setEnabled(false);
		
		ETInputPhoneNum.addTextChangedListener(this); 
		ETInputPwd.addTextChangedListener(this);
		
		sid = loadSid("SID");

		isHiddenPwd = true;
		IVPwdIcon.setImageResource(R.drawable.hide_pwd_new);
		ShowPwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isHiddenPwd) {
					isHiddenPwd = false;
					ETInputPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
					IVPwdIcon.setImageResource(R.drawable.show_pwd_new);
				} else {
					isHiddenPwd = true;
					ETInputPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
					IVPwdIcon.setImageResource(R.drawable.hide_pwd_new);
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
				startActivityForResult(intent, 0);
			}

		});

		loginLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if ("NET_WORKS".equals(loadSid("NETSTATE"))) {
                    pbLoginBar.setVisibility(View.VISIBLE);
					Toast.makeText(getApplicationContext(), R.string.login_ing,
							Toast.LENGTH_SHORT).show();

					try {
						loginURL = new URL(HOST + "/user/manage/login.do"
								+ "?sid=" + sid);
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
										loginStatusCode = response
												.getInt("code");
									} catch (JSONException e) {
										e.printStackTrace();
									}
									Log.e("TEST", response.toString());

                                    pbLoginBar.setVisibility(View.INVISIBLE);
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
													intent.setClass(Login.this, SetPersonalInfo.class);
													startActivity(intent);
												} else if(userStatus == 1) {
													intent.setClass(Login.this, SetPersonalInfoNotCerti.class);
													startActivity(intent);
												}
													
											}

											if (setPersonal == 1) {
												intent.setClass(Login.this, CloudDoorMainActivity.class);
												startActivity(intent);
											}
											//

											finish();
											

										} catch (JSONException e) {
											e.printStackTrace();
										}

										new Handler().postDelayed(
												new Runnable() {
													@Override
													public void run() {
														
													}
												}, 1000);

										SharedPreferences downPic = getSharedPreferences("DOWNPIC", 0);
										Editor editor1 = downPic.edit();
										editor1.putInt("PIC", 0);
										editor1.commit();
										
									} else if (loginStatusCode == -71) {
										Toast.makeText(getApplicationContext(),
												R.string.login_fail,
												Toast.LENGTH_SHORT).show();
									} else if(loginStatusCode == -99) {
    									Toast.makeText(getApplicationContext(),
    											R.string.unknown_err, Toast.LENGTH_SHORT)
    											.show();
    								}
								}
							}, new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
										Toast.makeText(Login.this, R.string.network_error, Toast.LENGTH_SHORT).show();
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
		});
		
		SharedPreferences RegiPhone = getSharedPreferences("REGIPHONE", 0);
     	Editor editor = RegiPhone.edit();
     	editor.putString("PHONE", "");
     	editor.commit();
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
         if(requestCode == 0 && resultCode == RESULT_OK) {

            finish();
        }
    }
	
	@Override
	protected void onResume() {
	    super.onResume();
	    ETInputPhoneNum.setText("");
	    ETInputPwd.setText("");
	    
		//
		Bundle bundle = this.getIntent().getExtras();
		if(bundle != null){
			String phone = bundle.getString("phone");
			ETInputPhoneNum.setText(phone.toString());
			ETInputPwd.requestFocus();
		}
	    
	    SharedPreferences RegiPhone = getSharedPreferences("REGIPHONE", 0);
	    String phone = RegiPhone.getString("PHONE", "");
	    if(RegiPhone.getString("PHONE", "").length() > 0){
	    	ETInputPhoneNum.setText(RegiPhone.getString("PHONE", null));
	    	ETInputPwd.requestFocus();
	    	
	    	Editor editor = RegiPhone.edit();
	    	editor.putString("PHONE", "");
	    	editor.commit();
	    }
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
		if(ETInputPhoneNum.getText().toString().length() > 10 && ETInputPwd.getText().toString().length() > 5){
			TVLogin.setTextColor(0xFFffffff);
			loginLayout.setEnabled(true);
			loginLayout.setBackgroundResource(R.drawable.selector_login_in);
		} else {
			TVLogin.setTextColor(0xFFf3f3f3);
			loginLayout.setEnabled(false);
			loginLayout.setBackgroundResource(R.drawable.shape_login_btn_disable);
		}
	}
	
	public BroadcastReceiver mConnectionStatusReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO: This method is called when the BroadcastReceiver is
			// receiving
			// an Intent broadcast.

			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();
			if (networkInfo != null) {
				if (networkInfo.isAvailable()) {
					saveSid("NETSTATE", "NET_WORKS");
					Log.i("NOTICE", "The Net is available!");
				}
				NetworkInfo.State state = connectivityManager.getNetworkInfo(
						connectivityManager.TYPE_MOBILE).getState();
				if (NetworkInfo.State.CONNECTED == state) {
					Log.i("NOTICE", "GPRS is OK!");
					NetworkInfo mobNetInfo = connectivityManager
							.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				}
				state = connectivityManager.getNetworkInfo(
						ConnectivityManager.TYPE_WIFI).getState();
				if (NetworkInfo.State.CONNECTED == state) {
					Log.i("NOTICE", "WIFI is OK!");
					NetworkInfo wifiNetInfo = connectivityManager
							.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				}
			} else {
				saveSid("NETSTATE", "NET_NOT_WORK");
			}
		}
	};
	
	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}
	
	public void setupUI(View view) {
		// Set up touch listener for non-text box views to hide keyboard.
		if (!(view instanceof EditText)) {
			view.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					hideSoftKeyboard(Login.this); 
					return false;
				}
			});
		}
		// If a layout container, iterate over children and seed recursion.
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				View innerView = ((ViewGroup) view).getChildAt(i);
				setupUI(innerView);
			}
		}
	}
}