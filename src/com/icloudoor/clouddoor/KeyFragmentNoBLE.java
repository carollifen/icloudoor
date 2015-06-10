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

	private int COLOR_CHANNEL_CHOOSE = 0xFF010101;
	private int COLOR_CHANNEL_NOT_CHOOSE = 0xFF999999;

	private int canDisturb;
	private int haveSound;
	private int canShake;
	private boolean isFindKey;

	private float alpha_transparent = 0.0f;
	private float alpha_opaque = 1.0f;
	
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
	
	// for new channel switch
	private SwitchButton switchBtn;
	private int isChooseCarChannel = 1;
	private boolean onlyOneDoor = false;
	private TextView doorName;
	private TextView scanStatus;
	private ImageView BtnOpenDoor;
	private OpenDoorRingView ringView;
	
	private RelativeLayout circleLayout;
    private ImageView circle;
    private ImageView radar;

	public KeyFragmentNoBLE() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.key_page, container, false);
		
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
		keyRedDot.setVisibility(View.INVISIBLE); 
		
		requestWeatherData();
		
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
					break;
				case MotionEvent.ACTION_UP: 
					break;
				}
				return false;
			}
			
		});
		
		scanStatus = (TextView) view.findViewById(R.id.scan_status);
		scanStatus.setText(R.string.can_not_use_open_door);

		return view;
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
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.e("TEST", "keyFragment onResume()");
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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
		SharedPreferences savedSid = getActivity().getSharedPreferences("SAVEDSID", 0);
		Editor editor = savedSid.edit();
		editor.putString("SID", sid);
		editor.commit();
	}

	public String loadSid() {
		SharedPreferences loadSid = getActivity().getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}
}
