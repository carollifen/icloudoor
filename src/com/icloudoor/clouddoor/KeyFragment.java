package com.icloudoor.clouddoor;

import java.io.IOException;
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
public class KeyFragment extends Fragment implements ShakeListener  {
	
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
	private TextView TvOpenKeyList;

	private ImageView IvChooseCar;
	private ImageView IvChooseMan;
	private ImageView IvSearchKey;
	private ImageView IvOpenDoorLogo;
	private ImageView IvWeatherWidgePush1;
	private ImageView IvWeatherWidgePush2;

	private int COLOR_CHANNEL_CHOOSE = 0xFF00354a;
	private int COLOR_CHANNEL_NOT_CHOOSE = 0xFFb5b5b5;

	private int isChooseCarChannel;
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
	
	//for BLE
	private static final int REQUEST_ENABLE_BT = 0;
	private static final long SCAN_PERIOD = 10000; // ms
	private BluetoothAdapter mBluetoothAdapter;
	private DeviceAdapter mDeviceAdapter;
	private UartService mUartService = null;
	private ShakeEventManager mShakeMgr;
	private List<BluetoothDevice> mDeviceList;
	private Map<String, Integer> mDevRssiValues;	
	private BluetoothGattCharacteristic mNotifyCharacteristic;
	
	private String NameOfDoorToOpen = null;
	private String IdOfDoorToOpen = null;
	
	private SoundPool mSoundPool;
//	private MediaPlayer mMediaPlayer;

	public KeyFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.key_page, container, false);
		
		channelSwitch = (RelativeLayout) view.findViewById(R.id.channel_switch);  // 点击 选择门的类型，车or人 ； 在setting中可以设置常用的门类型
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
		
		InitFragmentViews();
		InitViewPager();
		
		mKeyDBHelper = new MyDataBaseHelper(getActivity(), DATABASE_NAME);
		mKeyDB = mKeyDBHelper.getWritableDatabase();
		
		//BLE
		if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getActivity(), R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        }
		
		IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(mBluetoothStateReceiver, filter);
		
        BluetoothManager mBluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
		        
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), R.string.bt_not_supported, Toast.LENGTH_SHORT).show();
        }
       
        service_init(); 		
        checkBlueToothState();
		
        mShakeMgr = new ShakeEventManager(getActivity());
        mShakeMgr.setListener(KeyFragment.this);
		mShakeMgr.init(getActivity());
		
		carDoorList = new ArrayList<HashMap<String, String>>();
		manDoorList = new ArrayList<HashMap<String, String>>();
		if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
			if (DBCount() > 0) {
				Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME, null);
				if (mCursor.moveToFirst()) {
					int deviceIdIndex = mCursor.getColumnIndex("deviceId");
					int doorNamemIndex = mCursor.getColumnIndex("doorName");
					int CarOrManIndex = mCursor.getColumnIndex("CarOrMan");
					
					do {
						HashMap<String, String> temp = new HashMap<String, String>();
						String deviceId = mCursor.getString(deviceIdIndex);
						String doorName = mCursor.getString(doorNamemIndex);
						String CarOrMan = mCursor.getString(CarOrManIndex);
						
						if(CarOrMan.equals("1")){    //可通行的车门列表，包括deviceId, doorName
							temp.put("CDdeviceid", deviceId);
							temp.put("CDdoorName", doorName);						
							carDoorList.add(temp);
						}else if(CarOrMan.equals("0")){   //可通行的人门列表，包括deviceId, doorName
							temp.put("MDdeviceid", deviceId);
							temp.put("MDdoorName", doorName);						
							manDoorList.add(temp);
						}
					} while (mCursor.moveToNext());
				}
			}
		}
		
		// INIT: attemp to find the door can open
		for (int index = 0; index < mDeviceList.size(); index++) {		
			isFindKey = false;
			String temp = mDeviceList.get(index).getAddress();
			if (isChooseCarChannel == 1) {				
				for (int i = 0; i < carDoorList.size(); i++) {
					if (temp.equals(carDoorList.get(i).get("CDdeviceid"))) {
						IdOfDoorToOpen = temp;
						NameOfDoorToOpen = carDoorList.get(i).get("CDdoorName");
						isFindKey = true;
						IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
				        IvSearchKey.setImageResource(R.drawable.btn_background_blue);
				        TvDistrictDoor.setText(mDeviceList.get(index).getName());
				        TvDistrictDoor.setTextSize(18);
				        TvDistrictDoor.setTextColor(0xFFffffff);
				        TvCarNumber.setText(mDeviceList.get(index).getAddress());
				        TvCarNumber.setTextSize(18);
				        TvCarNumber.setTextColor(0xFFffffff);
						break;
					}
				}
			} else {				
				for (int i = 0; i < manDoorList.size(); i++) {
					if (temp.equals(manDoorList.get(i).get("MDdeviceid"))) {
						IdOfDoorToOpen = temp;
						NameOfDoorToOpen = manDoorList.get(i).get("MDdoorName");
						isFindKey = true;
						IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
				        IvSearchKey.setImageResource(R.drawable.btn_background_blue);
				        TvDistrictDoor.setText(mDeviceList.get(index).getName());
				        TvDistrictDoor.setTextSize(18);
				        TvDistrictDoor.setTextColor(0xFFffffff);
				        TvCarNumber.setText(mDeviceList.get(index).getAddress());
				        TvCarNumber.setTextSize(18);
				        TvCarNumber.setTextColor(0xFFffffff);
						break;
					}
				}
			}
			if (isFindKey){
				break;
			}
		}

		channelSwitch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isChooseCarChannel == 1) {     
					TvChooseCar.setTextColor(COLOR_CHANNEL_CHOOSE);
					TvChooseMan.setTextColor(COLOR_CHANNEL_NOT_CHOOSE);
					IvChooseCar.setAlpha(alpha_opaque);
					IvChooseMan.setAlpha(alpha_transparent);
					isChooseCarChannel = 0;
					
					for (int index = 0; index < mDeviceList.size(); index++) {		
						isFindKey = false;
						String temp = mDeviceList.get(index).getAddress();
						for (int i = 0; i < manDoorList.size(); i++) {
							if (temp.equals(manDoorList.get(i).get("MDdeviceid"))) {
								IdOfDoorToOpen = temp;
								NameOfDoorToOpen = manDoorList.get(i).get("MDdoorName");
								isFindKey = true;
								IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
						        IvSearchKey.setImageResource(R.drawable.btn_background_blue);
						        TvDistrictDoor.setText(mDeviceList.get(index).getName());
						        TvDistrictDoor.setTextSize(18);
						        TvDistrictDoor.setTextColor(0xFFffffff);
						        TvCarNumber.setText(mDeviceList.get(index).getAddress());
						        TvCarNumber.setTextSize(18);
						        TvCarNumber.setTextColor(0xFFffffff);
								break;
							}
						}
						if (isFindKey){
							Log.e("TEST", "got a car door");
							break;
						}
					}					
				} else {     
					TvChooseCar.setTextColor(COLOR_CHANNEL_NOT_CHOOSE);
					TvChooseMan.setTextColor(COLOR_CHANNEL_CHOOSE);
					IvChooseCar.setAlpha(alpha_transparent);
					IvChooseMan.setAlpha(alpha_opaque);
					isChooseCarChannel = 1;
					
					for (int index = 0; index < mDeviceList.size(); index++) {		
						isFindKey = false;
						String temp = mDeviceList.get(index).getAddress();
						for (int i = 0; i < carDoorList.size(); i++) {
							if (temp.equals(carDoorList.get(i).get("CDdeviceid"))) {
								IdOfDoorToOpen = temp;
								NameOfDoorToOpen = carDoorList.get(i).get("CDdoorName");
								isFindKey = true;
								IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
						        IvSearchKey.setImageResource(R.drawable.btn_background_blue);
						        TvDistrictDoor.setText(mDeviceList.get(index).getName());
						        TvDistrictDoor.setTextSize(18);
						        TvDistrictDoor.setTextColor(0xFFffffff);
						        TvCarNumber.setText(mDeviceList.get(index).getAddress());
						        TvCarNumber.setTextSize(18);
						        TvCarNumber.setTextColor(0xFFffffff);
								break;
							}
						}
						if (isFindKey){
							Log.e("TEST", "got a man door");
							break;
						}
					}
				}
			}
		});
				
		IvOpenDoorLogo.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				doOpenDoor();
				playOpenDoorSound();
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
	
    @Override
	 public void onStart() {
		 super.onStart();
	     IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	     filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	     filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
	 }
	
	@Override
	public void onPause() {
       super.onPause();
       scanLeDevice(false);
   }  
    
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    getActivity().unregisterReceiver(mBluetoothStateReceiver);
	    
	    try {
	    	LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(UARTStatusChangeReceiver);
	    } catch(Exception e) {
	    	
	    }
	    getActivity().unbindService(mServiceConnection);
	    mUartService.stopSelf();
	    mUartService = null;
	}
	
	private void service_init() {
        Intent bindIntent = new Intent(getActivity(), UartService.class);
        getActivity().bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

	private void checkBlueToothState() {
		Log.e("BLE", "checkBlueToothState");
		if(mBluetoothAdapter == null) {
	        Toast.makeText(getActivity(), R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
		} else {
			if (mBluetoothAdapter.isEnabled()) {
				if (mBluetoothAdapter.isDiscovering()) {
				} else {
					populateDeviceList();
					Toast.makeText(getActivity(), R.string.bt_enabled,
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	}
	
	private void populateDeviceList() {
		Log.e("BLE", "populateDeviceList");
		mDeviceList = new ArrayList<BluetoothDevice>();
		mDeviceAdapter = new DeviceAdapter(getActivity(),  mDeviceList);
		mDevRssiValues = new HashMap<String, Integer>();
        scanLeDevice(true);
	}
	
	private void scanLeDevice(final boolean enable) {
		Log.e("BLE", "scanLeDevice");
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            new Handler().postDelayed(new Runnable() {
				@Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
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
        
        mDevRssiValues.put(device.getAddress(), rssi);
        if (!deviceFound) {
        	mDeviceList.add(device);
        	mDeviceAdapter.notifyDataSetChanged();
        }
        
    }	
	
//	private void sendOpenDoorSignal() {
//		if(mUartService != null) {
//			
//				mUartService.readRXCharacteristic();
//			
//			
//		}
//	}
//	
	private void doOpenDoor() {
		Log.e("BLE", "doOpenDoor");
		IvOpenDoorLogo.setEnabled(false);
		Toast.makeText(getActivity(), R.string.door_open, Toast.LENGTH_SHORT).show();
		
		if(mDeviceList != null  && mDeviceList.size() > 0) {
			
			int maxRssiIndex = 0;
			int maxRssi = -128;
			
			for(int i = 0; i < mDeviceList.size(); i++) {
				String tempAdd = mDeviceList.get(i).getAddress();
				int tempRssi = mDevRssiValues.get(tempAdd);
				if(tempRssi > maxRssi) {
					maxRssi = tempRssi;
					maxRssiIndex = i;
				}
			}
			
			if(mDeviceList.get(maxRssiIndex).getAddress() != null) {
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
		        mUartService.connect(mDeviceList.get(maxRssiIndex).getAddress());
		        IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
		        IvSearchKey.setImageResource(R.drawable.btn_background_blue);
		        TvDistrictDoor.setText(mDeviceList.get(maxRssiIndex).getName());
		        TvDistrictDoor.setTextSize(18);
		        TvDistrictDoor.setTextColor(0xFFffffff);
		        TvCarNumber.setText(mDeviceList.get(maxRssiIndex).getAddress());
		        TvCarNumber.setTextSize(18);
		        TvCarNumber.setTextColor(0xFFffffff);
		        
		        Toast.makeText(getActivity(), String.valueOf(mDevRssiValues.get(mDeviceList.get(maxRssiIndex).getAddress())), Toast.LENGTH_LONG).show();
			}
			
			
		}
	}
	
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
        	Log.e("BLE", "onLeScan");
        	getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	
                	getActivity().runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                        	  addDevice(device,rssi);
                          }
                      });
                   
                }
            });
        }
    };
	
	public void InitFragmentViews() {
		isFindKey = false;
		
		SharedPreferences setting = getActivity().getSharedPreferences("SETTING", 0);
		isChooseCarChannel = setting.getInt("chooseCar", 1);
		if(isChooseCarChannel == 1){
			TvChooseCar.setTextColor(COLOR_CHANNEL_CHOOSE);
			TvChooseMan.setTextColor(COLOR_CHANNEL_NOT_CHOOSE);
			IvChooseCar.setAlpha(alpha_opaque);
			IvChooseMan.setAlpha(alpha_transparent);
		}else{
			TvChooseCar.setTextColor(COLOR_CHANNEL_NOT_CHOOSE);
			TvChooseMan.setTextColor(COLOR_CHANNEL_CHOOSE);
			IvChooseCar.setAlpha(alpha_transparent);
			IvChooseMan.setAlpha(alpha_opaque);
		}
		
		TvDistrictDoor.setText(R.string.searching_key);
		TvDistrictDoor.setTextSize(18);
		TvDistrictDoor.setTextColor(0xFFffffff);
		TvCarNumber.setText(R.string.can_shake_to_open_door);
		TvCarNumber.setTextSize(12);
		TvCarNumber.setTextColor(0xFF7d7d7d);
		
		IvSearchKey.setImageResource(R.drawable.btn_background_gray);
		IvOpenDoorLogo.setImageResource(R.drawable.btn_serch_1);
		IvOpenDoorLogo.setClickable(false);
		
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
			
		}

		@Override
		public void onPageSelected(int arg0) {
			
		}
		
	}

	private final BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("BLE", "mBluetoothStateReceiver");
			final String action = intent.getAction();
			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				 final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
	                     BluetoothAdapter.ERROR);
				 
				 switch (state) {
				case BluetoothAdapter.STATE_OFF:
					Log.e("BLE", "BluetoothAdapter.STATE_OFF");
					break;
	
				case BluetoothAdapter.STATE_TURNING_OFF:
					Log.e("BLE", "BluetoothAdapter.STATE_TURNING_OFF");
					break;
					
				case BluetoothAdapter.STATE_ON:
					Log.e("BLE", "BluetoothAdapter.STATE_ON");
					populateDeviceList();
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
        return intentFilter;
    }
	
	private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
        	Log.e("BLE", "UARTStatusChangeReceiver");
            String action = intent.getAction();
            
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
            	Log.e("BLE", "UartService.ACTION_GATT_CONNECTED");
            	getActivity().runOnUiThread(new Runnable() {
                     public void run() {
                     }
            	 });
            }
            
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
            	Log.e("BLE", "UartService.ACTION_GATT_SERVICES_DISCOVERED");
            	getActivity().runOnUiThread(new Runnable() {
                    public void run() {     
						if (mUartService != null) {
							mUartService.readRXCharacteristic();
						}
                    }
                });
            }
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
            	Log.e("BLE", "UartService.ACTION_GATT_DISCONNECTED");
            	getActivity().runOnUiThread(new Runnable() {
                     public void run() {
                    	 IvOpenDoorLogo.setEnabled(true);
                    	 IvOpenDoorLogo.setImageResource(R.drawable.btn_serch_1);
                    	 IvSearchKey.setImageResource(R.drawable.btn_background_gray);
                    	 TvDistrictDoor.setText(R.string.searching_key);
                    	 TvDistrictDoor.setTextSize(18);
                    	 TvDistrictDoor.setTextColor(0xFFffffff);
                    	 TvCarNumber.setText(R.string.can_shake_to_open_door);
                    	 TvCarNumber.setTextSize(12);
                    	 TvCarNumber.setTextColor(0xFF7d7d7d);
                         //mUartService.disconnect();
                         mUartService.close();
                     }
                 });
            }
            
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
            	Log.e("BLE", "UartService.ACTION_GATT_SERVICES_DISCOVERED");
             	 mUartService.enableTXNotification();
            }

            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
            	Log.e("BLE", "UartService.ACTION_DATA_AVAILABLE");
              
                 @SuppressWarnings("unused")
				final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                 getActivity().runOnUiThread(new Runnable() {
                     public void run() {
                    	 if(mUartService != null) {
                 			String message = new String(Character.toChars(new Random().nextInt(90 - 65) + 65));
                 			try{
                 				byte[] value = message.getBytes("UTF-8");
                 				mUartService.writeRXCharacteristic(value);
                 			} catch(Exception e) {                				
                 			}
                 		}
                     }
                 });
             }
            
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)){
            	Log.e("BLE", "UartService.DEVICE_DOES_NOT_SUPPORT_UART");
            	mUartService.disconnect();
            }
            
            
        }
    };
       
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
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
			if(devices != null && devices.size() > 0) {
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
    
	@Override
	public void onShake() {
		Log.e("TEST","shaking");
		doOpenDoor();
	}
	
	public void playOpenDoorSound(){
		mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		mSoundPool.play(mSoundPool.load(getActivity(), R.raw.ring, 0), 1, 1, 0, 0, 1);
//		mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.ring);
//		if(mMediaPlayer != null) {		
//			try {
//				mMediaPlayer.stop();
//				mMediaPlayer.prepare();
//				mMediaPlayer.start();
//			} catch (IllegalStateException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}					
//		}
	}
	
	// 返回数据表KeyInfoTable里面记录的数量
	private long DBCount() {
		String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
		SQLiteStatement statement = mKeyDB.compileStatement(sql);
		long count = statement.simpleQueryForLong();
		return count;
	}
}
