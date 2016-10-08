package cn.spinsoft.wdq.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.squareup.okhttp.Request;
import com.tencent.upload.Const;
import com.tencent.upload.task.Dentry;
import com.tencent.upload.task.ITask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.IPublishAidlCallback;
import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.browse.biz.BrowseParser;
import cn.spinsoft.wdq.mine.biz.MineParser;
import cn.spinsoft.wdq.mine.biz.PublishInfoBean;
import cn.spinsoft.wdq.mine.biz.PublishVideoBean;
import cn.spinsoft.wdq.upload.BizService;
import cn.spinsoft.wdq.upload.FCloudDataLayer;
import cn.spinsoft.wdq.upload.UploadUtil;
import cn.spinsoft.wdq.utils.BitmapUtils;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.OkHttpClientManager;
import cn.spinsoft.wdq.utils.UrlManager;

/**
 * Created by zhoujun on 15/12/21.
 */
public class PublishService extends Service {
    private static final String TAG = PublishService.class.getSimpleName();

    private PublishServiceBinder mBinder = new PublishServiceBinder(this);
    private IPublishAidlCallback mPublishCallback = null;

    private List<PublishInfoBean> mPublishInfoList = null;
    private List<PublishVideoBean> mPublishVideoList = null;

    private UploadUtil mUploadUtil;

    private boolean isPublishInfo = false;
    private boolean isPublishVideo = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPublishInfoList = new ArrayList<>();
        mPublishVideoList = new ArrayList<>();

        mUploadUtil = new UploadUtil(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mUploadUtil.changeEnv(Const.ServerEnv.NORMAL);

            }
        }, 1000);
        BizService.getInstance().init(this);
    }

    @Override
    public void onDestroy() {
        if (mPublishInfoList != null && !mPublishInfoList.isEmpty()) {
            try {
                mPublishCallback.savePublishUnComplete(null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (mPublishVideoList != null && !mPublishVideoList.isEmpty()) {
            try {
                mPublishCallback.savePublishUnComplete(null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    public void registerCallback(IPublishAidlCallback callback) throws RemoteException {
        mPublishCallback = callback;
    }

    public void prepareToPublish(Bundle bundle) throws RemoteException {
        if (bundle != null) {
            Parcelable parcelable = bundle.getParcelable(Constants.Strings.PUBLISH_INFO);
            if (parcelable != null) {
                LogUtil.w(TAG, "prepareToPublish:" + ((PublishInfoBean) parcelable).toString());
                mPublishInfoList.add((PublishInfoBean) parcelable);
                if (!isPublishInfo) {
                    doPublishInfo();
                }
            } else {
                parcelable = bundle.getParcelable(Constants.Strings.PUBLISH_VIDEO);
                if (parcelable != null) {
                    LogUtil.w(TAG, "prepareToPublish:" + ((PublishVideoBean) parcelable).toString());
                    mUploadUtil.makeDirOnCloud(((PublishVideoBean) parcelable).getUserId());
                    mPublishVideoList.add((PublishVideoBean) parcelable);
                    if (!isPublishVideo) {
                        doPublishVideo();
                    }
                }
            }
        }
    }

    public void unRegisterCallback(IPublishAidlCallback callback) throws RemoteException {
        mPublishCallback = null;
    }

    private void doPublishInfo() {
        if (mPublishInfoList.isEmpty()) {
            isPublishInfo = false;
            return;
        }
        PublishInfoBean infoBean = mPublishInfoList.get(0);
        if (infoBean == null) {
            if (mPublishInfoList.size() > 1) {
                mPublishInfoList.remove(0);
                doPublishInfo();
                return;
            }
            isPublishInfo = false;
            return;
        }
        isPublishInfo = true;
        List<String> imagePaths = infoBean.getImages();
        String posSmallImg = infoBean.getPosSmallImg();
        String videoUri = infoBean.getVideoUri();
        if ((imagePaths != null && !imagePaths.isEmpty()) || !TextUtils.isEmpty(posSmallImg)
                || !TextUtils.isEmpty(videoUri)) {
           /* final int[] idx = {0};
            uploadImage(idx, infoBean);*/
            //上传海报
            uploadPosImage(infoBean);
        } else {
            publishInfoToServer(infoBean);
        }
    }

    private void doPublishVideo() {
        if (mPublishVideoList.isEmpty()) {
            isPublishVideo = false;
            return;
        }
        final PublishVideoBean videoBean = mPublishVideoList.get(0);
        if (videoBean == null) {
            if (mPublishVideoList.size() > 1) {
                mPublishVideoList.remove(0);
                doPublishVideo();
                return;
            }
            isPublishVideo = false;
            return;
        }
        isPublishVideo = true;

        uploadVideo(videoBean);
    }

    private void uploadVideo(final PublishVideoBean videoBean) {
        File file = new File(videoBean.getVideoUrl());
        final String srcFilePath = file.getAbsolutePath();
        mUploadUtil.uploadVideo(file, new FCloudDataLayer.IUploadListener() {
            private static final int FILE_EXISTS = -4018;

            @Override
            public void onUploadSucceed(Dentry result) {
                String videoUrl = result.accessUrl;
                LogUtil.i(TAG, "Upload video succeed:" + srcFilePath + "====>" + videoUrl);
                if (mPublishCallback != null) {
                    try {
                        mPublishCallback.srcUpLoadResult(true, srcFilePath, videoUrl);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                videoBean.setVideoUrl(videoUrl);

                notifyUpload(videoBean.getVideoUrl(), "视频上传成功", true, "");

                if (!TextUtils.isEmpty(videoUrl)) {
                    Bitmap bm = BitmapUtils.getMediaFrame(srcFilePath);
                    String cachePath = BitmapUtils.cacheBitmap(bm);
//                    uploadImage(cachePath, 0, callback);
                    uploadImage(cachePath, videoBean);
                }
            }

            @Override
            public void onUploadFailed(int errorCode, String errorMsg) {
                String msg = "上传失败:" + srcFilePath;
                msg += "  errcode:" + errorCode + " errmsg:" + errorMsg;
                LogUtil.e(TAG, msg);
                if (mPublishCallback != null) {
                    try {
                        mPublishCallback.srcUpLoadResult(false, srcFilePath, "");
                        mPublishCallback.publishResult(errorCode, msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                notifyUpload(videoBean.getVideoUrl(), "视频上传失败", false, errorMsg);
                if (errorCode == FILE_EXISTS) {
                    // TODO: 15/11/27 nothing
                    mPublishVideoList.remove(videoBean);
                }

                doPublishVideo();
            }

            @Override
            public void onUploadProgress(long totalSize, long sendSize) {
//                LogUtil.i(TAG, "onUploadProgress:totalSize=" + totalSize);
                float a = (sendSize * 100) / (totalSize * 1.0f);
                LogUtil.i(TAG, "onUploadProgress:" + a + "%");
            }

            @Override
            public void onUploadStateChange(ITask.TaskState state) {
                LogUtil.w(TAG, "onUploadStateChange:" + state.getDesc());
            }
        });
    }

    /**
     * 上传视频缩略图
     *
     * @param posterPath
     * @param videoBean
     */
    private void uploadImage(final String posterPath, final PublishVideoBean videoBean) {
        uploadImage(posterPath, UrlManager.getUrl(UrlManager.UrlName.UPLOAD_IMAGE), 0,
                new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        LogUtil.e(TAG, "uploadImage:" + request.toString());
                        e.printStackTrace();
                        if (mPublishCallback != null) {
                            try {
                                mPublishCallback.srcUpLoadResult(false, posterPath, "");
                                mPublishCallback.publishResult(-1, "缩略图上传失败");
                            } catch (RemoteException e1) {
                                e1.printStackTrace();
                            }
                        }

                        //缩略图上传失败,指定为空地址
                        videoBean.setPosterUrl("");
                        publishVideoToServer(videoBean);//依然发布视频
                    }

                    @Override
                    public void onResponse(String response) {
                        LogUtil.i(TAG, "uploadImage:" + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.optString("code").equals("0")) {
                                String imageUrl = object.optString("filePath");
                                if (!TextUtils.isEmpty(imageUrl)) {
                                    videoBean.setPosterUrl(imageUrl);
                                    publishVideoToServer(videoBean);
                                }
                            } else {
                                mPublishVideoList.remove(videoBean);
                                doPublishVideo();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 上传发现视频缩略图
     *
     * @param posterPath
     * @param infoBean
     */
    private void uploadImage(final String posterPath, final PublishInfoBean infoBean) {
        uploadImage(posterPath, UrlManager.getUrl(UrlManager.UrlName.UPLOAD_IMAGE), 0,
                new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        LogUtil.e(TAG, "uploadImage:" + request.toString());
                        e.printStackTrace();
                        if (mPublishCallback != null) {
                            try {
                                mPublishCallback.srcUpLoadResult(false, posterPath, "");
                                mPublishCallback.publishResult(-1, "缩略图上传失败");
                            } catch (RemoteException e1) {
                                e1.printStackTrace();
                            }
                        }

                        //缩略图上传失败,指定为空地址
//                        infoBean.setVideoPoster("");

                        //上传失败继续上传
                        uploadImage(posterPath, infoBean);

                    }

                    @Override
                    public void onResponse(String response) {
                        LogUtil.i(TAG, "uploadImage:" + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.optString("code").equals("0")) {
                                String imageUrl = object.optString("filePath");
                                if (!TextUtils.isEmpty(imageUrl)) {
                                    infoBean.setVideoPoster(imageUrl);
                                    publishInfoToServer(infoBean);
                                }
                            } else {
                                mPublishInfoList.remove(infoBean);
                                publishInfoToServer(infoBean);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public static void uploadImage(final String imagePath, String uploadUrl, int userId,
                                   final OkHttpClientManager.ResultCallback<String> callback) {
        if (TextUtils.isEmpty(imagePath)) {
            return;
        }
        BitmapUtils.saveBitmap(BitmapUtils.scaleBitmap(imagePath, 480, 800), imagePath);//压缩转存图片
        final File imgFile = new File(imagePath);
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]
                {new OkHttpClientManager.Param("userid", String.valueOf(userId))};
        OkHttpClientManager.getUploadDelegate().postAsyn(uploadUrl, imgFile.getName(), imgFile,
                params, callback, imagePath);
    }

    /**
     * 上传发现的宣传视频
     *
     * @param infoBean
     */
    private void uploadDisVideo(final PublishInfoBean infoBean) {
        File file = new File(infoBean.getVideoUri());
        final String srcFilePath = file.getAbsolutePath();
        mUploadUtil.uploadVideo(file, new FCloudDataLayer.IUploadListener() {
            private static final int FILE_EXISTS = -4018;

            @Override
            public void onUploadSucceed(Dentry result) {
                String videoUrl = result.accessUrl;
                LogUtil.i(TAG, "Upload video succeed:" + srcFilePath + "====>" + videoUrl);
                if (mPublishCallback != null) {
                    try {
                        mPublishCallback.srcUpLoadResult(true, srcFilePath, videoUrl);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                infoBean.setVideoUri(videoUrl);
//                notifyUpload(videoBean.getVideoUrl(), "视频上传成功", true, "");

                if (!TextUtils.isEmpty(videoUrl)) {
                    Bitmap bm = BitmapUtils.getMediaFrame(srcFilePath);
                    String cachePath = BitmapUtils.cacheBitmap(bm);
//                    uploadImage(cachePath, 0, callback);
                    //视频上传成功——>上传缩略图
                    uploadImage(cachePath, infoBean);
                }
            }

            @Override
            public void onUploadFailed(int errorCode, String errorMsg) {
                String msg = "上传失败:" + srcFilePath;
                msg += "  errcode:" + errorCode + " errmsg:" + errorMsg;
                LogUtil.e(TAG, msg);
                if (mPublishCallback != null) {
                    try {
                        mPublishCallback.srcUpLoadResult(false, srcFilePath, "");
                        mPublishCallback.publishResult(errorCode, msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
//                notifyUpload(videoBean.getVideoUrl(), "视频上传失败", false, errorMsg);
                //继续上传
                uploadDisVideo(infoBean);
                if (errorCode == FILE_EXISTS) {
                    // TODO: 15/11/27 nothing
                    mPublishInfoList.remove(infoBean);
                }

//                doPublishVideo();
            }

            @Override
            public void onUploadProgress(long totalSize, long sendSize) {
//                LogUtil.i(TAG, "onUploadProgress:totalSize=" + totalSize);
                float a = (sendSize * 100) / (totalSize * 1.0f);
                LogUtil.i(TAG, "onUploadProgress:" + a + "%");
            }

            @Override
            public void onUploadStateChange(ITask.TaskState state) {
                LogUtil.w(TAG, "onUploadStateChange:" + state.getDesc());
            }
        });
    }

    /**
     * 上传海报图片
     *
     * @param infoBean
     */
    private void uploadPosImage(final PublishInfoBean infoBean) {
        if (infoBean != null && !TextUtils.isEmpty(infoBean.getPosSmallImg())) {
            final String imagePath = infoBean.getPosSmallImg();
            uploadImage(imagePath, UrlManager.getUrl(UrlManager.UrlName.UPLOAD_IMAGE),
                    infoBean.getUserId(), new OkHttpClientManager.ResultCallback<String>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            LogUtil.e(TAG, "onError uploadPosImage path:" + imagePath + " ERR");
                            e.printStackTrace();

                            if (mPublishCallback != null) {
                                try {
                                    mPublishCallback.srcUpLoadResult(false, imagePath, null);
                                } catch (RemoteException e1) {
                                    e1.printStackTrace();
                                }
                            }

                            uploadPosImage(infoBean);//上传失败继续上传
                        }

                        @Override
                        public void onResponse(String response) {
                            LogUtil.i(TAG, "onResponse uploadPosImage response:" + response);
                            try {
                                JSONObject object = new JSONObject(response);
                                if (object.optString("code").equals("0")) {
                                    String imageUrl = object.optString("filePath");
                                    String smallImgUrl = object.optString("smallImg");//服务器返回小图地址
                                    infoBean.setPosBigImg(imageUrl);
                                    infoBean.setPosSmallImg(smallImgUrl);
                                    if (mPublishCallback != null) {
                                        mPublishCallback.srcUpLoadResult(true, imagePath, imageUrl);
                                    }
                                    notifyUpload(imagePath, "图片上传成功", true, null);

                                    //上传其它图片
                                    int[] idx = {0};
                                    //海报上传成功-->上传其它图片
                                    uploadImage(idx, infoBean);
                                } else {
                                    uploadPosImage(infoBean);
                                    if (mPublishCallback != null) {
                                        mPublishCallback.srcUpLoadResult(false, imagePath, object.optString("errmsg"));
                                    }
                                    notifyUpload(imagePath, "图片上传失败", false, object.optString("errmsg"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } else {
            //上传其它图片
            int[] idx = {0};
            uploadImage(idx, infoBean);
        }
    }

    /**
     * 队列上传发布信息里的图片
     *
     * @param idx
     * @param infoBean
     */
    private void uploadImage(final int[] idx, final PublishInfoBean infoBean) {
        if (idx[0] >= infoBean.getImages().size()) {
            //图片上传完--->上传视频

//            publishInfoToServer(infoBean);
            if(!TextUtils.isEmpty(infoBean.getVideoUri())){
                uploadDisVideo(infoBean);
            }else {
                publishInfoToServer(infoBean);
            }
            return;
        }
        final String imagePath = infoBean.getImages().get(idx[0]);
        if (!TextUtils.isEmpty(imagePath)) {
            uploadImage(imagePath, UrlManager.getUrl(UrlManager.UrlName.UPLOAD_IMAGE),
                    infoBean.getUserId(), new OkHttpClientManager.ResultCallback<String>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            LogUtil.e(TAG, "onError uploadImage path:" + imagePath + " ERR");
                            e.printStackTrace();

                            if (mPublishCallback != null) {
                                try {
                                    mPublishCallback.srcUpLoadResult(false, imagePath, null);
                                } catch (RemoteException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            notifyUpload(imagePath, "图片上传失败", false, "");
                            //图片上传失败,远程连接指定为空地址
                            uploadImage(idx, infoBean);//上传失败继续上传
                            //infoBean.getImages().set(idx[0], "");
                            //继续上传后面的图片
                            //idx[0]++;
                            //uploadImage(idx, infoBean);
                        }

                        @Override
                        public void onResponse(String response) {
                            LogUtil.i(TAG, "onResponse uploadImage response:" + response);
                            try {
                                JSONObject object = new JSONObject(response);
                                if (object.optString("code").equals("0")) {
                                    String imageUrl = object.optString("filePath");
                                    String smallImgUrl = object.optString("smallImg");//服务器返回小图地址
                                    infoBean.getImages().set(idx[0], imageUrl);
                                    infoBean.getSmallImg().set(idx[0], smallImgUrl);
                                    if (mPublishCallback != null) {
                                        mPublishCallback.srcUpLoadResult(true, imagePath, imageUrl);
                                    }
                                    notifyUpload(imagePath, "图片上传成功", true, null);
                                } else {
                                    //infoBean.getImages().set(idx[0], "");
                                    uploadImage(idx, infoBean);
                                    if (mPublishCallback != null) {
                                        mPublishCallback.srcUpLoadResult(false, imagePath, object.optString("errmsg"));
                                    }
                                    notifyUpload(imagePath, "图片上传失败", false, object.optString("errmsg"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                            idx[0]++;
                            uploadImage(idx, infoBean);
                        }
                    });
        }
    }

    private void notifyUpload(String srcPath, String title, boolean success, String content) {
        //从系统服务中获得通知管理器
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //定义notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setContentTitle(title)
                .setContentText(srcPath + (success ? " 上传成功" : " 上传失败") + (success ? "" : content))
                .setAutoCancel(false).setSmallIcon(R.mipmap.ic_launcher).setDefaults(Notification.DEFAULT_ALL);
        Notification notif = builder.build();
        nm.notify(1, notif);
    }

    private void notifyPublish(String publishTitle, boolean success, String content) {
        //从系统服务中获得通知管理器
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //定义notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setContentTitle(success ? "发布成功" : "发布失败")
                .setContentText(publishTitle + (success ? " 发布成功" : " 发布失败") + (success ? "" : content))
                .setAutoCancel(false).setSmallIcon(R.mipmap.ic_launcher).setDefaults(Notification.DEFAULT_ALL);
        Notification notif = builder.build();
        nm.notify(2, notif);
    }

    private void publishInfoToServer(final PublishInfoBean infoBean) {
        MineParser.publishInfos(infoBean, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e(TAG, "publishInfoToServer ERR:" + request.toString());
                e.printStackTrace();
                if (mPublishCallback != null) {
                    try {
                        mPublishCallback.publishResult(-1, e.getMessage());
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                }
                notifyPublish(infoBean.getType().getName() + ":" + infoBean.getTitle(), false, "");
                doPublishInfo();
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, "publishInfoToServer:" + response);
                try {
                    if (mPublishCallback != null) {
                        JSONObject object = new JSONObject(response);
                        mPublishCallback.publishResult(object.optInt("code"), object.optString("errmsg"));
                    }
                    notifyPublish(infoBean.getType().getName() + ":" + infoBean.getTitle(), true, "");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                mPublishInfoList.remove(infoBean);
                doPublishInfo();
            }
        });
    }

    private void publishVideoToServer(final PublishVideoBean videoBean) {
        BrowseParser.publishVideo(videoBean, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e(TAG, "publishVideoToServer ERR:" + request.toString());
                e.printStackTrace();
                if (mPublishCallback != null) {
                    try {
                        mPublishCallback.publishResult(-1, "视频发布失败");
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                }
                notifyPublish(videoBean.getTitle(), false, "");
                doPublishVideo();
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, "publishVideoToServer:" + response);
                try {
                    if (TextUtils.isEmpty(response)) {
                        if (mPublishCallback != null) {
                            mPublishCallback.publishResult(-1, "视频发布失败");
                        }
                        notifyUpload(videoBean.getVideoUrl(), "视频上传失败", false, "");
                    } else {
                        JSONObject object = new JSONObject(response);
                        if (mPublishCallback != null) {
                            mPublishCallback.publishResult(object.optInt("code"), object.optString("msg"));
                        }
                        notifyPublish(videoBean.getTitle(), true, object.optString("msg"));
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mPublishVideoList.remove(videoBean);
                doPublishVideo();
            }
        });
    }
}
