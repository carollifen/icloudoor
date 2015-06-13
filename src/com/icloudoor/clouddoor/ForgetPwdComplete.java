package com.icloudoor.cloudoor;

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
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ForgetPwdComplete extends Activity implements TextWatcher {
	private EditText ETInputPwd;
	private EditText ETConfirmPwd;
	private TextView TVConfirm;
	private String inputPwd, confirmPwd;
	private RelativeLayout BtnBack;

	private URL registerURL;
	private RequestQueue mQueue;
	private int statusCode;
	private String HOST = "http://test.zone.icloudoor.com/icloudoor-web";
	private String sid = null;

	// for new ui
	private RelativeLayout pwdLayout;
	private RelativeLayout pwdAgainLayout;
	private RelativeLayout forgetCompleteLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getActionBar().hide();
		setContentView(R.layout.find_pwd_complete);

		setupUI(findViewById(R.id.main));

		ETInputPwd = (EditText) findViewById(R.id.forget_pwd_input_new_pwd);
		ETConfirmPwd = (EditText) findViewById(R.id.forget_pwd_input_new_pwd_again);
		TVConfirm = (TextView) findViewById(R.id.forget_pwd_confirm);

		// for new ui
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;

		pwdLayout = (RelativeLayout) findViewById(R.id.regi_input_pwd_layout);
		pwdAgainLayout = (RelativeLayout) findViewById(R.id.regi_input_pwd_again_layout);
		forgetCompleteLayout = (RelativeLayout) findViewById(R.id.regi_complete_layout);

		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) pwdLayout
				.getLayoutParams();
		RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) forgetCompleteLayout
				.getLayoutParams();
		RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) pwdAgainLayout
				.getLayoutParams();
		params.width = screenWidth - 48 * 2;
		params1.width = screenWidth - 48 * 2;
		params2.width = screenWidth - 48 * 2;
		pwdLayout.setLayoutParams(params);
		forgetCompleteLayout.setLayoutParams(params1);
		pwdAgainLayout.setLayoutParams(params2);

		pwdLayout.setBackgroundResource(R.drawable.shape_input_certi_code);
		pwdAgainLayout.setBackgroundResource(R.drawable.shape_input_certi_code);
		forgetCompleteLayout
				.setBackgroundResource(R.drawable.shape_regi_complete_disable);

		TVConfirm.setTextColor(0xFF999999);
		forgetCompleteLayout.setEnabled(false);

		TVConfirm.setTextColor(0xFF999999);
		forgetCompleteLayout.setEnabled(false);

		ETInputPwd.addTextChangedListener(this);
		ETConfirmPwd.addTextChangedListener(this);

		BtnBack = (RelativeLayout) findViewById(R.id.btn_back);
		BtnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(),
						ForgetPwdActivity.class);
				startActivity(intent);

				ForgetPwdComplete.this.finish();
			}

		});

		mQueue = Volley.newRequestQueue(this);
		sid = loadSid();

		forgetCompleteLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					registerURL = new URL(HOST
							+ "/user/manage/changePassword2.do" + "?sid=" + sid);
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
										statusCode = response.getInt("code");
									} catch (JSONException e) {
										e.printStackTrace();
									}

									if (statusCode == 1) {
										try {
											if (response.getString("sid") != null) {
												sid = response.getString("sid");
												saveSid(sid);
											}
										} catch (JSONException e) {
											e.printStackTrace();
										}
										Toast.makeText(getApplicationContext(),
												R.string.forget_success,
												Toast.LENGTH_SHORT).show();

										SharedPreferences RegiPhone = getSharedPreferences(
												"REGIPHONE", 0);
										Editor editor = RegiPhone.edit();
										editor.putString("PWD", confirmPwd);
										editor.commit();

										setResult(RESULT_OK);
										finish();
									} else if (statusCode == -41) {
										Toast.makeText(getApplicationContext(),
												R.string.weak_pwd,
												Toast.LENGTH_SHORT).show();
									} else if (statusCode == -99) {
										Toast.makeText(getApplicationContext(),
												R.string.unknown_err,
												Toast.LENGTH_SHORT).show();
									} else if (statusCode == -72) {
										Toast.makeText(getApplicationContext(),
												R.string.user_not_exist,
												Toast.LENGTH_SHORT).show();
									}
								}
							}, new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
									Toast.makeText(ForgetPwdComplete.this,
											R.string.network_error,
											Toast.LENGTH_SHORT).show();
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
				} else
					Toast.makeText(v.getContext(), R.string.diff_pwd,
							Toast.LENGTH_SHORT).show();
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

		String temp = s.toString();

		if (temp.length() > 1) {
			String tem = temp.substring(temp.length() - 1, temp.length());
			char[] temC = tem.toCharArray();
			int mid = temC[0];

			if ((mid >= 48 && mid <= 57) || (mid >= 65 && mid <= 90)
					|| (mid > 97 && mid <= 122)) {

			} else {
				s.delete(temp.length() - 1, temp.length());
				Toast.makeText(this, R.string.input_wrong, Toast.LENGTH_SHORT)
						.show();
			}
		} else if (temp.length() == 1) {
			char[] temC = temp.toCharArray();
			int mid = temC[0];

			if ((mid >= 48 && mid <= 57) || (mid >= 65 && mid <= 90)
					|| (mid > 97 && mid <= 122)) {

			} else {
				s.clear();
				Toast.makeText(this, R.string.input_wrong, Toast.LENGTH_SHORT)
						.show();
			}
		}

		if (ETInputPwd.getText().toString().length() > 5
				&& ETConfirmPwd.getText().toString().length() > 5) {
			TVConfirm.setTextColor(getResources().getColorStateList(
					R.color.text_confirm_pwd));
			forgetCompleteLayout.setEnabled(true);
			forgetCompleteLayout
					.setBackgroundResource(R.drawable.selector_next_step);
		} else {
			TVConfirm.setTextColor(0xFF999999);
			forgetCompleteLayout.setEnabled(false);
			forgetCompleteLayout
					.setBackgroundResource(R.drawable.shape_regi_complete_disable);
		}
	}

	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
				.getWindowToken(), 0);
	}

	public void setupUI(View view) {
		// Set up touch listener for non-text box views to hide keyboard.
		if (!(view instanceof EditText)) {
			view.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					hideSoftKeyboard(ForgetPwdComplete.this);
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