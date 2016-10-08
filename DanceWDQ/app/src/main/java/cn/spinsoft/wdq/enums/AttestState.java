package cn.spinsoft.wdq.enums;

/**
 * Created by hushujun on 15/12/31.
 */
public enum AttestState {
    CONFIRMED(1), REJECTED(2), UNCONFIRM(3), INVITE(4);
    private int state;

    AttestState(int state) {
        this.state = state;
    }

    public int getValue() {
        return state;
    }

    public static AttestState getEnum(int value) {
        switch (value) {
            case 1:
                return CONFIRMED;
            case 2:
                return REJECTED;
            case 4:
                return INVITE;
            case 3:
            default:
                return UNCONFIRM;
        }
    }

}
