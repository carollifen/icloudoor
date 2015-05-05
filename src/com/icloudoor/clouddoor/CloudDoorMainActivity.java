package com.icloudoor.clouddoor;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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

public class CloudDoorMainActivity extends FragmentActivity {

//	private ViewPager mViewPager;
//	private ArrayList<Fragment> mFragmentsList;
//	private MyFragmentPagerAadpter mFragmentAdapter;

	private MsgFragment mMsgFragment;
	private KeyFragment mKeyFragment;
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

	private int COLOR_GRAY = 0xFF747f8d;
	private int COLOR_BLACK = 0xFF000000;

	private float alpha_half_transparent = 0.2f;
	private float alpha_opaque = 1.0f;
	
	private int homePressed = 0;
	private int lockScreenBefore = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.new_main);

		mMsgFragment = new MsgFragment();
		mKeyFragment = new KeyFragment();
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
		mFragmenetTransaction.replace(R.id.id_content, mKeyFragment).commit();
		
		bottomTvKey.setTextColor(COLOR_BLACK);
		bottomTvMsg.setTextColor(COLOR_GRAY);
		bottomTvSetting.setTextColor(COLOR_GRAY);
		bottomTvWuye.setTextColor(COLOR_GRAY);

		bottomIvMsg.setImageResource(R.drawable.button_199);
		bottomIvKey.setImageResource(R.drawable.button_201);
		bottomIvSetting.setImageResource(R.drawable.button_207);
		bottomIvWuye.setImageResource(R.drawable.button_194);
	}

	public void onResume() {
		super.onResume();
		Log.e("TEST", "onResume");
		
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

			bottomIvMsg.setImageResource(R.drawable.button_199);
			bottomIvKey.setImageResource(R.drawable.button_203);
			bottomIvSetting.setImageResource(R.drawable.button_207);
			bottomIvWuye.setImageResource(R.drawable.button_192);
			
			mFragmenetTransaction.replace(R.id.id_content, mWuyeFragment);
			break;
		case R.id.bottom_msg_layout:
			bottomTvKey.setTextColor(COLOR_GRAY);
			bottomTvMsg.setTextColor(COLOR_BLACK);
			bottomTvSetting.setTextColor(COLOR_GRAY);
			bottomTvWuye.setTextColor(COLOR_GRAY);

			bottomIvMsg.setImageResource(R.drawable.button_196);
			bottomIvKey.setImageResource(R.drawable.button_203);
			bottomIvSetting.setImageResource(R.drawable.button_207);
			bottomIvWuye.setImageResource(R.drawable.button_194);

			mFragmenetTransaction.replace(R.id.id_content, mMsgFragment);
			
//			mViewPager.setCurrentItem(0);
			break;

		case R.id.bottom_key_layout:
			bottomTvKey.setTextColor(COLOR_BLACK);
			bottomTvMsg.setTextColor(COLOR_GRAY);
			bottomTvSetting.setTextColor(COLOR_GRAY);
			bottomTvWuye.setTextColor(COLOR_GRAY);

			bottomIvMsg.setImageResource(R.drawable.button_199);
			bottomIvKey.setImageResource(R.drawable.button_201);
			bottomIvSetting.setImageResource(R.drawable.button_207);
			bottomIvWuye.setImageResource(R.drawable.button_194);

			mFragmenetTransaction.replace(R.id.id_content, mKeyFragment);
			
//			mViewPager.setCurrentItem(1);
			break;

		case R.id.bottom_setting_layout:
			bottomTvKey.setTextColor(COLOR_GRAY);
			bottomTvMsg.setTextColor(COLOR_GRAY);
			bottomTvSetting.setTextColor(COLOR_BLACK);
			bottomTvWuye.setTextColor(COLOR_GRAY);

			bottomIvMsg.setImageResource(R.drawable.button_199);
			bottomIvKey.setImageResource(R.drawable.button_203);
			bottomIvSetting.setImageResource(R.drawable.button_205);
			bottomIvWuye.setImageResource(R.drawable.button_194);

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

}
