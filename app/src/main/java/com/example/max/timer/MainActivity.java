package com.example.max.timer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Chronometer;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView txDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        final Chronometer time = (Chronometer) findViewById(R.id.chronometer_main);
        Button btnStart = (Button) findViewById(R.id.btn_start);
        txDate = (TextView) findViewById(R.id.tx_date);


        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                time.start();

                final AlertDialog dateDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("设置倒计时")
                        .setView(buildCalendar())
                        .setPositiveButton("取消", null)
                        .setNegativeButton("确认", null)
                        .show();
            }
        });

    }

    CalendarView buildCalendar() {
        CalendarView calendar = new CalendarView(MainActivity.this);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Log.i(TAG, "onSelectedDayChange: year " + year + " month " + month + " day " + dayOfMonth);
                txDate.setText(year + "年" + month + "月" + dayOfMonth + "日");

            }
        });

        return calendar;
    }
}
