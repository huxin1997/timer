package com.example.max.timer.tool;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TimePicker;
import com.example.max.timer.R;

import java.security.MessageDigest;

/**
 * Created by 贺石骞 on 2018/4/9.
 */

public class Tool {

    public static View[] buildTimePickView(Context context) {
        View inflate1 = LayoutInflater.from(context).inflate(R.layout.create_time_timepicker, null, false);
        View[] views=new View[2];
        views[1]=inflate1;
        return views;
    }

    public static void showPopWindow(Context mContext,View attachView) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.add_timer_pop_selector,null,false);
        Button personTimer= (Button) view.findViewById(R.id.btn_pop_add_person_timer);
        Button teamTimer= (Button) view.findViewById(R.id.btn_pop_add_team_timer);

        PopupWindow popupWindow=new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setAnimationStyle(R.style.anim_pop_window);



        popupWindow.showAsDropDown(attachView,0,-(450));



    }

    public static String MD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes("utf-8"));
            return toHex(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHex(byte[] bytes) {

        final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i=0; i<bytes.length; i++) {
            ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }

    public static String parseDate(int y, int m, int d, int h, int M) {
        String y_,m_,d_,h_,M_;
        y_=String.valueOf(y);
        m_ = m<=9?"0"+m:""+m;
        d_ = d<=9?"0"+d:""+d;
        h_ = h<=9?"0"+h:""+h;
        M_ = M<=9?"0"+M:""+M;
        return y_+m_+d_+h_+M_;
    }
}
