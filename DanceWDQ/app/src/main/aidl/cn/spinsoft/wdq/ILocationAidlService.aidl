// ILocationAidlInterface.aidl
package cn.spinsoft.wdq;

import cn.spinsoft.wdq.ILocationAidlCallback;

// Declare any non-default types here with import statements

interface ILocationAidlService{
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);
    void registerCallback(ILocationAidlCallback callback);

    void startLocation();

    void stopLocation();

    void changLocationModel(int model, long interval);

    void unRegisterCallback(ILocationAidlCallback callback);
}
