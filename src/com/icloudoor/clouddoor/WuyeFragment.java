package com.icloudoor.clouddoor;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WuyeFragment extends Fragment {

	private String TAG = this.getClass().getSimpleName();

	private ImageView WuyeWidgePush1;
	private ImageView WuyeWidgePush2;
	private ImageView WuyeWidgePush3;

	private ImageView BtnLianxiwuye;
	private ImageView BtnNotice;
	private ImageView BtnFix;
	private ImageView BtnBad;
	private ImageView BtnGood;
	private ImageView BtnQuery;
	private ImageView BtnBill;
	private ImageView BtnPay;

	private RelativeLayout unreadNoticeLayout;
	private RelativeLayout unreadQueryLayout;
	private ImageView unreadNoticeDot;
	private ImageView unreadQueryDot;
	private TextView unreadNoticeCount;
	private TextView unreadQueryCount;
	private int unreadNotice, unreadQuery;

	public MyClickListener myClick;

	private AutoScrollViewPager viewPager;
	// private ViewPager mWuyeWidgePager;
	private ArrayList<Fragment> mWuyePageFragmentList;
	private WuyePageAdapter mWuyePageAdapter;
	public FragmentManager mFragmentManager;
	private WuyeWidgeFragment mWuyeWidgeFragment;
	private WuyeWidgeFragment2 mWuyeWidgeFragment2;
	private WuyeWidgeFragment3 mWuyeWidgeFragment3;
	public MyPageChangeListener myPageChangeListener;

	private URL unReadURL;
	private RequestQueue mQueue;
	private String HOST = "https://zone.icloudoor.com/icloudoor-web";
	private String sid;

	// for test
	private URL bannerURL;

	public WuyeFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.wuye_page, container, false);

		mQueue = Volley.newRequestQueue(getActivity());
		sid = loadSid();

		WuyeWidgePush1 = (ImageView) view.findViewById(R.id.Iv_wuye_widge_push1);
		WuyeWidgePush2 = (ImageView) view.findViewById(R.id.Iv_wuye_widge_push2);
		WuyeWidgePush3 = (ImageView) view.findViewById(R.id.Iv_wuye_widge_push3);

		BtnLianxiwuye = (ImageView) view.findViewById(R.id.btn_lianxiwuye);
		BtnNotice = (ImageView) view.findViewById(R.id.btn_notice);
		BtnFix = (ImageView) view.findViewById(R.id.btn_fix);
		BtnBad = (ImageView) view.findViewById(R.id.btn_bad);
		BtnGood = (ImageView) view.findViewById(R.id.btn_good);
		BtnQuery = (ImageView) view.findViewById(R.id.btn_query);
		BtnBill = (ImageView) view.findViewById(R.id.btn_bill);
		BtnPay = (ImageView) view.findViewById(R.id.btn_pay);

		unreadNoticeLayout = (RelativeLayout) view.findViewById(R.id.unread_notice_layout);
		unreadQueryLayout = (RelativeLayout) view.findViewById(R.id.unread_query_layout);
		unreadNoticeCount = (TextView) view.findViewById(R.id.unread_notice);
		unreadQueryCount = (TextView) view.findViewById(R.id.unread_query);
		unreadNoticeDot = (ImageView) view.findViewById(R.id.red_dot_notice);
		unreadQueryDot = (ImageView) view.findViewById(R.id.red_dot_query);

		unreadNoticeLayout.setVisibility(View.INVISIBLE);
		unreadQueryLayout.setVisibility(View.INVISIBLE);
		unreadNoticeCount.setText("");
		unreadQueryCount.setText("");

		myClick = new MyClickListener();

		BtnLianxiwuye.setOnClickListener(myClick);
		BtnNotice.setOnClickListener(myClick);
		BtnFix.setOnClickListener(myClick);
		BtnBad.setOnClickListener(myClick);
		BtnGood.setOnClickListener(myClick);
		BtnQuery.setOnClickListener(myClick);
		BtnBill.setOnClickListener(myClick);
		BtnPay.setOnClickListener(myClick);

		mFragmentManager = getChildFragmentManager();
		viewPager = (AutoScrollViewPager) view
				.findViewById(R.id.wuye_widge_pager);
		// mWuyeWidgePager = (ViewPager)
		// view.findViewById(R.id.wuye_widge_pager);
		myPageChangeListener = new MyPageChangeListener();

		InitFragmentViews();
		InitViewPager();

		return view;
	}

	public void InitFragmentViews() {
		WuyeWidgePush1.setImageResource(R.drawable.wuye_push_current);
		WuyeWidgePush2.setImageResource(R.drawable.wuye_push_next);
		WuyeWidgePush3.setImageResource(R.drawable.wuye_push_next);
	}

	public void InitViewPager() {
		mWuyePageFragmentList = new ArrayList<Fragment>();

		mWuyeWidgeFragment = new WuyeWidgeFragment();
		mWuyeWidgeFragment2 = new WuyeWidgeFragment2();
		mWuyeWidgeFragment3 = new WuyeWidgeFragment3();

		mWuyePageFragmentList.add(mWuyeWidgeFragment);
		mWuyePageFragmentList.add(mWuyeWidgeFragment2);
		mWuyePageFragmentList.add(mWuyeWidgeFragment3);

		mWuyePageAdapter = new WuyePageAdapter(mFragmentManager,
				mWuyePageFragmentList);
		viewPager.setAdapter(mWuyePageAdapter);
		viewPager.setOnPageChangeListener(myPageChangeListener);
		viewPager.setInterval(4000);
		viewPager.startAutoScroll();
		viewPager.setCurrentItem(0);
		// mWuyeWidgePager.setAdapter(mWuyePageAdapter);
		// mWuyeWidgePager.setCurrentItem(0);
		// mWuyeWidgePager.setOnPageChangeListener(myPageChangeListener);
	}
	
	public class MyPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// if (arg0 == 2) {
			// int index = mWuyeWidgePager.getCurrentItem();
			// mWuyeWidgePager.setCurrentItem(index);
			// if(index == 0){
			// WuyeWidgePush1.setImageResource(R.drawable.wuye_push_current);
			// WuyeWidgePush2.setImageResource(R.drawable.wuye_push_next);
			// }else if(index == 1){
			// WuyeWidgePush2.setImageResource(R.drawable.wuye_push_current);
			// WuyeWidgePush1.setImageResource(R.drawable.wuye_push_next);
			// }
			// }
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			Log.e(TAG, String.valueOf(position));

			if (position == 0) {
				WuyeWidgePush1.setImageResource(R.drawable.wuye_push_current);
				WuyeWidgePush2.setImageResource(R.drawable.wuye_push_next);
				WuyeWidgePush3.setImageResource(R.drawable.wuye_push_next);
			} else if (position == 1) {
				WuyeWidgePush2.setImageResource(R.drawable.wuye_push_current);
				WuyeWidgePush1.setImageResource(R.drawable.wuye_push_next);
				WuyeWidgePush3.setImageResource(R.drawable.wuye_push_next);
			} else if (position == 2) {
				WuyeWidgePush3.setImageResource(R.drawable.wuye_push_current);
				WuyeWidgePush1.setImageResource(R.drawable.wuye_push_next);
				WuyeWidgePush2.setImageResource(R.drawable.wuye_push_next);
			}
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		// stop auto scroll when onPause
		viewPager.stopAutoScroll();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		Log.e(TAG, "test");
		
		// start auto scroll when onResume
		viewPager.startAutoScroll();

		try {
			bannerURL = new URL(
					"https://zone.icloudoor.com/icloudoor-web/user/prop/zone/getBannerRotate.do"
							+ "?sid=" + sid);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		JsonObjectRequest mBannerRequest = new JsonObjectRequest(Method.POST,
				bannerURL.toString(), null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.e("response", response.toString());
						try {

							Log.e(TAG, response.toString());

							if (response.getInt("code") == 1) {
								if (getActivity() != null) {
									SharedPreferences banner = getActivity().getSharedPreferences("BANNER", 0);
									Editor editor = banner.edit();

									JSONArray data = response.getJSONArray("data");
									if (data.length() == 1) {
										if (data.getJSONObject(0).getString("type").equals("1")) {
											String bg = data.getJSONObject(0).getString("bgColor");
											String content = data.getJSONObject(0).getString("content");
											String title = data.getJSONObject(0).getString("title");
											String date = data.getJSONObject(0).getString("createDate");

											editor.putString("1bg", bg);
											editor.putString("1content", content);
											editor.putString("1title", title);
											editor.putString("1date", date);
											editor.putString("1type", "1");
										} else if (data.getJSONObject(0).getString("type").equals("2")) {
											String url = data.getJSONObject(0).getString("photoUrl");
											String link = data.getJSONObject(0).getString("link");
											editor.putString("1url", url);
											editor.putString("1link", link);
											editor.putString("1type", "2");
										}
									} else if (data.length() == 2) {
										if (data.getJSONObject(0).getString("type").equals("1")) {
											String bg = data.getJSONObject(0).getString("bgColor");
											String content = data.getJSONObject(0).getString("content");
											String title = data.getJSONObject(0).getString("title");
											String date = data.getJSONObject(0).getString("createDate");

											editor.putString("1bg", bg);
											editor.putString("1content", content);
											editor.putString("1title", title);
											editor.putString("1date", date);
											editor.putString("1type", "1");
										} else if (data.getJSONObject(0).getString("type").equals("2")) {
											String url = data.getJSONObject(0).getString("photoUrl");
											String link = data.getJSONObject(0).getString("link");
											editor.putString("1url", url);
											editor.putString("1link", link);
											editor.putString("1type", "2");
										}

										if (data.getJSONObject(1).getString("type").equals("1")) {
											String bg = data.getJSONObject(1).getString("bgColor");
											String content = data.getJSONObject(1).getString("content");
											String title = data.getJSONObject(1).getString("title");
											String date = data.getJSONObject(1).getString("createDate");

											editor.putString("2bg", bg);
											editor.putString("2content", content);
											editor.putString("2title", title);
											editor.putString("2date", date);
											editor.putString("2type", "1");
										} else if (data.getJSONObject(1).getString("type").equals("2")) {
											String url = data.getJSONObject(1).getString("photoUrl");
											String link = data.getJSONObject(1).getString("link");
											editor.putString("2url", url);
											editor.putString("2link", link);
											editor.putString("2type", "2");
										}
									} else if (data.length() == 3) {

										Log.e(TAG, "here");

										if (data.getJSONObject(0).getString("type").equals("1")) {
											String bg = data.getJSONObject(0).getString("bgColor");
											String content = data.getJSONObject(0).getString("content");
											String title = data.getJSONObject(0).getString("title");
											String date = data.getJSONObject(0).getString("createDate");
											
											editor.putString("1bg", bg);
											editor.putString("1content", content);
											editor.putString("1title", title);
											editor.putString("1date", date);
											editor.putString("1type", "1");
										} else if (data.getJSONObject(0).getString("type").equals("2")) {
											String url = data.getJSONObject(0).getString("photoUrl");
											String link = data.getJSONObject(0).getString("link");
											editor.putString("1url", url);
											editor.putString("1link", link);
											editor.putString("1type", "2");
										}

										if (data.getJSONObject(1).getString("type").equals("1")) {
											String bg = data.getJSONObject(1).getString("bgColor");
											String content = data.getJSONObject(1).getString("content");
											String title = data.getJSONObject(1).getString("title");
											String date = data.getJSONObject(1).getString("createDate");

											editor.putString("2bg", bg);
											editor.putString("2content", content);
											editor.putString("2title", title);
											editor.putString("2date", date);
											editor.putString("2type", "1");
										} else if (data.getJSONObject(1).getString("type").equals("2")) {
											String url = data.getJSONObject(1).getString("photoUrl");
											String link = data.getJSONObject(1).getString("link");
											editor.putString("2url", url);
											editor.putString("2link", link);
											editor.putString("2type", "2");
										}

										if (data.getJSONObject(2).getString("type").equals("1")) {
											String bg = data.getJSONObject(2).getString("bgColor");
											String content = data.getJSONObject(2).getString("content");
											String title = data.getJSONObject(2).getString("title");
											String date = data.getJSONObject(2).getString("createDate");

											editor.putString("3bg", bg);
											editor.putString("3content", content);
											editor.putString("3title", title);
											editor.putString("3date", date);
											editor.putString("3type", "1");
										} else if (data.getJSONObject(2).getString("type").equals("2")) {
											String url = data.getJSONObject(2).getString("photoUrl");
											String link = data.getJSONObject(2).getString("link");
											editor.putString("3url", url);
											editor.putString("3link", link);
											editor.putString("3type", "2");
										}
									}
									editor.commit();
								}
							} else if (response.getInt("code") == -2) {
								Toast.makeText(getActivity(), R.string.not_login, Toast.LENGTH_SHORT).show();
								final Intent intent = new Intent();
								intent.setClass(getActivity(), Login.class);
								startActivity(intent);
								getActivity().finish();
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
				});
		mQueue.add(mBannerRequest);

		try {
			unReadURL = new URL(HOST + "/user/prop/zone/getGridCount.do"
					+ "?sid=" + sid);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(Method.GET,
				unReadURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.e(TAG, response.toString());

						try {
							JSONArray data = response.getJSONArray("data");

							if (data.getInt(0) != 0) {
								unreadNotice = data.getInt(0);
								unreadNoticeLayout.setVisibility(View.VISIBLE);
								unreadNoticeCount.setText(String.valueOf(unreadNotice));
								if(unreadNotice > 9){
									unreadNoticeDot.setBackgroundResource(R.drawable.wuye_red_dot2);
								}else if(unreadNotice < 10){
									unreadNoticeDot.setBackgroundResource(R.drawable.wuye_red_dot1);
								}
							}

							if (data.getInt(1) != 0) {
								unreadQuery = data.getInt(1);
								unreadQueryLayout.setVisibility(View.VISIBLE);
								unreadQueryCount.setText(String.valueOf(unreadQuery));
								if(unreadQuery > 9){
									unreadQueryDot.setBackgroundResource(R.drawable.wuye_red_dot2);
								}else if(unreadQuery < 10){
									unreadQueryDot.setBackgroundResource(R.drawable.wuye_red_dot1);
								}
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, error.toString());
					}
				});
		mQueue.add(mJsonRequest);
	}

	public class MyClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.btn_lianxiwuye:
				intent.setClass(getActivity(), ContactWuyeActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_notice:

				unreadNoticeLayout.setVisibility(View.INVISIBLE);
				unreadNoticeCount.setText("");
				unreadNotice = 0;

				intent.setClass(getActivity(), NoticeActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_fix:
				intent.setClass(getActivity(), ReportToRepairActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_bad:
				intent.setClass(getActivity(), ComplainActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_good:
				intent.setClass(getActivity(), CommendActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_query:

				unreadQueryLayout.setVisibility(View.INVISIBLE);
				unreadQueryCount.setText("");
				unreadQuery = 0;

				intent.setClass(getActivity(), QueryActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_bill:
				intent.setClass(getActivity(), BillActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_pay:
				intent.setClass(getActivity(), PayActivity.class);
				startActivity(intent);
				break;
			}
		}

	}

	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void saveSid(String sid) {
		SharedPreferences savedSid = getActivity().getSharedPreferences(
				"SAVEDSID", 0);
		Editor editor = savedSid.edit();
		editor.putString("SID", sid);
		editor.commit();
	}

	public String loadSid() {
		SharedPreferences loadSid = getActivity().getSharedPreferences(
				"SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}

}
