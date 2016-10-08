package cn.spinsoft.wdq.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.StringUtils;

/**
 * Created by hushujun on 15/11/12.
 */
public class CameraDialog extends AlertDialog implements View.OnClickListener {
    private static final String TAG = CameraDialog.class.getSimpleName();
    private Context context;
    private Type mType = Type.VIDEO;
    private String mImageName = "imageName.jpg";

    public enum Type {
        IMAGE, VIDEO,IMAGE_ONCLIP
    }

    public CameraDialog(Context context, Type type) {
        this(context, R.style.DialogWithTransparentBackground);
        this.context = context;
        mType = type;
    }

    private CameraDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dia_camera);
        TextView recode = (TextView) findViewById(R.id.dia_camera_recode);
        recode.setOnClickListener(this);
        if (mType == Type.VIDEO) {
            recode.setText("录像");
        } else {
            recode.setText("拍照");
        }
        findViewById(R.id.dia_camera_choose).setOnClickListener(this);
        findViewById(R.id.dia_camera_cancel).setOnClickListener(this);

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dia_camera_recode:
                Intent intentRecode;
                int codeRecode;
                if (mType == Type.VIDEO) {
                    intentRecode = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    intentRecode.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    codeRecode = Constants.Ints.REQUEST_CODE_VIDEO_CAPTURE;
                } else {
                    intentRecode = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/"
                            + StringUtils.getApplicationName(getContext());
                    File file = new File(dir);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    Uri imageUri = Uri.fromFile(new File(dir, mImageName));
                    intentRecode.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    if(mType==Type.IMAGE_ONCLIP){
                        codeRecode = Constants.Ints.REQUEST_CODE_IMAGE_NOCLIP_CAPTURE;
                    }else {
                        codeRecode = Constants.Ints.REQUEST_CODE_IMAGE_CAPTURE;
                    }
                }
                try {
                    ((Activity) context).startActivityForResult(intentRecode, codeRecode);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                dismiss();
                break;
            case R.id.dia_camera_choose:
                Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                innerIntent.addCategory(Intent.CATEGORY_OPENABLE);
                int codeChoose;
                if (mType == Type.VIDEO) {
                    innerIntent.setType("video/*");
                    codeChoose = Constants.Ints.REQUEST_CODE_VIDEO_CHOOSE;
                } else if(mType == Type.IMAGE_ONCLIP){
                    innerIntent.setType("image/*");
                    codeChoose = Constants.Ints.REQUEST_CODE_IMAGE_NOCLIP_CHOOSE;
                } else {
                    innerIntent.setType("image/*");
                    codeChoose = Constants.Ints.REQUEST_CODE_IMAGE_CHOOSE;
                }
                Intent intent = Intent.createChooser(innerIntent, null);
                try {
                    ((Activity) context).startActivityForResult(intent, codeChoose);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "请安装文件管理器", Toast.LENGTH_SHORT).show();
                }
                dismiss();
                break;
            case R.id.dia_camera_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    public void setImageName(String imageName) {
        if (!TextUtils.isEmpty(imageName)) {
            this.mImageName = imageName;
        }
    }


    /**
     * if{@link #mType={@link Type#IMAGE}},your must {@link #setImageName(String)} before call {@link #show()}
     */
    @Override
    public void show() {
        super.show();
    }
}
