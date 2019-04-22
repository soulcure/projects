package com.ivmall.android.app.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.ivmall.android.app.uitls.Log;

/**
 * for ivmall laucher
 * Created by colin on 2015/8/18.
 */
public class KidsMindProvider extends ContentProvider {

    public static final String authority = "content://com.ivmall.android.app.provider.record";

    public static final Uri CONTENT_URI = Uri.parse(authority);

    private final String TAG = KidsMindProvider.class.getSimpleName();


    private static final String DB_NAME = "kidsmind.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "data_cache";


    private static final String BEGINNING = " (";
    private static final String TERMINATOR = ");";


    private static final String TABLE_ID = android.provider.BaseColumns._ID;

    private static final String TABLE_URL = "url";
    private static final String TABLE_PARAM = "param";


    private DBHelper dbHelper = null;


    public class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            StringBuffer sb = new StringBuffer();

            sb.append("CREATE TABLE ").append(TABLE_NAME);
            sb.append(BEGINNING);
            sb.append(TABLE_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");

            sb.append(TABLE_URL).append(" TEXT NOT NULL, ");
            sb.append(TABLE_PARAM).append(" TEXT NOT NULL");

            sb.append(TERMINATOR);

            String sql = sb.toString();
            Log.v(TAG, "create table sql:" + sql);

            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }


    @Override
    public boolean onCreate() {

        dbHelper = new DBHelper(getContext());
        return (dbHelper == null) ? false : true;

    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {


        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        if (c != null) {
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return c;


    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (null == uri || null == values) return null; // Should never happen but let's be safe

        Uri resultUri = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long rowId = db.insert(TABLE_NAME, null, values);

        if (rowId > 0) {
            resultUri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(rowId));
        }

        return resultUri;


    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }


    /*for user*/
    private void queryPlayRecord() {
        final String uriStr = "content://com.ivmall.android.app.provider.record";
        Uri contentUri = Uri.parse(uriStr);

        Cursor cursor = getContext().getContentResolver().query(contentUri, null, null, null, null);

        String url = cursor.getString(cursor.getColumnIndex("url"));  // for user http psot url
        String param = cursor.getString(cursor.getColumnIndex("param")); //for user http psot params

        cursor.close();
    }

    private void insertPlayRecord(String url, String param) {

        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = Uri.parse(authority);
        ContentValues values = new ContentValues();
        values.put(TABLE_URL, url);
        values.put(TABLE_PARAM, param);
        Uri returnUir = contentResolver.insert(uri, values);

        Log.v(TAG, "insert" + returnUir.getPath());
    }
        /*for user*/

}