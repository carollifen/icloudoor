package com.icloudoor.clouddoor;

import com.jauker.widget.BadgeView;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.pm.PackageManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.icloudoor.clouddoor.ShakeEventManager;
import com.icloudoor.clouddoor.UartService;
import com.icloudoor.clouddoor.ShakeEventManager.ShakeListener;

@SuppressLint("NewApi")
public class KeyFragmentNoBLE extends Fragment {


    private RelativeLayout channelSwitch;
    private RelativeLayout keyWidge;

    private TextView TvChooseCar;
    private TextView TvChooseMan;
//    private TextView TvDistrictDoor;
    private TextView TvCarNumber;
    private RelativeLayout TvOpenKeyList;

    private ImageView IvChooseCar;
    private ImageView IvChooseMan;
    private RelativeLayout IvSearchKey;
    private ImageView IvOpenDoorLogo;
    private ImageView IvWeatherWidgePush1;
    private ImageView IvWeatherWidgePush2;

    private int COLOR_CHANNEL_CHOOSE = 0xFF010101;
    private int COLOR_CHANNEL_NOT_CHOOSE = 0xFF999999;

    private int isChooseCarChannel;
    private int canDisturb;
    private int haveSound;
    private int canShake;
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


    public KeyFragmentNoBLE() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.key_page_noble, container, false);

        channelSwitch = (RelativeLayout) view.findViewById(R.id.channel_switch);
        keyWidge = (RelativeLayout) view.findViewById(R.id.key_widge);

        TvChooseCar = (TextView) view.findViewById(R.id.Tv_choose_car);
        TvChooseMan = (TextView) view.findViewById(R.id.Tv_choose_man);
        //TvDistrictDoor = (TextView) view.findViewById(R.id.district_door);
        TvCarNumber = (TextView) view.findViewById(R.id.car_number);
        //TvDistrictDoor.setSelected(true);
        TvCarNumber.setSelected(true);
        TvOpenKeyList = (RelativeLayout) view.findViewById(R.id.open_key_list);

        IvChooseCar = (ImageView) view.findViewById(R.id.Iv_choose_car);
        IvChooseMan = (ImageView) view.findViewById(R.id.Iv_choose_man);
        IvSearchKey = (RelativeLayout) view.findViewById(R.id.Iv_search_key);
        IvOpenDoorLogo = (ImageView) view.findViewById(R.id.Iv_open_door_logo);
        IvWeatherWidgePush1 = (ImageView) view.findViewById(R.id.Iv_weather_widge_push1);
        IvWeatherWidgePush2 = (ImageView) view.findViewById(R.id.Iv_weather_widge_push2);

        mFragmentManager = getChildFragmentManager();
        mWeatherWidgePager = (ViewPager) view.findViewById(R.id.weather_widge_pager);
        myPageChangeListener = new MyPageChangeListener();

        InitFragmentViews();
        InitViewPager();

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


        channelSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChooseCarChannel == 1) {
                    TvChooseCar.setTextColor(COLOR_CHANNEL_NOT_CHOOSE);
                    TvChooseMan.setTextColor(COLOR_CHANNEL_CHOOSE);
                    IvChooseCar.setAlpha(alpha_transparent);
                    IvChooseMan.setAlpha(alpha_opaque);
                    isChooseCarChannel = 0;
                } else {
                    TvChooseCar.setTextColor(COLOR_CHANNEL_CHOOSE);
                    TvChooseMan.setTextColor(COLOR_CHANNEL_NOT_CHOOSE);
                    IvChooseCar.setAlpha(alpha_opaque);
                    IvChooseMan.setAlpha(alpha_transparent);
                    isChooseCarChannel = 1;
                }
            }
        });

        IvOpenDoorLogo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TvOpenKeyList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return view;
    }

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

    public void InitFragmentViews() {
        isFindKey = false;

        SharedPreferences setting = getActivity().getSharedPreferences(
                "SETTING", 0);
        isChooseCarChannel = setting.getInt("chooseCar", 1);
        canDisturb = setting.getInt("disturb", 1);
        haveSound = setting.getInt("sound", 1);
        canShake = setting.getInt("shake", 0);

        if (isChooseCarChannel == 1) {
            TvChooseCar.setTextColor(COLOR_CHANNEL_CHOOSE);
            TvChooseMan.setTextColor(COLOR_CHANNEL_NOT_CHOOSE);
            IvChooseCar.setAlpha(alpha_opaque);
            IvChooseMan.setAlpha(alpha_transparent);
        } else {
            TvChooseCar.setTextColor(COLOR_CHANNEL_NOT_CHOOSE);
            TvChooseMan.setTextColor(COLOR_CHANNEL_CHOOSE);
            IvChooseCar.setAlpha(alpha_transparent);
            IvChooseMan.setAlpha(alpha_opaque);
        }

//        TvDistrictDoor.setText(R.string.searching_key);
//        TvDistrictDoor.setTextSize(18);
//        TvDistrictDoor.setTextColor(0xFFffffff);
//        TvCarNumber.setText(R.string.can_shake_to_open_door);
//        TvCarNumber.setTextColor(0xFFffffff);

        IvSearchKey.setBackgroundResource(R.drawable.btn_gray);
        IvOpenDoorLogo.setImageResource(R.drawable.btn_serch_1);
        IvOpenDoorLogo.setEnabled(false);

        IvWeatherWidgePush1.setImageResource(R.drawable.push_current);
        IvWeatherWidgePush2.setImageResource(R.drawable.push_next);
    }

    public void InitViewPager() {
        mKeyPageFragmentList = new ArrayList<Fragment>();

        mWeatherWidgeFragment = new WeatherWidgeFragment();
        mWeatherWidgeFragment2 = new WeatherWidgeFragment2();

        mKeyPageFragmentList.add(mWeatherWidgeFragment);
        mKeyPageFragmentList.add(mWeatherWidgeFragment2);

        mKeyPageAdapter = new KeyPageAdapter(mFragmentManager,
                mKeyPageFragmentList);
        mWeatherWidgePager.setAdapter(mKeyPageAdapter);
        mWeatherWidgePager.setCurrentItem(0);
        mWeatherWidgePager.setOnPageChangeListener(myPageChangeListener);
    }

    public class MyPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            if (arg0 == 2) {
                int index = mWeatherWidgePager.getCurrentItem();
                mWeatherWidgePager.setCurrentItem(index);
                if (index == 0) {
                    IvWeatherWidgePush1
                            .setImageResource(R.drawable.push_current);
                    IvWeatherWidgePush2.setImageResource(R.drawable.push_next);
                } else if (index == 1) {
                    IvWeatherWidgePush2
                            .setImageResource(R.drawable.push_current);
                    IvWeatherWidgePush1.setImageResource(R.drawable.push_next);
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {

        }

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
