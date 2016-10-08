package cn.spinsoft.wdq.mine.biz;

import cn.spinsoft.wdq.enums.MoneyFlow;

/**
 * Created by hushujun on 16/1/13.
 */
public class WalletRecode {
    private String createTime;
    private String quantity;
    private String remark;
    private String tradeName;
    private int recodeId;
    private MoneyFlow type;
    private int userId;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getRecodeId() {
        return recodeId;
    }

    public void setRecodeId(int recodeId) {
        this.recodeId = recodeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public MoneyFlow getType() {
        return type;
    }

    public void setType(MoneyFlow type) {
        this.type = type;
    }
}
