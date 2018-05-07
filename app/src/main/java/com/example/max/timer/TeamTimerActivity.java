package com.example.max.timer;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.max.timer.bean.GroupBean;
import com.example.max.timer.bean.TimerBean;
import com.example.max.timer.tool.SystemConfig;
import com.example.max.timer.tool.Tool;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeamTimerActivity extends AppCompatActivity {

    private static final String TAG = "TeamTimerActivity";
    

    private EditText etName;
    private Button creator;
    private Toolbar toolbar;


    private ProgressDialog p;
    private Handler handler_;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_timer);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("创建组");
        toolbar.inflateMenu(R.menu.toolbar_menu_qr_scan);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.toolbar_icon_qr_scan) {
                    new IntentIntegrator(TeamTimerActivity.this)
                            .setOrientationLocked(false)
                            .setCaptureActivity(ScanActivity.class)
                            .initiateScan();
                }
                return false;
            }
        });

        etName = findViewById(R.id.et_input_timer_name);
        creator = findViewById(R.id.btn_timer_create);

        handler_ = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int code = msg.arg1;
                p.dismiss();
                if(code==0){
                    Toast.makeText(TeamTimerActivity.this, "加入成功！", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    //todo error
                    Toast.makeText(TeamTimerActivity.this, "加入失败！请重试！", Toast.LENGTH_SHORT).show();
                }
            }
        };

        creator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setResult(SystemConfig.ACTIVITY_TIMER_CREATE_ACTIVITY_RESULT, null);
//                finish();
                final String name = etName.getText().toString();
                if ("".equals(name)) {
                    Toast.makeText(TeamTimerActivity.this, "请填写名称！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (p == null)
                    p = ProgressDialog.show(TeamTimerActivity.this, "请稍后...", "正在获取...", false, false);
                else
                    p.show();

                @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        int id = msg.arg1;
                        if(id==-1){
                            Toast.makeText(TeamTimerActivity.this, "生成失败！", Toast.LENGTH_SHORT).show();
                            p.dismiss();
                            return;
                        }
                        GroupBean groupBean = new GroupBean(name, Tool.MD5(name + System.currentTimeMillis()),id);
                        Intent intent = new Intent();
                        intent.putExtra("group", groupBean);
                        setResult(SystemConfig.ACTIVITY_TIMER_CREATE_GROUP_ACTIVITY_RESULT, intent);
                        p.dismiss();
                        finish();
                    }
                };

                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        RequestBody requestBody = RequestBody.create(SystemConfig.JSON, "{\"groupName\":\"" + name + "\",\"creatorId\":100}");

                        Request request1 = new Request.Builder()
                                .url("http://118.89.22.131:8080/v1/groups")
                                .post(requestBody)
                                .build();
                        int id=-1;
                        try {
                            Response execute = SystemConfig.client.newCall(request1).execute();
                            String rejson = execute.body().string();
                            Log.e(TAG, rejson);
                            JSONObject j=new JSONObject(rejson);
                            id = j.getJSONObject("data").getInt("id");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Message message=new Message();
                        message.arg1=id;
                        handler.sendMessage(message);
                    }
                }).start();

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(this, "扫描失败！无内容！", Toast.LENGTH_LONG).show();
            } else {
                String ScanResult = intentResult.getContents();
                if (p == null)
                    p = ProgressDialog.show(TeamTimerActivity.this, "请稍后...", "正在获取...", false, false);
                else
                    p.show();
                try {
                    JSONObject jsonObject=new JSONObject(ScanResult);
                    boolean group = jsonObject.has("group");
                    if(group){
                        final int gid = jsonObject.getInt("group");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                RequestBody requestBody=RequestBody.create(SystemConfig.JSON,"{\"gid\":"+gid+"}");
                                Request request=new Request.Builder()
                                        .url("http://118.89.22.131:8080/v1/groups/jojn")
                                        .post(requestBody)
                                        .build();
                                try {
                                    Response execute = SystemConfig.client.newCall(request).execute();
                                    String s = execute.body().string();
                                    Log.e(TAG,s);
                                    JSONObject json=new JSONObject(s);
                                    int code = json.getInt("code");
                                    Message message=new Message();
                                    message.arg1=code;
                                    handler_.sendMessage(message);
                                }catch (IOException e){
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }else {
                        Toast.makeText(this, "扫描失败！请确认是否为邀请入组二维码！", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
