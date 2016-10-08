package cn.spinsoft.wdq.login.biz;


import android.os.Parcel;
import android.os.Parcelable;

import cn.spinsoft.wdq.base.bean.UserBase;
import cn.spinsoft.wdq.enums.PlatForm;
import cn.spinsoft.wdq.enums.Sex;

/**
 * Created by zhoujun on 2016-3-4.
 * 第三方用户注册信息
 */
public class UserThird extends UserBase implements Parcelable {
    private String openId;
    private Sex sex;
    private PlatForm platform;
    private String channelId;
    private String telphone;
    private String vCode;
    private int deviceType = 2;//安卓

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public PlatForm getPlatform() {
        return platform;
    }

    public void setPlatform(PlatForm platform) {
        this.platform = platform;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public String getvCode() {
        return vCode;
    }

    public void setvCode(String vCode) {
        this.vCode = vCode;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nickName);
        dest.writeString(this.photoUrl);
        dest.writeString(this.openId);
        dest.writeInt(this.sex == null ? -1 : this.sex.ordinal());
        dest.writeInt(this.platform == null ? -1 : this.platform.ordinal());
        dest.writeString(this.channelId);
        dest.writeString(this.telphone);
        dest.writeString(this.vCode);
        dest.writeInt(this.deviceType);
    }

    public UserThird() {
    }

    protected UserThird(Parcel in) {
        this.nickName = in.readString();
        this.photoUrl = in.readString();
        this.openId = in.readString();
        int tmpSex = in.readInt();
        this.sex = tmpSex == -1 ? null : Sex.values()[tmpSex];
        int tmpPlatform = in.readInt();
        this.platform = tmpPlatform == -1 ? null : PlatForm.values()[tmpPlatform];
        this.channelId = in.readString();
        this.telphone = in.readString();
        this.vCode = in.readString();
        this.deviceType = in.readInt();
    }

    public static final Parcelable.Creator<UserThird> CREATOR = new Parcelable.Creator<UserThird>() {
        public UserThird createFromParcel(Parcel source) {
            return new UserThird(source);
        }

        public UserThird[] newArray(int size) {
            return new UserThird[size];
        }
    };
}
