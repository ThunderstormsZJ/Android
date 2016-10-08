package cn.spinsoft.wdq.video.biz;

import cn.spinsoft.wdq.enums.DiscoverType;

/**
 * 描述：广告信息</br>
 *
 * @author zhoujun</br>
 * @version 2015年4月23日 上午11:32:53
 */
public class AdvertisementInfo {
    private String id;
    private String imgUrl;
    private String content;
    private int type;
    private int discoverId;
    private int userId;
    private DiscoverType skipType;
    private String skipUrl;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DiscoverType getSkipType() {
        return skipType;
    }

    public void setSkipType(DiscoverType skipType) {
        this.skipType = skipType;
    }

    public String getSkipUrl() {
        return skipUrl;
    }

    public void setSkipUrl(String skipUrl) {
        this.skipUrl = skipUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDiscoverId() {
        return discoverId;
    }

    public void setDiscoverId(int discoverId) {
        this.discoverId = discoverId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
