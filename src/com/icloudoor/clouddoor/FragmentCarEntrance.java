package com.icloudoor.clouddoor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.umeng.common.message.Log;

public class FragmentCarEntrance extends Fragment {
	private String HOST = "http://zone.icloudoor.com/icloudoor-web"
			+ "/user/api/getFamilyUserAndCars.do";
	private String sid = null;

	private ImageView IVshowPhoneNum;
	private ImageView IVshowCarNum;

	private TextView TVphoneNum;
	private TextView TVcarNum;

	private RequestQueue mQueue;

	private ListView mCarNumListView;
	private ListView mFamiliesListView;

	private boolean isShowCarNum = false;
	private boolean isShowPhoneNum = false;

	private RelativeLayout showCarNum;

	private RelativeLayout showPhoneNum;

	private SharedPreferences carNumAndPhoneNumShare;
	public SharedPreferences zoneIdShare;
	private Editor carNumAndPhoneNumEditor;

	private ArrayList<Map<String, String>> familiesList;
	private ArrayList<Map<String, String>> carNumList;

	private ArrayList<Map<String, String>> tempfamiliesList;
	private ArrayList<Map<String, String>> tempcarNumList;

	private JsonObjectRequest jsonRequest;
	
	//
	SharedPreferences submitStatus;
	Editor editor;
	boolean havePhone;
	boolean haveCarNum;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_car_entrance, container,
				false);

		//
		havePhone = false;
		haveCarNum = false;
		submitStatus =  getActivity().getSharedPreferences("SUBMITSTATUS", 0);
		editor = submitStatus.edit();
		
		TVphoneNum = (TextView) view.findViewById(R.id.id_carentrance_phonenum);
		TVcarNum = (TextView) view.findViewById(R.id.id_carentrance_carnum);

		carNumAndPhoneNumShare = getActivity().getSharedPreferences(
				"carNumAndPhoneNum", 0);
		carNumAndPhoneNumEditor = carNumAndPhoneNumShare.edit();
		zoneIdShare = getActivity().getSharedPreferences("ZONESHARE", 0);
		Log.e("carresponse", "onCreateView");
		mFamiliesListView = (ListView) view.findViewById(R.id.phonenumlist);
		mFamiliesListView.setVisibility(View.GONE);
		mCarNumListView = (ListView) view.findViewById(R.id.carnumlist);
		mCarNumListView.setVisibility(View.GONE);
		familiesList = new ArrayList<Map<String, String>>();
		carNumList = new ArrayList<Map<String, String>>();

		IVshowCarNum = (ImageView) view.findViewById(R.id.iv_showcarnum);
		IVshowPhoneNum = (ImageView) view.findViewById(R.id.iv_showphonenum);

		showPhoneNum = (RelativeLayout) view
				.findViewById(R.id.id_show_phonenum);
		showCarNum = (RelativeLayout) view.findViewById(R.id.id_showcarnum);

		mFamiliesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView TVphoneItem = (TextView) view.findViewById(R.id.id_familychildphonenum);
				TVphoneNum.setText(TVphoneItem.getText().toString());
				carNumAndPhoneNumEditor.putString("PHONENUM", TVphoneNum.getText().toString()).commit();
				TVphoneNum.setTextColor(0xFF333333);
				mFamiliesListView.setVisibility(View.GONE);
				if (!isShowPhoneNum) {
					IVshowPhoneNum
							.setImageResource(R.drawable.common_hide_list);
					isShowPhoneNum = true;

				} else {
					IVshowPhoneNum
							.setImageResource(R.drawable.common_show_list);
					isShowPhoneNum = false;

				}
				
				//TODO
				if(TVphoneNum.length() > 0){
					havePhone = true;
					editor.putBoolean("CarPhone", havePhone);
					editor.commit();
				}
			}
		});

		mCarNumListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView TVcarItem = (TextView) view.findViewById(R.id.id_carchildnum);
				TVcarNum.setText(TVcarItem.getText().toString());
				carNumAndPhoneNumEditor.putString("CARNUM", TVcarNum.getText().toString()).commit();

				TVcarNum.setTextColor(0xFF333333);

				mCarNumListView.setVisibility(View.GONE);
				if (!isShowCarNum) {

					IVshowCarNum.setImageResource(R.drawable.common_hide_list);
					isShowCarNum = true;

				} else {
					IVshowCarNum.setImageResource(R.drawable.common_show_list);
					isShowCarNum = false;

				}

				if(TVcarNum.length() > 0){
					haveCarNum = true;
					editor.putBoolean("CarNum", haveCarNum);
					editor.commit();
				}
				
			}
		});


		showPhoneNum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.e("carresponse", "sadkjlfk;s;");
				if (!isShowPhoneNum) {
					IVshowPhoneNum
							.setImageResource(R.drawable.common_hide_list);
					isShowPhoneNum = true;
					mFamiliesListView.setVisibility(View.VISIBLE);
				} else {
					IVshowPhoneNum
							.setImageResource(R.drawable.common_show_list);
					isShowPhoneNum = false;

					mFamiliesListView.setVisibility(View.GONE);
				}
			}
		});

		showCarNum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!isShowCarNum) {

					IVshowCarNum.setImageResource(R.drawable.common_hide_list);
					isShowCarNum = true;
					mCarNumListView.setVisibility(View.VISIBLE);
				} else {
					IVshowCarNum.setImageResource(R.drawable.common_show_list);
					isShowCarNum = false;

					mCarNumListView.setVisibility(View.GONE);
				}

			}
		});

		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Log.e("carresponse", "onResume()");
		sid = loadSid();
		mQueue = Volley.newRequestQueue(getActivity());

		MyJsonObjectRequest mjsonobjrequest = new MyJsonObjectRequest(
				Method.POST, HOST + "?sid=" + sid, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						try {
							JSONObject jsonobj = response.getJSONObject("data");
							JSONArray familyArr = jsonobj
									.getJSONArray("families");
							JSONArray carNumArr = jsonobj.getJSONArray("cars");
							for (int i = 0; i < familyArr.length(); i++) {
								Map<String, String> familyMap = new HashMap<String, String>();
								String family = familyArr.getJSONObject(i)
										.getString("mobile");
								familyMap.put("families", family);
								familiesList.add(familyMap);
							}
							for (int j = 0; j < carNumArr.length(); j++) {
								Map<String, String> carNumMap = new HashMap<String, String>();
								String carNum = carNumArr.getJSONObject(j)
										.getString("plateNum");
								carNumMap.put("carNum", carNum);
								carNumList.add(carNumMap);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {

				Map<String, String> map = new HashMap<String, String>();

				map.put("zoneUserId", zoneIdShare.getString("ZONEID", null));

				return map;
			}
		};
		if (zoneIdShare.getString("ZONEID", null) != null)
			mQueue.add(mjsonobjrequest);

		mCarNumListView.setAdapter(new SimpleAdapter(getActivity(), carNumList,
				R.layout.carnum_list_item, new String[] { "carNum" },
				new int[] { R.id.id_carchildnum }));

		mFamiliesListView.setAdapter(new SimpleAdapter(getActivity(),
				familiesList, R.layout.phonenum_list_item,
				new String[] { "families" },
				new int[] { R.id.id_familychildphonenum }));

	}

	private void saveSid(String sid) {
		if (getActivity() != null) {
			SharedPreferences savedSid = getActivity().getSharedPreferences(
					"SAVEDSID", 0);
			Editor editor = savedSid.edit();
			editor.putString("SID", sid);
			editor.commit();
		}
	}

	private String loadSid() {

		SharedPreferences loadSid = getActivity().getSharedPreferences(
				"SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}

}
