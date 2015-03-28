package jp.kanagawa.kawasaki.kitakutter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

public class TimelineDB{
	public interface TimelineColumns extends BaseColumns{
		public static final String _ID = "_id";
		public static final String TIMELINE = "timeline";
	}
	
	private SQLiteDatabase timelineDB;
	private DatabaseHelper dbHelper;

	public TimelineDB(Context context){
		dbHelper = new DatabaseHelper(context);
		try{
			timelineDB = dbHelper.getWritableDatabase();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public long insert(ContentValues values){
		long rowID = timelineDB.insert("timelinetable", "", values);
		if(rowID > 0){
			return rowID;
		}
		throw new SQLException("Failed to insert row");
	}
	
	public Cursor query(String[] projection, 
						String selection, 
						String[] selectionArgs){
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables("timelinetable");
		Cursor c = sqlBuilder.query(timelineDB,projection,selection,selectionArgs,
									null,null,TimelineColumns._ID);
		return c;
	}
	
	public void dropForUpdateDB(){
		try{
			timelineDB.execSQL("DROP TABLE IF EXISTS timelinetable");
			timelineDB.execSQL(dbHelper.DATABASE_CREATE);
		}catch (SQLException e){
			throw e;
		}
	}
	
	public void closeDB(){
		timelineDB.close();
	}
	
	/*
	 * SQLiteヘルパークラス
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper{
		public static final String DATABASE_NAME = "timelines";
		public final String DATABASE_CREATE = "create table "
								+ "timelinetable" + " ("
								+ TimelineColumns._ID + " integer primary key autoincrement, "
								+ TimelineColumns.TIMELINE + " text"
								+ ");";
		
		DatabaseHelper(Context context){
			super(context,DATABASE_NAME,null,1);
		}
		public void onCreate(SQLiteDatabase db){
			try{
				db.execSQL(DATABASE_CREATE);
				Log.d("myapp","dbcreate");
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			try{
				db.execSQL("DROP TABLE IF EXISTS timelinetable");
				onCreate(db);	
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		@Override
		public void onOpen(SQLiteDatabase db){
			super.onOpen(db);
			Log.d("myapp","dbopen");
		}
	}
}