package com.example.max.timer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.max.timer.R;
import com.example.max.timer.bean.TimerBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by 贺石骞 on 2018/4/17.
 */

public class TimerDetailAdapter extends BaseAdapter{

    private Context context;
    private List<TimerBean> list;
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    private SimpleDateFormat sdfParse=new SimpleDateFormat("yyyyMMddHHmm");
    private Date parse;

    public TimerDetailAdapter(Context context,List<TimerBean> list) {
        this.context=context;
        this.list=list;
    }

    @Override
    public int getCount() {
        if(list==null)
            return 0;
        else
            return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View inflate = LayoutInflater.from(context).inflate(R.layout.timer_detail_list_item_layout, parent, false);
        Chronometer chronometer = (Chronometer) inflate.findViewById(R.id.detail_chronometer);
        TextView desc = (TextView) inflate.findViewById(R.id.tv_detail_list_item_desc);
        TimerBean timerBean = list.get(position);
        try {
            parse = sdfParse.parse(timerBean.getDateString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long l = (parse.getTime() - System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            chronometer.setCountDown(true);
            chronometer.setBase(SystemClock.elapsedRealtime() + l);
            chronometer.start();
        }else {
            Toast.makeText(context, "控件不支持你的系统版本！", Toast.LENGTH_SHORT).show();
        }
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                String s = chronometer.getText().toString();
                if(s.equals("00:00")|| s.contains("-")){
                    chronometer.stop();
                }
            }
        });
        desc.setText(sdf.format(parse.getTime()));
        return inflate;
    }
}
