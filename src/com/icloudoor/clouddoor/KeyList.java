package com.icloudoor.clouddoor;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class KeyList extends Activity {
	private ImageView IvBack;

	private URL downLoadKeyURL;
	private RequestQueue mQueue;

	private String HOST = "http://zone.icloudoor.com/icloudoor-web";
	private String sid = null;
	private JsonObjectRequest mJsonRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.key_list);

		IvBack = (ImageView) findViewById(R.id.btn_back_key_list);
		IvBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});

		mQueue = Volley.newRequestQueue(this);
		sid = loadSid();
		try {
			downLoadKeyURL = new URL(HOST + "/user/door/download.do" + "?sid="
					+ sid);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		mJsonRequest = new JsonObjectRequest(Method.POST,
				downLoadKeyURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
		mQueue.add(mJsonRequest);
	}

//	public void parseKeyData() {
//
//	}

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