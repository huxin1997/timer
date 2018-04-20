package com.example.max.timer;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.example.max.timer.bean.TimerBean;
import com.example.max.timer.tool.Tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TDetailPageActivity extends AppCompatActivity {

    private static final String TAG = "TDetailPageActivity";

    TextView showAllMan;
    ImageView backIv;
    LinearLayout llManList, llBottomBox;
    private int width;
    List<String> list = new ArrayList<>();
    ListView timerList;
    List<TimerBean> data = new ArrayList<>();
    private TimerDetailAdapter timerDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tdetail_page);

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
        String json = "{\"list\":[{\"name\":\"t1\"},{\"name\":\"t2\"},{\"name\":\"t3\"},{\"name\":\"tyhgyggygy4\"},{\"name\":\"t3\"},{\"name\":\"t4qweqweqwe\"},{\"name\":\"t3qweqweqwe\"},{\"name\":\"t4\"}]}";
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray list = jsonObject.getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                View inflate = LayoutInflater.from(TDetailPageActivity.this).inflate(R.layout.one_textview_layout, llManList, false);
                TextView t = (TextView) inflate.findViewById(R.id.tv_detail_man_name);
                String name = list.getJSONObject(i).getString("name");
                this.list.add(name);
                t.setText(name);
                llManList.addView(t);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //todo get more team timer list
//        ...
        data.add(new TimerBean(Tool.MD5("test"),"aha",2018,4,20,7,10,"201804200710",TimerBean.TYPE_GROUP_TIMER));
        data.add(new TimerBean(Tool.MD5("test"),"aha",2018,4,20,7,10,"201804200710",TimerBean.TYPE_GROUP_TIMER));
        data.add(new TimerBean(Tool.MD5("test"),"aha",2018,4,20,7,10,"201804200710",TimerBean.TYPE_GROUP_TIMER));
        data.add(new TimerBean(Tool.MD5("test"),"aha",2018,4,20,7,10,"201804200710",TimerBean.TYPE_GROUP_TIMER));
        data.add(new TimerBean(Tool.MD5("test"),"aha",2018,4,20,7,10,"201804200710",TimerBean.TYPE_GROUP_TIMER));
        //todo over

        timerList.setAdapter(timerDetailAdapter);

    }

    private void initListener() {
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        showAllMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View inflate = View.inflate(TDetailPageActivity.this, R.layout.show_all_member_list_layout, null);
                ListView listView = (ListView) inflate.findViewById(R.id.lv_all_member);
                listView.setAdapter(new ArrayAdapter<String>(TDetailPageActivity.this, android.R.layout.simple_list_item_1, TDetailPageActivity.this.list));
                AlertDialog.Builder builder = new AlertDialog.Builder(TDetailPageActivity.this);
                builder.setTitle("全部成员")
                        .setView(inflate)
                        .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void initViews() {
        showAllMan = (TextView) findViewById(R.id.detail_show_all_man_in_group);
        backIv = (ImageView) findViewById(R.id.iv_btn_back);
        llManList = (LinearLayout) findViewById(R.id.ll_member_list_container);
        llBottomBox = (LinearLayout) findViewById(R.id.ll_bottom_container_box);
        timerList = (ListView) findViewById(R.id.lv_team_detail_list);
    }

}
