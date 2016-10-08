package cn.spinsoft.wdq.user.biz;

import java.util.List;

import cn.spinsoft.wdq.base.bean.UserBase;
import cn.spinsoft.wdq.enums.AgeRange;
import cn.spinsoft.wdq.enums.Attention;
import cn.spinsoft.wdq.enums.Sex;
import cn.spinsoft.wdq.enums.UserType;

/**
 * Created by hushujun on 15/11/19.
 */
public class DancerInfo extends UserBase {
    private AgeRange ageRange;
    private Attention attention;
    private int admireCount;
    private int danceId;
    private int orgId;
    private List<String> labels;
    private String distance;
    private boolean isPublic;
    private Sex sex;

    public DancerInfo() {
        type = UserType.PERSONAL;
    }

    public AgeRange getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(AgeRange ageRange) {
        this.ageRange = ageRange;
    }

    public Attention getAttention() {
        return attention;
    }

    public void setAttention(Attention attention) {
        this.attention = attention;
    }

    public int getDanceId() {
        return danceId;
    }

    public void setDanceId(int danceId) {
        this.danceId = danceId;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }


    public boolean isPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public int getAdmireCount() {
        return admireCount;
    }

    public void setAdmireCount(int admireCount) {
        this.admireCount = admireCount;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    @Override
    public String toString() {
        return "DancerInfo{" +
                "ageRange=" + ageRange +
                ", attention=" + attention +
                ", danceId=" + danceId +
                ", labels=" + labels +
                ", distance=" + distance +
                ", isPublic=" + isPublic +
                ", sex=" + sex +
                '}';
    }
}
