package cn.spinsoft.wdq.db;

/**
 * Created by hushujun on 15/11/10.
 */
public class VideoRecodeBean {
    private int videoId = -1;
    private boolean attention = false;
    private boolean like = false;
    private boolean forward = false;
    private boolean comment = false;
    private boolean tips = false;

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public boolean isAttention() {
        return attention;
    }

    public void setAttention(boolean attention) {
        this.attention = attention;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    public boolean isTips() {
        return tips;
    }

    public void setTips(boolean tips) {
        this.tips = tips;
    }

    @Override
    public String toString() {
        return "VideoRecodeBean{" +
                "videoId=" + videoId +
                ", attention=" + attention +
                ", like=" + like +
                ", forward=" + forward +
                ", comment=" + comment +
                ", tips=" + tips +
                '}';
    }
}
