package com.tracmojo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RemDBHelper extends SQLiteOpenHelper {
	public static final String ROWID = "_id";
	public static final String JSON_DATA = "json_data";
    public static final String TRAC_TYPE = "json_type";

    public static final String PERSONAL_TRAC = "p";
    public static final String GROUP_TRAC = "g";
    public static final String FOLLOWING_TRAC = "f";


	private static final String TAG = "DBAdapter";

	public static final String DATABASE_NAME = "TracMojoDb";
	public static final String DATABASE_TABLE = "Trac";
	
	private static final int DATABASE_VERSION = 1;
	
	public RemDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		String sql = "create table IF NOT EXISTS Trac (_id INTEGER primary key, json_data TEXT, json_type TEXT);";
		
		Log.d(TAG,sql);
		
		try {
			db.execSQL(sql);
		
			Log.i(TAG, "Database created");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS" + DATABASE_TABLE);
		onCreate(db);

	}
	
}
