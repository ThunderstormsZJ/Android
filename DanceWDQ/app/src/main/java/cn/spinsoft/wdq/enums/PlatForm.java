package cn.spinsoft.wdq.enums;

import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by zhoujun on 2016-3-4.
 */
public enum PlatForm {
    WEIXI(SHARE_MEDIA.WEIXIN,1),QQ(SHARE_MEDIA.QQ,2);

    private SHARE_MEDIA pfName;
    private int pfType;

    public SHARE_MEDIA getPfName() {
        return pfName;
    }
    public int getPfType() {
        return pfType;
    }

    PlatForm(SHARE_MEDIA pfName, int pfType) {
        this.pfName = pfName;
        this.pfType = pfType;
    }

    public static PlatForm getEnum(SHARE_MEDIA pfName){
        if(pfName == WEIXI.getPfName()){
            return WEIXI;
        }else if(pfName == QQ.getPfName()){
            return QQ;
        }else {
            return null;
        }
    }
}
