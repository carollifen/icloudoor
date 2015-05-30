package com.icloudoor.clouddoor;

import java.lang.reflect.Field;
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

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class KeyListListFragment extends Fragment {
	
	private String TAG = this.getClass().getSimpleName();
	
	private final String DATABASE_NAME = "KeyDB.db";
	private final String TABLE_NAME = "KeyInfoTable";
	private final String ZONE_TABLE_NAME = "ZoneTable";
	private final String CAR_TABLE_NAME = "CarKeyTable";
	private MyDataBaseHelper mKeyDBHelper;
	private SQLiteDatabase mKeyDB;
	
	private URL returnCarKeyURL;
	private URL downLoadKeyURL;
	private RequestQueue mQueue;

	private String HOST = "http://zone.icloudoor.com/icloudoor-web";
	private String sid = null;
	private String uuid = null;
	private MyJsonObjectRequest mJsonRequest;
	private int statusCode;
	
	// Door info variable
	private ListView mKeyList;
	private ArrayList<HashMap<String, String>> doorNameList;
	private KeyListAdapter mAdapter;
	
	private ListView mTempKeyList;
	private ArrayList<HashMap<String, String>> tempDoorNameList;
	private TempKeyListAdapter mTempAdapter;
	
	//
	LinearLayout blankView;
	
	// for new key download interface
	private String ZONEID;
	private String DOORNAME;
	private String DOORID;
	private String DEVICEID;
	private String DOORTYPE;
	private String PLATENUM;
	private String DIRECTION; // 1.go in 2.go out
	private String AUTHFROM;
	private String AUTHTO;
	private String CARSTATUS; // 1. own car 2.borrow car 3.lend car
	private String CARPOSSTATUS; // 1.init 2.inside 3.outside

	private SharedPreferences carNumAndPhoneNumShare;
	
	public KeyListListFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_key_list_list, container, false);
		
		carNumAndPhoneNumShare = getActivity().getSharedPreferences("carNumAndPhoneNum", 0);
		
		mKeyList = (ListView) view.findViewById(R.id.key_listview);
		mTempKeyList = (ListView) view.findViewById(R.id.temp_key_listview);
		
		mKeyDBHelper = new MyDataBaseHelper(getActivity(), DATABASE_NAME);
		mKeyDB = mKeyDBHelper.getWritableDatabase();
		
		blankView = (LinearLayout) view.findViewById(R.id.blankview);
		blankView.setVisibility(View.GONE);
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
				
		mQueue = Volley.newRequestQueue(getActivity());
		
		sid = loadSid();
		uuid = loadUUID();
		if (uuid == null) {
			uuid = UUID.randomUUID().toString().replaceAll("-", "");
			saveUUID(uuid);
		}

		try {
//			downLoadKeyURL = new URL(HOST + "/user/door/download.do" + "?sid=" + sid);
			downLoadKeyURL = new URL(HOST + "/user/door/download2.do" + "?sid=" + sid);         //new key download interface
			Log.e(TAG, String.valueOf(downLoadKeyURL));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		if(getActivity() != null)
			Toast.makeText(getActivity(), R.string.downloading_key_list, Toast.LENGTH_SHORT).show();
		
		mJsonRequest = new MyJsonObjectRequest(Method.POST, downLoadKeyURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							statusCode = response.getInt("code");
							if (statusCode == 1) {

								parseKeyData(response);

								Log.e("TEST", response.toString());
								
								if(response.getString("sid") != null)
									saveSid(response.getString("sid"));

								
								// TODO show the keys	
								doorNameList = new ArrayList<HashMap<String, String>>();
								mAdapter = new KeyListAdapter(getActivity(), doorNameList);
								mKeyList.setAdapter(mAdapter);
								
								tempDoorNameList = new ArrayList<HashMap<String, String>>();
								mTempAdapter = new TempKeyListAdapter(getActivity(), tempDoorNameList);
								mTempKeyList.setAdapter(mTempAdapter);
								
								if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
									if (DBCount() > 0) {
										Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME, null);
										if (mCursor.moveToFirst()) {
											int authFromIndex = mCursor.getColumnIndex("authFrom");
											int authToIndex = mCursor.getColumnIndex("authTo");
											int doorNamemIndex = mCursor.getColumnIndex("doorName");
											int doorTypeIndex = mCursor.getColumnIndex("doorType");
											int zoneIdIndex = mCursor.getColumnIndex("zoneId");
											int carNumIndex = mCursor.getColumnIndex("plateNum");
											
											do{
												String doorName = mCursor.getString(doorNamemIndex);
												String authFrom = mCursor.getString(authFromIndex);
												String authTo = mCursor.getString(authToIndex);
												String doorType = mCursor.getString(doorTypeIndex);
												String zoneId = mCursor.getString(zoneIdIndex);
												String carNum = mCursor.getString(carNumIndex);
												
												HashMap<String, String> keyFromDB = new HashMap<String, String>();
												if(doorType.equals("1")){							
													keyFromDB.put("Door", doorName);
													keyFromDB.put("BEGIN", authFrom);
													keyFromDB.put("END", authTo);
													keyFromDB.put("STATUS", "none");
													doorNameList.add(keyFromDB);
													mAdapter.notifyDataSetChanged();
												}else if(doorType.equals("2")){
													if (mKeyDBHelper.tabIsExist(CAR_TABLE_NAME)) {
														if(DBCountCar() > 0){
															Cursor mCursorCar = mKeyDB.rawQuery("select * from " + CAR_TABLE_NAME, null);
															if (mCursorCar.moveToFirst()) {
																
																int l1ZoneIdIndex = mCursorCar.getColumnIndex("l1ZoneId");
																int plateNumIndex = mCursorCar.getColumnIndex("plateNum");
																int carStatusIndex = mCursorCar.getColumnIndex("carStatus");
																do{
																	String l1ZoneId = mCursorCar.getString(l1ZoneIdIndex);
																	String plateNum = mCursorCar.getString(plateNumIndex);
																	String carStatus = mCursorCar.getString(carStatusIndex);
																	
																	if(zoneId.equals(l1ZoneId) && carNum.equals(plateNum)){
																		if(carStatus.equals("2")){ //temp car key
																			blankView.setVisibility(View.VISIBLE);
																			HashMap<String, String> tempKeyFromDB = new HashMap<String, String>();
																			tempKeyFromDB.put("Door", doorName);
																			tempKeyFromDB.put("CARNUM", plateNum);
																			tempDoorNameList.add(tempKeyFromDB);
																			mTempAdapter.notifyDataSetChanged();
																		}else{
																			keyFromDB.put("Door", doorName);
																			keyFromDB.put("BEGIN", authFrom);
																			keyFromDB.put("END", authTo);
																			keyFromDB.put("STATUS", carStatus);
																			doorNameList.add(keyFromDB);
																			mAdapter.notifyDataSetChanged();
																		}
																	}
																	
																}while(mCursorCar.moveToNext());
															}
															mCursorCar.close();
														}
													}
												}
												
											}while(mCursor.moveToNext());
										}
										mCursor.close();
									}
								}
								
								
								
//								if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
//									Log.e("TESTTESTDB", "have the table");
//									if (DBCount() > 0) {
//										Log.e(TAG, String.valueOf(DBCount()));
//										Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME, null);
//										if (mCursor.moveToFirst()) {
//
//											doorNameList = new ArrayList<HashMap<String, String>>();
//											mAdapter = new KeyListAdapter(getActivity(), doorNameList);
//											mKeyList.setAdapter(mAdapter);
//											
//											tempDoorNameList = new ArrayList<HashMap<String, String>>();
//											mTempAdapter = new TempKeyListAdapter(getActivity(), tempDoorNameList);
//											mTempKeyList.setAdapter(mTempAdapter);
//											
//											int directionIndex = mCursor.getColumnIndex("direction");
//											int zoneIdIndex = mCursor.getColumnIndex("zoneId");
//											int deviceIdIndex = mCursor.getColumnIndex("deviceId");
//											int doorNamemIndex = mCursor.getColumnIndex("doorName");
//											int authFromIndex = mCursor.getColumnIndex("authFrom");
//											int authToIndex = mCursor.getColumnIndex("authTo");
//											int doorTypeIndex = mCursor.getColumnIndex("doorType");
////											int carStatusIndex = mCursor.getColumnIndex("carStatus");
//											int carNumIndex = mCursor.getColumnIndex("plateNum");
////											int carPosStatusIndex = mCursor.getColumnIndex("carPosStatus");
//
//											do {
//												String deviceId = mCursor.getString(deviceIdIndex);
//												String doorName = mCursor.getString(doorNamemIndex);
//												String authFrom = mCursor.getString(authFromIndex);
//												String authTo = mCursor.getString(authToIndex);
//												String doorType = mCursor.getString(doorTypeIndex);
////												String carStatus = mCursor.getString(carStatusIndex);
//												String carNum = mCursor.getString(carNumIndex);
//												String zoneId = mCursor.getString(zoneIdIndex);
////												String carPosStatus = mCursor.getString(carPosStatusIndex);
//												String direction = mCursor.getString(directionIndex);
//
//
//												Log.e("TESTTESTDB deviceId =", deviceId);
//												Log.e("TESTTESTDB doorName =", doorName);
//												Log.e("TESTTESTDB authFrom =", authFrom);
//												Log.e("TESTTESTDB authTo =", authTo);
//												Log.e("TESTTESTDB doorType =", doorType);
////												Log.e("TESTTESTDB carStatus =", carStatus);
//												Log.e("TESTTESTDB carNum =", carNum);
//												Log.e("TESTTESTDB zoneId =", zoneId);
////												Log.e("TESTTESTDB carPosStatus =", carPosStatus);
//												Log.e("TESTTESTDB direction =", direction);
//												
//												
//												/*  Add new logic for car key
//												 * 
//												 */
//												if(carStatus.equals("2")){
//													blankView.setVisibility(View.VISIBLE);
//													
//													Log.e(TAG, "add temp key");
//													
//													HashMap<String, String> tempKeyFromDB = new HashMap<String, String>();
//													tempKeyFromDB.put("Door", doorName);
//													tempKeyFromDB.put("BEGIN", authFrom);
//													tempKeyFromDB.put("END", authTo);
//													tempKeyFromDB.put("STATUS", carStatus);
//													tempKeyFromDB.put("DOORTYPE", doorType);
//													tempKeyFromDB.put("CARNUM", carNum);
//													tempKeyFromDB.put("ZONEID", zoneId);
//													tempKeyFromDB.put("POSSTATUS", carPosStatus);
//													tempKeyFromDB.put("DEVICEID", deviceId);
//
//													tempDoorNameList.add(tempKeyFromDB);
//													mTempAdapter.notifyDataSetChanged();
//													
//												} else {
//													HashMap<String, String> keyFromDB = new HashMap<String, String>();
//													keyFromDB.put("Door", doorName);
//													keyFromDB.put("BEGIN", authFrom);
//													keyFromDB.put("END", authTo);
//													keyFromDB.put("STATUS", carStatus);
//													keyFromDB.put("DOORTYPE", doorType);
//
//													doorNameList.add(keyFromDB);
//													mAdapter.notifyDataSetChanged();
//												}
//	
//											} while (mCursor.moveToNext());
//										}
//										mCursor.close();
//									}
//								}
							} else if (statusCode == -81) {
								if(getActivity() != null)
									Toast.makeText(getActivity(), R.string.have_no_key_authorised, Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							Log.e(TAG, "request error");
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
		
//		if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
//			Log.e("TESTTESTDBDB", "have the table");
//			if (DBCount() > 0) {
//				Log.e("TESTTESTDBDB", "table is not empty");
//				Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME, null);
//				if (mCursor.moveToFirst()) {
//
//					doorNameList = new ArrayList<HashMap<String, String>>();
//					mAdapter = new KeyListAdapter(getActivity(), doorNameList);
//					mKeyList.setAdapter(mAdapter);
//
//					int deviceIdIndex = mCursor.getColumnIndex("deviceId");
//					int doorNamemIndex = mCursor.getColumnIndex("doorName");
//					int authFromIndex = mCursor.getColumnIndex("authFrom");
//					int authToIndex = mCursor.getColumnIndex("authTo");
//					int doorTypeIndex = mCursor.getColumnIndex("doorType");
//					int carStatusIndex = mCursor.getColumnIndex("carStatus");
//
//					do {
//						String deviceId = mCursor.getString(deviceIdIndex);
//						String doorName = mCursor.getString(doorNamemIndex);
//						String authFrom = mCursor.getString(authFromIndex);
//						String authTo = mCursor.getString(authToIndex);
//						String doorType = mCursor.getString(doorTypeIndex);		
//						String carStatus = mCursor.getString(carStatusIndex);
//
//						Log.e("DBDB deviceId =", deviceId);
//						Log.e("DBDB doorName =", doorName);
//						Log.e("DBDB authFrom =", authFrom);
//						Log.e("DBDB authTo =", authTo);
//						Log.e("DBDB doorType =", doorType);
//						Log.e("DBDB carStatus =", carStatus);
//
//						HashMap<String, String> keyFromDB = new HashMap<String, String>();
//						keyFromDB.put("Door", doorName);
//						keyFromDB.put("BEGIN", authFrom);
//						keyFromDB.put("END", authTo);
//
//						doorNameList.add(keyFromDB);
//						mAdapter.notifyDataSetChanged();
//
//					} while (mCursor.moveToNext());
//				}
//				mCursor.close();
//			}
//		}
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
	 *carPosStatus:	1 - car inside the zone
	 *					  	2 - car outside the zone
	 */
	public void parseKeyData(JSONObject response) throws JSONException {
		Log.e("test for new interface", "parseKeyData func");
		
		// for new key download interface
		JSONObject data = response.getJSONObject("data");
		JSONArray doorAuths = data.getJSONArray("doorAuths");
		JSONArray zones = data.getJSONArray("zones");
		JSONArray cars = data.getJSONArray("cars");
		
		// for doorauths table -- START
		for (int index = 0; index < doorAuths.length(); index++) {
			JSONObject doorData = (JSONObject) doorAuths.get(index);
			
			ContentValues value = new ContentValues();
			
			if(doorData.getString("deviceId").length() > 0){
				if(!hasData(mKeyDB, doorData.getString("deviceId"))){    //insert the new key
					value.put("zoneId", doorData.getString("zoneId"));
					value.put("doorName", doorData.getString("doorName"));
					value.put("doorId", doorData.getString("doorId"));
					value.put("deviceId", doorData.getString("deviceId"));
					value.put("doorType", doorData.getString("doorType"));
					value.put("authFrom", doorData.getString("authFrom"));
					value.put("authTo", doorData.getString("authTo"));
					
					if (doorData.getString("doorType").equals("1")) {
						value.put("direction", "none");
						value.put("plateNum", "none");
//						value.put("carStatus", "none");
//						value.put("carPosStatus", "none");
						mKeyDB.insert(TABLE_NAME, null, value);
					} else if (doorData.getString("doorType").equals("2")){
//						JSONArray cars = data.getJSONArray("cars");
//						Log.e(TAG, "cars  " + String.valueOf(cars.length()));
//						for (int i = 0; i < cars.length(); i++) {
//							JSONObject carData = (JSONObject) cars.get(i);
//							
//							Log.e(TAG, "carData   " + carData.getString("l1ZoneId"));
//							Log.e(TAG, "doorData   " + doorData.getString("zoneId"));
//							Log.e(TAG, "carData   " + carData.getString("plateNum"));
//							Log.e(TAG, "doorData   " + doorData.getString("plateNum"));
//							
//							if (carData.getString("l1ZoneId").equals(doorData.getString("zoneId")) && carData.getString("plateNum").equals(doorData.getString("plateNum"))) {
//								Log.e(TAG, "add temp key_DB" + carData.getString("plateNum"));
								value.put("plateNum", doorData.getString("plateNum"));
								value.put("direction", doorData.getString("direction"));
//								value.put("carStatus", carData.getString("carStatus"));
//								value.put("carPosStatus", carData.getString("carPosStatus"));
								mKeyDB.insert(TABLE_NAME, null, value);
//								
//								// refresh the carPosStatus to "0" if carPosStatus not "0" when you get the new temp car key
//								if(!carData.getString("carPosStatus").equals("0")){
//									updatePosStatus(carData.getString("l1ZoneId"), carData.getString("plateNum"));
//								}
//								
//							}
//						}
					}
				} else {            // update the old key status
				
//					if(doorData.getString("doorType").equals("1")){
						ContentValues valueTemp = new ContentValues();
						valueTemp.put("authFrom", doorData.getString("authFrom"));
						valueTemp.put("authTo", doorData.getString("authTo"));
						
						mKeyDB.update("KeyInfoTable", valueTemp, "deviceId = ?", new String[] {doorData.getString("deviceId")});
//					}else if(doorData.getString("doorType").equals("2")){
//						ContentValues valueTemp1 = new ContentValues();
//						valueTemp1.put("authFrom", doorData.getString("authFrom"));
//						valueTemp1.put("authTo", doorData.getString("authTo"));
//						valueTemp1.put("direction", doorData.getString("direction"));
//						//TODO
//						JSONArray cars = data.getJSONArray("cars");
//						for (int i = 0; i < cars.length(); i++) {
//							JSONObject carData = (JSONObject) cars.get(i);
//							if (carData.getString("l1ZoneId").equals(doorData.getString("zoneId")) && carData.getString("plateNum").equals(carNumAndPhoneNumShare.getString("CARNUM", null))) {
//								Log.e(TAG, carData.getString("carStatus"));
//								valueTemp1.put("carStatus", carData.getString("carStatus"));
//								valueTemp1.put("carPosStatus", carData.getString("carPosStatus"));
//								break;
//							}
//						}
//						
//						mKeyDB.update("KeyInfoTable", valueTemp1, "deviceId = ?", new String[] {doorData.getString("deviceId")});
//					}
				}
			}	
		}
		
		//need to delete the old key
		if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
			if (DBCount() > 0) {
				Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME, null);
				if (mCursor.moveToFirst()) {
					boolean keepKey = false;
					int deviceIdIndex = mCursor.getColumnIndex("deviceId");
					String deviceId = mCursor.getString(deviceIdIndex);
		
					do{
						for (int index = 0; index < doorAuths.length(); index++) {
							JSONObject doorData = (JSONObject) doorAuths.get(index);
							
							if(doorData.getString("deviceId").length() > 0){
								if(doorData.getString("deviceId").equals(deviceId)){
									keepKey = true;
									break;
								}
							}
						}	
						
						if(!keepKey){
							//delete in the table
							mKeyDB.delete("KeyInfoTable", "deviceId = ?", new String[] {deviceId});
						}
						
					}while(mCursor.moveToNext());				
				}	
				mCursor.close();
			}
		}
		// for doorauths table -- END
		
		// for zones table -- START
		for (int index = 0; index < zones.length(); index++) {
			JSONObject zoneData = (JSONObject) zones.get(index);
			ContentValues value = new ContentValues();
			
			if(zoneData.getString("zoneId").length() > 0){
				if(!hasZoneData(mKeyDB, zoneData.getString("zoneId"))){   // insert new
					value.put("zoneid", zoneData.getString("zoneId"));		
					value.put("zonename", zoneData.getString("zoneName"));
					value.put("parentzoneid", zoneData.getString("parentZoneId"));
					mKeyDB.insert(ZONE_TABLE_NAME, null, value);
				}
			}
		}
		
		// delete old
		if (mKeyDBHelper.tabIsExist(ZONE_TABLE_NAME)) {
			if (DBCountZone() > 0) {
				Cursor mCursor = mKeyDB.rawQuery("select * from " + ZONE_TABLE_NAME, null);
				if (mCursor.moveToFirst()) {
					boolean keepKey = false;
					int zoneidIndex = mCursor.getColumnIndex("zoneid");
					String zoneid = mCursor.getString(zoneidIndex);
					
					do{
						for (int index = 0; index < zones.length(); index++) {
							JSONObject zoneData = (JSONObject) zones.get(index);
							
							if(zoneData.getString("zoneId").length() > 0){
								if(zoneData.getString("zoneId").equals(zoneid)){
									keepKey = true;
									break;
								}
							}
						}		
						
						if(!keepKey){
							mKeyDB.delete("ZoneTable", "zoneId = ?", new String[] {zoneid});
						}
					}while(mCursor.moveToNext());
				}
				mCursor.close();
			}
		}
		// for zones table -- END
		
		// for cars table -- START
		for (int index = 0; index < cars.length(); index++) {
			JSONObject carData = (JSONObject) cars.get(index);
			
			if(carData.getString("l1ZoneId").length() > 0){
				if(!hasCarData(mKeyDB, carData.getString("l1ZoneId"), carData.getString("plateNum"))){   // insert new
					ContentValues value = new ContentValues();
					value.put("l1ZoneId", carData.getString("l1ZoneId"));
					value.put("plateNum", carData.getString("plateNum"));
					value.put("carStatus", carData.getString("carStatus"));
					value.put("carPosStatus", carData.getString("carPosStatus"));
					mKeyDB.insert(CAR_TABLE_NAME, null, value);
					
					// refresh the carPosStatus to "0" if carPosStatus not "0" when you get the new temp car key
					if(!carData.getString("carPosStatus").equals("0")){
						updatePosStatus(carData.getString("l1ZoneId"), carData.getString("plateNum"));
					}
				}else{   // update old
					ContentValues value = new ContentValues();
					
					if(carData.getString("plateNum").equals(carNumAndPhoneNumShare.getString("CARNUM", null))){
						value.put("carStatus", carData.getString("carStatus"));
						value.put("carPosStatus", carData.getString("carPosStatus"));
						
						mKeyDB.update("CarKeyTable", value, "l1ZoneId = ?, plateNum = ?", new String[] {carData.getString("l1ZoneId"), carData.getString("plateNum")});
					}
				}
			}
		}
		
		// delete old
		if (mKeyDBHelper.tabIsExist(CAR_TABLE_NAME)) {
			if(DBCountCar() > 0){
				Cursor mCursor = mKeyDB.rawQuery("select * from " + CAR_TABLE_NAME, null);
				if(mCursor.moveToFirst()){
					boolean keepKey = false;
					int l1ZoneIdIndex = mCursor.getColumnIndex("l1ZoneId");
					int plateNumIndex = mCursor.getColumnIndex("plateNum");
					
					String l1ZoneId = mCursor.getString(l1ZoneIdIndex);
					String plateNum = mCursor.getString(plateNumIndex);
					
					do{
						for (int index = 0; index < cars.length(); index++) {
							JSONObject carData = (JSONObject) cars.get(index);
							
							if(carData.getString("l1ZoneId").length() > 0){
								if(carData.getString("l1ZoneId").equals(l1ZoneId) && carData.getString("plateNum").equals(plateNum)){
									keepKey = true;
									break;
								}
							}
						}
						if(!keepKey){
							mKeyDB.delete("CarKeyTable", "l1ZoneId = ?, plateNum = ?", new String[] {l1ZoneId, plateNum});
						}
					}while(mCursor.moveToNext());
				}
				mCursor.close();
			}
		}
		// for cars table -- END
	}
	
	//TODO
	// refresh the carPosStatus if carPosStatus not "0" when you get the temp car key
	public void updatePosStatus(final String zoneid, final String carnum) {
		URL updateCarPosStatusURL = null;
		String sid2 = null;
		RequestQueue mQueue2;
		
		sid2 = loadSid();
		try {
			updateCarPosStatusURL = new URL(HOST + "/user/api/updateCarPosStatus.do" + "?sid=" + sid2);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		mQueue2 = Volley.newRequestQueue(getActivity());
		
		MyJsonObjectRequest mJsonRequest2 = new MyJsonObjectRequest(Method.POST, updateCarPosStatusURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.e(TAG, "test " + response.toString());
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

					}
				}) {
			@Override
			protected Map<String, String> getParams()
					throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("l1ZoneId", zoneid);
				map.put("plateNum", carnum);
				map.put("carPosStatus", "0");
				return map;
			}
		};
		mQueue2.add(mJsonRequest2);
	}
	
	private class KeyListAdapter extends BaseAdapter {
		Context context;
//		private LayoutInflater mInflater;
		private ArrayList<HashMap<String, String>> doorNameList;

		// public void setDataList(ArrayList<HashMap<String, String>> list) {
		// doorNameList = list;
		// notifyDataSetChanged();
		// }

		public KeyListAdapter(Context context, ArrayList<HashMap<String, String>> list) {
			this.doorNameList = list;
			this.context = context;
//			this.mInflater = LayoutInflater.from(context);
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
				convertView = LayoutInflater.from(context).inflate(R.layout.key_list_item, null);
				holder = new ViewHolder();
				holder.doorname = (TextView) convertView.findViewById(R.id.door_name);
				holder.beginday = (TextView) convertView.findViewById(R.id.door_time_from);
				holder.endday = (TextView) convertView.findViewById(R.id.door_time_to);
				holder.bg = (LinearLayout) convertView.findViewById(R.id.item_bg);
				holder.keyStatus = (TextView) convertView.findViewById(R.id.key_status);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.doorname.setSelected(true);
			holder.doorname.setText(doorNameList.get(position).get("Door"));
			holder.beginday.setText(doorNameList.get(position).get("BEGIN"));
			holder.endday.setText(doorNameList.get(position).get("END"));

			if(doorNameList.get(position).get("STATUS").equals("3")){
				holder.bg.setBackgroundColor(0xffeeeeee);
				holder.keyStatus.setText(R.string.key_has_lend);
			} else {
				holder.bg.setBackgroundColor(0xffffffff);
				holder.keyStatus.setText("");
			}
			
			
			return convertView;
		}

		class ViewHolder {
			public TextView doorname;
			public TextView beginday;
			public TextView endday;
			public LinearLayout bg;
			public TextView keyStatus;
		}

	}
	
	//TODO for temp car key
	private class TempKeyListAdapter extends BaseAdapter {
		Context context;
//		private LayoutInflater mInflater;
		private ArrayList<HashMap<String, String>> tempDoorNameList;

		// public void setDataList(ArrayList<HashMap<String, String>> list) {
		// doorNameList = list;
		// notifyDataSetChanged();
		// }

		public TempKeyListAdapter(Context context, ArrayList<HashMap<String, String>> list) {
			this.tempDoorNameList = list;
			this.context = context;
//			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return tempDoorNameList.size();
		}

		@Override
		public Object getItem(int position) {
			return tempDoorNameList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.temp_car_key_item, null);
				holder = new ViewHolder();
				holder.carNum = (TextView) convertView.findViewById(R.id.temp_car_num);
				holder.tempDoorName = (TextView) convertView.findViewById(R.id.temp_car_key_name);
				holder.btnReturnCarKey = (TextView) convertView.findViewById(R.id.return_btn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.carNum.setSelected(true);
			holder.carNum.setText(tempDoorNameList.get(position).get("CARNUM"));
			holder.tempDoorName.setText(tempDoorNameList.get(position).get("Door"));
			
			final String zoneid = tempDoorNameList.get(position).get("ZONEID");
			final String carnum = tempDoorNameList.get(position).get("CARNUM");
			final String posstatus = tempDoorNameList.get(position).get("POSSTATUS");
			
			try {
				returnCarKeyURL = new URL(HOST + "/user/api/returnTempAuthCar.do" + "?sid=" + sid);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			
			holder.btnReturnCarKey.setTag(position);
			holder.btnReturnCarKey.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Log.e(TAG, "item clicked!!!");
					
					//TODO return key		
					MyJsonObjectRequest mReturnKeyRequest = new MyJsonObjectRequest(Method.POST, returnCarKeyURL.toString(), null,
							new Response.Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject response) {
										Log.e(TAG, response.toString());
										
										try {
											if(response.getInt("code") == 1){
												
												if(response.getString("sid") != null){
													saveSid(response.getString("sid"));
												}
												
												mKeyDB.delete("KeyInfoTable", "deviceId = ?", new String[] {tempDoorNameList.get(position).get("DEVICEID")});
												tempDoorNameList.remove(position);
												mTempAdapter.notifyDataSetChanged();
												if(tempDoorNameList.size() == 0){
													blankView.setVisibility(View.GONE);
												}
												
												Log.e(TAG, "car key return success!!!");
												
											}else if(response.getInt("code") == -1){
												Toast.makeText(getActivity(), R.string.wrong_params, Toast.LENGTH_SHORT).show();
											}else if(response.getInt("code") == -2){
												Toast.makeText(getActivity(), R.string.not_login, Toast.LENGTH_SHORT).show();
											}else if(response.getInt("code") == -99){
												Toast.makeText(getActivity(), R.string.unknown_err, Toast.LENGTH_SHORT).show();
											}else if(response.getInt("code") == -107){
												Toast.makeText(getActivity(), R.string.car_not_lend, Toast.LENGTH_SHORT).show();
											}
										} catch (JSONException e) {
											e.printStackTrace();
											Log.e(TAG, e.toString());
										}
										
									}
							}, new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {

								}
							}) {
						@Override
						protected Map<String, String> getParams()
								throws AuthFailureError {
							Map<String, String> map = new HashMap<String, String>();

							map.put("l1ZoneId", zoneid);
							map.put("plateNum", carnum);
							map.put("carPosStatus", posstatus);
							return map;
						}
					};
					mQueue.add(mReturnKeyRequest);
				}			
			});

			return convertView;
		}

		class ViewHolder {
			public TextView carNum;
			public TextView tempDoorName;
			public TextView btnReturnCarKey;
		}

	}
	
	public void saveSid(String sid) {	
		if(getActivity() != null) {
			SharedPreferences savedSid = getActivity().getSharedPreferences("SAVEDSID", 0);
			Editor editor = savedSid.edit();
			editor.putString("SID", sid);
			editor.commit();
		}
	}

	public String loadSid() {
		
		SharedPreferences loadSid = getActivity().getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}

	public void saveUUID(String uuid){	
		if(getActivity() != null) {
			SharedPreferences savedUUID = getActivity().getSharedPreferences("SAVEDUUID", 0);
			Editor editor = savedUUID.edit();
			editor.putString("UUID", uuid);
			editor.commit();
		}		
	}
	
	public String loadUUID(){
		SharedPreferences loadUUID = getActivity().getSharedPreferences("SAVEDUUID", 0);
		return loadUUID.getString("UUID", null);
	}
	
	private long DBCount() {  
	    String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
	    SQLiteStatement statement = mKeyDB.compileStatement(sql);
	    long count = statement.simpleQueryForLong();
	    return count;
	}
	
	private long DBCountZone() {  
	    String sql = "SELECT COUNT(*) FROM " + ZONE_TABLE_NAME;
	    SQLiteStatement statement = mKeyDB.compileStatement(sql);
	    long count = statement.simpleQueryForLong();
	    return count;
	}
	
	private long DBCountCar() {  
	    String sql = "SELECT COUNT(*) FROM " + CAR_TABLE_NAME;
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
	
	private boolean hasZoneData(SQLiteDatabase mDB, String str){
		boolean hasData = false;
		Cursor mCursor = mKeyDB.rawQuery("select * from " + ZONE_TABLE_NAME, null);
		
		if(mCursor.moveToFirst()){
			int zoneidIndex = mCursor.getColumnIndex("zoneid");
			do{
				 String zoneid = mCursor.getString(zoneidIndex);
				 
				 if(zoneid.equals(str)) {
					 hasData = true;
					 break;
				 }
				 
			}while(mCursor.moveToNext());
		}
		mCursor.close();
		return hasData;
	}
	
	private boolean hasCarData(SQLiteDatabase mDB, String str, String str1){
		boolean hasData = false;
		Cursor mCursor = mKeyDB.rawQuery("select * from " + CAR_TABLE_NAME, null);
		
		if(mCursor.moveToFirst()){
			int l1ZoneIdIndex = mCursor.getColumnIndex("l1ZoneId");
			int plateNumIndex = mCursor.getColumnIndex("plateNum");
			do{
				 String l1ZoneId = mCursor.getString(l1ZoneIdIndex);
				 String plateNum = mCursor.getString(plateNumIndex);
				 
				 if(l1ZoneId.equals(str) && plateNum.equals(str1)) {
					 hasData = true;
					 break;
				 }
				 
			}while(mCursor.moveToNext());
		}
		mCursor.close();
		return hasData;
	}

    @Override
    public void onDetach() {
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        super.onDetach();

    }
}
