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
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements TextWatcher {
	private TextView TVGetCertiCode;
	private TextView TVNextStep;
	private EditText ETInputPhoneNum;
	private EditText ETInputCertiCode;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		setContentView(R.layout.register);

		mQueue = Volley.newRequestQueue(this);

		ETInputPhoneNum = (EditText) findViewById(R.id.regi_input_phone_num);
		ETInputCertiCode = (EditText) findViewById(R.id.regi_input_certi_code);
		TVGetCertiCode = (TextView) findViewById(R.id.btn_regi_get_certi_code);
		TVNextStep = (TextView) findViewById(R.id.btn_regi_next_step);

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
		
		TVNextStep.setTextColor(0xFF999999);
//		TVNextStep.setEnabled(false);
		
		ETInputPhoneNum.addTextChangedListener(this);
		ETInputCertiCode.addTextChangedListener(this);
		
		sid = loadSid();
		
		BtnBack = (RelativeLayout) findViewById(R.id.btn_back);
		BtnBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		counter = new TimeCount(60000, 1000);
		getCertiCodeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Log.e("TEST***", "Button1 Clicked!!!");

				counter.start();
				try {
					requestCertiCodeURL = new URL(HOST
							+ "/user/manage/sendVerifyCode.do"+"?sid="+sid);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

				MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
						Method.POST, requestCertiCodeURL.toString(), null,
						new Response.Listener<JSONObject>() {

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
								Log.e("TEST",
										"RequestCertiStatusCode: "
												+ String.valueOf(RequestCertiStatusCode));
								Log.e("TEST", "response:" + response.toString());
								try {
									Log.e("TEST", "sid:" + response.getString("sid"));
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
							
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
							}
						}) {
					@Override
					protected Map<String, String> getParams()
							throws AuthFailureError {
						Map<String, String> map = new HashMap<String, String>();
						map.put("mobile", ETInputPhoneNum.getText().toString());
						return map;
					}
				};
				mQueue.add(mJsonRequest);

			}

		});
		nextLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Log.e("TEST***", "Button2 Clicked!!!");
				
				try {
					verifyCertiCodeURL = new URL(HOST
							+ "/user/manage/confirmVerifyCode.do" + "?sid="
							+ sid);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
						Method.POST, verifyCertiCodeURL.toString(), null,
						new Response.Listener<JSONObject>() {

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
								Log.e("TEST",
										"ConfirmCertiStatusCode: "
												+ String.valueOf(ConfirmCertiStatusCode));
								Log.e("TEST", "response:" + response.toString());
								try {
									Log.e("TEST", "sid:" + response.getString("sid"));
								} catch (JSONException e) {
									e.printStackTrace();
								}
								if (ConfirmCertiStatusCode == 1) {
									Intent intent = new Intent();
									intent.setClass(getApplicationContext(), RegisterComplete.class);
									Log.e("TEST***", "Button Clicked -- ready to send the intent !!!!");
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
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {

							}
						}) {
					@Override
					protected Map<String, String> getParams()
							throws AuthFailureError {
						Map<String, String> map = new HashMap<String, String>();
						map.put("verifyCode", ETInputCertiCode.getText()
								.toString());
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
            finish();
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
//			TVGetCertiCode.setTextSize(16);
//			TVGetCertiCode.setBackgroundResource(R.drawable.btn_certi_counter);
			TVGetCertiCode.setText(getString(R.string.have_send) + '\n' + "(" + millisUntilFinished /1000+")");
		}
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    ETInputPhoneNum.setText("");
	    ETInputCertiCode.setText("");
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
		
		if(ETInputPhoneNum.getText().toString().length() > 10){
			getCertiCodeLayout.setEnabled(true);
		}else{
			getCertiCodeLayout.setEnabled(false);
		}
		
		
		if(ETInputPhoneNum.getText().toString().length() > 10 && ETInputCertiCode.getText().toString().length() > 4){
			nextLayout.setEnabled(true);
			TVNextStep.setTextColor(0xFF0065a1);
			nextLayout.setBackgroundResource(R.drawable.selector_next_step);
//			TVNextStep.setEnabled(true);
		} else {
			nextLayout.setEnabled(false);
			TVNextStep.setTextColor(0xFF999999);
			nextLayout.setBackgroundResource(R.drawable.shape_next_disable);
//			TVNextStep.setEnabled(false);
		}
	}
	
}