// ILocationAidlCallback.aidl
package cn.spinsoft.wdq;

// Declare any non-default types here with import statements

interface ILocationAidlCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

    /**
    * 获取到经纬度,
    * @param longitude 经度
    * @param latitude 纬度
    */
    void locationGot(double longitude, double latitude, String city, String address);

    void locationFailed(int errCode, String errMsg);

//    void locationModelChanged(int model);
}
