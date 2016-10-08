//package cn.spinsoft.wdq.education;
//
//import android.app.Activity;
//import android.support.multidex.MultiDexApplication;
//import android.util.Log;
//
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.Volley;
//
//import java.util.LinkedList;
//import java.util.List;
//import java.util.logging.Handler;
//
//
///**
// * 此类保证退出时是退出所有的应用的Activity
// *
// * @author lhy
// */
//
//public class MyApplication extends MultiDexApplication {
//    private String TAG = "JPush";
//    private static List<Activity> activityList = new LinkedList<Activity>();
//    static MyApplication instance;
//    public static boolean isDownload = false;
//    // 获取到主线程的handler
//    private static Handler mMainThreadHandler = null;
//    // 获取到主线程
//    private static Thread mMainThread = null;
//    // 获取到主线程的id
//    private static int mMainThreadId;
//
//    public static RequestQueue queue;
//
//    // 对外暴露主线程的handler
//    public static Handler getMainThreadHandler() {
//        return mMainThreadHandler;
//    }
//
//    // 对外暴露主线程
//    public static Thread getMainThread() {
//        return mMainThread;
//    }
//
//    // 对外暴露主线程id
//    public static int getMainThreadId() {
//        return mMainThreadId;
//    }
//
//
//    // 单例模式中获取唯一的MyApplication实例
//    public static MyApplication getInstance() {
//        if (null == instance) {
//            instance = new MyApplication();
//        }
//        return instance;
//    }
//
//    // 添加Activity到容器中
//    public void addActivity(Activity activity) {
//        activityList.add(activity);
//    }
//
//    // 遍历所有Activity并finish
//    public void exit() {
//        for (Activity activity : activityList) {
//            if (activity != null)
//                activity.finish();
//        }
//        System.exit(0);
//    }
//
//    @Override
//    public void onCreate() {
//        instance = this;
//        super.onCreate();
//
//        mMainThread = Thread.currentThread();
//        mMainThreadId = android.os.Process.myTid();
//        //初始化volley队列
//        queue = Volley.newRequestQueue(getApplicationContext());//使用全局上下文
//
//        //极光推送初始化的部分
//        Log.e(TAG, "[ExampleApplication] 极光推送onCreate");
//        super.onCreate();
////        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
//        //小米手机初始化报错,无法启动
////        JPushInterface.init(this);            // 初始化 JPush
//
//    }
//
//    public static RequestQueue getHttpQueue() {
//        return queue;
//    }
//
//    public boolean isDownload() {
//        return isDownload;
//    }
//
//    public void setDownload(boolean isDownload) {
//        MyApplication.isDownload = isDownload;
//    }
//}
//
//
