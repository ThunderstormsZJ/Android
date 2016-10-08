package cn.spinsoft.wdq.enums;

/**
 * Created by hushujun on 15/12/2.
 */
public enum UserType {
    PERSONAL("个人", "0"), STORE("商家", "1"), UNKNOWN("未知", "-1");

    private String type;
    private String name;

    UserType(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getValue() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static UserType getEnum(String type) {
        if (type.equals(PERSONAL.getValue())) {
            return PERSONAL;
        } else if (type.equals(STORE.getValue())) {
            return STORE;
        } else {
            return UNKNOWN;
        }
    }
}