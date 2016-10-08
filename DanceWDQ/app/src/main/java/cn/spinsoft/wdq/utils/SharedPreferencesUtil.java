package cn.spinsoft.wdq.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Observable;

import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.mine.biz.PublishInfoBean;
import cn.spinsoft.wdq.mine.biz.PublishVideoBean;
import cn.spinsoft.wdq.service.LocationOnMain;

/**
 * Created by hushujun on 15/11/11.
 */
public class SharedPreferencesUtil extends Observable {
    private static final String TAG = SharedPreferencesUtil.class.getSimpleName();

    private SharedPreferences mSPF;
    private static SharedPreferencesUtil mUtil;

    private List<SimpleItemData> danceTypes;
    private UserLogin userLogin;

    private SharedPreferencesUtil(Context context) {
        mSPF = context.getSharedPreferences("spinsoft_wdq", Context.MODE_PRIVATE);
    }

    public static SharedPreferencesUtil getInstance(Context context) {
        if (mUtil == null) {
            mUtil = new SharedPreferencesUtil(context);
        }
        return mUtil;
    }

    public SharedPreferences getSharedPreferences() {
        return mSPF;
    }

    public void saveStartPage(String imageUrl) {
        SharedPreferences.Editor editor = mSPF.edit();
        editor.putString("start_page", imageUrl);
        editor.apply();
    }

    public String getStartPage() {
        return mSPF.getString("start_page", null);
    }

    public void saveDaceTypes(List<SimpleItemData> danceTypes) {
        if (danceTypes == null || danceTypes.isEmpty()) {
            return;
        }
        this.danceTypes = danceTypes;
        saveObject("dance_type", danceTypes);
    }

    @Deprecated
    public List<SimpleItemData> getDanceTypes() {
        if (danceTypes != null && !danceTypes.isEmpty()) {
            return danceTypes;
        }
        return (List<SimpleItemData>) readObject("dance_type");
    }

    public void saveLoginUser(UserLogin userLogin) {
        this.userLogin = userLogin;
        LocationOnMain.getInstance().setUserLogin(userLogin);
        saveObject("login_user", userLogin);
        setChanged();
        notifyObservers(userLogin);
    }

    public UserLogin getLoginUser() {
        if (userLogin != null) {
            return userLogin;
        }
        return (UserLogin) readObject("login_user");
    }

    public void savePublishInfoList(List<PublishInfoBean> publishInfoList) {
        saveObject("publish_info", publishInfoList);
    }

    public List<PublishInfoBean> getPublishInfoList() {
        return (List<PublishInfoBean>) readObject("publish_info");
    }

    public void savePublishVideoList(List<PublishVideoBean> publishVideoList) {
        saveObject("publish_video", publishVideoList);
    }

    public List<PublishVideoBean> getPublishVideoList() {
        return (List<PublishVideoBean>) readObject("publish_video");
    }

    private void saveObject(String key, Object object) {
        try {
            SharedPreferences.Editor editor = mSPF.edit();
            if (object != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                String types = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
                editor.putString(key, types);
                baos.close();
                oos.close();
            } else {
                editor.putString(key, null);
            }
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Object readObject(String key) {
        String typesBase64 = mSPF.getString(key, null);
        if (TextUtils.isEmpty(typesBase64)) {
            return null;
        }
        byte[] base64Bytes = Base64.decode(typesBase64.getBytes(), Base64.NO_WRAP);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            Object object = ois.readObject();
            bais.close();
            ois.close();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
