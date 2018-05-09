package com.example.max.timer;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.max.timer.bean.GroupBean;
import com.example.max.timer.bean.TimerBean;
import com.example.max.timer.service.TimeCheckANetCheckService;
import com.example.max.timer.tool.DBHelper;
import com.example.max.timer.tool.SystemConfig;
import com.example.max.timer.tool.Tool;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TimerCreateActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TimerCreateActivity";

    private EditText etDate, etTime, etName, etDesc;
    private int[] dateIntList = new int[]{-1, -1, -1};
    private int[] timeIntList = new int[]{-1, -1};
    private SimpleDateFormat sdfParse = new SimpleDateFormat("yyyyMMddHHmm");
    private Button creator;
    private DBHelper dbHelper;
    private int fromWhere;
    private int gid;
    private GroupBean groupBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_create);

        Intent intent = getIntent();
        if (intent == null) return;
        fromWhere = intent.getIntExtra("fromWhere", -1);
        if (fromWhere == SystemConfig.ACTIVITY_CREATE_TIMER_INNER_GROUP_ACTIVITY_POST) {
            groupBean = (GroupBean) intent.getSerializableExtra("gid");
            gid = groupBean.getId();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("创建");
//        toolbar.set
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.inflateMenu(R.menu.toolbar_menu_qr_scan);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.toolbar_icon_qr_scan) {
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
        etDesc = findViewById(R.id.et_timer_desc);

        etDesc.setOnClickListener(this);
        etDate.setOnClickListener(this);
        etTime.setOnClickListener(this);

        etDesc.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                etDesc.setText("");
                Toast.makeText(TimerCreateActivity.this, "已清空！", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

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
                final TimerBean timerBean = new TimerBean(Tool.MD5(dateString + etName.getText().toString() + System.currentTimeMillis()), etName.getText().toString(), dateIntList[0], dateIntList[1], dateIntList[2], timeIntList[0], timeIntList[1], dateString, TimerBean.TYPE_PRESON_TIMER,etDesc.getText().toString());

                switch (fromWhere) {
                    case SystemConfig.ACTIVITY_CREATE_TIMER_INNER_GROUP_ACTIVITY_POST: {

                        final ProgressDialog progressDialog = ProgressDialog.show(TimerCreateActivity.this, "请稍后...", "请求中...", false, false);
                        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                progressDialog.dismiss();
                                switch (msg.what) {
                                    case -2:
                                        Toast.makeText(TimerCreateActivity.this, "创建失败！请重试！", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1:
                                        boolean b = DBHelper.saveTimer2Database(groupBean, timerBean, TimerCreateActivity.this);
                                        if(b){
                                            Intent intent = new Intent();
                                            intent.putExtra("timerBean", timerBean);
                                            setResult(SystemConfig.ACTIVITY_CREATE_TIMER_INNER_GROUP_ACTIVITY_RESULT, intent);
                                            finish();
                                        }else {
                                            Toast.makeText(TimerCreateActivity.this, "创建失败！", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                }
                            }
                        };
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                RequestBody requestBody = null;
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("timerName", timerBean.getTimerNickName())
                                            .put("creatorId", 1)
                                            .put("createTime", System.currentTimeMillis())
                                            .put("expireTime", sdfParse.parse(timerBean.getDateString()).getTime())
                                            .put("notifyType", 0)
                                            .put("groupId", gid);
                                    requestBody = RequestBody.create(SystemConfig.JSON, jsonObject.toString());
                                } catch (ParseException | JSONException e) {
                                    e.printStackTrace();
                                    handler.sendEmptyMessage(-2);
                                }
                                Request request = new Request.Builder()
                                        .post(requestBody)
                                        .url("http://118.89.22.131:8080/v1/timers")
                                        .build();
                                try {
                                    Response execute = SystemConfig.client.newCall(request).execute();
                                    String string = execute.body().string();
                                    Log.e(TAG, string);
                                    JSONObject jb = new JSONObject(string);
                                    int code = jb.getInt("code");
                                    if (code == 0)
                                        handler.sendEmptyMessage(1);
                                    else
                                        handler.sendEmptyMessage(-2);
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                    handler.sendEmptyMessage(-2);
                                }
                            }
                        }).start();
                        break;
                    }
                    case SystemConfig.ACTIVITY_TIMER_CREATE_ACTIVITY_POST: {
                        boolean b = DBHelper.saveTimer2Database(timerBean, TimerCreateActivity.this);
                        if (b) {
                            Intent intent = new Intent();
                            intent.putExtra("timerBean", timerBean);
                            setResult(SystemConfig.ACTIVITY_TIMER_CREATE_ACTIVITY_RESULT, intent);
                            finish();
                            break;
                        } else {
                            Toast.makeText(TimerCreateActivity.this, "创建失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(this, "内容为空", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "扫描成功", Toast.LENGTH_LONG).show();
                String ScanResult = intentResult.getContents();
                try {
                    JSONObject jsonObject = new JSONObject(ScanResult);
                    Log.e("JB", jsonObject.toString());
                    String timerNickName = URLDecoder.decode(jsonObject.getString("timerNickName"), "utf-8");
                    dateIntList[0] = jsonObject.getInt("year");
                    dateIntList[1] = jsonObject.getInt("month");
                    dateIntList[2] = jsonObject.getInt("day");
                    timeIntList[0] = jsonObject.getInt("hour");
                    timeIntList[1] = jsonObject.getInt("minute");
                    String dateString = Tool.parseDate(dateIntList[0], dateIntList[1], dateIntList[2], timeIntList[0], timeIntList[1]);
                    TimerBean timerBean = new TimerBean(jsonObject.getString("timerID"), timerNickName, dateIntList[0], dateIntList[1], dateIntList[2], timeIntList[0], timeIntList[1], dateString, TimerBean.TYPE_PRESON_TIMER,etDesc.getText().toString());
                    if (timeIntList[0] >= 0 && timeIntList[0] <= 9) {
                        if (timeIntList[1] >= 0 && timeIntList[1] <= 9) {
                            etTime.setText("0" + timeIntList[0] + ":0" + timeIntList[1]);
                        } else {
                            etTime.setText("0" + timeIntList[0] + ":" + timeIntList[1]);
                        }
                    } else {
                        if (timeIntList[1] >= 0 && timeIntList[1] <= 9) {
                            etTime.setText(timeIntList[0] + ":0" + timeIntList[1]);
                        } else {
                            etTime.setText(timeIntList[0] + ":" + timeIntList[1]);
                        }
                    }
                    etDate.setText(dateIntList[0] + "-" + dateIntList[1] + "-" + dateIntList[2]);
                    etName.setText(timerNickName);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
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
            case R.id.et_timer_desc: {
                String oldStr = etDesc.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(TimerCreateActivity.this);
                View inflate = View.inflate(TimerCreateActivity.this, R.layout.dialog_desc_input_layout, null);
                final EditText editText=inflate.findViewById(R.id.et_desc_input);
                editText.setText(oldStr);
                AlertDialog alertDialog = builder.setTitle("输入倒计时描述")
                        .setView(inflate)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String desc = editText.getText().toString();
                                Log.e(TAG, desc);
                                etDesc.setText(desc);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                alertDialog.show();
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
