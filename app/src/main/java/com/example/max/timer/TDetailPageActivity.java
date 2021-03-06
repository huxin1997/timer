package com.example.max.timer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.max.timer.adapter.TimerDetailAdapter;
import com.example.max.timer.bean.GroupBean;
import com.example.max.timer.bean.TimerBean;
import com.example.max.timer.tool.Tool;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TDetailPageActivity extends AppCompatActivity {

    private static final String TAG = "TDetailPageActivity";

    private TextView showAllMan;
    private ImageView backIv,QrIv,MoreIv;
    private LinearLayout llManList, llBottomBox;
    private int width;
    private List<String> list = new ArrayList<>();
    private ListView timerList;
    private List<TimerBean> data = new ArrayList<>();
    private TimerDetailAdapter timerDetailAdapter;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private GroupBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tdetail_page);

        Intent intent = getIntent();
        if(intent==null) finish();
        bean = (GroupBean) intent.getSerializableExtra("bean");
        if(bean ==null) finish();

        width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        if (timerDetailAdapter == null)
            timerDetailAdapter = new TimerDetailAdapter(TDetailPageActivity.this, data);

        initViews();

        initListener();

        initData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewTreeObserver viewTreeObserver = llManList.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = llManList.getWidth();
                Log.e(TAG, width + "");
                if (width >= TDetailPageActivity.this.width) {
                    showAllMan.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initData() {
    }

    private void initListener() {
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        QrIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQR();
            }
        });

        MoreIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void showQR() {
        if (builder == null && alertDialog == null) {
            builder = new AlertDialog.Builder(TDetailPageActivity.this);
            Bitmap bitmap = null;
            BitMatrix result = null;
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                result = multiFormatWriter.encode("{\"group\":"+bean.getId()+"}", BarcodeFormat.QR_CODE, 400, 400);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                bitmap = barcodeEncoder.createBitmap(result);
            } catch (WriterException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
            }
            LinearLayout linearLayout=new LinearLayout(TDetailPageActivity.this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            ImageView imageView = new ImageView(TDetailPageActivity.this);
            imageView.setImageBitmap(bitmap);
            TextView textView=new TextView(TDetailPageActivity.this);
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

    private void initViews() {
        showAllMan = (TextView) findViewById(R.id.detail_show_all_man_in_group);
        backIv = (ImageView) findViewById(R.id.iv_btn_back);
        QrIv=findViewById(R.id.iv_btn_qr_code);
        llManList = (LinearLayout) findViewById(R.id.ll_member_list_container);
        llBottomBox = (LinearLayout) findViewById(R.id.ll_bottom_container_box);
        timerList = (ListView) findViewById(R.id.lv_team_detail_list);
        MoreIv=findViewById(R.id.iv_btn_menu_selector);
    }

}
