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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ForgetPwdNewPwd extends Activity {
	private EditText ETNewPwd;
	private EditText ETConfirmPwd;
	private TextView TVResetComplete;
	private URL changePasswordURL;
	private RequestQueue mQueue;

	private String newPwd, confirmPwd;

	private int statusCode;
	private String HOST = "http://zone.icloudoor.com/icloudoor-web";
	private String sid = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		setContentView(R.layout.reset_pwd);

		mQueue = Volley.newRequestQueue(this);

		ETNewPwd = (EditText) findViewById(R.id.reset_pwd_input_pwd);
		ETConfirmPwd = (EditText) findViewById(R.id.reset_pwd_input_pwd_again);
		TVResetComplete = (TextView) findViewById(R.id.reset_pwd_complete);

		sid = loadSid();

		TVResetComplete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					changePasswordURL = new URL(HOST
							+ "/user/manage/changePassword2.do" + "?sid=" + sid);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				newPwd = ETNewPwd.getText().toString();
				confirmPwd = ETConfirmPwd.getText().toString();
				if (newPwd.equals(confirmPwd)) {
					MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
							Method.POST, changePasswordURL.toString(), null,
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

					if (statusCode == 1) {
						Intent intent = new Intent();
						intent.setClass(v.getContext(), Login.class);
						startActivity(intent);
					} 
				} else {
					Toast.makeText(v.getContext(), R.string.diff_pwd,
							Toast.LENGTH_SHORT).show();
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