package com.example.max.timer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.TextView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = "MainActivity";
    private static final String SP_NAME = "SystemConfig";

    private FloatingActionButton btnAdd;
    private ImageView btnAddN;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private FragmentManager supportFragmentManager;
    private TimerListFragment timerListFragment;
    private GroupListFragment groupListFragment;
    private DrawerLayout drawerLayout;
    private SharedPreferences sharedPreferences;
    private LinearLayout llMenuHead;
    private boolean loginStatu;
    private TextView headTag;
    private TextView headUsername;

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
        SystemConfig.setContext(getApplicationContext());
        supportFragmentManager = getSupportFragmentManager();
        if (timerListFragment == null)
            timerListFragment = new TimerListFragment();
        if (groupListFragment == null)
            groupListFragment = new GroupListFragment();
        supportFragmentManager.beginTransaction().replace(R.id.real_content_container_fragment, groupListFragment).commit();
        supportFragmentManager.beginTransaction().replace(R.id.real_content_container_fragment, timerListFragment).commit();
        initView();
        initFabButton();
//        initService();
        initListener();
        initLogin();

    }

    private void initListener() {
        llMenuHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!loginStatu){
                    startActivityForResult(new Intent(MainActivity.this,LoginActivity.class),SystemConfig.ACTIVITY_LOGIN_OK_ACTIVITY_POST);
                }
            }
        });
    }

    private void initLogin() {
        sharedPreferences = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        boolean ls = sharedPreferences.getBoolean("LS", false);
        loginStatu = ls;
        if (ls) {
            //todo has be login

            @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if(msg.what==0){
                        headTag.setText(getString(R.string.tv_welcome));
                        headUsername.setText(sharedPreferences.getString("nickname","code:-10"));
                        SystemConfig.CLINT_ID="User"+sharedPreferences.getInt("id",-1);
                        return;
                    }
                    switch (msg.what){
                        case -1:
                            Toast.makeText(MainActivity.this, "登录状态异常！请重新启动！", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case -2:
                            Toast.makeText(MainActivity.this, "意外错误！错误码：-4", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                    }
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.remove("uname");
                    edit.remove("pword");
                    edit.remove("id");
                    edit.remove("LS");
                    edit.apply();
                    edit.commit();
                }
            };

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        loginStatusCheck();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        String message = e.getMessage();
                        if("no_text".equals(message)){
                            handler.sendEmptyMessage(-2);
                        }else if ("ok".equals(message)){
                            handler.sendEmptyMessage(0);
                        }else {
                            handler.sendEmptyMessage(-1);
                        }
                    }
                }
            }).start();
        } else {
            startActivityForResult(new Intent(MainActivity.this,LoginActivity.class),SystemConfig.ACTIVITY_LOGIN_OK_ACTIVITY_POST);
        }
    }

    private void loginStatusCheck() throws IOException, JSONException {
        String uname = sharedPreferences.getString("uname", "");
        String pword = sharedPreferences.getString("pword", "");
        if ("".equals(uname) || "".equals(pword)) {
            throw new IOException("no_text");
        }
        RequestBody rb = RequestBody.create(SystemConfig.JSON, "");
        Request request = new Request.Builder()
                .url("http://118.89.22.131:8080/login?username=" + uname + "&password=" + pword)
                .post(rb)
                .build();
        Response execute = SystemConfig.client.newCall(request).execute();
        String string = execute.body().string();
        Log.e(TAG,string);
        JSONObject jsonObject = new JSONObject(string);
        if (jsonObject.has("code")) {
            int code = jsonObject.getInt("code");
            if (code == -1)
                throw new IOException("error");
            else if(code==0){
                return;
            }
        }else {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            String username = jsonObject.getString("username");
            String nickname = jsonObject.getString("nickname");
            String password = jsonObject.getString("password");
            int id = jsonObject.getInt("id");
            edit.putInt("uid",id);
            edit.putString("uname",username);
            edit.putString("nickname",nickname);
            edit.putString("pword",password);
            edit.putBoolean("LS", true);
            edit.apply();
            edit.commit();
            throw new IOException("ok");
        }
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
        Button personTimer = view.findViewById(R.id.btn_pop_add_person_timer);
        Button teamTimer = view.findViewById(R.id.btn_pop_add_team_timer);
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
        Intent intent = new Intent(MainActivity.this, TimerCreateActivity.class);
        intent.putExtra("fromWhere", SystemConfig.ACTIVITY_CREATE_TIMER_INNER_GROUP_ACTIVITY_POST);
        startActivityForResult(intent, SystemConfig.ACTIVITY_TIMER_CREATE_ACTIVITY_POST);
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
                groupListFragment.addGroup(group);
                break;
            }
            case SystemConfig.ACTIVITY_LOGIN_OK_ACTIVITY_RESULT:{
                headTag.setText(getString(R.string.tv_welcome));
                headUsername.setText(sharedPreferences.getString("nickname","code:-10"));
                loginStatu=true;
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
        llMenuHead= (LinearLayout) navigationView.getHeaderView(0);
        headTag = llMenuHead.findViewById(R.id.tv_tag_head);
        headUsername = llMenuHead.findViewById(R.id.tv_user_name_head);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(!loginStatu){
            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
            AlertDialog alertDialog = builder.setTitle("请登录！")
                    .setMessage("您还没有登录，请登录后使用此功能！")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create();
            alertDialog.show();
            return false;
        }
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
                supportFragmentManager.beginTransaction().replace(R.id.real_content_container_fragment, groupListFragment).commit();
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
