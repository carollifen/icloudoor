package com.icloudoor.clouddoor;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingFragment extends Fragment {
	public Context context;

	private RelativeLayout RLSet;
	private RelativeLayout RLSig;
	private RelativeLayout RLShare;
	private RelativeLayout RLUpdate;

	private TextView logOut;
	private TextView toShowPersonalInfo;

	private MyOnClickListener myClickListener;

	private RequestQueue mQueue;
	private String HOST = "http://zone.icloudoor.com/icloudoor-web";
	private URL logOutURL;
	private String sid = null;
	private int statusCode;

	private int isLogin = 1;

	public SettingFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.set_page, container, false);

		myClickListener = new MyOnClickListener();

		mQueue = Volley.newRequestQueue(getActivity());
		sid = loadSid();

		RLSet = (RelativeLayout) view.findViewById(R.id.btn_set);
		RLSig = (RelativeLayout) view.findViewById(R.id.btn_sig);
		RLShare = (RelativeLayout) view.findViewById(R.id.btn_share);
		RLUpdate = (RelativeLayout) view.findViewById(R.id.btn_update);

		toShowPersonalInfo = (TextView) view.findViewById(R.id.toshow_personal_info_in_set);
		logOut = (TextView) view.findViewById(R.id.btn_logout);

		RLSet.setOnClickListener(myClickListener);
		RLSig.setOnClickListener(myClickListener);
		RLShare.setOnClickListener(myClickListener);
		RLUpdate.setOnClickListener(myClickListener);

		toShowPersonalInfo.setOnClickListener(myClickListener);
		logOut.setOnClickListener(myClickListener);

		return view;
	}

	public class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.toshow_personal_info_in_set:
				break;
			case R.id.btn_set:
				Intent intent = new Intent();
				intent.setClass(getActivity(), SettingDetailActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_sig:
				Intent intent1 = new Intent();
				intent1.setClass(getActivity(), SignActivity.class);
				startActivity(intent1);
				break;
			case R.id.btn_share:
				break;
			case R.id.btn_update:
				break;
			case R.id.btn_logout:
				try {
					logOutURL = new URL(HOST + "/user/manage/logout.do"
							+ "?sid=" + sid);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
						Method.POST, logOutURL.toString(), null,
						new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								try {
									if (response.getString("sid") != null){
										sid = response.getString("sid");
										saveSid(sid);
									}
									statusCode = response.getInt("code");
									
									isLogin = 0;
									SharedPreferences loginStatus = getActivity()
											.getSharedPreferences("LOGINSTATUS", 0);
									Editor editor1 = loginStatus.edit();
									editor1.putInt("LOGIN", isLogin);
									editor1.commit();
									Intent intent2 = new Intent();
									intent2.setClass(getActivity(), Login.class);
									startActivity(intent2);
									
									CloudDoorMainActivity mainActivity = (CloudDoorMainActivity) getActivity();
									mainActivity.finish();
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
							}
						});
				mQueue.add(mJsonRequest);
	
				break;
			}
		}

	}

	public void saveSid(String sid) {
		SharedPreferences savedSid = getActivity().getSharedPreferences(
				"SAVEDSID", 0);
		Editor editor = savedSid.edit();
		editor.putString("SID", sid);
		editor.commit();
	}

	public String loadSid() {
		SharedPreferences loadSid = getActivity().getSharedPreferences(
				"SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}

	@Override
    public void onDetach() {
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        super.onDetach();

    }
}
