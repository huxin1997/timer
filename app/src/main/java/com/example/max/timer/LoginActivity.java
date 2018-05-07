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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.max.timer.tool.SystemConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String SP_NAME = "SystemConfig";

    private Button login;
    private @SuppressLint("HandlerLeak")
    Handler handler;
    private ProgressDialog p;
    private AutoCompleteTextView actv;
    private EditText pwd;
    private SharedPreferences sharedPreferences;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences(SP_NAME, MODE_PRIVATE);

        login = findViewById(R.id.sign_in_button);
        actv = findViewById(R.id.tv_login_username);
        pwd = findViewById(R.id.tv_login_pwd);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case -2:
                        Toast.makeText(LoginActivity.this, "登录失败！账号或密码错误！", Toast.LENGTH_SHORT).show();
                        p.dismiss();
                        break;
                    case -1:
                        Toast.makeText(LoginActivity.this, "登录失败！错误代码：-1", Toast.LENGTH_SHORT).show();
                        p.dismiss();
                        break;
                    case 0:
                        int id = msg.arg1;
                        String[] o = (String[]) msg.obj;
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putInt("uid",id);
                        edit.putString("uname",o[0]);
                        edit.putString("nickname",o[1]);
                        edit.putString("pword",o[2]);
                        edit.putBoolean("LS", true);
                        edit.apply();
                        edit.commit();
                        p.dismiss();
                        setResult(SystemConfig.ACTIVITY_LOGIN_OK_ACTIVITY_RESULT);
                        finish();
                        break;
                }
            }
        };

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String username = actv.getText().toString();
                final String password = pwd.getText().toString();
                if ("".equals(username) || "".equals(password)) {
                    Toast.makeText(LoginActivity.this, "请填写账号和密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (p == null)
                    p = ProgressDialog.show(LoginActivity.this, "请稍后...", "正在获取...", false, false);
                else
                    p.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RequestBody rb = RequestBody.create(SystemConfig.JSON, "");
                        Request request = new Request.Builder()
                                .url("http://118.89.22.131:8080/login?username="+username+"&password="+password)
                                .post(rb)
                                .build();
                        try {
                            Response execute = SystemConfig.client.newCall(request).execute();
                            String string = execute.body().string();
                            Log.e(TAG, string);
                            JSONObject jsonObject = new JSONObject(string);
                            if (!jsonObject.has("code")) {
                                int id = jsonObject.getInt("id");
                                String username1 = jsonObject.getString("username");
                                String nickname = jsonObject.getString("nickname");
                                String password1 = jsonObject.getString("password");
                                Message message=new Message();
                                message.what=0;
                                message.arg1=id;
                                message.obj=new String[]{username1,nickname,password1};
                                handler.sendMessage(message);
                            } else {
                                handler.sendEmptyMessage(-2);
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(-1);
                        }
                    }
                }).start();
            }
        });

    }
}

