package com.example.max.timer;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.max.timer.adapter.TimerDetailAdapter;
import com.example.max.timer.bean.GroupBean;
import com.example.max.timer.bean.TimerBean;
import com.example.max.timer.tool.SystemConfig;
import com.example.max.timer.tool.Tool;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TDetailPageActivity extends AppCompatActivity {

    private static final String TAG = "TDetailPageActivity";

    private static String SUB_TOPIC="";

    private TextView showAllMan;
    private ImageView backIv, QrIv, MoreIv;
    private LinearLayout llManList, llBottomBox;
    private int width;
    private List<String> list = new ArrayList<>();
    private ListView timerList;
    private List<TimerBean> data = new ArrayList<>();
    private TimerDetailAdapter timerDetailAdapter;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private GroupBean bean;
    private Handler handler;
    private List<UserBean> userBeanList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tdetail_page);

        Intent intent = getIntent();
        if (intent == null) finish();
        bean = (GroupBean) intent.getSerializableExtra("bean");
        if (bean == null) finish();

        width = getWindow().getWindowManager().getDefaultDisplay().getWidth();

        initViews();


        initListener();

        initData();
        if (timerDetailAdapter == null){
            timerDetailAdapter = new TimerDetailAdapter(TDetailPageActivity.this, data);
            timerList.setAdapter(timerDetailAdapter);
        }

        try {
            initMqtt();
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    private void initMqtt() throws MqttException {
        SUB_TOPIC="groupId"+bean.getId();

    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewTreeObserver viewTreeObserver = llManList.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = llManList.getWidth();
                Log.e(TAG, width + "");
                if (width >= TDetailPageActivity.this.width) {
                    showAllMan.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private void initData() {
        userBeanList = new ArrayList<>();


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1: {
                        renderMemberList();
                        break;
                    }
                    case 2: {
                        break;
                    }
                    case 3: {
                        Toast.makeText(TDetailPageActivity.this, "拉取信息失败！请返回重新进入！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        };

        //init member list data
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url("http://118.89.22.131:8080/v1/groups/" + bean.getId() + "/members")
                        .build();
                try {
                    Response execute = SystemConfig.client.newCall(request).execute();
                    String json = execute.body().string();
                    Log.e(TAG, json);

                    JSONObject j = new JSONObject(json);
                    int code = j.getInt("code");
                    if (code == 0) {
                        JSONObject data = j.getJSONObject("data");
                        JSONArray member = data.getJSONArray("member");
                        userBeanList.clear();
                        for (int i = 0; i < member.length(); i++) {
                            JSONObject rd = member.getJSONObject(i);
                            userBeanList.add(new UserBean(rd.getInt("id"), rd.getString("username"), rd.getString("nickname")));
                        }
                        handler.sendEmptyMessage(1);
                        return;
                    } else
                        handler.sendEmptyMessage(3);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void renderMemberList() {
        if (userBeanList.isEmpty()) {
            Toast.makeText(this, "BUG了！", Toast.LENGTH_SHORT).show();
            return;
        }

        //clear layout all views
        llManList.removeAllViews();

        for (int i = 0; i < userBeanList.size(); i++) {
            View inflate = LayoutInflater.from(SystemConfig.getInstanceContext()).inflate(R.layout.one_textview_layout, llManList, false);
            TextView t = inflate.findViewById(R.id.tv_detail_man_name);
            t.setText(userBeanList.get(i).getNickname());
            llManList.addView(t);
        }

    }

    private void initListener() {
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        QrIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQR();
            }
        });

        MoreIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(TDetailPageActivity.this, MoreIv);
                popupMenu.getMenuInflater().inflate(R.menu.group_more_menu_item, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.btn_add_group_timer:
                                Intent intent = new Intent(TDetailPageActivity.this, TimerCreateActivity.class);
                                intent.putExtra("fromWhere", SystemConfig.ACTIVITY_CREATE_TIMER_INNER_GROUP_ACTIVITY_POST);
                                intent.putExtra("gid",bean);
                                startActivityForResult(intent, SystemConfig.ACTIVITY_CREATE_TIMER_INNER_GROUP_ACTIVITY_POST);
                                break;
                            case R.id.btn_add_group_member:
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case SystemConfig.ACTIVITY_CREATE_TIMER_INNER_GROUP_ACTIVITY_RESULT: {
                TimerBean timerBean = (TimerBean) data.getSerializableExtra("timerBean");
                addTimer2Group(timerBean);
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addTimer2Group(TimerBean timerBean) {
        data.add(timerBean);
        timerDetailAdapter.notifyDataSetChanged();
    }

    private void showQR() {
        if (builder == null && alertDialog == null) {
            builder = new AlertDialog.Builder(TDetailPageActivity.this);
            Bitmap bitmap = null;
            BitMatrix result = null;
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                result = multiFormatWriter.encode("{\"group\":" + bean.getId() + "}", BarcodeFormat.QR_CODE, 400, 400);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                bitmap = barcodeEncoder.createBitmap(result);
            } catch (WriterException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
            }
            LinearLayout linearLayout = new LinearLayout(TDetailPageActivity.this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            ImageView imageView = new ImageView(TDetailPageActivity.this);
            imageView.setImageBitmap(bitmap);
            TextView textView = new TextView(TDetailPageActivity.this);
            textView.setText("通过软件添加界面扫一扫即可添加！");
            textView.setGravity(Gravity.CENTER);
            linearLayout.addView(imageView);
            linearLayout.addView(textView);
            builder.setView(linearLayout);
            builder.setTitle("扫一扫");
            builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog = builder.create();
            alertDialog.show();
        } else {
            alertDialog.show();
        }
    }

    private void initViews() {
        showAllMan = (TextView) findViewById(R.id.detail_show_all_man_in_group);
        backIv = (ImageView) findViewById(R.id.iv_btn_back);
        QrIv = findViewById(R.id.iv_btn_qr_code);
        llManList = (LinearLayout) findViewById(R.id.ll_member_list_container);
        llBottomBox = (LinearLayout) findViewById(R.id.ll_bottom_container_box);
        timerList = (ListView) findViewById(R.id.lv_team_detail_list);
        MoreIv = findViewById(R.id.iv_btn_menu_selector);
    }


    private class UserBean {
        private int id;
        private String username;
        private String nickname;

        @Override
        public String toString() {
            return "UserBean{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", nickname='" + nickname + '\'' +
                    '}';
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public UserBean(int id, String username, String nickname) {

            this.id = id;
            this.username = username;
            this.nickname = nickname;
        }
    }
}
