package cn.spinsoft.wdq.login.frag;

import android.content.Intent;
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

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.login.LoginActivity;
import cn.spinsoft.wdq.login.ReplacePWDActivity;
import cn.spinsoft.wdq.login.biz.LoginParser;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.mine.biz.MineHandler;
import cn.spinsoft.wdq.service.LocationOnMain;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SecurityUtils;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;

/**
 * Created by hushujun on 15/11/2.
 */
public class LoginFrag extends BaseFragment implements View.OnClickListener, TextWatcher {
    private static final String TAG = LoginFrag.class.getSimpleName();
    private EditText mMobileEt, mPwdEt;
    private Button mLoginBtn, mWechatBtn, mQQBtn;
    private TextView mForgotTx;
    private String openid, nickname, headurl, sex, tl_type;


    @Override
    protected int getLayoutId() {
        return R.layout.frag_login;
    }

    @Override
    protected void initViewAndListener(View root, Bundle savedInstanceState) {
        mMobileEt = (EditText) root.findViewById(R.id.login_mobile);
        mPwdEt = (EditText) root.findViewById(R.id.login_pwd);
        mLoginBtn = (Button) root.findViewById(R.id.login_login);
        mWechatBtn = (Button) root.findViewById(R.id.login_wechat);
        mQQBtn = (Button) root.findViewById(R.id.login_qq);
        mForgotTx = (TextView) root.findViewById(R.id.login_forget_pwd);

        mLoginBtn.setOnClickListener(this);
        mWechatBtn.setOnClickListener(this);
        mQQBtn.setOnClickListener(this);
        mForgotTx.setOnClickListener(this);

        mMobileEt.addTextChangedListener(this);
        mPwdEt.addTextChangedListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMobileEt.setText(getActivity().getIntent().getStringExtra(Constants.Strings.USER_MOBILE));
    }

    @Override
    public void onClick(View v) {
        LogUtil.d(TAG, "onClick:" + v);
        switch (v.getId()) {
            case R.id.login_login:
                String mobile = mMobileEt.getText().toString();
                String pwd = SecurityUtils.passwordEncrypt(mPwdEt.getText().toString());
                double location[] = LocationOnMain.getInstance().getLocation();
                new AsyncLogin().execute(mobile, pwd, String.valueOf(location[0]), String.valueOf(location[1]));
                break;
            case R.id.login_wechat://微信登录
                ((LoginActivity)getActivity()).loginByThird(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.login_qq://qq登录
                ((LoginActivity)getActivity()).loginByThird(SHARE_MEDIA.QQ);
                break;
            case R.id.login_forget_pwd:
                Intent intent = new Intent(getActivity(), ReplacePWDActivity.class);
                String phone = mMobileEt.getText().toString();
                if (!TextUtils.isEmpty(phone)) {
                    intent.putExtra(Constants.Strings.USER_PHONE, phone);
                }
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // TODO: 15/11/4 to do nothing  
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO: 15/11/4 to do nothing 
    }

    @Override
    public void afterTextChanged(Editable s) {
        int length;
        length = mMobileEt.getText().length();
        if (length == 11) {//手机号码长度11位
            length = mPwdEt.getText().length();
            if (length >= 6 && length <= 32) {//密码长度6-32位
                mLoginBtn.setEnabled(true);
                return;
            }
        }
        mLoginBtn.setEnabled(false);
    }

    class AsyncLogin extends AsyncTask<String, Integer, UserLogin> {

        @Override
        protected UserLogin doInBackground(String... params) {
            return LoginParser.login(params[0], params[1], params[2], params[3]);
        }

        @Override
        protected void onPostExecute(UserLogin userLogin) {
            if (userLogin != null) {
                SimpleResponse response = userLogin.getResponse();
                if (response.getCode() == SimpleResponse.SUCCESS) {
                    Toast.makeText(getActivity(), R.string.toast_login_success, Toast.LENGTH_SHORT).show();
                    userLogin.setMobile(mMobileEt.getText().toString());
                    userLogin.setPwdMD5(SecurityUtils.passwordEncrypt(mPwdEt.getText().toString()));
                    SharedPreferencesUtil.getInstance(getActivity()).saveLoginUser(userLogin);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), R.string.toast_login_unsuccess, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
