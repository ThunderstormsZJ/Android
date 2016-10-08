package cn.spinsoft.wdq.user.biz;

import cn.spinsoft.wdq.base.bean.UserBase;
import cn.spinsoft.wdq.enums.Attention;
import cn.spinsoft.wdq.enums.Sex;

/**
 * Created by hushujun on 15/12/2.
 */
public class UserDetail extends UserBase {
    private Sex sex;
    private Attention attention = Attention.NONE;
    private int orgid;
    private String orgName;
    private String attest;
    private String worksNum = "0";
    private String dynamicNum = "0";
    private String attentNum = "0";
    private String fansNum = "0";

    public Attention getAttention() {
        return attention;
    }

    public void setAttention(Attention attention) {
        this.attention = attention;
    }

    public String getAttest() {
        return attest;
    }

    public void setAttest(String attest) {
        this.attest = attest;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public int getOrgid() {
        return orgid;
    }

    public void setOrgid(int orgid) {
        this.orgid = orgid;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getWorksNum() {
        return worksNum;
    }

    public void setWorksNum(String worksNum) {
        this.worksNum = worksNum;
    }

    public String getDynamicNum() {
        return dynamicNum;
    }

    public void setDynamicNum(String dynamicNum) {
        this.dynamicNum = dynamicNum;
    }

    public String getAttentNum() {
        return attentNum;
    }

    public void setAttentNum(String attentNum) {
        this.attentNum = attentNum;
    }

    public String getFansNum() {
        return fansNum;
    }

    public void setFansNum(String fansNum) {
        this.fansNum = fansNum;
    }

    @Override
    public String toString() {
        return "UserDetail{" +
                ", sex=" + sex +
                ", attention=" + attention +
                ", orgid='" + orgid + '\'' +
                ", orgName='" + orgName + '\'' +
                ", worksNum='" + worksNum + '\'' +
                ", dynamicNum='" + dynamicNum + '\'' +
                ", attentNum='" + attentNum + '\'' +
                ", fansNum='" + fansNum + '\'' +
                '}';
    }
}
