package com.example.max.timer;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.max.timer.tool.cn.heshiqian.TextKeyExtract;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.huxin.chronometer", appContext.getPackageName());
    }

    @Test
    public void test(){
        String extract = TextKeyExtract.extract("请明天早上十点10点到1101开会，请不要迟到", TextKeyExtract.KeyType.KEY_TIME_TYPE);
        System.out.println(extract);
    }
}
