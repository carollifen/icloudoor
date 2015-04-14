package com.icloudoor.clouddoor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class KeyList extends Activity {
	private ImageView IvBack;

	private URL downLoadKeyURL;
	private RequestQueue mQueue;

	private String HOST = "http://zone.icloudoor.com/icloudoor-web";
	private String sid = null;
	private JsonObjectRequest mJsonRequest;

	ListView mKeyList;
	KeyListAdapter mAdapter;
	ArrayList<HashMap<String, String>> doorNameList;

	// Door info variable
	String keyBeginDay;
	String keyEndDay;
	String L1ZoneName;
	String L1ZoneID;

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
						mAdapter = new KeyListAdapter(KeyList.this, doorNameList);
						mKeyList.setAdapter(mAdapter);					
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
		HashMap<String, String> map = new HashMap<String, String>();

		try {
			JSONArray dataArray = response.getJSONArray("data"); // 得到"data"这个array
			
			for(int i = 0; i < dataArray.length();i++) {    
				JSONObject L1Data = (JSONObject) dataArray.get(i);       // 得到里面的具体object--第i层的具体数据
				if (L1Data.getString("authFrom") != null)
					keyBeginDay = L1Data.getString("authFrom");

				if (L1Data.getString("authTo") != null)
					keyEndDay = L1Data.getString("authTo");
				
				L1ZoneName = L1Data.getString("l1ZoneName");
				L1ZoneID = L1Data.getString("l1ZoneId");
				
				map.put("Door", L1ZoneName);
				map.put("BEGIN", keyBeginDay);
				map.put("END", keyEndDay);
				doorNameList.add(map);
			}

			// JSONArray mmjo = L1Data.getJSONArray("carDoors");
			// //得到“carDoors”这个array；"normalDoors"也是array；"authTo"，"authFrom"，"l1ZoneName"，"l1ZoneId"这些是object
			// JSONObject mmmjo = mmjo.getJSONObject(0);
			// //"carDoors"里面可能包含多个门，这里得到第一个门
			Log.e("TEST", doorNameList.get(0).get("Door") + " and " +  doorNameList.get(0).get("BEGIN") + " and " +  doorNameList.get(0).get("END") ); // 得到第一个车门的"doorName"值，并在log中打印出来
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return doorNameList;
	}

	private class KeyListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private ArrayList<HashMap<String, String>> doorNameList;
		
//		public void setDataList(ArrayList<HashMap<String, String>> list) {
//			doorNameList = list;
//			notifyDataSetChanged();
//		}

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
}