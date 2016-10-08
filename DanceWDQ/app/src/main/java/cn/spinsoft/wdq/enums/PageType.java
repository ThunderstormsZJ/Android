package cn.spinsoft.wdq.enums;

import cn.spinsoft.wdq.utils.Constants;

/**
 * Created by hushujun on 15/12/3.
 */
public enum PageType {
    VIDEO(Constants.Strings.TYPE_VIDEO), FRIEND(Constants.Strings.TYPE_FRIEND),
    ORG(Constants.Strings.TYPE_ORGANIZATION), DISCOVER(Constants.Strings.TYPE_DISCOVER),
    UN("unknown");
    private String value;

    PageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PageType getEnum(String value) {
        if (value.equals(VIDEO.value)) {
            return VIDEO;
        } else if (value.equals(FRIEND.value)) {
            return FRIEND;
        } else if (value.equals(ORG.value)) {
            return ORG;
        } else if (value.equals(DISCOVER.value)) {
            return DISCOVER;
        } else {
            return UN;
        }
    }
}
