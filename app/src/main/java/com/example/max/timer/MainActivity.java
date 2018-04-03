package com.example.max.timer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private TextView txDate;
    private CalendarView calendar;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initView() {
        final Chronometer time = (Chronometer) findViewById(R.id.chronometer_main);
        Button btnStart = (Button) findViewById(R.id.btn_start);
        txDate = (TextView) findViewById(R.id.tx_date);

        time.setCountDown(true);

        time.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                String s = chronometer.getText().toString();
                if ("00:00".equals(s)) {
                    time.stop();
                    Toast.makeText(MainActivity.this, "时间到了", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final AlertDialog dateDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("设置倒计时")
                        .setView(buildCalendar())
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                time.setBase(SystemClock.elapsedRealtime());
                                time.start();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

    }

    private CalendarView buildCalendar() {
        calendar = new CalendarView(MainActivity.this);
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
