package com.example.max.timer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.max.timer.bean.TimerBean;
import com.example.max.timer.tool.SystemConfig;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DetailPageActivity extends AppCompatActivity {

    private TextView timerNickName, timerId,timerDesc;
    private ImageView ivBack, ivQR;
    private TimerBean bean;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private Chronometer chronometer;
    private SimpleDateFormat sdfParse = new SimpleDateFormat("yyyyMMddHHmm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);

        Intent intent = getIntent();
        if (intent == null) return;

        bean = (TimerBean) intent.getSerializableExtra("bean");

        timerNickName = findViewById(R.id.tv_timer_detail_nickname);
        timerId = findViewById(R.id.tv_timer_detail_id_number);
        ivBack = findViewById(R.id.iv_btn_back);
        ivQR = findViewById(R.id.iv_btn_qr_code);
        timerDesc=findViewById(R.id.tv_desc_person_);
        chronometer=findViewById(R.id.person_detail_chronometer);

        timerNickName.setText(bean.getTimerNickName());
        timerId.setText("");

        String dateString = bean.getDateString();
        Date parse=null;
        try {
            parse = sdfParse.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(parse==null){ Toast.makeText(this, "程序错误！", Toast.LENGTH_SHORT).show();finish();}

        long l = parse.getTime() - System.currentTimeMillis();


        chronometer.setBase(SystemClock.elapsedRealtime()+l);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            chronometer.setCountDown(true);
        }
        chronometer.start();

        timerDesc.setText(bean.getDesc());

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQR();
            }
        });

    }

    private void showQR() {
        if (builder == null && alertDialog == null) {
            builder = new AlertDialog.Builder(DetailPageActivity.this);
            Bitmap bitmap = null;
            BitMatrix result = null;
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                Gson gson=new Gson();
                TimerBean tempBean= (TimerBean) bean.clone();
                tempBean.setTimerNickName(URLEncoder.encode(tempBean.getTimerNickName(),"utf-8"));
                Log.e("json",gson.toJson(tempBean));
                Log.e("json",bean.toString());
                result = multiFormatWriter.encode(gson.toJson(tempBean), BarcodeFormat.QR_CODE, 450, 450);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                bitmap = barcodeEncoder.createBitmap(result);
            } catch (WriterException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            LinearLayout linearLayout=new LinearLayout(DetailPageActivity.this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            ImageView imageView = new ImageView(DetailPageActivity.this);
            imageView.setImageBitmap(bitmap);
            TextView textView=new TextView(DetailPageActivity.this);
            textView.setText("通过软件添加界面扫一扫即可添加！");
            textView.setGravity(Gravity.CENTER);
            linearLayout.addView(imageView);
            linearLayout.addView(textView);
            builder.setView(linearLayout);
            builder.setTitle("扫一扫");
            builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog = builder.create();
            alertDialog.show();
        }else {
            alertDialog.show();
        }
    }

}
