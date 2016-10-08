package cn.spinsoft.wdq.enums;

/**
 * Created by zhoujun on 2016-5-17.
 */
public enum ContestProgessState {
    NOSTART(0, "未开始"), INPROGESS(1, "进行中"), END(2, "已结束");

    private int state;
    private String desc;

    ContestProgessState(int state, String desc) {
        this.state = state;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public int getValue() {
        return state;
    }

    public static ContestProgessState getEnum(int value) {
        if (value == NOSTART.getValue()) {
            return NOSTART;
        } else if (value == INPROGESS.getValue()) {
            return INPROGESS;
        } else if (value == END.getValue()) {
            return END;
        } else {
            return END;
        }
    }
}
