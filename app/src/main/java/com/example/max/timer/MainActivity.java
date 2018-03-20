package com.example.max.timer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        final Chronometer time = (Chronometer) findViewById(R.id.chronometer_main);
        Button btnStart = (Button) findViewById(R.id.btn_start);



        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                time.start();


            }
        });


    }
}
