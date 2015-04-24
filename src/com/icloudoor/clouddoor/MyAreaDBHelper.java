package com.icloudoor.clouddoor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyAreaDBHelper extends SQLiteOpenHelper {

	private Context mContext;
	

	public MyAreaDBHelper(Context context, String databaseName,
			CursorFactory factory, int version) {
		super(context, databaseName, factory, version);
		mContext = context;
	}

	/**
	 * 数据库第一次创建时调用
	 * */
	@Override
	public void onCreate(SQLiteDatabase db) {
		executeAssetsSQL(db, "area.sql");
	}

	/**
	 * 数据库升级时调用
	 * */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//数据库不升级
		if (newVersion <= oldVersion) {
			return;
		}
		AreaDBConfiguration.oldVersion = oldVersion;

		int changeCnt = newVersion - oldVersion;
		for (int i = 0; i < changeCnt; i++) {
			// 依次执行updatei_i+1文件      由1更新到2 [1-2]，2更新到3 [2-3]
			String schemaName = "update" + (oldVersion + i) + "_"
					+ (oldVersion + i + 1) + ".sql";
			executeAssetsSQL(db, schemaName);
		}
	}

	/**
	 * 读取数据库文件（.sql），并执行sql语句
	 * */
	private void executeAssetsSQL(SQLiteDatabase db, String schemaName) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(mContext.getAssets()
					.open(AreaDBConfiguration.DB_PATH + "/" + schemaName)));

			String line;
			String buffer = "";
			while ((line = in.readLine()) != null) {
				buffer += line;
				if (line.trim().endsWith(";")) {
					db.execSQL(buffer.replace(";", ""));
					buffer = "";
				}
			}
		} catch (IOException e) {
			Log.e("db-error", e.toString());
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				Log.e("db-error", e.toString());
			}
		}
	}
	
	public boolean tabIsExist(String tabName) {
		boolean result = false;
		if (tabName == null) {
			return false;
		}
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.getWritableDatabase();// 此this是继承SQLiteOpenHelper类得到的
			String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+tabName.trim()+"' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

}