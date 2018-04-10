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

/**
 * Created by 贺石骞 on 2018/4/9.
 */

public class Tool {

    public static View[] buildTimePickView(Context context) {
        DatePicker datePicker=new DatePicker(context);
        TimePicker timePicker=new TimePicker(context);
        View[] views=new View[2];
        views[0]=datePicker;
        views[1]=timePicker;
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
}
