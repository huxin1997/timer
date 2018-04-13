package com.example.max.timer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ObbInfo;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.max.timer.bean.TimerBean;
import com.example.max.timer.tool.SystemConfig;
import com.example.max.timer.tool.Tool;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TimerCreateActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText etDate, etTime, etName;
    private int[] dateIntList = new int[3];
    private int[] timeIntList = new int[2];
    private Button creator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_create);

        etDate = (EditText) findViewById(R.id.et_input_time_selector);
        etTime = (EditText) findViewById(R.id.et_input_time_time_selector);
        etName = (EditText) findViewById(R.id.et_input_timer_name);
        creator = (Button) findViewById(R.id.btn_timer_create);

        etDate.setOnClickListener(this);
        etTime.setOnClickListener(this);

        creator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateString = parseDate(dateIntList[0], dateIntList[1], dateIntList[2], timeIntList[0], timeIntList[1]);
                TimerBean timerBean = new TimerBean(Tool.MD5(dateString+etName.getText().toString()), etName.getText().toString(), dateIntList[0], dateIntList[1], dateIntList[2], timeIntList[0], timeIntList[1], dateString);
                Intent intent = new Intent();
                intent.putExtra("timerBean", timerBean);
                setResult(SystemConfig.ACTIVITY_TIMER_CREATE_ACTIVITY_RESULT, intent);
                finish();
            }
        });

    }

    private String parseDate(int y, int m, int d, int h, int M) {
        String y_,m_,d_,h_,M_;
        y_=String.valueOf(y);
        m_ = m<=9?"0"+m:""+m;
        d_ = d<=9?"0"+d:""+d;
        h_ = h<=9?"0"+h:""+h;
        M_ = M<=9?"0"+M:""+M;
        return y_+m_+d_+h_+M_;
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
