// IPublishAidlCallback.aidl
package cn.spinsoft.wdq;

// Declare any non-default types here with import statements

interface IPublishAidlCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);
        //资源上传结果回传
      void srcUpLoadResult(boolean success, String srcPath, String srcUrl);

        //发布结果回传
      void publishResult(int code, String message);

       //远程服务退出时回传未完成的任务队列,主线程可以将未完成的任务存储,以便下次开启远程服务的时候继续执行
      void savePublishUnComplete(out Bundle bundle);
}
