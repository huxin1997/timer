package com.example.max.timer;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.max.timer.adapter.TimerListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private TextView txDate;
    private CalendarView calendar;
    private ListView listView;
    private Button btnAdd;
    private List<HashMap<String, Object>> data;
    private TimerListAdapter timerListAdapter;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();

        initTimerList();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,Object> map=new HashMap<>();
                map.put("toTime",System.currentTimeMillis() + 10 * 1000);
                data.add(map);
                timerListAdapter.notifyDataSetChanged();
            }
        });

    }

    private void initTimerList() {
        if (timerListAdapter == null){
            timerListAdapter = new TimerListAdapter(MainActivity.this, data);
            listView.setAdapter(timerListAdapter);
        }else {
            timerListAdapter.notifyDataSetChanged();
        }

    }

    private void initData() {
        //TODO get http request json form here, then insert data to adapter

        data = new ArrayList<HashMap<String, Object>>();
        Object[] data1 = new Object[]{System.currentTimeMillis() + 10 * 1000, System.currentTimeMillis() + 20 * 1000, System.currentTimeMillis() + 30 * 1000};
        Object[] data2 = new Object[]{};

        for (int k = 0; k < data1.length; k++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("toTime", data1[k]);
//            map.put("Redundance", data2[k]);
            data.add(map);
        }

    }

    private void initView() {
        listView = (ListView) findViewById(R.id.lv_my_timer_list);
        btnAdd = (Button) findViewById(R.id.btn_add_timer);
    }

    private CalendarView buildCalendar() {
        calendar = new CalendarView(MainActivity.this);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Log.i(TAG, "onSelectedDayChange: year " + year + " month " + month + " day " + dayOfMonth);
                txDate.setText(year + "年" + month + "月" + dayOfMonth + "日");

            }
        });
        return calendar;
    }
}
