package com.example.max.timer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.max.timer.bean.GroupBean;
import com.example.max.timer.bean.TimerBean;
import com.example.max.timer.fragment.GroupListFragment;
import com.example.max.timer.fragment.TimerListFragment;
import com.example.max.timer.service.TimeCheckANetCheckService;
import com.example.max.timer.tool.DBHelper;
import com.example.max.timer.tool.SystemConfig;
import com.example.max.timer.tool.Tool;
import com.example.max.timer.tool.VoiceInput;
import com.example.max.timer.tool.cn.heshiqian.TextKeyExtract;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = "MainActivity";
    private static final String SP_NAME = "SystemConfig";

    private FloatingActionMenu btnAdd;
    private FloatingActionButton btnAddTimer, btnAddGroup, btnTextInput, btnVoiceInput;
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
    private static TimeCheckANetCheckService service;
    private ServiceConnection serviceConnection;
    private SimpleDateFormat sdfParse = new SimpleDateFormat("yyyyMMddHHmm");
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
//            setContentView(R.layout.activity_main_n);
//        else
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
        initListener();
        initLogin();
        initService();

    }

    private void initListener() {
        llMenuHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loginStatu) {
                    startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), SystemConfig.ACTIVITY_LOGIN_OK_ACTIVITY_POST);
                }
            }
        });
    }

    private void cleanLoginInfo() {

        if (!loginStatu) return;

        final ProgressDialog show = ProgressDialog.show(MainActivity.this, "请稍后...", "请求中...", false, false);
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        Log.e(TAG, "normal logout");
                    case 2:
                        Toast.makeText(MainActivity.this, "注销成功！请重启App后使用！", Toast.LENGTH_SHORT).show();
                        loginStatu = false;
                        headTag.setText(getString(R.string.tv_welcome));
                        headUsername.setText("");
                        break;
                    case -1:
                        Toast.makeText(MainActivity.this, "注销失败！需要重启App！", Toast.LENGTH_SHORT).show();
                        break;
                }
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.remove("uname");
                edit.remove("pword");
                edit.remove("id");
                edit.remove("LS");
                edit.apply();
                edit.commit();
                show.dismiss();
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url("http://118.89.22.131:8080/logout")
                        .build();
                try {
                    Response execute = SystemConfig.client.newCall(request).execute();
                    String string = execute.body().string();
                    JSONObject jsonObject = new JSONObject(string);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        handler.sendEmptyMessage(1);
                    } else {
                        handler.sendEmptyMessage(2);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(-1);
                }
            }
        }).start();
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
                    if (msg.what == 0) {
                        headTag.setText(getString(R.string.tv_welcome));
                        headUsername.setText(sharedPreferences.getString("nickname", "code:-10"));
                        SystemConfig.CLINT_ID = "User" + sharedPreferences.getInt("uid", -1);
                        return;
                    }
                    switch (msg.what) {
                        case -1:
                            Toast.makeText(MainActivity.this, "登录状态异常！请重新启动！", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case -2:
                            Toast.makeText(MainActivity.this, "意外错误！错误码：-4", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                    }
                    cleanLoginInfo();
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
                        if ("no_text".equals(message)) {
                            handler.sendEmptyMessage(-2);
                        } else if ("ok".equals(message)) {
                            handler.sendEmptyMessage(0);
                        } else if ("was login".equals(message)) {
                            handler.sendEmptyMessage(0);
                        } else {
                            handler.sendEmptyMessage(-1);
                        }
                    }
                }
            }).start();
        } else {
//            startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), SystemConfig.ACTIVITY_LOGIN_OK_ACTIVITY_POST);
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
        Log.e(TAG, string);
        JSONObject jsonObject = new JSONObject(string);
        if (jsonObject.has("code")) {
            int code = jsonObject.getInt("code");
            if (code == -1)
                throw new IOException("error");
            else if (code == 0) {
                throw new IOException("was login");
            }
        } else {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            String username = jsonObject.getString("username");
            String nickname = jsonObject.getString("nickname");
            String password = jsonObject.getString("password");
            int id = jsonObject.getInt("id");
            edit.putInt("uid", id);
            edit.putString("uname", username);
            edit.putString("nickname", nickname);
            edit.putString("pword", password);
            edit.putBoolean("LS", true);
            edit.apply();
            edit.commit();
            throw new IOException("ok");
        }
    }

    private void initService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                service = ((TimeCheckANetCheckService.TCNCBinder) iBinder).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                service = null;
            }
        };
        Intent intent = new Intent(this, TimeCheckANetCheckService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    private void initFabButton() {
        btnAdd.setClosedOnTouchOutside(true);

        btnAddTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectorTime();
                btnAdd.close(false);
            }
        });

        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTeamTimer();
                btnAdd.close(false);
            }
        });

        btnTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popMenuL();

                btnAdd.close(false);
            }
        });

        // TODO: 2018/5/9  语音输入
        btnVoiceInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAdd.close(true);
                popMenuL();
                VoiceInput voiceInput = new VoiceInput(MainActivity.this);
                voiceInput.init(editText);
            }
        });


//        if (!(Build.VERSION.SDK_INT < Build.VERSION_CODES.N)){
//            btnAdd.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    RotateAnimation rotateAnimation = new RotateAnimation(0, 365 + 45, Animation.RELATIVE_TO_PARENT, 0.2F, Animation.RELATIVE_TO_PARENT, 0.2F);
//                    rotateAnimation.setFillAfter(true);
//                    rotateAnimation.setDuration(0);
//                    rotateAnimation.setDetachWallpaper(true);
//                    btnAdd.setAnimation(rotateAnimation);
//                    rotateAnimation.start();
//                    popMenu(v);
//                }
//            });
//            btnAdd.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    popMenuL(view);
//                    return true;
//                }
//            });
//        }
//        else
//            btnAddN.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    popMenuN();
//                }
//            });
    }

    private void popMenuL() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View inflate = View.inflate(MainActivity.this, R.layout.dialog_paste_text_layout, null);
        editText = inflate.findViewById(R.id.et_input_paste_text);
        AlertDialog alertDialog = builder.setView(inflate)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String str = editText.getText().toString();
                        Toast.makeText(MainActivity.this, "" + str, Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                        String extract = TextKeyExtract.extract(str, TextKeyExtract.KeyType.KEY_TIME_TYPE);
                        try {
                            Date parse = sdfParse.parse(extract);
                            Log.e(TAG, String.valueOf(parse.getTime()));
                            TimerBean timerBean = new TimerBean();
                            timerBean.setTimerNickName("我的倒计时");
                            timerBean.setDateString(extract);
                            timerBean.setTimerType(TimerBean.TYPE_PRESON_TIMER);
                            timerBean.setMinute(parse.getMinutes());
                            timerBean.setHour(parse.getHours());
                            timerBean.setDay(parse.getDate() + 1);
                            timerBean.setMonth(parse.getMonth());
                            timerBean.setYear(parse.getYear() + 1900);
                            timerBean.setTimerID(Tool.MD5(extract + "我的倒计时" + System.currentTimeMillis()));
                            boolean b = DBHelper.saveTimer2Database(timerBean, MainActivity.this);
                            if (b) {
                                timerListFragment.addTimer(timerBean);
                            } else {
                                Toast.makeText(MainActivity.this, "添加失败！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setTitle("识别")
                .create();
        alertDialog.show();
    }


    private void createTeamTimer() {
        startActivityForResult(new Intent(MainActivity.this, TeamTimerActivity.class), SystemConfig.ACTIVITY_TIMER_CREATE_GROUP_ACTIVITY_POST);
    }

    private void selectorTime() {
        Intent intent = new Intent(MainActivity.this, TimerCreateActivity.class);
        intent.putExtra("fromWhere", SystemConfig.ACTIVITY_TIMER_CREATE_ACTIVITY_POST);
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
            case SystemConfig.ACTIVITY_LOGIN_OK_ACTIVITY_RESULT: {
                headTag.setText(getString(R.string.tv_welcome));
                headUsername.setText(sharedPreferences.getString("nickname", "code:-10"));
                loginStatu = true;
                break;
            }
        }
    }

    private void initView() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
//            btnAddN = findViewById(R.id.btn_ib_add_timer);
//        else
        btnAdd = findViewById(R.id.btn_add_timer);
        btnAddTimer = findViewById(R.id.fab_add_person);
        btnAddGroup = findViewById(R.id.fab_add_group);
        btnTextInput = findViewById(R.id.fab_input_text);
        btnVoiceInput = findViewById(R.id.fab_input_voice);
        navigationView = findViewById(R.id.navgation_view);
        navigationView.setNavigationItemSelectedListener(this);
        llMenuHead = (LinearLayout) navigationView.getHeaderView(0);
        headTag = llMenuHead.findViewById(R.id.tv_tag_head);
        headUsername = llMenuHead.findViewById(R.id.tv_user_name_head);
    }

    private void popLoginTip() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                if (!loginStatu) {
                    popLoginTip();
                    return true;
                }
                if (groupListFragment == null)
                    groupListFragment = new GroupListFragment();
                supportFragmentManager.beginTransaction().replace(R.id.real_content_container_fragment, groupListFragment).commit();
                break;
            }
            case R.id.menu_btn_thr: {

                break;
            }
            case R.id.menu_btn_for: {
                if (!loginStatu) {
                    popLoginTip();
                    return true;
                }
                cleanLoginInfo();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            }
            case R.id.menu_btn_fiv: {
                unbindService(serviceConnection);
                finish();
                System.exit(0);
                break;
            }
        }
        drawerLayout.closeDrawer(Gravity.START);
        return true;
    }


    public static TimeCheckANetCheckService getService() {
        return service;
    }
}
