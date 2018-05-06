package com.example.max.timer.tool;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * Created by 贺石骞 on 2018/4/13.
 */

public final class SystemConfig {

    public static final int ACTIVITY_MAIN_ACTIVITY_POST = 5000;
    public static final int ACTIVITY_MAIN_ACTIVITY_RESULT = 4000;
    public static final int ACTIVITY_TIMER_CREATE_ACTIVITY_POST = 5001;
    public static final int ACTIVITY_TIMER_CREATE_ACTIVITY_RESULT = 4001;
    public static final int ACTIVITY_TIMER_CREATE_GROUP_ACTIVITY_POST = 5002;
    public static final int ACTIVITY_TIMER_CREATE_GROUP_ACTIVITY_RESULT = 4002;


    public static HashMap<HttpUrl, List<Cookie>> cookieHashMap = new HashMap<>();



    public static OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    SystemConfig.cookieHashMap.put(HttpUrl.parse("http://118.89.22.131:8080/shiro-2"), cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    if (SystemConfig.cookieHashMap.size() == 0)
                        return new ArrayList<>();
                    return SystemConfig.cookieHashMap.get(HttpUrl.parse("http://118.89.22.131:8080/shiro-2"));
                }
            })
            .build();

    private static Context mContext;

    public static void setContext(Context context) {
        mContext = context;
    }

    public static Context getInstanceContext() {
        return mContext;
    }
}
