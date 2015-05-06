package com.icloudoor.clouddoor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShowPersonalInfo extends Activity {

	private RequestQueue mQueue;
	private String HOST = "http://zone.icloudoor.com/icloudoor-web";
	private URL getInfoURL;
	private int statusCode;
	private String sid;
	private JSONObject data;
	
	private String name, nickname, birthday, id, province, city, district;
	private int sex, provinceid, cityid, districtid;
	
	private TextView TVName;
	private TextView TVNickName;
	private TextView TVSex;
	private ImageView IVSexImage;
	private TextView TVprovince;
	private TextView TVcity;
	private TextView TVdistrict;
	private TextView TVyear;
	private TextView TVmonth;
	private TextView TVday;
	private TextView TVid;
	
	private RelativeLayout back;
	private RelativeLayout toModifyProfile;
	
	private MyAreaDBHelper mAreaDBHelper;
	private SQLiteDatabase mAreaDB;
	private final String DATABASE_NAME = "area.db";
	private final String TABLE_NAME = "tb_core_area";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.show_personal_info);
		
		mAreaDBHelper = new MyAreaDBHelper(ShowPersonalInfo.this, DATABASE_NAME, null, 1);
		mAreaDB = mAreaDBHelper.getWritableDatabase();	
		
		initViews();
	}
	
	public void initViews() {
		TVName = (TextView) findViewById(R.id.personal_info_name);
		TVNickName = (TextView) findViewById(R.id.personal_info_NickName);
		TVSex = (TextView) findViewById(R.id.personal_info_sexName);
		IVSexImage = (ImageView) findViewById(R.id.personal_info_sexImage);
		TVprovince = (TextView) findViewById(R.id.personal_info_province);
		TVcity = (TextView) findViewById(R.id.personal_info_city);
		TVdistrict = (TextView) findViewById(R.id.personal_info_district);
		TVyear = (TextView) findViewById(R.id.personal_info_year);
		TVmonth = (TextView) findViewById(R.id.personal_info_month);
		TVday = (TextView) findViewById(R.id.personal_info_day);
		TVid = (TextView) findViewById(R.id.personal_info_ID);
		back = (RelativeLayout) findViewById(R.id.btn_back);
		toModifyProfile = (RelativeLayout) findViewById(R.id.tomodify_person_info);
		
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ShowPersonalInfo.this.finish();
			}
			
		});
		
		toModifyProfile.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ShowPersonalInfo.this, ModifyPersonalInfo.class);
				startActivity(intent);
			}
			
		});
	}
	
	public String getProvinceName(int provinceId) {
		String provinceName = null;
		Cursor mCursorP = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		if (mCursorP.moveToFirst()) {
			int provinceIndex = mCursorP.getColumnIndex("province_short_name");
			int provinceIdIndex = mCursorP.getColumnIndex("province_id");
			do{
				int tempPID = mCursorP.getInt(provinceIdIndex);
			    String tempPName = mCursorP.getString(provinceIndex);
				if(tempPID == provinceId){
					provinceName = tempPName;
					break;
				}		
			}while(mCursorP.moveToNext());		
		}
		mCursorP.close();
		return provinceName;
	}
	
	public String getCityName(int cityId) {
		String cityName = null;
		Cursor mCursorC = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		if (mCursorC.moveToFirst()) {
			int cityIndex = mCursorC.getColumnIndex("city_short_name");
			int cityIdIndex = mCursorC.getColumnIndex("city_id");
			do{
				int tempCID = mCursorC.getInt(cityIdIndex);
			    String tempCName = mCursorC.getString(cityIndex);
				if(tempCID == cityId){
					cityName = tempCName;
					break;
				}		
			}while(mCursorC.moveToNext());		
		}
		mCursorC.close();
		return cityName;
	}
	
	public String getDistrictName(int districtId) {
		String districtName = null;
		Cursor mCursorD = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		if (mCursorD.moveToFirst()) {
			int districtIndex = mCursorD.getColumnIndex("district_short_name");
			int districtIdIndex = mCursorD.getColumnIndex("district_id");
			do{
				int tempDID = mCursorD.getInt(districtIdIndex);
			    String tempDName = mCursorD.getString(districtIndex);
				if(tempDID == districtId){
					districtName = tempDName;
					break;
				}		
			}while(mCursorD.moveToNext());		
		}
		mCursorD.close();
		return districtName;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Log.e("TESTTEST", "onResume show");
		mQueue = Volley.newRequestQueue(this);
		sid = loadSid();
		try {
			getInfoURL = new URL(HOST + "/user/manage/getProfile.do" + "?sid=" + sid);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
				Method.POST, getInfoURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							if(response.getString("sid") != null){
								sid = response.getString("sid");
								saveSid(sid);
							}
							statusCode = response.getInt("code");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
						Log.e("TEST", response.toString());
						
						if(statusCode == 1){
							try {
								data = response.getJSONObject("data");
								
								name = data.getString("userName");
								nickname = data.getString("nickname");
								birthday = data.getString("birthday");
								id = data.getString("idCardNo");
								sex = data.getInt("sex");
								provinceid = data.getInt("provinceId");
								cityid = data.getInt("cityId");
								districtid = data.getInt("districtId");
								
								province = getProvinceName(provinceid);
								city = getCityName(cityid);
								district = getDistrictName(districtid);
										
								
								
								SharedPreferences saveProfile = getSharedPreferences("PROFILE",
										MODE_PRIVATE);
								Editor editor = saveProfile.edit();
								editor.putString("NAME", name);
								editor.putString("NICKNAME", nickname);
								editor.putString("ID", id);
								editor.putString("PROVINCE", province);
								editor.putString("CITY", city);
								editor.putString("DISTRICT", district);
								editor.putInt("PROVINCEID", provinceid);
								editor.putInt("CITYID", cityid);
								editor.putInt("DISTRICTID", districtid);
								editor.putInt("SEX", sex);
								editor.putString("YEAR", birthday.substring(0, 4));
								editor.putString("MONTH", birthday.substring(5, 7));
								editor.putString("DAY", birthday.substring(8));
								editor.commit();
								
								
								TVName.setText(name);
								TVNickName.setText(nickname);
								
								if(sex == 1){
									TVSex.setText("ÄÐ");
									IVSexImage.setImageResource(R.drawable.sex_blue);
								}else if(sex == 2){
									TVSex.setText("Å®");
									IVSexImage.setImageResource(R.drawable.sex_red);
								}
								
								TVprovince.setText(province);
								TVcity.setText(city);
								TVdistrict.setText(district);
								TVid.setText(id);
								TVyear.setText(birthday.substring(0, 4));
								TVmonth.setText(birthday.substring(5, 7));
								TVday.setText(birthday.substring(8));
								
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}else if (statusCode == -1) {
							Toast.makeText(getApplicationContext(), R.string.wrong_params, Toast.LENGTH_SHORT).show();
						} else if (statusCode == -2) {
							Toast.makeText(getApplicationContext(), R.string.not_login, Toast.LENGTH_SHORT).show();
						} else if (statusCode == -99) {
							Toast.makeText(getApplicationContext(), R.string.unknown_err, Toast.LENGTH_SHORT).show();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
		mQueue.add(mJsonRequest);
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
