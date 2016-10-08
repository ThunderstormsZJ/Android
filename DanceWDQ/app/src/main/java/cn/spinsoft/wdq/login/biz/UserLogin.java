package cn.spinsoft.wdq.login.biz;

import android.os.Parcel;
import android.os.Parcelable;

import cn.spinsoft.wdq.base.bean.UserBase;

/**
 * Created by zhoujun on 15/12/7.
 */
public class UserLogin extends UserBase implements Parcelable {
    private String mobile;
    private String pwdMD5;
    private int orgId = -1;
    private String orgIntro;
    private boolean isBindPhone;
    private String imToken;//即时聊天的唯一标识

    public UserLogin() {
    }

    protected UserLogin(Parcel in) {
        mobile = in.readString();
        pwdMD5 = in.readString();
        orgId = in.readInt();
        orgIntro = in.readString();
        isBindPhone = in.readByte() != 0;
        imToken = in.readString();
        userId = in.readInt();
    }

    public static final Creator<UserLogin> CREATOR = new Creator<UserLogin>() {
        @Override
        public UserLogin createFromParcel(Parcel in) {
            return new UserLogin(in);
        }

        @Override
        public UserLogin[] newArray(int size) {
            return new UserLogin[size];
        }
    };

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPwdMD5() {
        return pwdMD5;
    }

    public void setPwdMD5(String pwdMD5) {
        this.pwdMD5 = pwdMD5;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public String getOrgIntro() {
        return orgIntro;
    }

    public void setOrgIntro(String orgIntro) {
        this.orgIntro = orgIntro;
    }

    public String getImToken() {
        return imToken;
    }

    public void setImToken(String imToken) {
        this.imToken = imToken;
    }

    public boolean isBindPhone() {
        return isBindPhone;
    }

    public void setIsBindPhone(boolean isBindPhone) {
        this.isBindPhone = isBindPhone;
    }

    public String getUserAccount() {
        if (orgId > 0) {
            return "orgid" + orgId;
        }
        return "userid" + userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserLogin)) return false;
        if (!super.equals(o)) return false;

        UserLogin userLogin = (UserLogin) o;

        if (orgId != userLogin.orgId) return false;
        if (mobile != null ? !mobile.equals(userLogin.mobile) : userLogin.mobile != null)
            return false;
        return !(pwdMD5 != null ? !pwdMD5.equals(userLogin.pwdMD5) : userLogin.pwdMD5 != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mobile != null ? mobile.hashCode() : 0);
        result = 31 * result + (pwdMD5 != null ? pwdMD5.hashCode() : 0);
        result = 31 * result + orgId;
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mobile);
        dest.writeString(pwdMD5);
        dest.writeInt(orgId);
        dest.writeString(orgIntro);
        dest.writeByte((byte) (isBindPhone ? 1 : 0));
        dest.writeString(imToken);
        dest.writeInt(userId);
    }
}
