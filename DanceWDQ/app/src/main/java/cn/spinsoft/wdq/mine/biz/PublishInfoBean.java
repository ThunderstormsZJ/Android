package cn.spinsoft.wdq.mine.biz;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

import cn.spinsoft.wdq.enums.DiscoverType;

/**
 * Created by zhoujun on 15/12/21.
 */
public class PublishInfoBean implements Parcelable, Serializable {
    private int userId;
    private DiscoverType type;
    private String danceType;
    private String salary;
    private String title;
    private String content;
    private String posBigImg;//海报大图
    private String posSmallImg;//海报小图
    private String videoUri;//上传视频的Uri
    private String videoPoster;//上传视频的Uri
    private List<String> images;
    private List<String> smallImg;
    private String location;
    private String orgName;
    private String orgIntro;
    private String appEndTime;
    private String startTime;
    private String endTime;

    public PublishInfoBean() {}

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public DiscoverType getType() {
        return type;
    }

    public void setType(DiscoverType type) {
        this.type = type;
    }

    public String getDanceType() {
        return danceType;
    }

    public void setDanceType(String danceType) {
        this.danceType = danceType;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getSmallImg() {
        return smallImg;
    }

    public void setSmallImg(List<String> smallImg) {
        this.smallImg = smallImg;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgIntro() {
        return orgIntro;
    }

    public void setOrgIntro(String orgIntro) {
        this.orgIntro = orgIntro;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAppEndTime() {
        return appEndTime;
    }

    public void setAppEndTime(String appEndTime) {
        this.appEndTime = appEndTime;
    }

    public String getPosBigImg() {
        return posBigImg;
    }

    public void setPosBigImg(String posBigImg) {
        this.posBigImg = posBigImg;
    }

    public String getPosSmallImg() {
        return posSmallImg;
    }

    public void setPosSmallImg(String posSmallImg) {
        this.posSmallImg = posSmallImg;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }

    public String getVideoPoster() {
        return videoPoster;
    }

    public void setVideoPoster(String videoPoster) {
        this.videoPoster = videoPoster;
    }

    @Override
    public String toString() {
        return "PublishInfoBean{" +
                "userId=" + userId +
                ", type=" + type +
                ", danceType='" + danceType + '\'' +
                ", salary='" + salary + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", images=" + images + '\'' +
                ",smallImg=" + smallImg + '\'' +
                ", location='" + location + '\'' +
                ", orgName='" + orgName + '\'' +
                ", orgIntro='" + orgIntro + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PublishInfoBean that = (PublishInfoBean) o;

        if (userId != that.userId) return false;
        if (type != that.type) return false;
        if (danceType != that.danceType) return false;
        if (salary != that.salary) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (images != null ? !images.equals(that.images) : that.images != null) return false;
        if (smallImg != null ? !smallImg.equals(that.smallImg) : that.smallImg != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null)
            return false;
        if (orgName != null ? !orgName.equals(that.orgName) : that.orgName != null) return false;
        if (orgIntro != null ? !orgIntro.equals(that.orgIntro) : that.orgIntro != null)
            return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null)
            return false;
        return !(endTime != null ? !endTime.equals(that.endTime) : that.endTime != null);

    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + type.getValue();
        result = 31 * result + (salary != null ? salary.hashCode() : 0);
        result = 31 * result + (danceType != null ? danceType.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (images != null ? images.hashCode() : 0);
        result = 31 * result + (smallImg != null ? smallImg.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (orgName != null ? orgName.hashCode() : 0);
        result = 31 * result + (orgIntro != null ? orgIntro.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.userId);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.danceType);
        dest.writeString(this.salary);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.posBigImg);
        dest.writeString(this.posSmallImg);
        dest.writeString(this.videoUri);
        dest.writeString(this.videoPoster);
        dest.writeStringList(this.images);
        dest.writeStringList(this.smallImg);
        dest.writeString(this.location);
        dest.writeString(this.orgName);
        dest.writeString(this.orgIntro);
        dest.writeString(this.appEndTime);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
    }

    protected PublishInfoBean(Parcel in) {
        this.userId = in.readInt();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : DiscoverType.values()[tmpType];
        this.danceType = in.readString();
        this.salary = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        this.posBigImg = in.readString();
        this.posSmallImg = in.readString();
        this.videoUri = in.readString();
        this.videoPoster = in.readString();
        this.images = in.createStringArrayList();
        this.smallImg = in.createStringArrayList();
        this.location = in.readString();
        this.orgName = in.readString();
        this.orgIntro = in.readString();
        this.appEndTime = in.readString();
        this.startTime = in.readString();
        this.endTime = in.readString();
    }

    public static final Creator<PublishInfoBean> CREATOR = new Creator<PublishInfoBean>() {
        public PublishInfoBean createFromParcel(Parcel source) {
            return new PublishInfoBean(source);
        }

        public PublishInfoBean[] newArray(int size) {
            return new PublishInfoBean[size];
        }
    };
}
