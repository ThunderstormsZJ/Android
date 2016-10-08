package cn.spinsoft.wdq.upload;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.tencent.upload.Const;
import com.tencent.upload.UploadManager;
import com.tencent.upload.task.CommandTask;
import com.tencent.upload.task.UploadTask;

import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;

/**
 * Created by hushujun on 15/11/16.
 */
public class BizService {
    private static final String TAG = BizService.class.getSimpleName();

    public static class DefaultCfg {
        private static final String APP_ID = Constants.Strings.APPID_QCLOUND;
        public static final String VIDEO_BUCKET = Constants.Strings.QCLOUD_BUCKET;
    }

    public static String APP_ID = DefaultCfg.APP_ID;
    public static String VIDEO_BUCKET = DefaultCfg.VIDEO_BUCKET;
    public static String VIDEO_SIGN = "";

    //    public static String SDK_VERSION = "1.1.3";
    public static Const.ServerEnv ENVIRONMENT;

    private static BizService sInstance = null;
    private static final byte[] INSTANCE_LOCK = new byte[0];

    private UploadManager mVideoUploadManager;

    private SharedPreferences mSharedPreferences;

    public static synchronized BizService getInstance() {
        if (sInstance == null) {
            synchronized (INSTANCE_LOCK) {
                if (sInstance == null) {
                    sInstance = new BizService();
                }
            }
        }
        return sInstance;
    }

    private BizService() {
        ENVIRONMENT = Const.ServerEnv.NORMAL;
    }

    public void init(Context context) {
        mSharedPreferences = SharedPreferencesUtil.getInstance(context).getSharedPreferences();
        loadSign();

        mVideoUploadManager = new UploadManager(context, BizService.APP_ID, Const.FileType.Video, null);
    }

    public boolean upload(UploadTask task) {
        if (task == null) {
            return false;
        }
        return mVideoUploadManager.upload(task);
    }

    public boolean resume(UploadTask task) {
        if (task == null) {
            return false;
        }
        return mVideoUploadManager.resume(task.getTaskId());
    }

    public boolean pause(UploadTask task) {
        if (task == null) {
            return false;
        }
        return mVideoUploadManager.pause(task.getTaskId());
    }

    public boolean cancel(UploadTask task) {
        if (task == null) {
            return false;
        }
        return mVideoUploadManager.cancel(task.getTaskId());
    }

    public boolean sendCommand(CommandTask task) {
        if (task == null) {
            return false;
        }
        return mVideoUploadManager.sendCommand(task);
    }

    public void uploadManagerClose() {
        mVideoUploadManager.close();
    }

    public boolean uploadManagerClear() {
        return mVideoUploadManager.clear();
    }

    public void changeUploadEnv(Const.ServerEnv env) {
        ENVIRONMENT = env;
        mVideoUploadManager.setServerEnv(ENVIRONMENT);
    }

    private void loadSign() {
        APP_ID = mSharedPreferences.getString("qcloud_appid", "");
        if (TextUtils.isEmpty(APP_ID)) {
            APP_ID = DefaultCfg.APP_ID;
        }
        VIDEO_BUCKET = mSharedPreferences.getString("qcloud_bucket", "");
        if (TextUtils.isEmpty(VIDEO_BUCKET)) {
            VIDEO_BUCKET = DefaultCfg.VIDEO_BUCKET;
        }

        LogUtil.i(TAG, "load sign");
    }

    private void saveSign() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("qcloud_appid", APP_ID);
        editor.putString("qcloud_bucket", VIDEO_BUCKET);
        editor.commit();
        LogUtil.i(TAG, "save sign");
    }

//    public void updateSign(String appId, String bucket) {
//        String sign = VIDEO_SIGN;
//        updateSign(appId, bucket, sign);
//    }

    public void updateSign(String appId, String bucket, String sign) {
        APP_ID = appId;
        VIDEO_SIGN = sign;
        VIDEO_BUCKET = bucket;
        LogUtil.i(TAG, "update sign");
        saveSign();
    }
}
