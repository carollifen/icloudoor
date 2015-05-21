package com.icloudoor.clouddoor;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class WizardPageAdapter extends FragmentPagerAdapter{
	private ArrayList<Fragment> mWizardFragmentList;

	public WizardPageAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}
	
	public WizardPageAdapter(FragmentManager fm, ArrayList<Fragment> FragmentList) {
		super(fm);
		this.mWizardFragmentList = FragmentList;
	}	

	@Override
	public Fragment getItem(int index) {
		// TODO Auto-generated method stub
		return mWizardFragmentList.get(index);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mWizardFragmentList.size();
//		return Integer.MAX_VALUE;
	}
	
}