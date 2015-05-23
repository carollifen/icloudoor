package com.icloudoor.clouddoor;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 *
 */
@SuppressLint("ResourceAsColor")
public class KeyListAuthFragment extends Fragment{

	private String TAG = this.getClass().getSimpleName();
	
	private RelativeLayout showHideKeyList;
	private ImageView btnShowHideKeyList;
	private boolean isShowingKeyList;
	
	private RelativeLayout chooseCarKey;
	private RelativeLayout chooseManKey;
	private TextView carKeyText;
	private TextView manKeyText;
	private boolean isChooseCarKey;
	
	private RelativeLayout showTip;
	
	private TextView btnSubmitText;
	private int ColorDisable = 0xFF999999;
	
	public KeyListAuthFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_key_list_auth, container, false);
		
		// to hide the tip after 3 secs
		showTip = (RelativeLayout) view.findViewById(R.id.show_tip);

		Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.show_tip_animation);
		DecelerateInterpolator interpolator = new DecelerateInterpolator();
		animation.setInterpolator(interpolator);
		animation.setFillAfter(true);
		showTip.startAnimation(animation);
		
		// show or hide key list
		isShowingKeyList = false;
		showHideKeyList = (RelativeLayout) view.findViewById(R.id.show_hide_key_list);
		btnShowHideKeyList = (ImageView) view.findViewById(R.id.btn_show_hide_key_list);
		btnShowHideKeyList.setImageResource(R.drawable.common_show_list);
		
		showHideKeyList.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(!isShowingKeyList){
					btnShowHideKeyList.setImageResource(R.drawable.common_hide_list);
					isShowingKeyList = true;
				}else{
					btnShowHideKeyList.setImageResource(R.drawable.common_show_list);
					isShowingKeyList = false;
				}
			}
			
		});
		//
		
		// choose car or man key
		isChooseCarKey = false;
		chooseCarKey = (RelativeLayout) view.findViewById(R.id.btn_choose_car_key);
		chooseManKey = (RelativeLayout) view.findViewById(R.id.btn_choose_man_key);
		carKeyText = (TextView) view.findViewById(R.id.car_key_text);
		manKeyText = (TextView) view.findViewById(R.id.man_key_text);
		
		chooseCarKey.setBackgroundResource(R.drawable.channel_normal);
		chooseManKey.setBackgroundResource(R.drawable.channel_select);
		carKeyText.setTextColor(0xFF333333);
		manKeyText.setTextColor(0xFFffffff);
		
		chooseCarKey.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(!isChooseCarKey){
					chooseCarKey.setBackgroundResource(R.drawable.channel_select);
					chooseManKey.setBackgroundResource(R.drawable.channel_normal);
					carKeyText.setTextColor(0xFFffffff);
					manKeyText.setTextColor(0xFF333333);
					isChooseCarKey = true;
				}
			}
			
		});
		
		chooseManKey.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(isChooseCarKey){
					chooseCarKey.setBackgroundResource(R.drawable.channel_normal);
					chooseManKey.setBackgroundResource(R.drawable.channel_select);
					carKeyText.setTextColor(0xFF333333);
					manKeyText.setTextColor(0xFFffffff);
					isChooseCarKey = false;
				}
			}
			
		});
		//

		// submit 
		btnSubmitText = (TextView) view.findViewById(R.id.btn_submit_text);
		btnSubmitText.setTextColor(ColorDisable);
		btnSubmitText.setBackgroundResource(R.drawable.btn_submit_big_disable);
		
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		
		btnSubmitText.setWidth(screenWidth - 32 * 2);	
		btnSubmitText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
			
		});
		//
		
		return view;
	}


}
