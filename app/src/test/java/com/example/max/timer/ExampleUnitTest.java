package com.example.max.timer;

import android.util.Log;

import com.example.max.timer.tool.cn.heshiqian.TextKeyExtract;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test(){
        String extract = TextKeyExtract.extract("十一点到1101开会，请不要迟到", TextKeyExtract.KeyType.KEY_TIME_TYPE);
        System.out.println(extract);
    }

    @Test
    public void t2() throws ParseException {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS+SSSS");
        Date date = simpleDateFormat.parse("2018-05-07T13:08:06.000+0000");
        System.out.println(date.getTime()+"");
    }
}