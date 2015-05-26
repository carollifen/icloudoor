package com.icloudoor.clouddoor;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;




import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
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

/**
 * A simple {@link Fragment} subclass.
 *
 */
@SuppressLint("ResourceAsColor")
public class KeyListAuthFragment extends Fragment{
	
	public ImageView IVselectkeyItem;
	public	TextView TVListItem;
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
	
	public KeyListAuthFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_key_list_auth, container, false);
		mframlayout=(FrameLayout) view.findViewById(R.id.id_entrancecontent);
		
		mcarFragment=new FragmentCarEntrance();
		mManFragment=new FragmentManEntrance();
		mfragmentManager=getChildFragmentManager();
		mfragmentTrasaction=mfragmentManager.beginTransaction();
		
	
		mfragmentTrasaction.replace(R.id.id_entrancecontent, mManFragment);
		mfragmentTrasaction.commit();
		TVkeyname=(TextView) view.findViewById(R.id.keyname_in_key_auth);
		LVkeylist=(ListView)view.findViewById(R.id.doorname_listview);
		
		ArrayList<Map<String, String>> mlist=new ArrayList<Map<String,String>>();
		for(int i=3;i>=0;i--)
		{
			Map<String, String> map=new HashMap<String, String>();
			map.put("keyname"," Ωı–Âœ„Ω≠¥Û√≈"+i);
			mlist.add(map);
		}
		LVkeylist.setVisibility(View.GONE);
		
		
		//LVkeylist.setAdapter(new MykeyListAdapter(getActivity(), mlist) );
		LVkeylist.setAdapter(new SimpleAdapter(getActivity(),mlist, 
				R.layout.keylist_child, new String[]{"keyname"}, new int[]{R.id.id_keyname}));
		
		
		
		LVkeylist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//Log.e("textview", arg1);
				TVListItem=(TextView) view.findViewById(R.id.id_keyname);
				IVselectkeyItem=(ImageView) view.findViewById(R.id.select_keyicon);
				
				//IVselectkeyItem.setImageResource(R.drawable.key_selected);
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				TVkeyname.setText(TVListItem.getText().toString());
				LVkeylist.setVisibility(View.GONE);
				//IVselectkeyItem.setImageResource(R.drawable.key_notselected);
				if(!isShowingKeyList){
					btnShowHideKeyList.setImageResource(R.drawable.common_hide_list);
					isShowingKeyList = true;
					//LVkeylist.setVisibility(View.VISIBLE);
				}else{
					btnShowHideKeyList.setImageResource(R.drawable.common_show_list);
					isShowingKeyList = false;
					
					//LVkeylist.setVisibility(View.GONE);
				}
	
			}
			
		});
		
		
		
		// to hide the tip after 3 secs
		showTip = (RelativeLayout) view.findViewById(R.id.show_tip);
		
		Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.show_tip_animation);
		DecelerateInterpolator interpolator = new DecelerateInterpolator();
		animation.setInterpolator(interpolator);
		animation.setFillAfter(true);
		showTip.startAnimation(animation);
		
		// show or hide key list
		isShowingKeyList = false;
		showHideKeyList = (RelativeLayout) view.findViewById(R.id.show_hide_key_list);
		btnShowHideKeyList = (ImageView) view.findViewById(R.id.btn_show_hide_key_list);
		btnShowHideKeyList.setImageResource(R.drawable.common_show_list);
		
		showHideKeyList.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(!isShowingKeyList){
					btnShowHideKeyList.setImageResource(R.drawable.common_hide_list);
					isShowingKeyList = true;
					LVkeylist.setVisibility(View.VISIBLE);
				}else{
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
		
		chooseCarKey.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(!isChooseCarKey){
					mfragmentManager=getChildFragmentManager();
					mfragmentTrasaction=mfragmentManager.beginTransaction();
					mfragmentTrasaction.replace(R.id.id_entrancecontent, mcarFragment);
					mfragmentTrasaction.commit();
					chooseCarKey.setBackgroundResource(R.drawable.channel_select);
					chooseManKey.setBackgroundResource(R.drawable.channel_normal);
					carKeyText.setTextColor(0xFFffffff);
					manKeyText.setTextColor(0xFF333333);
					isChooseCarKey = true;
				}
			}
			
		});
		
		chooseManKey.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(isChooseCarKey){
					mfragmentManager=getChildFragmentManager();
					mfragmentTrasaction=mfragmentManager.beginTransaction();
					mfragmentTrasaction.replace(R.id.id_entrancecontent, mManFragment);
					mfragmentTrasaction.commit();
					chooseCarKey.setBackgroundResource(R.drawable.channel_normal);
					chooseManKey.setBackgroundResource(R.drawable.channel_select);
					carKeyText.setTextColor(0xFF333333);
					manKeyText.setTextColor(0xFFffffff);
					isChooseCarKey = false;
				}
			}
			
		});
		//

		// submit 
		btnSubmitText = (TextView) view.findViewById(R.id.btn_submit_text);
		btnSubmitText.setTextColor(ColorDisable);
		btnSubmitText.setBackgroundResource(R.drawable.btn_submit_big_disable);
		
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		
		btnSubmitText.setWidth(screenWidth - 32 * 2);	
		btnSubmitText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
			
		});
		//
		
		return view;
	}

//	private class MykeyListAdapter extends BaseAdapter
//	{	private LayoutInflater inflater;
//		private Context mcontext;
//		private ArrayList<Map<String, String>> mlist;
//		 public MykeyListAdapter(Context mcontext,ArrayList<Map<String, String>> mlist) 
//		{	this.inflater=LayoutInflater.from(mcontext);
//			this.mcontext=mcontext;
//			this.mlist=mlist;
//			}
//		@Override
//		public int getCount() {
//			// TODO Auto-generated method stub
//			return mlist.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			// TODO Auto-generated method stub
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			// TODO Auto-generated method stub
//			
//			convertView=inflater.inflate(R.layout.keylist_child, null);
//			 TVListItem=(TextView) convertView.findViewById(R.id.id_keyname);
//			 TVListItem.setText(mlist.get(position).get("keyname"));
//			 IVselectkeyItem=(ImageView) convertView.findViewById(R.id.select_keyicon);
//			 
//			 LVkeylist.setOnItemClickListener(new OnItemClickListener() {
//
//				@Override
//				public void onItemClick(AdapterView<?> parent, View view,
//						int position, long id) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
//			 
//			IVselectkeyItem.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					IVselectkeyItem.setImageResource(R.drawable.key_selected);
//					TVkeyname.setText("00");
//				}
//			});
//			 
//			
//			return convertView;
//		}
//		
//	}
	
	
//	private class KeyListAdapter extends BaseAdapter {
//		private LayoutInflater mInflater;
//		private ArrayList<HashMap<String, String>> doorNameList;
//
//		// public void setDataList(ArrayList<HashMap<String, String>> list) {
//		// doorNameList = list;
//		// notifyDataSetChanged();
//		// }
//
//		public KeyListAdapter(Context context, ArrayList<HashMap<String, String>> list) {
//			this.doorNameList = list;
//			this.mInflater = LayoutInflater.from(context);
//		}
//
//		@Override
//		public int getCount() {
//			return doorNameList.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return doorNameList.get(position);
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			ViewHolder holder;
//			if (convertView == null) {
//				convertView = mInflater.inflate(R.layout.keylist_child, null);
//				holder = new ViewHolder();
//				holder.doorname = (TextView) convertView.findViewById(R.id.door_name);
//				holder.beginday = (TextView) convertView.findViewById(R.id.door_time_from);
//				holder.endday = (TextView) convertView.findViewById(R.id.door_time_to);
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//			}
//
//			//holder.doorname.setSelected(true);
//			holder.TVListItem.setText(doorNameList.get(position).get("Door"));
//			holder.beginday.setText(doorNameList.get(position).get("BEGIN"));
//			holder.endday.setText(doorNameList.get(position).get("END"));
//
//			return convertView;
//		}
//
//		class ViewHolder {
//			public TextView TVListItem;
//			public ImageView IVselectkeyItem;
//			
//		}
//
//	}
//	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1&&resultCode==Activity.RESULT_OK);
		{
			 if (resultCode == Activity.RESULT_OK) {
		            ContentResolver reContentResolverol = getActivity().getContentResolver();
		             Uri contactData = data.getData();
		             @SuppressWarnings("deprecation")
		            Cursor cursor = reContentResolverol.query(contactData, null, null, null, null);
		             cursor.moveToFirst();
		             String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		            Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
		                     null, 
		                     ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, 
		                     null, 
		                     null);
		             while (phone.moveToNext()) {
		                 String usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		                // text.setText(usernumber+" ("+username+")");
		                // FragmentManEntrance f=new FragmentManEntrance();
		                 mManFragment.getData(usernumber);
		             }
		             
		         }
		    }
			Log.e("sd", "dsjdkfkl");
		
	}

}
