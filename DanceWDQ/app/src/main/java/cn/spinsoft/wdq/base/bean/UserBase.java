package cn.spinsoft.wdq.base.bean;

import java.io.Serializable;

import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.enums.UserType;

/**
 * Created by hushujun on 15/12/7.
 */
public class UserBase implements Serializable {
    static final long serialVersionUID = 7893905912796429755L;
    protected int userId;
    protected String nickName;
    protected String photoUrl;
    private String signature;
    protected UserType type;
    private SimpleResponse response;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public SimpleResponse getResponse() {
        return response;
    }

    public void setResponse(SimpleResponse response) {
        this.response = response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserBase)) return false;

        UserBase userBase = (UserBase) o;

        if (userId != userBase.userId) return false;
        if (nickName != null ? !nickName.equals(userBase.nickName) : userBase.nickName != null)
            return false;
        if (photoUrl != null ? !photoUrl.equals(userBase.photoUrl) : userBase.photoUrl != null)
            return false;
        if (signature != null ? !signature.equals(userBase.signature) : userBase.signature != null)
            return false;
        return type == userBase.type;
    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + (nickName != null ? nickName.hashCode() : 0);
        result = 31 * result + (photoUrl != null ? photoUrl.hashCode() : 0);
        result = 31 * result + (signature != null ? signature.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
