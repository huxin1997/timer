package com.example.max.timer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.max.timer.adapter.TimerListAdapter;
import com.example.max.timer.bean.GroupBean;
import com.example.max.timer.bean.TimerBean;
import com.example.max.timer.fragment.GroupListFragment;
import com.example.max.timer.fragment.TimerListFragment;
import com.example.max.timer.service.TimeCheckANetCheckService;
import com.example.max.timer.tool.DBHelper;
import com.example.max.timer.tool.SystemConfig;
import com.example.max.timer.tool.Tool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = "MainActivity";
    private static final String SP_NAME="SystemConfig";

    private FloatingActionButton btnAdd;
    private ImageView btnAddN;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private FragmentManager supportFragmentManager;
    private TimerListFragment timerListFragment;
    private GroupListFragment groupListFragment;
    private DrawerLayout drawerLayout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            setContentView(R.layout.activity_main_n);
        else
            setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        supportFragmentManager = getSupportFragmentManager();
        if (timerListFragment == null)
            timerListFragment = new TimerListFragment();
        supportFragmentManager.beginTransaction().replace(R.id.real_content_container_fragment, timerListFragment).commit();
        initView();
        initFabButton();
//        initService();
        initLogin();
    }

    private void initLogin() {
        sharedPreferences = getSharedPreferences(SP_NAME, MODE_PRIVATE);

    }


    private void initService() {
        startService(new Intent(MainActivity.this, TimeCheckANetCheckService.class));
    }

    private void initFabButton() {
        if (!(Build.VERSION.SDK_INT < Build.VERSION_CODES.N))
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popMenu(v);
                }
            });
        else
            btnAddN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popMenuN();
                }
            });
    }

    private void popMenuN() {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha = 0.95f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(attributes);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_timer_pop_selector, null, false);
        Button personTimer = (Button) view.findViewById(R.id.btn_pop_add_person_timer);
        Button teamTimer = (Button) view.findViewById(R.id.btn_pop_add_team_timer);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        personTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectorTime();
                alertDialog.dismiss();
            }
        });
        teamTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTeamTimer();
                alertDialog.dismiss();
            }
        });
    }

    private void popMenu(View v) {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha = 0.95f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(attributes);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_timer_pop_selector, null, false);
        Button personTimer = (Button) view.findViewById(R.id.btn_pop_add_person_timer);
        Button teamTimer = (Button) view.findViewById(R.id.btn_pop_add_team_timer);
        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setClippingEnabled(false);
        popupWindow.setAnimationStyle(R.style.anim_pop_window);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams attributes = getWindow().getAttributes();
                attributes.alpha = 1f;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(attributes);
            }
        });
        int i = v.getHeight() * 3 + 16;
        Log.e(TAG, i + "");
        popupWindow.showAsDropDown(v, -152, -i);
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

    private void createTeamTimer() {
        startActivityForResult(new Intent(MainActivity.this, TeamTimerActivity.class), SystemConfig.ACTIVITY_TIMER_CREATE_GROUP_ACTIVITY_POST);
    }

    private void selectorTime() {
        startActivityForResult(new Intent(MainActivity.this, TimerCreateActivity.class), SystemConfig.ACTIVITY_TIMER_CREATE_ACTIVITY_POST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case SystemConfig.ACTIVITY_TIMER_CREATE_ACTIVITY_RESULT: {
                TimerBean timerBean = (TimerBean) data.getSerializableExtra("timerBean");
                timerListFragment.addTimer(timerBean);
                Log.e(TAG, timerBean.toString());
                break;
            }
            case SystemConfig.ACTIVITY_TIMER_CREATE_GROUP_ACTIVITY_RESULT: {
                GroupBean group = (GroupBean) data.getSerializableExtra("group");
                Toast.makeText(this, group.getHash() + "->" + group.getName(), Toast.LENGTH_SHORT).show();
                if(groupListFragment==null)
                    groupListFragment=new GroupListFragment();
                groupListFragment.addGroup(group);
                break;
            }
        }
    }

    private void initView() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            btnAddN = findViewById(R.id.btn_ib_add_timer);
        else
            btnAdd = findViewById(R.id.btn_add_timer);
        navigationView = findViewById(R.id.navgation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_btn_one: {
                if (timerListFragment == null)
                    timerListFragment = new TimerListFragment();
                supportFragmentManager.beginTransaction().replace(R.id.real_content_container_fragment, timerListFragment).commit();
                break;
            }
            case R.id.menu_btn_two: {
                if (groupListFragment == null)
                    groupListFragment = new GroupListFragment();
                supportFragmentManager.beginTransaction().replace(R.id.real_content_container_fragment,groupListFragment).commit();
                break;
            }
            case R.id.menu_btn_thr: {

                break;
            }
        }
        drawerLayout.closeDrawer(Gravity.START);
        return false;
    }
}
