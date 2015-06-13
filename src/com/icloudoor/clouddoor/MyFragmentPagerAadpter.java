package com.icloudoor.cloudoor;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class MyFragmentPagerAadpter extends FragmentPagerAdapter {
	private ArrayList<Fragment> mFragmentList;

	public MyFragmentPagerAadpter(FragmentManager fm) {
		super(fm);
	}

	public MyFragmentPagerAadpter(FragmentManager fm, ArrayList<Fragment> FragmentList) {
		super(fm);
		this.mFragmentList = FragmentList;
	}	
	
	@Override
	public Fragment getItem(int index) {
		return mFragmentList.get(index);
	}

	@Override
	public int getCount() {
		return mFragmentList.size();
	}
	
}