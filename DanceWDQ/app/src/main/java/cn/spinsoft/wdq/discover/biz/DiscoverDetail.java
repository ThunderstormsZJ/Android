package cn.spinsoft.wdq.discover.biz;

import java.util.List;

import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.enums.ContestProgessState;
import cn.spinsoft.wdq.enums.DiscoverType;

/**
 * Created by hushujun on 15/12/9.
 */
public class DiscoverDetail {
    private String nickName;
    private String photoUrl;
    private String publishTime;//报名时间
    private String publishEndTime;//报名截止时间
    private String title;
    private String content;
    private String startTime;
    private String endTime;
    private String location;
    private String posSmallImg;//海报小图
    private String posBigImg;//海报大图
    private String videoImg;//视频图片
    private String videoUrl;//视频播放地址
    private DiscoverType type = DiscoverType.ACTIVITY;
    private ContestProgessState progessState;//赛事的进行的状态
    private boolean isLike = false;
    private int likeCount;
    private int commentCount;
    private int peopleNum;//报名人数
    private List<String> imageUrls;
    private List<String> smallImageUrls;//小图地址
    private List<SimpleItemData> likeUsers;
    private String danceName;
    private int forwardNum;
    private String orgName;
    private int userId;
    private int orgId;
    private String salary;
    private String orgIntro;
    private String city;//城市

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getOrgIntro() {
        return orgIntro;
    }

    public void setOrgIntro(String orgIntro) {
        this.orgIntro = orgIntro;
    }

    public String getDanceName() {
        return danceName;
    }

    public void setDanceName(String danceName) {
        this.danceName = danceName;
    }

    public int getForwardNum() {
        return forwardNum;
    }

    public void setForwardNum(int forwardNum) {
        this.forwardNum = forwardNum;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<SimpleItemData> getLikeUsers() {
        return likeUsers;
    }

    public void setLikeUsers(List<SimpleItemData> likeUsers) {
        this.likeUsers = likeUsers;
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

    public DiscoverType getType() {
        return type;
    }

    public void setType(DiscoverType type) {
        this.type = type;
    }

    public String getPosSmallImg() {
        return posSmallImg;
    }

    public void setPosSmallImg(String posSmallImg) {
        this.posSmallImg = posSmallImg;
    }

    public String getPosBigImg() {
        return posBigImg;
    }

    public void setPosBigImg(String posBigImg) {
        this.posBigImg = posBigImg;
    }

    public String getVideoImg() {
        return videoImg;
    }

    public void setVideoImg(String videoImg) {
        this.videoImg = videoImg;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public ContestProgessState getProgessState() {
        return progessState;
    }

    public void setProgessState(ContestProgessState progessState) {
        this.progessState = progessState;
    }

    public int getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(int peopleNum) {
        this.peopleNum = peopleNum;
    }

    public List<String> getSmallImageUrls() {
        return smallImageUrls;
    }

    public void setSmallImageUrls(List<String> smallImageUrls) {
        this.smallImageUrls = smallImageUrls;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public String getPublishEndTime() {
        return publishEndTime;
    }

    public void setPublishEndTime(String publishEndTime) {
        this.publishEndTime = publishEndTime;
    }

    @Override
    public String toString() {
        return "DiscoverDetail{" +
                "nickName='" + nickName + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", location='" + location + '\'' +
                ", type=" + type +
                ", isLike=" + isLike +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", imageUrls=" + imageUrls +
                ", likeUsers=" + likeUsers +
                ", danceName='" + danceName + '\'' +
                ", forwardNum=" + forwardNum +
                ", orgName='" + orgName + '\'' +
                ", userId=" + userId +
                ", salary='" + salary + '\'' +
                ", orgIntro='" + orgIntro + '\'' +
                '}';
    }
}
