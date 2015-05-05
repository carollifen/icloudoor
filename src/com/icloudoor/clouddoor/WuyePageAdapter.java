package com.icloudoor.clouddoor;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class WuyePageAdapter extends FragmentPagerAdapter{
	private ArrayList<Fragment> mWuyePageFragmentList;

	public WuyePageAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}
	
	public WuyePageAdapter(FragmentManager fm, ArrayList<Fragment> FragmentList) {
		super(fm);
		
		if(FragmentList == null){
			this.mWuyePageFragmentList = new ArrayList<Fragment>();
		}else{
			this.mWuyePageFragmentList = FragmentList;
		}
	}	

	@Override
	public Fragment getItem(int index) {
		// TODO Auto-generated method stub
		return mWuyePageFragmentList.get(index);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mWuyePageFragmentList.size();
	}
	
}
