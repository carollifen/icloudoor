package com.icloudoor.cloudoor;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2015/5/16.
 */
public class UpdateManager {
    private Context ctx;
    private UpdateCallback callback;
    private boolean canceled;
    private boolean hasNewVersion;
    private int curVersionCode;
    private int newVersionCode;
    private int progress;
    private String curVersion;
    private String newVersion = " Test V1.0 ";
    private String updateInfo;
    // the path where APK file saved
    public static final String UPDATE_DOWNURL =
            "http://pkg.fir.im/7a9f0291faea11e4923b3d19f379bfb04ea393fc.apk?attname=CloudDoor.apk&e=1431940480&token=KMHm2Srw8ucAeUwTrkfXSgx35GMiSYWo5N4QCy-B:i0xfxb2h7mMkf7GSdyBq-LdON04=";//"http://fir.im/ymdroid/CloudDoor.apk";
    // the path where the version file of APK saved
    public static final String UPDATE_CHECKURL = "http://fir.im/ymdroid/CloudDoor_version.txt";
    public static final String UPDATE_APKNAME = "CloudDoor.apk";
    public static final String UPDATE_SAVENAME = "CloudDoor.apk";

    private static final int UPDATE_CHECKCOMPLETED = 1;
    private static final int UPDATE_DOWNLOADING = 2;
    private static final int UPDATE_DOWNLOAD_ERROR = 3;
    private static final int UPDATE_DOWNLOAD_COMPLETED = 4;
    private static final int UPDATE_DOWNLOAD_CANCELED = 5;

    // where the local APK file saved
    private String savefolder = "/mnt/innerDisk/";//"/storage/sdcard0/";//

    public UpdateManager(Context context, UpdateCallback updateCallback) {
        ctx = context;
        callback = updateCallback;
        //savefolder = context.getFilesDir().toString();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            Object invoke = getVolumePathsMethod.invoke(storageManager, params);
            for (int i = 0; i < ((String[])invoke).length; i++) {
                System.out.println(((String[])invoke)[i]);
            }
            savefolder = ((String[])invoke)[0];
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        canceled = false;
        getCurVersion();
    }

    private void getCurVersion() {
        try {
            PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), 0);
            curVersion = pInfo.versionName;
            curVersionCode = pInfo.versionCode;
        } catch (NameNotFoundException e) {
            Log.e("update", e.getMessage());
            curVersion = "1.1.1000";
            curVersionCode = 111000;
        }

    }

    public void checkUpdate() {
        hasNewVersion = false;
//        new Thread(){
//            // ***************************************************************
//            /**
//             * @by wainiwann
//             *
//             */
//            @Override
//            public void run() {
//                Log.i("@@@@@", ">>>>>>>>>>>>>>>>>>>>>>>>>>>getServerVerCode() ");
//                try {
//                    String verjson = NetHelper.httpStringGet(UPDATE_CHECKURL);
//                    Log.i("@@@@", verjson
//                            + "**************************************************");
//                    JSONArray array = new JSONArray(verjson);
//
//                    if (array.length() > 0) {
//                        JSONObject obj = array.getJSONObject(0);
//                        try {
//                            newVersionCode = Integer.parseInt(obj.getString("verCode"));
//                            newVersion = obj.getString("verName");
//                            updateInfo = "";
//                            Log.i("newVerCode", newVersionCode
//                                    + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//                            Log.i("newVerName", newVersion
//                                    + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//                            if (newVersionCode > curVersionCode) {
//                                hasNewVersion = true;
//                            }
//                        } catch (Exception e) {
//                            newVersionCode = -1;
//                            newVersion = "";
//                            updateInfo = "";
//
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.e("update", e.getMessage());
//                }
//                updateHandler.sendEmptyMessage(UPDATE_CHECKCOMPLETED);
//            };
//            // ***************************************************************
//        }.start();
        hasNewVersion = true;
        updateHandler.sendEmptyMessage(UPDATE_CHECKCOMPLETED);
    }

    public void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // android.util.AndroidRuntimeException:
        // Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag.
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.setDataAndType(
                Uri.fromFile(new File(savefolder, UPDATE_SAVENAME)),
                "application/vnd.android.package-archive");
        ctx.startActivity(intent);
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public void downloadPackage()
    {


        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(UPDATE_DOWNURL);

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();


                    File ApkFile = new File(savefolder,UPDATE_SAVENAME);


                    if(ApkFile.exists())
                    {

                        ApkFile.delete();
                    }


                    FileOutputStream fos = new FileOutputStream(ApkFile);

                    int count = 0;
                    byte buf[] = new byte[512];

                    do{

                        int numread = is.read(buf);
                        count += numread;
                        progress =(int)(((float)count / length) * 100);

                        updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOADING));
                        if(numread <= 0){

                            updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_COMPLETED);
                            break;
                        }
                        fos.write(buf,0,numread);
                    }while(!canceled);
                    if(canceled)
                    {
                        updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_CANCELED);
                    }
                    fos.close();
                    is.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();

                    updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOAD_ERROR,e.getMessage()));
                } catch(IOException e){
                    e.printStackTrace();

                    updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOAD_ERROR,e.getMessage()));
                }

            }
        }.start();
    }

    Handler updateHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE_CHECKCOMPLETED:

                    callback.checkUpdateCompleted(hasNewVersion, newVersion);
                    break;
                case UPDATE_DOWNLOADING:

                    callback.downloadProgressChanged(progress);
                    break;
                case UPDATE_DOWNLOAD_ERROR:

                    callback.downloadCompleted(false, msg.obj.toString());
                    break;
                case UPDATE_DOWNLOAD_COMPLETED:

                    callback.downloadCompleted(true, "");
                    break;
                case UPDATE_DOWNLOAD_CANCELED:

                    callback.downloadCanceled();
                default:
                    break;
            }
        }
    };

    public interface UpdateCallback {
        public void checkUpdateCompleted(Boolean hasUpdate,
                                         CharSequence updateInfo);

        public void downloadProgressChanged(int progress);
        public void downloadCanceled();
        public void downloadCompleted(Boolean sucess, CharSequence errorMsg);
    }
}
