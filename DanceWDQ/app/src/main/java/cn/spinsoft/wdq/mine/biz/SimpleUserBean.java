package cn.spinsoft.wdq.mine.biz;

import cn.spinsoft.wdq.base.bean.UserBase;
import cn.spinsoft.wdq.enums.AttestState;

/**
 * Created by hushujun on 15/12/31.
 */
public class SimpleUserBean extends UserBase {

    private AttestState state;
    private int orgId = -1;
    private int recodeId;

    public int getRecodeId() {
        return recodeId;
    }

    public void setRecodeId(int recodeId) {
        this.recodeId = recodeId;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public AttestState getState() {
        return state;
    }

    public void setState(AttestState state) {
        this.state = state;
    }
}
