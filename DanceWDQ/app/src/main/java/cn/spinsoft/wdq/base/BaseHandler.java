package cn.spinsoft.wdq.base;

import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

/**
 * Created by hushujun on 15/11/4.
 */
public class BaseHandler extends Handler {
    private SparseArray<Callback> mCallbacks;

    public static int watcherUserId = -1;

    protected BaseHandler() {
        super();
        mCallbacks = new SparseArray<>();
    }

    public void addCallback(int key, Callback callback) {
        mCallbacks.put(key, callback);
    }

    protected void handleCallbackMessage(int key, Message msg) {
        Callback callback = mCallbacks.get(key);
        if (callback != null) {
            callback.handleMessage(msg);
        }
    }

    public void release() {
        mCallbacks.clear();
    }
}
