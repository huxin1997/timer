package com.example.max.timer.adapter;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.max.timer.R;
import com.example.max.timer.bean.TimerBean;
import com.example.max.timer.service.TimeCheckANetCheckService;
import com.example.max.timer.tool.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 贺石骞 on 2018/4/3.
 */

public class TimerListAdapter extends RecyclerView.Adapter<TimerListAdapter.TimerViewHolder> {

    private static final String TAG = "TimerListAdapter";
    private static final long[] patter = {0, 200, 300, 200, 300 ,500};

    private List<HashMap<String,Object>> data;
    private List<TimerBean> data_;
    private Context mContext;
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    private SimpleDateFormat sdfParse=new SimpleDateFormat("yyyyMMddHHmm");
    private SimpleDateFormat diyTime=new SimpleDateFormat("mm:ss");
    private Date parse;
    private OnItemClickListener mOnItemClickListener;
    private Toast apiLevelWarning;
    private List<Timer> timers=new ArrayList<>();
    private HashMap<String,Long> timeIntList=new HashMap<>();
    private Vibrator vibrator;

    public TimerListAdapter(Context context, List<TimerBean> data_) {
        this.data_=data_;
        mContext=context;
        vibrator = (Vibrator)mContext.getSystemService(mContext.VIBRATOR_SERVICE);
    }

    @Override
    public TimerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_timer_item, parent, false);
        TimerViewHolder timerViewHolder=new TimerViewHolder(inflate);
        return timerViewHolder;
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(final TimerViewHolder holder, final int position) {

        final TimerViewHolder holder_ = holder;

        if(mOnItemClickListener != null){
            //为ItemView设置监听器
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
        timeIntList.put(timerBean.getTimerID(),l);
        final int ceil = (int) Math.ceil(l /1000 / 60 / 60);
        if (ceil<24) {
            holder.chronometerT.setVisibility(View.GONE);
            holder.chronometer.setVisibility(View.VISIBLE);
            holder.timerToWhenText.setText(sdf.format(parse.getTime()));
            //API等级判断
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.chronometer.setCountDown(true);
                holder.chronometer.setBase(SystemClock.elapsedRealtime() + l);
                holder.chronometer.start();
                holder.chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                    @Override
                    public void onChronometerTick(Chronometer chronometer) {
                        String s = chronometer.getText().toString();
                        holder_.chronometer.setFormat("%s");

                    }
                });
            }else {
                if(apiLevelWarning==null) {
                    apiLevelWarning = Toast.makeText(mContext, "新版控件不支持你的系统版本！即将使用其他控件", Toast.LENGTH_SHORT);
                    apiLevelWarning.show();
                }

                holder.chronometerT.setVisibility(View.VISIBLE);
                holder.chronometer.setVisibility(View.GONE);

//                @SuppressLint("HandlerLeak") final Handler handler=new Handler(){
//                    @Override
//                    public void handleMessage(Message msg) {
//                        super.handleMessage(msg);
//                        Long aLong = timeIntList.get(data_.get(position).getTimerID());
//                        aLong-=1000;
//                        Date date = new Date(aLong);
//                        if(aLong<1000){
//                            holder_.chronometerT.setText("时间已过");
//                            timers.get(position).cancel();
//                            timers.remove(position);
//                            notifyDataSetChanged();
//                            return;
//                        }else {
//                            holder_.chronometerT.setText(date.getHours()+":"+date.getMinutes()+":"+date.getSeconds());
//                        }
//                        notifyItemChanged(position,0);
//                    }
//                };
//                Timer timer=new Timer();
//                TimerTask timerTask=new TimerTask() {
//                    @Override
//                    public void run() {
//                        handler.sendEmptyMessage(0);
//                    }
//                };
//                timer.scheduleAtFixedRate(timerTask,0,1000);
//                timers.add(timer);
            }
        } else {
            holder.chronometer.setVisibility(View.GONE);
            holder.chronometerT.setVisibility(View.VISIBLE);
            int allDay = (int)Math.ceil(l /1000 / 60 / 60 / 24);
            holder.timerToWhenText.setText(sdf.format(parse.getTime()));
            holder.chronometerT.setText("还有"+allDay+"天");
        }
        holder.delThisTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                builder.setMessage("确认删除吗？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                boolean b = DBHelper.delTimer4DB(mContext, data_.get(position).getTimerID());
                                if(b){
                                    if(!timers.isEmpty()) {
                                        timers.get(position);
                                        timers.remove(position);
                                    }
                                    data_.remove(position);
                                    notifyDataSetChanged();
                                    Toast.makeText(mContext, "删除成功！", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(mContext, "删除失败！", Toast.LENGTH_SHORT).show();
                                }
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void notify_(String s, TimerViewHolder holder_){
        if(s.equals("1:00:00")){
            vibrator.vibrate(patter, -1);
            Notification.Builder builder=new Notification.Builder(mContext);
            builder.setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("倒计时提示！")
                    .setContentText("您的倒计时还有一小时！");
            NotificationManager manager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                manager.notify(1,builder.build());
            }
        }
        if(s.equals("30:00")){
            vibrator.vibrate(patter, -1);
            Notification.Builder builder=new Notification.Builder(mContext);
            builder.setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("倒计时提示！")
                    .setContentText("您的倒计时还有30分钟！");
            NotificationManager manager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                manager.notify(2,builder.build());
            }
        }
        if(s.equals("15:00")){
            vibrator.vibrate(patter, -1);
            Notification.Builder builder=new Notification.Builder(mContext);
            builder.setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("倒计时提示！")
                    .setContentText("您的倒计时还有15分钟！");
            NotificationManager manager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                manager.notify(3,builder.build());
            }
        }
        if(s.equals("5:00")){
            vibrator.vibrate(patter, -1);
            Notification.Builder builder=new Notification.Builder(mContext);
            builder.setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("倒计时提示！")
                    .setContentText("您的倒计时还有5分钟！");
            NotificationManager manager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                manager.notify(4,builder.build());
            }
        }
        if(s.equals("00:00")||s.indexOf("−")!=-1){
            holder_.chronometer.stop();
            holder_.chronometer.setOnChronometerTickListener(null);
            holder_.chronometer.setVisibility(View.GONE);
            holder_.chronometerT.setText("时间已到！");
            holder_.chronometerT.setVisibility(View.VISIBLE);
        }
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
