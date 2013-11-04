package ru.nordmine.crystalmoney.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDb extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "ru.nordmine.crystalmoney.db";
	private static final int DATABASE_VERSION = 14;

	public static final String UID = "_id";
	
	public static final String ACCOUNT_TABLE_NAME = "accounts";
	public static final String ACCOUNT_AMOUNT = "amount";
	public static final String ACCOUNT_NAME = "name";
	public static final String ACCOUNT_COMMENT = "comment";
	public static final String ACCOUNT_IS_CARD = "is_card";
	public static final String ACCOUNT_PICTURE = "picture_id";
	
	public static final String TRX_TABLE_NAME = "transactions";
	public static final String TRX_TYPE = "trx_type";
	public static final String TRX_COMMENT = "trx_comment";
	public static final String TRX_CREATED = "created";
	public static final String TRX_AMOUNT = "amount";
	public static final String TRX_ACCOUNT_ID = "account_id";
	public static final String TRX_CATEGORY_ID = "category_id";	
	
	public static final String CAT_TABLE_NAME = "categories";
	public static final String CAT_NAME = "name";
	public static final String CAT_TYPE = "category_type";
	
	private static final String SQL_CREATE_ACCOUNTS =
			"create table " + ACCOUNT_TABLE_NAME 
			+ " (" + UID +  " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ ACCOUNT_NAME + " varchar(50) not null, "
			+ ACCOUNT_AMOUNT + " numeric not null, "
			+ ACCOUNT_COMMENT + " varchar(255) not null, "
			+ ACCOUNT_IS_CARD + " integer not null, "
			+ ACCOUNT_PICTURE + " integer not null)";
	
	private static final String SQL_CREATE_TRX = 
			"create table " + TRX_TABLE_NAME
			+ " (" + UID + " integer primary key autoincrement, "
			+ TRX_TYPE + " integer not null, "
			+ TRX_COMMENT + " varchar(255) not null, "
			+ TRX_CREATED + " integer not null, "
			+ TRX_AMOUNT + " numeric not null, "
			+ TRX_ACCOUNT_ID + " integer not null, "
			+ TRX_CATEGORY_ID + " integer not null, "
			+ "foreign key (" + TRX_CATEGORY_ID
			+ ") references " + CAT_TABLE_NAME + "(" + UID + "),"
			+ "foreign key (" + TRX_ACCOUNT_ID
			+ ") REFERENCES " + ACCOUNT_TABLE_NAME + "(" + UID + "))";
	
	private static final String SQL_CREATE_CATEGORY = 
			"create table " + CAT_TABLE_NAME
			+ " (" + UID + " integer primary key autoincrement, "
			+ CAT_NAME + " varchar(255) not null, "
			+ CAT_TYPE + " integer not null)";

	public MyDb(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
	        db.execSQL("PRAGMA foreign_keys=ON;");
	    }
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ACCOUNTS);
		db.execSQL(SQL_CREATE_CATEGORY);
		db.execSQL(SQL_CREATE_TRX);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		String[] tablesForDelete = new String[] { TRX_TABLE_NAME,
				ACCOUNT_TABLE_NAME, CAT_TABLE_NAME };
		for (String tableName : tablesForDelete) {
			db.execSQL("drop table if exists " + tableName);
		}
		onCreate(db);
	}

}
