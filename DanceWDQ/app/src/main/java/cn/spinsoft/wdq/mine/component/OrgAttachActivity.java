package cn.spinsoft.wdq.mine.component;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.ContentResolverUtil;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.StringUtils;
import cn.spinsoft.wdq.widget.CameraDialog;

/**
 * Created by zhoujun on 16/1/14.
 */
public class OrgAttachActivity extends ImageChooseClipActivity implements View.OnClickListener {
    private static final String TAG = OrgAttachActivity.class.getSimpleName();
    private ImageView mPreviewImg, mCameraImg;

    private int orgId = -1;
    private String mImageUrl = "";
    private boolean isNewImage = false;
    private ProgressDialog mProgressDia;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_org_attach;
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView backTx = (TextView) findViewById(R.id.base_title_back);
        TextView titleNameTx = (TextView) findViewById(R.id.base_title_name);
        titleNameTx.setText("机构认证");
        TextView uploadTx = (TextView) findViewById(R.id.base_title_otherfunction);
        uploadTx.setText("完成");
        mPreviewImg = (ImageView) findViewById(R.id.org_attach_preview);
        mCameraImg = (ImageView) findViewById(R.id.org_attach_camera);

        backTx.setOnClickListener(this);
        uploadTx.setOnClickListener(this);
        mCameraImg.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDia = new ProgressDialog(this);
        mProgressDia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        orgId = getIntent().getIntExtra(Constants.Strings.ORG_ID, -1);
        userId = getIntent().getIntExtra(Constants.Strings.USER_ID, -1);
        clipThenUpload = false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !isNewImage) {
            Picasso.with(this).load(Uri.parse(getIntent().getStringExtra(Constants.Strings.ORG_ATTACH)))
                    .resize(mPreviewImg.getMeasuredWidth(), mPreviewImg.getMeasuredHeight())
                    .into(mPreviewImg);
            mImageUrl = getIntent().getStringExtra(Constants.Strings.ORG_ATTACH);
        }
    }

    @NonNull
    @Override
    protected ImageView getPreviewImg() {
        return mPreviewImg;
    }

    @Override
    protected void uploadResult(@Nullable String imageUrl) {
        mImageUrl = imageUrl;
        LogUtil.w(TAG, imageUrl);
        mProgressDia.dismiss();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.Ints.REQUEST_CODE_IMAGE_NOCLIP_CHOOSE) {
            if (data != null) {
                String imagePathCH = ContentResolverUtil.getPath(this, data.getData());
                Picasso.with(this).load(Uri.fromFile(new File(imagePathCH)))
                        .resize(mPreviewImg.getMeasuredWidth(), mPreviewImg.getMeasuredHeight())
                        .into(mPreviewImg);
                isNewImage = true;
                setImagePath(imagePathCH);
            }
        }else if (requestCode == Constants.Ints.REQUEST_CODE_IMAGE_NOCLIP_CAPTURE){
            if (resultCode==RESULT_OK){
                String imagePathCap = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/"
                        + StringUtils.getApplicationName(this) + "/" + mCameraImageName;
                Picasso.with(this).load(Uri.fromFile(new File(imagePathCap)))
                        .resize(mPreviewImg.getMeasuredWidth(), mPreviewImg.getMeasuredHeight())
                        .into(mPreviewImg);
                isNewImage = true;
                setImagePath(imagePathCap);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(Constants.Strings.ORG_ATTACH, mImageUrl);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_title_back:
                finish();
                break;
            case R.id.base_title_otherfunction:
                if(upload()){
                    mProgressDia.show();
                }
//                finish();
                break;
            case R.id.org_attach_camera:
                if (mCameraDialog == null) {
                    mCameraDialog = new CameraDialog(this, CameraDialog.Type.IMAGE_ONCLIP);
                }
                mCameraImageName = "Org" + orgId + "_ATTACH_" + StringUtils.formatCurrentDateTime() + ".jpg";
                mCameraDialog.setImageName(mCameraImageName);
                mCameraDialog.show();
                break;
            default:
                break;
        }
    }
}
