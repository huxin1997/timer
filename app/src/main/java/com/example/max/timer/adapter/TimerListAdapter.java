package com.example.max.timer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.max.timer.R;
import com.example.max.timer.bean.TimerBean;
import com.example.max.timer.tool.SystemConfig;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import org.w3c.dom.Text;

import java.text.ParseException;
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
    private List<TimerBean> data_;
    private Context mContext;
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    private SimpleDateFormat sdfParse=new SimpleDateFormat("yyyyMMddHHmm");
    private Date parse;
    private OnItemClickListener mOnItemClickListener;

    public TimerListAdapter(Context context, List<TimerBean> data_) {
        this.data_=data_;
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

        final TimerViewHolder holder_ = holder;

        if(mOnItemClickListener != null){
            //为ItemView设置监听器
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG,"test");
                    int pos = holder_.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder_.itemView,pos);
                }
            });
        }

        TimerBean timerBean = data_.get(position);
        try {
            parse = sdfParse.parse(timerBean.getDateString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long l = (parse.getTime() - System.currentTimeMillis());
        final int ceil = (int) Math.ceil(l /1000 / 60 / 60);
        if (ceil<24) {
            holder.chronometerT.setVisibility(View.GONE);
            holder.chronometer.setVisibility(View.VISIBLE);
            holder.timerToWhenText.setText(sdf.format(parse.getTime()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.chronometer.setCountDown(true);
                holder.chronometer.setBase(SystemClock.elapsedRealtime() + l);
                holder.chronometer.start();
            }else {
                Toast.makeText(mContext, "控件不支持你的系统版本！", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            holder.chronometer.setVisibility(View.GONE);
            holder.chronometerT.setVisibility(View.VISIBLE);
            int allDay = (int)Math.ceil(l /1000 / 60 / 60 / 24);
            holder.timerToWhenText.setText(sdf.format(parse.getTime()));
            holder.chronometerT.setText("还有"+allDay+"天");
        }
        holder.chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                String s = chronometer.getText().toString();
                if(s.equals("00:00")||s.contains("-")){
                    chronometer.stop();
                }
            }
        });
        holder.delThisTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data_.remove(position);
                notifyDataSetChanged();
            }
        });
    }



    @Override
    public int getItemCount() {
        if(data_==null){
            return 0;
        }
        return data_.size();
    }


    public class TimerViewHolder extends RecyclerView.ViewHolder {

        TextView timerToWhenText;
        Button delThisTimer;
        Chronometer chronometer;
        TextView chronometerT;
//        SwipeMenuLayout swipeMenuLayout;
        CardView cardView;

        public TimerViewHolder(View itemView) {
            super(itemView);
            timerToWhenText= (TextView) itemView.findViewById(R.id.tv_date_list_item_own);
            delThisTimer= (Button) itemView.findViewById(R.id.btn_del_one_timer);
            chronometer= (Chronometer) itemView.findViewById(R.id.chronometer_list_item_own);
            chronometerT= (TextView) itemView.findViewById(R.id.textview_list_item_own);
//            swipeMenuLayout= (SwipeMenuLayout) itemView.findViewById(R.id.card_view_main_container);
            cardView= (CardView) itemView.findViewById(R.id.card_view_main_container);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

}
