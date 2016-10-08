package cn.spinsoft.wdq.upload;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.upload.Const.FileType;
import com.tencent.upload.task.Dentry;
import com.tencent.upload.task.ITask;
import com.tencent.upload.task.IUploadTaskListener;
import com.tencent.upload.task.UploadTask;
import com.tencent.upload.task.VideoAttr;
import com.tencent.upload.task.VideoInfo;
import com.tencent.upload.task.data.FileInfo;
import com.tencent.upload.task.impl.DirCreateTask;
import com.tencent.upload.task.impl.DirListTask;
import com.tencent.upload.task.impl.FileUploadTask;
import com.tencent.upload.task.impl.ObjectDeleteTask;
import com.tencent.upload.task.impl.ObjectStatTask;
import com.tencent.upload.task.impl.ObjectUpdateTask;
import com.tencent.upload.task.impl.VideoUploadTask;

import java.util.ArrayList;

import cn.spinsoft.wdq.utils.LogUtil;

/**
 * 腾讯云文件-数据层 从UI逻辑中分离出来
 */
public class FCloudDataLayer {
    private FileType mFileType;
    private Handler mMainHandler;

    private int mPageSize = 5;
    private int mListPattern = 0;
    private int mMaxConcurrent = 3;
    private boolean mOrder = false;
    private boolean mbUploading = false;
    private boolean mEnbaleHttpRange = true;
    private boolean mEnbaleCleanDwCache = false;
    private boolean mEnableHttpKeepAlive = true;
    private UploadTask mUploadTask = null;

    public static final String TAG = "FCloudDataLayer";

    private SharedPreferences mSharedPreferences;

    public FCloudDataLayer(Context context, FileType fileType) {
        mFileType = fileType;
        mMainHandler = new Handler(Looper.getMainLooper());
        mSharedPreferences = context.getSharedPreferences("fcloud_option_" + mFileType, 0);
    }

    public boolean isUploading() {
        return mbUploading;
    }

    public int getPageSize() {
        return mPageSize;
    }

    public void setPageSize(int pageSize) {
        mPageSize = pageSize;
    }

    public boolean getOrder() {
        return mOrder;
    }

    public void setOrder(boolean order) {
        mOrder = order;
    }

    public int getListPattern() {
        return mListPattern;
    }

    public void setListPattern(int listPattern) {
        mListPattern = listPattern;
    }

    public int getMaxConcurrent() {
        return mMaxConcurrent;
    }

    public void setMaxConcurrent(int maxConcurrent) {
        mMaxConcurrent = maxConcurrent;
    }

    public boolean getEnableCleanDwCache() {
        return mEnbaleCleanDwCache;
    }

    public void setEnableCleanDwCache(boolean enableCleanDwCache) {
        mEnbaleCleanDwCache = enableCleanDwCache;
    }

    public boolean getEnbaleHttpRange() {
        return mEnbaleHttpRange;
    }

    public void setEnbaleHttpRange(boolean enbaleHttpRange) {
        mEnbaleHttpRange = enbaleHttpRange;
    }

    public boolean getEnableHttpKeepAlive() {
        return mEnableHttpKeepAlive;
    }

    public void setEnableHttpKeepAlive(boolean enableHttpKeepAlive) {
        mEnableHttpKeepAlive = enableHttpKeepAlive;
    }

    private String getBucket() {
//        if (mFileType == FileType.Video) {
//            return BizService.VIDEO_BUCKET;
//        }

        return BizService.VIDEO_BUCKET;
    }

    private String getSign() {
//        if (mFileType == FileType.Video) {
//            return BizService.VIDEO_SIGN;
//        }

        return BizService.VIDEO_SIGN;
    }

//    private String getSecretId() {
////        if (mFileType == FileType.Video) {
////            return BizService.VIDEO_SECRETID;
////        }
//
//        return BizService.VIDEO_SECRETID;
//    }

    private FileType getFileType() {
        return mFileType;
    }

    public void loadConfigOption() {
        mPageSize = mSharedPreferences.getInt("page_size", 5);
        mOrder = mSharedPreferences.getBoolean("order", false);
        mListPattern = mSharedPreferences.getInt("list_pattern", 0);
        mMaxConcurrent = mSharedPreferences.getInt("max_concurrent", 3);
        mEnbaleHttpRange = mSharedPreferences.getBoolean("http_range", true);
        mEnbaleCleanDwCache = mSharedPreferences.getBoolean("dw_cache_clean", false);
        mEnableHttpKeepAlive = mSharedPreferences.getBoolean("http_keepalive", true);
    }

    public void saveConfigOption() {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean("order", mOrder);
        edit.putInt("page_size", mPageSize);
        edit.putInt("list_pattern", mListPattern);
        edit.putInt("max_concurrent", mMaxConcurrent);
        edit.putBoolean("http_range", mEnbaleHttpRange);
        edit.putBoolean("dw_cache_clean", mEnbaleCleanDwCache);
        edit.putBoolean("http_keepalive", mEnableHttpKeepAlive);
        edit.commit();
    }

    public class DentryMore extends Dentry {
        public boolean order; // 第一次拉取时排序方式
        public boolean prefix; // 第一次搜索时前缀匹配
        public String content; // 第一次拉取时翻页会话

        public DentryMore(int type) {
            super(type);
            content = "";
            order = false;
        }

        DentryMore setOrder(boolean order) {
            this.order = order;
            return this;
        }

        DentryMore setPrefix(boolean prefix) {
            this.prefix = prefix;
            return this;
        }

        DentryMore setContent(String content) {
            this.content = content;
            return this;
        }
    }

    public interface IListDirListener {
        void onListDirFailure(String path, int errcode, String errmsg);

        void onListDirSuccess(String path, ArrayList<Dentry> dentrList);
    }

    public boolean asyncListDir(final Dentry dentry, boolean enablePrefix, final IListDirListener listener) {
        if (dentry == null || dentry.type == Dentry.FILE || dentry.type == Dentry.VIDEO) {
            return false;
        }

        String content = "";
        boolean bOrder = getOrder();
        boolean bPrefix = enablePrefix;
        if (dentry.type == Dentry.MORE) {
            // 拉取上一页是保存的状态信息
            bOrder = ((DentryMore) dentry).order;
            bPrefix = ((DentryMore) dentry).prefix;
            content = ((DentryMore) dentry).content;
        }

        enablePrefix = bPrefix;
        final boolean order = bOrder;
        final boolean prefix = bPrefix;
        final String path = dentry.path;
        DirListTask task = new DirListTask(getFileType(), getBucket(), path, getPageSize(), getListPattern(), order,
                content, new DirListTask.IListener() {
            @Override
            public void onSuccess(DirListTask.CmdTaskRsp result) {
                final ArrayList<Dentry> dirList = result.inodes;
                if (result.hasMore) {
                    // 保存当前页的状态信息
                    DentryMore entry = null;
                    String content = result.content;
                    entry = new DentryMore(Dentry.MORE).setOrder(order).setPrefix(prefix).setContent(content);
                    entry.setPath(path).setName("更多...");
                    dirList.add(entry);
                }

                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onListDirSuccess(path, dirList);
                    }
                });
            }

            @Override
            public void onFailure(final int ret, final String msg) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onListDirFailure(path, ret, msg);
                    }
                });
            }
        });

        if (enablePrefix) {
            task.setPrefixSearch(true);
        }

        task.setAuth(getSign());
        return BizService.getInstance().sendCommand(task);
    }

    public interface IMkDirListener {
        void onMkDirSuccess(String path, String accessUrl);

        void onMkDirFailure(String path, int errcode, String errmsg);
    }

    public boolean asyncMkDir(Dentry dentry, final IMkDirListener listener) {
        if (dentry == null || dentry.type == Dentry.MORE || dentry.type == Dentry.FILE || dentry.type == Dentry.VIDEO
                || dentry.type == Dentry.BUCKET) {
            return false;
        }

        final String path = dentry.path;
        DirCreateTask task = new DirCreateTask(getFileType(), getBucket(), path, dentry.attribute,
                new DirCreateTask.IListener() {
                    @Override
                    public void onSuccess(final DirCreateTask.CmdTaskRsp result) {
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onMkDirSuccess(path, result.accessUrl);
                            }
                        });
                    }

                    @Override
                    public void onFailure(final int ret, final String msg) {
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onMkDirFailure(path, ret, msg);
                            }
                        });
                    }
                });

        task.setAuth(getSign());
        return BizService.getInstance().sendCommand(task);
    }

    public interface IStatListener {
        public void onStatSuccess(String path, Dentry dentry);

        public void onStatFailure(String path, int errcode, String errmsg);
    }

    public boolean asyncStat(Dentry dentry, final IStatListener listener) {
        if (dentry == null || dentry.type == Dentry.MORE) {
            return false;
        }

        final String path = dentry.path;
        ObjectStatTask task = new ObjectStatTask(getFileType(), getBucket(), path, dentry.type,
                new ObjectStatTask.IListener() {
                    @Override
                    public void onSuccess(ObjectStatTask.CmdTaskRsp result) {
                        final Dentry dentry = result.inode;
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onStatSuccess(path, dentry);
                            }
                        });
                    }

                    @Override
                    public void onFailure(final int ret, final String msg) {
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onStatFailure(path, ret, msg);
                            }
                        });
                    }
                });
        task.setAuth(getSign());
        return BizService.getInstance().sendCommand(task);
    }

    ;

    public interface IDeleteListener {
        public void onDeleteSuccess(String path);

        public void onDeleteFailure(String path, int errcode, String errmsg);
    }

    public boolean asyncDelete(final Dentry dentry, final IDeleteListener listener) {
        if (dentry == null || dentry.type == Dentry.MORE) {
            return false;
        }

        UpdateSignTask updateSignTask = new UpdateSignTask(BizService.APP_ID, getFileType()
                , getBucket(), dentry.path, new UpdateSignTask.OnGetSignListener() {
            @Override
            public void onSign(String sign) {
                if (TextUtils.isEmpty(sign)) {
                    LogUtil.w(TAG,"拉取签名失败");
                    return;
                }

                final String path = dentry.path;
                ObjectDeleteTask task = new ObjectDeleteTask(getFileType(), getBucket(), path, dentry.type,
                        new ObjectDeleteTask.IListener() {
                            @Override
                            public void onSuccess(ObjectDeleteTask.CmdTaskRsp result) {
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onDeleteSuccess(path);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(final int ret, final String msg) {
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onDeleteFailure(path, ret, msg);
                                    }
                                });
                            }
                        });

                task.setAuth(sign);
                BizService.getInstance().sendCommand(task);
            }
        });

        updateSignTask.execute();
        return true;
    }

    public interface IUpdateListener {
        public void onUpdateSuccess(String path);

        public void onUpdateFailure(String path, int errcode, String errmsg);
    }

    public boolean asyncUpdate(final Dentry dentry, final Object externData, final IUpdateListener listener) {
        if (dentry == null || dentry.type == Dentry.MORE) {
            return false;
        }

        UpdateSignTask updateSignTask = new UpdateSignTask(BizService.APP_ID, getFileType(),
                getBucket(), dentry.path, new UpdateSignTask.OnGetSignListener() {
            @Override
            public void onSign(String sign) {
                if (TextUtils.isEmpty(sign)) {
                    LogUtil.w(TAG,"拉取签名失败");
                    return;
                }

                final String path = dentry.path;
                ObjectUpdateTask.IListener objUpdatelistener = null;
                objUpdatelistener = new ObjectUpdateTask.IListener() {
                    @Override
                    public void onSuccess(ObjectUpdateTask.CmdTaskRsp result) {
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onUpdateSuccess(path);
                            }
                        });
                    }

                    @Override
                    public void onFailure(final int ret, final String msg) {
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onUpdateFailure(path, ret, msg);
                            }
                        });
                    }
                };

                ObjectUpdateTask task = null;
                if (dentry.type != Dentry.VIDEO) {
                    task = new ObjectUpdateTask(getFileType(), getBucket(), path, dentry.type,
                            dentry.attribute, objUpdatelistener);
                } else {
                    task = new ObjectUpdateTask(getFileType(), getBucket(), path, dentry.type,
                            VideoInfo.MaskAll, dentry.attribute, (VideoAttr) externData, objUpdatelistener);
                }

                task.setAuth(sign);
                BizService.getInstance().sendCommand(task);
            }
        });

        updateSignTask.execute();
        return true;
    }

    public interface IUploadListener {
        void onUploadSucceed(Dentry result);

        void onUploadFailed(int errorCode, String errorMsg);

        void onUploadProgress(long totalSize, long sendSize);

        void onUploadStateChange(ITask.TaskState state);// 上传状态改变
    }

    public boolean asyncUploadFile(String srcPath, final Dentry destDentry, Object externData,
                                   final IUploadListener listener) {
        if (destDentry == null || destDentry.type == Dentry.MORE || destDentry.type == Dentry.DIR
                || destDentry.type == Dentry.BUCKET) {
            return false;
        }

        mbUploading = true;
        final String destPath = destDentry.path;
        final String attribute = destDentry.attribute;

        IUploadTaskListener taskListener = new IUploadTaskListener() {
            @Override
            public void onUploadSucceed(FileInfo info) {
                final Dentry dentry = new Dentry().setPath(destPath).setType(destDentry.type).setAttribute(attribute)
                        .setAccessUrl(info.url);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onUploadSucceed(dentry);
                        mbUploading = false;
                    }
                });
            }

            @Override
            public void onUploadStateChange(final ITask.TaskState taskState) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onUploadStateChange(taskState);
                        if (taskState == ITask.TaskState.CANCEL) {
                            mbUploading = false;
                        }
                    }
                });
            }

            @Override
            public void onUploadProgress(final long totalSize, final long sendSize) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onUploadProgress(totalSize, sendSize);
                    }
                });
            }

            @Override
            public void onUploadFailed(final int ret, final String msg) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onUploadFailed(ret, msg);
                        mbUploading = false;
                    }
                });
            }
        };

        UploadTask task = null;
        if (destDentry.type == Dentry.FILE) {
            task = new FileUploadTask(getBucket(), srcPath, destPath, attribute, taskListener);
        } else if (destDentry.type == Dentry.VIDEO) {
            VideoAttr videoAttr = (VideoAttr) externData;
            task = new VideoUploadTask(getBucket(), srcPath, destPath, attribute, videoAttr, taskListener);
        }

        if (task == null) {
            return false;
        }

        int taskId = task.getTaskId();
        task.setAuth(getSign());
        if (!BizService.getInstance().upload(task)) {
            mbUploading = false;
            return false;
        }

        Log.i(TAG, "begin upload taskId " + taskId + " " + srcPath + " to " + destPath);
        mUploadTask = task;
        return true;
    }

    public boolean clear() {
        Log.i(TAG, "clear upload task");
        return BizService.getInstance().uploadManagerClear();
    }

    public boolean pauseUploadTask() {
        Log.i(TAG, "pauseUploadTask upload task " + mUploadTask.getTaskId());
        return BizService.getInstance().pause(mUploadTask);
    }

    public boolean resumeUploadTask() {
        Log.i(TAG, "resumeUploadTask upload task " + mUploadTask.getTaskId());
        return BizService.getInstance().resume(mUploadTask);
    }

    public boolean cancelUploadTask() {
        Log.i(TAG, "cancelUploadTask upload task " + mUploadTask.getTaskId());
        return BizService.getInstance().cancel(mUploadTask);
    }
}
