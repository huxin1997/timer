package com.example.max.timer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.max.timer.bean.TimerBean;

import java.io.Serializable;

public class DetailPageActivity extends AppCompatActivity {

    private TextView timerNickName,timerId;
    private ImageView ivBack,ivQR;
    private TimerBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);

        Intent intent = getIntent();
        if(intent==null) return;

        bean = (TimerBean) intent.getSerializableExtra("bean");

        timerNickName=findViewById(R.id.tv_timer_detail_nickname);
        timerId=findViewById(R.id.tv_timer_detail_id_number);
        ivBack=findViewById(R.id.iv_btn_back);

        timerNickName.setText(bean.getTimerNickName());
        timerId.setText(bean.getTimerID());

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
