package com.example.max.timer.tool;

import android.content.Context;

/**
 * Created by 贺石骞 on 2018/4/13.
 */

public final class SystemConfig {

    public static final int ACTIVITY_MAIN_ACTIVITY_POST=5000;
    public static final int ACTIVITY_MAIN_ACTIVITY_RESULT=4000;
    public static final int ACTIVITY_TIMER_CREATE_ACTIVITY_POST=5001;
    public static final int ACTIVITY_TIMER_CREATE_ACTIVITY_RESULT=4001;
    public static final int ACTIVITY_TIMER_CREATE_GROUP_ACTIVITY_POST=5002;
    public static final int ACTIVITY_TIMER_CREATE_GROUP_ACTIVITY_RESULT=4002;

    private static Context mContext;

    public static void setContext(Context context){
        mContext=context;
    }

    public static Context getInstanceContext(){
        return mContext;
    }
}
