package cn.spinsoft.wdq.video.biz;

import android.os.Parcel;
import android.os.Parcelable;

import cn.spinsoft.wdq.enums.Attention;

/**
 * Created by hushujun on 15/11/5.
 */
public class DanceVideoBean implements Parcelable {
    private String photoUrl;
    private String nickName;
    private int userId;
    private int orgId;
    private int videoId;
    private Attention attention = Attention.NONE;
    private boolean liked = false;
    private boolean isOriginal = false;
    private String posterUrl;
    private String playUrl;//播放路径
    private String danceName;
    private String title;
    private String timeDiff;
    private int likeCount;//喜爱
    private int forwardCount;//转发
    private int commentCount;//评论
    private int admireCount;//打赏

    public DanceVideoBean() {}

    private DanceVideoBean(Parcel in) {
        photoUrl = in.readString();
        nickName = in.readString();
        userId = in.readInt();
        videoId = in.readInt();
        attention = Attention.getEnum(in.readInt());
        liked = in.readByte() != 0;
        isOriginal = in.readByte() != 0;
        posterUrl = in.readString();
        playUrl = in.readString();
        danceName = in.readString();
        title = in.readString();
        timeDiff = in.readString();
        likeCount = in.readInt();
        forwardCount = in.readInt();
        commentCount = in.readInt();
        forwardCount = in.readInt();
    }

    public static final Creator<DanceVideoBean> CREATOR = new Creator<DanceVideoBean>() {
        @Override
        public DanceVideoBean createFromParcel(Parcel in) {
            return new DanceVideoBean(in);
        }

        @Override
        public DanceVideoBean[] newArray(int size) {
            return new DanceVideoBean[size];
        }
    };

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
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

    public int getUserId() {
        return userId;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Attention getAttention() {
        return attention;
    }

    public void setAttention(Attention attention) {
        this.attention = attention;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getDanceName() {
        return danceName;
    }

    public void setDanceName(String danceName) {
        this.danceName = danceName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimeDiff() {
        return timeDiff;
    }

    public void setTimeDiff(String timeDiff) {
        this.timeDiff = timeDiff;
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

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    @Override
    public String toString() {
        return "DanceVideoBean{" +
                "photoUrl='" + photoUrl + '\'' +
                ", nickName='" + nickName + '\'' +
                ", userId=" + userId +
                ", videoId=" + videoId +
                ", attention=" + attention +
                ", liked=" + liked +
                ", isOriginal=" + isOriginal +
                ", posterUrl='" + posterUrl + '\'' +
                ", danceName='" + danceName + '\'' +
                ", title='" + title + '\'' +
                ", timeDiff='" + timeDiff + '\'' +
                ", likeCount=" + likeCount +
                ", forwardCount=" + forwardCount +
                ", commentCount=" + commentCount +
                ", admireCount=" + admireCount +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(photoUrl);
        dest.writeString(nickName);
        dest.writeInt(userId);
        dest.writeInt(videoId);
        dest.writeInt(attention.getValue());
        dest.writeByte((byte) (liked ? 1 : 0));
        dest.writeByte((byte) (isOriginal ? 1 : 0));
        dest.writeString(posterUrl);
        dest.writeString(playUrl);
        dest.writeString(danceName);
        dest.writeString(title);
        dest.writeString(timeDiff);
        dest.writeInt(likeCount);
        dest.writeInt(forwardCount);
        dest.writeInt(commentCount);
        dest.writeInt(forwardCount);
    }

}
