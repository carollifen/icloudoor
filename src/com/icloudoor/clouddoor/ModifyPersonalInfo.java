package com.icloudoor.clouddoor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.Volley;
import com.icloudoor.clouddoor.Entities.FilePart;
import com.icloudoor.clouddoor.Entities.MultipartEntity;
import com.icloudoor.clouddoor.Entities.Part;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyPersonalInfo extends Activity {
	
	private String TAG = this.getClass().getSimpleName();
	
	private RelativeLayout back;
	private RelativeLayout saveModify;
	
	private EditText ETnickname;
	
	private TextView TVname;
//	private ImageView IVsexImage;
	private TextView TVsex;
	private TextView TVprovince;
	private TextView TVcity;
	private TextView TVdistrict;
	private TextView TVyear;
	private TextView TVmonth;
	private TextView TVday;
	private TextView TVid;
	
	
	private TextView addImage;
	private ImageView showImage;
	
	private String name, province, city, district, year, month, day, id, nickname, birthday;
	private int sex, provinceId, cityId, districtId;
	private String portrailUrl;
	
	private RequestQueue mQueue;
	private URL updateInfoURL;
	private String HOST = "http://zone.icloudoor.com/icloudoor-web";
	private String sid;
	private int statusCode;
	
	private MyAreaDBHelper mAreaDBHelper;
	private SQLiteDatabase mAreaDB;
	private final String DATABASE_NAME = "area.db";
	private final String TABLE_NAME = "tb_core_area";
	
	private List<File> mList;
	
	private String portraitUrl;
	
	private Bitmap bitmap;
	private Thread mThread;
	
	private static final int MSG_SUCCESS = 0;// get the image success
	private static final int MSG_FAILURE = 1;// fail
	
	//
	private String PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/Cloudoor/CacheImage/";
	private String imageName = "myImage.jpg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		setContentView(R.layout.modify_personal_info);
		
		mAreaDBHelper = new MyAreaDBHelper(ModifyPersonalInfo.this, DATABASE_NAME, null, 1);
		mAreaDB = mAreaDBHelper.getWritableDatabase();	
		
		ETnickname = (EditText) findViewById(R.id.personal_modify_NickName);
		TVname = (TextView) findViewById(R.id.personal_info_name);
//		IVsexImage = (ImageView) findViewById(R.id.personal_info_sexImage);
		TVsex = (TextView) findViewById(R.id.personal_info_sexName);
		TVprovince = (TextView) findViewById(R.id.personal_info_province);
		TVcity = (TextView) findViewById(R.id.personal_info_city);
		TVdistrict = (TextView) findViewById(R.id.personal_info_district);
		TVyear = (TextView) findViewById(R.id.personal_info_year);
		TVmonth = (TextView) findViewById(R.id.personal_info_month);
		TVday = (TextView) findViewById(R.id.personal_info_day);
		TVid = (TextView) findViewById(R.id.personal_info_ID);
		
		back = (RelativeLayout) findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		addImage = (TextView) findViewById(R.id.add_image);
		showImage = (ImageView) findViewById(R.id.personal_modifyPhoto);
		addImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ModifyPersonalInfo.this, TakePictureActivity.class);	
				startActivityForResult(intent, 0);
			}
			
		});
		
		saveModify = (RelativeLayout) findViewById(R.id.save_person_info_modify);
		
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
				
				//upload profile
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
						map.put("portraitUrl", portrailUrl);
						return map;
					}
				};
				
				 if(nickname.equals(null)){
					Toast.makeText(getApplicationContext(), R.string.plz_input_nickname, Toast.LENGTH_SHORT).show();
				}else{
					mQueue.add(mJsonRequest);
				}
				 
				// uploading image with a new thread
				
				 
			}	
			
		});
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
         if(requestCode == 0 && resultCode == RESULT_OK) {
        	 
        	 Toast.makeText(this, R.string.uploading_image, Toast.LENGTH_LONG).show();
        	 
            // upload image
        	 new Thread() {

					@Override
					public void run() {

						HttpClient httpClient = new DefaultHttpClient();
						HttpPost postRequest = new HttpPost(HOST
								+ "/user/api/uploadPortrait.do" + "?sid=" + sid);

						File file = null;
						file = new File(PATH + imageName);
						Part[] parts = null;
						FilePart filePart = null;
						try {
							filePart = new FilePart("portrait", file);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						parts = new Part[] { filePart };

						postRequest.setEntity(new MultipartEntity(parts));
						try {
							HttpResponse response = httpClient
									.execute(postRequest);
							BufferedReader reader = new BufferedReader(
									new InputStreamReader(response.getEntity()
											.getContent(), "UTF-8"));
							String sResponse;
							StringBuilder s = new StringBuilder();
							while ((sResponse = reader.readLine()) != null) {
								s = s.append(sResponse);
							}
							Log.e("TEst StringBuilder", s.toString());
							
							//
							JSONObject jsObj = new JSONObject(s.toString());
							JSONObject data = jsObj.getJSONObject("data");
							portraitUrl = data.getString("portraitUrl");
							Log.e(TAG, portraitUrl);
							
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				}.start();
        }
    }
	
	
	
	//TODO
	@Override
	public void onResume() {
		super.onResume();
		
		SharedPreferences homeKeyEvent = getSharedPreferences("HOMEKEY", 0);
	    int 	homePressed = homeKeyEvent.getInt("homePressed", 0);
		
	    SharedPreferences Sign = getSharedPreferences("SETTING", 0);
		int usesign = Sign.getInt("useSign", 0);
		
		if(homePressed == 1 && usesign ==1 ) {

			Intent intent = new Intent();
			intent.setClass(ModifyPersonalInfo.this, VerifyGestureActivity.class);
			startActivity(intent);
		}
		
		SharedPreferences loadProfile = getSharedPreferences("PROFILE", MODE_PRIVATE);
		name = loadProfile.getString("NAME", null);
		sex = loadProfile.getInt("SEX", 1);
//		province = loadProfile.getString("PROVINCE", null);
//		city = loadProfile.getString("CITY", null);
//		district = loadProfile.getString("DISTRICT", null);
		year = loadProfile.getString("YEAR", "2015");
		month = loadProfile.getString("MONTH", "03");
		day = loadProfile.getString("DAY", "30");
		id = loadProfile.getString("ID", null);		
		provinceId = loadProfile.getInt("PROVINCEID", 000000);
		cityId = loadProfile.getInt("CITYID", 000000);
		districtId = loadProfile.getInt("DISTRICTID", 000000);
		
		TVname.setText(name);
		if(sex == 1){
			TVsex.setText(R.string.male);
//			IVsexImage.setImageResource(R.drawable.sex_blue);
		}else if(sex == 2){
			TVsex.setText(R.string.female);
//			IVsexImage.setImageResource(R.drawable.sex_red);
		}
		if(provinceId != 0){
			province = getProvinceName(provinceId);
			TVprovince.setText(province);
		}
			
		if(cityId != 0){
			city = getCityName(cityId);
			TVcity.setText(city);
		}
			
		if(districtId != 0){
			district = getDistrictName(districtId);
			TVdistrict.setText(district);
		}
		TVyear.setText(year);
		TVmonth.setText(month);
		TVday.setText(day);
		TVid.setText(id);
		
//		SharedPreferences takePic = getSharedPreferences("TAKPIC", 0);
//		if(takePic.getInt("TAKEN", 0) == 1){
//			
//			Editor editor = takePic.edit();
//			editor.putInt("TAKEN", 0);
//			editor.commit();
			
//			mList = new ArrayList<File>();
//			String url = Environment.getExternalStorageDirectory().toString()+"/Cloudoor/ImageIcon";
//			File albumdir = new File(url);
//			File[] imgfile = albumdir.listFiles(filefiter);
//			int len = imgfile.length;
//			for(int i=0;i<len;i++){
//				mList.add(imgfile[i]);
//			}
//			Collections.sort(mList, new FileComparator());
//			
//			Log.e("TESt", mList.get(0).getAbsolutePath());
//			
//			Bitmap bm = BitmapFactory.decodeFile(mList.get(0).getAbsolutePath());
//			showImage.setImageBitmap(bm);
			
//			} else {
				SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", 0);
				portrailUrl = loginStatus.getString("URL", null);
				
				File f = new File(PATH + imageName);
				Log.e(TAG, PATH + imageName);
				if(f.exists()){
					Log.e(TAG, "use local");
					Bitmap bm = BitmapFactory.decodeFile(PATH + imageName);
					showImage.setImageBitmap(bm);
				}else{
					// request bitmap in the new thread
					if(portraitUrl != null){
						Log.e(TAG, "use net");
						if (mThread == null) {
							mThread = new Thread(runnable);
							mThread.start();
						}
					}
				}
//			}
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				showImage.setImageBitmap((Bitmap) msg.obj);
				break;
			case MSG_FAILURE:
				break;
			}
		}
	};
	
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(portraitUrl);
			final Bitmap bitmap;
			try {
				org.apache.http.HttpResponse httpResponse = httpClient
						.execute(httpGet);
				bitmap = BitmapFactory.decodeStream(httpResponse.getEntity()
						.getContent());
			} catch (Exception e) {
				mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
				return;
			}
			mHandler.obtainMessage(MSG_SUCCESS, bitmap).sendToTarget();
		}
	};
	
	private FileFilter filefiter = new FileFilter(){

		@Override
		public boolean accept(File pathname) {
			String tmp = pathname.getName().toLowerCase();
			
			if(tmp.endsWith(".png")||tmp.endsWith(".jpg") ||tmp.endsWith(".jpeg")){
				return true;
			}
			return false;
		}
		
	};
	
	private class FileComparator implements Comparator<File>{

		@Override
		public int compare(File lhs, File rhs) {
			if(lhs.lastModified()<rhs.lastModified()){
				return 1;  
			}else 
				return -1;
		}
		
	};
	
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
