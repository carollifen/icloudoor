package com.icloudoor.clouddoor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class WizardFragmentFour extends Fragment {
	private TextView welcome;

	public WizardFragmentFour() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_wizard_fragment_four,
				container, false);
		
		welcome = (TextView) view.findViewById(R.id.btn_welcome);
		welcome.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), Login.class);
				startActivity(intent);
				
				WizardActivity WizardActivity = (WizardActivity) getActivity();
				WizardActivity.finish();
			}
			
		});
		
		return view;
	}

}
