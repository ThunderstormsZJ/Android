package cn.spinsoft.wdq.login;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import cn.spinsoft.wdq.utils.UrlManager;
import cn.spinsoft.wdq.widget.VerifyButton;

/**
 * Created by hushujun on 15/12/30.
 */
public class ReplacePWDActivity extends BaseActivity implements TextWatcher, View.OnClickListener {
    private static final String TAG = ReplacePWDActivity.class.getSimpleName();

    private EditText mMobileEt, mVerifyEt, mPwdEt/*, mPwd2Et*/;
    private TextView mBackTx;
    private Button mConfirmBtn;
    private VerifyButton mVerifyBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_replace_pwd;
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView titileName = (TextView) findViewById(R.id.base_title_name);
        titileName.setText(R.string.login_rest_pwd);
        mBackTx = (TextView) findViewById(R.id.base_title_back);
        mMobileEt = (EditText) findViewById(R.id.replace_pwd_mobile);
        mVerifyEt = (EditText) findViewById(R.id.replace_pwd_verify_et);
        mPwdEt = (EditText) findViewById(R.id.replace_pwd_pwd);
        mVerifyBtn = (VerifyButton) findViewById(R.id.replace_pwd_verify_btn);
        mConfirmBtn = (Button) findViewById(R.id.replace_pwd_confirm);

        mMobileEt.addTextChangedListener(this);
        mVerifyEt.addTextChangedListener(this);
        mPwdEt.addTextChangedListener(this);

        mBackTx.setOnClickListener(this);
        mVerifyBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        String phone = getIntent().getStringExtra(Constants.Strings.USER_PHONE);
        if (TextUtils.isEmpty(phone)) {
            UserLogin userLogin = SharedPreferencesUtil.getInstance(this).getLoginUser();
            if (userLogin != null) {
                phone = userLogin.getMobile();
            }
        }
        mMobileEt.setText(phone);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        checkInput();
    }

    private void checkInput() {
        int length;
        length = mMobileEt.getText().length();
        if (length == 11) {//手机号码长度11位
            length = mPwdEt.getText().length();
            if (length >= 6 && length <= 32) {//密码长度6-32位
                length = mVerifyEt.getText().length();
                if (length == 6) {//验证码长度6位
                    mConfirmBtn.setEnabled(true);
                    return;
                }
            }
        }
        mConfirmBtn.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_title_back:
                finish();
                break;
            case R.id.replace_pwd_verify_btn:
                String mobile = mMobileEt.getText().toString();
                if (mobile.length() != 11) {
                    Toast.makeText(this, "请输入正确的手机号,长度为11位", Toast.LENGTH_SHORT).show();
                } else {
                    mVerifyBtn.setEnabled(false);
                    mVerifyBtn.setTextColor(getResources().getColor(R.color.gray));
                    mVerifyBtn.start();
                    new AsyncVerify().execute(mobile);
                }
                break;
            case R.id.replace_pwd_confirm:
                String pwd1 = mPwdEt.getText().toString();
//                String pwd2 = mPwd2Et.getText().toString();
                new AsyncReplacePwd().execute(mMobileEt.getText().toString(),
                        SecurityUtils.passwordEncrypt(pwd1), mVerifyEt.getText().toString());
                break;
            default:
                break;
        }
    }

    class AsyncVerify extends AsyncTask<String, Integer, SimpleResponse> {

        @Override
        protected SimpleResponse doInBackground(String... params) {
            return LoginParser.getVerifyCode(UrlManager.getUrl(UrlManager.UrlName.VERIFY_CODE_UPDATE), params[0]);
        }

        @Override
        protected void onPostExecute(SimpleResponse response) {
            if (response != null) {
                if (response.getCode() == SimpleResponse.SUCCESS) {
                    Toast.makeText(ReplacePWDActivity.this, R.string.toast_login_verify_success,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ReplacePWDActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ReplacePWDActivity.this, R.string.toast_login_verify_unsuccess,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    class AsyncReplacePwd extends AsyncTask<String, Integer, UserLogin> {

        @Override
        protected UserLogin doInBackground(String... params) {
            return LoginParser.replacePwd(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(UserLogin response) {
            if (response != null) {
                Toast.makeText(ReplacePWDActivity.this, R.string.toast_register_success,
                        Toast.LENGTH_SHORT).show();
                response.setMobile(mMobileEt.getText().toString());
                response.setPwdMD5(SecurityUtils.passwordEncrypt(mPwdEt.getText().toString()));
                SharedPreferencesUtil.getInstance(ReplacePWDActivity.this).saveLoginUser(response);
                finish();
            } else {
                Toast.makeText(ReplacePWDActivity.this, "重置密码失败,请检查您的输入或稍后重试",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
