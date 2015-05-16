package com.icloudoor.clouddoor;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingFragment extends Fragment {
	public Context context;

	private RelativeLayout RLSet;
	private RelativeLayout RLSig;
	private RelativeLayout RLShare;
	private RelativeLayout RLUpdate;
	private RelativeLayout showInfo;

	private TextView logOut;
	
	private TextView showName;
	private TextView showPhoneNum;
	private String name;
	private String phone;

	private MyOnClickListener myClickListener;

	private RequestQueue mQueue;
	private String HOST = "http://zone.icloudoor.com/icloudoor-web";
	private URL logOutURL;
	private String sid = null;
	private int statusCode;

	private int isLogin = 1;
	
	private ImageView image;
	private String portraitUrl;
	
	private Bitmap bitmap;
	private Thread mThread;
	
	private static final int MSG_SUCCESS = 0;//get the image success
	private static final int MSG_FAILURE = 1;// fail

	public SettingFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.set_page, container, false);

		mQueue = Volley.newRequestQueue(getActivity());
		myClickListener = new MyOnClickListener();

		showInfo = (RelativeLayout) view.findViewById(R.id.btn_show_personal_info);
		RLSet = (RelativeLayout) view.findViewById(R.id.btn_set);
		RLSig = (RelativeLayout) view.findViewById(R.id.btn_sig);
		RLShare = (RelativeLayout) view.findViewById(R.id.btn_share);
		RLUpdate = (RelativeLayout) view.findViewById(R.id.btn_update);
		showName = (TextView) view.findViewById(R.id.show_name);
		showPhoneNum = (TextView) view.findViewById(R.id.show_phoneNum);
		
		
		image = (ImageView) view.findViewById(R.id.person_image);

		
		
		SharedPreferences loginStatus = getActivity().getSharedPreferences("LOGINSTATUS", 0);	
		phone = loginStatus.getString("PHONENUM", null);	
		name = loginStatus.getString("NAME", null);
		portraitUrl = loginStatus.getString("URL", null);
		
		
		changeNum();
		showPhoneNum.setText(phone);
		showName.setText(name);
		
		logOut = (TextView) view.findViewById(R.id.btn_logout);

		RLSet.setOnClickListener(myClickListener);
		RLSig.setOnClickListener(myClickListener);
		RLShare.setOnClickListener(myClickListener);
		RLUpdate.setOnClickListener(myClickListener);

		showInfo.setOnClickListener(myClickListener);
		logOut.setOnClickListener(myClickListener);

		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (mThread == null) {
			mThread = new Thread(runnable);
			mThread.start();
		}
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				image.setImageBitmap((Bitmap) msg.obj);
				break;
			case MSG_FAILURE:
				break;
			}
		}
	};
	
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(portraitUrl);
			final Bitmap bitmap;
			try {
				org.apache.http.HttpResponse httpResponse = httpClient
						.execute(httpGet);

				bitmap = BitmapFactory.decodeStream(httpResponse.getEntity()
						.getContent());
			} catch (Exception e) {
				mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
				return;
			}
	
			mHandler.obtainMessage(MSG_SUCCESS, bitmap).sendToTarget();
		}
	};

	public class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_show_personal_info:
				Intent intent = new Intent();
				intent.setClass(getActivity(), ShowPersonalInfo.class);
				startActivity(intent);
				break;
			case R.id.btn_set:
				Intent intent1 = new Intent();
				intent1.setClass(getActivity(), SettingDetailActivity.class);
				startActivity(intent1);
				break;
			case R.id.btn_sig:
				Intent intent2 = new Intent();
				intent2.setClass(getActivity(), SignActivity.class);
				startActivity(intent2);
				break;
			case R.id.btn_share:
				break;
			case R.id.btn_update:
				break;
			case R.id.btn_logout:
                if ("NET_WORKS".equals(loadSid("NETSTATE"))) {
                    sid = loadSid("SID");

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
                                        if (response.getString("sid") != null) {
                                            sid = response.getString("sid");
                                            saveSid("SID", sid);
                                        }
                                        statusCode = response.getInt("code");

                                        isLogin = 0;
                                        SharedPreferences loginStatus = getActivity()
                                                .getSharedPreferences("LOGINSTATUS", 0);
                                        Editor editor1 = loginStatus.edit();
                                        editor1.putInt("LOGIN", isLogin);
                                        editor1.commit();
                                        Intent intent3 = new Intent();
                                        intent3.setClass(getActivity(), Login.class);
                                        startActivity(intent3);

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
                }else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                    }
                }
				break;
			}
		}

	}

	public void changeNum(){
		if(phone != null){	
			StringBuilder sb = new StringBuilder(phone); 
			sb.setCharAt(3, '*');
			sb.setCharAt(4, '*');
			sb.setCharAt(5, '*'); 
			sb.setCharAt(6, '*');
			phone = sb.toString();
		}
	}
	
	public void saveSid(String key, String value) {
		SharedPreferences savedSid = getActivity().getSharedPreferences(
				"SAVEDSID", 0);
		Editor editor = savedSid.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String loadSid(String key) {
		SharedPreferences loadSid = getActivity().getSharedPreferences(
				"SAVEDSID", 0);
		return loadSid.getString(key, null);
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
