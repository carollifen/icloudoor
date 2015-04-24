package com.icloudoor.clouddoor;

import java.util.ArrayList;

import com.icloudoor.clouddoor.KeyFragment.MyPageChangeListener;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class WuyeFragment extends Fragment {
	
	private ImageView WuyeWidgePush1;
	private ImageView WuyeWidgePush2;
	
	private ViewPager mWuyeWidgePager;
	private ArrayList<Fragment> mWuyePageFragmentList;
	private WuyePageAdapter mWuyePageAdapter;
	public FragmentManager mFragmentManager;
	private WuyeWidgeFragment mWuyeWidgeFragment;
	private WuyeWidgeFragment2 mWuyeWidgeFragment2;
	public MyPageChangeListener myPageChangeListener;

	public WuyeFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.wuye_page, container,false);
		
		WuyeWidgePush1 = (ImageView) view.findViewById(R.id.Iv_wuye_widge_push1);
		WuyeWidgePush2 = (ImageView) view.findViewById(R.id.Iv_wuye_widge_push2);
		
		mFragmentManager = getChildFragmentManager();
		mWuyeWidgePager = (ViewPager) view.findViewById(R.id.wuye_widge_pager);
		myPageChangeListener = new MyPageChangeListener();
		
		InitFragmentViews();
		InitViewPager();
		
		return view;
	}
	
	public void InitFragmentViews() {
		WuyeWidgePush1.setImageResource(R.drawable.push_current);
		WuyeWidgePush2.setImageResource(R.drawable.push_next);
	}
	
	public void InitViewPager(){
		mWuyePageFragmentList = new ArrayList<Fragment>();
		
		mWuyeWidgeFragment = new WuyeWidgeFragment();
		mWuyeWidgeFragment2 = new WuyeWidgeFragment2();
		
		mWuyePageFragmentList.add(mWuyeWidgeFragment);
		mWuyePageFragmentList.add(mWuyeWidgeFragment2);
		
		mWuyePageAdapter = new WuyePageAdapter(mFragmentManager, mWuyePageFragmentList);
		mWuyeWidgePager.setAdapter(mWuyePageAdapter);
		mWuyeWidgePager.setCurrentItem(0);
		mWuyeWidgePager.setOnPageChangeListener(myPageChangeListener);
	}

	public class MyPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			if (arg0 == 2) {
				int index = mWuyeWidgePager.getCurrentItem();
				mWuyeWidgePager.setCurrentItem(index);
				if(index == 0){
					WuyeWidgePush1.setImageResource(R.drawable.push_current);
					WuyeWidgePush2.setImageResource(R.drawable.push_next);
				}else if(index == 1){
					WuyeWidgePush2.setImageResource(R.drawable.push_current);
					WuyeWidgePush1.setImageResource(R.drawable.push_next);
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
	
}
