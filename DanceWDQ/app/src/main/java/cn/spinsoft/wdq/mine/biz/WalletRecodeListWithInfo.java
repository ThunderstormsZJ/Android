package cn.spinsoft.wdq.mine.biz;

import cn.spinsoft.wdq.base.bean.ListDataWithInfo;

/**
 * Created by hushujun on 16/1/13.
 */
public class WalletRecodeListWithInfo extends ListDataWithInfo<WalletRecode> {
    private String balance;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
