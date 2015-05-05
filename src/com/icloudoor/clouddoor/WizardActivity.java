package com.icloudoor.clouddoor;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class WizardActivity extends CircleViewPager {
	
	private int size;
	
	private ImageView Btnone;
	private ImageView Btntwo;
	private ImageView Btnthree;
	private ImageView Btnfour;
	
	private ViewPager mWizardPager;
	private ArrayList<Fragment> mWizardFragmentList;
	private WizardPageAdapter mWizardPageAdapter;
	private WizardFragmentOne mWizardFragOne, mWizardFragOne1;
	private WizardFragmentTwo mWizardFragTwo;
	private WizardFragmentThree mWizardFragThree;
	private WizardFragmentFour mWizardFragFour, mWizardFragFour1;
	public FragmentManager mFragmentManager;
//	public WizardPageChangeListener myPageChangeListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.activity_wizard);
		
		Btnone = (ImageView) findViewById(R.id.Iv_wizard_push1);
		Btntwo = (ImageView) findViewById(R.id.Iv_wizard_push2);
		Btnthree = (ImageView) findViewById(R.id.Iv_wizard_push3);
		Btnfour = (ImageView) findViewById(R.id.Iv_wizard_push4);
		Btnone.setImageResource(R.drawable.push_current);
		Btntwo.setImageResource(R.drawable.push_next);
		Btnthree.setImageResource(R.drawable.push_next);
		Btnfour.setImageResource(R.drawable.push_next);
		
		mFragmentManager = getSupportFragmentManager();
		mWizardPager = (ViewPager)findViewById(R.id.wizard_pager);
//		myPageChangeListener = new WizardPageChangeListener();
		
		mWizardFragmentList = new ArrayList<Fragment>();
		
		mWizardFragOne = new WizardFragmentOne();
		mWizardFragOne1 = new WizardFragmentOne();
		mWizardFragTwo = new WizardFragmentTwo();
		mWizardFragThree = new WizardFragmentThree();
		mWizardFragFour = new WizardFragmentFour();
		mWizardFragFour1 = new WizardFragmentFour();
		
		mWizardFragmentList.add(mWizardFragFour1);
		mWizardFragmentList.add(mWizardFragOne);
		mWizardFragmentList.add(mWizardFragTwo);
		mWizardFragmentList.add(mWizardFragThree);
		mWizardFragmentList.add(mWizardFragFour);
		mWizardFragmentList.add(mWizardFragOne1);
		
		initViewPager(R.id.wizard_pager, mWizardFragmentList ) ;
		
		setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageSelected(int position) {
				int a = getRealPosition(position, size-2) ;
				
				if(a == 0){
					Btnone.setImageResource(R.drawable.push_current);
					Btntwo.setImageResource(R.drawable.push_next);
					Btnthree.setImageResource(R.drawable.push_next);
					Btnfour.setImageResource(R.drawable.push_next);
				} else if(a == 1){
					Btntwo.setImageResource(R.drawable.push_current);
					Btnone.setImageResource(R.drawable.push_next);
					Btnthree.setImageResource(R.drawable.push_next);
					Btnfour.setImageResource(R.drawable.push_next);
				} else if(a == 2){
					Btnthree.setImageResource(R.drawable.push_current);
					Btntwo.setImageResource(R.drawable.push_next);
					Btnone.setImageResource(R.drawable.push_next);
					Btnfour.setImageResource(R.drawable.push_next);
				} else if(a == 3){
					Btnfour.setImageResource(R.drawable.push_current);
					Btntwo.setImageResource(R.drawable.push_next);
					Btnthree.setImageResource(R.drawable.push_next);
					Btnone.setImageResource(R.drawable.push_next);
				}
				
			}
			
		});
		
		
//			
//		mWizardPageAdapter = new WizardPageAdapter(mFragmentManager, mWizardFragmentList);
//		mWizardPager.setAdapter(mWizardPageAdapter);
//		mWizardPager.setCurrentItem(0);
//		mWizardPager.setOnPageChangeListener(myPageChangeListener);
	}
	
//	public class WizardPageChangeListener implements OnPageChangeListener {
//
//		@Override
//		public void onPageScrollStateChanged(int arg0) {
//			if (arg0 == 2) {
//				int index = mWizardPager.getCurrentItem();
//				mWizardPager.setCurrentItem(index);
//				if (index == 0) {
//					Btnone.setImageResource(R.drawable.push_current);
//					Btntwo.setImageResource(R.drawable.push_next);
//					Btnthree.setImageResource(R.drawable.push_next);
//					Btnfour.setImageResource(R.drawable.push_next);
//				} else if (index == 1) {
//					Btntwo.setImageResource(R.drawable.push_current);
//					Btnone.setImageResource(R.drawable.push_next);
//					Btnthree.setImageResource(R.drawable.push_next);
//					Btnfour.setImageResource(R.drawable.push_next);
//				} else if (index == 2) {
//					Btnthree.setImageResource(R.drawable.push_current);
//					Btntwo.setImageResource(R.drawable.push_next);
//					Btnone.setImageResource(R.drawable.push_next);
//					Btnfour.setImageResource(R.drawable.push_next);
//				} else if (index == 3) {
//					Btnfour.setImageResource(R.drawable.push_current);
//					Btntwo.setImageResource(R.drawable.push_next);
//					Btnthree.setImageResource(R.drawable.push_next);
//					Btnone.setImageResource(R.drawable.push_next);
//				}
//			}
//		}
//
//		@Override
//		public void onPageScrolled(int arg0, float arg1, int arg2) {
//
//		}
//
//		@Override
//		public void onPageSelected(int arg0) {
//
//		}
//
//	}
}
