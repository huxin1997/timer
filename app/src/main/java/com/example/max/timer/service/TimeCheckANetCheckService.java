package com.example.max.timer.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;

import com.example.max.timer.bean.TimerBean;
import com.example.max.timer.tool.DBHelper;

import java.io.File;
import java.util.Date;
import java.util.List;

public class TimeCheckANetCheckService extends Service {

    private static final String SP_NAME="notificationList";

    private List<TimerBean> timerBeans;
    private boolean isLoop = true;
    private Thread thread;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;

    public TimeCheckANetCheckService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        edit = sharedPreferences.edit();
        beginCheck();
        return super.onStartCommand(intent, flags, startId);
    }

    private void beginCheck(){
        if (thread == null)
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isLoop) {
                        timerBeans = DBHelper.readTimer4Database(TimeCheckANetCheckService.this);
                        for(TimerBean t : timerBeans){
                            String timerID = t.getTimerID();

                        }
                        Date date = new Date(System.currentTimeMillis());
                        int nowYear = date.getYear()+1900, nowMonth = date.getMonth()+1, nowDay = date.getDate(), nowHour = date.getHours(), nowMinute = date.getMinutes();
                        for (TimerBean t : timerBeans) {
                            boolean isHourToast = sharedPreferences.getBoolean("hourToast",false);
                            boolean is15MinuteToast = sharedPreferences.getBoolean("15MinuteToast",false);
                            boolean is5MinuteToast = sharedPreferences.getBoolean("5MinuteToast",false);
                            if (t.getYear() == nowYear && t.getMonth() == nowMonth && t.getDay() == nowDay) {
                                if (t.getHour() - nowHour <= 1 && !isHourToast) {
                                    //todo 一小时发通知
                                    isHourToast = true;
                                } else if (t.getMinute() - nowMinute <= 15 && !is15MinuteToast) {
                                    //todo 15分钟通知
                                    is15MinuteToast = true;
                                } else if (t.getMinute() - nowMinute <= 5 && !is5MinuteToast) {
                                    //todo 5分钟搞事
                                    is5MinuteToast = true;
                                }
                            }
                        }
                        //5分钟检查一次
                        try {
                            Thread.sleep(1000 * 60 * 1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        thread.start();
    }
}
