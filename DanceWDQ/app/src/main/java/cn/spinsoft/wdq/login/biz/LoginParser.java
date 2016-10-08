package cn.spinsoft.wdq.login.biz;

import android.text.TextUtils;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.enums.UserType;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.OkHttpClientManager;
import cn.spinsoft.wdq.utils.StringUtils;
import cn.spinsoft.wdq.utils.UrlManager;

/**
 * Created by hushujun on 15/11/4.
 */
public class LoginParser {
    private static final String TAG = LoginParser.class.getSimpleName();

    //登录
    public static UserLogin login(String mobile, String pwd, String longitude, String latitude) {
        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(pwd)) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("telphone", mobile),
                new OkHttpClientManager.Param("longitude", longitude),
                new OkHttpClientManager.Param("latitude", latitude),
                new OkHttpClientManager.Param("password", pwd)
        };
        return parseLogin(UrlManager.getUrl(UrlManager.UrlName.LOGIN), params);
    }

    //第三方登录--从服务器获取app的登录信息
    public static UserLogin getThirdLoginInfo(UserThird userRegist) {
        if (userRegist == null) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("openid", userRegist.getOpenId()),
                new OkHttpClientManager.Param("nickname", userRegist.getNickName()),
                new OkHttpClientManager.Param("headurl", userRegist.getPhotoUrl()),
                /*new OkHttpClientManager.Param("sex", String.valueOf(userRegist.getSex().getValue())),*/
                new OkHttpClientManager.Param("tl_type", String.valueOf(userRegist.getPlatform().getPfType())),
                /*new OkHttpClientManager.Param("channelid", userRegist.getChannelId()),*/
                new OkHttpClientManager.Param("devicetype", String.valueOf(userRegist.getDeviceType()))
//                new OkHttpClientManager.Param("telphone ", String.valueOf(userRegist.getTelphone())),
//                new OkHttpClientManager.Param("vcode", String.valueOf(userRegist.getvCode()))
        };
        return parseLogin(UrlManager.getUrl(UrlManager.UrlName.LOGIN_BY_THIRD), params);
    }
    //绑定手机号
    public static UserLogin bindPhoneLogin(UserThird userRegist) {
        if (userRegist == null) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("openid", userRegist.getOpenId()),
                new OkHttpClientManager.Param("nickname", userRegist.getNickName()),
                new OkHttpClientManager.Param("headurl", userRegist.getPhotoUrl()),
                /*new OkHttpClientManager.Param("sex", String.valueOf(userRegist.getSex().getValue())),*/
                new OkHttpClientManager.Param("tl_type", String.valueOf(userRegist.getPlatform().getPfType())),
                /*new OkHttpClientManager.Param("channelid", userRegist.getChannelId()),*/
                new OkHttpClientManager.Param("devicetype", String.valueOf(userRegist.getDeviceType())),
                new OkHttpClientManager.Param("telphone", String.valueOf(userRegist.getTelphone())),
                new OkHttpClientManager.Param("vcode", String.valueOf(userRegist.getvCode()))
        };
        return parseLogin(UrlManager.getUrl(UrlManager.UrlName.LOGIN_BY_THIRD), params);
    }

    public static SimpleResponse getVerify(String mobile) {
        return getVerifyCode(UrlManager.getUrl(UrlManager.UrlName.VERIFY_CODE), mobile);
    }

    public static SimpleResponse getVerifyCode(String url, String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            return null;
        }
        RequestBody body = new FormEncodingBuilder().add("telphone", mobile).build();
        Request request = new Request.Builder().url(url).post(body).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getVerifyCode:" + json);
            JSONObject object = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            return simpleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UserLogin register(String mobile, String pwd, String verify, String type,
                                     String longitude, String latitude) {
        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(verify)) {
            return null;
        }

        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("telphone", mobile),
                new OkHttpClientManager.Param("password", pwd),
                new OkHttpClientManager.Param("vcode", verify),
                new OkHttpClientManager.Param("longitude", longitude),
                new OkHttpClientManager.Param("latitude", latitude),
                new OkHttpClientManager.Param("type", type)
        };
        return parseLogin(UrlManager.getUrl(UrlManager.UrlName.REGISTER), params);
    }

    /**
     * 重置密码
     *
     * @param mobile
     * @param pwd
     * @param verify
     * @return
     */
    public static UserLogin replacePwd(String mobile, String pwd, String verify) {
        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(verify)) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("telphone", mobile),
                new OkHttpClientManager.Param("password", pwd),
                new OkHttpClientManager.Param("vcode", verify)
        };
        return parseLogin(UrlManager.getUrl(UrlManager.UrlName.REPLACE_PWD), params);
    }

    public static SimpleResponse updatePwd(String mobile, String oldPwd, String newPwd) {
        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(oldPwd) || TextUtils.isEmpty(newPwd)) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("telphone", StringUtils.getNoNullString(mobile)),
                new OkHttpClientManager.Param("oldPassword", oldPwd),
                new OkHttpClientManager.Param("newPassword", newPwd)
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.UPDATE_PWD), params);
            String json = response.body().string();
            LogUtil.w(TAG, "updatePwd:" + json);
            JSONObject object = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(Integer.parseInt(object.optString("code")));
            simpleResponse.setMessage(object.optString("msg"));
            return simpleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static UserLogin parseLogin(String url, OkHttpClientManager.Param[] params) {
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate().post(url, params);
            String json = response.body().string();
            LogUtil.w(TAG, "parseLogin:" + json);
            JSONObject object = new JSONObject(json);
            UserLogin userInfo = new UserLogin();
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(Integer.parseInt(object.optString("code")));
            simpleResponse.setMessage(object.optString("msg"));
            userInfo.setResponse(simpleResponse);

            if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                JSONObject userObj = object.optJSONObject("userinfo");
                UserType userType = UserType.getEnum(userObj.optString("type"));
                userInfo.setPhotoUrl(userObj.optString("headurl"));
                userInfo.setMobile(userObj.optString("telphone"));
                userInfo.setNickName(userObj.optString("name"));
                userInfo.setSignature(userObj.optString("signature"));
                userInfo.setType(userType);
                userInfo.setUserId(userObj.optInt("userid"));
                userInfo.setOrgId(userObj.optInt("orgid"));
                userInfo.setOrgIntro(userObj.optString("intro"));
                userInfo.setImToken(userObj.optString("token"));
                userInfo.setIsBindPhone(userObj.optInt("registerState") == 0 ? false : true);
            }
            return userInfo;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
