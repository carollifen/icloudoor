package com.icloudoor.clouddoor;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class WuYeDialog extends Dialog implements android.view.View.OnClickListener {
	
	
        public interface WuYeDialogCallBack{
                public void back();
        }
	
	 private String name;
     private WuYeDialogCallBack wuYeDialogCallBack;
 
     private TextView boundTV;
   private  Context context;
     public WuYeDialog (Context context,int theme,String name,WuYeDialogCallBack wuYeDialogCallBack) {
             super(context,theme);
             this.name = name;
             this.context=context;
             this.wuYeDialogCallBack  = wuYeDialogCallBack ;
            
     }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wuyedialog_layout);
		boundTV=(TextView) findViewById(R.id.id_queding);
		//boundTV.setTextColor(getColorStateList(R.color.test_input_color_selector));
		boundTV.setOnClickListener(this);
		
		
	}
	@Override
	public void onClick(View v) {
		wuYeDialogCallBack.back();
		WuYeDialog.this.dismiss();
		
	}
     
     

}
