package com.icloudoor.cloudoor;

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
	private int useSign = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		setContentView(R.layout.activity_launcher);
		
		final Intent intent = new Intent();
		if(CheckFirstRun()){
    		//addShortcutToDesktop();
    		
    		intent.setClass(this, WizardActivity.class);
		}else{
			SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", 0);
			isLogin = loginStatus.getInt("LOGIN", 0);
			
			SharedPreferences setSign = getSharedPreferences("SETTING", 0);
			useSign = setSign.getInt("useSign", 0);

			sid = loadSid();
			
			if (isLogin == 0 || sid == null) {
				intent.setClass(this, Login.class);
			} else if (isLogin == 1 && sid != null) {
				if(useSign == 0)
					intent.setClass(this, CloudDoorMainActivity.class);
				else if(useSign == 1)
					intent.setClass(this, VerifyGestureActivity.class);
			}
		}
		Timer jump = new Timer();
		TimerTask jumpTask = new TimerTask() {

			@Override
			public void run() {
				startActivity(intent);
				finish();
			}

		};
		jump.schedule(jumpTask, 1000);
	}
	
	private void addShortcutToDesktop() {
    	Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
    	shortcut.putExtra("duplicate", false);
    	shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, this.getString(R.string.app_name));
    	shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.drawable.logo_deep144));
    	shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(this, this.getClass()).setAction(Intent.ACTION_MAIN));
    	sendBroadcast(shortcut);
    }
    
	private boolean CheckFirstRun() {
    	SharedPreferences setting = getSharedPreferences("com.icloudoor.clouddoor", 0);
    	boolean user_first = setting.getBoolean("FIRST",true);
    	if(user_first) {
    		setting.edit().putBoolean("FIRST", false).commit();
    		return user_first;
    	}
    	return user_first;
    }
	
	public String loadSid() {
		SharedPreferences loadSid = getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}
	
}
