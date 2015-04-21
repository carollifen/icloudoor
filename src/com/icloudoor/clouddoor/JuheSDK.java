package com.icloudoor.clouddoor;

import android.app.Application;

import com.thinkland.sdk.android.SDKInitializer;

public class JuheSDK extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		SDKInitializer.initialize(getApplicationContext());
	}
}