package com.example.max.timer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.max.timer.bean.TimerBean;
import com.example.max.timer.tool.DBHelper;
import com.example.max.timer.tool.SystemConfig;
import com.example.max.timer.tool.Tool;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Date;

public class TimerCreateActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText etDate, etTime, etName;
    private int[] dateIntList = new int[]{-1, -1, -1};
    private int[] timeIntList = new int[]{-1, -1};
    private Button creator;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_create);

        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("创建");
//        toolbar.set
        toolbar.inflateMenu(R.menu.toolbar_menu_qr_scan);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if(itemId==R.id.toolbar_icon_qr_scan){
                    new IntentIntegrator(TimerCreateActivity.this)
                            .setOrientationLocked(false)
                            .setCaptureActivity(ScanActivity.class)
                            .initiateScan();
                }
                return false;
            }
        });

        etDate = findViewById(R.id.et_input_time_selector);
        etTime = findViewById(R.id.et_input_time_time_selector);
        etName = findViewById(R.id.et_input_timer_name);
        creator = findViewById(R.id.btn_timer_create);

        etDate.setOnClickListener(this);
        etTime.setOnClickListener(this);

        creator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int rs : dateIntList) {
                    if (rs == -1) {
                        Toast.makeText(TimerCreateActivity.this, "您日期未选择！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                for (int rs : timeIntList) {
                    if (rs == -1) {
                        Toast.makeText(TimerCreateActivity.this, "您时间未选择！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                String dateString = Tool.parseDate(dateIntList[0], dateIntList[1], dateIntList[2], timeIntList[0], timeIntList[1]);
                TimerBean timerBean = new TimerBean(Tool.MD5(dateString + etName.getText().toString() + System.currentTimeMillis()), etName.getText().toString(), dateIntList[0], dateIntList[1], dateIntList[2], timeIntList[0], timeIntList[1], dateString, TimerBean.TYPE_PRESON_TIMER);
                boolean b = DBHelper.saveTimer2Database(timerBean, TimerCreateActivity.this);
                if (b) {
                    Intent intent = new Intent();
                    intent.putExtra("timerBean", timerBean);
                    setResult(SystemConfig.ACTIVITY_TIMER_CREATE_ACTIVITY_RESULT, intent);
                    finish();
                } else {
                    Toast.makeText(TimerCreateActivity.this, "创建失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null) {
            if(intentResult.getContents() == null) {
                Toast.makeText(this,"内容为空",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,"扫描成功",Toast.LENGTH_LONG).show();
                String ScanResult = intentResult.getContents();
                Toast.makeText(this, ScanResult, Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.et_input_time_selector: {
                showDateSelector();
                break;
            }
            case R.id.et_input_time_time_selector: {
                showTimeSelector();
                break;
            }
        }
    }


    private void showTimeSelector() {
        int hours = new Date().getHours();
        int minutes = new Date().getMinutes();
        TimePickerDialog timePickerDialog = new TimePickerDialog(TimerCreateActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeIntList[0] = hourOfDay;
                timeIntList[1] = minute;
                if (hourOfDay >= 0 && hourOfDay <= 9) {
                    if (minute >= 0 && minute <= 9) {
                        etTime.setText("0" + hourOfDay + ":0" + minute);
                    } else {
                        etTime.setText("0" + hourOfDay + ":" + minute);
                    }
                } else {
                    if (minute >= 0 && minute <= 9) {
                        etTime.setText(hourOfDay + ":0" + minute);
                    } else {
                        etTime.setText(hourOfDay + ":" + minute);
                    }
                }
            }
        }, hours, minutes, true);
        timePickerDialog.show();
    }

    private void showDateSelector() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(TimerCreateActivity.this);
            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    dateIntList[0] = year;
                    dateIntList[1] = month + 1;
                    dateIntList[2] = dayOfMonth;
                    etDate.setText(dateIntList[0] + "-" + dateIntList[1] + "-" + dateIntList[2]);
                }
            });
            datePickerDialog.show();
        } else {
            DatePicker datePicker = new DatePicker(TimerCreateActivity.this);
            datePicker.init(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    dateIntList[0] = year;
                    dateIntList[1] = monthOfYear;
                    dateIntList[2] = dayOfMonth;
                }
            });
            AlertDialog.Builder builder = new AlertDialog.Builder(TimerCreateActivity.this);
            builder.setTitle("设置日期")
                    .setView(datePicker)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            etDate.setText(dateIntList[0] + "-" + dateIntList[1] + "-" + dateIntList[2]);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}
