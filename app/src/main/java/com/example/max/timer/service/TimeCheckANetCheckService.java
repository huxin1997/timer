package com.example.max.timer.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.example.max.timer.R;
import com.example.max.timer.bean.TimerBean;
import com.example.max.timer.tool.DBHelper;

import java.io.File;
import java.util.Date;
import java.util.List;

public class TimeCheckANetCheckService extends Service {

    private static final String SP_NAME_ONE_HOUR="notificationList1Hour";
    private static final String SP_NAME_30_MIN="notificationList30Min";
    private static final String SP_NAME_15_MIN="notificationList15Min";
    private static final String SP_NAME_5_MIN="notificationList5Min";

    private static final int NOTIFICATION_ONE_HOUR=1;
    private static final int NOTIFICATION_30_MIN=2;
    private static final int NOTIFICATION_15_MIN=3;
    private static final int NOTIFICATION_5_MIN=4;

    private List<TimerBean> timerBeans;
    private boolean isLoop = true;
    private Thread thread;
    private SharedPreferences spHour;
    private SharedPreferences sp30Min;
    private SharedPreferences sp15Min;
    private SharedPreferences sp5Min;
    private SharedPreferences.Editor spHourEdit;
    private SharedPreferences.Editor sp30MinEdit;
    private SharedPreferences.Editor sp15MinEdit;
    private SharedPreferences.Editor sp5MinEdit;

    public TimeCheckANetCheckService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        spHour = getSharedPreferences(SP_NAME_ONE_HOUR, MODE_PRIVATE);
        sp30Min = getSharedPreferences(SP_NAME_30_MIN, MODE_PRIVATE);
        sp15Min = getSharedPreferences(SP_NAME_15_MIN, MODE_PRIVATE);
        sp5Min = getSharedPreferences(SP_NAME_5_MIN, MODE_PRIVATE);
        spHourEdit = spHour.edit();
        sp30MinEdit = sp30Min.edit();
        sp15MinEdit = sp15Min.edit();
        sp5MinEdit = sp5Min.edit();
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
                        Date date = new Date(System.currentTimeMillis());
                        int nowYear = date.getYear()+1900, nowMonth = date.getMonth()+1, nowDay = date.getDate(), nowHour = date.getHours(), nowMinute = date.getMinutes();
                        for (TimerBean t : timerBeans) {
                            if (t.getYear() == nowYear && t.getMonth() == nowMonth && t.getDay() == nowDay) {
                                boolean isHourToast=false;
                                boolean is30MinuteToast=false;
                                boolean is15MinuteToast=false;
                                boolean is5MinuteToast=false;

                                if (spHour.getString(t.getTimerID(), "not yet").equals("not yet")&&t.getHour()-nowHour<=1&&t.getMinute()-nowMinute<=0) {
//                                    isHourToast=true;
                                    //todo 一小时发通知
                                    spHourEdit.putString(t.getTimerID(),"has");
                                    spHourEdit.apply();
                                    spHourEdit.commit();
                                    Notification.Builder builder=new Notification.Builder(TimeCheckANetCheckService.this);
                                    builder.setSmallIcon(R.mipmap.ic_launcher_round)
                                            .setContentTitle("倒计时提示！")
                                            .setContentText("您的倒计时还有一小时！");
                                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        manager.notify(NOTIFICATION_ONE_HOUR,builder.build());
                                    }
                                }else if(spHour.getString(t.getTimerID(), "not yet").equals("not yet")&&t.getHour()-nowHour<=0&&t.getMinute()-nowMinute<=30){
                                    spHourEdit.putString(t.getTimerID(),"has");
                                    spHourEdit.apply();
                                    spHourEdit.commit();
                                    Notification.Builder builder=new Notification.Builder(TimeCheckANetCheckService.this);
                                    builder.setSmallIcon(R.mipmap.ic_launcher_round)
                                            .setContentTitle("倒计时提示！")
                                            .setContentText("您的倒计时还有30分钟！");
                                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        manager.notify(NOTIFICATION_ONE_HOUR,builder.build());
                                    }
                                }else if (spHour.getString(t.getTimerID(), "not yet").equals("not yet")&&t.getHour()-nowHour<=0&&t.getMinute()-nowMinute<=15){
                                    spHourEdit.putString(t.getTimerID(),"has");
                                    spHourEdit.apply();
                                    spHourEdit.commit();
                                    Notification.Builder builder=new Notification.Builder(TimeCheckANetCheckService.this);
                                    builder.setSmallIcon(R.mipmap.ic_launcher_round)
                                            .setContentTitle("倒计时提示！")
                                            .setContentText("您的倒计时还有15分钟！");
                                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        manager.notify(NOTIFICATION_ONE_HOUR,builder.build());
                                    }
                                }else if (spHour.getString(t.getTimerID(), "not yet").equals("not yet")&&t.getHour()-nowHour<=0&&t.getMinute()-nowMinute<=5){
                                    spHourEdit.putString(t.getTimerID(),"has");
                                    spHourEdit.apply();
                                    spHourEdit.commit();
                                    Notification.Builder builder=new Notification.Builder(TimeCheckANetCheckService.this);
                                    builder.setSmallIcon(R.mipmap.ic_launcher_round)
                                            .setContentTitle("倒计时提示！")
                                            .setContentText("您的倒计时还有5分钟！");
                                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        manager.notify(NOTIFICATION_ONE_HOUR,builder.build());
                                    }
                                }

                                Log.e("Toast",t.getTimerID()+" "+isHourToast+" "+is30MinuteToast+" "+is15MinuteToast+" "+is5MinuteToast+"");


                            }
                        }
                        //5分钟检查一次
                        try {
                            Thread.sleep(1000 * 10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        thread.start();
    }
}
