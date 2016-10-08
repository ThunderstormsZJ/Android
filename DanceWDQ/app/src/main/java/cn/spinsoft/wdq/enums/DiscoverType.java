package cn.spinsoft.wdq.enums;

/**
 * Created by hushujun on 15/12/1.
 */
public enum DiscoverType {
    ACTIVITY("活动", 1), TOPIC("话题", 2), CONTEST("赛事", 3), RECRUIT("招聘", 4), OTHER("寻宝", 5), UNKNOWN("未知", -1);

    private String name;
    private int value;

    DiscoverType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public static DiscoverType getEnum(int value) {
        if (value == ACTIVITY.value) {
            return ACTIVITY;
        } else if (value == TOPIC.value) {
            return TOPIC;
        } else if (value == CONTEST.value) {
            return CONTEST;
        } else if (value == RECRUIT.value) {
            return RECRUIT;
        } else if (value == OTHER.value) {
            return OTHER;
        } else {
            return UNKNOWN;
        }
    }
}
