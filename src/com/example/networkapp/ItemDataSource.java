package com.example.networkapp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class ItemDataSource {

	// Database fields
	static Context context;
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { 
			MySQLiteHelper.COLUMN_ITEM_ID,
			MySQLiteHelper.COLUMN_ITEM_TITLE,
			MySQLiteHelper.COLUMN_ITEM_DESCRIPTION,
			MySQLiteHelper.COLUMN_ITEM_PRICE,
			MySQLiteHelper.COLUMN_ITEM_IMAGE,
			MySQLiteHelper.COLUMN_ITEM_IMAGE_URL,
			MySQLiteHelper.COLUMN_ITEM_CATEGORY };

	public ItemDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
		this.context = context;
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Item createItem(Item item) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_ID, item.getId());
		values.put(MySQLiteHelper.COLUMN_ITEM_TITLE, item.getTitle());
		values.put(MySQLiteHelper.COLUMN_ITEM_DESCRIPTION,item.getDescription());
		values.put(MySQLiteHelper.COLUMN_ITEM_PRICE, item.getPrice());
		values.put(MySQLiteHelper.COLUMN_ITEM_CATEGORY, item.getCategoryId());
		values.put(MySQLiteHelper.COLUMN_ITEM_IMAGE_URL, item.getImageUrl());
		
		
		Bitmap b = getBitmapFromURL(item.getImageUrl());
		String name = "item_image_"+item.getId()+".png";
		StoreByteImage(b,name);
		values.put(MySQLiteHelper.COLUMN_ITEM_IMAGE, name);
		
		Log.i("MyActivity", "Name image"+name);

		int insertId = (int) database.insert(MySQLiteHelper.TABLE_ITEM, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_ITEM, allColumns,
				MySQLiteHelper.COLUMN_ITEM_ID + " = " + insertId, null, null,
				null, null);
		cursor.moveToFirst();
		Item newItem = cursorToItem(cursor);
		cursor.close();
		return newItem;
	}

	public List<Item> getItemsByCategory(int category_id) {
		List<Item> items = new ArrayList<Item>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_ITEM, allColumns,
				MySQLiteHelper.COLUMN_ITEM_CATEGORY + "=" + category_id, null,
				null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Item item = cursorToItem(cursor);
			items.add(item);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return items;
	}

	public Item get(int id) {

		Cursor cursor = database.query(MySQLiteHelper.TABLE_ITEM, allColumns,
				MySQLiteHelper.COLUMN_ID + "=" + id, null, null, null, null);

		cursor.moveToFirst();
		Item item = cursorToItem(cursor);
		cursor.close();
		return item;
	}

	private Item cursorToItem(Cursor cursor) {
		Item item = new Item();
		item.setId(cursor.getInt(0));
		item.setTitle(cursor.getString(1));
		item.setDescription(cursor.getString(2));
		item.setPrice(cursor.getFloat(3));
		item.setImage(cursor.getString(4));
		item.setImageUrl(cursor.getString(5));
		item.setCategoryId(cursor.getInt(6));

		return item;
	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean StoreByteImage(Bitmap imageData, String nameFile) {

		File f = new File(Environment.getExternalStorageDirectory() + "/"+ context.getString(R.string.image_folder));
		if(!f.isDirectory()){
			f.mkdirs();
		}	

	    OutputStream outStream = null;
	    File file = new File(f, nameFile);
	    try {
	     outStream = new FileOutputStream(file);
	     imageData.compress(Bitmap.CompressFormat.PNG, 100, outStream);
	     outStream.flush();
	     outStream.close();
	    }
	    catch(Exception e)
	    {
	    	return false;
	    }
	    
	    return true;

	}

}
