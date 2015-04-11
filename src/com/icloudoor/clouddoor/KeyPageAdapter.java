package com.icloudoor.clouddoor;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class KeyPageAdapter extends FragmentPagerAdapter{
	private ArrayList<Fragment> mKeyPageFragmentList;

	public KeyPageAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}
	
	public KeyPageAdapter(FragmentManager fm, ArrayList<Fragment> FragmentList) {
		super(fm);
		this.mKeyPageFragmentList = FragmentList;
	}	

	@Override
	public Fragment getItem(int index) {
		// TODO Auto-generated method stub
		return mKeyPageFragmentList.get(index);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mKeyPageFragmentList.size();
	}
	
}
