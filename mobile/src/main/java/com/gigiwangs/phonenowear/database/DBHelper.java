package com.gigiwangs.phonenowear.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteException;

import android.util.Log;

import com.gigiwangs.phonenowear.PhoneArea;

/**
 *
 * @author hy511
 *
 */
@SuppressLint("SdCardPath")
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DATABASE";
    public static DBHelper instance;
    private SQLiteDatabase db = null;

    private static final Integer DB_VERSION =2;
    public static final String DB_PATH = "/data/data/com.gigiwangs.phonenowear/databases/";
    private static final String DB_NAME = "location.db";
    private static final String TBL_PHONE_NAME = "phone_location";
    private static final String TBL_CITY_NAME = "City_PhoneCode";

    /**
     * 单例模式
     * @param context
     */
    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        try {
            db = getWritableDatabase();
        } catch (Exception e) {
            db = getReadableDatabase();
        }

    }

    /**
     * @param context
     * @return
     */
    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // db.execSQL("create table phone_location (_id INTEGER primary key,location varchar(32) not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public static boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        try{
            String path = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            // database don't exist yet.
            e.printStackTrace();
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }
    /**
     * 复制数据库文件到软件目录
     * @param context
     */
    public static void copyDB(Context context) {
        boolean dbExist = checkDataBase();
        if(dbExist) {
            Log.i(TAG, "DATABASE: Exist .. ");
        }
        boolean dBExists = new File(DB_PATH + DB_NAME).exists();
        if (!dBExists) {
            Log.i(TAG, "DATABASE: NOT EXISTS ");
            File directory = new File(DB_PATH);
            if (!directory.exists())
                directory.mkdir();
            try {
                Log.i(TAG, "DATABASE: COPYING .. ");
                InputStream is = context.getAssets().open(DB_NAME);
                OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
                os.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public PhoneArea findPhoneArea(String... args) {

        Cursor c = null;
        PhoneArea phoneArea = null;
        try {

            c = db.rawQuery("select * from "+ TBL_CITY_NAME +" where code = ?",
                    args);
            if (c.getCount() == 1) {
                c.moveToNext();
                Integer id = c.getInt(c.getColumnIndex("Code"));
                String province = c.getString(c.getColumnIndex("Province"));
                String city = c.getString(c.getColumnIndex("City"));
                String area=province+city;
                phoneArea = new PhoneArea(id, area);
                Log.d(TAG, "find:" + args[0] + ";area:" + area);
            }
            else
            {
                Log.e(TAG, "Can't find:" + args[0]);
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.e(TAG, "findPhoneArea exception:" + e.getMessage());
        } finally {
            if (c != null)
                c.close();
        }
        return phoneArea;

    }

    public PhoneArea findMobileArea(String... args) {

        Cursor c = null;
        Log.d(TAG, "find:" + args[0]);
        PhoneArea phoneArea = null;
        try {

            c = db.rawQuery("select * from "+ TBL_PHONE_NAME +" where rowid = ?",
                    args);
            if (c.getCount() == 1) {
                c.moveToNext();
                Integer id = c.getInt(c.getColumnIndex("_id"));
                String area = c.getString(c.getColumnIndex("area"));
                phoneArea = new PhoneArea(id, area);
                Log.d(TAG, "find:" + args[0] + ";area:" + area);
            }
            else
            {
                Log.e(TAG, "Can't find:" + args[0]);
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.e(TAG, "findMobileArea exception:" + e.getMessage());
        } finally {
            if (c != null)
                c.close();
        }
        return phoneArea;

    }


}