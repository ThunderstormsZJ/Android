package cn.spinsoft.wdq.discover.biz;

import java.util.List;

import cn.spinsoft.wdq.enums.ContestProgessState;
import cn.spinsoft.wdq.enums.DiscoverType;

/**
 * Created by hushujun on 15/11/24.
 */
public class DiscoverItemBean {
    private String photoUrl;
    private int userId;
    private int orgId;
    private String nickName;
    private String title;
    private String content;
    private String location;
    private String time;
    private String posSmallImg;//海报小图
    private String posBigImg;//海报大图
    private ContestProgessState progessState;//赛事的进行的状态
    private boolean isLike = false;
    private List<String> imagesUrls;
    private List<String> smallImgs;//加载小图
    private int likeCount;
    private int forwardCount;
    private int commentCount;
    private DiscoverType type;
    private int eventId;
    private int forwardId;
    private int forwarType;
    private int personNum;//报名人数
    private String salary;//薪资
    private String city;//城市

    public boolean isLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    public ContestProgessState getProgessState() {
        return progessState;
    }

    public void setProgessState(ContestProgessState progessState) {
        this.progessState = progessState;
    }

    public DiscoverType getType() {
        return type;
    }

    public void setType(DiscoverType type) {
        this.type = type;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<String> getImagesUrls() {
        return imagesUrls;
    }

    public void setImagesUrls(List<String> imagesUrls) {
        this.imagesUrls = imagesUrls;
    }

    public List<String> getSmallImgs() {
        return smallImgs;
    }

    public void setSmallImgs(List<String> smallImgs) {
        this.smallImgs = smallImgs;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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

    public int getForwardId() {
        return forwardId;
    }

    public void setForwardId(int forwardId) {
        this.forwardId = forwardId;
    }

    public int getForwarType() {
        return forwarType;
    }

    public void setForwarType(int forwarType) {
        this.forwarType = forwarType;
    }

    public int getPersonNum() {
        return personNum;
    }

    public void setPersonNum(int personNum) {
        this.personNum = personNum;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "DiscoverItemBean{" +
                "photoUrl='" + photoUrl + '\'' +
                ", userId=" + userId +
                ", nickName='" + nickName + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", location='" + location + '\'' +
                ", time='" + time + '\'' +
                ", isLike=" + isLike +
                ", imagesUrls=" + imagesUrls +
                ", smallImgs=" + smallImgs +
                ", likeCount=" + likeCount +
                ", forwardCount=" + forwardCount +
                ", commentCount=" + commentCount +
                ", type=" + type +
                ", eventId=" + eventId +
                ", forwardId=" + forwardId +
                ", forwarType=" + forwarType +
                '}';
    }
}
