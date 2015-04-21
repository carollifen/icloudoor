package com.icloudoor.clouddoor;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class WeatherWidgeFragment extends Fragment {
	
	private TextView City;
	private TextView Date;
	private TextView Week;
	private TextView Temp;
	private TextView Day1;
	private TextView Day2;
	private TextView Day3;
	private TextView Day1Bg;
	private TextView Day2Bg;
	private TextView Day3Bg;
	private TextView YiContent;
	private TextView JiContent;
	private ImageView WeatherIcon;
	private MyClick myClick;
	
	public final Calendar c =  Calendar.getInstance();
	
	public char centigrade = 176;
	
	// 获取经纬度
	private LocationManager locationManager;
	private double longitude = 0.0;
	private double latitude = 0.0;
	//心知天气
	private String HOST = "https://api.thinkpage.cn/v2/weather/all.json?";
	private URL weatherURL;
	private String Key = "XSI7AKYYBY";
	private RequestQueue mQueue;
	//聚合老黄历
	private String HOST_Laohuangli = "http://v.juhe.cn/laohuangli/d?";
	private URL laohuangliRequestURL;
	private String laohuangliKey = "ce9652a2ea10b061e3e479606c5529ca";
	
	private long mLastRequestTime;
	private long mCurrentRequestTime;
	
	private static int[] weatherIcons = new int[]{R.drawable.sunny, R.drawable.clear, R.drawable.fair, R.drawable.fair1, R.drawable.cloudy, R.drawable.party_cloudy
		, R.drawable.party_cloudy1, R.drawable.mostly_cloudy, R.drawable.mostly_cloudy1, R.drawable.overcast, R.drawable.shower, R.drawable.thundershower
		, R.drawable.thundershower_with_hail, R.drawable.light_rain, R.drawable.moderate_rain, R.drawable.heavy_rain, R.drawable.storm, R.drawable.heavy_storm
		, R.drawable.severe_storm, R.drawable.ice_rain, R.drawable.sleet, R.drawable.snow_flurry, R.drawable.light_snow, R.drawable.moderate_snow
		, R.drawable.heavy_snow, R.drawable.snowstorm, R.drawable.dust, R.drawable.sand, R.drawable.duststorm, R.drawable.sandstorm, R.drawable.foggy
		, R.drawable.haze, R.drawable.windy, R.drawable.blustery, R.drawable.hurricane, R.drawable.tropical_storm, R.drawable.tornado, R.drawable.cold, R.drawable.hot};
		
	public WeatherWidgeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
				
		View view = inflater.inflate(R.layout.fragment_weather_widge, container,
				false);
		
		myClick = new MyClick();
		
		City = (TextView) view.findViewById(R.id.weather_city);
		Date = (TextView) view.findViewById(R.id.weather_date);
		Week = (TextView) view.findViewById(R.id.weather_week);
		Temp = (TextView) view.findViewById(R.id.weather_temperature);	
		Day1 = (TextView) view.findViewById(R.id.weather_day_now);
		Day2 = (TextView) view.findViewById(R.id.weather_day_after);
		Day3 = (TextView) view.findViewById(R.id.weather_day_after_after);
		Day1Bg = (TextView) view.findViewById(R.id.weather_day_now_color);
		Day2Bg = (TextView) view.findViewById(R.id.weather_day_after_color);
		Day3Bg = (TextView) view.findViewById(R.id.weather_day_after_after_color);
		YiContent = (TextView) view.findViewById(R.id.weather_content_yi);
		JiContent = (TextView) view.findViewById(R.id.weather_content_ji);
		WeatherIcon = (ImageView) view.findViewById(R.id.weather_icon);
		
		Day1Bg.setOnClickListener(myClick);
		Day2Bg.setOnClickListener(myClick);
		Day3Bg.setOnClickListener(myClick);
		
		// To get the longitude and latitude -- 经度，纬度
		locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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
		
		// INIT -- To get the current date
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = sDateFormat.format(new java.util.Date());
		
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00")); 
	
		Date.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" 
				+ String.valueOf(c.get(Calendar.MONTH) + 1) + "月" 
				+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + "日");	
		Week.setText("星期" + getWeek(c.get(Calendar.DAY_OF_WEEK)));
		Day1.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
		
		if(isBigMonth(c.get(Calendar.MONTH) + 1)){                 //大月
			if(c.get(Calendar.DAY_OF_MONTH) == 31){
				Day2.setText(String.valueOf((c.get(Calendar.DAY_OF_MONTH)+1)%31));
				Day3.setText(String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%31));
			}else if(c.get(Calendar.DAY_OF_MONTH) == 30){
				Day2.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1));
				Day3.setText(String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%31));
			}else{
				Day2.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1));
				Day3.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)+2));
			}
		}else if(isSmallMonth(c.get(Calendar.MONTH) + 1)){      //小月
			if(c.get(Calendar.DAY_OF_MONTH) == 30){
				Day2.setText(String.valueOf((c.get(Calendar.DAY_OF_MONTH)+1)%30));
				Day3.setText(String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%30));
			}else if(c.get(Calendar.DAY_OF_MONTH) == 29){
				Day2.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1));
				Day3.setText(String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%30));
			}else{
				Day2.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1));
				Day3.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)+2));
			}
		}else if(isLeapYear(c.get(Calendar.MONTH) + 1)) {       //闰年2月  
			if(c.get(Calendar.DAY_OF_MONTH) == 29){
				Day2.setText(String.valueOf((c.get(Calendar.DAY_OF_MONTH)+1)%29));
				Day3.setText(String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%29));
			}else if(c.get(Calendar.DAY_OF_MONTH) == 28){
				Day2.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1));
				Day3.setText(String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%29));
			}else{
				Day2.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1));
				Day3.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)+2));
			}
		}else if(!(isLeapYear(c.get(Calendar.MONTH) + 1))){     //非闰年2月 
			if(c.get(Calendar.DAY_OF_MONTH) == 28){
				Day2.setText(String.valueOf((c.get(Calendar.DAY_OF_MONTH)+1)%28));
				Day3.setText(String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%28));
			}else if(c.get(Calendar.DAY_OF_MONTH) == 27){
				Day2.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1));
				Day3.setText(String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%28));
			}else{
				Day2.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1));
				Day3.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)+2));
			}
		}
				
		mQueue = Volley.newRequestQueue(getActivity());
			
		try {
			weatherURL = new URL(HOST + "city=" + String.valueOf(latitude) + ":" + String.valueOf(longitude)
					+ "&language=zh-chs&unit=c&aqi=city&key=" + Key);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		JsonObjectRequest mWeatherRequest = new JsonObjectRequest(
				Method.GET, weatherURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.e("Test", response.toString());
						try {
							if(response.getString("status").equals("OK")){
								JSONArray weather= response.getJSONArray("weather");
								JSONObject data = (JSONObject)weather.get(0);							
								JSONObject now = data.getJSONObject("now");
								JSONArray future = data.getJSONArray("future");
								JSONObject tomorrow= (JSONObject)future.get(0);	
								JSONObject tomorrow2= (JSONObject)future.get(1);	
								
								//保存以供下次使用
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
								
								City.setText(data.getString("city_name"));
								Temp.setText(now.getString("temperature") + String.valueOf(centigrade));
								WeatherIcon.setImageResource(weatherIcons[Integer.parseInt(now.getString("code"))]);													
							} else {
								
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
		};
		mLastRequestTime = loadLastRequestTime();
		mCurrentRequestTime = System.currentTimeMillis();
		if((mCurrentRequestTime - mLastRequestTime)/1000 >= 10800){
			saveLastRequestTime(mCurrentRequestTime);
			mQueue.add(mWeatherRequest);
		}else{
			SharedPreferences loadWeather = getActivity().getSharedPreferences("SAVEDWEATHER",
					0);
			
			City.setText(loadWeather.getString("City", null));
			Temp.setText(loadWeather.getString("Day1Temp", null) + String.valueOf(centigrade));		
			WeatherIcon.setImageResource(weatherIcons[Integer.parseInt(loadWeather.getString("Day1IconIndex", "0"))]);
		}
		
		return view;
	}
	
	//TODO
	public class MyClick implements OnClickListener{
		SharedPreferences loadWeather = getActivity().getSharedPreferences("SAVEDWEATHER",
				0);
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.weather_day_now_color:
				Day1Bg.setBackgroundResource(R.drawable.home_btn_recommended);
				Day2Bg.setBackgroundResource(R.drawable.home_btn_recommended_default);
				Day3Bg.setBackgroundResource(R.drawable.home_btn_recommended_default);
				
				Date.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" 
						+ String.valueOf(c.get(Calendar.MONTH) + 1) + "月" 
						+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + "日");	
				Week.setText("星期" + getWeek(c.get(Calendar.DAY_OF_WEEK)));
				
				Temp.setText(loadWeather.getString("Day1Temp", null) + String.valueOf(centigrade));
				
				WeatherIcon.setImageResource(weatherIcons[Integer.parseInt(loadWeather.getString("Day1IconIndex", "0"))]);
				
				break;
			case R.id.weather_day_after_color:
				Day2Bg.setBackgroundResource(R.drawable.home_btn_recommended);
				Day1Bg.setBackgroundResource(R.drawable.home_btn_recommended_default);
				Day3Bg.setBackgroundResource(R.drawable.home_btn_recommended_default);
				
				if(isBigMonth(c.get(Calendar.MONTH) + 1)){                 //大月
					if(c.get(Calendar.DAY_OF_MONTH) == 31){
						Date.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" 
								+ String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + "月" 
								+ String.valueOf(1) + "日");
					}else{
						Date.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" 
								+ String.valueOf(c.get(Calendar.MONTH) + 1) + "月" 
								+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1) + "日");	
					}
				}else if(isSmallMonth(c.get(Calendar.MONTH) + 1)){      //小月
					if(c.get(Calendar.DAY_OF_MONTH) == 30){
						Date.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" 
								+ String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + "月" 
								+ String.valueOf(1) + "日");
					}else{
						Date.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" 
								+ String.valueOf(c.get(Calendar.MONTH) + 1) + "月" 
								+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1) + "日");
					}
				}else if(isLeapYear(c.get(Calendar.MONTH) + 1)) {       //闰年2月  
					if(c.get(Calendar.DAY_OF_MONTH) == 29){
						Date.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" 
								+ String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + "月" 
								+ String.valueOf(1) + "日");
					}else{
						Date.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" 
								+ String.valueOf(c.get(Calendar.MONTH) + 1) + "月" 
								+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1) + "日");
					}
				}else if(!(isLeapYear(c.get(Calendar.MONTH) + 1))){     //非闰年2月 
					if(c.get(Calendar.DAY_OF_MONTH) == 28){
						Date.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" 
								+ String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + "月" 
								+ String.valueOf(1) + "日");
					}else{
						Date.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" 
								+ String.valueOf(c.get(Calendar.MONTH) + 1) + "月" 
								+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1) + "日");
					}
				}
				
				Week.setText("星期" + getWeek((c.get(Calendar.DAY_OF_WEEK)+1)%7));
				
				Temp.setText(loadWeather.getString("Day2TempHigh", null) + String.valueOf(centigrade));
				
				WeatherIcon.setImageResource(weatherIcons[Integer.parseInt(loadWeather.getString("Day2IconIndexDay", "0"))]);
				
				break;
			case R.id.weather_day_after_after_color:
				Day3Bg.setBackgroundResource(R.drawable.home_btn_recommended);
				Day1Bg.setBackgroundResource(R.drawable.home_btn_recommended_default);
				Day2Bg.setBackgroundResource(R.drawable.home_btn_recommended_default);
				
				if(isBigMonth(c.get(Calendar.MONTH) + 1)){                 //大月
					if(c.get(Calendar.DAY_OF_MONTH) == 31 && c.get(Calendar.DAY_OF_MONTH) == 30){
						Date.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" 
								+ String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + "月" 
								+ String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%31) + "日");
					}else{
						Date.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" 
								+ String.valueOf(c.get(Calendar.MONTH) + 1) + "月" 
								+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+2) + "日");	
					}
				}else if(isSmallMonth(c.get(Calendar.MONTH) + 1)){      //小月
					if(c.get(Calendar.DAY_OF_MONTH) == 30 && c.get(Calendar.DAY_OF_MONTH) == 29){
						Date.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" 
								+ String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + "月" 
								+ String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%30) + "日");
					}else{
						Date.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" 
								+ String.valueOf(c.get(Calendar.MONTH) + 1) + "月" 
								+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+2) + "日");
					}
				}else if(isLeapYear(c.get(Calendar.MONTH) + 1)) {       //闰年2月  
					if(c.get(Calendar.DAY_OF_MONTH) == 29 && c.get(Calendar.DAY_OF_MONTH) == 28){
						Date.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" 
								+ String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + "月" 
								+ String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%29) + "日");
					}else{
						Date.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" 
								+ String.valueOf(c.get(Calendar.MONTH) + 1) + "月" 
								+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+2) + "日");
					}
				}else if(!(isLeapYear(c.get(Calendar.MONTH) + 1))){     //非闰年2月 
					if(c.get(Calendar.DAY_OF_MONTH) == 28 && c.get(Calendar.DAY_OF_MONTH) == 27){
						Date.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" 
								+ String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + "月" 
								+ String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%28) + "日");
					}else{
						Date.setText(String.valueOf(c.get(Calendar.YEAR)) + "年" 
								+ String.valueOf(c.get(Calendar.MONTH) + 1) + "月" 
								+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+2) + "日");
					}
				}	
				Week.setText("星期" + getWeek((c.get(Calendar.DAY_OF_WEEK)+2)%7));
				
				Temp.setText(loadWeather.getString("Day3TempHigh", null) + String.valueOf(centigrade));
				
				WeatherIcon.setImageResource(weatherIcons[Integer.parseInt(loadWeather.getString("Day3IconIndexDay", "0"))]);
				
				break;
			default:
				break;
			}
		}
		
	}
	
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
				latitude = location1.getLatitude(); // 经度
				longitude = location1.getLongitude(); // 纬度
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
		// Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		// Provider被enable时触发此函数，比如GPS被打开
		@Override
		public void onProviderEnabled(String provider) {
			
		}

		// Provider被disable时触发此函数，比如GPS被关闭
		@Override
		public void onProviderDisabled(String provider) {
			
		}

		// 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				Log.e("Map",
						"Location changed : Lat: " + location.getLatitude()
								+ " Lng: " + location.getLongitude());
				latitude = location.getLatitude(); // 经度
				longitude = location.getLongitude(); // 纬度
			}
		}
	};

	public String getWeek(int i){
		String week = null;
		if(i == 1){
			week ="天";
		}else if(i == 2){
			week ="一";
		}else if(i == 3){
			week ="二";
		}else if(i == 4){
			week ="三";
		}else if(i == 5){
			week ="四";
		}else if(i == 6){
			week ="五";
		}else if(i == 7){
			week ="六";
		}
		return week;
	}
	
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

}
