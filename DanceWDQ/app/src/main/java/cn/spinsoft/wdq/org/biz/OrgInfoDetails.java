package cn.spinsoft.wdq.org.biz;

import java.util.List;
import java.util.Map;

import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.enums.Attention;

/**
 * Created by hushujun on 16/1/8.
 */
public class OrgInfoDetails {
    private SimpleResponse response;
    private Map<String,String> workUrls;
    private Attention isAttention;
    private String address;
    private String orgName;
    private String signature;
    private String introduce;
    private String headurl;
    private String mobile;
    private int workCount;
    private int dynamicCount;
    private int teacherCount;
    private int courseCount;
    private int orgid;
    private int userId;

    public SimpleResponse getResponse() {
        return response;
    }

    public void setResponse(SimpleResponse response) {
        this.response = response;
    }

    public Map<String, String> getWorkUrls() {
        return workUrls;
    }

    public void setWorkUrls(Map<String, String> workUrls) {
        this.workUrls = workUrls;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getHeadurl() {
        return headurl;
    }

    public void setHeadurl(String headurl) {
        this.headurl = headurl;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getOrgid() {
        return orgid;
    }

    public void setOrgid(int orgid) {
        this.orgid = orgid;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Attention getIsAttention() {
        return isAttention;
    }

    public void setIsAttention(Attention isAttention) {
        this.isAttention = isAttention;
    }

    public int getWorkCount() {
        return workCount;
    }

    public void setWorkCount(int workCount) {
        this.workCount = workCount;
    }

    public int getDynamicCount() {
        return dynamicCount;
    }

    public void setDynamicCount(int dynamicCount) {
        this.dynamicCount = dynamicCount;
    }

    public int getTeacherCount() {
        return teacherCount;
    }

    public void setTeacherCount(int teacherCount) {
        this.teacherCount = teacherCount;
    }

    public int getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(int courseCount) {
        this.courseCount = courseCount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
