package com.icloudoor.clouddoor;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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

	private RelativeLayout bottomMsg;
	private RelativeLayout bottomKey;
	private RelativeLayout bottomSetting;

	private TextView bottomTvMsg;
	private TextView bottomTvKey;
	private TextView bottomTvSetting;

	private ImageView bottomIvMsg;
	private ImageView bottomIvKey;
	private ImageView bottomIvSetting;

	public FragmentManager mFragmentManager;
	public MyOnClickListener myClickListener;
	public FragmentTransaction mFragmenetTransaction;
//	public MyPageChangeListener myPageChangeListener;

	private int COLOR_GRAY = 0xFFbdc7d4;
	private int COLOR_BLACK = 0xFF000000;

	private float alpha_half_transparent = 0.2f;
	private float alpha_opaque = 1.0f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.new_main);

		mMsgFragment = new MsgFragment();
		mKeyFragment = new KeyFragment();
		mSettingFragment = new SettingFragment();
		
		
		
//		mFragmentManager = getSupportFragmentManager();

//		InitViewPager();
		InitViews();
		InitState();
	}

	public void InitViews() {
		myClickListener = new MyOnClickListener();
//		myPageChangeListener = new MyPageChangeListener();

//		mViewPager = (ViewPager) findViewById(R.id.vPager);

		bottomMsg = (RelativeLayout) findViewById(R.id.bottom_msg_layout);
		bottomKey = (RelativeLayout) findViewById(R.id.bottom_key_layout);
		bottomSetting = (RelativeLayout) findViewById(R.id.bottom_setting_layout);

		bottomTvMsg = (TextView) findViewById(R.id.bottom_tv_msg);
		bottomTvKey = (TextView) findViewById(R.id.bottom_tv_key);
		bottomTvSetting = (TextView) findViewById(R.id.bottom_tv_setting);

		bottomIvMsg = (ImageView) findViewById(R.id.btn_Msg);
		bottomIvKey = (ImageView) findViewById(R.id.btn_Key);
		bottomIvSetting = (ImageView) findViewById(R.id.btn_set);

//		mViewPager.setAdapter(mFragmentAdapter);
//		mViewPager.setOnPageChangeListener(myPageChangeListener);

		bottomIvMsg.setOnClickListener(myClickListener);
		bottomIvKey.setOnClickListener(myClickListener);
		bottomIvSetting.setOnClickListener(myClickListener);
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
		bottomIvKey.setAlpha(alpha_opaque);
		bottomIvMsg.setAlpha(alpha_half_transparent);		
		bottomIvSetting.setAlpha(alpha_half_transparent);
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
//		case 0:
		case R.id.btn_Msg:
			bottomTvMsg.setTextColor(COLOR_BLACK);
			bottomTvKey.setTextColor(COLOR_GRAY);
			bottomTvSetting.setTextColor(COLOR_GRAY);

			bottomIvMsg.setAlpha(alpha_opaque);
			bottomIvKey.setAlpha(alpha_half_transparent);
			bottomIvSetting.setAlpha(alpha_half_transparent);

			mFragmenetTransaction.replace(R.id.id_content, mMsgFragment);
			
//			mViewPager.setCurrentItem(0);
			break;
//		case 1:
		case R.id.btn_Key:
			bottomTvKey.setTextColor(COLOR_BLACK);
			bottomTvMsg.setTextColor(COLOR_GRAY);
			bottomTvSetting.setTextColor(COLOR_GRAY);

			bottomIvKey.setAlpha(alpha_opaque);
			bottomIvMsg.setAlpha(alpha_half_transparent);
			bottomIvSetting.setAlpha(alpha_half_transparent);

			mFragmenetTransaction.replace(R.id.id_content, mKeyFragment);
			
//			mViewPager.setCurrentItem(1);
			break;
//		case 2:
		case R.id.btn_set:
			bottomTvSetting.setTextColor(COLOR_BLACK);
			bottomTvMsg.setTextColor(COLOR_GRAY);
			bottomTvKey.setTextColor(COLOR_GRAY);

			bottomIvSetting.setAlpha(alpha_opaque);
			bottomIvMsg.setAlpha(alpha_half_transparent);
			bottomIvKey.setAlpha(alpha_half_transparent);

			mFragmenetTransaction.replace(R.id.id_content, mSettingFragment);
			
//			mViewPager.setCurrentItem(2);
			break;
		}
		mFragmenetTransaction.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cloud_door_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
