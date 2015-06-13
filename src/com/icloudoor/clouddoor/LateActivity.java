package com.icloudoor.cloudoor;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class LateActivity extends Activity {
	
	private RelativeLayout shareLayout;
	private RelativeLayout dismiss;
	
	String appID = "wxcddf37d2f770581b";
	String appSecret = "01d7ab875773e1282059d5b47b792e2b";

	UMWXHandler wxHandler;
	UMWXHandler wxCircleHandler;
	UMSocialService mController;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_late);
		
		dismiss = (RelativeLayout) findViewById(R.id.dismiss);
		shareLayout = (RelativeLayout) findViewById(R.id.share_layout);
		
		dismiss.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				setResult(0);
				finish();
			}
			
		});

		// ����΢��ƽ̨
		wxHandler = new UMWXHandler(LateActivity.this, appID, appSecret);
		wxHandler.addToSocialSDK();
		// ����΢������Ȧ
		wxCircleHandler = new UMWXHandler(LateActivity.this, appID, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();

		mController = UMServiceFactory.getUMSocialService("com.umeng.share");
		mController.setShareMedia(new UMImage(LateActivity.this, BitmapFactory.decodeStream(getResources().openRawResource(
								R.raw.late_share_pic))));
		mController.getConfig().removePlatform(SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT);			
		
		shareLayout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				mController.openShare(LateActivity.this, false);
			}
			
		});
	}

	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    /**ʹ��SSO��Ȩ�����������´��� */
	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}
}