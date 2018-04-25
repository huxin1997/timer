package com.example.max.timer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.max.timer.bean.TimerBean;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.Serializable;

public class DetailPageActivity extends AppCompatActivity {

    private TextView timerNickName, timerId;
    private ImageView ivBack, ivQR;
    private TimerBean bean;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

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

        timerNickName.setText(bean.getTimerNickName());
        timerId.setText(bean.getTimerID());

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
                result = multiFormatWriter.encode(bean.getTimerID(), BarcodeFormat.QR_CODE, 400, 400);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                bitmap = barcodeEncoder.createBitmap(result);
            } catch (WriterException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
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
