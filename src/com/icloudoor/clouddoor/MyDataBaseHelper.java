package com.icloudoor.cloudoor;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDataBaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "KeyDB.db";
	public static final String TABLE_NAME = "KeyInfoTable";
	public static final String Key_TABLE_NAME = "ZoneKeyTable";
	public static final String ZONE_TABLE_NAME = "ZoneTable";
	public static final String CAR_TABLE_NAME = "CarKeyTable";

	public MyDataBaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public MyDataBaseHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public MyDataBaseHelper(Context context, String name) {
		this(context, name, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer sBuffer = new StringBuffer();
		StringBuffer keyBuffer = new StringBuffer();
		StringBuffer zoneBuffer = new StringBuffer();
		StringBuffer carBuffer = new StringBuffer();
		
		sBuffer.append("CREATE TABLE [" + TABLE_NAME + "] (");
		sBuffer.append("[zoneId] TEXT, ");
		sBuffer.append("[doorName] TEXT, ");
		sBuffer.append("[doorId] TEXT,");
		sBuffer.append("[deviceId] TEXT,");
		sBuffer.append("[doorType] TEXT,");
		sBuffer.append("[plateNum] TEXT, ");
		sBuffer.append("[direction] TEXT, ");
		sBuffer.append("[authFrom] TEXT,");
		sBuffer.append("[authTo] TEXT)");
		db.execSQL(sBuffer.toString());
		Log.e("DBHelper", "TABLE onCreate");

		keyBuffer.append("CREATE TABLE [" + Key_TABLE_NAME + "] (");
		keyBuffer.append("[zoneId] TEXT, ");
		keyBuffer.append("[zoneAddress] TEXT,");
		keyBuffer.append("[l1ZoneId] TEXT)");
		db.execSQL(keyBuffer.toString());
		Log.e("DBHelper", "TABLE onCreate");
		
		zoneBuffer.append("CREATE TABLE [" + ZONE_TABLE_NAME + "](");
		zoneBuffer.append("[zoneid] TEXT, ");
		zoneBuffer.append("[zonename] TEXT, ");
		zoneBuffer.append("[parentzoneid] TEXT)");
		db.execSQL(zoneBuffer.toString());
		Log.e("DBHelper", "TABLE onCreate");
		
		carBuffer.append("CREATE TABLE [" + CAR_TABLE_NAME + "](");
		carBuffer.append("[l1ZoneId] TEXT, ");
		carBuffer.append("[plateNum] TEXT, ");
		carBuffer.append("[carStatus] TEXT, ");
		carBuffer.append("[carPosStatus] TEXT)");
		db.execSQL(carBuffer.toString());
		Log.e("DBHelper", "TABLE onCreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

	public boolean tabIsExist(String tabName) {
		boolean result = false;
		if (tabName == null) {
			return false;
		}
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.getWritableDatabase();
			String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
					+ tabName.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}
            cursor.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

}