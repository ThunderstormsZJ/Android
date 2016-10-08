package cn.spinsoft.wdq.user.biz;

import cn.spinsoft.wdq.base.bean.ListDataWithInfo;

/**
 * Created by hushujun on 15/12/2.
 */
public class UserVideoWithInfo extends ListDataWithInfo<UserVideo> {
    private int thumNum;

    public int getThumNum() {
        return thumNum;
    }

    public void setThumNum(int thumNum) {
        this.thumNum = thumNum;
    }
}
