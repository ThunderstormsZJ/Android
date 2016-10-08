package cn.spinsoft.wdq.enums;

/**
 * Created by hushujun on 15/11/19.
 */
public enum Attention {
    //未知        无关系     关注对方        被对方关注   互相关注
    UNKNOWN(-1), NONE(0), INITIATIVE(1), PASSIVE(2), MUTUAL(3);

    private int a;

    Attention(int value) {
        a = value;
    }

    public int getValue() {
        return a;
    }

    public boolean isAttented() {
        return a % 2 == 1;
    }

    public static Attention getEnum(int value) {
        if (value == NONE.getValue()) {
            return NONE;
        } else if (value == INITIATIVE.getValue()) {
            return INITIATIVE;
        } else if (value == PASSIVE.getValue()) {
            return PASSIVE;
        } else if (value == MUTUAL.getValue()) {
            return MUTUAL;
        } else {
            return UNKNOWN;
        }
    }

    public static Attention getEnumByBn(boolean isAttention){
        if(isAttention){
            return Attention.INITIATIVE;
        }else{
            return Attention.NONE;
        }
    }

    public static Attention getReverse(Attention att) {
        switch (att) {
            case NONE:
                return INITIATIVE;
            case PASSIVE:
                return MUTUAL;
            case INITIATIVE:
                return NONE;
            case MUTUAL:
                return PASSIVE;
            default:
                return UNKNOWN;
        }
    }
}
