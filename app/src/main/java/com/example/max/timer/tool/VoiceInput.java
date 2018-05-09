package com.example.max.timer.tool;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by HuXin on 2018/5/9.
 */

public class VoiceInput {

    Context context;

    private static final String TAG = "VoiceInput";

    public VoiceInput(Context context) {
        // 将“12345678”替换成您申请的APPID，申请地址：http://open.voicecloud.cn
        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=5af16548");
        this.context = context;
    }

    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    public void init(final EditText editText) {

        //1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        RecognizerDialog iatDialog = new RecognizerDialog(context, new InitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        //2.设置听写参数，同上节
        iatDialog.setParameter(SpeechConstant.DOMAIN, "iat");
        iatDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        iatDialog.setParameter(SpeechConstant.ACCENT, "mandarin ");
        iatDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
//                        editResult.setText(recognizerResult.getResultString());

                String s = printResult(recognizerResult);
                Log.i(TAG, "onResult: " + s);
                editText.setText(s);

            }

            @Override
            public void onError(SpeechError speechError) {

            }
        });
//4.开始听写
        iatDialog.show();
    }


    private String printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        if (resultBuffer != null) {
            return resultBuffer.toString();
        } else {
            return null;
        }


    }


}
