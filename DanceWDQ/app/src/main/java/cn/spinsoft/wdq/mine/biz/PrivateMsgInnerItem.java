package cn.spinsoft.wdq.mine.biz;

/**
 * Created by hushujun on 16/1/18.
 */
public class PrivateMsgInnerItem {
    private String createTime;
    private String headUrl;
    private int recodeId;
    private int state;
    private int userId;
    private String content;
    private boolean sendBySelf = false;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public int getRecodeId() {
        return recodeId;
    }

    public void setRecodeId(int recodeId) {
        this.recodeId = recodeId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSendBySelf() {
        return sendBySelf;
    }

    public void setSendBySelf(boolean sendBySelf) {
        this.sendBySelf = sendBySelf;
    }
}
