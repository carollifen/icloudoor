package com.icloudoor.clouddoor;

import com.icloudoor.clouddoor.ForGetDialog .ForGetDialogInterface;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ForGetDialog extends Dialog {
	 public interface ForGetDialogInterface{
         public void back(int haveset);
 }
 
 private String name;
 private ForGetDialogInterface customDialogListener;
private EditText pswEditText;
 private TextView cancle,bound;
private  Context context;
 public ForGetDialog (Context context,int theme,String name,ForGetDialogInterface customDialogListener) {
         super(context,theme);
         this.name = name;
         this.context=context;
         this.customDialogListener = customDialogListener;
        
 }
 
 @Override
 protected void onCreate(Bundle savedInstanceState) { 
         super.onCreate(savedInstanceState);
         setContentView(R.layout.forgetdialog);
         pswEditText=(EditText) findViewById(R.id.id_pswEdit);
          cancle=(TextView) findViewById(R.id.id_cancle);
          bound=(TextView) findViewById(R.id.id_bound);
          bound.setOnClickListener(clickListener);
          cancle.setOnClickListener(clickListener);
          bound.setTextColor(0x7FFFFFFF);
          bound.setEnabled(false);
          cancle.setTextColor(0xff000000);
          pswEditText.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					if(pswEditText.getText().toString().length() > 5){
						bound.setTextColor(0xFFFFFFFF);
						bound.setEnabled(true);
					} else {
						bound.setTextColor(0x7fffffff);
						bound.setEnabled(false);
					}
				}
			});
        // setTitle(name); 
      
              }
 
 private View.OnClickListener clickListener = new View.OnClickListener() {
         
         @Override
         public void onClick(View v) {
         	if(v.getId()==R.id.id_bound)
         	{	SharedPreferences loginStatus = context.getSharedPreferences("LOGINSTATUS",0);
         		String oldPsw=loginStatus.getString("PASSWARD", null);
         		if(pswEditText.getText().toString().equals(oldPsw))
         		{	Intent broadcastIntent=new Intent("KillConfirmActivity");
         			ForGetDialog .this.context.sendBroadcast(broadcastIntent);
         			
         			Intent verifybroadcastIntent=new Intent("KillVerifyActivity");
         		 	ForGetDialog .this.context.sendBroadcast(verifybroadcastIntent);
         		 	
         			customDialogListener.back(0);
         			ForGetDialog .this.dismiss();
         		}
         		else
         		{
         			Toast.makeText(context, R.string.wrong_pwd, Toast.LENGTH_SHORT).show();
         			pswEditText.setText("");
         		}
         	}
         	else if(v.getId()==R.id.id_cancle)
         	{
         		pswEditText.setText("");
         		ForGetDialog .this.dismiss();
         	}
         }
 };
}
