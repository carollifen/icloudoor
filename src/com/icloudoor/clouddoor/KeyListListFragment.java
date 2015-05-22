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

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
	private MyDataBaseHelper mKeyDBHelper;
	private SQLiteDatabase mKeyDB;
	
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

	public KeyListListFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_key_list_list, container, false);
		
		mKeyList = (ListView) view.findViewById(R.id.key_listview);
		
		mKeyDBHelper = new MyDataBaseHelper(getActivity(), DATABASE_NAME);
		mKeyDB = mKeyDBHelper.getWritableDatabase();
		
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

								if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
									Log.e("TESTTESTDB", "have the table");
									if (DBCount() > 0) {
										Log.e("TESTTESTDB", "table is not empty");
										Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME, null);
										if (mCursor.moveToFirst()) {

											doorNameList = new ArrayList<HashMap<String, String>>();
											mAdapter = new KeyListAdapter(getActivity(), doorNameList);
											mKeyList.setAdapter(mAdapter);

											int deviceIdIndex = mCursor.getColumnIndex("deviceId");
											int doorNamemIndex = mCursor.getColumnIndex("doorName");
											int authFromIndex = mCursor.getColumnIndex("authFrom");
											int authToIndex = mCursor.getColumnIndex("authTo");
											int doorTypeIndex = mCursor.getColumnIndex("doorType");

											do {
												String deviceId = mCursor.getString(deviceIdIndex);
												String doorName = mCursor.getString(doorNamemIndex);
												String authFrom = mCursor.getString(authFromIndex);
												String authTo = mCursor.getString(authToIndex);
												String doorType = mCursor.getString(doorTypeIndex);

												Log.e("TESTTESTDB deviceId =", deviceId);
												Log.e("TESTTESTDB doorName =", doorName);
												Log.e("TESTTESTDB authFrom =", authFrom);
												Log.e("TESTTESTDB authTo =", authTo);
												Log.e("TESTTESTDB doorType =", doorType);

												HashMap<String, String> keyFromDB = new HashMap<String, String>();
												keyFromDB.put("Door", doorName);
												keyFromDB.put("BEGIN", authFrom);
												keyFromDB.put("END", authTo);

												doorNameList.add(keyFromDB);
												mAdapter.notifyDataSetChanged();

											} while (mCursor.moveToNext());
										}
										mCursor.close();
									}
								}
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
		
		if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
			Log.e("TESTTESTDBDB", "have the table");
			if (DBCount() > 0) {
				Log.e("TESTTESTDBDB", "table is not empty");
				Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME, null);
				if (mCursor.moveToFirst()) {

					doorNameList = new ArrayList<HashMap<String, String>>();
					mAdapter = new KeyListAdapter(getActivity(), doorNameList);
					mKeyList.setAdapter(mAdapter);

					int deviceIdIndex = mCursor.getColumnIndex("deviceId");
					int doorNamemIndex = mCursor.getColumnIndex("doorName");
					int authFromIndex = mCursor.getColumnIndex("authFrom");
					int authToIndex = mCursor.getColumnIndex("authTo");
					int doorTypeIndex = mCursor.getColumnIndex("doorType");

					do {
						String deviceId = mCursor.getString(deviceIdIndex);
						String doorName = mCursor.getString(doorNamemIndex);
						String authFrom = mCursor.getString(authFromIndex);
						String authTo = mCursor.getString(authToIndex);
						String doorType = mCursor.getString(doorTypeIndex);

						Log.e("TESTTESTDBDB deviceId =", deviceId);
						Log.e("TESTTESTDBDB doorName =", doorName);
						Log.e("TESTTESTDBDB authFrom =", authFrom);
						Log.e("TESTTESTDBDB authTo =", authTo);
						Log.e("TESTTESTDBDB doorType =", doorType);

						HashMap<String, String> keyFromDB = new HashMap<String, String>();
						keyFromDB.put("Door", doorName);
						keyFromDB.put("BEGIN", authFrom);
						keyFromDB.put("END", authTo);

						doorNameList.add(keyFromDB);
						mAdapter.notifyDataSetChanged();

					} while (mCursor.moveToNext());
				}
				mCursor.close();
			}
		}
	}
	
	/*
	 * key params:
	 * authType: 	1 - forever		  // the authtype is cancelled in the new key download interface
	 *                	2 - long time
	 *                	3 - temp
	 * doorType: 	1 - man	
	 *                	2 - car
	 */
	public void parseKeyData(JSONObject response) throws JSONException {
		Log.e("test for new interface", "parseKeyData func");
		
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
	
	private class KeyListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private ArrayList<HashMap<String, String>> doorNameList;

		// public void setDataList(ArrayList<HashMap<String, String>> list) {
		// doorNameList = list;
		// notifyDataSetChanged();
		// }

		public KeyListAdapter(Context context, ArrayList<HashMap<String, String>> list) {
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
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.doorname.setSelected(true);
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
