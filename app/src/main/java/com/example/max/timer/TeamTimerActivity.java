package com.example.max.timer;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

public class TeamTimerActivity extends AppCompatActivity {

    private EditText etName;
    private Button creator;
    private Toolbar toolbar;

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


        creator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setResult(SystemConfig.ACTIVITY_TIMER_CREATE_ACTIVITY_RESULT, null);
//                finish();
                String name = etName.getText().toString();
                if("".equals(name)){
                    Toast.makeText(TeamTimerActivity.this, "请填写名称！", Toast.LENGTH_SHORT).show();
                    return;
                }
                GroupBean groupBean=new GroupBean(name,Tool.MD5(name+System.currentTimeMillis()));
                Intent intent = new Intent();
                intent.putExtra("group",groupBean);
                setResult(SystemConfig.ACTIVITY_TIMER_CREATE_GROUP_ACTIVITY_RESULT,intent);
                finish();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(this, "扫描失败！请确认是否为邀请入组二维码！", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "扫描成功！", Toast.LENGTH_LONG).show();
                String ScanResult = intentResult.getContents();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
