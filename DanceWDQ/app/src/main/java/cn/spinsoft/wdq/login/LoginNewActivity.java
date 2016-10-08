package cn.spinsoft.wdq.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.enums.PlatForm;
import cn.spinsoft.wdq.enums.Sex;
import cn.spinsoft.wdq.login.biz.LoginParser;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.login.biz.UserThird;
import cn.spinsoft.wdq.mine.biz.MineHandler;
import cn.spinsoft.wdq.service.LocationOnMain;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SecurityUtils;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;

/**
 * Created by zhoujun on 2016-3-31.
 */
public class LoginNewActivity extends BaseActivity implements View.OnClickListener, UMAuthListener,
        Handler.Callback {
    private final static String TAG = LoginNewActivity.class.getSimpleName();

    private ImageButton mCloseIBtn;
    private EditText mPhoneEt, mPwdEt;
    private Button mForgetPwdBtn, mDoLoginBtn, mDoRegisterBtn;
    private ImageButton mWeiXiIBtn, mQQIBtn;
    private UserThird userThird;

    private UMShareAPI mShareAPI;
    private AbortableFuture<LoginInfo> loginRequest;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_new;
    }

    @Override
    protected void initHandler() {
        mHandler = new MineHandler();
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        mCloseIBtn = (ImageButton) findViewById(R.id.login_close);
        mWeiXiIBtn = (ImageButton) findViewById(R.id.login_way_weixi);
        mQQIBtn = (ImageButton) findViewById(R.id.login_way_qq);
        mPhoneEt = (EditText) findViewById(R.id.login_input_phone);
        mPwdEt = (EditText) findViewById(R.id.login_input_password);
        mDoLoginBtn = (Button) findViewById(R.id.login_dologin);
        mForgetPwdBtn = (Button) findViewById(R.id.login_forget_password);
        mDoRegisterBtn = (Button) findViewById(R.id.login_doregister);

        mWeiXiIBtn.setOnClickListener(this);
        mQQIBtn.setOnClickListener(this);
        mCloseIBtn.setOnClickListener(this);
        mDoLoginBtn.setOnClickListener(this);
        mForgetPwdBtn.setOnClickListener(this);
        mDoRegisterBtn.setOnClickListener(this);

        mShareAPI = UMShareAPI.get(this);
        mHandler.addCallback(MineHandler.CHILD_LOGIN, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_close:
                finish();
                break;
            case R.id.login_dologin://登录
                String mobile = mPhoneEt.getText().toString();
                String pwd = SecurityUtils.passwordEncrypt(mPwdEt.getText().toString());
                if (checkInputText()) {
                    double location[] = LocationOnMain.getInstance().getLocation();
                    new AsyncLogin().execute(mobile, pwd, String.valueOf(location[0]), String.valueOf(location[1]));
                    mDoLoginBtn.setEnabled(false);
                }
                break;
            case R.id.login_way_weixi://微信登录
                loginByThird(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.login_way_qq://qq登录
                loginByThird(SHARE_MEDIA.QQ);
                break;
            case R.id.login_forget_password://忘记密码
                Intent intent = new Intent(this, ReplacePWDActivity.class);
                String phone = mPhoneEt.getText().toString();
                if (!TextUtils.isEmpty(phone)) {
                    intent.putExtra(Constants.Strings.USER_PHONE, phone);
                }
                startActivity(intent);
                break;
            case R.id.login_doregister://注册
                Intent regIntent = new Intent(this, RegisterNewActivity.class);
                startActivity(regIntent);
                break;
            default:
                break;
        }
    }

    private boolean checkInputText() {
        int length = mPhoneEt.getText().length();
        if (length == 11) {//手机号码长度11位
            length = mPwdEt.getText().length();
            if (length >= 6 && length <= 32) {//密码长度6-32位
                return true;
            } else {
                Toast.makeText(this, "密码至少6位", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "手机号码不正确", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    //第三方登录
    public void loginByThird(SHARE_MEDIA platForm) {
        mShareAPI.deleteOauth(this, platForm, this);//清除对应的授权
        mShareAPI.doOauthVerify(this, platForm, this);
    }

    @Override
    public void onComplete(SHARE_MEDIA share_media, int action, Map<String, String> map) {
        switch (share_media) {
            case QQ://QQ
                doLoginThirdOper(share_media, action, map);
                break;
            case WEIXIN://微信
                doLoginThirdOper(share_media, action, map);
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
        Toast.makeText(this, "登录失败,请重试", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media, int i) {

    }

    //根据类型执行第三方登录的响应操作
    private void doLoginThirdOper(SHARE_MEDIA shareMedia, int action, Map<String, String> map) {
        if (action == ACTION_AUTHORIZE) {//认证
            mShareAPI.getPlatformInfo(this, shareMedia, this);
        } else if (action == ACTION_GET_PROFILE) {//获取用户信息
            if (map == null || map.get("errcode") != null) {
                return;
            }
            userThird = bindUserData(shareMedia, map);
            userThird.setPlatform(PlatForm.getEnum(shareMedia));
            Message msg = new Message();
            msg.what = R.id.msg_get_third_loginInfo;
            msg.obj = userThird;
            mHandler.sendMessage(msg);
        }
    }

    //绑定用户数据
    private UserThird bindUserData(SHARE_MEDIA shareMedia, Map<String, String> map) {
        UserThird userRegist = new UserThird();
        if (shareMedia == SHARE_MEDIA.QQ) {
            userRegist.setOpenId(map.get("openid"));
            userRegist.setNickName(map.get("screen_name"));
            userRegist.setPhotoUrl(map.get("profile_image_url"));
            userRegist.setSex(Sex.getEnum(map.get("gender")));
        } else if (shareMedia == SHARE_MEDIA.WEIXIN) {
            userRegist.setOpenId(map.get("openid"));
            userRegist.setNickName(map.get("nickname"));
            userRegist.setPhotoUrl(map.get("headimgurl"));
            userRegist.setSex(Sex.getEnum(Integer.parseInt(map.get("sex"))));
        }
        return userRegist;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.msg_third_loginInfo_got://第三方登入
                if (msg.obj != null) {
                    UserLogin mUserLogin = (UserLogin) msg.obj;
                    if (mUserLogin.isBindPhone()) {
                        SharedPreferencesUtil.getInstance(this).saveLoginUser(mUserLogin);
                        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Intent intent = new Intent(this, BindPhoneActivity.class);
//                        intent.putExtra(Constants.Strings.USER_LOGIN, userThird);
                        intent.putExtra(Constants.Strings.USER_LOGIN, (Parcelable) userThird);
                        startActivity(intent);
                        this.finish();
                    }
                }
                break;
            default:
                break;
        }
        return true;
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
                    Toast.makeText(LoginNewActivity.this, R.string.toast_login_success, Toast.LENGTH_SHORT).show();
                    loginRequest = NIMClient.getService(AuthService.class).
                            login(new LoginInfo(String.valueOf(userLogin.getUserAccount()), userLogin.getImToken()));
                    NimUIKit.setAccount(String.valueOf(userLogin.getUserAccount()));
                    LogUtil.w("LoginStatus:", NIMClient.getStatus().toString());

                    userLogin.setMobile(mPhoneEt.getText().toString());
                    userLogin.setPwdMD5(SecurityUtils.passwordEncrypt(mPwdEt.getText().toString()));
                    SharedPreferencesUtil.getInstance(LoginNewActivity.this).saveLoginUser(userLogin);
                    LoginNewActivity.this.finish();
                } else {
                    Toast.makeText(LoginNewActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginNewActivity.this, R.string.toast_login_unsuccess, Toast.LENGTH_SHORT).show();
            }
            mDoLoginBtn.setEnabled(true);
        }
    }
}
