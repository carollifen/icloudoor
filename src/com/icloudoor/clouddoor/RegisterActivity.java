package com.icloudoor.clouddoor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements TextWatcher {
	
	private String TAG = this.getClass().getSimpleName();
	
	private TextView TVGetCertiCode;
	private TextView TVNextStep;
	private EditText ETInputPhoneNum;
	private EditText ETInputCertiCode;
	private URL requestCertiCodeURL, verifyCertiCodeURL;
	private RequestQueue mQueue;
	private RelativeLayout BtnBack;
	private SmsContent content;
	private TimeCount counter;

	private int RequestCertiStatusCode;
	private int ConfirmCertiStatusCode;
	private String sid = null;

	private String HOST = "https://zone.icloudoor.com/icloudoor-web";
	
	// for new ui
	private RelativeLayout phoneLayout;
	private RelativeLayout phoneInputLayout;
	private RelativeLayout getCertiCodeLayout;
	private RelativeLayout inputCertiCodeLayout;
	private RelativeLayout nextLayout;
	private RelativeLayout xieYiLayout;
	private TextView xieYiText;
	private TextView sendText;
	private CheckBox checkBox;
	private boolean checkXieyi = false;
	
	private boolean isBackKey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.register);
		
		setupUI(findViewById(R.id.main));
		
		Log.e(TAG, "oncreate");

		isBackKey = false;
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
		xieYiLayout = (RelativeLayout) findViewById(R.id.xieyi_layout);
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) phoneLayout.getLayoutParams();
		RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) phoneInputLayout.getLayoutParams();
		RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) getCertiCodeLayout.getLayoutParams();
		RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) inputCertiCodeLayout.getLayoutParams();
		RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams) nextLayout.getLayoutParams();
		RelativeLayout.LayoutParams params5 = (RelativeLayout.LayoutParams) xieYiLayout.getLayoutParams();
		params.width = screenWidth - 48*2;
		params1.width = (screenWidth - 48*2 - 8)*2/3;
		params2.width = (screenWidth - 48*2 - 8)*1/3;
		params3.width = screenWidth - 48*2;
		params4.width = screenWidth - 48*2;
		params5.width = screenWidth - 48*2;
		
		//TODO
		xieYiText = (TextView) findViewById(R.id.xieyi_text);
		xieYiText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, XieYiActivity.class);
				startActivity(intent);
			}
			
		});
		//
		
		sendText = (TextView) findViewById(R.id.send_text);
		sendText.setWidth(screenWidth - 48*2);
		
		checkBox = (CheckBox) findViewById(R.id.check_box);
		
		phoneLayout.setLayoutParams(params);
		phoneInputLayout.setLayoutParams(params1);
		getCertiCodeLayout.setLayoutParams(params2);
		inputCertiCodeLayout.setLayoutParams(params3);
		nextLayout.setLayoutParams(params4);
		xieYiLayout.setLayoutParams(params5);
		
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
				isBackKey = true;
				finish();
			}
			
		});

        content = new SmsContent(new Handler());
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, content);
		counter = new TimeCount(60000, 1000);
		getCertiCodeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

                if (ETInputPhoneNum.getText().toString().length() > 11){
                    Toast.makeText(getApplicationContext(), R.string.error_phonenumb_over, Toast.LENGTH_SHORT).show();
                }else {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    counter.start();
                    try {
                        requestCertiCodeURL = new URL(HOST + "/user/manage/sendVerifyCode.do" + "?sid=" + sid);
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
                                        RequestCertiStatusCode = response.getInt("code");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    Log.e("TEST", "response:" + response.toString());

                                    if (RequestCertiStatusCode == -20) {
                                        Toast.makeText(getApplicationContext(),
                                                R.string.send_too_many_a_day, Toast.LENGTH_SHORT)
                                                .show();
                                    } else if (RequestCertiStatusCode == -21) {
                                        Toast.makeText(getApplicationContext(),
                                                R.string.send_too_frequently, Toast.LENGTH_SHORT)
                                                .show();
                                    } else if(RequestCertiStatusCode == -99) {
    									Toast.makeText(getApplicationContext(),
    											R.string.unknown_err, Toast.LENGTH_SHORT)
    											.show();
    								}
                                }

                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
    							Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
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
			}

		});
		nextLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				try {
					verifyCertiCodeURL = new URL(HOST + "/user/manage/confirmVerifyCode4Reg.do" + "?sid=" + sid);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
						Method.POST, verifyCertiCodeURL.toString(), null,
						new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								try {
									ConfirmCertiStatusCode = response.getInt("code");
								} catch (JSONException e) {
									e.printStackTrace();
								}

								if (ConfirmCertiStatusCode == 1) {
									try {
										if (response.getString("sid") != null) {
											sid = response.getString("sid");
											saveSid(sid);
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
									Intent intent = new Intent();
									intent.setClass(getApplicationContext(), RegisterComplete.class);
									startActivityForResult(intent, 0);
								} else if (ConfirmCertiStatusCode == -30) {
									Toast.makeText(getApplicationContext(),
											R.string.input_wrong_certi_code, Toast.LENGTH_SHORT)
											.show();
								} else if (ConfirmCertiStatusCode == -31) {
									Toast.makeText(getApplicationContext(), R.string.certi_code_overdue,
											Toast.LENGTH_SHORT).show();
								} else if(ConfirmCertiStatusCode == -40) {
									Toast.makeText(getApplicationContext(), R.string.phone_num_have_been_registerred,
											Toast.LENGTH_LONG).show();
									SharedPreferences RegiPhone = getSharedPreferences("REGIPHONE", 0);
						          	Editor editor = RegiPhone.edit();
						          	editor.putString("PHONE", ETInputPhoneNum.getText().toString());
						          	editor.commit();
									finish();
								} else if(ConfirmCertiStatusCode == -99){
									Toast.makeText(getApplicationContext(), R.string.error_in_send_sms,
											Toast.LENGTH_SHORT).show();
								}
							}
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
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
        	SharedPreferences RegiPhone = getSharedPreferences("REGIPHONE", 0);
          	Editor editor = RegiPhone.edit();
          	editor.putString("PHONE", ETInputPhoneNum.getText().toString());
          	editor.commit();
          	
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
		}
		@Override
		public void onTick(long millisUntilFinished){
			getCertiCodeLayout.setEnabled(false);
			getCertiCodeLayout.setBackgroundResource(R.drawable.shape_right_corner_pressed);
			TVGetCertiCode.setText(getString(R.string.have_send) + '\n' + "(" + millisUntilFinished /1000+")");
		}
	}

    class SmsContent extends ContentObserver {
        private Cursor cursor = null;
        private String subString = null;

        public SmsContent(Handler handler){
            super(handler);
            // TODO Auto-generated constructor stub
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            cursor = managedQuery(Uri.parse("content://sms/inbox"),
                    new String[] { "_id", "address", "read", "body" },
                    /*" address=? and read=?"*/null,
                    /*new String[] { "10690023192088", "0" }*/null, "_id desc");
            if (cursor != null && cursor.getCount() > 0){
                ContentValues values = new ContentValues();
                values.put("read", "1");
                cursor.moveToNext();
                int smsbodyColumn = cursor.getColumnIndex("body");
                String smsBody = cursor.getString(smsbodyColumn);
                if (smsBody.toString().length() > 10) {
                    subString = smsBody.substring(0, 6);
//                Log.e("test22", subString);
                    int ret = subString.compareTo(getString(R.string.sms_content_to_compare));
                    if (ret == 0) {
                        ETInputCertiCode.setText(getDynamicPassword(smsBody));
                        ETInputCertiCode.clearFocus();
                    }
                }
            }

            if (Build.VERSION.SDK_INT < 14) {
                cursor.close();
            }
        }

        /**
         *
         * @param str Content of the message
         *
         * @return get the verify code (5-bit)
         */
        public String getDynamicPassword(String str) {
            // verify code is 5 bits
            Pattern continuousNumberPattern = Pattern.compile("(?<![0-9])([0-9]{"
                    + 5 + "})(?![0-9])");
            Matcher m = continuousNumberPattern.matcher(str);
            String dynamicPassword = "";
            while (m.find()) {
                System.out.print(m.group());
                dynamicPassword = m.group();
            }

            return dynamicPassword;
        }
    }

	@Override
	protected void onResume() {
	    super.onResume();
	    
	    Log.e(TAG, "onresume");

	    ETInputPhoneNum.setText("");
	    ETInputCertiCode.setText("");
	    
	    Log.e(TAG, String.valueOf(isBackKey));
	    
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
	
	public void onStop(){
		super.onStop();
		
		Log.e(TAG, "onstop");
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
			
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if(isChecked){
						checkXieyi = true;
						
						nextLayout.setEnabled(true);
						TVNextStep.setTextColor(0xFF0065a1);
						nextLayout.setBackgroundResource(R.drawable.selector_next_step);
						Log.e(TAG, "checked!");
					}else{
						checkXieyi = false;
						
						nextLayout.setEnabled(false);
						TVNextStep.setTextColor(0xFF999999);
						nextLayout.setBackgroundResource(R.drawable.shape_next_disable);
						Log.e(TAG, "unchecked!");
					}
				}
				
			});
		} else {
			nextLayout.setEnabled(false);
			TVNextStep.setTextColor(0xFF999999);
			nextLayout.setBackgroundResource(R.drawable.shape_next_disable);
		}
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "ondestroy");
        this.getContentResolver().unregisterContentObserver(content);
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
	
	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}
	
	public void setupUI(View view) {
		// Set up touch listener for non-text box views to hide keyboard.
		if (!(view instanceof EditText)) {
			view.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					hideSoftKeyboard(RegisterActivity.this); 
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