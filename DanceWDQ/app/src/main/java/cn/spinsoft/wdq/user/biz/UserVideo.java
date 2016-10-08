package cn.spinsoft.wdq.user.biz;

/**
 * Created by hushujun on 15/12/2.
 */
public class UserVideo {
    private String playUrl;
    private String createTime;
    private String danceId;
    private String introduce;
    private String poster;
    private String title;
    private int userId;
    private int videoId;

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDanceId() {
        return danceId;
    }

    public void setDanceId(String danceId) {
        this.danceId = danceId;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    @Override
    public String toString() {
        return "UserVideo{" +
                "playUrl='" + playUrl + '\'' +
                ", createTime='" + createTime + '\'' +
                ", danceId='" + danceId + '\'' +
                ", introduce='" + introduce + '\'' +
                ", poster='" + poster + '\'' +
                ", title='" + title + '\'' +
                ", userId=" + userId +
                ", videoId=" + videoId +
                '}';
    }
}
