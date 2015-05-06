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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyPersonalInfo extends Activity {
	
	private RelativeLayout back;
	private TextView saveModify;
	
	private EditText ETnickname;
	
	private TextView TVname;
	private ImageView IVsexImage;
	private TextView TVsex;
	private TextView TVprovince;
	private TextView TVcity;
	private TextView TVdistrict;
	private TextView TVyear;
	private TextView TVmonth;
	private TextView TVday;
	private TextView TVid;
	
	private String name, province, city, district, year, month, day, id, nickname, birthday;
	private int sex, provinceId, cityId, districtId;
	
	private RequestQueue mQueue;
	private URL updateInfoURL;
	private String HOST = "http://zone.icloudoor.com/icloudoor-web";
	private String sid;
	private int statusCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.modify_personal_info);
		
		ETnickname = (EditText) findViewById(R.id.personal_modify_NickName);
		TVname = (TextView) findViewById(R.id.personal_info_name);
		IVsexImage = (ImageView) findViewById(R.id.personal_info_sexImage);
		TVsex = (TextView) findViewById(R.id.personal_info_sexName);
		TVprovince = (TextView) findViewById(R.id.personal_info_province);
		TVcity = (TextView) findViewById(R.id.personal_info_city);
		TVdistrict = (TextView) findViewById(R.id.personal_info_district);
		TVyear = (TextView) findViewById(R.id.personal_info_year);
		TVmonth = (TextView) findViewById(R.id.personal_info_month);
		TVday = (TextView) findViewById(R.id.personal_info_day);
		TVid = (TextView) findViewById(R.id.personal_info_ID);
		
		SharedPreferences loadProfile = getSharedPreferences("PROFILE", MODE_PRIVATE);
		name = loadProfile.getString("NAME", null);
		sex = loadProfile.getInt("SEX", 1);
		province = loadProfile.getString("PROVINCE", null);
		city = loadProfile.getString("CITY", null);
		district = loadProfile.getString("DISTRICT", null);
		year = loadProfile.getString("YEAR", "2015");
		month = loadProfile.getString("MONTH", "03");
		day = loadProfile.getString("DAY", "30");
		id = loadProfile.getString("ID", null);
		
		provinceId = loadProfile.getInt("PROVINCEID", 000000);
		cityId = loadProfile.getInt("CITYID", 000000);
		districtId = loadProfile.getInt("DISTRICTID", 000000);
		
		TVname.setText(name);
		if(sex == 1){
			TVsex.setText("ÄÐ");
			IVsexImage.setImageResource(R.drawable.sex_blue);
		}else if(sex == 2){
			TVsex.setText("Å®");
			IVsexImage.setImageResource(R.drawable.sex_red);
		}
		TVprovince.setText(province);
		TVcity.setText(city);
		TVdistrict.setText(district);
		TVyear.setText(year);
		TVmonth.setText(month);
		TVday.setText(day);
		TVid.setText(id);
		
		back = (RelativeLayout) findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		saveModify = (TextView) findViewById(R.id.save_person_info_modify);
		
		sid = loadSid();
		
		mQueue = Volley.newRequestQueue(this);
		try {
			updateInfoURL = new URL(HOST + "/user/manage/updateProfile.do" + "?sid=" + sid);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}	
		
		saveModify.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {	
				
				nickname = ETnickname.getText().toString();
				birthday = year + "-" + month + "-" + day;				
				MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
						Method.POST, updateInfoURL.toString(), null,
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
									finish();									
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
						map.put("userName", name);
						map.put("nickname", nickname);
						map.put("idCardNo", id);
						map.put("sex", String.valueOf(sex));
						map.put("birthday", birthday);
						map.put("provinceId", String.valueOf(provinceId));
						map.put("cityId", String.valueOf(cityId));
						map.put("districtId", String.valueOf(districtId));
						return map;
					}
				};
				
				 if(nickname.equals(null)){
					Toast.makeText(getApplicationContext(), R.string.plz_input_nickname, Toast.LENGTH_SHORT).show();
				}else{
					mQueue.add(mJsonRequest);
				}
			}	
			
		});
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
