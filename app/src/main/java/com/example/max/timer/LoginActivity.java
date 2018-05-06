package com.example.max.timer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.max.timer.tool.SystemConfig;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG="LoginActivity";
    private static final String SP_NAME = "SystemConfig";

    private Button login;
    private @SuppressLint("HandlerLeak") Handler handler;
    private ProgressDialog p;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login=findViewById(R.id.sign_in_button);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
//                SharedPreferences.Editor edit = getSharedPreferences(SP_NAME, MODE_PRIVATE).edit();
//                edit.putBoolean("LS",true);
//                edit.apply();
//                edit.commit();
                p.dismiss();
                finish();
            }
        };

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (p == null)
                    p = ProgressDialog.show(LoginActivity.this, "请稍后...", "正在获取...", false, false);
                else
                    p.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RequestBody rb = RequestBody.create(TeamTimerActivity.JSON, "");
                        Request request = new Request.Builder()
                                .url("http://118.89.22.131:8080/login?username=admin&password=123456")
                                .post(rb)
                                .build();
                        try {
                            Response execute = SystemConfig.client.newCall(request).execute();
                            Log.e(TAG, execute.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(0);
                    }
                }).start();
            }
        });

    }
}

