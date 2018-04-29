package com.example.max.timer;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

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

    private EditText etDate, etTime, etName;
    private int[] dateIntList = new int[]{-1, -1, -1};
    private int[] timeIntList = new int[]{-1, -1};
    private Button creator, addMember;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_timer);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("创建组");
        toolbar.inflateMenu(R.menu.toolbar_menu_qr_scan);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if(itemId==R.id.toolbar_icon_qr_scan){
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
        addMember = findViewById(R.id.btn_add_member);


        creator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(SystemConfig.ACTIVITY_TIMER_CREATE_ACTIVITY_RESULT, null);
                finish();
            }
        });


        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(progressDialog==null)
//                    progressDialog = ProgressDialog.show(TeamTimerActivity.this, "请稍后...", "正在请求邀请链接~");
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                    }
                }).start();
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null) {
            if(intentResult.getContents() == null) {
                Toast.makeText(this,"扫描失败！请确认是否为邀请入组二维码！",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,"扫描成功！",Toast.LENGTH_LONG).show();
                String ScanResult = intentResult.getContents();
            }
        } else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }


}
