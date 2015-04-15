package com.icloudoor.clouddoor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.pm.PackageManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.icloudoor.clouddoor.ShakeEventManager;
import com.icloudoor.clouddoor.UartService;
import com.icloudoor.clouddoor.ShakeEventManager.ShakeListener;

@SuppressLint("NewApi")
public class KeyFragment extends Fragment implements ShakeListener  {
	
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
	
	//for BLE
	private static final int REQUEST_ENABLE_BT = 0;
	private static final long SCAN_PERIOD = 10000; // ms
	private BluetoothAdapter mBluetoothAdapter;
	private UartService mUartService = null;
	private ShakeEventManager mShakeMgr;
	private List<BluetoothDevice> mDeviceList;
	private Map<String, Integer> mDevRssiValues;
	
	private SQLiteDatabase mKeyDB = null;

	public KeyFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//TODO for BLE
		if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getActivity(), R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        }
		
		IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(mBluetoothStateReceiver, filter);
		
        BluetoothManager mBluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
		        
        mShakeMgr = new ShakeEventManager();
        mShakeMgr.setListener(KeyFragment.this);
		mShakeMgr.init(getActivity());
		
		checkBlueToothState();
		
		service_init(); 
	
		View view = inflater.inflate(R.layout.key_page, container, false);

		channelSwitch = (RelativeLayout) view.findViewById(R.id.channel_switch);  // ��� ѡ���ŵ����ͣ���or�� �� ��setting�п������ó��õ�������
		keyWidge = (RelativeLayout) view.findViewById(R.id.key_widge);    
		
		TvChooseCar = (TextView) view.findViewById(R.id.Tv_choose_car);
		TvChooseMan = (TextView) view.findViewById(R.id.Tv_choose_man);
		TvDistrictDoor = (TextView) view.findViewById(R.id.district_door);
		TvCarNumber = (TextView) view.findViewById(R.id.car_number);
		TvOpenKeyList = (TextView) view.findViewById(R.id.open_key_list);
		
		IvChooseCar = (ImageView) view.findViewById(R.id.Iv_choose_car);   
		IvChooseMan = (ImageView) view.findViewById(R.id.Iv_choose_man); 
		IvSearchKey = (ImageView) view.findViewById(R.id.Iv_search_key);   //��Ҫ�ı䱳����ɫ����������״̬
		IvOpenDoorLogo = (ImageView) view.findViewById(R.id.Iv_open_door_logo);  //��Ҫ�ı�ͼ������������״̬
		IvOpenDoorLogo.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				doOpenDoor();
			}
			
		});
		
		IvWeatherWidgePush1 = (ImageView) view.findViewById(R.id.Iv_weather_widge_push1);
		IvWeatherWidgePush2 = (ImageView) view.findViewById(R.id.Iv_weather_widge_push2);

		mFragmentManager = getChildFragmentManager();
		mWeatherWidgePager = (ViewPager) view.findViewById(R.id.weather_widge_pager);
		myPageChangeListener = new MyPageChangeListener();
		
		InitViewPager();
		InitFragmentViews();

		channelSwitch.setOnClickListener(new OnClickListener() {      //�����ͼ���������ɫ����Ӧ�仯

			@Override
			public void onClick(View v) {
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
		
		//TODO
		//��ť�ļ�����Ҫ��ɼ�⵽��������ʱ��������Կ��ʱ����״̬�ļ���
		keyWidge.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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
					TvDistrictDoor.setText("�����㽭����");
					TvDistrictDoor.setTextSize(18);
					TvDistrictDoor.setTextColor(0xFFffffff);
					TvCarNumber.setText("��A XXXXX");
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
		mDeviceList = new ArrayList<BluetoothDevice>();
		mDevRssiValues = new HashMap<String, Integer>();
        scanLeDevice(true);
	}
	
	private void scanLeDevice(final boolean enable) {
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
        }
    }	
	
	private void sendOpenDoorSignal() {
		if(mUartService != null) {
			String message = new String(Character.toChars(new Random().nextInt(90 - 65) + 65));
			try{
				byte[] value = message.getBytes("UTF-8");
				mUartService.writeRXCharacteristic(value);
			} catch(Exception e) {
				
			}
			
		}
	}
	
	private void doOpenDoor() {
		IvOpenDoorLogo.setEnabled(false);
		Toast.makeText(getActivity(), R.string.door_open, Toast.LENGTH_SHORT).show();
		
		if(mDeviceList != null  && mDeviceList.size() > 0) {
			if(mDeviceList.get(0).getAddress() != null) {
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
		        mUartService.connect(mDeviceList.get(0).getAddress());
		        IvOpenDoorLogo.setImageResource(R.drawable.selector_pressed);
		        IvSearchKey.setImageResource(R.drawable.btn_background_blue);
		        TvDistrictDoor.setText("�����㽭����");
		        TvDistrictDoor.setTextSize(18);
		        TvDistrictDoor.setTextColor(0xFFffffff);
		        TvCarNumber.setText("��A XXXXX");
		        TvCarNumber.setTextSize(18);
		        TvCarNumber.setTextColor(0xFFffffff);
			}		
		}
	}
	
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
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
			final String action = intent.getAction();
			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				 final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
	                     BluetoothAdapter.ERROR);
				 
				 switch (state) {
				case BluetoothAdapter.STATE_OFF:
					
					break;
	
				case BluetoothAdapter.STATE_TURNING_OFF:
					break;
					
				case BluetoothAdapter.STATE_ON:
					populateDeviceList();
					break;
					
				case BluetoothAdapter.STATE_TURNING_ON:
					break;
				}
			}
			
		} 
	};
	
	private static IntentFilter makeGattUpdateIntentFilter() {
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
            String action = intent.getAction();
            
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
            	getActivity().runOnUiThread(new Runnable() {
                     public void run() {
                     }
            	 });
            }
            
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
            	getActivity().runOnUiThread(new Runnable() {
                    public void run() {     
                    	sendOpenDoorSignal();
                    }
                });
            }
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
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
             	 mUartService.enableTXNotification();
            }

            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
              
                 @SuppressWarnings("unused")
				final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                 getActivity().runOnUiThread(new Runnable() {
                     public void run() {
                    	 
                     }
                 });
             }
            
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)){
            	mUartService.disconnect();
            }
            
            
        }
    };
       
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
        	mUartService = ((UartService.LocalBinder) rawBinder).getService();
        		if (!mUartService.initialize()) {
        			getActivity().finish();
                }

        }

        public void onServiceDisconnected(ComponentName classname) {
        	mUartService = null;

        }
    };
	
	@Override
	public void onShake() {
		doOpenDoor();
	}
}
