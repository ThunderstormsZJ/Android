package cn.spinsoft.wdq.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName: ThreadManager
 * @Description: TODO(线程管理) 所有的线程任务都通过该入口执行
 * @date 2015-3-20 下午4:48:10
 */
public class ThreadManager {

    private static ExecutorService mThreads; // 会自动回收的无界限线程池

    private static ExecutorService mSingleThread;// 单个线程池

    /*
     * 获取一个单例会自动回收的无界线程池
     */
    private static ExecutorService getMultiThreads() {
        if (mThreads == null) {
            synchronized (ThreadManager.class) {
                if (mThreads == null) {
                    mThreads = Executors.newCachedThreadPool();
                }
            }
        }
        return mThreads;
    }

    /**
     * @param @param run 设定文件
     * @return void 返回类型
     * @throws
     * @Description: TODO(耗时任务的执行入口函數)
     */
    public static void execute(Runnable task) {
        try {
            if (task == null) {
                return;
            }
            mThreads = getMultiThreads();
            mThreads.execute(task);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /*
     * 获取单任务的线程池，适合比较短时间能完成的任务，避免阻塞后续任务
     */
    private static ExecutorService getSingleThread() {
        if (mSingleThread == null) {
            synchronized (ThreadManager.class) {
                if (mSingleThread == null) {
                    mSingleThread = Executors.newSingleThreadExecutor();
                }
            }
        }
        return mSingleThread;
    }

    /**
     * @param @param task 设定文件
     * @return void 返回类型
     * @throws
     * @Description: TODO(单线程任务的执行入口)
     */
    public static void exectueSingleTask(Runnable task) {
        try {
            if (task == null) {
                return;
            }
            mSingleThread = getSingleThread();
            mSingleThread.execute(task);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * @return void 返回类型
     * @throws
     * @Description: TODO(关闭多线程，建议在程序退出时执行)
     */
    public static void cancleThreads() {
        // closeExecutorService(mThreads);
    }

    /**
     * @return void 返回类型
     * @throws
     * @Description: TODO(终止单线程池，在不需要用的时候执行)
     */
    public static void cancleSingleTask() {
        // closeExecutorService(mSingleThread);
    }

    public static void closeExecutorService(ExecutorService pool) {
    }

    /**
     * @return void 返回类型
     * @throws
     * @Description: TODO(关闭所有的任务，建议在程序退出时调用)
     */
    public static void cancleAllTask() {
        // closeExecutorService(mThreads);
        // closeExecutorService(mSingleThread);
    }
}
