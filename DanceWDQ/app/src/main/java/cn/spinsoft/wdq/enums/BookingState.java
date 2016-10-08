package cn.spinsoft.wdq.enums;

/**
 * Created by hushujun on 16/1/5.
 */
public enum BookingState {
    CONFIRMED(0, "已确认"), REJECTED(2, "已拒绝"), CANCELED(3, "已取消"), UNCONFIRM(1, "待确认"), ALL(-1, "全部");
    private int value;
    private String name;

    BookingState(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public static BookingState getEnum(int value) {
        switch (value) {
            case 1:
                return UNCONFIRM;
            case 2:
                return REJECTED;
            case 3:
                return CANCELED;
            case 0:
            default:
                return CONFIRMED;
        }
    }
}
