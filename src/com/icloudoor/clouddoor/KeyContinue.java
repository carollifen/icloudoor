package com.icloudoor.clouddoor;

import java.util.ArrayList;
import java.util.List;

import com.icloudoor.clouddoor.MyDatePickerUtilView.onSelectListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class KeyContinue extends Activity {

	MyDatePickerUtilView mDatePicker;

	private TextView TVPickYear;
	private TextView TVPickMonth;
	private TextView TVPickDay;
	private TextView TVPickerScope;

	private List<String> pickerDataYear;
	private List<String> pickerDataMonth;
	private List<String> pickerDataDay;

	private PickerClickListener mClickListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.key_continue);

		mDatePicker = (MyDatePickerUtilView) findViewById(R.id.numberpicker);

		pickerDataYear = new ArrayList<String>();
		pickerDataMonth = new ArrayList<String>();
		pickerDataDay = new ArrayList<String>();

		mClickListener = new PickerClickListener();

		TVPickYear = (TextView) findViewById(R.id.pick_year);
		TVPickMonth = (TextView) findViewById(R.id.pick_month);
		TVPickDay = (TextView) findViewById(R.id.pick_day);
		TVPickerScope = (TextView) findViewById(R.id.picker_scope);

		TVPickYear.setOnClickListener(mClickListener);
		TVPickMonth.setOnClickListener(mClickListener);
		TVPickDay.setOnClickListener(mClickListener);

		initPicker();
	}

	public class PickerClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.pick_year:
				TVPickerScope.setText(R.string.year);
				for (int i = 2015; i < 2030; i++) {
					pickerDataYear.add(String.valueOf(i));
				}
				mDatePicker.setData(pickerDataYear);
				mDatePicker.setOnSelectListener(new onSelectListener() {

					@Override
					public void onSelect(String text) {
						TVPickYear.setText(text);

					}

				});
				break;
			case R.id.pick_month:
				TVPickerScope.setText(R.string.month);
				for (int i = 1; i <= 12; i++) {
					pickerDataMonth.add(String.valueOf(i));
				}
				mDatePicker.setData(pickerDataMonth);
				mDatePicker.setOnSelectListener(new onSelectListener() {

					@Override
					public void onSelect(String text) {
						TVPickMonth.setText(text);

					}

				});
				break;
			case R.id.pick_day:
				TVPickerScope.setText(R.string.day);
				for (int i = 1; i <= 31; i++) {
					pickerDataDay.add(String.valueOf(i));
				}
				mDatePicker.setData(pickerDataDay);
				mDatePicker.setOnSelectListener(new onSelectListener() {

					@Override
					public void onSelect(String text) {
						TVPickDay.setText(text);

					}

				});
				break;
			}
		}

	}

	public void initPicker() {
		for (int i = 2015; i < 2030; i++) {
			pickerDataYear.add(String.valueOf(i));
		}
		mDatePicker.setData(pickerDataYear);
	}
}