package cn.spinsoft.wdq.enums;

/**
 * Created by zhoujun on 2016-3-23.
 */
public enum MoneyFlow {
    BALANCEOUT(1,"余额支出"),WITHDRAW(2,"提现"),BALANCEIN(3,"转入"),WXOUT(4,"微信支出");

    private int moneyType;

    public int getMoneyType() {
        return moneyType;
    }

    MoneyFlow(int type,String desc){
        this.moneyType = type;
    }

    public static MoneyFlow getEnum(int type){
        switch (type){
            case 1:
                return BALANCEOUT;
            case 2:
                return WITHDRAW;
            case 3:
                return BALANCEIN;
            default:
                return WXOUT;
        }
    }
}
