package com.icloudoor.clouddoor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.icloudoor.clouddoor.ChannelSwitchView.OnCheckedChangeListener;
import com.icloudoor.clouddoor.ShakeEventManager;
import com.icloudoor.clouddoor.UartService;
//import com.icloudoor.clouddoor.ShakeEventManager.ShakeListener;
import com.icloudoor.clouddoor.SwitchButton.OnSwitchListener;

@SuppressLint("NewApi")
public class KeyFragmentNoBLE extends Fragment {

	private String TAG = this.getClass().getSimpleName();
	
	private MyDataBaseHelper mKeyDBHelper;
	private SQLiteDatabase mKeyDB;
	private final String DATABASE_NAME = "KeyDB.db";
	private final String TABLE_NAME = "KeyInfoTable";

	private ArrayList<HashMap<String, String>> carDoorList;
	private ArrayList<HashMap<String, String>> manDoorList;

	private RelativeLayout channelSwitch;
	private RelativeLayout keyWidge;

	private TextView TvChooseCar;
	private TextView TvChooseMan;
	private TextView TvDistrictDoor;
	private TextView TvCarNumber;
	private RelativeLayout TvOpenKeyList;

	private ImageView IvChooseCar;
	private ImageView IvChooseMan;
	private RelativeLayout IvSearchKey;
//	private ImageView IvOpenDoorLogo;
//	private ImageView IvWeatherWidgePush1;
//	private ImageView IvWeatherWidgePush2;

	private int COLOR_CHANNEL_CHOOSE = 0xFF010101;
	private int COLOR_CHANNEL_NOT_CHOOSE = 0xFF999999;

	private int canDisturb;
	private int haveSound;
	private int canShake;
	private boolean isFindKey;

	private float alpha_transparent = 0.0f;
	private float alpha_opaque = 1.0f;

//	private ViewPager mWeatherWidgePager;
//	private ArrayList<Fragment> mKeyPageFragmentList;
//	private KeyPageAdapter mKeyPageAdapter;
//	private WeatherWidgeFragment mWeatherWidgeFragment;
//	private WeatherWidgeFragment2 mWeatherWidgeFragment2;
//	public FragmentManager mFragmentManager;
//	public MyPageChangeListener myPageChangeListener;
	
	// for new UI weather
	private LinearLayout weatherWidge;
	
	private WeatherClick mClick;
	
	private ImageView weatherBtnLeft;
	private ImageView weatherBtnRight;
	private TextView weatherTemp;
	private TextView weatherStatus;
	private TextView contentYi;
	private TextView contentJi;
	private int showDay;  // 0 for day one; 1 for day two; 2 for day three
	private String D1, D2, D3;
	private TextView date;

	private LocationManager locationManager;
	private double longitude = 0.0;
	private double latitude = 0.0;
	
	public final Calendar c =  Calendar.getInstance();
	
	public char centigrade = 176;
	
	private String HOST = "https://api.thinkpage.cn/v2/weather/all.json?";
	private URL weatherURL;
	private String Key = "XSI7AKYYBY";
	private RequestQueue mQueue;
	
	private String lhlHOST = "https://zone.icloudoor.com/icloudoor-web";
	private URL lhlURL;
	private int lhlCode;
	private String sid;
	
	private String day1;
	private String lastRequestLHL;
	private boolean haveRequestLHL = false;
	
	private long mLastRequestTime;
	private long mCurrentRequestTime;
	
	private ImageView keyRedDot;
//	private TextView keyRedDotNum;
	
	// for new channel switch
	private SwitchButton switchBtn;
//	private LinearLayout channelSwitchLayout;
//	private ChannelSwitchView csv;
	private int isChooseCarChannel = 1;
	private boolean onlyOneDoor = false;
	private TextView doorName;
	private TextView scanStatus;
	private ImageView BtnOpenDoor;
	private OpenDoorRingView ringView;
	
	private RelativeLayout circleLayout;
    private ImageView circle;
    private ImageView radar;

	// for BLE
//	private static final int REQUEST_ENABLE_BT = 0;
//	private static final long SCAN_PERIOD = 1500; // ms
//	private BluetoothAdapter mBluetoothAdapter;
//	private DeviceAdapter mDeviceAdapter;
//	private UartService mUartService = null;
//	private ShakeEventManager mShakeMgr;
//	private List<BluetoothDevice> mDeviceList;
//	private Map<String, Integer> mDevRssiValues;
//	private BluetoothGattCharacteristic mNotifyCharacteristic;
//
//	private int deviceIndexToOpen = 0;
//	private String NameOfDoorToOpen = null;
//	private String IdOfDoorToOpen = null;
//
//	private SoundPool mSoundPool;
//		
//	private boolean checkForOpenDoor = false;
//	
//	private String tempDeviceAddr = null;
	
	public KeyFragmentNoBLE() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.key_page, container, false);

//		mKeyDBHelper = new MyDataBaseHelper(getActivity(), DATABASE_NAME);
//		mKeyDB = mKeyDBHelper.getWritableDatabase();
//		
//		IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//		getActivity().registerReceiver(mBluetoothStateReceiver, filter);
		
//		channelSwitch = (RelativeLayout) view.findViewById(R.id.channel_switch);
//		keyWidge = (RelativeLayout) view.findViewById(R.id.key_widge);
//
//		TvChooseCar = (TextView) view.findViewById(R.id.Tv_choose_car);
//		TvChooseMan = (TextView) view.findViewById(R.id.Tv_choose_man);
//		TvDistrictDoor = (TextView) view.findViewById(R.id.district_door);
//		TvCarNumber = (TextView) view.findViewById(R.id.car_number);
//		TvDistrictDoor.setSelected(true);
//		TvCarNumber.setSelected(true);	
//		TvOpenKeyList = (RelativeLayout) view.findViewById(R.id.open_key_list);
//
//		IvChooseCar = (ImageView) view.findViewById(R.id.Iv_choose_car);
//		IvChooseMan = (ImageView) view.findViewById(R.id.Iv_choose_man);
//		IvSearchKey = (RelativeLayout) view.findViewById(R.id.Iv_search_key);
//		IvOpenDoorLogo = (ImageView) view.findViewById(R.id.Iv_open_door_logo);
//		IvWeatherWidgePush1 = (ImageView) view.findViewById(R.id.Iv_weather_widge_push1);
//		IvWeatherWidgePush2 = (ImageView) view.findViewById(R.id.Iv_weather_widge_push2);

//		mFragmentManager = getChildFragmentManager();
//		mWeatherWidgePager = (ViewPager) view.findViewById(R.id.weather_widge_pager);
//		myPageChangeListener = new MyPageChangeListener();
		
		// for new UI weather
		date = (TextView) view.findViewById(R.id.date);
		weatherWidge = (LinearLayout) view.findViewById(R.id.weather_widge);
		weatherWidge.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), WeatherDetail.class);
				startActivity(intent);
			}
			
		});
		
		weatherBtnLeft = (ImageView) view.findViewById(R.id.weather_btn_left);
		weatherBtnRight = (ImageView) view.findViewById(R.id.weather_btn_right);
		
		mClick = new WeatherClick();
		weatherBtnLeft.setOnClickListener(mClick);
		weatherBtnRight.setOnClickListener(mClick);
		showDay = 0; // defaul to show today's weather status
		
		weatherBtnLeft.setVisibility(View.INVISIBLE);
		
		
		weatherTemp = (TextView) view.findViewById(R.id.weather_temp);
		weatherStatus = (TextView) view.findViewById(R.id.weather_status);
		weatherStatus.setSelected(true);
		contentYi = (TextView) view.findViewById(R.id.weather_yi);
		contentJi = (TextView) view.findViewById(R.id.weather_ji);
		contentYi.setSelected(true);
		contentJi.setSelected(true);
		
		keyRedDot = (ImageView) view.findViewById(R.id.key_red_dot);
//		keyRedDotNum = (TextView) view.findViewById(R.id.key_red_dot_num);
		keyRedDot.setVisibility(View.INVISIBLE); 
//		keyRedDotNum.setText("");
		
		requestWeatherData();
		
		// for new channel switch
//		channelSwitchLayout = (LinearLayout) view.findViewById(R.id.channel_switch_layout);
//		csv = new ChannelSwitchView(getActivity());
//		csv.setOnCheckedChangeListener(new OnCheckedChangeListener(){
//
//			@Override
//			public void onCheckedChanged(boolean isChecked) {
//				isChooseCarChannel = getState(isChecked);
//				
//				Log.e(TAG, "channel: " + String.valueOf(isChooseCarChannel));
//				Log.e(TAG, String.valueOf(onlyOneDoor));
//								
//				if(!onlyOneDoor){
//					populateDeviceList();
//				}
//					
//				onlyOneDoor = !onlyOneDoor;
//			}
//			
//		});
		
//		channelSwitchLayout.addView(csv);
		//
		
		circleLayout = (RelativeLayout) view.findViewById(R.id.circle_layout);
		circle = (ImageView) view.findViewById(R.id.circle);
		radar = (ImageView) view.findViewById(R.id.radar_light);
		
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		LayoutParams param = (LayoutParams) circle.getLayoutParams();
		param.width = screenWidth;
		param.height = screenWidth;
		circle.setLayoutParams(param);
		radar.setLayoutParams(param);
		
		Animation animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.run);
		LinearInterpolator lin1 = new LinearInterpolator();
		animation1.setInterpolator(lin1);
		radar.startAnimation(animation1);
		
		switchBtn = (SwitchButton) view.findViewById(R.id.btn_switch);
		if(isChooseCarChannel == 1){
			switchBtn.setSwitch(false, 0);
		}else{
			switchBtn.setSwitch(true, 0);
		}
			
		switchBtn.setOnSwitchListener(new OnSwitchListener() {
			@Override
			public boolean onSwitch(SwitchButton v, boolean isRight) {	
				if(isRight)
					isChooseCarChannel = 0;
				else
					isChooseCarChannel = 1;
				return false;
			}
		});
		
		BtnOpenDoor = (ImageView) view.findViewById(R.id.btn_open_door);
//		ringView = (OpenDoorRingView) view.findViewById(R.id.open_door_halo);
//		ringView.setVisibility(View.INVISIBLE);
		
		BtnOpenDoor.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
			}
			
		});
		BtnOpenDoor.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
//					ringView.setVisibility(View.VISIBLE);
					break;
				case MotionEvent.ACTION_UP: 
//					ringView.setVisibility(View.INVISIBLE);
					break;
				}
				return false;
			}
			
		});
		
//		doorName = (TextView) view.findViewById(R.id.door_name);
		scanStatus = (TextView) view.findViewById(R.id.scan_status);
		scanStatus.setText(R.string.can_not_use_open_door);
	
//		InitFragmentViews();
//		InitViewPager();
		
//		// BLE
//		if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//			Toast.makeText(getActivity(), R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
//		}
//
//		BluetoothManager mBluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
//		mBluetoothAdapter = mBluetoothManager.getAdapter();
//
//		if (mBluetoothAdapter == null) {
//			if (getActivity() != null)
//				Toast.makeText(getActivity(), R.string.bt_not_supported, Toast.LENGTH_SHORT).show();
//		}

//		checkBlueToothState();
//		service_init();

//		mShakeMgr = new ShakeEventManager(getActivity());
//		mShakeMgr.setListener(KeyFragmentNoBLE.this);
//		mShakeMgr.init(getActivity());
//			
//		carDoorList = new ArrayList<HashMap<String, String>>();
//		manDoorList = new ArrayList<HashMap<String, String>>();
//		if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
//			if (DBCount() > 0) {
//				
//				BadgeView badge = new com.jauker.widget.BadgeView(getActivity());
//				badge.setTargetView(TvOpenKeyList);
//				badge.setBadgeGravity(Gravity.RIGHT);
//				badge.setBadgeCount((int)DBCount());//TODO
//				
//				Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME,
//						null);
//				if (mCursor.moveToFirst()) {
//					int deviceIdIndex = mCursor.getColumnIndex("deviceId");
//					int doorNamemIndex = mCursor.getColumnIndex("doorName");
//					int doorTypeIndex = mCursor.getColumnIndex("doorType");
//
//					do {
//						HashMap<String, String> temp = new HashMap<String, String>();
//						String deviceId = mCursor.getString(deviceIdIndex);
//						String doorName = mCursor.getString(doorNamemIndex);
//						String doorType = mCursor.getString(doorTypeIndex);
//
//						if (doorType.equals("2")) { 
//							temp.put("CDdeviceid", deviceId);
//							temp.put("CDdoorName", doorName);
//							carDoorList.add(temp);
//						} else if (doorType.equals("1")) { 
//							temp.put("MDdeviceid", deviceId);
//							temp.put("MDdoorName", doorName);
//							manDoorList.add(temp);
//						}
//					} while (mCursor.moveToNext());
//				}
//			}
//		}

//		channelSwitch.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (isChooseCarChannel == 1) {
//					TvChooseCar.setTextColor(COLOR_CHANNEL_NOT_CHOOSE);
//					TvChooseMan.setTextColor(COLOR_CHANNEL_CHOOSE);
//					IvChooseCar.setAlpha(alpha_transparent);
//					IvChooseMan.setAlpha(alpha_opaque);
//					isChooseCarChannel = 0;
//					populateDeviceList();
//				} else {
//					TvChooseCar.setTextColor(COLOR_CHANNEL_CHOOSE);
//					TvChooseMan.setTextColor(COLOR_CHANNEL_NOT_CHOOSE);
//					IvChooseCar.setAlpha(alpha_opaque);
//					IvChooseMan.setAlpha(alpha_transparent);
//					isChooseCarChannel = 1;
//					populateDeviceList();
//				}
//			}
//		});

//		IvOpenDoorLogo.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				doOpenDoor();
//				if (haveSound == 1) {
//					playOpenDoorSound();
//				}
//			}
//		});

//		TvOpenKeyList.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(getActivity(), KeyList.class);
//				startActivity(intent);
//			}
//		});
		return view;
	}
	
	// for new channel switch
//	private int getState(boolean state) {
//    	if(state) {
//    		return 1;
//    	} 
//    	return 0;
//    }
//	
	// for new UI weather
	private void toggleGPS() {
		Intent gpsIntent = new Intent();
		gpsIntent.setClassName("com.android.settings",
				"com.android.settings.widget.SettingsAppWidgetProvider");
		gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
		gpsIntent.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(getActivity(), 0, gpsIntent, 0).send();
		} catch (CanceledException e) {
			e.printStackTrace();
			locationManager
					.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
							1000, 0, locationListener);
			Location location1 = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location1 != null) {
				latitude = location1.getLatitude(); 
				longitude = location1.getLongitude(); 
			}
		}
	}
	
	private void getLocation() {
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		} else {

			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
		}
	}

	LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		
		@Override
		public void onProviderEnabled(String provider) {
			
		}

		
		@Override
		public void onProviderDisabled(String provider) {
			
		}

		
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				Log.e("Map",
						"Location changed : Lat: " + location.getLatitude()
								+ " Lng: " + location.getLongitude());
				latitude = location.getLatitude(); 
				longitude = location.getLongitude(); 
			}
		}
	};
	
	public boolean isBigMonth(int m) {
		if(m == 1 || m == 3 || m == 5 || m == 7 || m == 8 || m == 10 || m == 12) 
			return true;	
		return false;
	}
	
	public boolean isSmallMonth(int m) {
		if(m == 4 || m == 6 || m == 9 || m == 11) 
			return true;	
		return false;
	}
	
	public boolean isLeapYear(int y) {
		if((y%4==0 && y%100!=0) || y%400==0) 
			return true;	
		return false;
	}

	@SuppressLint("SimpleDateFormat")
	public void requestWeatherData() {
		
		final Calendar c =  Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00")); 
		
		D1 = String.valueOf(c.get(Calendar.MONTH) + 1) + getString(R.string.month)
				+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + getString(R.string.day);
		
		if(isBigMonth(c.get(Calendar.MONTH) + 1)){                 
			if(c.get(Calendar.DAY_OF_MONTH) == 31){
				if((c.get(Calendar.MONTH) + 1) == 12){   
					D2 = String.valueOf(1) + getString(R.string.month)
							+ String.valueOf(1) + getString(R.string.day);
				}else{
					D2 = String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + getString(R.string.month) 
							+ String.valueOf(1) + getString(R.string.day);
				}
			}else{
				D2 = String.valueOf(c.get(Calendar.MONTH) + 1) + getString(R.string.month) 
						+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1) + getString(R.string.day);	
			}
		}else if(isSmallMonth(c.get(Calendar.MONTH) + 1)){     
			if(c.get(Calendar.DAY_OF_MONTH) == 30){
				D2 = String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + getString(R.string.month) 
						+ String.valueOf(1) + getString(R.string.day);
			}else{
				D2 = String.valueOf(c.get(Calendar.MONTH) + 1) + getString(R.string.month) 
						+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1) + getString(R.string.day);
			}
		}else if(isLeapYear(c.get(Calendar.MONTH) + 1)) {        
			if(c.get(Calendar.DAY_OF_MONTH) == 29){
				D2 = String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + getString(R.string.month) 
						+ String.valueOf(1) + getString(R.string.day);
			}else{
				D2 = String.valueOf(c.get(Calendar.MONTH) + 1) + getString(R.string.month) 
						+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1) + getString(R.string.day);
			}
		}else if(!(isLeapYear(c.get(Calendar.MONTH) + 1))){     
			if(c.get(Calendar.DAY_OF_MONTH) == 28){
				D2 = String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + getString(R.string.month) 
						+ String.valueOf(1) + getString(R.string.day);
			}else{
				D2 = String.valueOf(c.get(Calendar.MONTH) + 1) + getString(R.string.month) 
						+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1) + getString(R.string.day);
			}
		}
		
		if(isBigMonth(c.get(Calendar.MONTH) + 1)){                 
			if(c.get(Calendar.DAY_OF_MONTH) == 31 || c.get(Calendar.DAY_OF_MONTH) == 30){
				if((c.get(Calendar.MONTH) + 1) == 12){  
					D3 = String.valueOf(1) + getString(R.string.month)
							+ String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%31) + getString(R.string.day);
				}else{
					D3 = String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + getString(R.string.month) 
							+ String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%31) + getString(R.string.day);
				}
			}else{
				D3 = String.valueOf(c.get(Calendar.MONTH) + 1) + getString(R.string.month) 
						+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+2) + getString(R.string.day);	
			}
		}else if(isSmallMonth(c.get(Calendar.MONTH) + 1)){     
			if(c.get(Calendar.DAY_OF_MONTH) == 30 || c.get(Calendar.DAY_OF_MONTH) == 29){
				D3 = String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + getString(R.string.month) 
						+ String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%30) + getString(R.string.day);
			}else{
				D3 = String.valueOf(c.get(Calendar.MONTH) + 1) + getString(R.string.month) 
						+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+2) + getString(R.string.day);
			}
		}else if(isLeapYear(c.get(Calendar.MONTH) + 1)) {        
			if(c.get(Calendar.DAY_OF_MONTH) == 29 || c.get(Calendar.DAY_OF_MONTH) == 28){
				D3 = String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + getString(R.string.month) 
						+ String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%29) + getString(R.string.day);
			}else{
				D3 = String.valueOf(c.get(Calendar.MONTH) + 1) + getString(R.string.month) 
						+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+2) + getString(R.string.day);
			}
		}else if(!(isLeapYear(c.get(Calendar.MONTH) + 1))){     
			if(c.get(Calendar.DAY_OF_MONTH) == 28 || c.get(Calendar.DAY_OF_MONTH) == 27){
				D3 = String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + getString(R.string.month) 
						+ String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%28) + getString(R.string.day);
			}else{
				D3 = String.valueOf(c.get(Calendar.MONTH) + 1) + getString(R.string.month) 
						+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+2) + getString(R.string.day);
			}
		}	
		
		Log.e(TAG, D1 + " " + D2 + " " + D3);
		
		date.setText(D1);
		
		// To get the longitude and latitude
		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			getLocation();
		} else {
			toggleGPS();
			new Handler() {
			}.postDelayed(new Runnable() {
				@Override
				public void run() {
					getLocation();
				}
			}, 2000);
		}

		// INIT
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		date = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		day1 = formatter.format(date);

		SharedPreferences saveRequestLHL = getActivity().getSharedPreferences(
				"LHLREQUESTDATE", 0);
		lastRequestLHL = saveRequestLHL.getString("LHLlastrequestdate", null);
		if (day1.equals(lastRequestLHL))
			haveRequestLHL = true;
		else
			haveRequestLHL = false;

		mQueue = Volley.newRequestQueue(getActivity());
		sid = loadSid();

		try {
			if (latitude != 0.0 || longitude != 0.0) { // can get the location
														// in time
				SharedPreferences saveLocation = getActivity()
						.getSharedPreferences("LOCATION", 0);
				Editor editor = saveLocation.edit();
				editor.putString("Latitude", String.valueOf(latitude));
				editor.putString("Longitude", String.valueOf(longitude));
				editor.commit();

				weatherURL = new URL(HOST + "city=" + String.valueOf(latitude)
						+ ":" + String.valueOf(longitude)
						+ "&language=zh-chs&unit=c&aqi=city&key=" + Key);
			} else {
				SharedPreferences loadLocation = getActivity()
						.getSharedPreferences("LOCATION", 0); // if we can't get
																// the location
																// in time, use
																// the location
																// for the last
																// usage
				latitude = Double.parseDouble(loadLocation.getString(
						"Latitude", "0.0"));
				longitude = Double.parseDouble(loadLocation.getString(
						"Longitude", "0.0"));

				if (longitude == 0.0 && latitude == 0.0) // if no location for
															// the last usage,
															// then use the ip
															// address to get
															// the weather info
															// for better user
															// experiences
					weatherURL = new URL(HOST + "city=ip"
							+ "&language=zh-chs&unit=c&aqi=city&key=" + Key);
				else 
					weatherURL = new URL(HOST + "city=" + String.valueOf(latitude)
							+ ":" + String.valueOf(longitude)
							+ "&language=zh-chs&unit=c&aqi=city&key=" + Key);
					
			}

			lhlURL = new URL(lhlHOST + "/user/data/laohuangli/get.do" + "?sid="
					+ sid);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		MyJsonObjectRequest mLhlRequest = new MyJsonObjectRequest(Method.POST,
				lhlURL.toString(), null, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.e(TAG, response.toString());
						
						try {
							if (response.getString("sid") != null) {
								sid = response.getString("sid");
								saveSid(sid);
							}
							lhlCode = response.getInt("code");
							
							if(lhlCode == 1){
								JSONArray data = response.getJSONArray("data");
								JSONObject Day1 = (JSONObject) data.get(0);
								JSONObject Day2 = (JSONObject) data.get(1);
								JSONObject Day3 = (JSONObject) data.get(2);
								
								SharedPreferences savedLHL = getActivity().getSharedPreferences("SAVEDLHL",
										0);
								Editor editor = savedLHL.edit();
								editor.putString("D1YI", Day1.getString("yi"));
								editor.putString("D1JI", Day1.getString("ji"));
								editor.putString("D1YINLI", Day1.getString("yinli"));
								editor.putString("D2YI", Day2.getString("yi"));
								editor.putString("D2JI", Day2.getString("ji"));
								editor.putString("D2YINLI", Day2.getString("yinli"));
								editor.putString("D3YI", Day3.getString("yi"));
								editor.putString("D3JI", Day3.getString("ji"));
								editor.putString("D3YINLI", Day3.getString("yinli"));
								editor.commit();
								
								contentYi.setText(Day1.getString("yi"));
								contentJi.setText(Day1.getString("ji"));
								
								SharedPreferences saveRequestLHL = getActivity().getSharedPreferences("LHLREQUESTDATE",
										0);
								Editor editor1 = saveRequestLHL.edit();
								editor1.putString("LHLlastrequestdate", day1);
								editor1.commit();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						if(getActivity() != null)
							Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("date", day1);
				map.put("days", "3");
				return map;
			}
		};
		if (!haveRequestLHL) {
			mQueue.add(mLhlRequest);
		} else {
			SharedPreferences loadLHL = getActivity().getSharedPreferences("SAVEDLHL", 0);
			contentYi.setText(loadLHL.getString("D1YI", null));
			contentJi.setText(loadLHL.getString("D1JI", null));
		}

		JsonObjectRequest mWeatherRequest = new JsonObjectRequest(Method.GET,
				weatherURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.e(TAG, response.toString());
						
						try {
							if(response.getString("status").equals("OK")){
								JSONArray weather= response.getJSONArray("weather");
								JSONObject data = (JSONObject)weather.get(0);							
								JSONObject now = data.getJSONObject("now");
								JSONArray future = data.getJSONArray("future");
								JSONObject tomorrow= (JSONObject)future.get(0);	
								JSONObject tomorrow2= (JSONObject)future.get(1);
								
								SharedPreferences savedWeather = getActivity().getSharedPreferences("SAVEDWEATHER",
										0);
								Editor editor = savedWeather.edit();
								editor.putString("City", data.getString("city_name"));
								editor.putString("Day1Temp", now.getString("temperature"));
								editor.putString("Day1Weather", now.getString("text"));
								editor.putString("Day1IconIndex", now.getString("code"));
								
								editor.putString("Day2TempLow", tomorrow.getString("low"));
								editor.putString("Day2TempHigh", tomorrow.getString("high"));
								editor.putString("Day2Weather", tomorrow.getString("text"));
								editor.putString("Day2IconIndexDay", tomorrow.getString("code1"));
								editor.putString("Day2IconIndexNight", tomorrow.getString("code2"));
								
								editor.putString("Day3TempLow", tomorrow2.getString("low"));
								editor.putString("Day3TempHigh", tomorrow2.getString("high"));
								editor.putString("Day3Weather", tomorrow2.getString("text"));
								editor.putString("Day3IconIndexDay", tomorrow2.getString("code1"));
								editor.putString("Day3IconIndexNight", tomorrow2.getString("code2"));
								
								editor.commit();
								
								weatherTemp.setText(now.getString("temperature") + String.valueOf(centigrade));
								weatherStatus.setText(now.getString("text"));
								weatherTemp.setTextSize(19);
								weatherStatus.setTextSize(13);
							}else {
								weatherTemp.setText(getString(R.string.weather_not_available));
								weatherTemp.setTextSize(16);
							}
						} catch (JSONException e) {
							e.printStackTrace();	
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						weatherTemp.setText(getString(R.string.weather_not_available));
						weatherTemp.setTextSize(16);
						
						if(getActivity() != null)
							Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
					}
				}) {
		};
		mLastRequestTime = loadLastRequestTime();
		mCurrentRequestTime = System.currentTimeMillis();
		if ((mCurrentRequestTime - mLastRequestTime) / 1000 >= 10800) {
			saveLastRequestTime(mCurrentRequestTime);
			mQueue.add(mWeatherRequest);
		} else {
			SharedPreferences loadWeather = getActivity().getSharedPreferences("SAVEDWEATHER", 0);
			
			if(loadWeather.getString("Day1Temp", "N/A").equals("N/A")){
				weatherTemp.setText(getString(R.string.weather_not_available));
				weatherTemp.setTextSize(16);
			}else{
				weatherTemp.setText(loadWeather.getString("Day1Temp", "N/A") + String.valueOf(centigrade)); //TODO
				weatherStatus.setText(loadWeather.getString("Day1Weather", "N/A"));
				
				weatherTemp.setTextSize(19);
				weatherStatus.setTextSize(13);
			}
			
		}

	}

	public class WeatherClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			SharedPreferences loadWeather = getActivity().getSharedPreferences("SAVEDWEATHER", 0);
			SharedPreferences loadLHL = getActivity().getSharedPreferences("SAVEDLHL", 0);
			
			if(v.getId() == R.id.weather_btn_left) {
				Log.e(TAG, "click left");
				if(showDay == 1) {    // now the day two weather, to show the day one weather
					showDay--;
					if(loadWeather.getString("Day1Temp", "N/A").equals("N/A")){
						weatherTemp.setText(getString(R.string.weather_not_available));
						weatherTemp.setTextSize(16);
					}else{
						weatherTemp.setText(loadWeather.getString("Day1Temp", "N/A") + String.valueOf(centigrade));
						weatherStatus.setText(loadWeather.getString("Day1Weather", "N/A"));
						
						weatherTemp.setTextSize(19);
						weatherStatus.setTextSize(13);
					}

					date.setText(D1);
					
					contentYi.setText(loadLHL.getString("D1YI", null));
					contentJi.setText(loadLHL.getString("D1JI", null));
					
					weatherBtnLeft.setVisibility(View.INVISIBLE);
					weatherBtnRight.setVisibility(View.VISIBLE);
				} else if(showDay == 2) {   // now the day three weather, to show the day two weather
					showDay--;
					if(loadWeather.getString("Day2TempHigh", "N/A").equals("N/A")){
						weatherTemp.setText(getString(R.string.weather_not_available));
						weatherTemp.setTextSize(16);
					}else{
						weatherTemp.setText(loadWeather.getString("Day2TempHigh", "N/A") + String.valueOf(centigrade));
						weatherStatus.setText(loadWeather.getString("Day2Weather", "N/A"));
						
						weatherTemp.setTextSize(19);
						weatherStatus.setTextSize(13);
					}
					
					date.setText(D2);

					contentYi.setText(loadLHL.getString("D2YI", null));
					contentJi.setText(loadLHL.getString("D2JI", null));
					
					weatherBtnLeft.setVisibility(View.VISIBLE);
					weatherBtnRight.setVisibility(View.VISIBLE);
				}
			}else if(v.getId() == R.id.weather_btn_right) {
				Log.e(TAG, "click right");
				if(showDay == 0) {    // now the day one weather, to show the day two weather
					showDay++;
					if(loadWeather.getString("Day2TempHigh", "N/A").equals("N/A")){
						weatherTemp.setText(getString(R.string.weather_not_available));
						weatherTemp.setTextSize(16);
					}else{
						weatherTemp.setText(loadWeather.getString("Day2TempHigh", "N/A") + String.valueOf(centigrade));
						weatherStatus.setText(loadWeather.getString("Day2Weather", "N/A"));
						
						weatherTemp.setTextSize(19);
						weatherStatus.setTextSize(13);
					}
					
					date.setText(D2);
	
					contentYi.setText(loadLHL.getString("D2YI", null));
					contentJi.setText(loadLHL.getString("D2JI", null));
					
					weatherBtnLeft.setVisibility(View.VISIBLE);
					weatherBtnRight.setVisibility(View.VISIBLE);
				} else if(showDay == 1) {   // now the day two weather, to show the day three weather
					showDay++;
					if(loadWeather.getString("Day3TempHigh", "N/A").equals("N/A")){
						weatherTemp.setText(getString(R.string.weather_not_available));
						weatherTemp.setTextSize(16);
					}else{
						weatherTemp.setText(loadWeather.getString("Day3TempHigh", "N/A") + String.valueOf(centigrade));
						weatherStatus.setText(loadWeather.getString("Day3Weather", "N/A"));
						
						weatherTemp.setTextSize(19);
						weatherStatus.setTextSize(13);
					}
                    
					date.setText(D3);
			
					contentYi.setText(loadLHL.getString("D3YI", null));
					contentJi.setText(loadLHL.getString("D3JI", null));
					
					weatherBtnLeft.setVisibility(View.VISIBLE);
					weatherBtnRight.setVisibility(View.INVISIBLE);
				}
			}
		}
		
	};
	
	@Override
	public void onStart() {
		super.onStart();
//		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.e("TEST", "keyFragment onResume()");
		
//		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//		if(currentapiVersion >= 18){
//			// BLE
//			if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//				Toast.makeText(getActivity(), R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
//			}
//
//			BluetoothManager mBluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
//			mBluetoothAdapter = mBluetoothManager.getAdapter();
//
//			if (mBluetoothAdapter == null) {
//				if (getActivity() != null)
//					Toast.makeText(getActivity(), R.string.bt_not_supported, Toast.LENGTH_SHORT).show();
//			}
//			checkBlueToothState();
//			service_init();
//		} else {
//			if(getActivity() != null)
//				Toast.makeText(getActivity(), R.string.low_android_version, Toast.LENGTH_SHORT).show();
//		}
//		
//		if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
//			if (DBCount() > 0) {
//				
//				Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME,
//						null);
//				if (mCursor.moveToFirst()) {
//					int deviceIdIndex = mCursor.getColumnIndex("deviceId");
//					int doorNamemIndex = mCursor.getColumnIndex("doorName");
//					int doorTypeIndex = mCursor.getColumnIndex("doorType");
//
//					do {
//						HashMap<String, String> temp = new HashMap<String, String>();
//						String deviceId = mCursor.getString(deviceIdIndex);
//						String doorName = mCursor.getString(doorNamemIndex);
//						String doorType = mCursor.getString(doorTypeIndex);
//
//						if (doorType.equals("2")) {
//							temp.put("CDdeviceid", deviceId);
//							temp.put("CDdoorName", doorName);
//							carDoorList.add(temp);
//						} else if (doorType.equals("1")) { 
//							temp.put("MDdeviceid", deviceId);
//							temp.put("MDdoorName", doorName);
//							manDoorList.add(temp);
//						}
//					} while (mCursor.moveToNext());
//				}
//			}
//		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
//		if(mBluetoothAdapter.isEnabled())
//			scanLeDevice(false);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		getActivity().unregisterReceiver(mBluetoothStateReceiver);
//
//		try {
//			LocalBroadcastManager.getInstance(getActivity())
//					.unregisterReceiver(UARTStatusChangeReceiver);
//		} catch (Exception e) {
//
//		}
//		getActivity().unbindService(mServiceConnection);
//		mUartService.stopSelf();
//		mUartService = null;
	}

//	private void service_init() {
//		Intent bindIntent = new Intent(getActivity(), UartService.class);
//		getActivity().bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
//		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
//				UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
//	}

//	private void checkBlueToothState() {
//		Log.e("BLE", "checkBlueToothState");
//		if (mBluetoothAdapter == null) {
//			if (getActivity() != null)
//				Toast.makeText(getActivity(), R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
//		} else {
//			if (mBluetoothAdapter.isEnabled()) {
//				if (mBluetoothAdapter.isDiscovering()) {
//				} else{
//					populateDeviceList();
//					// if(getActivity() != null)
//					// Toast.makeText(getActivity(), R.string.bt_enabled,
//					// Toast.LENGTH_SHORT).show();
//				}
//			} else {
//				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//			}
//		}
//	}

//	private void populateDeviceList() {
//		Log.e("BLE", "populateDeviceList");
		
//		IvOpenDoorLogo.setEnabled(false);
//		TvDistrictDoor.setText(R.string.searching_key);
//		TvDistrictDoor.setTextSize(18);
//		TvDistrictDoor.setTextColor(0xFFffffff);
//		TvCarNumber.setText(R.string.can_shake_to_open_door);
//		TvCarNumber.setTextColor(0xFFffffff);
//		IvSearchKey.setBackgroundResource(R.drawable.btn_gray);
//		IvOpenDoorLogo.setImageResource(R.drawable.btn_serch_1);
		
//		doorName.setText("");
//		
//		mDeviceList = new ArrayList<BluetoothDevice>();
//		mDeviceAdapter = new DeviceAdapter(getActivity(), mDeviceList);
//		mDevRssiValues = new HashMap<String, Integer>();
//		scanLeDevice(true);
//
//		scanStatus.setText(R.string.scanning);
//	}
//
//	private void scanLeDevice(final boolean enable) {
//		Log.e("BLE", "scanLeDevice");
//		if (enable) {
//			// Stops scanning after a pre-defined scan period.
//			new Handler().postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					mBluetoothAdapter.stopLeScan(mLeScanCallback);
////					channelSwitch.setEnabled(true);
//					
//					Log.e(TAG, "mDeviceList.size() =" + String.valueOf(mDeviceList.size()));
//					
//					if(mDeviceList.size() != 0) scanStatus.setText(R.string.can_shake_to_open_door);
//					
//					// add for the case of only one door -- START
//					if (mDeviceList != null && mDeviceList.size() == 1) {
//						onlyOneDoor = true;
//						for (int i = 0; i < carDoorList.size(); i++) {
//							String tempDID = carDoorList.get(i).get("CDdeviceid");
//							tempDID = tempDID.toUpperCase();
//							char[] data = tempDID.toCharArray();
//							String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
//									+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
//									+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
//									+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
//									+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
//									+ String.valueOf(data[10]) + String.valueOf(data[11]);
//							Log.e("TEST", "CDdeviceID:" + formatDeviceId);
//
//							if (mDeviceList.get(0).getAddress().equals(formatDeviceId)) {
////								IvOpenDoorLogo.setEnabled(true);
////								IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
////								IvSearchKey.setBackgroundResource(R.drawable.btn_background_blue);
////								TvDistrictDoor.setText(carDoorList.get(i).get("CDdoorName"));
////								TvDistrictDoor.setTextSize(18);
////								TvDistrictDoor.setTextColor(0xFFffffff);
////								TvCarNumber.setText(carDoorList.get(i).get("CDdeviceid"));
////								TvCarNumber.setTextColor(0xFFffffff);
//								
//								doorName.setText(carDoorList.get(i).get("CDdoorName"));
//								
//								csv.changeChecked(true);
////								onlyOneDoor = false;
//								
//								isChooseCarChannel = 1;
//								
//								break;
//							}
//						}
//						
//						for (int i = 0; i < manDoorList.size(); i++) {
//							String tempDID = manDoorList.get(i).get("MDdeviceid");
//							tempDID = tempDID.toUpperCase();
//							char[] data = tempDID.toCharArray();
//							String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
//									+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
//									+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
//									+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
//									+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
//									+ String.valueOf(data[10]) + String.valueOf(data[11]);
//							Log.e("TEST", "MDdeviceID:" + formatDeviceId);
//
//							if (mDeviceList.get(0).getAddress().equals(formatDeviceId)) {
////								IvOpenDoorLogo.setEnabled(true);
////								IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
////								IvSearchKey.setBackgroundResource(R.drawable.btn_background_blue);
////								TvDistrictDoor.setText(manDoorList.get(i).get("MDdoorName"));
////								TvDistrictDoor.setTextSize(18);
////								TvDistrictDoor.setTextColor(0xFFffffff);
////								TvCarNumber.setText(manDoorList.get(i).get("MDdeviceid"));
////								TvCarNumber.setTextColor(0xFFffffff);
//								
//								doorName.setText(manDoorList.get(i).get("MDdoorName"));
//
//								csv.changeChecked(false);
////								onlyOneDoor = false;
//								
//								isChooseCarChannel = 0;
//								
//								break;
//							}
//						}
//					}
//					// add for the case of only one door -- END		
//					
//					if (mDeviceList != null && mDeviceList.size() > 1) {
//
//						int maxRssiIndex = 0;
//						int maxRssi = -128;
//
//						for (int i = 0; i < mDeviceList.size(); i++) {
//							Log.e("TEST", "checking rssi");
//							String tempAdd = mDeviceList.get(i).getAddress();
//							int tempRssi = mDevRssiValues.get(tempAdd);
//							if (tempRssi > maxRssi) {
//								maxRssi = tempRssi;
//								maxRssiIndex = i;
//							}
//						}
//						deviceIndexToOpen = maxRssiIndex;
//					}
//					
//					if (mDeviceList != null && mDeviceList.size() > 1) {
//						onlyOneDoor = false;
//						if (isChooseCarChannel == 1) {
//							for (int i = 0; i < carDoorList.size(); i++) {
//								String tempDID = carDoorList.get(i).get("CDdeviceid");
//								tempDID = tempDID.toUpperCase();
//								char[] data = tempDID.toCharArray();
//								String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
//										+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
//										+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
//										+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
//										+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
//										+ String.valueOf(data[10]) + String.valueOf(data[11]);
//								Log.e("TEST", "CDdeviceID:" + formatDeviceId);
//
//								if (mDeviceList.get(deviceIndexToOpen).getAddress().equals(formatDeviceId)) {
////									IvOpenDoorLogo.setEnabled(true);
////									IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
////									IvSearchKey.setBackgroundResource(R.drawable.btn_background_blue);
////									TvDistrictDoor.setText(carDoorList.get(i).get("CDdoorName"));
////									TvDistrictDoor.setTextSize(18);
////									TvDistrictDoor.setTextColor(0xFFffffff);
////									TvCarNumber.setText(carDoorList.get(i).get("CDdeviceid"));
////									TvCarNumber.setTextColor(0xFFffffff);
//									
//									doorName.setText(carDoorList.get(i).get("CDdoorName"));
//								}
//							}
//						} else {
//							for (int i = 0; i < manDoorList.size(); i++) {
//								String tempDID = manDoorList.get(i).get("MDdeviceid");
//								tempDID = tempDID.toUpperCase();
//								char[] data = tempDID.toCharArray();
//								String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
//										+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
//										+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
//										+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
//										+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
//										+ String.valueOf(data[10]) + String.valueOf(data[11]);
//								Log.e("TEST", "MDdeviceID:" + formatDeviceId);
//
//								if (mDeviceList.get(deviceIndexToOpen).getAddress().equals(formatDeviceId)) {
////									IvOpenDoorLogo.setEnabled(true);
////									IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
////									IvSearchKey.setBackgroundResource(R.drawable.btn_background_blue);
////									TvDistrictDoor.setText(manDoorList.get(i).get("MDdoorName"));
////									TvDistrictDoor.setTextSize(18);
////									TvDistrictDoor.setTextColor(0xFFffffff);
////									TvCarNumber.setText(manDoorList.get(i).get("MDdeviceid"));
////									TvCarNumber.setTextColor(0xFFffffff);
//									
//									doorName.setText(manDoorList.get(i).get("MDdoorName"));
//								}
//							}
//						}
//					}
//				}
//			}, SCAN_PERIOD);
//			mBluetoothAdapter.startLeScan(mLeScanCallback);
////			channelSwitch.setEnabled(false);
////			if (getActivity() != null)
////				Toast.makeText(getActivity(), R.string.scanning, 3000).show();
//		} else {
//			mBluetoothAdapter.stopLeScan(mLeScanCallback);
////			channelSwitch.setEnabled(true);
//			
//			if(mDeviceList.size() != 0) scanStatus.setText(R.string.can_shake_to_open_door);
//			
//			// add for the case of only one door -- START
//			if (mDeviceList != null && mDeviceList.size() == 1) {
//				onlyOneDoor = true;
//				for (int i = 0; i < carDoorList.size(); i++) {
//					String tempDID = carDoorList.get(i).get("CDdeviceid");
//					tempDID = tempDID.toUpperCase();
//					char[] data = tempDID.toCharArray();
//					String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
//							+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
//							+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
//							+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
//							+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
//							+ String.valueOf(data[10]) + String.valueOf(data[11]);
//					Log.e("TEST", "CDdeviceID:" + formatDeviceId);
//
//					if (mDeviceList.get(0).getAddress().equals(formatDeviceId)) {
////						IvOpenDoorLogo.setEnabled(true);
////						IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
////						IvSearchKey.setBackgroundResource(R.drawable.btn_background_blue);
////						TvDistrictDoor.setText(carDoorList.get(i).get("CDdoorName"));
////						TvDistrictDoor.setTextSize(18);
////						TvDistrictDoor.setTextColor(0xFFffffff);
////						TvCarNumber.setText(carDoorList.get(i).get("CDdeviceid"));
////						TvCarNumber.setTextColor(0xFFffffff);
//						
//						doorName.setText(carDoorList.get(i).get("CDdoorName"));
//						
//						csv.changeChecked(true);
////						onlyOneDoor = false;
//
//						isChooseCarChannel = 1;
//						
//						break;
//					}
//				}
//				
//				for (int i = 0; i < manDoorList.size(); i++) {
//					String tempDID = manDoorList.get(i).get("MDdeviceid");
//					tempDID = tempDID.toUpperCase();
//					char[] data = tempDID.toCharArray();
//					String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
//							+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
//							+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
//							+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
//							+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
//							+ String.valueOf(data[10]) + String.valueOf(data[11]);
//					Log.e("TEST", "MDdeviceID:" + formatDeviceId);
//
//					if (mDeviceList.get(0).getAddress().equals(formatDeviceId)) {
////						IvOpenDoorLogo.setEnabled(true);
////						IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
////						IvSearchKey.setBackgroundResource(R.drawable.btn_background_blue);
////						TvDistrictDoor.setText(manDoorList.get(i).get("MDdoorName"));
////						TvDistrictDoor.setTextSize(18);
////						TvDistrictDoor.setTextColor(0xFFffffff);
////						TvCarNumber.setText(manDoorList.get(i).get("MDdeviceid"));
////						TvCarNumber.setTextColor(0xFFffffff);
//						
//						doorName.setText(manDoorList.get(i).get("MDdoorName"));
//
//						csv.changeChecked(false);
////						onlyOneDoor = false;
//						
//						isChooseCarChannel = 0;
//						
//						break;
//					}
//				}
//			}
//			// add for the case of only one door -- END	
//			
//			if (mDeviceList != null && mDeviceList.size() > 1) {
//				onlyOneDoor = false;
//				if (isChooseCarChannel == 1) {
//					for (int i = 0; i < carDoorList.size(); i++) {
//						String tempDID = carDoorList.get(i).get("CDdeviceid");
//						tempDID = tempDID.toUpperCase();
//						char[] data = tempDID.toCharArray();
//						String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
//								+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
//								+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
//								+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
//								+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
//								+ String.valueOf(data[10]) + String.valueOf(data[11]);
//						Log.e("TEST", "CDdeviceID:" + formatDeviceId);
//
//						if (mDeviceList.get(deviceIndexToOpen).getAddress().equals(formatDeviceId)) {
////							IvOpenDoorLogo.setEnabled(true);
////							IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
////							IvSearchKey.setBackgroundResource(R.drawable.btn_background_blue);
////							TvDistrictDoor.setText(carDoorList.get(i).get("CDdoorName"));
////							TvDistrictDoor.setTextSize(18);
////							TvDistrictDoor.setTextColor(0xFFffffff);
////							TvCarNumber.setText(carDoorList.get(i).get("CDdeviceid"));
////							TvCarNumber.setTextColor(0xFFffffff);
//							
//							doorName.setText(carDoorList.get(i).get("CDdoorName"));
//						}
//					}
//				} else {
//					for (int i = 0; i < manDoorList.size(); i++) {
//						String tempDID = manDoorList.get(i).get("MDdeviceid");
//						tempDID = tempDID.toUpperCase();
//						char[] data = tempDID.toCharArray();
//						String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
//								+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
//								+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
//								+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
//								+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
//								+ String.valueOf(data[10]) + String.valueOf(data[11]);
//						Log.e("TEST", "MDdeviceID:" + formatDeviceId);
//
//						if (mDeviceList.get(deviceIndexToOpen).getAddress().equals(formatDeviceId)) {
////							IvOpenDoorLogo.setEnabled(true);
////							IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
////							IvSearchKey.setBackgroundResource(R.drawable.btn_background_blue);
////							TvDistrictDoor.setText(manDoorList.get(i).get("MDdoorName"));
////							TvDistrictDoor.setTextSize(18);
////							TvDistrictDoor.setTextColor(0xFFffffff);
////							TvCarNumber.setText(manDoorList.get(i).get("MDdeviceid"));
////							TvCarNumber.setTextColor(0xFFffffff);
//							
//							doorName.setText(manDoorList.get(i).get("MDdoorName"));
//						}
//					}
//				}
//			}
//		}
//	}
//
//	private void addDevice(BluetoothDevice device, int rssi) {
//		Log.e("BLE", "addDevice");
//		boolean deviceFound = false;
//		
//
//		for (BluetoothDevice listDev : mDeviceList) {
//			if (listDev.getAddress().equals(device.getAddress())) {
//				deviceFound = true;
//				break;
//			}
//		}
//		
//		if(tempDeviceAddr != device.getAddress()){
//			tempDeviceAddr = device.getAddress();
//			
////			if (isChooseCarChannel == 1) {
//				for (int i = 0; i < carDoorList.size(); i++) {
//					String tempDID = carDoorList.get(i).get("CDdeviceid");
//					tempDID = tempDID.toUpperCase();
//					char[] data = tempDID.toCharArray();
//					String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
//							+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":" 
//							+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
//							+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":" 
//							+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
//							+ String.valueOf(data[10]) + String.valueOf(data[11]);
//					Log.e("TEST", "CDdeviceID:" + formatDeviceId);
//
//					if (device.getAddress().equals(formatDeviceId)) {
//						mDevRssiValues.put(device.getAddress(), rssi);
//						if (!deviceFound) {
//							Log.e("TEST", "add a car door");
//							mDeviceList.add(device);
//							mDeviceAdapter.notifyDataSetChanged();
//							
//							break;
//						}
//					}
//				}
////			} else {
//				for (int i = 0; i < manDoorList.size(); i++) {
//					String tempDID = manDoorList.get(i).get("MDdeviceid");
//					tempDID = tempDID.toUpperCase();
//					char[] data = tempDID.toCharArray();
//					String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
//							+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
//							+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
//							+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
//							+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
//							+ String.valueOf(data[10]) + String.valueOf(data[11]);
//					Log.e("TEST", "MDdeviceID:" + formatDeviceId);
//
//					if (device.getAddress().equals(formatDeviceId)) {
//						mDevRssiValues.put(device.getAddress(), rssi);
//						if (!deviceFound) {
//							Log.e("TEST", "add a man door");
//							mDeviceList.add(device);
//							mDeviceAdapter.notifyDataSetChanged();
//							
//							break;
//						}
//					}
//				}
////			}
//		}
//	}
//	
//	private void doOpenDoor() {
//		Log.e("BLE", "doOpenDoor");
////		IvOpenDoorLogo.setEnabled(false);
//		// if(getActivity() != null)
//		// Toast.makeText(getActivity(), R.string.door_open,
//		// Toast.LENGTH_SHORT).show();
//		
//		onlyOneDoor = !onlyOneDoor;
//		
//		if (mDeviceList != null && mDeviceList.size() > 0) {
//			if (mDeviceList.get(deviceIndexToOpen).getAddress() != null) {
//				mBluetoothAdapter.stopLeScan(mLeScanCallback);
//				mUartService.connect(mDeviceList.get(deviceIndexToOpen)
//						.getAddress());
////				populateDeviceList();				
//				new Handler().postDelayed(new Runnable() {
//	                @Override
//	                public void run() {
//	                    if(!checkForOpenDoor) {
//	                    	Toast.makeText(getActivity(), R.string.open_door_fail, Toast.LENGTH_LONG).show();
//	                    	Log.e("test for open door", "fail");
//	                    }else{
//	                    	checkForOpenDoor = true;
//	                    }
//	                }
//	            }, 3000);
//				
//				new Handler().postDelayed(new Runnable() {
//	                @Override
//	                public void run() {
//	                	populateDeviceList();
//	                }
//	            }, 10000);
//			}
//		}
//	}
//
//	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
//
//		@Override
//		public void onLeScan(final BluetoothDevice device, final int rssi,
//				byte[] scanRecord) {
//			Log.e("BLE", "onLeScan");
//			getActivity().runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					if (getActivity() != null) {
//						getActivity().runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								addDevice(device, rssi);
//							}
//						});
//					}
//
//				}
//			});
//		}
//	};

//	public void InitFragmentViews() {
//		isFindKey = false;
//
//		SharedPreferences setting = getActivity().getSharedPreferences(
//				"SETTING", 0);
//		isChooseCarChannel = setting.getInt("chooseCar", 1);
//		canDisturb = setting.getInt("disturb", 1);
//		haveSound = setting.getInt("sound", 1);
//		canShake = setting.getInt("shake", 0);

//		if (isChooseCarChannel == 1) {
//			TvChooseCar.setTextColor(COLOR_CHANNEL_CHOOSE);
//			TvChooseMan.setTextColor(COLOR_CHANNEL_NOT_CHOOSE);
//			IvChooseCar.setAlpha(alpha_opaque);
//			IvChooseMan.setAlpha(alpha_transparent);
//		} else {
//			TvChooseCar.setTextColor(COLOR_CHANNEL_NOT_CHOOSE);
//			TvChooseMan.setTextColor(COLOR_CHANNEL_CHOOSE);
//			IvChooseCar.setAlpha(alpha_transparent);
//			IvChooseMan.setAlpha(alpha_opaque);
//		}

//		TvDistrictDoor.setText(R.string.searching_key);
//		TvDistrictDoor.setTextSize(18);
//		TvDistrictDoor.setTextColor(0xFFffffff);
//		TvCarNumber.setText(R.string.can_shake_to_open_door);
//		TvCarNumber.setTextColor(0xFFffffff);
//
//		IvSearchKey.setBackgroundResource(R.drawable.btn_gray);
//		IvOpenDoorLogo.setImageResource(R.drawable.btn_serch_1);
//		IvOpenDoorLogo.setEnabled(false);

//		IvWeatherWidgePush1.setImageResource(R.drawable.push_current);
//		IvWeatherWidgePush2.setImageResource(R.drawable.push_next);
//	}

//	public void InitViewPager() {
//		mKeyPageFragmentList = new ArrayList<Fragment>();
//
//		mWeatherWidgeFragment = new WeatherWidgeFragment();
//		mWeatherWidgeFragment2 = new WeatherWidgeFragment2();
//
//		mKeyPageFragmentList.add(mWeatherWidgeFragment);
//		mKeyPageFragmentList.add(mWeatherWidgeFragment2);
//
//		mKeyPageAdapter = new KeyPageAdapter(mFragmentManager,
//				mKeyPageFragmentList);
//		mWeatherWidgePager.setAdapter(mKeyPageAdapter);
//		mWeatherWidgePager.setCurrentItem(0);
//		mWeatherWidgePager.setOnPageChangeListener(myPageChangeListener);
//	}

//	public class MyPageChangeListener implements OnPageChangeListener {
//
//		@Override
//		public void onPageScrollStateChanged(int arg0) {
//			if (arg0 == 2) {
//				int index = mWeatherWidgePager.getCurrentItem();
//				mWeatherWidgePager.setCurrentItem(index);
//				if (index == 0) {
//					IvWeatherWidgePush1
//							.setImageResource(R.drawable.push_current);
//					IvWeatherWidgePush2.setImageResource(R.drawable.push_next);
//				} else if (index == 1) {
//					IvWeatherWidgePush2
//							.setImageResource(R.drawable.push_current);
//					IvWeatherWidgePush1.setImageResource(R.drawable.push_next);
//				}
//			}
//		}
//
//		@Override
//		public void onPageScrolled(int arg0, float arg1, int arg2) {
//
//		}
//
//		@Override
//		public void onPageSelected(int arg0) {
//
//		}
//
//	}

//	private final BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			Log.e("BLE", "mBluetoothStateReceiver");
//			final String action = intent.getAction();
//			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
//				final int state = intent.getIntExtra(
//						BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
//
//				switch (state) {
//				case BluetoothAdapter.STATE_OFF:
//					Log.e("BLE", "BluetoothAdapter.STATE_OFF");
//					break;
//
//				case BluetoothAdapter.STATE_TURNING_OFF:
//					Log.e("BLE", "BluetoothAdapter.STATE_TURNING_OFF");
//					break;
//
//				case BluetoothAdapter.STATE_ON:
//					Log.e("BLE", "BluetoothAdapter.STATE_ON");
//					populateDeviceList();
//					break;
//
//				case BluetoothAdapter.STATE_TURNING_ON:
//					Log.e("BLE", "BluetoothAdapter.STATE_TURNING_ON");
//					break;
//				}
//			}
//		}
//	};
//
//	private static IntentFilter makeGattUpdateIntentFilter() {
//		Log.e("BLE", "makeGattUpdateIntentFilter");
//		final IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
//		intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
//		intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
//		intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
//		intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
//		intentFilter.addAction(UartService.ACTION_MAKESURE_DOOROPENED);
//		return intentFilter;
//	}
	
//	private Handler handler = new Handler();
//
//	private Runnable task = new Runnable() {
//		public void run() {
//			// TODOAuto-generated method stub
//			handler.postDelayed(this, 2 * 100);// set the delay time, ms
//			// do something here!!
//			if (mUartService != null) {
//                mUartService.readRXCharacteristic(mUartService.SIMPLEPROFILE_CHAR2_UUID);
//            }
//			Log.e("TEst for response", "print");
//		}
//	};
	
//	private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {
//
//		public void onReceive(Context context, Intent intent) {
//			Log.e("BLE", "UARTStatusChangeReceiver");
//			String action = intent.getAction();
//
//			if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
//				Log.e("BLE", "UartService.ACTION_GATT_CONNECTED");
//				getActivity().runOnUiThread(new Runnable() {
//					public void run() {
//					}
//				});
//			}
//
//			if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
//				Log.e("BLE", "UartService.ACTION_GATT_SERVICES_DISCOVERED");
//				getActivity().runOnUiThread(new Runnable() {
//					public void run() {
//						if (mUartService != null) {
//							mUartService.readRXCharacteristic(mUartService.RX_CHAR_UUID);
//						}
//					}
//				});
//			}
//
//			if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
//				Log.e("BLE", "UartService.ACTION_GATT_DISCONNECTED");
//				getActivity().runOnUiThread(new Runnable() {
//					public void run() {
////						IvOpenDoorLogo.setEnabled(true);
//						// mUartService.disconnect();
//						mUartService.close();
//					}
//				});
//			}
//
//			if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
//				Log.e("BLE", "UartService.ACTION_GATT_SERVICES_DISCOVERED");
//				mUartService.enableTXNotification();
//			}
//
//			if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
//				Log.e("BLE", "UartService.ACTION_DATA_AVAILABLE");
//
//				@SuppressWarnings("unused")
//				final byte[] txValue = intent
//						.getByteArrayExtra(UartService.EXTRA_DATA);
//				getActivity().runOnUiThread(new Runnable() {
//					public void run() {
//						if (mUartService != null) {
//							String message = new String(
//									Character.toChars(new Random()
//											.nextInt(90 - 65) + 65));
//							try {
//								byte[] value = message.getBytes("UTF-8");
//								mUartService.writeRXCharacteristic(value);
//							} catch (Exception e) {
//							}
//						}
//					}
//				});
//			}
//
//			if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
//				Log.e("BLE", "UartService.DEVICE_DOES_NOT_SUPPORT_UART");
//				mUartService.disconnect();
//			}
//			
//			//new add for response
//			if (action.equals(UartService.ACTION_MAKESURE_DOOROPENED)) {
//                Log.e("BLE", "UartService.ACTION_MAKESURE_DOOROPENED");
//                final byte[] txValue = intent
//                        .getByteArrayExtra(UartService.EXTRA_DATA);
////                getActivity().runOnUiThread(new Runnable() {
////					public void run() {
//						if (txValue[0] == 0x10) {
//		                    // door had opened. go on ...
//							
//							checkForOpenDoor = true;
//							Toast.makeText(getActivity(), R.string.open_door_success, Toast.LENGTH_LONG).show();
//		                    Log.e("BLE", "door had opened.");
//		                }
////					}
////                });
//            }
//
//		}
//	};
//	
//	
//
//	private ServiceConnection mServiceConnection = new ServiceConnection() {
//		public void onServiceConnected(ComponentName className,
//				IBinder rawBinder) {
//			Log.e("BLE", "onServiceConnected");
//			mUartService = ((UartService.LocalBinder) rawBinder).getService();
//			if (!mUartService.initialize()) {
//				getActivity().finish();
//			}
//
//		}
//
//		public void onServiceDisconnected(ComponentName classname) {
//			Log.e("BLE", "onServiceDisconnected");
//			mUartService = null;
//
//		}
//	};
//
//	private class DeviceAdapter extends BaseAdapter {
//		private Context context;
//		private List<BluetoothDevice> devices;
//
//		public DeviceAdapter(Context context, List<BluetoothDevice> devices) {
//			this.context = context;
//			this.devices = devices;
//		}
//
//		@Override
//		public int getCount() {
//			int ret = 0;
//			if (devices != null && devices.size() > 0) {
//				ret = devices.size();
//			}
//			return ret;
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return devices.get(position);
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			return null;
//		}
//
//	}
//
//	@Override
//	public void onShake() {
//		if (canShake == 1) {
//			Log.e("TEST", "shaking");
//			doOpenDoor();
//		}
//	}

//	public void playOpenDoorSound() {
//		mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
//		mSoundPool.play(mSoundPool.load(getActivity(), R.raw.ring, 0), 1, 1, 0,
//				0, 1);
//	}
//
//	private long DBCount() {
//		String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
//		SQLiteStatement statement = mKeyDB.compileStatement(sql);
//		long count = statement.simpleQueryForLong();
//		return count;
//	}
	
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
	
	public void saveLastRequestTime(long time) {
		SharedPreferences savedTime = getActivity().getSharedPreferences("SAVEDTIME",
				0);
		Editor editor = savedTime.edit();
		editor.putLong("TIME", time);
		editor.commit();
	}

	public long loadLastRequestTime() {
		SharedPreferences loadTime = getActivity().getSharedPreferences("SAVEDTIME", 0);
		return loadTime.getLong("TIME", 0);
	}
	
	public void saveSid(String sid) {
		SharedPreferences savedSid = getActivity().getSharedPreferences("SAVEDSID", 0);
		Editor editor = savedSid.edit();
		editor.putString("SID", sid);
		editor.commit();
	}

	public String loadSid() {
		SharedPreferences loadSid = getActivity().getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}

//	@Override
//	public void onShake() {
//		// TODO Auto-generated method stub
//		
//	}
}
