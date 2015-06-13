package com.icloudoor.cloudoor;

import com.icloudoor.cloudoor.TakePicCamInterface.CamOpenOverCallback;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class TakePictureActivity extends Activity implements CamOpenOverCallback {

	TakePicCamSurfaceView surfaceView = null;
	TakePicMaskView maskView = null;
	float previewRate = -1f;
//	int DST_CENTER_RECT_WIDTH = 125; //dip
//	int DST_CENTER_RECT_HEIGHT = 125;//dip
	Point rectPictureSize = null;
	
	private RelativeLayout btnCancel, btnConfirm;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread openThread = new Thread(){
			@Override
			public void run() {
				TakePicCamInterface.getInstance().doOpenCamera(TakePictureActivity.this);
			}
		};
		openThread.start();

		setContentView(R.layout.take_picture);
		initUI();
		initViewParams();
		
		btnCancel.setOnClickListener(new BtnListeners());
		btnConfirm.setOnClickListener(new BtnListeners());
	}

	private void initUI(){
		surfaceView = (TakePicCamSurfaceView) findViewById(R.id.camera_surfaceview);
		maskView = (TakePicMaskView) findViewById(R.id.view_mask);
		btnCancel = (RelativeLayout) findViewById(R.id.take_pic_cancel);
		btnConfirm = (RelativeLayout) findViewById(R.id.take_pic_confirm);
	}
	private void initViewParams(){
		LayoutParams params = surfaceView.getLayoutParams();
		Point p = TakePicDisplayUtil.getScreenMetrics(this);
		params.width = p.x;
		params.height = p.y;
		previewRate = TakePicDisplayUtil.getScreenRate(this); 
		surfaceView.setLayoutParams(params);

	}

	@Override
	public void cameraHasOpened() {
		SurfaceHolder holder = surfaceView.getSurfaceHolder();
		TakePicCamInterface.getInstance().doStartPreview(holder, previewRate);
		if(maskView != null){
//			Rect screenCenterRect = createCenterScreenRect(TakePicDisplayUtil.dip2px(this, DST_CENTER_RECT_WIDTH)
//					,TakePicDisplayUtil.dip2px(this, DST_CENTER_RECT_HEIGHT));
			Rect screenCenterRect = createCenterScreenRect(400, 400);
			maskView.setCenterRect(screenCenterRect);
		}
	}
	private class BtnListeners implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.take_pic_confirm:
//				if(rectPictureSize == null){
////					rectPictureSize = createCenterPictureRect(TakePicDisplayUtil.dip2px(TakePictureActivity.this, DST_CENTER_RECT_WIDTH)
////							,TakePicDisplayUtil.dip2px(TakePictureActivity.this, DST_CENTER_RECT_HEIGHT));
//					rectPictureSize = createCenterPictureRect(400, 400);
//				}
				TakePicCamInterface.getInstance().doTakePicture(400, 400);
				
				SharedPreferences takePic = getSharedPreferences("TAKPIC", 0);
				Editor editor = takePic.edit();
				editor.putInt("TAKEN", 1);
				editor.commit();

				setResult(RESULT_OK);
				TakePictureActivity.this.finish();
				break;
			case R.id.take_pic_cancel:
				TakePictureActivity.this.finish();
				break;
			default:
				break;
			}
		}

	}
//	
//	/**
//	 * @param w px
//	 * @param h px
//	 * @return
//	 */
//	private Point createCenterPictureRect(int w, int h){
//		
//		int wScreen = TakePicDisplayUtil.getScreenMetrics(this).x;
//		int hScreen = TakePicDisplayUtil.getScreenMetrics(this).y;
//		int wSavePicture = TakePicCamInterface.getInstance().doGetPrictureSize().y; 
//		int hSavePicture = TakePicCamInterface.getInstance().doGetPrictureSize().x;
//		float wRate = (float)(wSavePicture) / (float)(wScreen);
//		float hRate = (float)(hSavePicture) / (float)(hScreen);
//		float rate = (wRate <= hRate) ? wRate : hRate;
//		
//		int wRectPicture = (int)( w * wRate);
//		int hRectPicture = (int)( h * hRate);
//		return new Point(wRectPicture, hRectPicture);
//		
//	}
	/**
	 * 
	 * @param w px
	 * @param h	px
	 * @return
	 */
	private Rect createCenterScreenRect(int w, int h){
		int x1 = TakePicDisplayUtil.getScreenMetrics(this).x / 2 - w / 2;
		int y1 = TakePicDisplayUtil.getScreenMetrics(this).y / 2 - h / 2;
		int x2 = x1 + w;
		int y2 = y1 + h;
		return new Rect(x1, y1, x2, y2);
	}

}
