package com.icloudoor.clouddoor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class WuyeWidgeFragment2 extends Fragment {

	private String TAG = this.getClass().getSimpleName();

	private RelativeLayout bigLayout;
	private RelativeLayout contentLayout;
	private TextView TVtitle;
	private TextView TVcontent;
	private TextView TVnamedate;
	private ImageView bgImage;

	private Thread mThread;

	private String portraitUrl;

	private static final int MSG_SUCCESS = 0;// get the image success
	private static final int MSG_FAILURE = 1;// fail

	private String link;
	
	//
	private String PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/Cloudoor/CachePic";
	private String imageName = "myCachePic2.jpg";
	
	public WuyeWidgeFragment2() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_wuye_widge_fragment2,
				container, false);
		
		bgImage = (ImageView) view.findViewById(R.id.image_bg);
		bigLayout = (RelativeLayout) view.findViewById(R.id.big_layout);
		contentLayout = (RelativeLayout) view.findViewById(R.id.content_layout);
		TVtitle = (TextView) view.findViewById(R.id.title);
		TVcontent = (TextView) view.findViewById(R.id.content);
		TVnamedate = (TextView) view.findViewById(R.id.name_date);

		SharedPreferences banner = getActivity().getSharedPreferences("BANNER",
				0);
		if (banner.getString("2type", "0").equals("1")) {
			DisplayMetrics dm = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
			int screenWidth = dm.widthPixels;
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) contentLayout
					.getLayoutParams();
			params.width = screenWidth - 48 * 2;

			contentLayout.setLayoutParams(params);

			TVtitle.setText(banner.getString("2title", null));
			TVnamedate.setText(banner.getString("2date", null));

			if (banner.getString("2content", null) != null) {
				String formatContent = banner.getString("2content", null)
						.replace("\t", "\n    ");
				TVcontent.setText(formatContent);
			}
			
			String color = banner.getString("2bg", null);
			bigLayout.setBackgroundColor(Color.parseColor(color));
			
		} else if (banner.getString("2type", "0").equals("2")) {
			
			File f = new File(PATH + "/" + imageName);
			if (f.exists()) {
				Log.e(TAG, "use local");
				Bitmap bm = BitmapFactory.decodeFile(PATH + "/" + imageName);
				bgImage.setImageBitmap(bm);
			} else {
				if (banner.getString("2url", null) != null) {

					portraitUrl = banner.getString("2url", null);

					Log.e(TAG, portraitUrl);

					if (mThread == null) {
						mThread = new Thread(runnable);
						mThread.start();
					}
				}
			}

			if (banner.getString("2link", null) != null) {
				link = banner.getString("2link", null);
				bgImage.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Uri uri = Uri.parse(link);
						Intent it = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(it);
					}

				});
			}
		}
		
		return view;
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				bgImage.setImageBitmap((Bitmap) msg.obj);
				break;
			case MSG_FAILURE:
				break;
			}
		}
	};

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(portraitUrl);
			final Bitmap bitmap;
			try {
				org.apache.http.HttpResponse httpResponse = httpClient
						.execute(httpGet);

				bitmap = BitmapFactory.decodeStream(httpResponse.getEntity()
						.getContent());
			} catch (Exception e) {
				mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
				return;
			}
			
			File f = new File(PATH);
			if (!f.exists()) {
				f.mkdirs();
			}

			try {
				FileOutputStream out = new FileOutputStream(PATH + "/"
						+ imageName);
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
				out.flush();
				out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			mHandler.obtainMessage(MSG_SUCCESS, bitmap).sendToTarget();
		}
	};
	
	@Override
    public void onDetach() {
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        super.onDetach();

    }
}
