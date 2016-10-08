package cn.spinsoft.wdq.settings;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.utils.Constants;

/**
 * Created by zhoujun on 2016-4-15.
 */
public class AboutActivity extends BaseActivity {
    private final static String TAG = AboutActivity.class.getSimpleName();

    private TextView mCallPhoneTx;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView backTx = (TextView) findViewById(R.id.base_title_back);
        backTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView titleTx = (TextView) findViewById(R.id.base_title_name);
        titleTx.setText("关于我们");


        mCallPhoneTx = (TextView) findViewById(R.id.about_callphone);
        mCallPhoneTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, Constants.Ints.PERMISSION_CALL_PHONE);
                AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setTitle("提示");
                builder.setMessage("是否拨打客服电话  " + mCallPhoneTx.getText().toString())
                        .setPositiveButton("取消", null)
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, //就是对话框控件
                                                int which) {
                                Intent phoneIntent = new Intent("android.intent.action.CALL",
                                        Uri.parse("tel:" + mCallPhoneTx.getText().toString()));
                                startActivity(phoneIntent);
                            }
                        });
                builder.setCancelable(false);//点击对话框以外的区域，对话框不会关闭
                AlertDialog d = builder.create();//创建对话框
                d.show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.Ints.PERMISSION_CALL_PHONE) {
            for (int index = 0; index < permissions.length; index++) {
                switch (permissions[index]) {
                    case Manifest.permission.CALL_PHONE:
                        if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
                            builder.setIcon(android.R.drawable.ic_dialog_alert);
                            builder.setTitle("提示");
                            builder.setMessage("是否拨打客服电话  " + mCallPhoneTx.getText().toString())
                                    .setPositiveButton("取消", null)
                                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, //就是对话框控件
                                                            int which) {
                                            Intent phoneIntent = new Intent("android.intent.action.CALL",
                                                    Uri.parse("tel:" + mCallPhoneTx.getText().toString()));
                                            startActivity(phoneIntent);
                                        }
                                    });
                            builder.setCancelable(false);//点击对话框以外的区域，对话框不会关闭
                            AlertDialog d = builder.create();//创建对话框
                            d.show();
                        } else if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                            Toast.makeText(this, "您拒绝了拨打电话权限，此功能不可用！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "应用没有拨打电话权限，此功能不可用！", Toast.LENGTH_LONG).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
