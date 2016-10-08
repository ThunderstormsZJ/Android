// IPublishAidlService.aidl
package cn.spinsoft.wdq;
import android.os.Bundle;
import cn.spinsoft.wdq.IPublishAidlCallback;

// Declare any non-default types here with import statements

interface IPublishAidlService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);
        //注册回调接收
        void registerCallback(IPublishAidlCallback callback);

        //将主线程的发布任务递交给远程服务
        void prepareToPublish(in Bundle bundle);

        //注销回调接收
        void unRegisterCallback(IPublishAidlCallback callback);
}
