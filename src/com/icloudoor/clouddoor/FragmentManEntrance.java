package com.icloudoor.clouddoor;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
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
		
		call_contact=(RelativeLayout) view.findViewById(R.id.id_call_contacts);
		call_contact.setOnClickListener(this);
		phoneEdit=(EditText) view.findViewById(R.id.id_manentrance_phonenum);
		call_datepicker=(RelativeLayout) view.findViewById(R.id.icon_dateshow);
		call_datepicker.setOnClickListener(this);
		date_show=(TextView) view.findViewById(R.id.date_show_textview);
		String str=mYear+"/"+(mMonth+1)+"/"+mDay;
		date_show.setText(str);
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
	phoneEdit.setText(phonenum);
	phoneEdit.setTextColor(0xff333333);
}


}
