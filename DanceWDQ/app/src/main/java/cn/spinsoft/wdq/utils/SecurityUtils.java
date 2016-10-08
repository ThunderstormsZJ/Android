package cn.spinsoft.wdq.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import org.apache.http.NameValuePair;

import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.spinsoft.wdq.login.LoginNewActivity;
import cn.spinsoft.wdq.login.biz.UserLogin;

/**
 * Created by zhoujun on 15/11/4.
 */
public class SecurityUtils {
    private final static String TAG = SecurityUtils.class.getSimpleName();

    public static String passwordEncrypt(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            return "";
        }
        String md5String = pwd;
        for (int i = 0; i < 5; i++) {
            md5String = MD5Utils.getMD5String(md5String);
        }
        return md5String;
    }

    /**
     * 用户信息是否有效
     *
     * @return
     */
    public static boolean isUserValidity(Context context, int userId) {
        if (userId <= 0) {
//            Toast.makeText(context, R.string.toast_user_is_unvalidity, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, LoginNewActivity.class);
            context.startActivity(intent);
            return false;
        } else {
            return true;
        }
    }

    public static boolean isUserValidity(Context context, UserLogin userLogin) {
        if (userLogin == null || userLogin.getUserId() <= 0) {
//            Toast.makeText(context, R.string.toast_user_is_unvalidity, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, LoginNewActivity.class);
            context.startActivity(intent);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 微信支付需要的数据
     */
    public static String getNonceStr() {//获取随机数
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    public static long genTimeStamp() {//获取时间戳
        return System.currentTimeMillis() / 1000;
    }

    public static String getAppSign(Map<String, String> params) {//获取签名
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(entry.getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constants.Strings.WX_APP_KEY);
        LogUtil.w(TAG,sb.toString());
        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        LogUtil.w(TAG,"appSign:-->"+appSign);
        return appSign;
    }

    public static String getAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constants.Strings.WX_APP_KEY);
        LogUtil.w(TAG, sb.toString());
        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        LogUtil.w(TAG, "appSign:----->"+appSign);
        return appSign;
    }
}
