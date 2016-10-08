package cn.spinsoft.wdq.mine.biz;

import java.util.List;

import cn.spinsoft.wdq.base.bean.UserBase;
import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.enums.AgeRange;
import cn.spinsoft.wdq.enums.Sex;

/**
 * Created by zhoujun on 15/12/7.
 */
public class UserInfoDetail extends UserBase {
    private Sex sex = Sex.FEMALE;
    private String contactWay;
    private boolean visible = true;
    private AgeRange ageRange = AgeRange.AGE_80;
    private String tall;
    private List<SimpleItemData> dances;
    private String telephone;
    private int orgId;
    private String orgIntro;
    private String orgAddress;
    private List<String> attachments;

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getContactWay() {
        return contactWay;
    }

    public void setContactWay(String contactWay) {
        this.contactWay = contactWay;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public AgeRange getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(AgeRange ageRange) {
        this.ageRange = ageRange;
    }

    public String getTall() {
        return tall;
    }

    public void setTall(String tall) {
        this.tall = tall;
    }

    public List<SimpleItemData> getDances() {
        return dances;
    }

    public void setDances(List<SimpleItemData> dances) {
        this.dances = dances;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public String getOrgAddress() {
        return orgAddress;
    }

    public void setOrgAddress(String orgAddress) {
        this.orgAddress = orgAddress;
    }

    public void copy(UserInfoDetail detail) {
        if (detail != null) {
            this.sex = detail.sex;
            this.contactWay = detail.contactWay;
            this.visible = detail.visible;
            this.ageRange = detail.ageRange;
            this.tall = detail.tall;
            this.dances = detail.dances;
            this.telephone = detail.telephone;
            this.orgId = detail.orgId;
            this.orgIntro = detail.orgIntro;
            this.attachments = detail.attachments;
            setUserId(detail.getUserId());
            setPhotoUrl(detail.getPhotoUrl());
            setNickName(detail.getNickName());
            setSignature(detail.getSignature());
            setType(detail.getType());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        UserInfoDetail that = (UserInfoDetail) o;

        if (visible != that.visible) return false;
        if (orgId != that.orgId) return false;
        if (sex != that.sex) return false;
        if (contactWay != null ? !contactWay.equals(that.contactWay) : that.contactWay != null)
            return false;
        if (ageRange != that.ageRange) return false;
        if (tall != null ? !tall.equals(that.tall) : that.tall != null) return false;
        if (dances != null ? !dances.equals(that.dances) : that.dances != null) return false;
        if (telephone != null ? !telephone.equals(that.telephone) : that.telephone != null)
            return false;
        if (orgIntro != null ? !orgIntro.equals(that.orgIntro) : that.orgIntro != null)
            return false;
        return !(attachments != null ? !attachments.equals(that.attachments) : that.attachments != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (sex != null ? sex.hashCode() : 0);
        result = 31 * result + (contactWay != null ? contactWay.hashCode() : 0);
        result = 31 * result + (visible ? 1 : 0);
        result = 31 * result + (ageRange != null ? ageRange.hashCode() : 0);
        result = 31 * result + (tall != null ? tall.hashCode() : 0);
        result = 31 * result + (dances != null ? dances.hashCode() : 0);
        result = 31 * result + (telephone != null ? telephone.hashCode() : 0);
        result = 31 * result + orgId;
        result = 31 * result + (orgIntro != null ? orgIntro.hashCode() : 0);
        result = 31 * result + (attachments != null ? attachments.hashCode() : 0);
        return result;
    }
}
