package cn.spinsoft.wdq.upload;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.upload.Const.FileType;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.UrlManager;

/**
 * 向服务器请求更新签名的异步任务，用于演示客户端如何获取签名<br/>
 * 作用: 1.拉取上传使用的签名 2.拉取用于文件资源操作(删除、复制等)的单次有效签名<br/>
 * <br/>
 * <p/>
 * 注意：业务修改了BizService.APPID之后，需要自己实现相关的签名拉取逻辑<br/>
 */
public class UpdateSignTask extends AsyncTask<Void, Integer, String> {
    private static final String TAG = UpdateSignTask.class.getSimpleName();

    private String mAppid;
    private String mBucket;
    private String mFileId;
    private FileType mFileType;

    private boolean mbSingleSign;

    private OnGetSignListener mListener;

    public interface OnGetSignListener {
        void onSign(String sign);
    }

    /**
     * 构造函数
     *
     * @param appid    业务APPID
     * @param bucket   业务使用的Bucket
     * @param fileId   文件ID； 传入ID为空时，则拉取全局有效的签名；传入ID不为空，则拉取单次有效签名
     * @param listener 任务结果回调
     */
    public UpdateSignTask(String appid, FileType fileType, String bucket,
                          String fileId, OnGetSignListener listener) {
        mFileType = fileType;
        mFileId = fileId;
        mBucket = bucket;
        mAppid = appid;
        mBucket = bucket;
        mListener = listener;
        mbSingleSign = (!TextUtils.isEmpty(fileId));
    }

    @Override
    protected void onPreExecute() {
// TODO: 15/11/17 to do nothing here
    }

    private String encodeUrl(String url) {
        if (TextUtils.isEmpty(url))
            return url;
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    @Override
    protected String doInBackground(Void... params) {
        String cgi = UrlManager.getUrl(UrlManager.UrlName.UPLOAD_SIGNATURE);

        if (mbSingleSign) {
            String fileid = "/" + mAppid + "/" + mBucket + mFileId;
            if (mFileType == FileType.Photo) {
                fileid = mFileId;
            }

            cgi += "&fileid=" + encodeUrl(fileid);
        }

        try {
            URL url = new URL(cgi);
            Log.i(TAG, "update sign cgi:" + cgi);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();

            byte[] mSocketBuf = new byte[4 * 1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int count = 0;
            while ((count = in.read(mSocketBuf, 0, mSocketBuf.length)) > 0) {
                baos.write(mSocketBuf, 0, count);
            }

            String config = new String(baos.toByteArray());
            Log.i(TAG, "update sign config:" + config);
            JSONObject jsonData = new JSONObject(config);
            return jsonData.optString("signaure");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String sign) {
//        Log.i(TAG, "update sign response:" + sign);

        if (!TextUtils.isEmpty(sign)) {
            LogUtil.d(TAG, "更新签名成功");
            // mFileId为空说明是长期有效的签名，保存之；否则为单次有效签名，无需保存
            if (!mbSingleSign)
                BizService.getInstance().updateSign(mAppid, mBucket, sign);
        } else {
            LogUtil.d(TAG, "更新签名失败");
        }

        if (mListener != null)
            mListener.onSign(sign);
    }
}
