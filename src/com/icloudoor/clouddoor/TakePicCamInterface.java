package com.icloudoor.clouddoor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;

public class TakePicCamInterface {

	private Camera mCamera;
	private Camera.Parameters mParams;
	private boolean isPreviewing = false;
	private float mPreviwRate = -1f;
	private static TakePicCamInterface mCameraInterface;

	public interface CamOpenOverCallback{
		public void cameraHasOpened();
	}

	private TakePicCamInterface(){

	}
	public static synchronized TakePicCamInterface getInstance(){
		if(mCameraInterface == null){
			mCameraInterface = new TakePicCamInterface();
		}
		return mCameraInterface;
	}

	public void doOpenCamera(CamOpenOverCallback callback){

		mCamera = Camera.open();
		callback.cameraHasOpened();
	}

	public void doStartPreview(SurfaceHolder holder, float previewRate){

		if(isPreviewing){
			mCamera.stopPreview();
			return;
		}
		if(mCamera != null){
			try {
				mCamera.setPreviewDisplay(holder);
			} catch (IOException e) {
				e.printStackTrace();
			}
			initCamera(previewRate);
		}


			}

	public void doStartPreview(SurfaceTexture surface, float previewRate){

		if(isPreviewing){
			mCamera.stopPreview();
			return;
		}
		if(mCamera != null){
			try {
				mCamera.setPreviewTexture(surface);
			} catch (IOException e) {
				e.printStackTrace();
			}
			initCamera(previewRate);
		}

	}

	public void doStopCamera(){
		if(null != mCamera)
		{
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview(); 
			isPreviewing = false; 
			mPreviwRate = -1f;
			mCamera.release();
			mCamera = null;     
		}
	}

	public void doTakePicture(){
		if(isPreviewing && (mCamera != null)){
			mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
		}
	}
	int DST_RECT_WIDTH, DST_RECT_HEIGHT;
	public void doTakePicture(int w, int h){
		if(isPreviewing && (mCamera != null)){

			DST_RECT_WIDTH = w;
			DST_RECT_HEIGHT = h;
			mCamera.takePicture(mShutterCallback, null, mRectJpegPictureCallback);
		}
	}
	public Point doGetPrictureSize(){
		Size s = mCamera.getParameters().getPictureSize();
		return new Point(s.width, s.height);
	}

	@SuppressWarnings("deprecation")
	private void initCamera(float previewRate){
		if(mCamera != null){

			mParams = mCamera.getParameters();
			mParams.setPictureFormat(ImageFormat.JPEG);

			Size pictureSize = TakePicCamParaUtil.getInstance().getPropPictureSize(
					mParams.getSupportedPictureSizes(),previewRate, 800);
			mParams.setPictureSize(pictureSize.width, pictureSize.height);
			Size previewSize = TakePicCamParaUtil.getInstance().getPropPreviewSize(
					mParams.getSupportedPreviewSizes(), previewRate, 800);
			mParams.setPreviewSize(previewSize.width, previewSize.height);

			mCamera.setDisplayOrientation(90);

			List<String> focusModes = mParams.getSupportedFocusModes();
			if(focusModes.contains("continuous-video")){
				mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			}
			mCamera.setParameters(mParams);	
			mCamera.startPreview();



			isPreviewing = true;
			mPreviwRate = previewRate;

			mParams = mCamera.getParameters(); 

		}
	}


	ShutterCallback mShutterCallback = new ShutterCallback() 
	{
		public void onShutter() {

		}
	};
	PictureCallback mRawCallback = new PictureCallback() 
	{

		public void onPictureTaken(byte[] data, Camera camera) {

		}
	};
	
	/**
	 * normal
	 */
	PictureCallback mJpegPictureCallback = new PictureCallback() 
	{
		public void onPictureTaken(byte[] data, Camera camera) {
			Bitmap b = null;
			if(null != data){
				b = BitmapFactory.decodeByteArray(data, 0, data.length);
				mCamera.stopPreview();
				isPreviewing = false;
			}
			
			if(null != b)
			{
				
				Bitmap rotaBitmap = TakePicImageUtil.getRotateBitmap(b, 90.0f);
				TakePicFileUtil.saveBitmap(rotaBitmap);
			}
			
			mCamera.startPreview();
			isPreviewing = true;
		}
	};

	/**
	 * Rect
	 */
	PictureCallback mRectJpegPictureCallback = new PictureCallback() 

	{
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub

			Bitmap b = null;
			if(null != data){
				b = BitmapFactory.decodeByteArray(data, 0, data.length);
				mCamera.stopPreview();
				isPreviewing = false;
			}
			
			if(null != b)
			{

				Bitmap rotaBitmap = TakePicImageUtil.getRotateBitmap(b, 90.0f);
				int x = rotaBitmap.getWidth()/2 - DST_RECT_WIDTH/2;
				int y = rotaBitmap.getHeight()/2 - DST_RECT_HEIGHT/2;

				Bitmap rectBitmap = Bitmap.createBitmap(rotaBitmap, x, y, DST_RECT_WIDTH, DST_RECT_HEIGHT);
			
				TakePicFileUtil.saveBitmap(rectBitmap);
		
				if(rotaBitmap.isRecycled()){
					rotaBitmap.recycle();
					rotaBitmap = null;
				}
				if(rectBitmap.isRecycled()){
					rectBitmap.recycle();
					rectBitmap = null;
				}
			}

			mCamera.startPreview();
			isPreviewing = true;
			if(!b.isRecycled()){
				b.recycle();
				b = null;
			}

		}
	};


}