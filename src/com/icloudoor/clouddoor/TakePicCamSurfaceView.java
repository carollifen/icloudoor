package com.icloudoor.cloudoor;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TakePicCamSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	TakePicCamInterface mCameraInterface;
	Context mContext;
	SurfaceHolder mSurfaceHolder;
	@SuppressWarnings("deprecation")
	public TakePicCamSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;
		mSurfaceHolder = getHolder();
		mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		TakePicCamInterface.getInstance().doStopCamera();
	}
	public SurfaceHolder getSurfaceHolder(){
		return mSurfaceHolder;
	}
	
}
