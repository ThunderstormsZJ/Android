package cn.spinsoft.wdq.login.frag;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.effect.AnimatorEffect;
import cn.spinsoft.wdq.enums.UserType;
import cn.spinsoft.wdq.login.PerfectInfoActivity;
import cn.spinsoft.wdq.login.RegisterNewActivity;
import cn.spinsoft.wdq.login.biz.LoginParser;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.service.LocationOnMain;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.SecurityUtils;
import cn.spinsoft.wdq.widget.RadioGroup;
import cn.spinsoft.wdq.widget.VerifyButton;

/**
 * Created by zhoujun on 15/11/2.
 */
public class RegisterFrag extends BaseFragment implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener {
    private static final String TAG = RegisterFrag.class.getSimpleName();
    private RadioGroup mTypeRg;
    private TextView mLabelSlideTx;
    private EditText mMobileEt, mVerifyEt, mPwdEt;
    private Button mRegisterBtn, mUserReadBtn;
    private VerifyButton mVerifyBtn;
    private CheckBox mAgreementCB;
    private ProtocolFrag mProtocolFrag;
    private UserType mChooseType = UserType.PERSONAL;
    private TextView mTypeTipTx;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_register;
    }

    @Override
    protected void initViewAndListener(View root, Bundle savedInstanceState) {
        mMobileEt = (EditText) root.findViewById(R.id.register_mobile);
        mVerifyEt = (EditText) root.findViewById(R.id.register_verify_et);
        mPwdEt = (EditText) root.findViewById(R.id.register_pwd);
        mVerifyBtn = (VerifyButton) root.findViewById(R.id.register_verify_btn);
        mUserReadBtn = (Button) root.findViewById(R.id.register_userread);
        mRegisterBtn = (Button) root.findViewById(R.id.register_doregiste);
        mAgreementCB = (CheckBox) root.findViewById(R.id.register_agreement);
        mTypeRg = (cn.spinsoft.wdq.widget.RadioGroup) root.findViewById(R.id.register_labels);
        mLabelSlideTx = (TextView) root.findViewById(R.id.discover_slide);
        mTypeTipTx = (TextView) root.findViewById(R.id.register_choose_tip);

        mVerifyBtn.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);
//        mAgreementCB.setOnCheckedChangeListener(this);
        mUserReadBtn.setOnClickListener(this);
        mTypeRg.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register_verify_btn) {
            if (mMobileEt.getText().length() != 11) {
                Toast.makeText(getActivity(), "请输入正确的手机号,长度为11位", Toast.LENGTH_SHORT).show();
            } else {
                mVerifyBtn.setEnabled(false);
                String mobile = mMobileEt.getText().toString();
                new AsyncVerify().execute(mobile);
            }
        } else if (v.getId() == R.id.register_doregiste) {//注册
            if (checkInput()) {
                String pwd1 = mPwdEt.getText().toString();
                String mobile = mMobileEt.getText().toString();
                String pwd = SecurityUtils.passwordEncrypt(pwd1);//加密
                String verify = mVerifyEt.getText().toString();
                double[] location = LocationOnMain.getInstance().getLocation();
                new AsyncRegister().execute(mobile, pwd, verify, mChooseType.getValue(),
                        String.valueOf(location[0]), String.valueOf(location[1]));
                mRegisterBtn.setEnabled(false);
            }
        } else if (v.getId() == R.id.register_userread) {//用户协议
            if (mProtocolFrag == null) {
                mProtocolFrag = new ProtocolFrag();
            }
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.register_child_container, mProtocolFrag).commit();
            ((RegisterNewActivity) getActivity()).setBackTx();
        }
    }

  /*  @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            checkInput();
        } else {
            mRegisterBtn.setEnabled(false);
        }
    }*/

    private boolean checkInput() {
        int length;
        length = mMobileEt.getText().length();
        if (mAgreementCB.isChecked()) {
            if (length == 11) {//手机号码长度11位
                length = mPwdEt.getText().length();
                if (length >= 6 && length <= 32) {//密码长度6-32位
                    length = mVerifyEt.getText().length();
                    if (length == 6) {//验证码长度6位
                        return true;
                    } else {
                        Toast.makeText(getContext(), "验证码不正确", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "密码至少6位", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "手机号码不正确", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "请先同意协议", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onCheckedChanged(cn.spinsoft.wdq.widget.RadioGroup group, @IdRes int checkedId) {
        View view = group.findViewById(checkedId);
        AnimatorEffect.smoothHorizontalSlideTo(mLabelSlideTx, view);
        if (checkedId == R.id.register_choose_person) {
            mChooseType = UserType.PERSONAL;
            mTypeTipTx.setText("注册个人账号");
        } else if (checkedId == R.id.register_choose_org) {
            mTypeTipTx.setText("注册机构账号");
            mChooseType = UserType.STORE;
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
                    Toast.makeText(getActivity(), R.string.toast_login_verify_success, Toast.LENGTH_SHORT).show();
                } else {
                    mVerifyBtn.setEnabled(true);
                    Toast.makeText(getActivity(), simpleResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), R.string.toast_login_verify_unsuccess, Toast.LENGTH_SHORT).show();
            }
        }
    }

    class AsyncRegister extends AsyncTask<String, Integer, UserLogin> {

        @Override
        protected UserLogin doInBackground(String... params) {
            return LoginParser.register(params[0], params[1], params[2], params[3], params[4], params[5]);
        }

        @Override
        protected void onPostExecute(UserLogin userLogin) {
            if (userLogin != null) {
                if (userLogin.getResponse().getCode() == SimpleResponse.SUCCESS) {
                    Toast.makeText(getActivity(), R.string.toast_register_success, Toast.LENGTH_SHORT).show();
                    userLogin.setMobile(mMobileEt.getText().toString());
                    userLogin.setPwdMD5(SecurityUtils.passwordEncrypt(mPwdEt.getText().toString()));
                    getActivity().finish();

                    //完善资料
                    Intent intent = new Intent(getActivity(), PerfectInfoActivity.class);
                    intent.putExtra(Constants.Strings.USER_LOGIN, (Parcelable) userLogin);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), userLogin.getResponse().getMessage(), Toast.LENGTH_SHORT).show();
                }
                mRegisterBtn.setEnabled(true);
            } else {
                Toast.makeText(getActivity(), R.string.toast_register_unsuccess, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
