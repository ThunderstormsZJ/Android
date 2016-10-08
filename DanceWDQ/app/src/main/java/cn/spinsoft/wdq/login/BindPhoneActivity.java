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
import cn.spinsoft.wdq.login.biz.UserThird;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.utils.StringUtils;
import cn.spinsoft.wdq.widget.VerifyButton;

/**
 * Created by zhoujun on 2016-4-11.
 */
public class BindPhoneActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    private final static String TAG = BindPhoneActivity.class.getSimpleName();

    private EditText mMobileEt, mVerifyEt;
    private VerifyButton mVerifyBtn;
    private Button mDoLoginBtn;

    private UserThird userThird;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bindphone;
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView backTx = (TextView) findViewById(R.id.base_title_back);
        backTx.setText("返回");
        TextView titleTx = (TextView) findViewById(R.id.base_title_name);
        titleTx.setText("绑定手机号");
        TextView tipTx = (TextView) findViewById(R.id.bindphone_pswtip);
        tipTx.setText(StringUtils.packageSizeString(getString(R.string.bindphone_tip), 5, 11, 19));

        mMobileEt = (EditText) findViewById(R.id.bindphone_mobile);
        mVerifyBtn = (VerifyButton) findViewById(R.id.bindphone_verify_btn);
        mVerifyEt = (EditText) findViewById(R.id.bindphone_verify_input);
        mDoLoginBtn = (Button) findViewById(R.id.bindphone_dologin);

        mVerifyBtn.setOnClickListener(this);
        backTx.setOnClickListener(this);
        mVerifyBtn.addTextChangedListener(this);
        mDoLoginBtn.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userThird = getIntent().getParcelableExtra(Constants.Strings.USER_LOGIN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_title_back:
                finish();
                break;
            case R.id.bindphone_verify_btn:
                if (mMobileEt.getText().length() != 11) {
                    Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                } else {
                    mVerifyBtn.setEnabled(false);
                    String mobile = mMobileEt.getText().toString();
                    new AsyncVerify().execute(mobile);
                }
                break;
            case R.id.bindphone_dologin:
                userThird.setTelphone(mMobileEt.getText().toString());
                userThird.setvCode(mVerifyEt.getText().toString());
                new AsyncDoBind().execute(userThird);
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Editable verifyText = mVerifyEt.getText();
        if (!TextUtils.isEmpty(verifyText.toString())) {
            if (verifyText.length() == 6) {
                mDoLoginBtn.setEnabled(true);
            }
        }
    }

    class AsyncVerify extends AsyncTask<String, Integer, SimpleResponse> {
        @Override
        protected SimpleResponse doInBackground(String... params) {
            return LoginParser.getVerify(params[0]);
        }

        @Override
        protected void onPostExecute(SimpleResponse simpleResponse) {
            if (simpleResponse != null) {
                if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                    mVerifyBtn.start();
                    Toast.makeText(BindPhoneActivity.this, R.string.toast_login_verify_success, Toast.LENGTH_SHORT).show();
                } else {
                    mVerifyBtn.setEnabled(true);
                    Toast.makeText(BindPhoneActivity.this, simpleResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(BindPhoneActivity.this, R.string.toast_login_verify_unsuccess, Toast.LENGTH_SHORT).show();
            }
        }
    }

    class AsyncDoBind extends AsyncTask<UserThird, Integer, UserLogin> {

        @Override
        protected UserLogin doInBackground(UserThird... params) {
            return LoginParser.bindPhoneLogin(params[0]);
        }

        @Override
        protected void onPostExecute(UserLogin userLogin) {
            if (userLogin != null && userLogin.getResponse().getCode() == SimpleResponse.SUCCESS) {
                SharedPreferencesUtil.getInstance(BindPhoneActivity.this).saveLoginUser(userLogin);
                Toast.makeText(BindPhoneActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                BindPhoneActivity.this.finish();
            }
        }
    }
}
