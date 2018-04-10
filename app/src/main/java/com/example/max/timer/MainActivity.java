package com.example.max.timer;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.max.timer.adapter.TimerListAdapter;
import com.example.max.timer.tool.Tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private TextView txDate;
    private RecyclerView listView;
    private FloatingActionButton btnAdd;
    private List<HashMap<String, Object>> data;
    private TimerListAdapter timerListAdapter;
    private int[] selectDateTempStorage = new int[3];
    private AlertDialog.Builder builder;
    private View[] views;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initView();
//
        initData();
//
        initTimerList();

        initFabButton();

    }

    private void initFabButton() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WindowManager.LayoutParams attributes = getWindow().getAttributes();
                attributes.alpha=0.3f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(attributes);
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_timer_pop_selector,null,false);
                Button personTimer= (Button) view.findViewById(R.id.btn_pop_add_person_timer);
                Button teamTimer= (Button) view.findViewById(R.id.btn_pop_add_team_timer);
                final PopupWindow popupWindow=new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,true);
                popupWindow.setAnimationStyle(R.style.anim_pop_window);
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        WindowManager.LayoutParams attributes = getWindow().getAttributes();
                        attributes.alpha=1f;
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        getWindow().setAttributes(attributes);
                    }
                });
                popupWindow.showAsDropDown(v,-110,-460);
                personTimer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectorTime();
                        popupWindow.dismiss();
                    }
                });
                teamTimer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        });
    }

    private void selectorTime(){
        View[] views = Tool.buildTimePickView(MainActivity.this);
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setView(views[0]);
        AlertDialog alertDialog = builder.setTitle("设置时间").create();
        alertDialog.show();
    }


    private void initTimerList() {
        timerListAdapter = new TimerListAdapter(MainActivity.this, data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        listView.setAdapter(timerListAdapter);
        listView.setLayoutManager(linearLayoutManager);
        timerListAdapter.notifyDataSetChanged();
    }

    private void initData() {
        //TODO get http request json form here, then insert data to adapter

        data = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> map = new HashMap<>();
        map.put("timerToWhenLong", SystemClock.elapsedRealtime() + 20 * 60 * 1000);
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("timerToWhenLong", SystemClock.elapsedRealtime() + 30 * 60 * 1000);

        data.add(map);
        data.add(map1);

    }

    private void initView() {
        listView = (RecyclerView) findViewById(R.id.recycler_view_timer_list);
        btnAdd = (FloatingActionButton) findViewById(R.id.btn_add_timer);
    }


}
