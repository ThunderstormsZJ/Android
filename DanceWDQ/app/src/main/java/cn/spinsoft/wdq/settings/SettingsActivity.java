package cn.spinsoft.wdq.settings;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.login.UpdatePWDActivity;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.mine.component.MineInfoActivity;
import cn.spinsoft.wdq.mine.component.OrgInfoActivity;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.FileUtils;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SecurityUtils;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.utils.StringUtils;
import cn.spinsoft.wdq.widget.ConfirmDialog;

/**
 * Created by zhoujun on 15/12/29.
 */
public class SettingsActivity extends BaseActivity implements View.OnClickListener,
        FileUtils.OnFileDeleteCallback, Observer, ConfirmDialog.OnConfirmClickListenter {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    private TextView mCacheSizeTx, mCancelTx/*,mAboutTx*/;
    private UserLogin mUserLogin;
    private ConfirmDialog confirmDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView back = (TextView) findViewById(R.id.setting_back);
        TextView material = (TextView) findViewById(R.id.setting_material);
        TextView pwd = (TextView) findViewById(R.id.setting_pwd);
        RelativeLayout cache = (RelativeLayout) findViewById(R.id.setting_cache_line);
        mCacheSizeTx = (TextView) findViewById(R.id.setting_cache_size);
        mCancelTx = (TextView) findViewById(R.id.setting_cancel);
//        mAboutTx = (TextView) findViewById(R.id.setting_about);

        back.setOnClickListener(this);
        material.setOnClickListener(this);
        pwd.setOnClickListener(this);
        cache.setOnClickListener(this);
        mCancelTx.setOnClickListener(this);
//        mAboutTx.setOnClickListener(this);

        confirmDialog = new ConfirmDialog(this, ConfirmDialog.Type.LOGOUT, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferencesUtil.getInstance(this).addObserver(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        String size = FileUtils.getFileSize(getCacheDir());
        mCacheSizeTx.setText(size);
        mUserLogin = SharedPreferencesUtil.getInstance(this).getLoginUser();
        if (mUserLogin == null) {
            mCancelTx.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        SharedPreferencesUtil.getInstance(this).deleteObserver(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_back:
                finish();
                break;
            case R.id.setting_material:
                if (SecurityUtils.isUserValidity(this, mUserLogin)) {
                    if (mUserLogin.getOrgId() > 0) {
                        Intent orgIntent = new Intent(this, OrgInfoActivity.class);
                        OrgInfoActivity.modeType = Constants.Strings.EDIT_MODE;
                        orgIntent.putExtra(Constants.Strings.ORG_ID, mUserLogin.getOrgId());
                        startActivity(orgIntent);
                    } else {
                        Intent miIntent = new Intent(this, MineInfoActivity.class);
                        MineInfoActivity.modeType = Constants.Strings.EDIT_MODE;
                        miIntent.putExtra(Constants.Strings.USER_ID, mUserLogin.getUserId());
                        startActivity(miIntent);
                    }
                }
                break;
            case R.id.setting_pwd:
                if (SecurityUtils.isUserValidity(this, mUserLogin)) {
                    startActivity(new Intent(this, UpdatePWDActivity.class));
                }
                break;
            case R.id.setting_cache_line:
                LogUtil.w(TAG, getCacheDir() + "");
                new ConfirmDialog(this, ConfirmDialog.Type.CLEARCACHE, new ConfirmDialog.OnConfirmClickListenter() {
                    @Override
                    public void onConfirmClick(View v) {
                        if (v.getId() == R.id.dia_confirm_confirm) {
                            FileUtils.deleteFiles(getCacheDir(), SettingsActivity.this);
                            String imgTempPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + StringUtils.getApplicationName(SettingsActivity.this);
                            File imgTempFile = new File(imgTempPath);
                            if (imgTempFile.exists()) {
                                FileUtils.deleteFiles(imgTempFile, SettingsActivity.this);
                            }
                        }
                    }
                }).show();
                break;
          /*  case R.id.setting_about:
                startActivity(new Intent(this,AboutActivity.class));
                break;*/
            case R.id.setting_cancel:
                confirmDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public void fileDeleteComplete(boolean success) {
        if (success) {
            Toast.makeText(this, "缓存已清除", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "缓存清理失败,请重试", Toast.LENGTH_SHORT).show();
        }
        mCacheSizeTx.setText(FileUtils.getFileSize(getCacheDir()));
    }

    @Override
    public void update(Observable observable, Object data) {
        mUserLogin = (UserLogin) data;
        if (mUserLogin == null) {
            mCancelTx.setVisibility(View.GONE);
        } else {
            mCancelTx.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConfirmClick(View v) {
        switch (v.getId()) {
            case R.id.dia_confirm_confirm:
                SharedPreferencesUtil.getInstance(this).saveLoginUser(null);

                NimUIKit.setAccount("");
                NIMClient.getService(AuthService.class).logout();
//                Toast.makeText(this, "注销登录成功", Toast.LENGTH_SHORT).show();
                this.finish();
                break;
            default:
                confirmDialog.dismiss();
                break;
        }
    }
}
