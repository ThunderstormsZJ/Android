package cn.spinsoft.wdq.org.biz;

import java.util.List;

import cn.spinsoft.wdq.mine.biz.SimpleUserBean;

/**
 * Created by hushujun on 16/1/6.
 */
public class OrgInfo extends SimpleUserBean {
    private int orgId;
    private String orgName;
    private String address;
    private double distance;
    private int pageViews;
    private int teacherCount;
    private List<String> labels;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getPageViews() {
        return pageViews;
    }

    public void setPageViews(int pageViews) {
        this.pageViews = pageViews;
    }

    public int getTeacherCount() {
        return teacherCount;
    }

    public void setTeacherCount(int teacherCount) {
        this.teacherCount = teacherCount;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Override
    public String toString() {
        return "OrgInfo{" +
                "orgId=" + orgId +
                ", orgName='" + orgName + '\'' +
                ", address='" + address + '\'' +
                ", distance=" + distance +
                ", pageViews=" + pageViews +
                ", teacherCount=" + teacherCount +
                ", labels=" + labels +
                '}';
    }
}
