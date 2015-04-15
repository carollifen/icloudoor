package com.icloudoor.clouddoor;

import java.io.*;
import org.json.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
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
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class KeyList extends Activity{

	private MyDataBaseHelper mKeyDBHelper;
	private SQLiteDatabase mKeyDB;
	private final String DATABASE_NAME = "KeyDB.db";
	private final String TABLE_NAME = "KeyInfoTable";

	private ImageView IvBack;

	private URL downLoadKeyURL;
	private RequestQueue mQueue;

	private String HOST = "http://zone.icloudoor.com/icloudoor-web";
	private String sid = null;
	private JsonObjectRequest mJsonRequest;
	
	private JSONObject oldData;

	// Door info variable
	ListView mKeyList;
	KeyListAdapter mAdapter;
	ArrayList<HashMap<String, String>> doorNameList;
	String keyBeginDay;
	String keyEndDay;
	String L1ZoneName;
	String L1ZoneID;
	String L2ZoneName;
	String L2ZoneID;
	String L3ZoneName;
	String L3ZoneID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.key_list);
		
		mKeyList = (ListView) findViewById(R.id.key_listview);

		mQueue = Volley.newRequestQueue(this);
		sid = loadSid();
		try {
			downLoadKeyURL = new URL(HOST + "/user/door/download.do" + "?sid="
					+ sid);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		mJsonRequest = new JsonObjectRequest(Method.POST,
				downLoadKeyURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						doorNameList = parseKeyData(response);
						mAdapter = new KeyListAdapter(KeyList.this,
								doorNameList);
						mKeyList.setAdapter(mAdapter);
						Log.e("TEST", response.toString());
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
		mQueue.add(mJsonRequest);
		
		IvBack = (ImageView) findViewById(R.id.btn_back_key_list);
		IvBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});

	}

	public ArrayList<HashMap<String, String>> parseKeyData(JSONObject response) {
		ArrayList<HashMap<String, String>> doorNameList = new ArrayList<HashMap<String, String>>();
		doorNameList = new ArrayList<HashMap<String, String>>();
		mKeyDBHelper = new MyDataBaseHelper(KeyList.this,
				DATABASE_NAME);
		mKeyDB = mKeyDBHelper.getWritableDatabase();
		
		try {
			JSONArray dataArray = response.getJSONArray("data"); // 得到"data"这个array

			for (int indexL1 = 0; indexL1 < dataArray.length(); indexL1++) {
				JSONObject L1Data = (JSONObject) dataArray.get(indexL1); // 得到里面的具体object--第i层的具体数据
				keyBeginDay = L1Data.getString("authFrom");
				keyEndDay = L1Data.getString("authTo");
				L1ZoneName = L1Data.getString("l1ZoneName");
				L1ZoneID = L1Data.getString("l1ZoneId");

				JSONArray L1CarDoors = L1Data.getJSONArray("carDoors");
				for (int indexL1Car = 0; indexL1Car < L1CarDoors.length(); indexL1Car++) {
					HashMap<String, String> mapL1Car = new HashMap<String, String>();
					JSONObject L1CarDoorsData = (JSONObject) L1CarDoors
							.get(indexL1Car);

					ContentValues values = new ContentValues();
					if (L1CarDoorsData.getInt("status") == 1) {
						mapL1Car.put(
								"Door",
								L1ZoneName
										+ L1CarDoorsData.getString("doorName"));
						mapL1Car.put("BEGIN", keyBeginDay);
						mapL1Car.put("END", keyEndDay);
						mapL1Car.put("DOORID",
								L1CarDoorsData.getString("doorId"));
						mapL1Car.put("DEVICEID",
								L1CarDoorsData.getString("deviceId"));
						doorNameList.add(mapL1Car);
						
						values.put("doorId", L1CarDoorsData.getString("doorId"));
						values.put("doorName",
								L1ZoneName + L1CarDoorsData.getString("doorName"));
						values.put("deviceId", L1CarDoorsData.getString("deviceId"));
						values.put("authFrom", keyBeginDay);
						values.put("authTo", keyEndDay);
						values.put("status", L1CarDoorsData.getInt("status"));
						mKeyDB.insert(TABLE_NAME, null, values);
					}
				}

				JSONArray L1ManDoors = L1Data.getJSONArray("normalDoors");
				for (int indexL1Man = 0; indexL1Man < L1ManDoors.length(); indexL1Man++) {
					HashMap<String, String> mapL1Man = new HashMap<String, String>();
					JSONObject L1ManDoorsData = (JSONObject) L1ManDoors
							.get(indexL1Man);

					ContentValues values = new ContentValues();
					if (L1ManDoorsData.getInt("status") == 1) {
						mapL1Man.put(
								"Door",
								L1ZoneName
										+ L1ManDoorsData.getString("doorName"));
						mapL1Man.put("BEGIN", keyBeginDay);
						mapL1Man.put("END", keyEndDay);
						mapL1Man.put("DOORID",
								L1ManDoorsData.getString("doorId"));
						mapL1Man.put("DEVICEID",
								L1ManDoorsData.getString("deviceId"));
						doorNameList.add(mapL1Man);
						
						values.put("doorId", L1ManDoorsData.getString("doorId"));
						values.put("doorName",
								L1ZoneName + L1ManDoorsData.getString("doorName"));
						values.put("deviceId", L1ManDoorsData.getString("deviceId"));
						values.put("authFrom", keyBeginDay);
						values.put("authTo", keyEndDay);
						values.put("status", L1ManDoorsData.getInt("status"));
						mKeyDB.insert(TABLE_NAME, null, values);
					}
				}

				JSONArray L2DataArray = L1Data.getJSONArray("l2Zones"); // 得到"l2Zones"这个array
				for (int indexL2 = 0; indexL2 < L2DataArray.length(); indexL2++) {
					JSONObject L2Data = (JSONObject) L2DataArray.get(indexL2);
					L2ZoneName = L2Data.getString("l2ZoneName");
					L2ZoneID = L2Data.getString("l2ZoneId");

					JSONArray L2CarDoors = L2Data.getJSONArray("carDoors");
					for (int indexL2Car = 0; indexL2Car < L2CarDoors.length(); indexL2Car++) {
						HashMap<String, String> mapL2Car = new HashMap<String, String>();
						JSONObject L2CarDoorsData = (JSONObject) L2CarDoors
								.get(indexL2Car);

						ContentValues values = new ContentValues();
						if (L2CarDoorsData.getInt("status") == 1) {
							mapL2Car.put("Door", L1ZoneName + L2ZoneName
									+ L2CarDoorsData.getString("doorName"));
							mapL2Car.put("BEGIN", keyBeginDay);
							mapL2Car.put("END", keyEndDay);
							mapL2Car.put("DOORID",
									L2CarDoorsData.getString("doorId"));
							mapL2Car.put("DEVICEID",
									L2CarDoorsData.getString("deviceId"));
							doorNameList.add(mapL2Car);
							
							values.put("doorId", L2CarDoorsData.getString("doorId"));
							values.put("doorName", L1ZoneName + L2ZoneName
									+ L2CarDoorsData.getString("doorName"));
							values.put("deviceId",
									L2CarDoorsData.getString("deviceId"));
							values.put("authFrom", keyBeginDay);
							values.put("authTo", keyEndDay);
							values.put("status", L2CarDoorsData.getInt("status"));
							mKeyDB.insert(TABLE_NAME, null, values);
						}
					}

					JSONArray L2ManDoors = L2Data.getJSONArray("normalDoors");
					for (int indexL2Man = 0; indexL2Man < L2ManDoors.length(); indexL2Man++) {
						HashMap<String, String> mapL2Man = new HashMap<String, String>();
						JSONObject L2ManDoorsData = (JSONObject) L2ManDoors
								.get(indexL2Man);

						ContentValues values = new ContentValues();
						if (L2ManDoorsData.getInt("status") == 1) {
							mapL2Man.put("Door", L1ZoneName + L2ZoneName
									+ L2ManDoorsData.getString("doorName"));
							mapL2Man.put("BEGIN", keyBeginDay);
							mapL2Man.put("END", keyEndDay);
							mapL2Man.put("DOORID",
									L2ManDoorsData.getString("doorId"));
							mapL2Man.put("DEVICEID",
									L2ManDoorsData.getString("deviceId"));
							doorNameList.add(mapL2Man);
							
							values.put("doorId", L2ManDoorsData.getString("doorId"));
							values.put("doorName", L1ZoneName + L2ZoneName
									+ L2ManDoorsData.getString("doorName"));
							values.put("deviceId",
									L2ManDoorsData.getString("deviceId"));
							values.put("authFrom", keyBeginDay);
							values.put("authTo", keyEndDay);
							values.put("status", L2ManDoorsData.getInt("status"));
							mKeyDB.insert(TABLE_NAME, null, values);
						}
					}

					JSONArray L3DataArray = L2Data.getJSONArray("l3Zones"); // 得到"l3Zones"这个array
					for (int indexL3 = 0; indexL3 < L3DataArray.length(); indexL3++) {
						JSONObject L3Data = (JSONObject) L3DataArray
								.get(indexL3);
						L3ZoneName = L3Data.getString("l3ZoneName");
						L3ZoneID = L3Data.getString("l3ZoneId");

						JSONArray L3CarDoors = L3Data.getJSONArray("carDoors");
						for (int indexL3Car = 0; indexL3Car < L3CarDoors
								.length(); indexL3Car++) {
							HashMap<String, String> mapL3Car = new HashMap<String, String>();
							JSONObject L3CarDoorsData = (JSONObject) L3CarDoors
									.get(indexL3Car);

							ContentValues values = new ContentValues();
							if (L3CarDoorsData.getInt("status") == 1) {
								mapL3Car.put(
										"Door",
										L1ZoneName
												+ L2ZoneName
												+ L3ZoneName
												+ L3CarDoorsData
														.getString("doorName"));
								mapL3Car.put("BEGIN", keyBeginDay);
								mapL3Car.put("END", keyEndDay);
								mapL3Car.put("DOORID",
										L3CarDoorsData.getString("doorId"));
								mapL3Car.put("DEVICEID",
										L3CarDoorsData.getString("deviceId"));
								doorNameList.add(mapL3Car);
								
								values.put("doorId",
										L3CarDoorsData.getString("doorId"));
								values.put(
										"doorName",
										L1ZoneName
												+ L2ZoneName
												+ L3ZoneName
												+ L3CarDoorsData
														.getString("doorName"));
								values.put("deviceId",
										L3CarDoorsData.getString("deviceId"));
								values.put("authFrom", keyBeginDay);
								values.put("authTo", keyEndDay);
								values.put("status",
										L3CarDoorsData.getInt("status"));
								mKeyDB.insert(TABLE_NAME, null, values);
							}
						}

						JSONArray L3ManDoors = L3Data
								.getJSONArray("normalDoors");
						for (int indexL3Man = 0; indexL3Man < L3ManDoors
								.length(); indexL3Man++) {
							HashMap<String, String> mapL3Man = new HashMap<String, String>();
							JSONObject L3ManDoorsData = (JSONObject) L3ManDoors
									.get(indexL3Man);

							ContentValues values = new ContentValues();
							if (L3ManDoorsData.getInt("status") == 1) {
								mapL3Man.put(
										"Door",
										L1ZoneName
												+ L2ZoneName
												+ L3ZoneName
												+ L3ManDoorsData
														.getString("doorName"));
								mapL3Man.put("BEGIN", keyBeginDay);
								mapL3Man.put("END", keyEndDay);
								mapL3Man.put("DOORID",
										L3ManDoorsData.getString("doorId"));
								mapL3Man.put("DEVICEID",
										L3ManDoorsData.getString("deviceId"));
								doorNameList.add(mapL3Man);
								
								values.put("doorId",
										L3ManDoorsData.getString("doorId"));
								values.put(
										"doorName",
										L1ZoneName
												+ L2ZoneName
												+ L3ZoneName
												+ L3ManDoorsData
														.getString("doorName"));
								values.put("deviceId",
										L3ManDoorsData.getString("deviceId"));
								values.put("authFrom", keyBeginDay);
								values.put("authTo", keyEndDay);
								values.put("status",
										L3ManDoorsData.getInt("status"));
								mKeyDB.insert(TABLE_NAME, null, values);
							}
						}
					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		mKeyDB.close();
		return doorNameList;
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
				holder.doorname = (TextView) convertView
						.findViewById(R.id.door_name);
				holder.beginday = (TextView) convertView
						.findViewById(R.id.door_time_from);
				holder.endday = (TextView) convertView
						.findViewById(R.id.door_time_to);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.doorname.setText(doorNameList.get(position).get("Door"));
			holder.beginday.setText(doorNameList.get(position).get("BEGIN"));
			holder.endday.setText(doorNameList.get(position).get("END"));

			return convertView;
		}

		class ViewHolder {
			public TextView doorname;
			public TextView beginday;
			public TextView endday;
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

	
	public void saveKeyListInfo(JSONObject data) {
		SharedPreferences savedKeyList = getSharedPreferences("SAVEDKEYLIST",
				MODE_PRIVATE);
		Editor editor = savedKeyList.edit();
		editor.putString("KEYLIST", data.toString());
		editor.commit();
	}
	
	public JSONObject loadKey() {
		SharedPreferences loadKeyList = getSharedPreferences("SAVEDKEYLIST", 0);
		try {
			return new JSONObject(loadKeyList.getString("KEYLIST", null));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}