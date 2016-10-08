package cn.spinsoft.wdq.upload;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tencent.upload.Const;
import com.tencent.upload.task.Dentry;
import com.tencent.upload.task.VideoAttr;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import cn.spinsoft.wdq.utils.LogUtil;

/**
 * Created by hushujun on 15/11/17.
 */
public class UploadUtil {
    private static final String TAG = UploadUtil.class.getSimpleName();
    private static final int DIR_EXISTS = -178;
    private static final int FILE_EXISTS = -4018;

    private Context mContext;

    private FCloudDataLayer mFCloudDataLayer;

    private String mCurrPath;
    //    private String mRootPath;
    private boolean hasUserDirOnServer = false;


    public UploadUtil(Context context) {
        this.mContext = context;
        mFCloudDataLayer = new FCloudDataLayer(context, Const.FileType.Video);
    }

    public void changeEnv(final Const.ServerEnv env) {
        BizService.getInstance().changeUploadEnv(env);
        updateSign(new UpdateSignTask.OnGetSignListener() {
            @Override
            public void onSign(String sign) {
                if (TextUtils.isEmpty(sign)) {
                    Log.i(TAG, "change server environment to " + env.getDesc() + " failed");
                } else {
                    Log.i(TAG, "change server environment to:" + env.getDesc() + " success");
                }
            }
        });

//        Toast.makeText(mContext, "切换环境:" + env.getDesc(), Toast.LENGTH_SHORT).show();
    }

    /**
     * //     * 更新多次签名
     * //
     */
    private void updateSign(UpdateSignTask.OnGetSignListener listener) {
        UpdateSignTask updateSignTask = null;
        updateSignTask = new UpdateSignTask(BizService.APP_ID, Const.FileType.Video,
                BizService.VIDEO_BUCKET, null, listener);
        updateSignTask.execute();
    }

    public void makeDirOnCloud(int userId) {
        if (!getCurrPath().contains("User") && !hasUserDirOnServer) {
            doMakeDirProccess("User" + userId);
        }
    }

    private String getCurrPath() {
        if (TextUtils.isEmpty(mCurrPath)) {
            return getRootPath();
        }

        int lastPos = mCurrPath.length() - 1;
        if (mCurrPath.charAt(lastPos) == '/') {
            return mCurrPath;
        }

        lastPos = mCurrPath.lastIndexOf("/");
        if (lastPos < 0) {
            return getRootPath();
        }

        return mCurrPath.substring(0, lastPos + 1);
    }

    private void setCurrPath(String path) {
        mCurrPath = path;
    }

    private String getRootPath() {
        return "/";
    }

    private void doMakeDirProccess(String dirName) {
        if (TextUtils.isEmpty(dirName)) {
            return;
        }
        int lastpos = dirName.length() - 1;
        if (dirName.charAt(lastpos) != '/') {
            dirName += "/";
        }

        String path = getCurrPath() + dirName;
        if (dirName.charAt(0) == '/') {
            path = dirName;
        }

        Dentry dentry = new Dentry(Dentry.DIR).setPath(path).setAttribute("");
        mFCloudDataLayer.asyncMkDir(dentry, new FCloudDataLayer.IMkDirListener() {
            @Override
            public void onMkDirSuccess(String path, String accessUrl) {
                hasUserDirOnServer = true;
                Dentry dentry = new Dentry(Dentry.DIR).setPath(path);
                mFCloudDataLayer.asyncListDir(dentry, false, getListDirListener());
                LogUtil.i(TAG, "onMkDirSuccess:" + path);
            }

            @Override
            public void onMkDirFailure(String path, int errcode, String errmsg) {
                String msg = "创建目录失败:" + path;
                msg += "  errcode:" + errcode + " errmsg:" + errmsg;
//                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                LogUtil.e(TAG, msg);
                if (errcode == DIR_EXISTS) {
                    setCurrPath(path);
                    hasUserDirOnServer = true;
                }
            }
        });
    }

    private FCloudDataLayer.IListDirListener getListDirListener() {
        return new FCloudDataLayer.IListDirListener() {
            @Override
            public void onListDirFailure(String path, int errcode, String errmsg) {
                String msg = "拉取目录失败:" + path;
                msg += "  errcode:" + errcode + " errmsg:" + errmsg;
//                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                LogUtil.e(TAG, msg);
            }

            @Override
            public void onListDirSuccess(String path, ArrayList<Dentry> dentrList) {
                setCurrPath(path);
            }
        };
    }

    public void uploadVideo(File file, FCloudDataLayer.IUploadListener listener) {
        if (file == null || !file.exists()) {
            Toast.makeText(mContext, "视频不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        VideoAttr videoAttr = new VideoAttr();
        final String srcFilePath = file.getAbsolutePath();
        final String destFilePath = getCurrPath() + file.getName();

        videoAttr.isCheck = false;
        videoAttr.title = file.getName();
        videoAttr.desc = new Date().toString() + "-" + file.getName();
        mFCloudDataLayer.asyncUploadFile(srcFilePath, new Dentry(Dentry.VIDEO).setPath(destFilePath), videoAttr, listener);
//        doUploadProccess(srcFilePath, new Dentry(Dentry.VIDEO).setPath(destFilePath), videoAttr, callback);
    }

//    private void doUploadProccess(final String srcFilePath, Dentry destDentry, Object externData
//            , final OkHttpClientManager.ResultCallback<String> callback) {
//        mFCloudDataLayer.asyncUploadFile(srcFilePath, destDentry, externData, new FCloudDataLayer.IUploadListener() {
//
//            @Override
//            public void onUploadSucceed(Dentry result) {
//                Toast.makeText(mContext, srcFilePath + "上传成功", Toast.LENGTH_SHORT).show();
//
//                LogUtil.i(TAG, "onUploadSucceed:" + result.path);
//                LogUtil.w(TAG, "onUploadSucceed:" + result.accessUrl);
//
//                //
//                BrowseHandler.Status.Video.videoUrl = result.accessUrl;
//                if (!TextUtils.isEmpty(BrowseHandler.Status.Video.videoUrl)) {
//                    Bitmap bm = BitmapUtils.getMediaFrame(srcFilePath);
//                    String cachePath = BitmapUtils.cacheBitmap(bm);
//                    uploadImage(cachePath, 0, callback);
//                }
//            }
//
//            @Override
//            public void onUploadFailed(int errorCode, String errorMsg) {
//                String msg = "上传失败:" + srcFilePath;
//                msg += "  errcode:" + errorCode + " errmsg:" + errorMsg;
//                LogUtil.e(TAG, msg);
//                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
//                if (errorCode == FILE_EXISTS) {
//                    // TODO: 15/11/27 nothing
//                }
//            }
//
//            @Override
//            public void onUploadProgress(long totalSize, long sendSize) {
//                LogUtil.i(TAG, "onUploadProgress:totalSize=" + totalSize);
//                float a = (sendSize * 100) / (totalSize * 1.0f);
//                LogUtil.i(TAG, "onUploadProgress:" + a + "%");
//            }
//
//            @Override
//            public void onUploadStateChange(ITask.TaskState state) {
//                LogUtil.w(TAG, "onUploadStateChange:" + state.getDesc());
//            }
//        });
//    }
//
//    private void uploadImage(final String imagePath, int userId, final OkHttpClientManager.ResultCallback<String> callback) {
//        uploadImage(imagePath, UrlManager.getUrl(UrlManager.UrlName.UPLOAD_IMAGE), userId, callback);
//    }

//    public static void uploadImage(final String imagePath, String uploadUrl, int userId,
//                                   final OkHttpClientManager.ResultCallback<String> callback) {
//        if (TextUtils.isEmpty(imagePath)) {
//            return;
//        }
//        final File imgFile = new File(imagePath);
//        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]
//                {new OkHttpClientManager.Param("userid", String.valueOf(userId))};
//        OkHttpClientManager.getUploadDelegate().postAsyn(uploadUrl, imgFile.getNickName(), imgFile, params,
//                new OkHttpClientManager.ResultCallback<String>() {
//                    @Override
//                    public void onError(Request request, Exception e) {
////                        imgFile.delete();
//                        if (callback != null) {
//                            callback.onError(request, e);
//                        }
//                    }
//
//                    @Override
//                    public void onResponse(String response) {
//                        LogUtil.i(TAG, "uploadImage:" + response);
////                        imgFile.delete();
//                        if (callback != null) {
//                            callback.onResponse(response);
//                        }
//                    }
//                }, imagePath);
//    }


//    public void saveVideoInfo(String videoUrl, String imgUrl, int danceType, String title, String desc,
//                              int userId, String region, double longitude, double latitude) {
//        LogUtil.d(TAG, "saveVideoInfo:videoUrl=" + videoUrl + ",imgUrl=" + imgUrl);
//        FormEncodingBuilder builder = new FormEncodingBuilder();
//        builder.add("title", title);
//        builder.add("path", videoUrl);
//        builder.add("bigimg", imgUrl);
//        builder.add("desc", desc);
//        builder.add("type", String.valueOf(danceType));
//        builder.add("userid", String.valueOf(userId));
//        builder.add("region", region);
//        builder.add("longitude", String.valueOf(longitude));
//        builder.add("latitude", String.valueOf(latitude));
//        Request request = new Request.Builder().url(UrlManager.getUrl(UrlManager.UrlName.SAVE_VIDEO_INFO)).post(builder.build()).build();
//        OkHttpClientManager.getInstance().getGetDelegate().getAsyn(request, new OkHttpClientManager.ResultCallback<String>() {
//            @Override
//            public void onError(Request request, Exception e) {
//                LogUtil.e(TAG, "saveVideoInfo:" + e);
//                Toast.makeText(mContext, "发表并上传视频失败", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onResponse(String response) {
//                LogUtil.i(TAG, "saveVideoInfo:" + response);
//                Toast.makeText(mContext, "发表并上传视频成功", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
