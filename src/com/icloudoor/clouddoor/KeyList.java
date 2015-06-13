package com.icloudoor.cloudoor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class KeyList extends FragmentActivity{

	private String TAG = "KeyList";
	
	private final String DATABASE_NAME = "KeyDB.db";
	private final String TABLE_NAME = "KeyInfoTable";
	private MyDataBaseHelper mKeyDBHelper;
	private SQLiteDatabase mKeyDB;
	
	private RelativeLayout IvBack;

	private URL downLoadKeyURL;
	private RequestQueue mQueue;

	private String HOST = "http://test.zone.icloudoor.com/icloudoor-web";
	private String sid = null;
	private String uuid = null;
	private MyJsonObjectRequest mJsonRequest;
	private int statusCode;

	// Door info variable
	private ListView mKeyList;
	/*
	private KeyListAdapter mAdapter;
	*/
	private ArrayList<HashMap<String, String>> doorNameList;
	
	
	//for old key download interface
	private String L1ZoneName;
	private String L1ZoneID;
	private String L2ZoneName;
	private String L2ZoneID;
	private String L3ZoneName;
	private String L3ZoneID;
	private String[] carNums;
	
	//for new key download interface
	private String ZONEID;
	private String DOORNAME;
	private String DOORID;
	private String DEVICEID;
	private String DOORTYPE;
	private String PLATENUM;
	private String DIRECTION;    //1.go in   2.go out
	private String AUTHFROM;
	private String AUTHTO;
	private String CARSTATUS;    //1. own car  2.borrow car  3.lend car
	private String CARPOSSTATUS;    //1.init   2.inside   3.outside
	
	// for new ui
	private RelativeLayout keyListSwitch;
	private RelativeLayout switchAuth;
	private RelativeLayout switchList;
	private TextView switchAuthText;
	private TextView switchListText;
	private boolean chooseList;
	
	private KeyListAuthFragment mAuthFragment;
	private KeyListListFragment mListFragment;
	public FragmentManager mFragmentManager;
	public FragmentTransaction mFragmenetTransaction;
	public MyOnClickListener myClickListener;
	
	private Broadcast mFinishActivityBroadcast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.key_list);
		mFinishActivityBroadcast=	new Broadcast();
		 IntentFilter intentFilter = new IntentFilter();
		    intentFilter.addAction("com.icloudoor.clouddoor.ACTION_FINISH");
		    registerReceiver(mFinishActivityBroadcast, intentFilter);

		mKeyDBHelper = new MyDataBaseHelper(KeyList.this, DATABASE_NAME);
		mKeyDB = mKeyDBHelper.getWritableDatabase();
		
		//for new ui
		chooseList = true;
		
		keyListSwitch = (RelativeLayout) findViewById(R.id.key_list_switch);
		switchAuth = (RelativeLayout) findViewById(R.id.select_auth);
		switchList = (RelativeLayout) findViewById(R.id.select_list);
		switchAuthText = (TextView) findViewById(R.id.select_auth_text);
		switchListText = (TextView) findViewById(R.id.select_list_text);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) keyListSwitch.getLayoutParams();
		RelativeLayout.LayoutParams paramsAuth = (RelativeLayout.LayoutParams) switchAuth.getLayoutParams();
		RelativeLayout.LayoutParams paramsList = (RelativeLayout.LayoutParams) switchList.getLayoutParams();
		params.width = screenWidth - 105*2;
		paramsAuth.width = screenWidth/2 - 105;
		paramsList.width = screenWidth/2 - 105;
		keyListSwitch.setLayoutParams(params);
		switchAuth.setLayoutParams(paramsAuth);
		switchList.setLayoutParams(paramsList);
		
		
		
		switchAuth.setBackgroundResource(R.drawable.key_list_normal_left);
		switchList.setBackgroundResource(R.drawable.key_list_select_right);
		switchAuthText.setTextColor(0xFF666666);	
		switchListText.setTextColor(0xFFffffff);	
		
		mAuthFragment = new KeyListAuthFragment();
		mListFragment = new KeyListListFragment();
		mFragmentManager = getSupportFragmentManager();
		mFragmenetTransaction = mFragmentManager.beginTransaction();
		mFragmenetTransaction.replace(R.id.key_list_content, mListFragment).commit();

		myClickListener = new MyOnClickListener();

		switchAuth.setOnClickListener(myClickListener);		
		switchList.setOnClickListener(myClickListener);

		IvBack = (RelativeLayout) findViewById(R.id.btn_back_key_list);
		IvBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});
		

	}
	
	public class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View view) {
			mFragmenetTransaction = mFragmentManager.beginTransaction();
			if(view.getId() == R.id.select_auth){
				if(chooseList){
					switchAuth.setBackgroundResource(R.drawable.key_list_select_left);
					switchList.setBackgroundResource(R.drawable.key_list_normal_right);
					switchAuthText.setTextColor(0xFFffffff);	
					switchListText.setTextColor(0xFF666666);
					chooseList = false;
					
					mFragmenetTransaction.replace(R.id.key_list_content, mAuthFragment);
				}
			}else if(view.getId() == R.id.select_list){
				if(!chooseList){
					switchAuth.setBackgroundResource(R.drawable.key_list_normal_left);
					switchList.setBackgroundResource(R.drawable.key_list_select_right);
					switchAuthText.setTextColor(0xFF666666);	
					switchListText.setTextColor(0xFFffffff);	
					chooseList = true;
					
					mFragmenetTransaction.replace(R.id.key_list_content, mListFragment);
				}
			}
			mFragmenetTransaction.commit();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.e(TAG, "onResume");
	}

	/*
	 * key params:
	 * authType: 	1 - forever		  // the authtype is cancelled in the new key download interface
	 *                	2 - long time
	 *                	3 - temp
	 * doorType: 	1 - man	
	 *                	2 - car
	 * carStatus:  1 - my own car
	 * 					2 - my borrowed car
	 *					3 - my lend car
	 *carPosStatus:  1 - car inside the zone
	 *					   2 - car outside the zone
	 */
	public void parseKeyData(JSONObject response) throws JSONException {
		Log.e("test for new interface", "parseKeyData func");
//		ArrayList<HashMap<String, String>> doorNameList = new ArrayList<HashMap<String, String>>();
		
		// for new key download interface
		JSONObject data = response.getJSONObject("data");
		JSONArray doorAuths = data.getJSONArray("doorAuths");
		for (int index = 0; index < doorAuths.length(); index++) {
			JSONObject doorData = (JSONObject) doorAuths.get(index);
			
			ContentValues value = new ContentValues();
			if(doorData.getString("deviceId").length() > 0){
				if(!hasData(mKeyDB, doorData.getString("deviceId"))){
					value.put("zoneId", doorData.getString("zoneId"));
					value.put("doorName", doorData.getString("doorName"));
					value.put("doorId", doorData.getString("doorId"));
					value.put("deviceId", doorData.getString("deviceId"));
					value.put("doorType", doorData.getString("doorType"));
					value.put("plateNum", doorData.getString("plateNum"));
					value.put("direction", doorData.getString("direction"));
					value.put("authFrom", doorData.getString("authFrom"));
					value.put("authTo", doorData.getString("authTo"));
					
					JSONArray cars = data.getJSONArray("cars");
					for(int i = 0; i < cars.length(); i++){
						JSONObject carData = (JSONObject) cars.get(i);
						if(carData.getString("l1ZoneId").equals(doorData.getString("zoneId")) 
								&& carData.getString("plateNum").equals(doorData.getString("plateNum"))){
							value.put("carStatus", carData.getString("carStatus"));
							value.put("carPosStatus", carData.getString("carPosStatus"));
						}
					}
					mKeyDB.insert(TABLE_NAME, null, value);
				}
			}
		}
	}

	public void saveSid(String sid) {
		SharedPreferences savedSid = getSharedPreferences("SAVEDSID",
				MODE_PRIVATE);
		Editor editor = savedSid.edit();
		editor.putString("SID", sid);
		editor.commit();
	}

	public String loadSid() {
		SharedPreferences loadSid = getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}

	public void saveUUID(String uuid){
		SharedPreferences savedUUID = getSharedPreferences("SAVEDUUID",
				MODE_PRIVATE);
		Editor editor = savedUUID.edit();
		editor.putString("UUID", uuid);
		editor.commit();
	}
	
	public String loadUUID(){
		SharedPreferences loadUUID = getSharedPreferences("SAVEDUUID",
				MODE_PRIVATE);
		return loadUUID.getString("UUID", null);
	}
	
	private long DBCount() {  
	    String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
	    SQLiteStatement statement = mKeyDB.compileStatement(sql);
	    long count = statement.simpleQueryForLong();
	    return count;
	}
	
	private boolean hasData(SQLiteDatabase mDB, String str){
		boolean hasData = false;
		Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME,null);
		
		if(mCursor.moveToFirst()){
			int deviceIdIndex = mCursor.getColumnIndex("deviceId");
			do{
				 String deviceId = mCursor.getString(deviceIdIndex);
				 
				 if(deviceId.equals(str)) {
					 hasData = true;
					 break;
				 }
				 
			}while(mCursor.moveToNext());
		}
        mCursor.close();
		return hasData;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mFinishActivityBroadcast);
		
	}
	
	class Broadcast extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			KeyList.this.finish();
		}
		
	}
}