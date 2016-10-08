package cn.spinsoft.wdq.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.login.biz.LoginParser;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.SecurityUtils;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;

/**
 * Created by hushujun on 16/1/15.
 */
public class UpdatePWDActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = UpdatePWDActivity.class.getSimpleName();
    private EditText mOldPwdEt, mNewPwdEt, mNewPwd2Et;
    private UserLogin mUserLogin;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_update_pwd;
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView backTx = (TextView) findViewById(R.id.base_title_back);
        TextView titleTx = (TextView) findViewById(R.id.base_title_name);
        titleTx.setText("修改密码");
        mOldPwdEt = (EditText) findViewById(R.id.update_pwd_oldpwd);
        mNewPwdEt = (EditText) findViewById(R.id.update_pwd_newpwd);
        mNewPwd2Et = (EditText) findViewById(R.id.update_pwd_newpwd_2);
        Button mConfirmBtn = (Button) findViewById(R.id.update_pwd_confirm);

        backTx.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_title_back:
                finish();
                break;
            case R.id.update_pwd_confirm:
                checkInputAndUpdate();
                break;
            default:
                break;
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mUserLogin = SharedPreferencesUtil.getInstance(this).getLoginUser();
        if (mUserLogin == null) {
            Toast.makeText(this, "用户未登陆,不能使用密码修改功能,请先登陆", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void checkInputAndUpdate() {
        String oldPwd = mOldPwdEt.getText().toString();
        if (TextUtils.isEmpty(oldPwd)) {
            Toast.makeText(this, "请输入之前的旧密码", Toast.LENGTH_SHORT).show();
            return;
        } else {
            oldPwd = SecurityUtils.passwordEncrypt(oldPwd);
            if (mUserLogin != null) {
                if (!oldPwd.equals(mUserLogin.getPwdMD5())) {
                    Toast.makeText(this, "旧密码不正确,如果已忘记密码,可以进入登陆界面点击忘记密码进行重置", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        String newPwd = mNewPwdEt.getText().toString();
        if (TextUtils.isEmpty(newPwd)) {
            Toast.makeText(this, "请设置新的密码", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (newPwd.length() < 6 || newPwd.length() > 32) {
                Toast.makeText(this, "新密码长度不合法,长度为6-32位", Toast.LENGTH_LONG).show();
                return;
            }
            if (SecurityUtils.passwordEncrypt(newPwd).equals(oldPwd)) {
                Toast.makeText(this, "新密码与旧密码一致,请重设新密码", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        String newPwd2 = mNewPwd2Et.getText().toString();
        if (TextUtils.isEmpty(newPwd2)) {
            Toast.makeText(this, "请重复新设置的密码", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (!newPwd.equals(newPwd2)) {
                Toast.makeText(this, "两次设置的密码不一致,请检查后重试", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        new AsyncUpdate().execute(mUserLogin.getMobile(), oldPwd, SecurityUtils.passwordEncrypt(newPwd));
    }

    class AsyncUpdate extends AsyncTask<String, Integer, SimpleResponse> {

        @Override
        protected SimpleResponse doInBackground(String... params) {
            return LoginParser.updatePwd(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(SimpleResponse simpleResponse) {
            if (simpleResponse != null) {
                if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                    Toast.makeText(UpdatePWDActivity.this, simpleResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdatePWDActivity.this, LoginNewActivity.class);
                    if (mUserLogin != null) {
                        intent.putExtra(Constants.Strings.USER_MOBILE, mUserLogin.getMobile());
                    }
                    startActivity(intent);
                    SharedPreferencesUtil.getInstance(UpdatePWDActivity.this).saveLoginUser(null);
                    finish();
                }
            }
        }
    }
}
