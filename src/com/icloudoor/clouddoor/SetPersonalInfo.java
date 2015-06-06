package com.icloudoor.clouddoor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SetPersonalInfo extends Activity {

	private Broadcast mFinishActivityBroadcast;
	
	private String TAG = this.getClass().getSimpleName();

	private MyAreaDBHelper mAreaDBHelper;
	private SQLiteDatabase mAreaDB;
	private final String DATABASE_NAME = "area.db";
	private final String TABLE_NAME = "tb_core_area";

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
	private EditText nickName;
	private RelativeLayout setSexMan;
	private RelativeLayout setSexWoman;
	private ImageView sexMan;
	private ImageView sexWoman;
	private EditText birthYear;
	private EditText birthMonth;
	private EditText birthDay;
	private TextView personalID;
	private TextView realName;

	private static final int MSG_SUCCESS = 0;// get the image success
	private static final int MSG_FAILURE = 1;// fail
	private Thread mThread;
	
	private String NAME;
	private String ID;
	private String whereFrom;

	private String Name, Nickname, Age, PersonalID, province, city, district,
			year, month, day, BirthDay;
	private int Sex, provinceId, cityId, districtId;
	private String portraitUrl;

	private RelativeLayout back;
	private RelativeLayout save;

	private RequestQueue mQueue;
	private URL setInfoURL;
	private String HOST = "https://zone.icloudoor.com/icloudoor-web";
	private String sid;
	private int statusCode;

	private int setPersonal = 0;

	//
	private String PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/Cloudoor/CacheImage/";
	private String imageName = "myImage.jpg";

	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int PICTURE_REQUEST_CODE = 2;
	
	private SelectPicPopupWindow menuWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getActionBar().hide();
		whereFrom = getIntent().getStringExtra("Whereis");
		setContentView(R.layout.set_person_info);
		
		mFinishActivityBroadcast=	new Broadcast();
		 IntentFilter intentFilter = new IntentFilter();
		    intentFilter.addAction("com.icloudoor.clouddoor.ACTION_FINISH");
		    registerReceiver(mFinishActivityBroadcast, intentFilter);



		back = (RelativeLayout) findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});

		// the ID and realname of certi user cannot be modified
		SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS",
				MODE_PRIVATE);
		NAME = loginStatus.getString("NAME", null);
		ID = loginStatus.getString("ID", null);
		portraitUrl = loginStatus.getString("URL", null);

		realName = (TextView) findViewById(R.id.personal_RealName);
		personalID = (TextView) findViewById(R.id.personal_ID);

		realName.setText(NAME);

		if (ID.length() > 0) {
			StringBuilder sb = new StringBuilder(ID);
			for (int i = 3; i < 14; i++) {
				sb.setCharAt(i, '*');
			}
			personalID.setText(sb.toString());
		}
		//

		mAreaDBHelper = new MyAreaDBHelper(SetPersonalInfo.this, DATABASE_NAME,
				null, 1);
		mAreaDB = mAreaDBHelper.getWritableDatabase();

		initViews();

		//
		File f = new File(PATH + imageName);
		if (f.exists()) {
			Log.e(TAG, "use local");
			BitmapFactory.Options opts=new BitmapFactory.Options();
			opts.inTempStorage = new byte[100 * 1024];
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inSampleSize = 8;
			Bitmap bm = BitmapFactory.decodeFile(PATH + imageName, opts);
			personImage.setImageBitmap(bm);
		}
		//

		initSpinnerData();

		setSpinner();

		// get the selected province name and id
		provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

						int tempLength = 0;
						String[] tempCitySet;
						for (int aa = 0; aa < maxClength; aa++) {
							if (citySet[position][aa] != null)
								tempLength++;
						}

						tempCitySet = new String[tempLength];
						for (int aa = 0; aa < tempLength; aa++) {
							tempCitySet[aa] = citySet[position][aa];
						}

						cityAdapter = new ArrayAdapter<String>(SetPersonalInfo.this, android.R.layout.simple_spinner_item, tempCitySet);
						citySpinner.setAdapter(cityAdapter);
						provincePosition = position;

						province = provinceSet[position];
						Log.e("Spinner test pro", province);

						Cursor mCursorP = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
						if (mCursorP.moveToFirst()) {
							int provinceIndex = mCursorP.getColumnIndex("province_short_name");
							int provinceIdIndex = mCursorP.getColumnIndex("province_id");
							do {
								String tempPName = mCursorP.getString(provinceIndex);
								int tempPID = mCursorP.getInt(provinceIdIndex);
								if (tempPName.equals(province)) {
									provinceId = tempPID;
									break;
								}
							} while (mCursorP.moveToNext());
						}
						mCursorP.close();
						Log.e("spinner pro id", String.valueOf(provinceId));

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}

				});
		// get the selected city name and id
		citySpinner
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						int tempLength = 0;
						String[] tempDistrictSet;
						for (int aa = 0; aa < maxDlength; aa++) {
							if (districtSet[provincePosition][position][aa] != null)
								tempLength++;
						}

						tempDistrictSet = new String[tempLength];
						for (int aa = 0; aa < tempLength; aa++) {
							tempDistrictSet[aa] = districtSet[provincePosition][position][aa];
						}

						districtAdapter = new ArrayAdapter<String>(SetPersonalInfo.this, android.R.layout.simple_spinner_item, tempDistrictSet);
						districtSpinner.setAdapter(districtAdapter);
						cityPosition = position;

						city = citySet[provincePosition][position];
						Log.e("Spinner test city", city);

						Cursor mCursorC = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
						if (mCursorC.moveToFirst()) {
							int cityIndex = mCursorC.getColumnIndex("city_short_name");
							int cityIdIndex = mCursorC.getColumnIndex("city_id");
							do {
								String tempCName = mCursorC.getString(cityIndex);
								int tempCID = mCursorC.getInt(cityIdIndex);
								if (tempCName.equals(city)) {
									cityId = tempCID;
									break;
								}
							} while (mCursorC.moveToNext());
						}
						mCursorC.close();
						Log.e("spinner city id", String.valueOf(cityId));
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}

				});
		// get the selected district name and id
		districtSpinner
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						district = districtSet[provincePosition][cityPosition][position];
						Log.e("Spinner test dis", district);

						Cursor mCursorD = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
						if (mCursorD.moveToFirst()) {
							int districtIndex = mCursorD.getColumnIndex("district_short_name");
							int districtIdIndex = mCursorD.getColumnIndex("district_id");
							do {
								String tempDName = mCursorD.getString(districtIndex);
								int tempDID = mCursorD.getInt(districtIdIndex);
								if (tempDName.equals(district)) {
									districtId = tempDID;
									break;
								}
							} while (mCursorD.moveToNext());
						}
						mCursorD.close();
						Log.e("spinner dis id", String.valueOf(districtId));
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}

				});

		personImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO to gallery or camera
				// Intent intent = new Intent();
				// intent.setClass(SetPersonalInfo.this,
				// TakePictureActivity.class);
				// startActivityForResult(intent, 0);

				menuWindow = new SelectPicPopupWindow(SetPersonalInfo.this, itemsOnClick); 
				menuWindow.showAtLocation(SetPersonalInfo.this.findViewById(R.id.main), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
//				openOptionsMenu();
			}

		});

		setSexMan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Sex == 2) {
					Sex = 1;
					sexMan.setImageResource(R.drawable.select);
					sexWoman.setImageResource(R.drawable.not_select);
				}

			}

		});

		setSexWoman.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Sex == 1) {
					Sex = 2;
					sexMan.setImageResource(R.drawable.not_select);
					sexWoman.setImageResource(R.drawable.select);
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
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Name = realName.getText().toString();
				Nickname = nickName.getText().toString();
				// PersonalID = personalID.getText().toString();
				BirthDay = birthYear.getText().toString() + "-" + (birthMonth.getText().toString().length() == 1 ? ("0" + birthMonth.getText().toString()) : birthMonth.getText().toString())
						+ "-" + (birthDay.getText().toString().length() == 1 ? ("0" + birthDay.getText().toString()) : birthDay.getText().toString());
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
								if (statusCode == 1) {
									setPersonal = 1;
									SharedPreferences personalInfo = getSharedPreferences("PERSONSLINFO", MODE_PRIVATE);
									Editor editor = personalInfo.edit();
									editor.putInt("SETINFO", setPersonal);
									editor.commit();

									// SharedPreferences loginStatus =
									// getSharedPreferences("LOGINSTATUS",
									// MODE_PRIVATE);
									// Editor editor1 = loginStatus.edit();
									// editor1.putString("NAME", Name);
									// editor1.commit();
									if (whereFrom == null) {
										Intent intent = new Intent();
										intent.setClass(SetPersonalInfo.this, CloudDoorMainActivity.class);
										startActivity(intent);
									}
									
									setResult(RESULT_OK);
									SetPersonalInfo.this.finish();
								} else if (statusCode == -1) {
									Toast.makeText(getApplicationContext(), R.string.not_enough_params, Toast.LENGTH_SHORT).show();
								} else if (statusCode == -2) {
									Toast.makeText(getApplicationContext(), R.string.not_login, Toast.LENGTH_SHORT).show();
								} else if (statusCode == -99) {
									Toast.makeText(getApplicationContext(), R.string.unknown_err, Toast.LENGTH_SHORT).show();
								} else if (statusCode == -42) {
									Toast.makeText(getApplicationContext(), R.string.nick_name_already, Toast.LENGTH_SHORT).show();
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
						map.put("userName", NAME);
						map.put("nickname", Nickname);
						map.put("idCardNo", ID);
						map.put("sex", String.valueOf(Sex));
						map.put("birthday", BirthDay);
						map.put("provinceId", String.valueOf(provinceId));
						map.put("cityId", String.valueOf(cityId));
						map.put("districtId", String.valueOf(districtId));
						map.put("portraitUrl", portraitUrl);
						return map;
					}
				};

				if (NAME.equals(null)) {
					Toast.makeText(getApplicationContext(), R.string.plz_input_name, Toast.LENGTH_SHORT).show();
				} else if (Nickname.equals(null)) {
					Toast.makeText(getApplicationContext(), R.string.plz_input_nickname, Toast.LENGTH_SHORT).show();
				} else if (ID.equals(null)) {
					Toast.makeText(getApplicationContext(), R.string.plz_input_id, Toast.LENGTH_SHORT).show();
				} else if (birthYear.getText().toString().equals(null)
						|| birthMonth.getText().toString().equals(null)
						|| birthDay.getText().toString().equals(null)) {
					Toast.makeText(getApplicationContext(), R.string.plz_input_birthday, Toast.LENGTH_SHORT).show();
				} else if (portraitUrl.equals(null)) {
					Toast.makeText(getApplicationContext(), R.string.plz_upload_image, Toast.LENGTH_SHORT).show();
				} else {
					mQueue.add(mJsonRequest);
				}
			}
		});
	}

	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			menuWindow.dismiss();
			switch (v.getId()) {
			case R.id.btn_take_photo:
				startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 1);
				break;
			case R.id.btn_pick_photo:
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, 0);
				break;
			default:
				menuWindow.dismiss();
				break;
			}

		}

	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0 && resultCode == RESULT_OK) {

			final Uri uri = data.getData();
			Log.e(TAG, "uri: " + getRealPathFromURI(uri));
			
			BitmapFactory.Options opts=new BitmapFactory.Options();
			opts.inTempStorage = new byte[100 * 1024];
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inSampleSize = 4;
			Bitmap bm = BitmapFactory.decodeFile(getRealPathFromURI(uri), opts);

			if(bm.getWidth() < bm.getHeight()){
				bm = zoomImage(bm, 400, 400);
			}else{
				bm = getRotateBitmap(bm, 90);
				bm = zoomImage(bm, 400, 400);
			}
		
			TakePicFileUtil.getInstance().saveBitmap(bm);			
			personImage.setImageBitmap(bm);

			// upload image
			new Thread() {

				@Override
				public void run() {
					
					Log.e(TAG, "thread run");
					
					try {
						sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					HttpClient httpClient = new DefaultHttpClient();
					HttpPost postRequest = new HttpPost(HOST + "/user/api/uploadPortrait.do" + "?sid=" + sid);

					File file = null;
					file = new File(getRealPathFromURI(uri));
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
						HttpResponse response = httpClient.execute(postRequest);
						BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
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
		} else if (requestCode == CAMERA_REQUEST_CODE
				&& resultCode == Activity.RESULT_OK && data != null) {
			
			final Uri uri = data.getData();
			BitmapFactory.Options opts=new BitmapFactory.Options();
			opts.inTempStorage = new byte[100 * 1024];
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inSampleSize = 4;
			Bitmap bm = BitmapFactory.decodeFile(getRealPathFromURI(uri), opts);
			
			if(bm.getWidth() < bm.getHeight()){
				bm = zoomImage(bm, 400, 400);
			}else{
				bm = getRotateBitmap(bm, 90);
				bm = zoomImage(bm, 400, 400);
			}
			
			TakePicFileUtil.getInstance().saveBitmap(bm);
			personImage.setImageBitmap(bm);
			
			new Thread() {

				@Override
				public void run() {

					try {
						sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					HttpClient httpClient = new DefaultHttpClient();
					HttpPost postRequest = new HttpPost(HOST + "/user/api/uploadPortrait.do" + "?sid=" + sid);

					File file = null;
					file = new File(getRealPathFromURI(uri));
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
						HttpResponse response = httpClient.execute(postRequest);
						BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
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

	public void onResume() {
		super.onResume();
		SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", 0);
		portraitUrl = loginStatus.getString("URL", null);

		File f = new File(PATH + imageName);
		Log.e(TAG, PATH + imageName);
		if (f.exists()) {
			Log.e(TAG, "use local");
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inTempStorage = new byte[100 * 1024];
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inSampleSize = 4;
			Bitmap bm = BitmapFactory.decodeFile(PATH + imageName, opts);
			personImage.setImageBitmap(bm);
		} else {
			// request bitmap in the new thread
			if (portraitUrl != null) {
				Log.e(TAG, "use net");
				if (mThread == null) {
					mThread = new Thread(runnable);
					mThread.start();
				}
			}
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				personImage.setImageBitmap((Bitmap) msg.obj);
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
				org.apache.http.HttpResponse httpResponse = httpClient.execute(httpGet);
				BitmapFactory.Options opts=new BitmapFactory.Options();
				opts.inTempStorage = new byte[100 * 1024];
				opts.inPreferredConfig = Bitmap.Config.RGB_565;
				opts.inPurgeable = true;
				opts.inSampleSize = 4;
				bitmap = BitmapFactory.decodeStream(httpResponse.getEntity().getContent(), null, opts);
			} catch (Exception e) {
				mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
				return;
			}
			mHandler.obtainMessage(MSG_SUCCESS, bitmap).sendToTarget();
		}
	};

	

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
			while (mCursorP.moveToNext()) {
				if (mCursorP.getInt(provinceIdIndex) != tempPId) {
					tempPId = mCursorP.getInt(provinceIdIndex);
					maxPlength++;
				}
			}
			mCursorP.close();
		}

		if (mCursorC.moveToFirst()) {
			int tempCcount = 1;
			int tempPId = mCursorC.getInt(provinceIdIndex);
			int tempCId = mCursorC.getInt(cityIdIndex);
			while (mCursorC.moveToNext()) {
				if (mCursorC.getInt(provinceIdIndex) == tempPId && mCursorC.getInt(cityIdIndex) != tempCId) {
					tempCId = mCursorC.getInt(cityIdIndex);
					tempCcount++;
				} else if (mCursorC.getInt(provinceIdIndex) != tempPId && mCursorC.getInt(cityIdIndex) != tempCId) {
					tempPId = mCursorC.getInt(provinceIdIndex);
					tempCId = mCursorC.getInt(cityIdIndex);
					if (tempCcount > maxClength) {
						maxClength = tempCcount;
					}
					tempCcount = 1;
				}
			}
			mCursorC.close();
		}

		if (mCursorD.moveToFirst()) {
			int tempDcount = 1;
			int tempPId = mCursorD.getInt(provinceIdIndex);
			int tempCId = mCursorD.getInt(cityIdIndex);
			while (mCursorD.moveToNext()) {
				if (mCursorD.getInt(provinceIdIndex) == tempPId && mCursorD.getInt(cityIdIndex) == tempCId) {
					tempDcount++;
				} else if (mCursorD.getInt(provinceIdIndex) == tempPId && mCursorD.getInt(cityIdIndex) != tempCId) {
					tempCId = mCursorD.getInt(cityIdIndex);
					if (tempDcount > maxDlength) {
						maxDlength = tempDcount;
					}
					tempDcount = 1;
				} else if (mCursorD.getInt(provinceIdIndex) != tempPId && mCursorD.getInt(cityIdIndex) != tempCId) {
					tempPId = mCursorD.getInt(provinceIdIndex);
					tempCId = mCursorD.getInt(cityIdIndex);
					if (tempDcount > maxDlength) {
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
		if (mCursor.moveToFirst()) {
			provinceSet[a] = mCursor.getString(provinceIndex);
			citySet[a][b] = mCursor.getString(cityIndex);
			districtSet[a][b][c] = mCursor.getString(disdrictIndex);

			while (mCursor.moveToNext()) {
				if (mCursor.getString(provinceIndex).equals(provinceSet[a])) {
					if (mCursor.getString(cityIndex).equals(citySet[a][b])) {
						c++;
						districtSet[a][b][c] = mCursor.getString(disdrictIndex);
					} else {
						c = 0;
						b++;
						citySet[a][b] = mCursor.getString(cityIndex);
						districtSet[a][b][c] = mCursor.getString(disdrictIndex);
					}
				} else {
					b = 0;
					c = 0;
					a++;
					provinceSet[a] = mCursor.getString(provinceIndex);
					citySet[a][b] = mCursor.getString(cityIndex);
					districtSet[a][b][c] = mCursor.getString(disdrictIndex);
				}
			}
		}
		mCursor.close();
	}

	public void initViews() {
		personImage = (ImageView) findViewById(R.id.personal_AddPhoto);
		addImage = (TextView) findViewById(R.id.add_image);
		realName = (TextView) findViewById(R.id.personal_RealName);
		nickName = (EditText) findViewById(R.id.personal_NickName);
		setSexMan = (RelativeLayout) findViewById(R.id.personal_sex_man);
		setSexWoman = (RelativeLayout) findViewById(R.id.personal_sex_woman);
		sexMan = (ImageView) findViewById(R.id.personal_SexMan);
		sexWoman = (ImageView) findViewById(R.id.personal_SexWoman);
		birthYear = (EditText) findViewById(R.id.personal_year);
		birthMonth = (EditText) findViewById(R.id.personal_month);
		birthDay = (EditText) findViewById(R.id.personal_day);
		personalID = (TextView) findViewById(R.id.personal_ID);
		back = (RelativeLayout) findViewById(R.id.btn_back);
		save = (RelativeLayout) findViewById(R.id.save_person_info);

		Sex = 2;
		sexMan.setImageResource(R.drawable.not_select);
		sexWoman.setImageResource(R.drawable.select);

		SharedPreferences saveProfile = getSharedPreferences("PROFILE", MODE_PRIVATE);

		nickName.setText(saveProfile.getString("NICKNAME", ""));
		birthYear.setText(saveProfile.getString("YEAR", ""));
		birthMonth.setText(saveProfile.getString("MONTH", ""));
		birthDay.setText(saveProfile.getString("DAY", ""));
	}

	private void setSpinner() {
		provinceSpinner = (Spinner) findViewById(R.id.Addr_provice);
		citySpinner = (Spinner) findViewById(R.id.Addr_city);
		districtSpinner = (Spinner) findViewById(R.id.Addr_disctrict);

		provinceAdapter = new ArrayAdapter<String>(SetPersonalInfo.this, android.R.layout.simple_spinner_item, provinceSet);
		provinceSpinner.setAdapter(provinceAdapter);

		// some items in the array may be null, so it will cause the NPE.
		// so i delete these codes
		// cityAdapter = new ArrayAdapter<String>(SetPersonalInfo.this,
		// android.R.layout.simple_spinner_item, citySet[0]);
		// citySpinner.setAdapter(cityAdapter);
		//
		// districtAdapter = new ArrayAdapter<String>(SetPersonalInfo.this,
		// android.R.layout.simple_spinner_item, districtSet[0][0]);
		// districtSpinner.setAdapter(districtAdapter);
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
		if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
//			setPersonal = 0;
//			SharedPreferences personalInfo = getSharedPreferences("PERSONSLINFO", MODE_PRIVATE);
//			Editor editor = personalInfo.edit();
//			editor.putInt("SETINFO", setPersonal);
//			editor.commit();

			finish();
		}
		return super.onKeyDown(keyCode, event);

	}
	
	public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
	
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
			double newHeight) {

		float width = bgimage.getWidth();
		float height = bgimage.getHeight();

		Matrix matrix = new Matrix();

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		return bitmap;
	}
	
	public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree){
		Matrix matrix = new Matrix();
		matrix.postRotate((float)rotateDegree);
		Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
		return rotaBitmap;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mFinishActivityBroadcast);
		
	}
	
	class Broadcast extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			SetPersonalInfo.this.finish();
		}
		
	}
	
}
