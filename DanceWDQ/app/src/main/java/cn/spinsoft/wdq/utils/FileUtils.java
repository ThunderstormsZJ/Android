package cn.spinsoft.wdq.utils;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by hushujun on 15/12/29.
 */
public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    public static String getFileSize(File file) {
        long fileSize = getFolderSize(file);
        LogUtil.w(TAG, "getFileSize:" + fileSize);
        return calculateFileSize(fileSize);
    }

    private static long getFolderSize(File file) {
        long size = 0;
        if (file != null && file.exists()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    size += getFolderSize(f);
                } else {
                    size += f.length();
                }
            }
        }
        return size;
    }

    public interface OnFileDeleteCallback {
        void fileDeleteComplete(boolean success);
    }

    public static void deleteFiles(File dir, OnFileDeleteCallback callback) {
        boolean success = deleteFiles(dir);
        if (callback != null) {
            callback.fileDeleteComplete(success);
        }
    }

    private static boolean deleteFiles(File dir) {
        if (dir.exists()) {
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                for (File file : files) {
                    boolean success = deleteFiles(file);
                    if (!success) {
                        return false;
                    }
                }
            } else {
                return dir.delete();
            }
        } else {
            LogUtil.e(TAG, "文件或者文件夹不存在，请检查路径是否正确！");
        }
        return true;
    }

    public static String getVideoTimeLength(String path) {
        if (TextUtils.isEmpty(path)) {
            LogUtil.e(TAG, "getVideoTimeLength: path is empty");
            return "时长: 0 秒";
        }
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        String timeLength = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        mmr.release();
        if (TextUtils.isDigitsOnly(timeLength)) {
            int length = Integer.valueOf(timeLength);
            int s = 1 * 1000;
            int m = s * 60;
            int h = m * 60;
            if (length > h) {
                int hour = length / h;
                int min = length % h / m;
                int sec = length % h % m / s;
                timeLength = "时长: " + hour + ":" + min + ":" + sec;
            } else if (length > m) {
                int min = length / m;
                int sec = length % m / s;
                timeLength = "时长: " + min + ":" + sec;
            } else {
                int sec = length / s;
                timeLength = "时长: " + sec + " 秒";
            }
        } else {
            timeLength = "时长: " + timeLength;
        }
        return timeLength;
    }

    public static String getVideoSizeLength(String path) {
        if (TextUtils.isEmpty(path)) {
            LogUtil.e(TAG, "getVideoSizeLength: path is empty");
            return "0kb";
        }
        File file = new File(path);
        if (!file.exists()) {
            LogUtil.e(TAG, "getVideoSizeLength: the path related is no a file");
            return "0kb";
        }
        long length = file.length();
        return "大小:" + calculateFileSize(length);
    }

    public static String getVideoNameNoPostFix(String path) {
        if (TextUtils.isEmpty(path)) {
            LogUtil.e(TAG, "getVideoName is null");
            return null;
        }
        File file = new File(path);
        if (!file.exists()) {
            LogUtil.e(TAG, path + ":this file is not exists");
            return null;
        }
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") > 0) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }

    public static String calculateFileSize(double length) {
        int K = 1 * 1024;
        int M = K * 1024;
        int G = M * 1024;
        String sizeLength;
        if (length > G) {
            sizeLength = String.format("约 %.2f G", length / G);
        } else if (length > M) {
            sizeLength = String.format("约 %.2f M", length / M);
        } else {
            sizeLength = String.format("约 %.2f K", length / K);
        }
        return sizeLength;
    }

    public static String createImgTempFile(String imgName, Context context) {
        String imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/"
                + StringUtils.getApplicationName(context);
        File file = new File(imagePath, imgName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imagePath + "/" + imgName;
    }
}
