package com.icloudoor.clouddoor;

import java.util.ArrayList;

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

public class WuyeFragment extends Fragment {
	
	private ImageView WuyeWidgePush1;
	private ImageView WuyeWidgePush2;
	
	private ImageView BtnLianxiwuye;
	private ImageView BtnNotice;
	private ImageView BtnFix;
	private ImageView BtnBad;
	private ImageView BtnGood;
	private ImageView BtnQuery;
	private ImageView BtnBill;
	private ImageView BtnPay;
	
	private MyClickListener myClick;
	
	private ViewPager mWuyeWidgePager;
	private ArrayList<Fragment> mWuyePageFragmentList;
	private WuyePageAdapter mWuyePageAdapter;
	public FragmentManager mFragmentManager;
	private WuyeWidgeFragment mWuyeWidgeFragment;
	private WuyeWidgeFragment2 mWuyeWidgeFragment2;
	public MyPageChangeListener myPageChangeListener;

	public WuyeFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.wuye_page, container,false);
		
		WuyeWidgePush1 = (ImageView) view.findViewById(R.id.Iv_wuye_widge_push1);
		WuyeWidgePush2 = (ImageView) view.findViewById(R.id.Iv_wuye_widge_push2);
		
		BtnLianxiwuye = (ImageView) view.findViewById(R.id.btn_lianxiwuye);
		BtnNotice = (ImageView) view.findViewById(R.id.btn_notice);
		BtnFix = (ImageView) view.findViewById(R.id.btn_fix);
		BtnBad = (ImageView) view.findViewById(R.id.btn_bad);
		BtnGood = (ImageView) view.findViewById(R.id.btn_good);
		BtnQuery = (ImageView) view.findViewById(R.id.btn_query);
		BtnBill = (ImageView) view.findViewById(R.id.btn_bill);
		BtnPay = (ImageView) view.findViewById(R.id.btn_pay);
		
		BtnLianxiwuye.setOnClickListener(myClick);
		BtnNotice.setOnClickListener(myClick);
		BtnFix.setOnClickListener(myClick);
		BtnBad.setOnClickListener(myClick);
		BtnGood.setOnClickListener(myClick);
		BtnQuery.setOnClickListener(myClick);
		BtnBill.setOnClickListener(myClick);
		BtnPay.setOnClickListener(myClick);
		
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
	
	public class MyClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btn_lianxiwuye:
				break;
			case R.id.btn_notice:
				break;
			case R.id.btn_fix:
				break;
			case R.id.btn_bad:
				break;
			case R.id.btn_good:
				break;
			case R.id.btn_query:
				break;
			case R.id.btn_bill:
				break;
			case R.id.btn_pay:
				break;
			}
			
		}
		
	}
	
}
