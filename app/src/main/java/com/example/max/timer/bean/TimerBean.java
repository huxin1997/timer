package com.example.max.timer.bean;

import java.io.Serializable;

/**
 * Created by 贺石骞 on 2018/4/11.
 */

public class TimerBean implements Serializable,Cloneable{

    public static final int TYPE_GROUP_TIMER=-1;
    public static final int TYPE_PRESON_TIMER=-2;

    private String timerID;
    private String timerNickName;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private String dateString;
    private int timerType=TYPE_PRESON_TIMER;

    private String desc;


    @Override
    public String toString() {
        return "TimerBean{" +
                "timerID='" + timerID + '\'' +
                ", timerNickName='" + timerNickName + '\'' +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minute=" + minute +
                ", dateString='" + dateString + '\'' +
                ", timerType=" + timerType +
                ", desc='" + desc + '\'' +
                '}';
    }

    public static int getTypeGroupTimer() {
        return TYPE_GROUP_TIMER;
    }

    public static int getTypePresonTimer() {
        return TYPE_PRESON_TIMER;
    }

    public String getTimerID() {
        return timerID;
    }

    public void setTimerID(String timerID) {
        this.timerID = timerID;
    }

    public String getTimerNickName() {
        return timerNickName;
    }

    public void setTimerNickName(String timerNickName) {
        this.timerNickName = timerNickName;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public int getTimerType() {
        return timerType;
    }

    public void setTimerType(int timerType) {
        this.timerType = timerType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public TimerBean(String timerID, String timerNickName, int year, int month, int day, int hour, int minute, String dateString, int timerType, String desc) {

        this.timerID = timerID;
        this.timerNickName = timerNickName;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.dateString = dateString;
        this.timerType = timerType;
        this.desc = desc;
    }

    public TimerBean() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
