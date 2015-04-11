package com.icloudoor.clouddoor;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class LauncherActivity extends Activity {

	private String sid = null;
	private int isLogin = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.activity_launcher);

		SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", 0);
		isLogin = loginStatus.getInt("LOGIN", 0);

		sid = loadSid();
		
		final Intent intent = new Intent();
		if (isLogin == 0 || sid == null) {
			intent.setClass(this, Login.class);
		} else if (isLogin == 1 && sid != null) {
			intent.setClass(this, CloudDoorMainActivity.class);
		}
		Timer jump = new Timer();
		TimerTask jumpTask = new TimerTask() {

			@Override
			public void run() {
				startActivity(intent);
				finish();
			}

		};
		jump.schedule(jumpTask, 2000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launcher, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public String loadSid() {
		SharedPreferences loadSid = getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}
}
