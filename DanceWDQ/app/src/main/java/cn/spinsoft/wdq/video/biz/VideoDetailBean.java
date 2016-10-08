package cn.spinsoft.wdq.video.biz;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.bean.SimpleItemData;

/**
 * Created by hushujun on 15/11/9.
 */
public class VideoDetailBean implements Parcelable{
    private String playUrl;
    private boolean attentioned;
    private boolean isLike;
    private boolean isOriginal;
    private String photoUrl;
    private String nickName;
    private int likeCount;
    private int forwardCount;
    private int commentCount;
    private int admireCount;
    private int videoId;
    private int userId;
    private int orgId;
    private String title;
    private String danceName;
    private String posterUrl;
    private String desc;
    private String timeDiff;
    private int watchCount;
    private List<SimpleItemData> userAdmire;//打赏人头像

    public VideoDetailBean(){}

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isAttentioned() {
        return attentioned;
    }

    public void setAttentioned(boolean attentioned) {
        this.attentioned = attentioned;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    public boolean isOriginal() {
        return isOriginal;
    }

    public void setIsOriginal(boolean isOriginal) {
        this.isOriginal = isOriginal;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getForwardCount() {
        return forwardCount;
    }

    public void setForwardCount(int forwardCount) {
        this.forwardCount = forwardCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getAdmireCount() {
        return admireCount;
    }

    public void setAdmireCount(int admireCount) {
        this.admireCount = admireCount;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDanceName() {
        return danceName;
    }

    public void setDanceName(String danceName) {
        this.danceName = danceName;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTimeDiff() {
        return timeDiff;
    }

    public void setTimeDiff(String timeDiff) {
        this.timeDiff = timeDiff;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(int watchCount) {
        this.watchCount = watchCount;
    }

    public List<SimpleItemData> getUserAdmire() {
        return userAdmire;
    }

    public void setUserAdmire(List<SimpleItemData> userAdmire) {
        this.userAdmire = userAdmire;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    @Override
    public String toString() {
        return "VideoDetailBean{" +
                "playUrl='" + playUrl + '\'' +
                ", attentioned=" + attentioned +
                ", isLike=" + isLike +
                ", isOriginal=" + isOriginal +
                ", photoUrl='" + photoUrl + '\'' +
                ", likeCount=" + likeCount +
                ", forwardCount=" + forwardCount +
                ", commentCount=" + commentCount +
                ", admireCount=" + admireCount +
                ", videoId=" + videoId +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", danceName='" + danceName + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                ", desc='" + desc + '\'' +
                ", timeDiff='" + timeDiff + '\'' +
                ", watchCount=" + watchCount +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.playUrl);
        dest.writeByte(attentioned ? (byte) 1 : (byte) 0);
        dest.writeByte(isLike ? (byte) 1 : (byte) 0);
        dest.writeByte(isOriginal ? (byte) 1 : (byte) 0);
        dest.writeString(this.photoUrl);
        dest.writeString(this.nickName);
        dest.writeInt(this.likeCount);
        dest.writeInt(this.forwardCount);
        dest.writeInt(this.commentCount);
        dest.writeInt(this.admireCount);
        dest.writeInt(this.videoId);
        dest.writeInt(this.userId);
        dest.writeInt(this.orgId);
        dest.writeString(this.title);
        dest.writeString(this.danceName);
        dest.writeString(this.posterUrl);
        dest.writeString(this.desc);
        dest.writeString(this.timeDiff);
        dest.writeInt(this.watchCount);
        dest.writeList(this.userAdmire);
    }

    protected VideoDetailBean(Parcel in) {
        this.playUrl = in.readString();
        this.attentioned = in.readByte() != 0;
        this.isLike = in.readByte() != 0;
        this.isOriginal = in.readByte() != 0;
        this.photoUrl = in.readString();
        this.nickName = in.readString();
        this.likeCount = in.readInt();
        this.forwardCount = in.readInt();
        this.commentCount = in.readInt();
        this.admireCount = in.readInt();
        this.videoId = in.readInt();
        this.userId = in.readInt();
        this.orgId = in.readInt();
        this.title = in.readString();
        this.danceName = in.readString();
        this.posterUrl = in.readString();
        this.desc = in.readString();
        this.timeDiff = in.readString();
        this.watchCount = in.readInt();
        this.userAdmire = new ArrayList<SimpleItemData>();
        in.readList(this.userAdmire, List.class.getClassLoader());
    }

    public static final Creator<VideoDetailBean> CREATOR = new Creator<VideoDetailBean>() {
        public VideoDetailBean createFromParcel(Parcel source) {
            return new VideoDetailBean(source);
        }

        public VideoDetailBean[] newArray(int size) {
            return new VideoDetailBean[size];
        }
    };
}
