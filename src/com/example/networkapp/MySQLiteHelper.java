package com.example.networkapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
	
	/*
	 * adb -s emulator-5554 shell
	   sqlite3 /data/data/com.example.networkapp/databases/menu.db

	 */

	public static final String TABLE_CATEGORY = "category";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";

	public static final String TABLE_ITEM = "item";
	public static final String COLUMN_ITEM_ID = "_id";
	public static final String COLUMN_ITEM_TITLE = "item_title";
	public static final String COLUMN_ITEM_DESCRIPTION = "item_description";
	public static final String COLUMN_ITEM_PRICE = "item_price";
	public static final String COLUMN_ITEM_IMAGE = "item_image";
	public static final String COLUMN_ITEM_IMAGE_URL = "item_image_url";
	public static final String COLUMN_ITEM_CATEGORY = "category_id";
	
	
	private static final String DATABASE_NAME = "menu.db";
	private static final int DATABASE_VERSION = 6;

	// Database creation sql statement
	private static final String CREATE_TABLE_CATEGORY = "create table "
			+ TABLE_CATEGORY + "(" + COLUMN_ID    + " integer primary key, "
								   + COLUMN_TITLE + " text not null);";
	
	// Database creation sql statement
	private static final String CREATE_TABLE_ITEM = "create table "
				+ TABLE_ITEM + "(" + COLUMN_ITEM_ID          + " integer primary key, " 
			    				   + COLUMN_ITEM_TITLE 	     + " text not null,"
			    				   + COLUMN_ITEM_DESCRIPTION + " text not null," 
			    				   + COLUMN_ITEM_PRICE 	     + " float not null,"
			    				   + COLUMN_ITEM_IMAGE 	     + " text,"
			    				   + COLUMN_ITEM_IMAGE_URL 	 + " text,"
			    				   + COLUMN_ITEM_CATEGORY    + " integer no null );";


	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.i("MyActivity", "create tables:");
		database.execSQL(CREATE_TABLE_CATEGORY);
		database.execSQL(CREATE_TABLE_ITEM);
		Log.i("MyActivity", "create tables: " + CREATE_TABLE_ITEM);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
		onCreate(db);
	}
	
	
} 
