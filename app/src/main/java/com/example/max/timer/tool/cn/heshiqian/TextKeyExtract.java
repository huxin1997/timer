package com.example.max.timer.tool.cn.heshiqian;

import android.util.Log;

import com.example.max.timer.tool.Tool;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 贺石骞 on 2018/5/8.
 */

public class TextKeyExtract {

    private static final String TAG = "TextKeyExtract";

    public TextKeyExtract() {
    }

    public static String extract(String str, int keyType) {
        switch (keyType) {
            case KeyType.KEY_TIME_TYPE:
                return new TextKeyExtract().extractTime(str);
            case KeyType.KEY_ADDRESS_TYPE:
                return new TextKeyExtract().extractAddress(str);
            case KeyType.KEY_MAN_TYPE:
                return new TextKeyExtract().extractMan(str);
            case KeyType.KEY_WHERE_TYPE:
                return new TextKeyExtract().extractWhere(str);
            case KeyType.KEY_THING_TYPE:
                return new TextKeyExtract().extractThing(str);
        }
        return "";
    }

    private String extractThing(String str) {

        return "";
    }

    private String extractWhere(String str) {

        return "";
    }

    private String extractMan(String str) {

        return "";
    }

    private String extractAddress(String str) {

        return "";
    }

    private String extractTime(String str) {
        int AMorPM=-1;
        String[] keyP={"下午","晚上","中午","午后"};
        String[] keyA={"上午","早"};
        str=str.replace("快一点","");
        for(String s:keyP){
            if(str.contains(s)){
                AMorPM=KeyType.KEY_PM_TIME;
            }
        }
        for(String s:keyA){
            if(str.contains(s)){
                AMorPM=KeyType.KEY_AM_TIME;
            }
        }

        int hour = rex24Hour(str);
        Date date = new Date(System.currentTimeMillis());

        int hours = date.getHours();

        if(AMorPM==KeyType.KEY_AM_TIME){
            //do nothing
        }else if(AMorPM==KeyType.KEY_PM_TIME){
            hour+=12;
        }else {
            if(hours>12){
                hour+=12;
            }
        }

        int day = date.getDate()+rexDay(str);
        int month = date.getMonth();
        int year = date.getYear();



        return Tool.parseDate(1900+year,month+1,day,hour,0);
    }

    private int rexDay(String str){
        String[] dayKey={"明天","后天","今天"};
        if(str.contains(dayKey[0])){
            return 1;
        }else if(str.contains(dayKey[1])){
            return 2;
        }else if(str.contains(dayKey[2])){
            return 0;
        }else
            return 0;
    }

    private int rex24Hour(String str){
        Pattern compile24 = Pattern.compile("(([1-9])|([1-2][0-9])\\?)点");
        Pattern compileTF = Pattern.compile("((一)|(二)|(三)|(四)|(五)|(六)|(七)|(八)|(九)|(十)|([十][一])|([十][二])|([十][三])|([十][四])|([十][五])|([十][六])|([十][七])|([十][八])|([十][九])|([二][十])|([二][十][一])|([二][十][二]))点");
        Matcher matcher = compile24.matcher(str);
        Matcher tf = compileTF.matcher(str);
        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            Log.e(TAG, str.substring(start, end));
            String substring = str.substring(start, end-1);
            return Integer.parseInt(substring);
        }else {
            if(tf.find()){
                int start = tf.start();
                int end = tf.end();
                String substring = str.substring(start, end-1);

                if("六".equals(substring)){
                    return 6;
                }else if("七".equals(substring)){
                    return 7;
                }else if("八".equals(substring)){
                    return 8;
                }else if("九".equals(substring)){
                    return 9;
                }else if("十".equals(substring)){
                    return 10;
                }else if("十一".equals(substring)){
                    return 11;
                }else if("十二".equals(substring)){
                    return 12;
                }else if("十三".equals(substring)){
                    return 13;
                }else if("十四".equals(substring)){
                    return 14;
                }else if("十五".equals(substring)){
                    return 15;
                }else if("十六".equals(substring)){
                    return 16;
                }else if("十七".equals(substring)){
                    return 17;
                }else if("十八".equals(substring)){
                    return 18;
                }else if("十九".equals(substring)){
                    return 19;
                }else if("二十".equals(substring)){
                    return 20;
                }else if("二十一".equals(substring)){
                    return 21;
                }else if("二十二".equals(substring)){
                    return 22;
                }
            }
        }
        return -1;
    }

    public class KeyType {

        public static final int KEY_TIME_TYPE = -6120;
        public static final int KEY_ADDRESS_TYPE = -6121;
        public static final int KEY_MAN_TYPE = -6122;
        public static final int KEY_WHERE_TYPE = -6123;
        public static final int KEY_THING_TYPE = -6124;

        public static final int KEY_AM_TIME=-6125;
        public static final int KEY_PM_TIME=-6126;
    }
}
