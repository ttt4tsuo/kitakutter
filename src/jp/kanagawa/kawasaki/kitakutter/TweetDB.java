package jp.kanagawa.kawasaki.kitakutter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

public class TweetDB{
	public interface DataColumns extends BaseColumns{
		public static final String _ID = "_id";
		public static final String FIRST = "first";
		public static final String SECOND = "second";
		public static final String THIRD = "third";
	}
	
	private SQLiteDatabase tweetDB;
	
	private static class DatabaseHelper extends SQLiteOpenHelper{
		public static final String DATABASE_NAME = "tweets";
		public static final String DATABASE_CREATE = "create table "
								+ "tweettable" + " ("
								+ DataColumns._ID + " integer primary key autoincrement, "
								+ DataColumns.FIRST + " text, "
								+ DataColumns.SECOND + " text, "
								+ DataColumns.THIRD + " text"
								+ ");";
		DatabaseHelper(Context context){
			super(context,DATABASE_NAME,null,2);
		}
		public void onCreate(SQLiteDatabase db){
			try{
				db.execSQL(DATABASE_CREATE);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			try{
				db.execSQL("DROP TABLE IF EXISTS tweettable");
				onCreate(db);	
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public TweetDB(Context context){
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		try{
			tweetDB = dbHelper.getWritableDatabase();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public long insert(ContentValues values){
		long rowID = tweetDB.insert("tweettable", "", values);
		if(rowID > 0){
			return rowID;
		}
		throw new SQLException("Failed to insert row");
	}
	
	public Cursor query(String[] projection, 
						String selection, 
						String[] selectionArgs,
						String sortOrder){
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables("tweettable");
		if(sortOrder==null || sortOrder.equals("")){
				sortOrder = DataColumns.FIRST;
		}
		Cursor c = sqlBuilder.query(tweetDB,projection,selection,selectionArgs,
									null,null,sortOrder);
		return c;
	}
	public void closeDB(){
		tweetDB.close();
	}
}