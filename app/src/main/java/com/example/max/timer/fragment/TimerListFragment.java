package com.example.max.timer.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.max.timer.DetailPageActivity;
import com.example.max.timer.R;
import com.example.max.timer.TDetailPageActivity;
import com.example.max.timer.adapter.TimerListAdapter;
import com.example.max.timer.bean.TimerBean;
import com.example.max.timer.tool.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimerListFragment extends Fragment {

    private List<TimerBean> data;
    private View view;
    private RecyclerView recyclerView;
    private TimerListAdapter timerListAdapter;
    private SimpleDateFormat sdfParse = new SimpleDateFormat("yyyyMMddHHmm");
    private LinearLayout llMainTips;


    public TimerListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view==null)
            view = inflater.inflate(R.layout.real_content_layout, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_timer_list);
        llMainTips=view.findViewById(R.id.ll_main_page_tips);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        timerListAdapter = new TimerListAdapter(getContext(),data);
        timerListAdapter.setOnItemClickListener(new TimerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (data.get(position).getTimerType() == TimerBean.TYPE_GROUP_TIMER) {
                    Intent intent = new Intent(getContext(), TDetailPageActivity.class);
                    intent.putExtra("bean",data.get(position));
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getContext(), DetailPageActivity.class);
                    intent.putExtra("bean",data.get(position));
                    startActivity(intent);
                }
            }
        });
        timerListAdapter.setOnDataEmptyListener(new TimerListAdapter.OnDataIsNothingListener() {
            @Override
            public void dataEmpty(boolean isEmpty) {
                if(isEmpty){
                    showTip();
                }
            }
        });
        recyclerView.setAdapter(timerListAdapter);
    }

    private void initData() {
        //TODO get http request json form here, then insert data to adapter

        data = new ArrayList<>();


        //TODO get data from database
        List<TimerBean> timerBeans = DBHelper.readTimer4Database(getContext(),DBHelper.TYPE_PERSON);
        data.addAll(timerBeans);

        sortTimer();

        showTip();

    }

    private void showTip(){
        if(data.isEmpty()){
            llMainTips.setVisibility(View.VISIBLE);
        }else {
            llMainTips.setVisibility(View.GONE);
        }
    }

    private void sortTimer(){
        Collections.sort(data, new Comparator<TimerBean>() {
            @Override
            public int compare(TimerBean timerBean, TimerBean t1) {
                String dateString = timerBean.getDateString();
                String dateString1 = t1.getDateString();
                Date parse=new Date();
                Date parse1=new Date();
                try {
                    parse = sdfParse.parse(dateString);
                    parse1 = sdfParse.parse(dateString1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Long l1=parse.getTime();
                Long l2=parse1.getTime();

                return l1.compareTo(l2);
            }
        });
    }

    public void notifyData(){
        timerListAdapter.notifyDataSetChanged();
        showTip();
    }

    public void addTimer(TimerBean bean){
        data.add(bean);
        sortTimer();
        notifyData();
    }

}
