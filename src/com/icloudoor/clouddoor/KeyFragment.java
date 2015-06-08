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
import java.util.UUID;

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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import android.os.Message;
import android.os.Vibrator;
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
import com.icloudoor.clouddoor.SwitchButton.OnSwitchListener;
import com.icloudoor.clouddoor.UartService;
import com.icloudoor.clouddoor.ShakeEventManager.OnShakeListener;
//import com.icloudoor.clouddoor.animationUtils.MyAnimationLine;
//import com.icloudoor.clouddoor.animationUtils.MyAnimationView;

@SuppressLint("NewApi")
public class KeyFragment extends Fragment {

	private String TAG = this.getClass().getSimpleName();
	
	private MyDataBaseHelper mKeyDBHelper;
	private SQLiteDatabase mKeyDB;
	private final String DATABASE_NAME = "KeyDB.db";
	private final String TABLE_NAME = "KeyInfoTable";
	private final String CAR_TABLE_NAME = "CarKeyTable";
	private final String ZONE_TABLE_NAME = "ZoneTable";

	private ArrayList<HashMap<String, String>> carDoorList;
	private ArrayList<HashMap<String, String>> manDoorList;

	private RelativeLayout channelSwitch;
	private RelativeLayout keyWidge;

	private TextView TvChooseCar;
	private TextView TvChooseMan;
	private TextView TvDistrictDoor;
	private TextView TvCarNumber;
	private RelativeLayout RlOpenKeyList;

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
    private Vibrator vibrator;

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
	
	private WeatherClick mWeatherClick;
	
	private ImageView weatherBtnLeft;
	private ImageView weatherBtnRight;
	private TextView weatherTemperature;
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
    private boolean mBTScanning = false;
    private boolean mBtStateOpen = false;
	
	private long mLastRequestTime;
	private long mCurrentRequestTime;
	
	private ImageView keyRedDot;
//	private TextView keyRedDotNum;
	private int newNum;
	private String uuid;
	private URL downLoadKeyURL;
	
	// for new channel switch
	private SwitchButton switchBtn;
//	private LinearLayout channelSwitchLayout;
//	private ChannelSwitchView csv;
	private int isChooseCarChannel;   // 1 for car; 2 for man
    private int mOpenDoorState;
	private boolean onlyOneDoor = false;
	private StrokeTextView doorName;
    private ImageView doorNameFlag;
	private TextView scanStatus;
	private ImageView BtnOpenDoor;
//	private OpenDoorRingView ringView;
//	private ImageView halo;
//	private MyAnimationLine myline;
//    private MyAnimationView myAnimationView;
    private Animation animation1;

	// for BLE
	private static final int REQUEST_ENABLE_BT = 0;
	private static final long SCAN_PERIOD = 1000; // ms
	private BluetoothAdapter mBluetoothAdapter;
	private DeviceAdapter mDeviceAdapter;
	private UartService mUartService = null;
	private ShakeEventManager mShakeMgr;
	private List<BluetoothDevice> mDeviceList;
	private List<BluetoothDevice> tempCarDoorList = new ArrayList<BluetoothDevice>();
	private List<BluetoothDevice> tempManDoorList = new ArrayList<BluetoothDevice>();
	private Map<String, Integer> mDevRssiValues;
	private BluetoothGattCharacteristic mNotifyCharacteristic;

	private int deviceIndexToOpen = 0;
	private String NameOfDoorToOpen = null;
	private String IdOfDoorToOpen = null;

	private SoundPool mSoundPool;
		
	private boolean mDoorState = false; // open is true, close is false

	private String tempDeviceAddr = null;
    private Handler mHandler;
    private MyThread  myThread = null;
    
    private RelativeLayout circleLayout;
    private ImageView circle;
    private ImageView radar;
    
    private SharedPreferences carNumAndPhoneNumShare;
    
    /*
     * new shake
     */
//    private SensorManager sensorManager;
//    private static final int SENSOR_SHAKE = 10;
    
    
	public KeyFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.key_page, container, false);
        vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        carNumAndPhoneNumShare = getActivity().getSharedPreferences("carNumAndPhoneNum", 0);
        
		mKeyDBHelper = new MyDataBaseHelper(getActivity(), DATABASE_NAME);
		mKeyDB = mKeyDBHelper.getWritableDatabase();
		
		IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		getActivity().registerReceiver(mBluetoothStateReceiver, filter);
		
//		channelSwitch = (RelativeLayout) view.findViewById(R.id.channel_switch);
//		keyWidge = (RelativeLayout) view.findViewById(R.id.key_widge);
//
//		TvChooseCar = (TextView) view.findViewById(R.id.Tv_choose_car);
//		TvChooseMan = (TextView) view.findViewById(R.id.Tv_choose_man);
//		TvDistrictDoor = (TextView) view.findViewById(R.id.district_door);
//		TvCarNumber = (TextView) view.findViewById(R.id.car_number);
//		TvDistrictDoor.setSelected(true);
//		TvCarNumber.setSelected(true);	
		RlOpenKeyList = (RelativeLayout) view.findViewById(R.id.open_key_list);
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
		
		InitFragmentViews();
		
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

        mWeatherClick = new WeatherClick();
		weatherBtnLeft.setOnClickListener(mWeatherClick);
		weatherBtnRight.setOnClickListener(mWeatherClick);
		showDay = 0; // defaul to show today's weather status
		
		weatherBtnLeft.setVisibility(View.INVISIBLE);


        weatherTemperature = (TextView) view.findViewById(R.id.weather_temp);
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
		
		mQueue = Volley.newRequestQueue(getActivity());
		sid = loadSid();
		
		newNum = 0;
		checkForNewKey();		
		
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
//				if(!mBTScanning){
//					populateDeviceList(mBtStateOpen);
//                    Log.e(TAG, "start scanning");
//				}
//					
//				//onlyOneDoor = !onlyOneDoor;
//			}
//			
//		});
//		
//		channelSwitchLayout.addView(csv);
		//
		switchBtn = (SwitchButton) view.findViewById(R.id.btn_switch);
		if(isChooseCarChannel == 1){
			switchBtn.setSwitch(false, 0);
			Log.e(TAG, String.valueOf(isChooseCarChannel));
		}else{
			switchBtn.setSwitch(true, 0);
			Log.e(TAG, String.valueOf(isChooseCarChannel));
		}
			
		switchBtn.setOnSwitchListener(new OnSwitchListener() {
			@Override
			public boolean onSwitch(SwitchButton v, boolean isRight) {
				if (isRight) {
					isChooseCarChannel = 0;
					if (!mBTScanning) {
						populateDeviceList(mBtStateOpen);
						Log.e(TAG, "start scanning");
					}
					Log.e(TAG, String.valueOf(isChooseCarChannel));
				} else {
					isChooseCarChannel = 1;
					if (!mBTScanning) {
						populateDeviceList(mBtStateOpen);
						Log.e(TAG, "start scanning");
					}
					Log.e(TAG, String.valueOf(isChooseCarChannel));
				}
				return false;
			}
		});
		
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
		
		
		
//		halo = (ImageView) view.findViewById(R.id.halo);
//		Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.tip);
//		LinearInterpolator lin = new LinearInterpolator();
//		animation.setInterpolator(lin);
//		halo.startAnimation(animation);
		
//		myline=(MyAnimationLine) view.findViewById(R.id.myAnimationLine);
//        myAnimationView = (MyAnimationView)view.findViewById(R.id.myAnimationView);
		animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.run);
		LinearInterpolator lin1 = new LinearInterpolator();
		animation1.setInterpolator(lin1);
//		myline.startAnimation(animation1);
//		radar.startAnimation(animation1);
		
		BtnOpenDoor = (ImageView) view.findViewById(R.id.btn_open_door);
		BtnOpenDoor.setImageResource(R.drawable.door_normalll_new);
		BtnOpenDoor.setEnabled(false);
//		ringView = (OpenDoorRingView) view.findViewById(R.id.open_door_halo);
//		ringView.setVisibility(View.INVISIBLE);
		
		BtnOpenDoor.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				if(mBtStateOpen == false){
					Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
				}else {
					 if (mOpenDoorState == 0) {
		                    mOpenDoorState = 1; // doing opendoor
		                    Log.i("test", "doOpenDoor");
		                    
		                    if(haveSound == 1){
		                    	playOpenDoorSound();
		                    }

		                    doOpenDoor(mBtStateOpen); //ONLY FOR TEST
		                }
				}   
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
		
		doorName = (StrokeTextView) view.findViewById(R.id.door_name);
        doorNameFlag = (ImageView) view.findViewById(R.id.door_name_flag);
		scanStatus = (TextView) view.findViewById(R.id.scan_status);
		scanStatus.setText(R.string.can_shake_to_open_door);
	
		
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

		mShakeMgr = new ShakeEventManager(getActivity());
		mShakeMgr.setOnShakeListener(new OnShakeListener() {
			@Override
			public void onShake() {
				if(canShake == 1){
					if(mBtStateOpen == false){
						Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
					}else {
						if (mOpenDoorState == 0) {
							mOpenDoorState = 1;
							Log.i("test", "doOpenDoor");

							doOpenDoor(mBtStateOpen);
						}
					}
				}
			}
		});

		carDoorList = new ArrayList<HashMap<String, String>>();
		manDoorList = new ArrayList<HashMap<String, String>>();
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
//					populateDeviceList(mBtStateOpen);
//				} else {
//					TvChooseCar.setTextColor(COLOR_CHANNEL_CHOOSE);
//					TvChooseMan.setTextColor(COLOR_CHANNEL_NOT_CHOOSE);
//					IvChooseCar.setAlpha(alpha_opaque);
//					IvChooseMan.setAlpha(alpha_transparent);
//					isChooseCarChannel = 1;
//					populateDeviceList(mBtStateOpen);
//				}
//			}
//		});

//		IvOpenDoorLogo.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				doOpenDoor(mBtStateOpen);
//				if (haveSound == 1) {
//					playOpenDoorSound();
//				}
//			}
//		});

		RlOpenKeyList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				keyRedDot.setVisibility(View.INVISIBLE); 
//				keyRedDotNum.setText("");
				
				Intent intent = new Intent();
				intent.setClass(getActivity(), KeyList.class);
				startActivity(intent);
			}
		});
		return view;
	}
	
	public void checkForNewKey() {
		uuid = loadUUID();
		if (uuid == null) {
			uuid = UUID.randomUUID().toString().replaceAll("-", "");
			saveUUID(uuid);
		}

		try {
			downLoadKeyURL = new URL(
					"https://zone.icloudoor.com/icloudoor-web/user/door/download2.do"
							+ "?sid=" + sid);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(Method.POST,
				downLoadKeyURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							if (response.getInt("code") == 1) {

								parseKeyData(response);

								Log.e(TAG, response.toString());

								if (response.getString("sid") != null)
									saveSid(response.getString("sid"));
								
								//TODO DELETE TEMPARY
								if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
									if (DBCount() > 0) {
										
										Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME,
												null);
										if (mCursor.moveToFirst()) {
											int zoneIdIndex = mCursor.getColumnIndex("zoneId");
											int deviceIdIndex = mCursor.getColumnIndex("deviceId");
											int doorNamemIndex = mCursor.getColumnIndex("doorName");
											int doorTypeIndex = mCursor.getColumnIndex("doorType");
											int directionIndex = mCursor.getColumnIndex("direction");

											do {
												HashMap<String, String> temp = new HashMap<String, String>();
												String deviceId = mCursor.getString(deviceIdIndex);
												String doorName = mCursor.getString(doorNamemIndex);
												String doorType = mCursor.getString(doorTypeIndex);
												String direction = mCursor.getString(directionIndex);
												String zoneId = mCursor.getString(zoneIdIndex);

												/*  Add new logic for car key
												 *  select the car doors can be opened, 
												 *  and all the man doors
												 */
												if (doorType.equals("2")) {						
													Log.e(TAG, "add a car key");
													temp.put("CDdeviceid", deviceId);
													temp.put("CDdoorName", doorName);
													temp.put("CDdoorType", doorType);
													temp.put("CDDirection", direction);
													carDoorList.add(temp);		
												} else if (doorType.equals("1")) {
													Log.e(TAG, "add man key");
													temp.put("MDdeviceid", deviceId);
													temp.put("MDdoorName", doorName);
													temp.put("MDdoorType", doorType);
													temp.put("MDDirection", direction);
													manDoorList.add(temp);
												}
											} while (mCursor.moveToNext());
										}
						                mCursor.close();
									}
								}

							} else if (response.getInt("code") == -81) {
								if (getActivity() != null)
									Toast.makeText(getActivity(),
											R.string.have_no_key_authorised,
											Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							Log.e(TAG, "request error");
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("appId", uuid);
				return map;
			}
		};

		mQueue.add(mJsonRequest);
	}
	
	public void parseKeyData(JSONObject response) throws JSONException {
		Log.e("test for new interface", "parseKeyData func");
		
		// for new key download interface
		JSONObject data = response.getJSONObject("data");
		JSONArray doorAuths = data.getJSONArray("doorAuths");
		JSONArray zones = data.getJSONArray("zones");
		JSONArray cars = data.getJSONArray("cars");
		
		// for doorauths table -- START
		for (int index = 0; index < doorAuths.length(); index++) {
			JSONObject doorData = (JSONObject) doorAuths.get(index);
			
			ContentValues value = new ContentValues();
			
			if(doorData.getString("deviceId").length() > 0){
				if(!hasData(mKeyDB, doorData.getString("deviceId"))){    //insert the new key
					
					newNum++;
					
					value.put("zoneId", doorData.getString("zoneId"));
					value.put("doorName", doorData.getString("doorName"));
					value.put("doorId", doorData.getString("doorId"));
					value.put("deviceId", doorData.getString("deviceId"));
					value.put("doorType", doorData.getString("doorType"));
					value.put("authFrom", doorData.getString("authFrom"));
					value.put("authTo", doorData.getString("authTo"));
					
					if (doorData.getString("doorType").equals("1")) {
						Log.e(TAG, "add a 1");
						value.put("direction", "none");
						value.put("plateNum", "none");
						mKeyDB.insert(TABLE_NAME, null, value);
					} else if (doorData.getString("doorType").equals("2")){
						Log.e(TAG, "add a 2");
								value.put("plateNum", doorData.getString("plateNum"));
								value.put("direction", doorData.getString("direction"));
								mKeyDB.insert(TABLE_NAME, null, value);
					}
					Log.e(TAG, "after parse: " + String.valueOf(DBCount()));
				} else {            // update the old key status
						ContentValues valueTemp = new ContentValues();
						valueTemp.put("authFrom", doorData.getString("authFrom"));
						valueTemp.put("authTo", doorData.getString("authTo"));
						
						mKeyDB.update("KeyInfoTable", valueTemp, "deviceId = ?", new String[] {doorData.getString("deviceId")});
				}
			}	
		}
		
		//need to delete the old key
		if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
			if (DBCount() > 0) {
				Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME, null);
				if (mCursor.moveToFirst()) {
					boolean keepKey = false;
					int deviceIdIndex = mCursor.getColumnIndex("deviceId");
					String deviceId = mCursor.getString(deviceIdIndex);
		
					do{
						for (int index = 0; index < doorAuths.length(); index++) {
							JSONObject doorData = (JSONObject) doorAuths.get(index);
							
							if(doorData.getString("deviceId").length() > 0){
								if(doorData.getString("deviceId").equals(deviceId)){
									keepKey = true;
									break;
								}
							}
						}	
						
						if(!keepKey){
							Log.e(TAG, "delete a");
							//delete in the table
							mKeyDB.delete("KeyInfoTable", "deviceId = ?", new String[] {deviceId});
						}
						
					}while(mCursor.moveToNext());				
				}	
				mCursor.close();
			}
		}
		// for doorauths table -- END
		
		// for zones table -- START
		for (int index = 0; index < zones.length(); index++) {
			JSONObject zoneData = (JSONObject) zones.get(index);
			ContentValues value = new ContentValues();
			
			if(zoneData.getString("zoneId").length() > 0){
				if(!hasZoneData(mKeyDB, zoneData.getString("zoneId"))){   // insert new
					Log.e(TAG, "add a zone");
					value.put("zoneid", zoneData.getString("zoneId"));		
					value.put("zonename", zoneData.getString("zoneName"));
					value.put("parentzoneid", zoneData.getString("parentZoneId"));
					mKeyDB.insert(ZONE_TABLE_NAME, null, value);
				}
			}
		}
		
		// delete old
		if (mKeyDBHelper.tabIsExist(ZONE_TABLE_NAME)) {
			if (DBCountZone() > 0) {
				Cursor mCursor = mKeyDB.rawQuery("select * from " + ZONE_TABLE_NAME, null);
				if (mCursor.moveToFirst()) {
					boolean keepKey = false;
					int zoneidIndex = mCursor.getColumnIndex("zoneid");
					String zoneid = mCursor.getString(zoneidIndex);
					
					do{
						for (int index = 0; index < zones.length(); index++) {
							JSONObject zoneData = (JSONObject) zones.get(index);
							
							if(zoneData.getString("zoneId").length() > 0){
								if(zoneData.getString("zoneId").equals(zoneid)){
									keepKey = true;
									break;
								}
							}
						}		
						
						if(!keepKey){
							Log.e(TAG, "delete a zone");
							mKeyDB.delete("ZoneTable", "zoneId = ?", new String[] {zoneid});
						}
					}while(mCursor.moveToNext());
				}
				mCursor.close();
			}
		}
		// for zones table -- END
		
		// for cars table -- START
		for (int index = 0; index < cars.length(); index++) {
			JSONObject carData = (JSONObject) cars.get(index);
			
			if(carData.getString("l1ZoneId").length() > 0){
				if(!hasCarData(mKeyDB, carData.getString("l1ZoneId"), carData.getString("plateNum"))){   // insert new
					Log.e(TAG, "add a car");
					ContentValues value = new ContentValues();
					value.put("l1ZoneId", carData.getString("l1ZoneId"));
					value.put("plateNum", carData.getString("plateNum"));
					value.put("carStatus", carData.getString("carStatus"));
					value.put("carPosStatus", carData.getString("carPosStatus"));
					mKeyDB.insert(CAR_TABLE_NAME, null, value);
					
					// refresh the carPosStatus to "0" if carPosStatus not "0" and you hava a car key (own or borrowed) when you get the new car key
					if(!carData.getString("carPosStatus").equals("0") && !carData.getString("carStatus").equals("3")){
						updatePosStatus(carData.getString("l1ZoneId"), carData.getString("plateNum"));
					}
				}else{   // update old
					ContentValues value = new ContentValues();
					
					if(carData.getString("plateNum").equals(carNumAndPhoneNumShare.getString("CARNUM", null))){
						value.put("carStatus", carData.getString("carStatus"));
						value.put("carPosStatus", carData.getString("carPosStatus"));
						
						mKeyDB.update("CarKeyTable", value, "l1ZoneId = ? and plateNum = ?", new String[] {carData.getString("l1ZoneId"), carData.getString("plateNum")});
					}
				}
			}
		}
		
		// delete old
		
			if (mKeyDBHelper.tabIsExist("CarKeyTable")) {
				if(DBCountCar() > 0){
					Cursor mCursor = mKeyDB.rawQuery("select * from " + "CarKeyTable", null);
					if(mCursor.moveToFirst()){
						boolean keepKey = false;
						int l1ZoneIdIndex = mCursor.getColumnIndex("l1ZoneId");
						int plateNumIndex = mCursor.getColumnIndex("plateNum");
						
						String l1ZoneId = mCursor.getString(l1ZoneIdIndex);
						String plateNum = mCursor.getString(plateNumIndex);
						
						do{
							for (int index = 0; index < cars.length(); index++) {
								JSONObject carData = (JSONObject) cars.get(index);
								
								if(carData.getString("l1ZoneId").length() > 0){
									if(carData.getString("l1ZoneId").equals(l1ZoneId) && carData.getString("plateNum").equals(plateNum)){
										keepKey = true;
										break;
									}
								}
							}
							if(!keepKey){
								Log.e(TAG, "delete a car");
								mKeyDB.delete("CarKeyTable", "l1ZoneId = ? and plateNum = ?", new String[] {l1ZoneId, plateNum});
								mKeyDB.delete("KeyInfoTable", "zoneId = ? and plateNum = ?", new String[] {l1ZoneId, plateNum});
							}
						}while(mCursor.moveToNext());
					}
					mCursor.close();
				}
			}
		// for cars table -- END
			
		if (newNum > 0) {
			keyRedDot.setVisibility(View.VISIBLE);
		}
	}
	
	public void updatePosStatus(final String zoneid, final String carnum) {
		URL updateCarPosStatusURL = null;
		String sid2 = null;
		RequestQueue mQueue2;
		
		sid2 = loadSid();
		try {
			updateCarPosStatusURL = new URL(HOST + "/user/api/updateCarPosStatus.do" + "?sid=" + sid2);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		mQueue2 = Volley.newRequestQueue(getActivity());
		
		MyJsonObjectRequest mJsonRequest2 = new MyJsonObjectRequest(Method.POST, updateCarPosStatusURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.e(TAG, "test " + response.toString());
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
				map.put("l1ZoneId", zoneid);
				map.put("plateNum", carnum);
				map.put("carPosStatus", "0");
				return map;
			}
		};
		mQueue2.add(mJsonRequest2);
	}

	// for new channel switch
	private int getState(boolean state) {
    	if(state) {
    		return 1;
    	} 
    	return 0;
    }
	
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

		try {
//			if (latitude != 0.0 || longitude != 0.0) { // can get the location
//														// in time
//				SharedPreferences saveLocation = getActivity()
//						.getSharedPreferences("LOCATION", 0);
//				Editor editor = saveLocation.edit();
//				editor.putString("Latitude", String.valueOf(latitude));
//				editor.putString("Longitude", String.valueOf(longitude));
//				editor.commit();
//
//				weatherURL = new URL(HOST + "city=" + String.valueOf(latitude)
//						+ ":" + String.valueOf(longitude)
//						+ "&language=zh-chs&unit=c&aqi=city&key=" + Key);
//			} else {
//				SharedPreferences loadLocation = getActivity()
//						.getSharedPreferences("LOCATION", 0); // if we can't get
//																// the location
//																// in time, use
//																// the location
//																// for the last
//																// usage
//				latitude = Double.parseDouble(loadLocation.getString(
//						"Latitude", "0.0"));
//				longitude = Double.parseDouble(loadLocation.getString(
//						"Longitude", "0.0"));
//
//				if (longitude == 0.0 && latitude == 0.0) // if no location for
//															// the last usage,
//															// then use the ip
//															// address to get
//															// the weather info
//															// for better user
//															// experiences
//					weatherURL = new URL(HOST + "city=ip"
//							+ "&language=zh-chs&unit=c&aqi=city&key=" + Key);
//				else 
					weatherURL = new URL(HOST + "city=" + String.valueOf(latitude)
							+ ":" + String.valueOf(longitude)
							+ "&language=zh-chs&unit=c&aqi=city&key=" + Key);
					
//			}

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
								if (getActivity() != null) {
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
                                }
								contentYi.setText(Day1.getString("yi"));
								contentJi.setText(Day1.getString("ji"));
								
								if (getActivity() != null) {
									SharedPreferences saveRequestLHL = getActivity().getSharedPreferences("LHLREQUESTDATE",
											0);
									Editor editor1 = saveRequestLHL.edit();
									editor1.putString("LHLlastrequestdate", day1);
									editor1.commit();
								}	
							}
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
								if (getActivity() != null) {
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
                                }
                                weatherTemperature.setText(now.getString("temperature") + String.valueOf(centigrade));
								weatherStatus.setText(now.getString("text"));
								weatherTemperature.setTextSize(19);
								weatherStatus.setTextSize(13);
							} else {
								weatherTemperature.setText(getString(R.string.weather_not_available));
								weatherTemperature.setTextSize(16);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						weatherTemperature.setText(getString(R.string.weather_not_available));
						weatherTemperature.setTextSize(16);
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
				weatherTemperature.setText(getString(R.string.weather_not_available));
				weatherTemperature.setTextSize(16);
			}else{
				weatherTemperature.setText(loadWeather.getString("Day1Temp", "N/A") + String.valueOf(centigrade)); //TODO
				weatherStatus.setText(loadWeather.getString("Day1Weather", "N/A"));
				
				weatherTemperature.setTextSize(19);
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
						weatherTemperature.setText(getString(R.string.weather_not_available));
						weatherTemperature.setTextSize(16);
					}else{
						weatherTemperature.setText(loadWeather.getString("Day1Temp", "N/A") + String.valueOf(centigrade));
						weatherStatus.setText(loadWeather.getString("Day1Weather", "N/A"));
						
						weatherTemperature.setTextSize(19);
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
						weatherTemperature.setText(getString(R.string.weather_not_available));
						weatherTemperature.setTextSize(16);
					}else{
						weatherTemperature.setText(loadWeather.getString("Day2TempHigh", "N/A") + String.valueOf(centigrade));
						weatherStatus.setText(loadWeather.getString("Day2Weather", "N/A"));
						
						weatherTemperature.setTextSize(19);
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
						weatherTemperature.setText(getString(R.string.weather_not_available));
						weatherTemperature.setTextSize(16);
					}else{
						weatherTemperature.setText(loadWeather.getString("Day2TempHigh", "N/A") + String.valueOf(centigrade));
						weatherStatus.setText(loadWeather.getString("Day2Weather", "N/A"));
						
						weatherTemperature.setTextSize(19);
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
						weatherTemperature.setText(getString(R.string.weather_not_available));
						weatherTemperature.setTextSize(16);
					}else{
						weatherTemperature.setText(loadWeather.getString("Day3TempHigh", "N/A") + String.valueOf(centigrade));
						weatherStatus.setText(loadWeather.getString("Day3Weather", "N/A"));
						
						weatherTemperature.setTextSize(19);
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
        Log.e("TEST63", "keyFragment onStart()");
		super.onStart();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        BluetoothManager mBluetoothManager = (BluetoothManager) getActivity()
                .getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.bt_not_supported, Toast.LENGTH_SHORT).show();
        }

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 10:
                        if (mOpenDoorState == 0) {
                            Log.i(TAG, "Thread handler");
                            populateDeviceList(mBtStateOpen);
                        }
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };

        if (mBluetoothAdapter.isEnabled()) {
            if (mBluetoothAdapter.isEnabled()) {
                if (mBluetoothAdapter.isDiscovering()) {
                } else {
                    mBtStateOpen = true;
                    Log.i(TAG, "myThread111");
                    myThread = new MyThread();
                    myThread.start();
                    //populateDeviceList(mBtStateOpen);
                    // if(getActivity() != null)
                    // Toast.makeText(getActivity(), R.string.bt_enabled,
                    // Toast.LENGTH_SHORT).show();
                    service_init();
                }
            }
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

	@Override
	public void onResume() {
		super.onResume();
		
//		if (sensorManager != null) {
//            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
//        }
		
		Log.e("TEST", "keyFragment onResume()");
        mOpenDoorState = 0;
//		checkBlueToothState();

//		//TODO DELETE TEMPARY
//		if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
//			if (DBCount() > 0) {
//				
//				Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME,
//						null);
//				if (mCursor.moveToFirst()) {
//					int zoneIdIndex = mCursor.getColumnIndex("zoneId");
//					int deviceIdIndex = mCursor.getColumnIndex("deviceId");
//					int doorNamemIndex = mCursor.getColumnIndex("doorName");
//					int doorTypeIndex = mCursor.getColumnIndex("doorType");
//					int directionIndex = mCursor.getColumnIndex("direction");
////					int carStatusIndex = mCursor.getColumnIndex("carStatus");
////					int carPosStatusIndex = mCursor.getColumnIndex("carPosStatus");
//
//					do {
//						HashMap<String, String> temp = new HashMap<String, String>();
//						String deviceId = mCursor.getString(deviceIdIndex);
//						String doorName = mCursor.getString(doorNamemIndex);
//						String doorType = mCursor.getString(doorTypeIndex);
//						String direction = mCursor.getString(directionIndex);
////						String carStatus = mCursor.getString(carStatusIndex);
////						String carPosStatus = mCursor.getString(carPosStatusIndex);
//						String zoneId = mCursor.getString(zoneIdIndex);
//
//						/*  Add new logic for car key
//						 *  select the car doors can be opened, 
//						 *  and all the man doors
//						 */
//						if (doorType.equals("2")) {						
//							Log.e(TAG, "add a car key");
//							temp.put("CDdeviceid", deviceId);
//							temp.put("CDdoorName", doorName);
//							temp.put("CDdoorType", doorType);
//							temp.put("CDDirection", direction);
//							carDoorList.add(temp);		
//						} else if (doorType.equals("1")) {
//							Log.e(TAG, "add man key");
//							temp.put("MDdeviceid", deviceId);
//							temp.put("MDdoorName", doorName);
//							temp.put("MDdoorType", doorType);
//							temp.put("MDDirection", direction);
//							manDoorList.add(temp);
//						}
//					} while (mCursor.moveToNext());
//				}
//                mCursor.close();
//			}
//		}

//        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                Message msg = new Message();
//                msg.what = 10;
//                mHandler.sendMessage(msg);
//                Log.i(TAG, "myThread");
//            }
//        }   , 0,
//         6,
//                TimeUnit.SECONDS) ;
	}

    private  class MyThread extends Thread {

        private volatile boolean stopThread = false;
		private volatile boolean mKeyFindState = false;
        private final long mScanningProidShort = 3000;
		private final long mScanningProidLong = 6000;

        public void stopThread() {
            this.stopThread = true;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted() && !stopThread) {
                Message msg = new Message();
                msg.what = 10;
                mHandler.sendMessage(msg);
                Log.i("ThreadTest", Thread.currentThread().getId() + "myThread");
                try {
                    Thread.sleep(mScanningProidShort);
                    if (mKeyFindState == true){
                        Thread.sleep(mScanningProidLong);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("test63", "onActivityResult ");
        if (requestCode == REQUEST_ENABLE_BT && resultCode == 1){
            if (mBluetoothAdapter.isEnabled()) {
                if (mBluetoothAdapter.isDiscovering()) {
                } else{
                    mBtStateOpen = true;
                    Log.i(TAG, "myThread111");
                    myThread = new MyThread();
                    myThread.start();

                    //populateDeviceList(mBtStateOpen);
                    service_init();
                }
            }
        }else if(requestCode == REQUEST_ENABLE_BT && resultCode == 0){
            mBtStateOpen = false;
        	BtnOpenDoor.setEnabled(true);
        	BtnOpenDoor.setImageResource(R.drawable.selector_open_door);
        }
    }

    @Override
	public void onPause() {
		super.onPause();
		if(mBluetoothAdapter.isEnabled())
			scanLeDevice(false);
        if (myThread != null) {
            myThread.stopThread();
        }
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(mBluetoothStateReceiver);
        }
        try {
            LocalBroadcastManager.getInstance(getActivity())
                    .unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception e) {

        }

        if(mUartService != null){
            if (getActivity() != null) {
                getActivity().unbindService(mServiceConnection);
            }
            mUartService.stopSelf();
            mUartService = null;
        }



	}

	private void service_init() {
		Intent bindIntent = new Intent(getActivity(), UartService.class);
		getActivity().bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
	}

//	private void checkBlueToothState() {
//		Log.e("BLE", "checkBlueToothState");
//		if (mBluetoothAdapter == null) {
//			if (getActivity() != null)
//				Toast.makeText(getActivity(), R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
//		} else {
//			if (mBluetoothAdapter.isEnabled()) {
//				if (mBluetoothAdapter.isDiscovering()) {
//				} else{
//					populateDeviceList(mBtStateOpen);
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

	private void populateDeviceList(final boolean btStateOpen) {
		Log.e("BLE", "populateDeviceList");
		if (btStateOpen) {
            BtnOpenDoor.setImageResource(R.drawable.door_normalll_new);
            BtnOpenDoor.setEnabled(false);

            radar.startAnimation(animation1);
            circle.setVisibility(View.VISIBLE);
            radar.setVisibility(View.VISIBLE);

            doorName.setText("");
            doorNameFlag.setVisibility(View.INVISIBLE);

            mDeviceList = new ArrayList<BluetoothDevice>();
            mDeviceAdapter = new DeviceAdapter(getActivity(), mDeviceList);
            mDevRssiValues = new HashMap<String, Integer>();
            scanLeDevice(true);

            scanStatus.setText(R.string.scanning);
        }
	}

	private void scanLeDevice(final boolean enable) {
		Log.e("BLE", "scanLeDevice");
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			new Handler().postDelayed(new Runnable() {
				@SuppressLint("NewApi")
				@Override
				public void run() {
					boolean findKey = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					mBTScanning = false;
//					channelSwitch.setEnabled(true);

					Log.e(TAG, "mDeviceList.size() =" + String.valueOf(mDeviceList.size()));

					// add for the case of only one door -- START
					if (mDeviceList != null && mDeviceList.size() == 1) {
						onlyOneDoor = true;
						for (int i = 0; i < carDoorList.size(); i++) {
							String tempDID = carDoorList.get(i).get("CDdeviceid");
							tempDID = tempDID.toUpperCase();
							char[] data = tempDID.toCharArray();
							String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
									+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
									+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
									+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
									+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
									+ String.valueOf(data[10]) + String.valueOf(data[11]);
							Log.e("TEST", "CDdeviceID:" + formatDeviceId);

							if (mDeviceList.get(0).getAddress().equals(formatDeviceId)) {
//								IvOpenDoorLogo.setEnabled(true);
//								IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
//								IvSearchKey.setBackgroundResource(R.drawable.btn_background_blue);
//								TvDistrictDoor.setText(carDoorList.get(i).get("CDdoorName"));
//								TvDistrictDoor.setTextSize(18);
//								TvDistrictDoor.setTextColor(0xFFffffff);
//								TvCarNumber.setText(carDoorList.get(i).get("CDdeviceid"));
//								TvCarNumber.setTextColor(0xFFffffff);

								BtnOpenDoor.setImageResource(R.drawable.selector_open_door);
								BtnOpenDoor.setEnabled(true);

								doorName.setText(carDoorList.get(i).get("CDdoorName"));
								doorNameFlag.setVisibility(View.VISIBLE);

//								csv.changeChecked(true);
//								onlyOneDoor = false;
								findKey = true;
								isChooseCarChannel = 1;
								switchBtn.setSwitch(false);
								break;
							}
						}

						if (!findKey) {
							for (int i = 0; i < manDoorList.size(); i++) {
								String tempDID = manDoorList.get(i).get("MDdeviceid");
								tempDID = tempDID.toUpperCase();
								char[] data = tempDID.toCharArray();
								String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
										+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
										+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
										+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
										+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
										+ String.valueOf(data[10]) + String.valueOf(data[11]);
								Log.e("TEST", "MDdeviceID:" + formatDeviceId);

								if (mDeviceList.get(0).getAddress().equals(formatDeviceId)) {
//								IvOpenDoorLogo.setEnabled(true);
//								IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
//								IvSearchKey.setBackgroundResource(R.drawable.btn_background_blue);
//								TvDistrictDoor.setText(manDoorList.get(i).get("MDdoorName"));
//								TvDistrictDoor.setTextSize(18);
//								TvDistrictDoor.setTextColor(0xFFffffff);
//								TvCarNumber.setText(manDoorList.get(i).get("MDdeviceid"));
//								TvCarNumber.setTextColor(0xFFffffff);

									BtnOpenDoor.setImageResource(R.drawable.selector_open_door);
									BtnOpenDoor.setEnabled(true);

									doorName.setText(manDoorList.get(i).get("MDdoorName"));
									doorNameFlag.setVisibility(View.VISIBLE);

//                                    csv.changeChecked(false);
//								onlyOneDoor = false;

									isChooseCarChannel = 0;
									switchBtn.setSwitch(true);
									findKey = true;
									break;
								}
							}
						}
					}
					// add for the case of only one door -- END		

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
					if (!findKey) {
						if (mDeviceList != null && mDeviceList.size() > 1) {
							onlyOneDoor = false;
							if (isChooseCarChannel == 1) {
//								List<BluetoothDevice> tempCarDoorList = new ArrayList<BluetoothDevice>();
								for (int temp = 0; temp < mDeviceList.size(); temp++) {
									for (int i = 0; i < carDoorList.size(); i++) {
										String tempDID = carDoorList.get(i).get("CDdeviceid");
										tempDID = tempDID.toUpperCase();
										char[] data = tempDID.toCharArray();
										String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
												+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
												+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
												+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
												+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
												+ String.valueOf(data[10]) + String.valueOf(data[11]);
										if (formatDeviceId.equals(mDeviceList.get(temp).getAddress())) {
											tempCarDoorList.add(mDeviceList.get(temp));
											findKey = true;
										}
									}
								}
								if (findKey) {
									if (tempCarDoorList.size() == 1) {
										for (int i = 0; i < carDoorList.size(); i++) {
											String tempDID = carDoorList.get(i).get("CDdeviceid");
											tempDID = tempDID.toUpperCase();
											char[] data = tempDID.toCharArray();
											String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
													+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
													+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
													+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
													+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
													+ String.valueOf(data[10]) + String.valueOf(data[11]);
											Log.e("TEST", "CDdeviceID:" + formatDeviceId);

											if (tempCarDoorList.get(0).getAddress().equals(formatDeviceId)) {
												BtnOpenDoor.setImageResource(R.drawable.selector_open_door);
												BtnOpenDoor.setEnabled(true);
												doorName.setText(carDoorList.get(i).get("CDdoorName"));
												doorNameFlag.setVisibility(View.VISIBLE);
											}
										}
									}else if (tempCarDoorList.size() > 1){

											int maxRssiIndex = 0;
											int maxRssi = -128;

											for (int i = 0; i < tempCarDoorList.size(); i++) {
												Log.e("TEST", "checking rssi");
												String tempAdd = tempCarDoorList.get(i).getAddress();
												int tempRssi = mDevRssiValues.get(tempAdd);
												if (tempRssi > maxRssi) {
													maxRssi = tempRssi;
													maxRssiIndex = i;
												}
											}
											deviceIndexToOpen = maxRssiIndex;

										for (int i = 0; i < carDoorList.size(); i++) {
											String tempDID = carDoorList.get(i).get("CDdeviceid");
											tempDID = tempDID.toUpperCase();
											char[] data = tempDID.toCharArray();
											String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
													+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
													+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
													+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
													+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
													+ String.valueOf(data[10]) + String.valueOf(data[11]);
											Log.e("TEST", "CDdeviceID:" + formatDeviceId);

											if (tempCarDoorList.get(deviceIndexToOpen).getAddress().equals(formatDeviceId)) {
												BtnOpenDoor.setImageResource(R.drawable.selector_open_door);
												BtnOpenDoor.setEnabled(true);
												doorName.setText(carDoorList.get(i).get("CDdoorName"));
												doorNameFlag.setVisibility(View.VISIBLE);
											}
										}
									}
								}
							}else {// man door
//								List<BluetoothDevice> tempManDoorList = new ArrayList<BluetoothDevice>();
								for (int temp = 0; temp < mDeviceList.size(); temp++) {
									for (int i = 0; i < manDoorList.size(); i++) {
										String tempDID = manDoorList.get(i).get("MDdeviceid");
										tempDID = tempDID.toUpperCase();
										char[] data = tempDID.toCharArray();
										String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
												+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
												+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
												+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
												+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
												+ String.valueOf(data[10]) + String.valueOf(data[11]);
										if (formatDeviceId.equals(mDeviceList.get(temp).getAddress())) {
											tempManDoorList.add(mDeviceList.get(temp));
											findKey = true;
										}
									}
								}
								if (findKey) {
									if (tempManDoorList.size() == 1) {
										for (int i = 0; i < manDoorList.size(); i++) {
											String tempDID = manDoorList.get(i).get("MDdeviceid");
											tempDID = tempDID.toUpperCase();
											char[] data = tempDID.toCharArray();
											String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
													+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
													+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
													+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
													+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
													+ String.valueOf(data[10]) + String.valueOf(data[11]);
											Log.e("TEST", "MDdeviceID:" + formatDeviceId);

											if (mDeviceList.get(0).getAddress().equals(formatDeviceId)) {
												BtnOpenDoor.setImageResource(R.drawable.selector_open_door);
												BtnOpenDoor.setEnabled(true);
												doorName.setText(manDoorList.get(i).get("MDdoorName"));
												doorNameFlag.setVisibility(View.VISIBLE);
											}
										}
									}else if (tempManDoorList.size() > 1) {

										int maxRssiIndex = 0;
										int maxRssi = -128;

										for (int i = 0; i < tempManDoorList.size(); i++) {
											Log.e("TEST", "checking rssi");
											String tempAdd = tempManDoorList.get(i).getAddress();
											int tempRssi = mDevRssiValues.get(tempAdd);
											if (tempRssi > maxRssi) {
												maxRssi = tempRssi;
												maxRssiIndex = i;
											}
										}
										deviceIndexToOpen = maxRssiIndex;

										for (int i = 0; i < manDoorList.size(); i++) {
											String tempDID = manDoorList.get(i).get("MDdeviceid");
											tempDID = tempDID.toUpperCase();
											char[] data = tempDID.toCharArray();
											String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
													+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
													+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
													+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
													+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
													+ String.valueOf(data[10]) + String.valueOf(data[11]);
											Log.e("TEST", "MDdeviceID:" + formatDeviceId);

											if (tempManDoorList.get(deviceIndexToOpen).getAddress().equals(formatDeviceId)) {
												BtnOpenDoor.setImageResource(R.drawable.selector_open_door);
												BtnOpenDoor.setEnabled(true);
												doorName.setText(manDoorList.get(i).get("MDdoorName"));
												doorNameFlag.setVisibility(View.VISIBLE);
											}
										}
									}
								}
							}
						}
					}
					if (findKey/*mDeviceList.size() != 0*/) {
						scanStatus.setText(R.string.can_shake_to_open_door);
						myThread.mKeyFindState = true;
						circle.setVisibility(View.INVISIBLE);
						radar.setVisibility(View.INVISIBLE);
						radar.clearAnimation();
					} else {
						findKey = false;
						myThread.mKeyFindState = false;
						circle.setVisibility(View.VISIBLE);
						radar.setVisibility(View.VISIBLE);
					}
				}
			}, SCAN_PERIOD);
			if (mBluetoothAdapter.startLeScan(mLeScanCallback)){
                mBTScanning = true;
                Log.i(TAG, "mBTScanning is true");
            }
//			channelSwitch.setEnabled(false);
//			if (getActivity() != null)
//				Toast.makeText(getActivity(), R.string.scanning, 3000).show();
		} else {
            if (mBTScanning) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
//			channelSwitch.setEnabled(true);
			/*
			if(mDeviceList.size() != 0) scanStatus.setText(R.string.can_shake_to_open_door);

			// add for the case of only one door -- START
			if (mDeviceList != null && mDeviceList.size() == 1) {
				onlyOneDoor = true;
				for (int i = 0; i < carDoorList.size(); i++) {
					String tempDID = carDoorList.get(i).get("CDdeviceid");
					tempDID = tempDID.toUpperCase();
					char[] data = tempDID.toCharArray();
					String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
							+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
							+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
							+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
							+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
							+ String.valueOf(data[10]) + String.valueOf(data[11]);
					Log.e("TEST", "CDdeviceID:" + formatDeviceId);

					if (mDeviceList.get(0).getAddress().equals(formatDeviceId)) {
//						IvOpenDoorLogo.setEnabled(true);
//						IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
//						IvSearchKey.setBackgroundResource(R.drawable.btn_background_blue);
//						TvDistrictDoor.setText(carDoorList.get(i).get("CDdoorName"));
//						TvDistrictDoor.setTextSize(18);
//						TvDistrictDoor.setTextColor(0xFFffffff);
//						TvCarNumber.setText(carDoorList.get(i).get("CDdeviceid"));
//						TvCarNumber.setTextColor(0xFFffffff);
						
						BtnOpenDoor.setImageResource(R.drawable.selector_open_door);
						BtnOpenDoor.setEnabled(true);
						
						doorName.setText(carDoorList.get(i).get("CDdoorName"));
                        doorNameFlag.setVisibility(View.VISIBLE);
						
						csv.changeChecked(true);
//						onlyOneDoor = false;

						isChooseCarChannel = 1;
						
						break;
					}
				}
				
				for (int i = 0; i < manDoorList.size(); i++) {
					String tempDID = manDoorList.get(i).get("MDdeviceid");
					tempDID = tempDID.toUpperCase();
					char[] data = tempDID.toCharArray();
					String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
							+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
							+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
							+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
							+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
							+ String.valueOf(data[10]) + String.valueOf(data[11]);
					Log.e("TEST", "MDdeviceID:" + formatDeviceId);

					if (mDeviceList.get(0).getAddress().equals(formatDeviceId)) {
//						IvOpenDoorLogo.setEnabled(true);
//						IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
//						IvSearchKey.setBackgroundResource(R.drawable.btn_background_blue);
//						TvDistrictDoor.setText(manDoorList.get(i).get("MDdoorName"));
//						TvDistrictDoor.setTextSize(18);
//						TvDistrictDoor.setTextColor(0xFFffffff);
//						TvCarNumber.setText(manDoorList.get(i).get("MDdeviceid"));
//						TvCarNumber.setTextColor(0xFFffffff);
						
						BtnOpenDoor.setImageResource(R.drawable.selector_open_door);
						BtnOpenDoor.setEnabled(true);
						
						doorName.setText(manDoorList.get(i).get("MDdoorName"));
                        doorNameFlag.setVisibility(View.VISIBLE);

						csv.changeChecked(false);
//						onlyOneDoor = false;
						
						isChooseCarChannel = 0;
						
						break;
					}
				}
			}
			// add for the case of only one door -- END	
			
			if (mDeviceList != null && mDeviceList.size() > 1) {
				onlyOneDoor = false;
				if (isChooseCarChannel == 1) {
					for (int i = 0; i < carDoorList.size(); i++) {
						String tempDID = carDoorList.get(i).get("CDdeviceid");
						tempDID = tempDID.toUpperCase();
						char[] data = tempDID.toCharArray();
						String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
								+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
								+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
								+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
								+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
								+ String.valueOf(data[10]) + String.valueOf(data[11]);
						Log.e("TEST", "CDdeviceID:" + formatDeviceId);

						if (mDeviceList.get(deviceIndexToOpen).getAddress().equals(formatDeviceId)) {
//							IvOpenDoorLogo.setEnabled(true);
//							IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
//							IvSearchKey.setBackgroundResource(R.drawable.btn_background_blue);
//							TvDistrictDoor.setText(carDoorList.get(i).get("CDdoorName"));
//							TvDistrictDoor.setTextSize(18);
//							TvDistrictDoor.setTextColor(0xFFffffff);
//							TvCarNumber.setText(carDoorList.get(i).get("CDdeviceid"));
//							TvCarNumber.setTextColor(0xFFffffff);
							
							BtnOpenDoor.setImageResource(R.drawable.selector_open_door);
							BtnOpenDoor.setEnabled(true);
							
							doorName.setText(carDoorList.get(i).get("CDdoorName"));
                            doorNameFlag.setVisibility(View.VISIBLE);
						}
					}
				} else {
					for (int i = 0; i < manDoorList.size(); i++) {
						String tempDID = manDoorList.get(i).get("MDdeviceid");
						tempDID = tempDID.toUpperCase();
						char[] data = tempDID.toCharArray();
						String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
								+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
								+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
								+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
								+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
								+ String.valueOf(data[10]) + String.valueOf(data[11]);
						Log.e("TEST", "MDdeviceID:" + formatDeviceId);

						if (mDeviceList.get(deviceIndexToOpen).getAddress().equals(formatDeviceId)) {
//							IvOpenDoorLogo.setEnabled(true);
//							IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
//							IvSearchKey.setBackgroundResource(R.drawable.btn_background_blue);
//							TvDistrictDoor.setText(manDoorList.get(i).get("MDdoorName"));
//							TvDistrictDoor.setTextSize(18);
//							TvDistrictDoor.setTextColor(0xFFffffff);
//							TvCarNumber.setText(manDoorList.get(i).get("MDdeviceid"));
//							TvCarNumber.setTextColor(0xFFffffff);
							
							BtnOpenDoor.setImageResource(R.drawable.selector_open_door);
							BtnOpenDoor.setEnabled(true);
							
							doorName.setText(manDoorList.get(i).get("MDdoorName"));
                            doorNameFlag.setVisibility(View.VISIBLE);
						}
					}
				}
			}*/
		}
	}

	private void addDevice(BluetoothDevice device, int rssi) {
		Log.e("BLE", "addDevice");
		boolean deviceFound = false;
		

		for (BluetoothDevice listDev : mDeviceList) {
			if (listDev.getAddress().equals(device.getAddress())) {
				deviceFound = true;
				break;
			}
		}
		
		if(tempDeviceAddr != device.getAddress()){
			tempDeviceAddr = device.getAddress();
			
//			if (isChooseCarChannel == 1) {
				for (int i = 0; i < carDoorList.size(); i++) {
					String tempDID = carDoorList.get(i).get("CDdeviceid");
					tempDID = tempDID.toUpperCase();
					char[] data = tempDID.toCharArray();
					String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
							+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":" 
							+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
							+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":" 
							+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
							+ String.valueOf(data[10]) + String.valueOf(data[11]);
					Log.e("TEST", "CDdeviceID:" + formatDeviceId);

					if (device.getAddress().equals(formatDeviceId)) {
						mDevRssiValues.put(device.getAddress(), rssi);
						if (!deviceFound) {
							Log.e("TEST", "add a car door");
							mDeviceList.add(device);
							mDeviceAdapter.notifyDataSetChanged();
							
							break;
						}
					}
				}
//			} else {
				for (int i = 0; i < manDoorList.size(); i++) {
					String tempDID = manDoorList.get(i).get("MDdeviceid");
					tempDID = tempDID.toUpperCase();
					char[] data = tempDID.toCharArray();
					String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
							+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
							+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
							+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
							+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
							+ String.valueOf(data[10]) + String.valueOf(data[11]);
					Log.e("TEST", "MDdeviceID:" + formatDeviceId);

					if (device.getAddress().equals(formatDeviceId)) {
						mDevRssiValues.put(device.getAddress(), rssi);
						if (!deviceFound) {
							Log.e("TEST", "add a man door");
							mDeviceList.add(device);
							mDeviceAdapter.notifyDataSetChanged();
							
							break;
						}
					}
				}
//			}
		}
	}
	
	private void doOpenDoor(final boolean btStateOpen) {
        Log.e("BLE", "doOpenDoor");
//		IvOpenDoorLogo.setEnabled(false);
        // if(getActivity() != null)
        // Toast.makeText(getActivity(), R.string.door_open,
        // Toast.LENGTH_SHORT).show();
        if (btStateOpen) {
            onlyOneDoor = !onlyOneDoor;

            if (mDeviceList != null && mDeviceList.size() > 0) {
                if (mDeviceList.get(deviceIndexToOpen).getAddress() != null) {
//                if (mBTScanning) {
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                }

                    //TODO
                    if (isChooseCarChannel == 1) {
                        for (int index = 0; index < carDoorList.size(); index++) {
                            String deviceId = null;
                            String tempDeviceId = null;
                            deviceId = carDoorList.get(index).get("CDdeviceid");
                            tempDeviceId = deviceId.toUpperCase();
                            char[] data = tempDeviceId.toCharArray();
                            String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
                                    + String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
                                    + String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
                                    + String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
                                    + String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
                                    + String.valueOf(data[10]) + String.valueOf(data[11]);

                            if (formatDeviceId.equals(tempCarDoorList.get(deviceIndexToOpen).getAddress())) {
                                if (carDoorList.get(index).get("CDDirection").equals("1")) {  // go in
                                    if (mKeyDBHelper.tabIsExist(CAR_TABLE_NAME)) {
                                        if (DBCountCar() > 0) {
                                            Cursor mCursor = mKeyDB.rawQuery("select * from " + CAR_TABLE_NAME, null);
                                            if (mCursor.moveToFirst()) {
                                                int carStatusIndex = mCursor.getColumnIndex("carStatus");
                                                int carPosStatusIndex = mCursor.getColumnIndex("carPosStatus");
                                                int l1ZoneIdIndex = mCursor.getColumnIndex("l1ZoneId");
                                                int plateNumIndex = mCursor.getColumnIndex("plateNum");

                                                do {
                                                    String carStatus = mCursor.getString(carStatusIndex);
                                                    String carPosStatus = mCursor.getString(carPosStatusIndex);
                                                    final String l1ZoneId = mCursor.getString(l1ZoneIdIndex);
                                                    final String plateNum = mCursor.getString(plateNumIndex);

                                                    if ((carStatus.equals("1") || carStatus.equals("2"))
                                                            && (carPosStatus.equals("0") || carPosStatus.equals("2"))) {
                                                        // can open
                                                        if (mUartService != null) {
                                                            mUartService.connect(tempCarDoorList.get(deviceIndexToOpen).getAddress());

                                                            // 30s
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    // update
                                                                    // the
                                                                    // carPosStatus
                                                                    ContentValues value = new ContentValues();
                                                                    value.put("carPosStatus", "1");
                                                                    mKeyDB.update(CAR_TABLE_NAME, value, "l1ZoneId=? and plateNum=?",
                                                                            new String[]{l1ZoneId, plateNum});
                                                                }
                                                            }, 30 * 1000);
                                                        }

                                                    } else if(carStatus.equals("3")){
                                                    	if (getActivity() != null) {
                                                            Toast.makeText(getActivity(), R.string.car_already_lend, Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        // can not open
                                                        if (getActivity() != null) {
                                                            Log.e(TAG, "sorry, cannot open");
                                                            Toast.makeText(getActivity(), R.string.sorry_you_are_in_the_zone, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                } while (mCursor.moveToNext());
                                            }
                                            mCursor.close();
                                        }
                                    }
                                } else if (carDoorList.get(index).get("CDDirection").equals("2")) {  // go out
                                    if (mKeyDBHelper.tabIsExist(CAR_TABLE_NAME)) {
                                        if (DBCountCar() > 0) {
                                            Cursor mCursor = mKeyDB.rawQuery("select * from " + CAR_TABLE_NAME, null);
                                            if (mCursor.moveToFirst()) {
                                                int carStatusIndex = mCursor.getColumnIndex("carStatus");
                                                int carPosStatusIndex = mCursor.getColumnIndex("carPosStatus");
                                                int l1ZoneIdIndex = mCursor.getColumnIndex("l1ZoneId");
                                                int plateNumIndex = mCursor.getColumnIndex("plateNum");

                                                do {
                                                    String carStatus = mCursor.getString(carStatusIndex);
                                                    String carPosStatus = mCursor.getString(carPosStatusIndex);
                                                    final String l1ZoneId = mCursor.getString(l1ZoneIdIndex);
                                                    final String plateNum = mCursor.getString(plateNumIndex);

                                                    if ((carStatus.equals("1") || carStatus.equals("2"))
                                                            && (carPosStatus.equals("0") || carPosStatus.equals("1"))) {
                                                        // can open
                                                        mUartService.connect(tempCarDoorList.get(deviceIndexToOpen).getAddress());


                                                        // 30s
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                // update the carPosStatus
                                                                ContentValues value = new ContentValues();
                                                                value.put("carPosStatus", "2");
                                                                mKeyDB.update(CAR_TABLE_NAME, value, "l1ZoneId=? and plateNum=?", new String[]{l1ZoneId, plateNum});
                                                            }
                                                        }, 30 * 1000);
                                                    } else if(carStatus.equals("3")){
                                                    	if (getActivity() != null) {
                                                            Toast.makeText(getActivity(), R.string.car_already_lend, Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        // can not open
                                                        if (getActivity() != null) {
                                                            Log.e(TAG, "sorry, cannot open");
                                                            Toast.makeText(getActivity(), R.string.sorry_you_are_out_the_zone, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                } while (mCursor.moveToNext());
                                            }
                                            mCursor.close();
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (mUartService != null) {
                            mUartService.connect(tempManDoorList.get(deviceIndexToOpen).getAddress());
                        }
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mOpenDoorState != 0) {
//                            Toast.makeText(getActivity(), R.string.open_door_fail, Toast.LENGTH_SHORT).show();
//                            Log.e("test for open door", "fail");
                                mOpenDoorState = 0;
                            }
                        }
                    }, 5000);

                }
            }
        }
    }

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				byte[] scanRecord) {
			Log.e("BLE", "onLeScan");
			if(getActivity() != null) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (getActivity() != null) {
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									addDevice(device, rssi);
								}
							});
						}

					}
				});
			}
		}
	};

	public void InitFragmentViews() {
		isFindKey = false;

		SharedPreferences setting = getActivity().getSharedPreferences(
				"SETTING", 0);
		isChooseCarChannel = setting.getInt("chooseCar", 1);
		canDisturb = setting.getInt("disturb", 1);
		haveSound = setting.getInt("sound", 1);
		canShake = setting.getInt("shake", 0);

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
	}

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

	private final BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("BLE", "mBluetoothStateReceiver");
			final String action = intent.getAction();
			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				final int state = intent.getIntExtra(
						BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

				switch (state) {
				case BluetoothAdapter.STATE_OFF:
					Log.e("BLE", "BluetoothAdapter.STATE_OFF");
                    mBtStateOpen = false;
					break;

				case BluetoothAdapter.STATE_TURNING_OFF:
					Log.e("BLE", "BluetoothAdapter.STATE_TURNING_OFF");
					break;

				case BluetoothAdapter.STATE_ON:
					Log.e("BLE", "BluetoothAdapter.STATE_ON");
                    mBtStateOpen = true;
                    Log.i(TAG, "myThread111");
                    myThread = new MyThread();
                    myThread.start();
					//populateDeviceList(mBtStateOpen);
					break;

				case BluetoothAdapter.STATE_TURNING_ON:
					Log.e("BLE", "BluetoothAdapter.STATE_TURNING_ON");
					break;
				}
			}
		}
	};

	private static IntentFilter makeGattUpdateIntentFilter() {
		Log.e("BLE", "makeGattUpdateIntentFilter");
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
		intentFilter.addAction(UartService.ACTION_MAKESURE_DOOROPENED);
		return intentFilter;
	}
	
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
	
	private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			Log.e("BLE", "UARTStatusChangeReceiver");
			String action = intent.getAction();

			if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
				Log.e("test", "UartService.ACTION_GATT_CONNECTED");
//				getActivity().runOnUiThread(new Runnable() {
//					public void run() {
//					}
//				});
			}
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                Log.e("test", "UartService.ACTION_GATT_SERVICES_DISCOVERED");
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if (mUartService != null) {
                            mUartService.readRXCharacteristic(mUartService.RX_CHAR_UUID);
                        }
                    }
                });
            }
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                Log.e("BLE", "UartService.ACTION_GATT_DISCONNECTED");
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
//						IvOpenDoorLogo.setEnabled(true);
                        // mUartService.disconnect();
                        mUartService.close();
                        if (mOpenDoorState != 0) {
							if (!mDoorState) {
								Toast.makeText(getActivity(), R.string.open_door_fail, Toast.LENGTH_SHORT).show();
							}
                            Log.e("test for open door", "Gatt close");
                            mOpenDoorState = 0;
							mDoorState = false;
                        }
                    }
                });
            }
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                Log.e("test", "UartService.ACTION_GATT_SERVICES_DISCOVERED");
                mUartService.enableTXNotification();
            }
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
                Log.e("test", "UartService.ACTION_DATA_AVAILABLE");

                @SuppressWarnings("unused")
                final byte[] txValue = intent
                        .getByteArrayExtra(UartService.EXTRA_DATA);
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if (mUartService != null) {
                            String message = new String(
                                    Character.toChars(new Random()
                                            .nextInt(90 - 65) + 65));
                            try {
                                byte[] value = message.getBytes("UTF-8");
                                mUartService.writeRXCharacteristic(value);
                            } catch (Exception e) {
                            }
                        }
                    }
                });
            }
            if (action.equals(UartService.ACTION_MAKESURE_DOOROPENED)) {//new add for response
                Log.e("test", "UartService.ACTION_MAKESURE_DOOROPENED");
                final byte[] txValue = intent
                        .getByteArrayExtra(UartService.EXTRA_DATA);
//                getActivity().runOnUiThread(new Runnable() {
//					public void run() {
                if (txValue[0] == 0x10) {
                    vibrator.vibrate(500);
                    // door had opened. go on ...
                    Toast.makeText(getActivity(), R.string.open_door_success, Toast.LENGTH_SHORT).show();
                    mDoorState = true;
//                                        new Handler().postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Log.e("BLE", "door can open again");
//                                                mOpenDoorState = 0;
//                                            }
//                                        }, 3000);

                }
//					}
//                });
            }
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                Log.e("BLE", "UartService.DEVICE_DOES_NOT_SUPPORT_UART");
                mUartService.disconnect();
            }
		}
	};

    @Override
    public void onStop() {
        super.onStop();
        vibrator.cancel();
        
//        if (sensorManager != null) {
//            sensorManager.unregisterListener(sensorEventListener);
//        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
				IBinder rawBinder) {
			Log.e("BLE", "onServiceConnected");
			mUartService = ((UartService.LocalBinder) rawBinder).getService();
			if (!mUartService.initialize()) {
				getActivity().finish();
			}

		}

		public void onServiceDisconnected(ComponentName classname) {
			Log.e("BLE", "onServiceDisconnected");
			mUartService = null;

		}
	};

	private class DeviceAdapter extends BaseAdapter {
		private Context context;
		private List<BluetoothDevice> devices;

		public DeviceAdapter(Context context, List<BluetoothDevice> devices) {
			this.context = context;
			this.devices = devices;
		}

		@Override
		public int getCount() {
			int ret = 0;
			if (devices != null && devices.size() > 0) {
				ret = devices.size();
			}
			return ret;
		}

		@Override
		public Object getItem(int position) {
			return devices.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return null;
		}

	}

	public void playOpenDoorSound() {
		mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		mSoundPool.play(mSoundPool.load(getActivity(), R.raw.ring, 0), 1, 1, 0,
				0, 1);
	}

//	private boolean hasData(SQLiteDatabase mDB, String str){
//		boolean hasData = false;
//		Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME,null);
//		
//		if(mCursor.moveToFirst()){
//			int deviceIdIndex = mCursor.getColumnIndex("deviceId");
//			do{
//				 String deviceId = mCursor.getString(deviceIdIndex);
//				 
//				 if(deviceId.equals(str)) {
//					 hasData = true;
//					 break;
//				 }
//				 
//			}while(mCursor.moveToNext());
//		}
//        mCursor.close();
//		return hasData;
//	}
	
	private boolean hasData(SQLiteDatabase mDB, String str){
		boolean hasData = false;
		Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME,null);
		
		if(mCursor.moveToFirst()){
			int deviceIdIndex = mCursor.getColumnIndex("deviceId");
			do{
				 String deviceId = mCursor.getString(deviceIdIndex);
				 
				 if(deviceId.equals(str)) {
					 hasData = true;
					 break;
				 }
				 
			}while(mCursor.moveToNext());
		}
		mCursor.close();
		return hasData;
	}
	
	private boolean hasZoneData(SQLiteDatabase mDB, String str){
		boolean hasData = false;
		Cursor mCursor = mKeyDB.rawQuery("select * from " + ZONE_TABLE_NAME, null);
		
		if(mCursor.moveToFirst()){
			int zoneidIndex = mCursor.getColumnIndex("zoneid");
			do{
				 String zoneid = mCursor.getString(zoneidIndex);
				 
				 if(zoneid.equals(str)) {
					 hasData = true;
					 break;
				 }
				 
			}while(mCursor.moveToNext());
		}
		mCursor.close();
		return hasData;
	}
	
	private boolean hasCarData(SQLiteDatabase mDB, String str, String str1){
		boolean hasData = false;
		Cursor mCursor = mKeyDB.rawQuery("select * from " + CAR_TABLE_NAME, null);
		
		if(mCursor.moveToFirst()){
			int l1ZoneIdIndex = mCursor.getColumnIndex("l1ZoneId");
			int plateNumIndex = mCursor.getColumnIndex("plateNum");
			do{
				 String l1ZoneId = mCursor.getString(l1ZoneIdIndex);
				 String plateNum = mCursor.getString(plateNumIndex);
				 
				 if(l1ZoneId.equals(str) && plateNum.equals(str1)) {
					 hasData = true;
					 break;
				 }
				 
			}while(mCursor.moveToNext());
		}
		mCursor.close();
		return hasData;
	}
	
//	private long DBCount() {
//		String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
//		SQLiteStatement statement = mKeyDB.compileStatement(sql);
//		long count = statement.simpleQueryForLong();
//		return count;
//	}
//	
//	private long DBCountCar() {  
//	    String sql = "SELECT COUNT(*) FROM " + CAR_TABLE_NAME;
//	    SQLiteStatement statement = mKeyDB.compileStatement(sql);
//	    long count = statement.simpleQueryForLong();
//	    return count;
//	}
	
	private long DBCount() {  
	    String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
	    SQLiteStatement statement = mKeyDB.compileStatement(sql);
	    long count = statement.simpleQueryForLong();
	    return count;
	}
	
	private long DBCountZone() {  
	    String sql = "SELECT COUNT(*) FROM " + ZONE_TABLE_NAME;
	    SQLiteStatement statement = mKeyDB.compileStatement(sql);
	    long count = statement.simpleQueryForLong();
	    return count;
	}
	
	private long DBCountCar() {  
	    String sql = "SELECT COUNT(*) FROM " + CAR_TABLE_NAME;
	    SQLiteStatement statement = mKeyDB.compileStatement(sql);
	    long count = statement.simpleQueryForLong();
	    return count;
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
        if (getActivity() != null) {
            SharedPreferences savedSid = getActivity().getSharedPreferences("SAVEDSID", 0);
            Editor editor = savedSid.edit();
            editor.putString("SID", sid);
            editor.commit();
        }
	}

	public String loadSid() {
		SharedPreferences loadSid = getActivity().getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}
	
	public void saveUUID(String uuid){	
		if(getActivity() != null) {
			SharedPreferences savedUUID = getActivity().getSharedPreferences("SAVEDUUID", 0);
			Editor editor = savedUUID.edit();
			editor.putString("UUID", uuid);
			editor.commit();
		}		
	}
	
	public String loadUUID(){
		SharedPreferences loadUUID = getActivity().getSharedPreferences("SAVEDUUID", 0);
		return loadUUID.getString("UUID", null);
	}
	
	/*
	 * for new shake
	 */
//    private SensorEventListener sensorEventListener = new SensorEventListener() {
//
//        @Override
//        public void onSensorChanged(SensorEvent event) {
//            float[] values = event.values;
//            float x = values[0];
//            float y = values[1];
//            float z = values[2];
//            int medumValue = 19;
//        if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
//                Message msg = new Message();
//                msg.what = SENSOR_SHAKE;
//                handler.sendMessage(msg);
//            }
//        }
//
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//        }
//    };
 
    /**
     * when shake, do something
     */ 
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//            case SENSOR_SHAKE:
//            	if(mBtStateOpen == false){
//					Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//		            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//				}else {
//					 if (mOpenDoorState == 0) {
//		                    mOpenDoorState = 1;
//		                    Log.i("test", "doOpenDoor");
//
//		                    if(canShake == 1){
//		                    	doOpenDoor(mBtStateOpen);
//		                    }
//		                }
//				}
//                break;
//            }
//        }
//
//    };
}
