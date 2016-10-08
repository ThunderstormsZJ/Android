package cn.spinsoft.wdq.mine.biz;

/**
 * Created by hushujun on 16/1/14.
 */
public class OrgOrderBean extends BookingCourseBean {
    private String createTime;
    private String userName;
    private String userPhone;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
