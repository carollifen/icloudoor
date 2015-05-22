package com.icloudoor.clouddoor;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.umeng.message.tag.TagManager;

public class CloudDoorMainActivity extends FragmentActivity {
    private final String TAG = this.getClass().getSimpleName();
//	private ViewPager mViewPager;
//	private ArrayList<Fragment> mFragmentsList;
//	private MyFragmentPagerAadpter mFragmentAdapter;
    public static CloudDoorMainActivity instance = null;
	private MsgFragment mMsgFragment;
	private KeyFragment mKeyFragment;
	private KeyFragmentNoBLE mKeyFragmentNoBLE;
	private SettingFragment mSettingFragment;
	private WuyeFragment mWuyeFragment;

	private RelativeLayout bottomWuye;
	private RelativeLayout bottomMsg;
	private RelativeLayout bottomKey;
	private RelativeLayout bottomSetting;

	private TextView bottomTvMsg;
	private TextView bottomTvKey;
	private TextView bottomTvSetting;
	private TextView bottomTvWuye;

	private ImageView bottomIvMsg;
	private ImageView bottomIvKey;
	private ImageView bottomIvSetting;
	private ImageView bottomIvWuye;

	public FragmentManager mFragmentManager;
	public MyOnClickListener myClickListener;
	public FragmentTransaction mFragmenetTransaction;
//	public MyPageChangeListener myPageChangeListener;

	private int COLOR_GRAY = 0xFF999999;
	private int COLOR_BLACK = 0xFF0065a1;

	private float alpha_half_transparent = 0.2f;
	private float alpha_opaque = 1.0f;
	
	private int homePressed = 0;
	private int lockScreenBefore = 0;
	
	private int currentVersion;
	
	// for Umeng Push Service
	private RequestQueue mRequestQueue;
 	private String url="http://zone.icloudoor.com/icloudoor-web/user/api/getTags.do";
 	private String tag;
 	private String sid;
 	private PushAgent mPushAgent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		setContentView(R.layout.new_main);
		
		// for Umeng Push Service
		mPushAgent = PushAgent.getInstance(getApplicationContext());
		mPushAgent.enable();
		String device_token = UmengRegistrar
				.getRegistrationId(getApplicationContext());
		Log.e("devicetoken", device_token);
		// mPushAgent.setDebugMode(true);
		mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		sid = loadSid();
		JsonObjectRequest mJsonObjectRequest = new JsonObjectRequest(url
				+ "?sid=" + sid, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				Log.e("response", response.toString());
				try {
					
					if(response.getString("sid") != null)
						saveSid("SID", sid);
					
					if(response.getInt("code") == 1){
						JSONArray tagJson = response.getJSONArray("data");
						for (int i = 0; i < tagJson.length(); i++) {
							tag = (String) tagJson.get(i);
							Log.e("response", tag);
							new AddTagTask(tag).execute();
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
			}
		});
		mRequestQueue.add(mJsonObjectRequest);

		
        registerReceiver(mConnectionStatusReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        instance = this;
        
		SharedPreferences personalInfo = getSharedPreferences("PERSONSLINFO", MODE_PRIVATE);
		int setPersonal = personalInfo.getInt("SETINFO", 1);
		if(setPersonal == 0) {
			Log.e("jump to set", "in main activity");
			Intent  intentSetInfo = new Intent();
			intentSetInfo.setClass(getApplicationContext(), SetPersonalInfo.class);
			startActivity(intentSetInfo);
		}

		mMsgFragment = new MsgFragment();
		currentVersion = android.os.Build.VERSION.SDK_INT;
		if(currentVersion >= 18){
			mKeyFragment = new KeyFragment();
		}else{
			mKeyFragmentNoBLE = new KeyFragmentNoBLE();
		}
		mSettingFragment = new SettingFragment();
		mWuyeFragment = new WuyeFragment();
		
//		mFragmentManager = getSupportFragmentManager();

//		InitViewPager();
		InitViews();
		InitState();
		
		registerReceiver(mHomeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));


	}


	public void InitViews() {
		myClickListener = new MyOnClickListener();
//		myPageChangeListener = new MyPageChangeListener();

//		mViewPager = (ViewPager) findViewById(R.id.vPager);

		bottomMsg = (RelativeLayout) findViewById(R.id.bottom_msg_layout);
		bottomKey = (RelativeLayout) findViewById(R.id.bottom_key_layout);
		bottomSetting = (RelativeLayout) findViewById(R.id.bottom_setting_layout);
		bottomWuye = (RelativeLayout) findViewById(R.id.bottom_wuye_layout);

		bottomTvMsg = (TextView) findViewById(R.id.msg_text);
		bottomTvKey = (TextView) findViewById(R.id.key_text);
		bottomTvSetting = (TextView) findViewById(R.id.set_text);
		bottomTvWuye = (TextView) findViewById(R.id.wuye_text);

		bottomIvMsg = (ImageView) findViewById(R.id.msg_image);
		bottomIvKey = (ImageView) findViewById(R.id.key_image);
		bottomIvSetting = (ImageView) findViewById(R.id.set_image);
		bottomIvWuye = (ImageView) findViewById(R.id.wuye_image);

//		mViewPager.setAdapter(mFragmentAdapter);
//		mViewPager.setOnPageChangeListener(myPageChangeListener);

		bottomMsg.setOnClickListener(myClickListener);
		bottomKey.setOnClickListener(myClickListener);
		bottomSetting.setOnClickListener(myClickListener);
		bottomWuye.setOnClickListener(myClickListener);
	}

//	public void InitViewPager() {
//		mFragmentsList = new ArrayList<Fragment>();
//
//		mMsgFragment = new MsgFragment();
//		mKeyFragment = new KeyFragment();
//		mSettingFragment = new SettingFragment();
//
//		mFragmentsList.add(mMsgFragment);
//		mFragmentsList.add(mKeyFragment);
//		mFragmentsList.add(mSettingFragment);
//
//		mFragmentAdapter = new MyFragmentPagerAadpter(mFragmentManager,
//				mFragmentsList);
//	}

	public void InitState() {
//		mViewPager.setCurrentItem(1);
		mFragmentManager = getSupportFragmentManager();
		mFragmenetTransaction = mFragmentManager.beginTransaction();
		if(currentVersion >= 18){
			mFragmenetTransaction.replace(R.id.id_content, mKeyFragment).commit();
		}else{
			mFragmenetTransaction.replace(R.id.id_content, mKeyFragmentNoBLE).commit();
		}
		
		
		bottomTvKey.setTextColor(COLOR_BLACK);
		bottomTvMsg.setTextColor(COLOR_GRAY);
		bottomTvSetting.setTextColor(COLOR_GRAY);
		bottomTvWuye.setTextColor(COLOR_GRAY);

		bottomIvMsg.setImageResource(R.drawable.msg_normal);
		bottomIvKey.setImageResource(R.drawable.key_pressed);
		bottomIvSetting.setImageResource(R.drawable.my_normal);
		bottomIvWuye.setImageResource(R.drawable.wuye_normal);
	}

	public void onResume() {
		super.onResume();
		Log.e(TAG, "onResume");
		
		SharedPreferences homeKeyEvent = getSharedPreferences("HOMEKEY", 0);
		homePressed = homeKeyEvent.getInt("homePressed", 0);
		
		SharedPreferences setSign = getSharedPreferences("SETTING", 0);
		int useSign = setSign.getInt("useSign", 0);
		
		if(homePressed == 1 && useSign == 1) {

			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), VerifyGestureActivity.class);
			startActivity(intent);
		}
	}
	
	public void onDestroy() {
        Log.e(TAG, "onDestroy");
		unregisterReceiver(mHomeKeyEventReceiver);
        unregisterReceiver(mConnectionStatusReceiver);
        super.onDestroy();
    }
 
	public class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View view) {
			BottomColorChange(view.getId());
		}
	}

//	public class MyPageChangeListener implements OnPageChangeListener {
//
//		@Override
//		public void onPageScrollStateChanged(int arg0) {
//			if (arg0 == 2) {
//				int index = mViewPager.getCurrentItem();
//				BottomColorChange(index);
//			}
//		}
//
//		@Override
//		public void onPageScrolled(int arg0, float arg1, int arg2) {
//		}
//
//		@Override
//		public void onPageSelected(int index) {
//		}
//
//	}

	public void BottomColorChange(int index) {
		mFragmentManager = getSupportFragmentManager();
		mFragmenetTransaction = mFragmentManager.beginTransaction();
		switch (index) {
		case R.id.bottom_wuye_layout:
			bottomTvKey.setTextColor(COLOR_GRAY);
			bottomTvMsg.setTextColor(COLOR_GRAY);
			bottomTvSetting.setTextColor(COLOR_GRAY);
			bottomTvWuye.setTextColor(COLOR_BLACK);

			bottomIvMsg.setImageResource(R.drawable.msg_normal);
			bottomIvKey.setImageResource(R.drawable.key_normal);
			bottomIvSetting.setImageResource(R.drawable.my_normal);
			bottomIvWuye.setImageResource(R.drawable.wuye_selected);
			
			mFragmenetTransaction.replace(R.id.id_content, mWuyeFragment);
			break;
		case R.id.bottom_msg_layout:
			bottomTvKey.setTextColor(COLOR_GRAY);
			bottomTvMsg.setTextColor(COLOR_BLACK);
			bottomTvSetting.setTextColor(COLOR_GRAY);
			bottomTvWuye.setTextColor(COLOR_GRAY);

			bottomIvMsg.setImageResource(R.drawable.msg_selected);
			bottomIvKey.setImageResource(R.drawable.key_normal);
			bottomIvSetting.setImageResource(R.drawable.my_normal);
			bottomIvWuye.setImageResource(R.drawable.wuye_normal);

			mFragmenetTransaction.replace(R.id.id_content, mMsgFragment);
			
//			mViewPager.setCurrentItem(0);
			break;

		case R.id.bottom_key_layout:
			bottomTvKey.setTextColor(COLOR_BLACK);
			bottomTvMsg.setTextColor(COLOR_GRAY);
			bottomTvSetting.setTextColor(COLOR_GRAY);
			bottomTvWuye.setTextColor(COLOR_GRAY);

			bottomIvMsg.setImageResource(R.drawable.msg_normal);
			bottomIvKey.setImageResource(R.drawable.key_pressed);
			bottomIvSetting.setImageResource(R.drawable.my_normal);
			bottomIvWuye.setImageResource(R.drawable.wuye_normal);

			if(currentVersion >= 18){
				mFragmenetTransaction.replace(R.id.id_content, mKeyFragment);
			}else{
				mFragmenetTransaction.replace(R.id.id_content, mKeyFragmentNoBLE);
			}

//			mViewPager.setCurrentItem(1);
			break;

		case R.id.bottom_setting_layout:
			bottomTvKey.setTextColor(COLOR_GRAY);
			bottomTvMsg.setTextColor(COLOR_GRAY);
			bottomTvSetting.setTextColor(COLOR_BLACK);
			bottomTvWuye.setTextColor(COLOR_GRAY);

			bottomIvMsg.setImageResource(R.drawable.msg_normal);
			bottomIvKey.setImageResource(R.drawable.key_normal);
			bottomIvSetting.setImageResource(R.drawable.my_selected);
			bottomIvWuye.setImageResource(R.drawable.wuye_normal);

			mFragmenetTransaction.replace(R.id.id_content, mSettingFragment);
			
//			mViewPager.setCurrentItem(2);
			break;
		}
		mFragmenetTransaction.commit();
	}

	private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {

		String SYSTEM_REASON = "reason";
		String SYSTEM_HOME_KEY = "homekey";
		String SYSTEM_DIALOG_REASON_LOCK = "lock";
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
					Log.e("TEST", "homekey pressed before");
					homePressed = 1;
					
					SharedPreferences homeKeyEvent = getSharedPreferences("HOMEKEY", 0);
					Editor editor = homeKeyEvent.edit();
					editor.putInt("homePressed", homePressed);
					editor.commit();

				}else if(TextUtils.equals(reason, SYSTEM_DIALOG_REASON_LOCK)){
					homePressed = 1;
					
					SharedPreferences homeKeyEvent = getSharedPreferences("HOMEKEY", 0);
					Editor editor = homeKeyEvent.edit();
					editor.putInt("homePressed", homePressed);
					editor.commit();

				}
			}
		}
		
	};

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
                    Log.i("NOTICE", "The net is available!");
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
    
    class AddTagTask extends AsyncTask<Void, Void, String>{
    	
    	String tagString;
    	String[] tags;

    	public AddTagTask(String tag) {
    	 	tagString = tag;
    	 }
    	
		@Override
		protected String doInBackground(Void... params) {
			try {
				TagManager.Result result = mPushAgent.getTagManager().add(
						tagString);
				Log.e("result", result.toString());
				return result.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "Fail";
		}
		
		@Override
		protected void onPostExecute(String result) {
			
		}
    }
    
	public String loadSid() {
		SharedPreferences loadSid = CloudDoorMainActivity.this
				.getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}

    public void saveSid(String key, String value) {
        SharedPreferences savedSid = this.getSharedPreferences(
                "SAVEDSID", 0);
        Editor editor = savedSid.edit();
        editor.putString(key, value);
        editor.commit();
    }
}
