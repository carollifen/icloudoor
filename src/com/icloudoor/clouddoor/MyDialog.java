package com.icloudoor.clouddoor;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MyDialog extends Dialog {
        public interface OnCustomDialogListener{
                public void back(int haveset);
        }
        
        private String name;
        private OnCustomDialogListener customDialogListener;
       private EditText pswEditText;
        private TextView cancle,bound;
      private  Context context;
        public MyDialog(Context context,int theme,String name,OnCustomDialogListener customDialogListener) {
                super(context,theme);
                this.name = name;
                this.context=context;
                this.customDialogListener = customDialogListener;
               
        }
        
        @Override
        protected void onCreate(Bundle savedInstanceState) { 
                super.onCreate(savedInstanceState);
                setContentView(R.layout.forget_gesture_dialog);
                pswEditText=(EditText) findViewById(R.id.pswEdit);
                 cancle=(TextView) findViewById(R.id.cancle);
                 bound=(TextView) findViewById(R.id.bound);
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
                	if(v.getId()==R.id.bound)
                	{	SharedPreferences loginStatus = context.getSharedPreferences("LOGINSTATUS",0);
                		String oldPsw=loginStatus.getString("PASSWARD", null);
                		if(pswEditText.getText().toString().equals(oldPsw))
                		{	Intent broadcastIntent=new Intent("KillConfirmActivity");
                			MyDialog.this.context.sendBroadcast(broadcastIntent);
                			
                			Intent verifybroadcastIntent=new Intent("KillVerifyActivity");
                		 	MyDialog.this.context.sendBroadcast(verifybroadcastIntent);
                		 	
                			customDialogListener.back(0);
                			MyDialog.this.dismiss();
                		}
                		else
                		{
                			Toast.makeText(context, R.string.wrong_pwd, Toast.LENGTH_SHORT).show();
                			pswEditText.setText("");
                		}
                	}
                	else if(v.getId()==R.id.cancle)
                	{
                		pswEditText.setText("");
                		MyDialog.this.dismiss();
                	}
                }
        };
}
