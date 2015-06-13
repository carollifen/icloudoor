package com.icloudoor.cloudoor;

import java.util.Calendar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WeatherDetail extends Activity {
	
	private static int[] weatherIcons = new int[] { R.drawable.sunny,
		R.drawable.clear, R.drawable.fair, R.drawable.fair1,
		R.drawable.cloudy, R.drawable.party_cloudy,
		R.drawable.party_cloudy1, R.drawable.mostly_cloudy,
		R.drawable.mostly_cloudy1, R.drawable.overcast, R.drawable.shower,
		R.drawable.thundershower, R.drawable.thundershower_with_hail,
		R.drawable.light_rain, R.drawable.moderate_rain,
		R.drawable.heavy_rain, R.drawable.storm, R.drawable.heavy_storm,
		R.drawable.severe_storm, R.drawable.ice_rain, R.drawable.sleet,
		R.drawable.snow_flurry, R.drawable.light_snow,
		R.drawable.moderate_snow, R.drawable.heavy_snow,
		R.drawable.snowstorm, R.drawable.dust, R.drawable.sand,
		R.drawable.duststorm, R.drawable.sandstorm, R.drawable.foggy,
		R.drawable.haze, R.drawable.windy, R.drawable.blustery,
		R.drawable.hurricane, R.drawable.tropical_storm,
		R.drawable.tornado, R.drawable.cold, R.drawable.hot ,R.drawable.unknown_weather};
	
	private RelativeLayout back;
	private RelativeLayout other;

	private TextView cityName;
	private TextView day1Temp;
	private TextView day1Weather;
	private TextView day1Week;
	private ImageView day1Icon;
	private TextView day1Yi;
	private TextView day1Ji;
	
	private TextView day2Week;
	private TextView day2High;
	private TextView day2Low;
	private ImageView day2Icon;
	
	private TextView day3Week;
	private TextView day3High;
	private TextView day3Low;
	private ImageView day3Icon;
	
	public char centigrade = 176;
	public final Calendar c = Calendar.getInstance();
	
	private Broadcast mFinishActivityBroadcast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_detail);
		
		mFinishActivityBroadcast=	new Broadcast();
		 IntentFilter intentFilter = new IntentFilter();
		    intentFilter.addAction("com.icloudoor.clouddoor.ACTION_FINISH");
		    registerReceiver(mFinishActivityBroadcast, intentFilter);


		
		back = (RelativeLayout) findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		other = (RelativeLayout) findViewById(R.id.other);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) other.getLayoutParams();
		lp.width = screenWidth - 50 * 2;
		other.setLayoutParams(lp);
		
		initViews();
		
		SharedPreferences savedLHL = getSharedPreferences("SAVEDLHL", 0);
		SharedPreferences savedWeather = getSharedPreferences("SAVEDWEATHER", 0);
		
		cityName.setText(savedWeather.getString("City", "N/A"));
		day1Temp.setText(savedWeather.getString("Day1Temp", "N/A") + String.valueOf(centigrade));
		day1Weather.setText(savedWeather.getString("Day1Weather", "N/A"));
		day1Week.setText(getString(R.string.week) + getWeek(c.get(Calendar.DAY_OF_WEEK)) + "    ½ñÌì");		
		if(savedWeather.getString("Day1IconIndex", "99").equals("99")){
			day1Icon.setBackgroundResource(weatherIcons[39]);
		} else {
			day1Icon.setBackgroundResource(weatherIcons[Integer.parseInt(savedWeather.getString("Day1IconIndex", "99"))]);
		}

		day1Yi.setText(savedLHL.getString("D1YI", "N/A"));
		day1Ji.setText(savedLHL.getString("D1JI", "N/A"));
		
		day2Week.setText(getString(R.string.week) + getWeek(c.get(Calendar.DAY_OF_WEEK)+1));
		day2High.setText(savedWeather.getString("Day2TempHigh", "N/A") + String.valueOf(centigrade));
		day2Low.setText(savedWeather.getString("Day2TempLow", "N/A") + String.valueOf(centigrade));
		if(savedWeather.getString("Day2IconIndexDay", "99").equals("99")){
			day2Icon.setBackgroundResource(weatherIcons[39]);
		} else {
			day2Icon.setBackgroundResource(weatherIcons[Integer.parseInt(savedWeather.getString("Day2IconIndexDay", "99"))]);
		}
		
		day3Week.setText(getString(R.string.week) + getWeek(c.get(Calendar.DAY_OF_WEEK)+2));
		day3High.setText(savedWeather.getString("Day3TempHigh", "N/A") + String.valueOf(centigrade));
		day3Low.setText(savedWeather.getString("Day3TempLow", "N/A") + String.valueOf(centigrade));
		if(savedWeather.getString("Day3IconIndexDay", "99").equals("99")){
			day3Icon.setBackgroundResource(weatherIcons[39]);
		} else {
			day3Icon.setBackgroundResource(weatherIcons[Integer.parseInt(savedWeather.getString("Day3IconIndexDay", "99"))]);
		}
	}
	
	public void initViews() {
		cityName = (TextView) findViewById(R.id.city_name);
		day1Temp = (TextView) findViewById(R.id.temp_day1);
		day1Weather = (TextView) findViewById(R.id.weather_day1);
		day1Week = (TextView) findViewById(R.id.date_day1);
		day1Icon = (ImageView) findViewById(R.id.icon_day1);
		day1Yi = (TextView) findViewById(R.id.content_yi);
		day1Ji = (TextView) findViewById(R.id.content_ji);
		
		day2Week = (TextView) findViewById(R.id.date_day2);
		day2High = (TextView) findViewById(R.id.day2_high);
		day2Low = (TextView) findViewById(R.id.day2_low);
		day2Icon = (ImageView) findViewById(R.id.icon_day2);
		
		day3Week = (TextView) findViewById(R.id.date_day3);
		day3High = (TextView) findViewById(R.id.day3_high);
		day3Low = (TextView) findViewById(R.id.day3_low);
		day3Icon = (ImageView) findViewById(R.id.icon_day3);
	}
	
	public String getWeek(int i){
		String week = null;
		if(i > 7) i = i - 7;
		if(i == 1){
			week = getString(R.string.sunday);
		}else if(i == 2){
			week = getString(R.string.monday);
		}else if(i == 3){
			week = getString(R.string.tuesday);
		}else if(i == 4){
			week = getString(R.string.wednesday);
		}else if(i == 5){
			week = getString(R.string.thursday);
		}else if(i == 6){
			week = getString(R.string.friday);
		}else if(i == 7){
			week = getString(R.string.saturday);
		}
		return week;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mFinishActivityBroadcast);
		
	}
	
	class Broadcast extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			WeatherDetail.this.finish();
		}
		
	}
	
}
