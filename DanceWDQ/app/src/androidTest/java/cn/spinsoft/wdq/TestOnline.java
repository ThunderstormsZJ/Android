package cn.spinsoft.wdq;

import android.test.InstrumentationTestCase;
import cn.spinsoft.wdq.home.MainParser;
import cn.spinsoft.wdq.utils.Constants;

/**
 * Created by Administrator on 2016-1-29.
 */
public class TestOnline extends InstrumentationTestCase{
    public void test(){
        MainParser.getStartPage();
    }
}
