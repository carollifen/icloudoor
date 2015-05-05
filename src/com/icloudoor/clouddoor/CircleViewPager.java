package com.icloudoor.clouddoor;

import java.util.ArrayList;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class CircleViewPager extends FragmentActivity {
	private int size ;
	protected ViewPager viewPager ;
	private OnPageChangeListener listener ;
	
	protected void initViewPager(int viewPagerId,ArrayList<Fragment> fragments){
		size = fragments.size() ;
		viewPager = (ViewPager)findViewById(viewPagerId) ;
		viewPager.setAdapter(new WuyePageAdapter(getSupportFragmentManager(), fragments)) ;
		
		viewPager.setCurrentItem(1);
		
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int state) {
				if(null != listener){
					listener.onPageScrollStateChanged(state) ;
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				if(null != listener){
					listener.onPageScrolled(position, positionOffset, positionOffsetPixels) ;
				}
			}

			@Override
			public void onPageSelected(int position) {
				if(position == 0){
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							viewPager.setCurrentItem(size-2,false) ;
						}
					}, 50) ;
				}else if(position ==size-1){
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							viewPager.setCurrentItem(1,false) ;
						}
					}, 50) ;

				}

				if(null != listener){
					listener.onPageSelected(position) ;
				}
			}
			
		});
	}
	
	protected int getRealPosition(int broadenPosition,int size){
		int position ;
		if(broadenPosition==0){
			position = size-1 ;
		}else if(broadenPosition==size+1){
			position = 0 ;
		}else{
			position=broadenPosition-1 ;
		}

		return position ;
	}
	
	protected void setOnPageChangeListener(OnPageChangeListener l){
		listener = l ;
	}
}