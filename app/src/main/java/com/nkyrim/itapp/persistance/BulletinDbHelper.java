package com.nkyrim.itapp.persistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nkyrim.itapp.domain.Bulletin;

import java.util.ArrayList;
import java.util.List;

/**
 * Database controller for (latest)bulletin storage and retrieval
 */
public class BulletinDbHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "bulletins.db";
	private static final int DB_VERSION = 1;

	private static final String TABLE_BULLETIN = "bulletin";
	private static final String COL_ID = "_id";
	private static final String COL_TITLE = "title";
	private static final String COL_TEXT = "text";
	private static final String COL_AUTHOR = "author";
	private static final String COL_BOARD = "board";
	private static final String COL_DATE = "date";
	private static final String COL_ATTACH = "attach";

	public BulletinDbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String create = "create table " + TABLE_BULLETIN
				+ " (" + COL_ID + " integer primary key autoincrement, "
				+ COL_TITLE + " text, " + COL_TEXT + " text, "
				+ COL_AUTHOR + " text, " + COL_BOARD + " text, "
				+ COL_DATE + " text, " + COL_ATTACH + " text);";

		db.execSQL(create);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("drop table if exists " + TABLE_BULLETIN);

		// Create tables again
		onCreate(db);
	}

	public ArrayList<Bulletin> getBulletins() {
		ArrayList<Bulletin> list = new ArrayList<>(20);
		Bulletin bulletin;

		// Open db
		SQLiteDatabase db = this.getWritableDatabase();

		// Select query
		Cursor cursor = db.query(TABLE_BULLETIN, null, null, null, null, null, null);

		// Get the records
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			bulletin = new Bulletin(cursor.getString(1), cursor.getString(2), cursor.getString(3),
									cursor.getString(4), cursor.getString(5), cursor.getString(6));
			list.add(bulletin);
			cursor.moveToNext();
		}

		// close db
		cursor.close();
		db.close();

		return list;
	}

	public Bulletin getLatest() {
		Bulletin bulletin = null;

		// Open db
		SQLiteDatabase db = this.getWritableDatabase();

		// Select query
		Cursor cursor = db.rawQuery(
				"SELECT * "
						+ "FROM " + TABLE_BULLETIN
						+ " WHERE " + COL_ID + "= (SELECT MIN(" + COL_ID + ")"
						+ " FROM " + TABLE_BULLETIN + ");", null);

		// Get the records
		cursor.moveToFirst();
		if(!cursor.isAfterLast()) {
			bulletin = new Bulletin(cursor.getString(1), cursor.getString(2), cursor.getString(3),
									cursor.getString(4), cursor.getString(5), cursor.getString(6));
		}

		// close db
		cursor.close();
		db.close();

		return bulletin;
	}

	public void insert(List<Bulletin> list) {
		// Open db
		SQLiteDatabase db = this.getWritableDatabase();

		// Delete all(20) previous records
		db.delete(TABLE_BULLETIN, null, null);
		db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_BULLETIN + "'");

		// Add the first 20 new bulletins
		ContentValues[] values = new ContentValues[20];
		for (int i = 0; i < 20; i++) {
			if(i >= list.size()) break;
			values[i] = new ContentValues();
			values[i].put(COL_TITLE, list.get(i).getTitle());
			values[i].put(COL_TEXT, list.get(i).getText());
			values[i].put(COL_AUTHOR, list.get(i).getAuthor());
			values[i].put(COL_BOARD, list.get(i).getBoard());
			values[i].put(COL_DATE, list.get(i).getDate());
			values[i].put(COL_ATTACH, list.get(i).getAttach());
		}
		for (int i = 0; i < 20; i++) {
			if(values[i] != null) db.insert(TABLE_BULLETIN, null, values[i]);
		}

		// close db
		db.close();
	}
}
