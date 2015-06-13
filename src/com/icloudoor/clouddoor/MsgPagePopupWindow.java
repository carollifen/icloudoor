package com.icloudoor.cloudoor;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;

public class MsgPagePopupWindow extends PopupWindow{
	
	private View mPopupView;
	
	public MsgPagePopupWindow(final Activity context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPopupView = inflater.inflate(R.layout.msg_popup_menu, null);
		
		this.setContentView(mPopupView);
		this.setFocusable(true);
	}
}