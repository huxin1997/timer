package com.example.max.timer.tool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.max.timer.bean.GroupBean;
import com.example.max.timer.bean.TimerBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 贺石骞 on 2018/4/13.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";

    public static final int TYPE_GROUP=-23;
    public static final int TYPE_PERSON=-24;

    public static final String TABLENAME="localtimerlist";
    public static final String GROUP_TABLE_NAME="localgrouplist";
    private static final String DBNAME="TimerTogether";
    private static final int VERSION=1;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE localtimerlist(hash_id String,time_nick_name String,year integer,month integer,day integer,houer integer,minute integer,type integer,date_string String,desc_ String)");
        db.execSQL("CREATE TABLE localgrouplist(group_id String,group_name String,grouo_id_int integer,hash_id String,time_nick_name String,year integer,month integer,day integer,houer integer,minute integer,type integer,date_string String,desc_ String)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static boolean saveTimer2Database(GroupBean groupBean, TimerBean timerBean, Context context){
        SQLiteDatabase writableDatabase = new DBHelper(context, DBNAME, null, VERSION).getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("group_id",groupBean.getHash());
        cv.put("group_name",groupBean.getName());
        cv.put("grouo_id_int",groupBean.getId());
        cv.put("hash_id",timerBean.getTimerID());
        cv.put("time_nick_name",timerBean.getTimerNickName());
        cv.put("year",timerBean.getYear());
        cv.put("month",timerBean.getMonth());
        cv.put("day",timerBean.getDay());
        cv.put("houer",timerBean.getHour());
        cv.put("minute",timerBean.getMinute());
        cv.put("type",timerBean.getTimerType());
        cv.put("date_string",timerBean.getDateString());
        cv.put("desc_",timerBean.getDesc());
        long insert = writableDatabase.insert(GROUP_TABLE_NAME, null, cv);
        Log.e(TAG,insert+"");
        return insert>=1;
    }

    public static boolean saveTimer2Database(TimerBean timerBean,Context context){
        SQLiteDatabase writableDatabase = new DBHelper(context, DBNAME, null, VERSION).getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("hash_id",timerBean.getTimerID());
        cv.put("time_nick_name",timerBean.getTimerNickName());
        cv.put("year",timerBean.getYear());
        cv.put("month",timerBean.getMonth());
        cv.put("day",timerBean.getDay());
        cv.put("houer",timerBean.getHour());
        cv.put("minute",timerBean.getMinute());
        cv.put("type",timerBean.getTimerType());
        cv.put("date_string",timerBean.getDateString());
        cv.put("desc_",timerBean.getDesc());
        long insert = writableDatabase.insert(TABLENAME, null, cv);
        Log.e(TAG,insert+"");
        return insert>=1;
    }

    public static List<TimerBean> readTimer4Database(Context context,int type){

        switch (type){
            case TYPE_PERSON:{
                List<TimerBean> list=new ArrayList<>();
                SQLiteDatabase writableDatabase = new DBHelper(context, DBNAME, null, VERSION).getWritableDatabase();
                Cursor query = writableDatabase.query(TABLENAME, null, null, null, null, null, null, null);
                for(query.moveToFirst();!query.isAfterLast();query.moveToNext()){
                    TimerBean bean=new TimerBean();
                    bean.setTimerID(query.getString(query.getColumnIndex("hash_id")));
                    bean.setTimerNickName(query.getString(query.getColumnIndex("time_nick_name")));
                    bean.setYear(query.getInt(query.getColumnIndex("year")));
                    bean.setMonth(query.getInt(query.getColumnIndex("month")));
                    bean.setDay(query.getInt(query.getColumnIndex("day")));
                    bean.setHour(query.getInt(query.getColumnIndex("houer")));
                    bean.setMinute(query.getInt(query.getColumnIndex("minute")));
                    bean.setDateString(query.getString(query.getColumnIndex("date_string")));
                    int type_ = query.getInt(query.getColumnIndex("type"));
                    bean.setDesc(query.getString(query.getColumnIndex("desc_")));
                    bean.setTimerType(type_==TimerBean.TYPE_GROUP_TIMER?TimerBean.TYPE_GROUP_TIMER:TimerBean.TYPE_PRESON_TIMER);
                    list.add(bean);
                }
                if(list.size()==0)
                    Log.w(TAG,"No Data Save On DB!");
                return list;
            }
            case TYPE_GROUP:{
                List<TimerBean> list=new ArrayList<>();
                SQLiteDatabase writableDatabase = new DBHelper(context, DBNAME, null, VERSION).getWritableDatabase();
                Cursor query = writableDatabase.query(GROUP_TABLE_NAME, null, null, null, null, null, null, null);
                for(query.moveToFirst();!query.isAfterLast();query.moveToNext()){
                    TimerBean bean=new TimerBean();
                    bean.setTimerID(query.getString(query.getColumnIndex("hash_id")));
                    bean.setTimerNickName(query.getString(query.getColumnIndex("time_nick_name")));
                    bean.setYear(query.getInt(query.getColumnIndex("year")));
                    bean.setMonth(query.getInt(query.getColumnIndex("month")));
                    bean.setDay(query.getInt(query.getColumnIndex("day")));
                    bean.setHour(query.getInt(query.getColumnIndex("houer")));
                    bean.setMinute(query.getInt(query.getColumnIndex("minute")));
                    bean.setDateString(query.getString(query.getColumnIndex("date_string")));
                    int type_ = query.getInt(query.getColumnIndex("type"));
                    bean.setDesc(query.getString(query.getColumnIndex("desc_")));
                    bean.setTimerType(type_==TimerBean.TYPE_GROUP_TIMER?TimerBean.TYPE_GROUP_TIMER:TimerBean.TYPE_PRESON_TIMER);
                    list.add(bean);
                }
                if(list.size()==0)
                    Log.w(TAG,"No Data Save On DB!");
                return list;
            }
        }
        return new ArrayList<>();
    }

    public static boolean delTimer4DB(Context context,String timerId){
        SQLiteDatabase writableDatabase = new DBHelper(context, DBNAME, null, VERSION).getWritableDatabase();
        int delete = writableDatabase.delete(TABLENAME, "hash_id=?", new String[]{timerId});
        return delete>=1;
    }

}
