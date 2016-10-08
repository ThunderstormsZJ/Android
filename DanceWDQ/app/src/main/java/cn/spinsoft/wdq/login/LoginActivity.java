package cn.spinsoft.wdq.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.enums.PlatForm;
import cn.spinsoft.wdq.enums.Sex;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.login.biz.UserThird;
import cn.spinsoft.wdq.login.frag.LoginFrag;
import cn.spinsoft.wdq.login.frag.ProtocolFrag;
import cn.spinsoft.wdq.login.frag.RegisterFrag;
import cn.spinsoft.wdq.mine.biz.MineHandler;
import cn.spinsoft.wdq.utils.ContentResolverUtil;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;

/**
 * Created by zhoujun on 15/11/2.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, UMAuthListener ,
        Handler.Callback{
    private static final String TAG = LoginActivity.class.getSimpleName();

    private TextView mTitleTx, mRegisterTx;
    private LoginFrag mLoginFrag;
    private RegisterFrag mRegisterFrag;
    private UMShareAPI mShareAPI;
    private UserLogin mUserLogin;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initHandler() {
        mHandler = new MineHandler();
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        mHandler.addCallback(MineHandler.CHILD_LOGIN,this);
        TextView mBackTx = (TextView) findViewById(R.id.base_title_back);
        mTitleTx = (TextView) findViewById(R.id.base_title_name);
        mRegisterTx = (TextView) findViewById(R.id.base_title_otherfunction);
        mRegisterTx.setText(R.string.action_register);
        mTitleTx.setText(R.string.login_login_str);

        mBackTx.setOnClickListener(this);
        mRegisterTx.setOnClickListener(this);
        if (getSupportFragmentManager().findFragmentById(R.id.login_child_container) == null) {
            mLoginFrag = new LoginFrag();
            getSupportFragmentManager().beginTransaction().replace(R.id.login_child_container, mLoginFrag).commit();
        } else {
            mLoginFrag = (LoginFrag) getSupportFragmentManager().findFragmentById(R.id.login_child_container);
        }

        mShareAPI = UMShareAPI.get(this);
    }

    @Override
    public void onBackPressed() {
        ContentResolverUtil.hideIMM(getCurrentFocus());
        if (getSupportFragmentManager().findFragmentById(R.id.login_child_container) instanceof RegisterFrag) {
            getSupportFragmentManager().beginTransaction().replace(R.id.login_child_container, mLoginFrag).commit();
            mTitleTx.setText(R.string.login_login_str);
            mRegisterTx.setVisibility(View.VISIBLE);
        }else if (getSupportFragmentManager().findFragmentById(R.id.login_child_container) instanceof ProtocolFrag) {
            getSupportFragmentManager().beginTransaction().replace(R.id.login_child_container, mRegisterFrag).commit();
            mTitleTx.setText(R.string.action_register);
            mRegisterTx.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_title_back:
                ContentResolverUtil.hideIMM(getCurrentFocus());
                if (getSupportFragmentManager().findFragmentById(R.id.login_child_container) instanceof RegisterFrag) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.login_child_container, mLoginFrag).commit();
                    mTitleTx.setText(R.string.login_login_str);
                    mRegisterTx.setVisibility(View.VISIBLE);
                }else if (getSupportFragmentManager().findFragmentById(R.id.login_child_container) instanceof ProtocolFrag) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.login_child_container, mRegisterFrag).commit();
                    mTitleTx.setText(R.string.action_register);
                    mRegisterTx.setVisibility(View.GONE);
                } else {
                    finish();
                }
                break;
            case R.id.base_title_otherfunction:
                ContentResolverUtil.hideIMM(getCurrentFocus());
                if (mRegisterFrag == null) {
                    mRegisterFrag = new RegisterFrag();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.login_child_container, mRegisterFrag).commit();
                mTitleTx.setText(R.string.action_register);
                mRegisterTx.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    //第三方登录
    public void loginByThird(SHARE_MEDIA platForm) {
        mShareAPI.deleteOauth(this, platForm, this);//清除对应的授权
        mShareAPI.doOauthVerify(this, platForm, this);
    }

    /**
     * 第三方登录listener
     */
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
        Toast.makeText(this,"登录失败,请重试",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media, int i) {

    }

    //根据类型执行第三方登录的响应操作
    private void doLoginThirdOper(SHARE_MEDIA shareMedia, int action, Map<String, String> map) {
        if (action == ACTION_AUTHORIZE) {//认证
            mShareAPI.getPlatformInfo(this, shareMedia, this);
            Toast.makeText(this,"登录成功",Toast.LENGTH_SHORT).show();
        } else if (action == ACTION_GET_PROFILE) {//获取用户信息
            if(map==null || map.get("errcode")!=null){
                return ;
            }
            UserThird userThird = bindUserData(shareMedia,map);
            userThird.setPlatform(PlatForm.getEnum(shareMedia));
            Message msg = new Message();
            msg.what = R.id.msg_get_third_loginInfo;
            msg.obj = userThird;
            mHandler.sendMessage(msg);
        }
    }
    //绑定用户数据
    private UserThird bindUserData(SHARE_MEDIA shareMedia,Map<String, String> map){
        UserThird userRegist = new UserThird();
        if(shareMedia==SHARE_MEDIA.QQ){
            userRegist.setOpenId(map.get("openid"));
            userRegist.setNickName(map.get("screen_name"));
            userRegist.setPhotoUrl(map.get("profile_image_url"));
            userRegist.setSex(Sex.getEnum(map.get("gender")));
        }else if(shareMedia==SHARE_MEDIA.WEIXIN){
            userRegist.setOpenId(map.get("openid"));
            userRegist.setNickName(map.get("nickname"));
            userRegist.setPhotoUrl(map.get("headimgurl"));
            userRegist.setSex(Sex.getEnum(Integer.parseInt(map.get("sex"))));
        }
        return userRegist;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case R.id.msg_third_loginInfo_got://第三方登入
                if(msg.obj!=null){
                    mUserLogin = (UserLogin) msg.obj;
                    SharedPreferencesUtil.getInstance(this).saveLoginUser(mUserLogin);
                    finish();
                }
                break;
            default:break;
        }
        return false;
    }
}
