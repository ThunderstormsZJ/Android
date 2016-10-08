package cn.spinsoft.wdq.mine.component;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.service.PublishService;
import cn.spinsoft.wdq.utils.BitmapUtils;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.ContentResolverUtil;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.OkHttpClientManager;
import cn.spinsoft.wdq.utils.StringUtils;
import cn.spinsoft.wdq.utils.UrlManager;
import cn.spinsoft.wdq.widget.CameraDialog;

/**
 * Created by hushujun on 16/1/14.
 */
public abstract class ImageChooseClipActivity extends BaseActivity {
    private static final String TAG = ImageChooseClipActivity.class.getSimpleName();
    protected CameraDialog mCameraDialog;
    protected String mCameraImageName = "imageName.jpg";
    protected boolean photoUploading = false;

    private String mImagePath = "";
    protected boolean clipThenUpload = true;
    protected int userId = -1;

    protected void setImagePath(String imagePath){
        this.mImagePath = imagePath;
    }

    protected abstract
    @NonNull
    ImageView getPreviewImg();

    protected abstract void uploadResult(@Nullable String imageUrl);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.Ints.REQUEST_CODE_IMAGE_CAPTURE) {
                String imagePathCap = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/"
                        + StringUtils.getApplicationName(this) + "/" + mCameraImageName;
                imagePathSelected(imagePathCap);
            } else if (requestCode == Constants.Ints.REQUEST_CODE_IMAGE_CHOOSE) {
                String imagePathCH = ContentResolverUtil.getPath(this, data.getData());
                imagePathSelected(imagePathCH);
            } else if (requestCode == Constants.Ints.REQUEST_CODE_IMAGE_CLIPPED) {
                onImageClipped(data);
            }
        }
    }

    private void imagePathSelected(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            Toast.makeText(this, "图片选择路径为空", Toast.LENGTH_SHORT).show();
            return;
        }
        LogUtil.w(TAG, "onActivityResult:" + imagePath);
        startPhotoZoom(Uri.fromFile(new File(imagePath)));
    }

    private void startPhotoZoom(Uri uri) {
        int width = getPreviewImg().getMeasuredWidth();
        int height = getPreviewImg().getMeasuredHeight();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", height);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("return-data", true);
        /*intent.putExtra("scale", true);//黑边
        intent.putExtra("scaleUpIfNeeded", true);//黑边*/
        startActivityForResult(intent, Constants.Ints.REQUEST_CODE_IMAGE_CLIPPED);
    }

    private void onImageClipped(Intent data) {
        if (data == null) {
            Toast.makeText(this, "裁剪图片失败,请重新裁剪", Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle extras = data.getExtras();
        if (extras == null) {
            Toast.makeText(this, "裁剪图片失败,请重新裁剪", Toast.LENGTH_SHORT).show();
        }
        Bitmap photo = extras.getParcelable("data");
        final String imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/"
                + StringUtils.getApplicationName(this) + "/" + mCameraImageName;
        LogUtil.w(TAG, "onActivityResult:" + imagePath);
        BitmapUtils.saveBitmap(photo, imagePath);
        getPreviewImg().setImageURI(Uri.fromFile(new File(imagePath)));
        mImagePath = imagePath;
        if (clipThenUpload) {
            upload();
        }
    }

    protected boolean upload() {
        if (TextUtils.isEmpty(mImagePath)) {
            Toast.makeText(this, "你还没有选择好图片", Toast.LENGTH_SHORT).show();
            return false;
        }
        photoUploading = true;
        PublishService.uploadImage(mImagePath, UrlManager.getUrl(UrlManager.UrlName.MINE_INFO_UPLOAD_PHOTO),
                userId, new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        photoUploading = false;
                        LogUtil.e(TAG, "uploadImage:" + e);
                        Toast.makeText(ImageChooseClipActivity.this, "图片上传失败,请重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        photoUploading = false;
                        LogUtil.i(TAG, "uploadImage:" + response);
                        Toast.makeText(ImageChooseClipActivity.this, "图片上传成功", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject object = new JSONObject(response);
                            uploadResult(object.optString("filePath"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        return true;
    }
}
