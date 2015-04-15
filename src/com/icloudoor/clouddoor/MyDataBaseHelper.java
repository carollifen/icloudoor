package com.icloudoor.clouddoor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDataBaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "KeyDB.db";
	public static final String TABLE_NAME = "KeyInfoTable";

	public MyDataBaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	public MyDataBaseHelper(Context context, String name, int version){
		this(context,name,null,version);
	}

	public MyDataBaseHelper(Context context, String name){
		this(context,name,DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer sBuffer = new StringBuffer();

		sBuffer.append("CREATE TABLE [" + TABLE_NAME + "] (");
		sBuffer.append("[doorId] TEXT, ");
		sBuffer.append("[doorName] TEXT,");
		sBuffer.append("[deviceId] TEXT,");
		sBuffer.append("[authFrom] TEXT,");
		sBuffer.append("[authTo] TEXT,");
		sBuffer.append("[status] INTEGER)");

		db.execSQL(sBuffer.toString());
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

}