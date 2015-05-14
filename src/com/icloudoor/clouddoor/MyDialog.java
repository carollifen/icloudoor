package com.icloudoor.clouddoor;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MyDialog extends Dialog {
        //定义回调事件，用于dialog的点击事件
        public interface OnCustomDialogListener{
                public void back(int haveset);
        }
        
        private String name;
        private OnCustomDialogListener customDialogListener;
       private EditText pswEditText;
        private Button cancle,bound;
      private  Context context;
        public MyDialog(Context context,String name,OnCustomDialogListener customDialogListener) {
                super(context);
                this.name = name;
                this.context=context;
                this.customDialogListener = customDialogListener;
               
        }
        
        @Override
        protected void onCreate(Bundle savedInstanceState) { 
                super.onCreate(savedInstanceState);
                setContentView(R.layout.forget_gesture_dialog);
                pswEditText=(EditText) findViewById(R.id.pswEdit);
                 cancle=(Button) findViewById(R.id.cancle);
                 bound=(Button) findViewById(R.id.bound);
                 bound.setOnClickListener(clickListener);
                 cancle.setOnClickListener(clickListener);
    			
                //设置标题
                setTitle(name); 
             
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
                			customDialogListener.back(0);
                			MyDialog.this.dismiss();
                		}
                		else
                		{
                			Toast.makeText(context, "密码输入有误", Toast.LENGTH_SHORT).show();
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
