package com.icloudoor.clouddoor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SetPersonalInfo extends Activity {
	
	private MyAreaDBHelper mAreaDBHelper;
	private SQLiteDatabase mAreaDB;
	private final String DATABASE_NAME = "area.db";
	private final String TABLE_NAME = "tb_core_area";
//	private final String TABLE_NAME = "core_area";
	
	private String[] provinceSet;
	private String[][] citySet;
	private String[][][] districtSet;
	
	private List<String> provinceList = new ArrayList<String>();  
	private List<String> cityList = new ArrayList<String>(); 
	private List<String> districtList = new ArrayList<String>(); 
	
	private Spinner provinceSpinner = null; 
	private Spinner citySpinner = null;
	private Spinner districtSpinner = null; 
	ArrayAdapter<String> provinceAdapter = null;
	ArrayAdapter<String> cityAdapter = null;
	ArrayAdapter<String> districtAdapter = null;
	private int provincePosition = 0;
	private int cityPosition = 0;
	private int districtPosition = 0;
	
	private int maxPlength;
	private int maxClength;
	private int maxDlength;

	private ImageView personImage;
	private TextView addImage;
	private EditText realName;
	private EditText nickName;
	private RelativeLayout setSex;
	private ImageView sexMan;
	private ImageView sexWoman;
	private EditText birthYear;
	private EditText birthMonth;
	private EditText birthDay;
	private EditText personalID;
	
	private String Name, Nickname, Age, PersonalID, province, city, district, year, month, day, BirthDay;
	private int Sex, provinceId, cityId, districtId;
	
	private RelativeLayout back;
	private TextView save;
	
	private RequestQueue mQueue;
	private URL setInfoURL;
	private String HOST = "http://zone.icloudoor.com/icloudoor-web";
	private String sid;
	private int statusCode;
	
	private int setPersonal = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.set_person_info);
		
		mAreaDBHelper = new MyAreaDBHelper(SetPersonalInfo.this, DATABASE_NAME, null, 1);
		mAreaDB = mAreaDBHelper.getWritableDatabase();	
		
		initViews();	
		
		initSpinnerData();
		
		setSpinner();
		
		//get the selected province name and id
		provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				int tempLength = 0;
				String[] tempCitySet;
				for(int aa = 0; aa < maxClength; aa++){
					if(citySet[position][aa] != null) tempLength++;
				}
				
				tempCitySet = new String[tempLength];
				for(int aa = 0; aa < tempLength; aa++){
					tempCitySet[aa] = citySet[position][aa];
				}
						
				cityAdapter = new ArrayAdapter<String>(
						SetPersonalInfo.this, android.R.layout.simple_spinner_item, tempCitySet);
				citySpinner.setAdapter(cityAdapter);
				provincePosition = position;
 
				province = provinceSet[position];
				Log.e("Spinner test pro",  province);
				
				Cursor mCursorP = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
				if (mCursorP.moveToFirst()) {
					int provinceIndex = mCursorP.getColumnIndex("province_short_name");
					int provinceIdIndex = mCursorP.getColumnIndex("province_id");
					do{
						String tempPName = mCursorP.getString(provinceIndex);
						int tempPID = mCursorP.getInt(provinceIdIndex);
						if(tempPName.equals(province)){
							provinceId = tempPID;
							break;
						}		
					}while(mCursorP.moveToNext());		
				}
				mCursorP.close();
				Log.e("spinner pro id", String.valueOf(provinceId));
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
		
			}
			
		});
		 //get the selected city name and id
		citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				int tempLength = 0;
				String[] tempDistrictSet;
				for(int aa = 0; aa < maxDlength; aa++){
					if(districtSet[provincePosition][position][aa] != null) tempLength++;
				}
				
				tempDistrictSet = new String[tempLength];
				for(int aa = 0; aa < tempLength; aa++){
					tempDistrictSet[aa] = districtSet[provincePosition][position][aa];
				}
						
				districtAdapter = new ArrayAdapter<String>(SetPersonalInfo.this,
                        android.R.layout.simple_spinner_item, tempDistrictSet);
				districtSpinner.setAdapter(districtAdapter);
				cityPosition = position;
				
				city = citySet[provincePosition][position];
				Log.e("Spinner test city",  city);
				
				Cursor mCursorC = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
				if (mCursorC.moveToFirst()) {
					int cityIndex = mCursorC.getColumnIndex("city_short_name");
					int cityIdIndex = mCursorC.getColumnIndex("city_id");
					do{
						String tempCName = mCursorC.getString(cityIndex);
						int tempCID = mCursorC.getInt(cityIdIndex);
						if(tempCName.equals(city)){
							cityId = tempCID;
							break;
						}		
					}while(mCursorC.moveToNext());		
				}
				mCursorC.close();
				Log.e("spinner city id", String.valueOf(cityId));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				 
			}
			
		});
		//get the selected district name and id
		districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
 
				district = districtSet[provincePosition][cityPosition][position];     
				Log.e("Spinner test dis",  district);
				
				Cursor mCursorD = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
				if (mCursorD.moveToFirst()) {
					int districtIndex = mCursorD.getColumnIndex("district_short_name");
					int districtIdIndex = mCursorD.getColumnIndex("district_id");
					do{
						String tempDName = mCursorD.getString(districtIndex);
						int tempDID = mCursorD.getInt(districtIdIndex);
						if(tempDName.equals(district)){
							districtId = tempDID;
							break;
						}		
					}while(mCursorD.moveToNext());		
				}
				mCursorD.close();
				Log.e("spinner dis id", String.valueOf(districtId));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
			
		});		

		addImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO to add the personal image
				
			}
			
		});
		
		setSex.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(Sex == 1){
					Sex = 2;
					sexMan.setImageResource(R.drawable.sex_gray);
					sexWoman.setImageResource(R.drawable.sex_red);
				}else if(Sex == 2){
					Sex = 1;
					sexMan.setImageResource(R.drawable.sex_blue);
					sexWoman.setImageResource(R.drawable.sex_gray);
				}
				
			}
			
		});
		
		sid = loadSid();
		
		mQueue = Volley.newRequestQueue(this);
		try {
			setInfoURL = new URL(HOST + "/user/manage/updateProfile.do" + "?sid=" + sid);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}		
		save.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Name = realName.getText().toString();				
				Nickname = nickName.getText().toString();
				PersonalID = personalID.getText().toString();
				BirthDay = birthYear.getText().toString() + "-" 
						+ (birthMonth.getText().toString().length() == 1 ? ("0" + birthMonth.getText().toString()) : birthMonth.getText().toString()) + "-" 
						+ (birthDay.getText().toString().length() == 1 ? ("0" + birthDay.getText().toString()) : birthDay.getText().toString());		
				MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
						Method.POST, setInfoURL.toString(), null,
						new Response.Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {
								try {
									if (response.getString("sid") != null) {
										sid = response.getString("sid");
										saveSid(sid);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
								Log.e("TEST", response.toString());
								
								try {
									statusCode = response.getInt("code");
								} catch (JSONException e) {
									e.printStackTrace();
								}
								if(statusCode == 1) {
									setPersonal = 1;
									SharedPreferences personalInfo = getSharedPreferences("PERSONSLINFO", MODE_PRIVATE);
									Editor editor = personalInfo.edit();
									editor.putInt("SETINFO", setPersonal);
									editor.commit();
									
									Intent intent = new Intent();
									intent.setClass(SetPersonalInfo.this, CloudDoorMainActivity.class);
									startActivity(intent);
									
									SetPersonalInfo.this.finish();
								} else if (statusCode == -1) {
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
						}) {
					@Override
					protected Map<String, String> getParams()
							throws AuthFailureError {
						Map<String, String> map = new HashMap<String, String>();
						map.put("userName", Name);
						map.put("nickname", Nickname);
						map.put("idCardNo", PersonalID);
						map.put("sex", String.valueOf(Sex));
						map.put("birthday", BirthDay);
						map.put("provinceId", String.valueOf(provinceId));
						map.put("cityId", String.valueOf(cityId));
						map.put("districtId", String.valueOf(districtId));
						return map;
					}
				};
				
				if(Name.equals(null)){
					Toast.makeText(getApplicationContext(), R.string.plz_input_name, Toast.LENGTH_SHORT).show();
				}else if(Nickname.equals(null)){
					Toast.makeText(getApplicationContext(), R.string.plz_input_nickname, Toast.LENGTH_SHORT).show();
				}else if(PersonalID.equals(null)){
					Toast.makeText(getApplicationContext(), R.string.plz_input_id, Toast.LENGTH_SHORT).show();
				}else if(birthYear.getText().toString().equals(null) || birthMonth.getText().toString().equals(null) || birthDay.getText().toString().equals(null)){
					Toast.makeText(getApplicationContext(), R.string.plz_input_birthday, Toast.LENGTH_SHORT).show();
				}else{
					mQueue.add(mJsonRequest);
				}
			}			
		});
	}
	
	public void initSpinnerData() {
		Cursor mCursorP = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		Cursor mCursorC = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		Cursor mCursorD = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		int provinceIdIndex = mCursorP.getColumnIndex("province_id");
		int cityIdIndex = mCursorP.getColumnIndex("city_id");
		int districtIdIndex = mCursorP.getColumnIndex("district_id");
		maxPlength = 1;
		maxClength = 1;
		maxDlength = 1;
		
		if (mCursorP.moveToFirst()) {
			int tempPId = mCursorP.getInt(provinceIdIndex);
			while(mCursorP.moveToNext()){
				if (mCursorP.getInt(provinceIdIndex) != tempPId) {
					tempPId = mCursorP.getInt(provinceIdIndex);
					maxPlength++;
				}
			}
			mCursorP.close();
		}
		
		if(mCursorC.moveToFirst()){
			int tempCcount = 1;
			int tempPId = mCursorC.getInt(provinceIdIndex);
			int tempCId = mCursorC.getInt(cityIdIndex);
			while (mCursorC.moveToNext()) {
				if(mCursorC.getInt(provinceIdIndex) == tempPId
						&& mCursorC.getInt(cityIdIndex) != tempCId){
					tempCId = mCursorC.getInt(cityIdIndex);
					tempCcount++;
				}else if(mCursorC.getInt(provinceIdIndex) != tempPId
						&& mCursorC.getInt(cityIdIndex) != tempCId){
					tempPId = mCursorC.getInt(provinceIdIndex);
					tempCId = mCursorC.getInt(cityIdIndex);
					if(tempCcount > maxClength) {
						maxClength = tempCcount;
					}
					tempCcount = 1;
				}
			}
			mCursorC.close();
		}
		
		if(mCursorD.moveToFirst()){
			int tempDcount = 1;
			int tempPId = mCursorD.getInt(provinceIdIndex);
			int tempCId = mCursorD.getInt(cityIdIndex);
			while (mCursorD.moveToNext()) {
				if(mCursorD.getInt(provinceIdIndex) == tempPId
						&& mCursorD.getInt(cityIdIndex) == tempCId){
					tempDcount++;
				}else if(mCursorD.getInt(provinceIdIndex) == tempPId
						&& mCursorD.getInt(cityIdIndex) != tempCId){
					tempCId = mCursorD.getInt(cityIdIndex);
					if(tempDcount > maxDlength) {
						maxDlength = tempDcount;
					}
					tempDcount = 1;
				}else if(mCursorD.getInt(provinceIdIndex) != tempPId
						&& mCursorD.getInt(cityIdIndex) != tempCId){
					tempPId = mCursorD.getInt(provinceIdIndex);
					tempCId = mCursorD.getInt(cityIdIndex);
					if(tempDcount > maxDlength) {
						maxDlength = tempDcount;
					}
					tempDcount = 1;
				}
			}
			mCursorD.close();
		}
		
		provinceSet = new String[maxPlength];
		citySet = new String[maxPlength][maxClength];
		districtSet = new String[maxPlength][maxClength][maxDlength];
		int a = 0, b = 0, c = 0;
		Cursor mCursor = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		int provinceIndex = mCursor.getColumnIndex("province_short_name");
		int cityIndex = mCursor.getColumnIndex("city_short_name");
		int disdrictIndex = mCursor.getColumnIndex("district_short_name");
		if(mCursor.moveToFirst()){
			provinceSet[a] = mCursor.getString(provinceIndex);
			citySet[a][b] = mCursor.getString(cityIndex);
			districtSet[a][b][c] = mCursor.getString(disdrictIndex);
			
			while(mCursor.moveToNext()){
				if(mCursor.getString(provinceIndex).equals(provinceSet[a])){
					if(mCursor.getString(cityIndex).equals(citySet[a][b])){
						c++;
						districtSet[a][b][c] = mCursor.getString(disdrictIndex);
					}else{
						c = 0;
						b++;
						citySet[a][b] = mCursor.getString(cityIndex);
						districtSet[a][b][c] = mCursor.getString(disdrictIndex);
					}
				}else{
					b = 0;
					c = 0;
					a++;
					provinceSet[a] = mCursor.getString(provinceIndex);
					citySet[a][b] = mCursor.getString(cityIndex);
					districtSet[a][b][c] = mCursor.getString(disdrictIndex);
				}
			}
		}
	}
	
	public void initViews() {
		personImage =  (ImageView) findViewById(R.id.personal_AddPhoto);
		addImage = (TextView) findViewById(R.id.add_image);
		realName = (EditText) findViewById(R.id.personal_RealName);
		nickName = (EditText) findViewById(R.id.personal_NickName);
		setSex = (RelativeLayout) findViewById(R.id.personal_sex);
		sexMan = (ImageView) findViewById(R.id.personal_SexMan);
		sexWoman = (ImageView) findViewById(R.id.personal_SexWoman);
		birthYear = (EditText) findViewById(R.id.personal_Year);
		birthMonth = (EditText) findViewById(R.id.personal_Month);
		birthDay = (EditText) findViewById(R.id.personal_Day);
		personalID = (EditText) findViewById(R.id.personal_ID);
		back = (RelativeLayout) findViewById(R.id.btn_back);
		save = (TextView) findViewById(R.id.save_person_info);
		
		Sex = 1;
		sexMan.setImageResource(R.drawable.sex_blue);
		sexWoman.setImageResource(R.drawable.sex_gray);
	}
	
	private void setSpinner(){
		provinceSpinner = (Spinner) findViewById(R.id.Addr_provice);
		citySpinner = (Spinner) findViewById(R.id.Addr_city);
		districtSpinner = (Spinner) findViewById(R.id.Addr_disctrict);

		provinceAdapter = new ArrayAdapter<String>(SetPersonalInfo.this,
				android.R.layout.simple_spinner_item, provinceSet);
		provinceSpinner.setAdapter(provinceAdapter);

		// some items in the array may be null, so it will cause the NPE.
		// so i delete these codes
//		cityAdapter = new ArrayAdapter<String>(SetPersonalInfo.this,
//				android.R.layout.simple_spinner_item, citySet[0]);
//		citySpinner.setAdapter(cityAdapter);
//
//		districtAdapter = new ArrayAdapter<String>(SetPersonalInfo.this,
//				android.R.layout.simple_spinner_item, districtSet[0][0]);
//		districtSpinner.setAdapter(districtAdapter);
	}
	
	private long DBCount() {  
	    String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
	    SQLiteStatement statement = mAreaDB.compileStatement(sql);
	    long count = statement.simpleQueryForLong();
	    return count;
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		setPersonal = 0;
		SharedPreferences personalInfo = getSharedPreferences("PERSONSLINFO", MODE_PRIVATE);
		Editor editor = personalInfo.edit();
		editor.putInt("SETINFO", setPersonal);
		editor.commit();
		
		finish();
		return super.onKeyDown(keyCode, event);

	}
}
