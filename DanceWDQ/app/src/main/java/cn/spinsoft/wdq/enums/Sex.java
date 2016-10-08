package cn.spinsoft.wdq.enums;

/**
 * Created by zhoujun on 15/11/19.
 */
public enum Sex {
    UNKNOWN("未知", -1), MAN("男", 2), FEMALE("女", 1), UNLIMITED("不限", 0);

    private int sex;
    private String sexStr;

    Sex(String sexStr, int sex) {
        this.sex = sex;
        this.sexStr = sexStr;
    }

    public int getValue() {
        return sex;
    }

    public String getName() {
        return sexStr;
    }

    public static Sex getEnum(int sex) {
        if (sex == MAN.getValue()) {
            return MAN;
        } else if (sex == FEMALE.getValue()) {
            return FEMALE;
        } else if (sex == UNLIMITED.getValue()) {
            return UNLIMITED;
        } else {
            return UNKNOWN;
        }
    }

    public static Sex getEnum(String sex) {
        if (sex.equals(MAN.getName())) {
            return MAN;
        } else if (sex.equals(FEMALE.getName())) {
            return FEMALE;
        } else if (sex.equals(UNLIMITED.getName())) {
            return UNLIMITED;
        } else {
            return UNKNOWN;
        }
    }
}
