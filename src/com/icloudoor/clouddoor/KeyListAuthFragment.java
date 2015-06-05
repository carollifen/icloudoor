package com.icloudoor.clouddoor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.umeng.message.proguard.m;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 *
 */
@SuppressLint("ResourceAsColor")
public class KeyListAuthFragment extends Fragment {

	private String HOST = "https://zone.icloudoor.com/icloudoor-web"
			+ "/user/api/getMyAddress.do";

	private String postKerUrl = "https://zone.icloudoor.com/icloudoor-web/user/api/authTempCar.do";

	private String postNomalKeyUrl = "https://zone.icloudoor.com/icloudoor-web/user/api/authTempNormal.do";
	private String sid = null;

	private SharedPreferences dateAndPhoneShare;
	private Editor dateAndPhoneEditor;
	private SharedPreferences carNumAndPhoneNumShare;
	private Editor carAndPhoneEditor;
	private SharedPreferences zoneIdShare;
	private Editor meditor;
	private RequestQueue mQueue;

	private String carPosition = null;

	private final String DATABASE_NAME = "KeyDB.db";
	private final String TABLE_NAME = "ZoneKeyTable";
	private MyDataBaseHelper mKeyDBHelper;
	private SQLiteDatabase mKeyDB;

	private String zoneUserId;
	private String plateNum;
	private String toMobile;
	private String carPosStatus;

	public ImageView IVselectkeyItem;
	public TextView TVListItem;
	private String TAG = this.getClass().getSimpleName();

	private ListView LVkeylist;

	private RelativeLayout showHideKeyList;
	private ImageView btnShowHideKeyList;
	private boolean isShowingKeyList;

	private RelativeLayout chooseCarKey;
	private RelativeLayout chooseManKey;
	private TextView carKeyText;
	private TextView manKeyText;
	private boolean isChooseCarKey;

	private RelativeLayout showTip;

	private FrameLayout mframlayout;

	private FragmentManager mfragmentManager;
	private FragmentTransaction mfragmentTrasaction;

	private TextView TVkeyname;
	private FragmentCarEntrance mcarFragment;
	private FragmentManEntrance mManFragment;

	private TextView btnSubmitText;
	private int ColorDisable = 0xFF999999;

	private ArrayList<Map<String, String>> Zonekeylist;
	private ArrayList<Map<String, String>> tempkeylist;

	public KeyListAuthFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_key_list_auth,
				container, false);

		mQueue = Volley.newRequestQueue(getActivity());

		dateAndPhoneShare = getActivity().getSharedPreferences("DATEANDPHONESHARE", 0);
		dateAndPhoneEditor = dateAndPhoneShare.edit();
		carNumAndPhoneNumShare = getActivity().getSharedPreferences("carNumAndPhoneNum", 0);
		carAndPhoneEditor = carNumAndPhoneNumShare.edit();
		zoneIdShare = getActivity().getSharedPreferences("ZONESHARE", 0);
		meditor = zoneIdShare.edit();

		mKeyDBHelper = new MyDataBaseHelper(getActivity(), DATABASE_NAME);
		mKeyDB = mKeyDBHelper.getWritableDatabase();

		tempkeylist = new ArrayList<Map<String, String>>();

		mframlayout = (FrameLayout) view.findViewById(R.id.id_entrancecontent);

		mcarFragment = new FragmentCarEntrance();
		mManFragment = new FragmentManEntrance();
		mfragmentManager = getChildFragmentManager();
		mfragmentTrasaction = mfragmentManager.beginTransaction();

		mfragmentTrasaction.replace(R.id.id_entrancecontent, mManFragment);
		mfragmentTrasaction.commit();
		TVkeyname = (TextView) view.findViewById(R.id.keyname_in_key_auth);
		LVkeylist = (ListView) view.findViewById(R.id.doorname_listview);

		mKeyDBHelper = new MyDataBaseHelper(getActivity(), DATABASE_NAME);
		mKeyDB = mKeyDBHelper.getReadableDatabase();

		ArrayList<Map<String, String>> mlist = new ArrayList<Map<String, String>>();
		for (int i = 3; i >= 0; i--) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("keyname", " 锦绣香江大门" + i);
			mlist.add(map);
		}
		LVkeylist.setVisibility(View.GONE);

		// LVkeylist.setAdapter(new MykeyListAdapter(getActivity(), mlist) );

		LVkeylist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				TVListItem = (TextView) view.findViewById(R.id.id_keyname);
				IVselectkeyItem = (ImageView) view.findViewById(R.id.select_keyicon);

				TVkeyname.setText(TVListItem.getText().toString());
				TVkeyname.setTextColor(0xFF333333);
				Cursor cursor = mKeyDB.rawQuery("select * from ZoneKeyTable where zoneAddress=?",
						new String[] { TVkeyname.getText().toString() });
				if (cursor.moveToFirst()) {
					String zoneid = cursor.getString(cursor.getColumnIndex("zoneId"));
					String l1ZoneId = cursor.getString(cursor.getColumnIndex("l1ZoneId"));
					Log.e("ididididid", zoneid);

					meditor.putString("ZONEID", zoneid);
					meditor.putString("l1ZoneId", l1ZoneId);
					meditor.commit();
				}
                cursor.close();

				LVkeylist.setVisibility(View.GONE);

				if (!isShowingKeyList) {
					btnShowHideKeyList.setImageResource(R.drawable.common_hide_list);
					isShowingKeyList = true;

				} else {
					btnShowHideKeyList.setImageResource(R.drawable.common_show_list);
					isShowingKeyList = false;

				}

			}

		});

		// to hide the tip after 3 secs
		showTip = (RelativeLayout) view.findViewById(R.id.show_tip);

		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.show_tip_animation);
		DecelerateInterpolator interpolator = new DecelerateInterpolator();
		animation.setInterpolator(interpolator);
		animation.setFillAfter(true);
		showTip.startAnimation(animation);

		// show or hide key list
		isShowingKeyList = false;
		showHideKeyList = (RelativeLayout) view.findViewById(R.id.show_hide_key_list);
		btnShowHideKeyList = (ImageView) view.findViewById(R.id.btn_show_hide_key_list);
		btnShowHideKeyList.setImageResource(R.drawable.common_show_list);

		showHideKeyList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isShowingKeyList) {
					btnShowHideKeyList.setImageResource(R.drawable.common_hide_list);
					isShowingKeyList = true;
					LVkeylist.setVisibility(View.VISIBLE);
				} else {
					btnShowHideKeyList.setImageResource(R.drawable.common_show_list);
					isShowingKeyList = false;
					LVkeylist.setVisibility(View.GONE);
				}
			}

		});
		//
		// choose car or man key
		isChooseCarKey = false;
		chooseCarKey = (RelativeLayout) view.findViewById(R.id.btn_choose_car_key);
		chooseManKey = (RelativeLayout) view.findViewById(R.id.btn_choose_man_key);
		carKeyText = (TextView) view.findViewById(R.id.car_key_text);
		manKeyText = (TextView) view.findViewById(R.id.man_key_text);

		chooseCarKey.setBackgroundResource(R.drawable.channel_normal);
		chooseManKey.setBackgroundResource(R.drawable.channel_select);
		carKeyText.setTextColor(0xFF333333);
		manKeyText.setTextColor(0xFFffffff);

		chooseCarKey.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (TVkeyname.getText().toString() != "") {
					if (!isChooseCarKey) {
						mfragmentManager = getChildFragmentManager();
						mfragmentTrasaction = mfragmentManager
								.beginTransaction();
						mfragmentTrasaction.replace(R.id.id_entrancecontent,
								mcarFragment);
						mfragmentTrasaction.commit();
						chooseCarKey
								.setBackgroundResource(R.drawable.channel_select);
						chooseManKey
								.setBackgroundResource(R.drawable.channel_normal);
						carKeyText.setTextColor(0xFFffffff);
						manKeyText.setTextColor(0xFF333333);
						isChooseCarKey = true;
					}
				} else {
					Toast.makeText(getActivity(), R.string.select_district_first,
							Toast.LENGTH_SHORT).show();
				}
		
			}

		});

		chooseManKey.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isChooseCarKey) {
					mfragmentManager = getChildFragmentManager();
					mfragmentTrasaction = mfragmentManager.beginTransaction();
					mfragmentTrasaction.replace(R.id.id_entrancecontent, mManFragment);
					mfragmentTrasaction.commit();
					chooseCarKey.setBackgroundResource(R.drawable.channel_normal);
					chooseManKey.setBackgroundResource(R.drawable.channel_select);
					carKeyText.setTextColor(0xFF333333);
					manKeyText.setTextColor(0xFFffffff);
					isChooseCarKey = false;
					// LVkeylist.setAdapter(new
					// SimpleAdapter(getActivity(),mankeylist,
					// R.layout.keylist_child, new String[]{"mankeyItem"}, new
					// int[]{R.id.id_keyname}));
				}
			}

		});
		//
		// submit
		btnSubmitText = (TextView) view.findViewById(R.id.btn_submit_text);
		btnSubmitText.setBackgroundResource(R.drawable.selector_submit_text_bg);
	    btnSubmitText.setTextColor(getResources().getColorStateList(R.color.test_input_color_selector));
//		btnSubmitText.setTextColor(ColorDisable);
//		btnSubmitText.setBackgroundResource(R.drawable.btn_submit_big_disable);
//		btnSubmitText.setEnabled(false);
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;

		btnSubmitText.setWidth(screenWidth - 32 * 2);

		SharedPreferences status = getActivity().getSharedPreferences("SUBMITSTATUS", 0);
//		if((status.getBoolean("CarPhone", false) && status.getBoolean("CarNum", false) && TVkeyname.length() > 0)
//				|| (status.getBoolean("ManPhone", false) && status.getBoolean("ManDate", false) && TVkeyname.length() > 0)){
//			btnSubmitText.setEnabled(true);
//		}
		
		btnSubmitText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences status = getActivity().getSharedPreferences("SUBMITSTATUS", 0);
				if (isChooseCarKey) {
					
					if (carNumAndPhoneNumShare.getString("CARNUM", null) != null) {
						Cursor carPositionCursor = mKeyDB.rawQuery("select * from CarKeyTable where plateNum=? and l1ZoneId=?",
								new String[] { carNumAndPhoneNumShare.getString("CARNUM", null),  zoneIdShare.getString("l1ZoneId", null)});
						if (carPositionCursor.moveToFirst()) {
							do {
								carPosition = carPositionCursor.getString(carPositionCursor
										.getColumnIndex("carPosStatus"));
							} while (carPositionCursor.moveToNext());
						}
						carPositionCursor.close();
					}
					
					sid = loadSid();
					MyJsonObjectRequest carAndPhoneRequest = new MyJsonObjectRequest(
							Method.POST, postKerUrl + "?sid=" + sid, null,
							new Response.Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject response) {
									Log.e("psotKeyResponse",response.toString());

									try {
										
										if(response.getInt("code") == 1){
											if(response.getString("sid") != null){
												saveSid(response.getString("sid"));
											}
											
											if(getActivity() != null){
												Toast.makeText(getActivity(), R.string.auth_successful, Toast.LENGTH_SHORT).show();
											}
											
											//when succeed to lend the key, refresh the "carStatus" to "3" in the database
											ContentValues value = new ContentValues();
											value.put("carStatus", "3");
											mKeyDB.update("CarKeyTable", value, "plateNum=? and l1ZoneId=?", new String[] { carNumAndPhoneNumShare.getString("CARNUM", null),  zoneIdShare.getString("l1ZoneId", null)});
										}
										
										if(response.getInt("code") == -100){
											Toast.makeText(getActivity(), R.string.car_already_lend, Toast.LENGTH_SHORT).show();
										}else if(response.getInt("code") == -101){
											Toast.makeText(getActivity(), R.string.user_not_regis, Toast.LENGTH_SHORT).show();
										}else if(response.getInt("code") == -104){
											Toast.makeText(getActivity(), R.string.cannot_auth_to_self, Toast.LENGTH_SHORT).show();
										}else if(response.getInt("code") == -1){
											Toast.makeText(getActivity(), R.string.wrong_params, Toast.LENGTH_SHORT).show();
										}else if(response.getInt("code") == -105){
											Toast.makeText(getActivity(), R.string.can_only_have_one_car_key_in_one_district, Toast.LENGTH_SHORT).show();
										}
									} catch (JSONException e) {
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
						protected Map<String, String> getParams()
								throws AuthFailureError {

							Map<String, String> map = new HashMap<String, String>();

							map.put("zoneUserId",zoneIdShare.getString("ZONEID", null));
							map.put("plateNum", carNumAndPhoneNumShare.getString("CARNUM", null));
							map.put("toMobile", carNumAndPhoneNumShare.getString("PHONENUM", null));
							map.put("carPosStatus", carPosition);
							return map;
						}
					};

					if(status.getBoolean("CarPhone", false) && status.getBoolean("CarNum", false) && TVkeyname.length() > 0){
						mQueue.add(carAndPhoneRequest);
					}				
				} else {
					sid = loadSid();
					MyJsonObjectRequest postMankeyRequest = new MyJsonObjectRequest(
							Method.POST, postNomalKeyUrl + "?sid=" + sid, null,
							new Response.Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject response) {
									// TODO Auto-generated method stub
									Log.e("psotnomalnomalKeyResponse", response.toString());
									try {
										
										if(response.getInt("code") == 1){
											if(response.getString("sid") != null){
												saveSid(response.getString("sid"));
											}
											
											if(getActivity() != null){
												Toast.makeText(getActivity(), R.string.auth_successful, Toast.LENGTH_SHORT).show();
											}
										}
										
										
										if(response.getInt("code") == -101){
											Toast.makeText(getActivity(), R.string.user_not_regis, Toast.LENGTH_SHORT).show();
										}else if(response.getInt("code") == -102){
											Toast.makeText(getActivity(), R.string.lend_count_too_more, Toast.LENGTH_SHORT).show();
										}else if(response.getInt("code") == -103){
											Toast.makeText(getActivity(), R.string.already_have_the_temp_key, Toast.LENGTH_SHORT).show();
										}else if(response.getInt("code") == -104){
											Toast.makeText(getActivity(), R.string.cannot_auth_to_self, Toast.LENGTH_SHORT).show();
										}else if(response.getInt("code") == -1){
											Toast.makeText(getActivity(), R.string.wrong_params, Toast.LENGTH_SHORT).show();
										}else if(response.getInt("code") == -106){
											Toast.makeText(getActivity(), R.string.borrow_count_too_more, Toast.LENGTH_SHORT).show();
										}
									} catch (JSONException e) {
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
						protected Map<String, String> getParams()
								throws AuthFailureError {
							StringBuilder ss = new StringBuilder();
							Map<String, String> map = new HashMap<String, String>();

							map.put("zoneUserId", zoneIdShare.getString("ZONEID", null));
							map.put("authDate", dateAndPhoneShare.getString("DATE", null));
							map.put("toMobile", dateAndPhoneShare.getString("PHONENUM", null));

							return map;
						}
					};
					if(status.getBoolean("ManPhone", false) && status.getBoolean("ManDate", false) && TVkeyname.length() > 0){
						mQueue.add(postMankeyRequest);
					}
				}
			}

		});
		//

		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		sid = loadSid();

		Zonekeylist = new ArrayList<Map<String, String>>();
		JsonObjectRequest mjsonobjrequest = new JsonObjectRequest(HOST
				+ "?sid=" + sid, null, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					
					Log.e(TAG, response.toString());
					
					if(response.getInt("code") == 1){
						JSONArray zoneArr = response.getJSONArray("data");
						
						for (int i = 0; i < zoneArr.length(); i++) {
							String zoneId = zoneArr.getJSONObject(i).getString("zoneUserId");
							String zoneAdd = zoneArr.getJSONObject(i).getString("address");
							String l1ZoneId = zoneArr.getJSONObject(i).getString("l1ZoneId");
							Log.e("address", zoneAdd);
							Map<String, String> zoneMap = new HashMap<String, String>();
							zoneMap.put("zoneName", zoneAdd);
							Zonekeylist.add(zoneMap);
							ContentValues value = new ContentValues();
							value.put("zoneId", zoneId);
							value.put("zoneAddress", zoneAdd);
							value.put("l1ZoneId", l1ZoneId);
							try {
								mKeyDB.insert(TABLE_NAME, null, value);
							} catch (Exception e) {
								Log.e("dbdbdbd", "数据已经存在");
							}
						}
						
						if(zoneArr.length() == 1){
							TVkeyname.setText(zoneArr.getJSONObject(0).getString("address"));
							meditor.putString("ZONEID", zoneArr.getJSONObject(0).getString("zoneId"));
							meditor.putString("l1ZoneId", zoneArr.getJSONObject(0).getString("l1ZoneId"));
							meditor.commit();
						} else if(zoneArr.length() > 1){
							TVkeyname.setText(R.string.plz_choose_district);
							TVkeyname.setTextColor(0xFF999999);
						}
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

		});
		mQueue.add(mjsonobjrequest);

		LVkeylist.setAdapter(new SimpleAdapter(getActivity(), Zonekeylist,
				R.layout.keylist_child, new String[] { "zoneName" },
				new int[] { R.id.id_keyname }));

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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == Activity.RESULT_OK)
			;
		{
			if (resultCode == Activity.RESULT_OK) {
				ContentResolver reContentResolverol = getActivity()
						.getContentResolver();
				Uri contactData = data.getData();
				@SuppressWarnings("deprecation")
				Cursor cursor = reContentResolverol.query(contactData, null,
						null, null, null);
				cursor.moveToFirst();
				String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                cursor.close();
				Cursor phone = reContentResolverol.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);
				while (phone.moveToNext()) {
					String usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					// dateAndPhoneEditor.putString("PHONENUM",
					// usernumber).commit();
					// text.setText(usernumber+" ("+username+")");
					// FragmentManEntrance f=new FragmentManEntrance();
					mManFragment.getData(usernumber);
				}
                phone.close();
			}
		}
		Log.e("sd", "dsjdkfkl");

	}

    @Override
    public void onDestroy() {
        mKeyDB.close();
        super.onDestroy();
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
