package com.example.max.timer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.max.timer.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 贺石骞 on 2018/4/3.
 */

public class TimerListAdapter extends RecyclerView.Adapter<TimerListAdapter.TimerViewHolder> {

    private static final String TAG = "TimerListAdapter";

    private List<HashMap<String,Object>> data;
    private Context mContext;
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

    public TimerListAdapter(Context context, List<HashMap<String,Object>> data) {
        this.data=data;
        mContext=context;
    }

    @Override
    public TimerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_timer_item, parent, false);
        TimerViewHolder timerViewHolder=new TimerViewHolder(inflate);
        return timerViewHolder;
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(TimerViewHolder holder, final int position) {
        long timerToWhenLong = (long) data.get(position).get("timerToWhenLong");
        Log.e(TAG,timerToWhenLong+"");
        holder.timerToWhenText.setText(sdf.format(new Date()));
        holder.chronometer.setBase((long) data.get(position).get("timerToWhenLong"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.chronometer.setCountDown(true);
            holder.chronometer.start();
        }
        holder.delThisTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        if(data==null){
            return 0;
        }
        return data.size();
    }


    public class TimerViewHolder extends RecyclerView.ViewHolder {

        TextView timerToWhenText;
        Button delThisTimer;
        Chronometer chronometer;

        public TimerViewHolder(View itemView) {
            super(itemView);
            timerToWhenText= (TextView) itemView.findViewById(R.id.tv_date_list_item_own);
            delThisTimer= (Button) itemView.findViewById(R.id.btn_del_one_timer);
            chronometer= (Chronometer) itemView.findViewById(R.id.chronometer_list_item_own);
        }
    }

}
