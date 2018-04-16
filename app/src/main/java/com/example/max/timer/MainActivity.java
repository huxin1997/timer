package com.example.max.timer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.example.max.timer.bean.TimerBean;
import com.example.max.timer.tool.SystemConfig;
import com.example.max.timer.tool.Tool;

import java.io.Serializable;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private RecyclerView listView;
    private FloatingActionButton btnAdd;
    private List<TimerBean> data;
    private TimerListAdapter timerListAdapter;
    private View[] views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        views = Tool.buildTimePickView(MainActivity.this);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

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
                attributes.alpha=0.95f;
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
                        createTeamTimer();
                        popupWindow.dismiss();
                    }
                });
            }
        });
    }

    private void createTeamTimer() {
        startActivityForResult(new Intent(MainActivity.this,TeamTimerActivity.class), SystemConfig.ACTIVITY_TIMER_CREATE_GROUP_ACTIVITY_POST);
    }
    private void selectorTime(){
        startActivityForResult(new Intent(MainActivity.this,TimerCreateActivity.class), SystemConfig.ACTIVITY_TIMER_CREATE_ACTIVITY_POST);
    }


    private void initTimerList() {
        timerListAdapter = new TimerListAdapter(MainActivity.this, data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        listView.setAdapter(timerListAdapter);
        listView.setLayoutManager(linearLayoutManager);

        timerListAdapter.setOnItemClickListener(new TimerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(MainActivity.this,TDetailPageActivity.class));
            }
        });
        timerListAdapter.notifyDataSetChanged();

    }

    private void initData() {
        //TODO get http request json form here, then insert data to adapter

        data=new ArrayList<>();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case SystemConfig.ACTIVITY_TIMER_CREATE_ACTIVITY_RESULT:{
                TimerBean timerBean = (TimerBean) data.getSerializableExtra("timerBean");
                this.data.add(timerBean);
                timerListAdapter.notifyDataSetChanged();
                Log.e(TAG,timerBean.toString());
                break;
            }
        }
    }

    private void initView() {
        listView = (RecyclerView) findViewById(R.id.recycler_view_timer_list);
        btnAdd = (FloatingActionButton) findViewById(R.id.btn_add_timer);
    }

}
