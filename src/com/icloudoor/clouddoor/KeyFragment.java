package com.icloudoor.clouddoor;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class KeyFragment extends Fragment {
	private RelativeLayout channelSwitch;
	private RelativeLayout keyWidge;

	private TextView TvChooseCar;
	private TextView TvChooseMan;
	private TextView TvDistrictDoor;
	private TextView TvCarNumber;
	private TextView TvOpenKeyList;

	private ImageView IvChooseCar;
	private ImageView IvChooseMan;
	private ImageView IvSearchKey;
	private ImageView IvOpenDoorLogo;
	private ImageView IvWeatherWidgePush1;
	private ImageView IvWeatherWidgePush2;

	private int COLOR_CHANNEL_CHOOSE = 0xFF00354a;
	private int COLOR_CHANNEL_NOT_CHOOSE = 0xFFb5b5b5;

	private boolean isChooseCarChannel;
	private boolean isFindKey;

	private float alpha_transparent = 0.0f;
	private float alpha_opaque = 1.0f;
	
	private ViewPager mWeatherWidgePager;
	private ArrayList<Fragment> mKeyPageFragmentList;
	private KeyPageAdapter mKeyPageAdapter;
	private WeatherWidgeFragment mWeatherWidgeFragment;
	private WeatherWidgeFragment2 mWeatherWidgeFragment2;
	public FragmentManager mFragmentManager;
	public MyPageChangeListener myPageChangeListener;

	public KeyFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.key_page, container, false);

		channelSwitch = (RelativeLayout) view.findViewById(R.id.channel_switch);
		keyWidge = (RelativeLayout) view.findViewById(R.id.key_widge);
		
		TvChooseCar = (TextView) view.findViewById(R.id.Tv_choose_car);
		TvChooseMan = (TextView) view.findViewById(R.id.Tv_choose_man);
		TvDistrictDoor = (TextView) view.findViewById(R.id.district_door);
		TvCarNumber = (TextView) view.findViewById(R.id.car_number);
		TvOpenKeyList = (TextView) view.findViewById(R.id.open_key_list);
		
		IvChooseCar = (ImageView) view.findViewById(R.id.Iv_choose_car);
		IvChooseMan = (ImageView) view.findViewById(R.id.Iv_choose_man);
		IvSearchKey = (ImageView) view.findViewById(R.id.Iv_search_key);
		IvOpenDoorLogo = (ImageView) view.findViewById(R.id.Iv_open_door_logo);
		IvWeatherWidgePush1 = (ImageView) view.findViewById(R.id.Iv_weather_widge_push1);
		IvWeatherWidgePush2 = (ImageView) view.findViewById(R.id.Iv_weather_widge_push2);

		mFragmentManager = getChildFragmentManager();
		mWeatherWidgePager = (ViewPager) view.findViewById(R.id.weather_widge_pager);
		myPageChangeListener = new MyPageChangeListener();
		InitViewPager();
		InitFragmentViews();

		channelSwitch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isChooseCarChannel) {
					TvChooseCar.setTextColor(COLOR_CHANNEL_CHOOSE);
					TvChooseMan.setTextColor(COLOR_CHANNEL_NOT_CHOOSE);
					IvChooseCar.setAlpha(alpha_opaque);
					IvChooseMan.setAlpha(alpha_transparent);
					isChooseCarChannel = false;
				} else {
					TvChooseCar.setTextColor(COLOR_CHANNEL_NOT_CHOOSE);
					TvChooseMan.setTextColor(COLOR_CHANNEL_CHOOSE);
					IvChooseCar.setAlpha(alpha_transparent);
					IvChooseMan.setAlpha(alpha_opaque);
					isChooseCarChannel = true;
				}
			}

		});
		keyWidge.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!isFindKey) {
					TvDistrictDoor.setText(R.string.searching_key);
					TvDistrictDoor.setTextSize(18);
					TvDistrictDoor.setTextColor(0xFFffffff);
					TvCarNumber.setText(R.string.can_shake_to_open_door);
					TvCarNumber.setTextSize(12);
					TvCarNumber.setTextColor(0xFF7d7d7d);
					IvSearchKey.setImageResource(R.drawable.btn_background_gray);
					IvOpenDoorLogo.setImageResource(R.drawable.btn_serch_1);
					isFindKey = true;
				}else{
					TvDistrictDoor.setText("Ωı–Âœ„Ω≠±±√≈");
					TvDistrictDoor.setTextSize(18);
					TvDistrictDoor.setTextColor(0xFFffffff);
					TvCarNumber.setText("‘¡A XXXXX");
					TvCarNumber.setTextSize(18);
					TvCarNumber.setTextColor(0xFFffffff);
					IvSearchKey.setImageResource(R.drawable.btn_background_blue);
					IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
					isFindKey = false;
				}
			}
			
		});
		TvOpenKeyList.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), KeyList.class);
				startActivity(intent);
			}
			
		});
		return view;
	}

	public void InitFragmentViews() {
		isChooseCarChannel = true;
		isFindKey = false;

		TvChooseCar.setTextColor(COLOR_CHANNEL_CHOOSE);
		TvChooseMan.setTextColor(COLOR_CHANNEL_NOT_CHOOSE);
		IvChooseCar.setAlpha(alpha_opaque);
		IvChooseMan.setAlpha(alpha_transparent);
		
		TvDistrictDoor.setText(R.string.searching_key);
		TvDistrictDoor.setTextSize(18);
		TvDistrictDoor.setTextColor(0xFFffffff);
		TvCarNumber.setText(R.string.can_shake_to_open_door);
		TvCarNumber.setTextSize(12);
		TvCarNumber.setTextColor(0xFF7d7d7d);
		
		IvSearchKey.setImageResource(R.drawable.btn_background_gray);
		IvOpenDoorLogo.setImageResource(R.drawable.btn_serch_1);
		
		IvWeatherWidgePush1.setImageResource(R.drawable.push_current);
		IvWeatherWidgePush2.setImageResource(R.drawable.push_next);
	}

	
	public void InitViewPager(){
		mKeyPageFragmentList = new ArrayList<Fragment>();
		
		mWeatherWidgeFragment = new WeatherWidgeFragment();
		mWeatherWidgeFragment2 = new WeatherWidgeFragment2();
		
		mKeyPageFragmentList.add(mWeatherWidgeFragment);
		mKeyPageFragmentList.add(mWeatherWidgeFragment2);
		
		mKeyPageAdapter = new KeyPageAdapter(mFragmentManager, mKeyPageFragmentList);
		mWeatherWidgePager.setAdapter(mKeyPageAdapter);
		mWeatherWidgePager.setCurrentItem(0);
		mWeatherWidgePager.setOnPageChangeListener(myPageChangeListener);
	}
	
	public class MyPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			if (arg0 == 2) {
				int index = mWeatherWidgePager.getCurrentItem();
				mWeatherWidgePager.setCurrentItem(index);
				if(index == 0){
					IvWeatherWidgePush1.setImageResource(R.drawable.push_current);
					IvWeatherWidgePush2.setImageResource(R.drawable.push_next);
				}else if(index == 1){
					IvWeatherWidgePush2.setImageResource(R.drawable.push_current);
					IvWeatherWidgePush1.setImageResource(R.drawable.push_next);
				}
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
