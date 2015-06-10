package com.icloudoor.clouddoor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class TakePicFileUtil {

	private static String TAG = "TakePicFileUtil";
	
	private static final File parentPath = Environment.getExternalStorageDirectory();
	private static String storagePath = "";
	private static final String FIRSTLAYER_FOLDER_NAME = "Cloudoor";
	private static final String DST_FOLDER_NAME = "CacheImage";
	private static String picName;
	private static TakePicFileUtil mFileUtil = null;

	public static TakePicFileUtil getInstance(){
		if(mFileUtil  == null){
			mFileUtil  = new TakePicFileUtil();
			return mFileUtil;
		}
		else{
			return mFileUtil;
		}
	}	
	
	private static String initPath(){
		
		File f = null;
		if(storagePath.equals("")){	
			storagePath = parentPath.getAbsolutePath() + "/" + FIRSTLAYER_FOLDER_NAME + "/" + DST_FOLDER_NAME;
			f = new File(storagePath);
			if(!f.exists()){
				f.mkdirs();
			}
		}
		return storagePath;
	}

	public static void saveBitmap(Bitmap b){

		String path = initPath();
		
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd-hhmmss");
		String dataTake = sDateFormat.format(System.currentTimeMillis());
		
		String jpegName = path + "/" + "myImage.jpg";

		try {
			Log.e(TAG, "save pic");
			
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}

		
		Log.e("camera", "pic saved jpegName: " + jpegName);
	}

}