package com.icloudoor.clouddoor;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentCarEntrance extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.fragment_car_entrance, container, false);
		return view;
	}

	
}
