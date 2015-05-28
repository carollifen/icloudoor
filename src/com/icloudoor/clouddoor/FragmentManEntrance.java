package com.icloudoor.clouddoor;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FragmentManEntrance extends Fragment implements OnClickListener{
	private RelativeLayout call_contact;
	private static final int REQUEST_CONTACT = 1;
	
	private SharedPreferences dateAndPhoneShare;
	private Editor dateAndPhoneEditor;
	
	private SharedPreferences zoneIdShare;
	private String phonenum;
	final Calendar c = Calendar.getInstance();
	int mYear = c.get(Calendar.YEAR);
	int mMonth = c.get(Calendar.MONTH);
	int mDay = c.get(Calendar.DAY_OF_MONTH);
	private RelativeLayout call_datepicker;
	private EditText phoneEdit;
	
	private TextView date_show;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.fragment_man_entrance, container, false);
		
		dateAndPhoneShare=getActivity().getSharedPreferences("DATEANDPHONESHARE", 0);
		dateAndPhoneEditor=dateAndPhoneShare.edit();
		zoneIdShare=getActivity().getSharedPreferences("ZONESHARE", 0);
		call_contact=(RelativeLayout) view.findViewById(R.id.id_call_contacts);
		call_contact.setOnClickListener(this);
		phoneEdit=(EditText) view.findViewById(R.id.id_manentrance_phonenum);
		call_datepicker=(RelativeLayout) view.findViewById(R.id.icon_dateshow);
		call_datepicker.setOnClickListener(this);
		date_show=(TextView) view.findViewById(R.id.date_show_textview);
		String str=mYear+"/"+(mMonth+1)+"/"+mDay;
		date_show.setText(str);
		phoneEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				phoneEdit.setTextColor(0xff333333);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				dateAndPhoneEditor.putString("PHONENUM",phoneEdit.getText().toString()).commit();
				phoneEdit.setTextColor(0xff333333);
			}
		});
		return view;
	}
	
	
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.id_call_contacts)
		{
			Intent intent = new Intent();

			intent.setAction(Intent.ACTION_PICK);

			intent.setData(ContactsContract.Contacts.CONTENT_URI);

			getParentFragment().startActivityForResult(intent, REQUEST_CONTACT);
		}
		if(v.getId()==R.id.icon_dateshow)
		{
		
		  DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {  
		        public void onDateSet(DatePicker view, int year, int monthOfYear,  
		                int dayOfMonth) {  
		            Log.d("test", ""+year+"Äê"+(monthOfYear+1)+"ÔÂ"+dayOfMonth+"ÈÕ"); 
		            String s=year+"/"+(monthOfYear+1)+"/"+dayOfMonth;
		            dateAndPhoneEditor.putString("DATE", year+"-"+(monthOfYear+1)+"-"+dayOfMonth).commit();
		            date_show.setText(s);
		            date_show.setTextColor(0xff333333);
		        }  
		    };  
		    new DatePickerDialog(getActivity(),onDateSetListener, mYear	, mMonth	, mDay).show();
			
		}
		
	}
	
	public void getData(String phonenum)
	{
	Log.e("phone", phonenum);
	StringBuilder ss=new StringBuilder(phonenum);
	phonenum.replace(" ", "");
	phoneEdit.setText(phonenum.replace(" ", ""));
	
	//dateAndPhoneEditor.putString("PHONENUM",phoneEdit.getText().toString()).commit();
	
	phoneEdit.setTextColor(0xff333333);
}


}
