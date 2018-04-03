package com.example.max.timer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.max.timer.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 贺石骞 on 2018/4/3.
 */

public class TimerListAdapter extends BaseAdapter {

    private static final String TAG = "TimerListAdapter";

    private List<HashMap<String,Object>> data;
    private Context mContext;
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

    public TimerListAdapter(Context context, List<HashMap<String,Object>> data) {
        this.data=data;
        mContext=context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View inflate = View.inflate(mContext, R.layout.list_items_timer_item, null);
        Chronometer chronometer= (Chronometer) inflate.findViewById(R.id.chronometer_list_item_own);
        TextView textView= (TextView) inflate.findViewById(R.id.tv_date_list_item_own);
        long toTime = (long) data.get(position).get("toTime");
        textView.setText(mContext.getString(R.string.timer_to_time,sdf.format(new Date(toTime))));
        long nowTime = System.currentTimeMillis();
        chronometer.setCountDown(true);
        chronometer.setBase(SystemClock.elapsedRealtime()+(toTime-nowTime));
        chronometer.setFormat("dd天HH时mm分ss秒");
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                String s = chronometer.getText().toString();
                if("00:00".equals(s)){
                    chronometer.stop();
                    Toast.makeText(mContext, "位置为"+position+"的时钟到了", Toast.LENGTH_SHORT).show();
                }
            }
        });
        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"Click");
            }
        });
        chronometer.start();
        return inflate;
    }
}
