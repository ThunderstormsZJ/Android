package cn.spinsoft.wdq.mine.biz;

/**
 * Created by hushujun on 16/1/12.
 */
public class RelatedBean {
    private String createTime;
    private int currUserId;
    private String currName;
    private int type;
    private int discoverType;//活动类型
    private int anotherUserId;
    private String anotherName;
    private String anotherPhoto;
    private String comment;
    private int recodeId;
    private String imageUrl;
    private String relatedTitle;
    private String relatedContent;
    private int relatedId;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCurrName() {
        return currName;
    }

    public void setCurrName(String currName) {
        this.currName = currName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getCurrUserId() {
        return currUserId;
    }

    public void setCurrUserId(int currUserId) {
        this.currUserId = currUserId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAnotherUserId() {
        return anotherUserId;
    }

    public void setAnotherUserId(int anotherUserId) {
        this.anotherUserId = anotherUserId;
    }

    public String getAnotherName() {
        return anotherName;
    }

    public void setAnotherName(String anotherName) {
        this.anotherName = anotherName;
    }

    public String getAnotherPhoto() {
        return anotherPhoto;
    }

    public void setAnotherPhoto(String anotherPhoto) {
        this.anotherPhoto = anotherPhoto;
    }

    public int getRecodeId() {
        return recodeId;
    }

    public void setRecodeId(int recodeId) {
        this.recodeId = recodeId;
    }

    public String getRelatedContent() {
        return relatedContent;
    }

    public void setRelatedContent(String relatedContent) {
        this.relatedContent = relatedContent;
    }

    public int getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(int relatedId) {
        this.relatedId = relatedId;
    }

    public String getRelatedTitle() {
        return relatedTitle;
    }

    public void setRelatedTitle(String relatedTitle) {
        this.relatedTitle = relatedTitle;
    }

    public int getDiscoverType() {
        return discoverType;
    }

    public void setDiscoverType(int discoverType) {
        this.discoverType = discoverType;
    }
}