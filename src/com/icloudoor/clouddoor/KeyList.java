package com.icloudoor.clouddoor;

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
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class KeyList extends Activity{

	private final String DATABASE_NAME = "KeyDB.db";
	private final String TABLE_NAME = "KeyInfoTable";
	private MyDataBaseHelper mKeyDBHelper = new MyDataBaseHelper(KeyList.this, DATABASE_NAME);
	private SQLiteDatabase mKeyDB;
	

	private RelativeLayout IvBack;

	private URL downLoadKeyURL;
	private RequestQueue mQueue;

	private String HOST = "http://zone.icloudoor.com/icloudoor-web";
	private String sid = null;
	private String uuid = null;
	private MyJsonObjectRequest mJsonRequest;
	private int statusCode;
	private int isLogin;

	// Door info variable
	ListView mKeyList;
	KeyListAdapter mAdapter;
	ArrayList<HashMap<String, String>> doorNameList;
	String L1ZoneName;
	String L1ZoneID;
	String L2ZoneName;
	String L2ZoneID;
	String L3ZoneName;
	String L3ZoneID;
	String[] carNums;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.key_list);
		
		mKeyList = (ListView) findViewById(R.id.key_listview);

//		mKeyDBHelper = new MyDataBaseHelper(KeyList.this, DATABASE_NAME);
		mKeyDB = mKeyDBHelper.getWritableDatabase();
		
		mQueue = Volley.newRequestQueue(this);
		
		sid = loadSid();
		uuid = loadUUID();
		if (uuid == null) {
			uuid = UUID.randomUUID().toString().replaceAll("-", "");
			saveUUID(uuid);
		}

		try {
			downLoadKeyURL = new URL(HOST + "/user/door/download.do" + "?sid=" + sid);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		Toast.makeText(KeyList.this, R.string.downloading_key_list, Toast.LENGTH_SHORT).show();
		
		mJsonRequest = new MyJsonObjectRequest(Method.POST,
				downLoadKeyURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							statusCode = response.getInt("code");
							if (statusCode == 1) {

								parseKeyData(response);

								Log.e("TEST", response.toString());
								Log.e("TEST  DB  count: ", String.valueOf(DBCount()));

								// TODO
								// show the key list -- START
//								if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
//									Log.e("TESTTESTDB", "have the table");
//									if (DBCount() > 0) {
//										Log.e("TESTTESTDB", "table is not empty");
//										Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME, null);
//										if (mCursor.moveToFirst()) {
//
//											doorNameList = new ArrayList<HashMap<String, String>>();
//											mAdapter = new KeyListAdapter(KeyList.this, doorNameList);
//											mKeyList.setAdapter(mAdapter);
//
//											int deviceIdIndex = mCursor.getColumnIndex("deviceId");
//											int doorNamemIndex = mCursor.getColumnIndex("doorName");
//											int authFromIndex = mCursor.getColumnIndex("authFrom");
//											int authToIndex = mCursor.getColumnIndex("authTo");
//											int doorTypeIndex = mCursor.getColumnIndex("doorType");
//											int authTypeIndex = mCursor.getColumnIndex("authType");
//
//											do {
//												String deviceId = mCursor.getString(deviceIdIndex);
//												String doorName = mCursor.getString(doorNamemIndex);
//												String authFrom = mCursor.getString(authFromIndex);
//												String authTo = mCursor.getString(authToIndex);
//												String doorType = mCursor.getString(doorTypeIndex);
//												String authType = mCursor.getString(authTypeIndex);
//
//												Log.e("TESTTESTDB deviceId =", deviceId);
//												Log.e("TESTTESTDB doorName =", doorName);
//												Log.e("TESTTESTDB authFrom =", authFrom);
//												Log.e("TESTTESTDB authTo =", authTo);
//												Log.e("TESTTESTDB doorType =", doorType);
//												Log.e("TESTTESTDB authType =", authType);
//
//												HashMap<String, String> keyFromDB = new HashMap<String, String>();
//												keyFromDB.put("Door", doorName);
//												keyFromDB.put("BEGIN", authFrom);
//												keyFromDB.put("END", authTo);
//												keyFromDB.put("AuthType", authType);
//
//												doorNameList.add(keyFromDB);
//												mAdapter.notifyDataSetChanged();
//
//											} while (mCursor.moveToNext());
//										}
//										mCursor.close();
//									}
//								}
								// show the key list -- END
							} else if (statusCode == -81) {
								Toast.makeText(KeyList.this, R.string.have_no_key_authorised, Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

					}
				}){
			@Override
			protected Map<String, String> getParams()
					throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("appId", uuid);
				return map;
			}
		};
		mQueue.add(mJsonRequest);
		
		if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
			Log.e("TESTTESTDB", "have the table");
			if (DBCount() > 0) {
				Log.e("TESTTESTDB", "table is not empty");
				Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME, null);
				if (mCursor.moveToFirst()) {

					doorNameList = new ArrayList<HashMap<String, String>>();
					mAdapter = new KeyListAdapter(KeyList.this, doorNameList);
					mKeyList.setAdapter(mAdapter);

					int deviceIdIndex = mCursor.getColumnIndex("deviceId");
					int doorNamemIndex = mCursor.getColumnIndex("doorName");
					int authFromIndex = mCursor.getColumnIndex("authFrom");
					int authToIndex = mCursor.getColumnIndex("authTo");
					int doorTypeIndex = mCursor.getColumnIndex("doorType");
					int authTypeIndex = mCursor.getColumnIndex("authType");

					do {
						String deviceId = mCursor.getString(deviceIdIndex);
						String doorName = mCursor.getString(doorNamemIndex);
						String authFrom = mCursor.getString(authFromIndex);
						String authTo = mCursor.getString(authToIndex);
						String doorType = mCursor.getString(doorTypeIndex);
						String authType = mCursor.getString(authTypeIndex);

						Log.e("TESTTESTDB deviceId =", deviceId);
						Log.e("TESTTESTDB doorName =", doorName);
						Log.e("TESTTESTDB authFrom =", authFrom);
						Log.e("TESTTESTDB authTo =", authTo);
						Log.e("TESTTESTDB doorType =", doorType);
						Log.e("TESTTESTDB authType =", authType);

						HashMap<String, String> keyFromDB = new HashMap<String, String>();
						keyFromDB.put("Door", doorName);
						keyFromDB.put("BEGIN", authFrom);
						keyFromDB.put("END", authTo);
						keyFromDB.put("AuthType", authType);

						doorNameList.add(keyFromDB);
						mAdapter.notifyDataSetChanged();

					} while (mCursor.moveToNext());
				}
				mCursor.close();
			}
		}
				
		IvBack = (RelativeLayout) findViewById(R.id.btn_back_key_list);
		IvBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});

	}

	/*
	 * 钥匙参数说明
	 * authType: 	1 - 永久		
	 *                	2 - 长久	
	 *                	3 - 临时
	 * doorType: 	1 - 人行门	
	 *                	2 - 车门
	 */
	public void parseKeyData(JSONObject response) {
		ArrayList<HashMap<String, String>> doorNameList = new ArrayList<HashMap<String, String>>();

		try {
			JSONArray dataArray = response.getJSONArray("data"); // 得到"data"这个array
			
			for (int indexL1 = 0; indexL1 < dataArray.length(); indexL1++) {
				JSONObject L1Data = (JSONObject) dataArray.get(indexL1); // 得到里面的具体object--第i层的具体数据

				/*
				 * 第一层L1：小区级别钥匙信息
				 */
				L1ZoneName = L1Data.getString("zoneName"); 
				L1ZoneID = L1Data.getString("zoneId");
				JSONArray L1Doors = L1Data.getJSONArray("doors");
				for (int L1DoorIndex = 0; L1DoorIndex < L1Doors.length(); L1DoorIndex++) {
					HashMap<String, String> mapL1 = new HashMap<String, String>();
					JSONObject L1DoorsData = (JSONObject) L1Doors.get(L1DoorIndex);

					ContentValues valuesL1 = new ContentValues();			
					if (L1DoorsData.getString("deviceId") != null) {
//						mapL1.put("DoorName", L1ZoneName+L1DoorsData.getString("doorName"));
//						mapL1.put("DoorId", L1DoorsData.getString("doorId"));
//						mapL1.put("DeviceId", L1DoorsData.getString("deviceId"));
//						mapL1.put("DoorType", String.valueOf(L1DoorsData.getInt("doorType")));
//						mapL1.put("AuthType", String.valueOf(L1DoorsData.getInt("authType")));
//						mapL1.put("AuthFrom", L1DoorsData.getString("authFrom"));
//						mapL1.put("AuthTo", L1DoorsData.getString("authTo"));
//						doorNameList.add(mapL1);
						if(!hasData(mKeyDB, L1DoorsData.getString("deviceId"))){
							valuesL1.put("zoneName", L1ZoneName);
							valuesL1.put("zoneId", L1ZoneID);
							valuesL1.put("doorName", L1ZoneName+L1DoorsData.getString("doorName"));
							valuesL1.put("doorId", L1DoorsData.getString("doorId"));
							valuesL1.put("deviceId", L1DoorsData.getString("deviceId"));
							valuesL1.put("doorType", String.valueOf(L1DoorsData.getInt("doorType")));
							valuesL1.put("authType", String.valueOf(L1DoorsData.getInt("authType")));
							valuesL1.put("authFrom", L1DoorsData.getString("authFrom"));
							valuesL1.put("authTo", L1DoorsData.getString("authTo"));
							mKeyDB.insert(TABLE_NAME, null, valuesL1);
						}else{
							
						}
					}else
						continue;
				}
				
				/*
				 * 第二层L2：园区级别钥匙信息
				 */
				JSONArray L2DataArray = L1Data.getJSONArray("zones"); 
				for(int indexL2 = 0; indexL2 < L2DataArray.length(); indexL2++){
					
					JSONObject L2Data =  (JSONObject) L2DataArray.get(indexL2);
					L2ZoneName = L2Data.getString("zoneName");
					L2ZoneID = L2Data.getString("zoneId");
					JSONArray L2Doors = L2Data.getJSONArray("doors");
					for (int L2DoorIndex = 0; L2DoorIndex < L2Doors.length(); L2DoorIndex++) {
						HashMap<String, String> mapL2 = new HashMap<String, String>();
						JSONObject L2DoorsData = (JSONObject) L2Doors.get(L2DoorIndex);

						ContentValues valuesL2 = new ContentValues();
						if (L2DoorsData.getString("deviceId") != null) {
//								mapL2.put("DoorName", L1ZoneName+L2ZoneName+L2DoorsData.getString("doorName"));
//								mapL2.put("DoorId", L2DoorsData.getString("doorId"));
//								mapL2.put("DeviceId", L2DoorsData.getString("deviceId"));
//								mapL2.put("DoorType", String.valueOf(L2DoorsData.getInt("doorType")));
//								mapL2.put("AuthType", String.valueOf(L2DoorsData.getInt("authType")));
//								mapL2.put("AuthFrom", L2DoorsData.getString("authFrom"));
//								mapL2.put("AuthTo", L2DoorsData.getString("authTo"));
//								doorNameList.add(mapL2);
							if(!hasData(mKeyDB, L2DoorsData.getString("deviceId"))){
								valuesL2.put("zoneName", L2ZoneName);
								valuesL2.put("zoneId", L2ZoneID);
								valuesL2.put("doorName", L1ZoneName+L2ZoneName+L2DoorsData.getString("doorName"));
								valuesL2.put("doorId", L2DoorsData.getString("doorId"));
								valuesL2.put("deviceId", L2DoorsData.getString("deviceId"));
								valuesL2.put("doorType", String.valueOf(L2DoorsData.getInt("doorType")));
								valuesL2.put("authType", String.valueOf(L2DoorsData.getInt("authType")));
								valuesL2.put("authFrom", L2DoorsData.getString("authFrom"));
								valuesL2.put("authTo", L2DoorsData.getString("authTo"));
								mKeyDB.insert(TABLE_NAME, null, valuesL2);
							}
						}else 
							continue;
					}
					
					/*
					 * 第三层L3：楼栋级别钥匙信息
					 */
					JSONArray L3DataArray = L2Data.getJSONArray("zones");
					for(int indexL3 = 0; indexL3 < L3DataArray.length(); indexL3++){
						JSONObject L3Data =  (JSONObject) L3DataArray.get(indexL3);
						L3ZoneName = L3Data.getString("zoneName");
						L3ZoneID = L3Data.getString("zoneId");
						JSONArray L3Doors = L3Data.getJSONArray("doors");
						for (int L3DoorIndex = 0; L3DoorIndex < L3Doors.length(); L3DoorIndex++) {
							HashMap<String, String> mapL3 = new HashMap<String, String>();
							JSONObject L3DoorsData = (JSONObject) L3Doors.get(L3DoorIndex);

							ContentValues valuesL3 = new ContentValues();
							if (L3DoorsData.getString("deviceId") != null) {
//									mapL3.put("DoorName", L1ZoneName+L2ZoneName+L3ZoneName+L3DoorsData.getString("doorName"));
//									mapL3.put("DoorId", L3DoorsData.getString("doorId"));
//									mapL3.put("DeviceId", L3DoorsData.getString("deviceId"));
//									mapL3.put("DoorType", String.valueOf(L3DoorsData.getInt("doorType")));
//									mapL3.put("AuthType", String.valueOf(L3DoorsData.getInt("authType")));
//									mapL3.put("AuthFrom", L3DoorsData.getString("authFrom"));
//									mapL3.put("AuthTo", L3DoorsData.getString("authTo"));
//									doorNameList.add(mapL3);
								if(!hasData(mKeyDB, L3DoorsData.getString("deviceId"))){
									valuesL3.put("zoneName", L3ZoneName);
									valuesL3.put("zoneId", L3ZoneID);
									valuesL3.put("doorName", L1ZoneName+L2ZoneName+L3ZoneName+L3DoorsData.getString("doorName"));
									valuesL3.put("doorId", L3DoorsData.getString("doorId"));
									valuesL3.put("deviceId", L3DoorsData.getString("deviceId"));
									valuesL3.put("doorType", String.valueOf(L3DoorsData.getInt("doorType")));
									valuesL3.put("authType", String.valueOf(L3DoorsData.getInt("authType")));
									valuesL3.put("authFrom", L3DoorsData.getString("authFrom"));
									valuesL3.put("authTo", L3DoorsData.getString("authTo"));
									mKeyDB.insert(TABLE_NAME, null, valuesL3);
								}
							}else 
								continue;
						}
					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private class KeyListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private ArrayList<HashMap<String, String>> doorNameList;

		// public void setDataList(ArrayList<HashMap<String, String>> list) {
		// doorNameList = list;
		// notifyDataSetChanged();
		// }

		public KeyListAdapter(Context context,
				ArrayList<HashMap<String, String>> list) {
			this.doorNameList = list;
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return doorNameList.size();
		}

		@Override
		public Object getItem(int position) {
			return doorNameList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.key_list_item, null);
				holder = new ViewHolder();
				holder.doorname = (TextView) convertView.findViewById(R.id.door_name);
				holder.beginday = (TextView) convertView.findViewById(R.id.door_time_from);
				holder.endday = (TextView) convertView.findViewById(R.id.door_time_to);
				holder.keyItemBg = (LinearLayout) convertView.findViewById(R.id.key_item_bg);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.doorname.setSelected(true);
			holder.doorname.setText(doorNameList.get(position).get("Door"));
			holder.beginday.setText(doorNameList.get(position).get("BEGIN"));
			holder.endday.setText(doorNameList.get(position).get("END"));
			
			if(doorNameList.get(position).get("AuthType").equals("1")){
				holder.keyItemBg.setBackgroundResource(R.drawable.key_list_back_forever);
			}else if(doorNameList.get(position).get("AuthType").equals("2")){
				holder.keyItemBg.setBackgroundResource(R.drawable.key_list_back_long);
			}else if(doorNameList.get(position).get("AuthType").equals("3")){
				holder.keyItemBg.setBackgroundResource(R.drawable.key_list_back_temp);
			}

			return convertView;
		}

		class ViewHolder {
			public TextView doorname;
			public TextView beginday;
			public TextView endday;
			public LinearLayout keyItemBg;
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
	
	
	//返回数据表KeyInfoTable里面记录的数量
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
		return hasData;
	}
}