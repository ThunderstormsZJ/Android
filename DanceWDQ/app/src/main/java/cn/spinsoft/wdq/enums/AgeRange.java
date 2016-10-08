package cn.spinsoft.wdq.enums;

/**
 * Created by hushujun on 15/11/19.
 */
public enum AgeRange {
    UNKNOWN("未知", -1), AGE_00("00后", 0), AGE_10("10后", 1), AGE_20("20后", 2), AGE_30("30后", 3),
    AGE_40("40后", 4), AGE_50("50后", 5), AGE_60("60后", 6), AGE_70("70后", 7), AGE_80("80后", 8), AGE_90("90后", 9);
    private int age;
    private String range;

    AgeRange(String range, int age) {
        this.age = age;
        this.range = range;
    }

    public int getValue() {
        return age;
    }

    public String getName() {
        return range;
    }

    public static AgeRange getEnum(int age) {
        if (age == AGE_10.getValue()) {
            return AGE_10;
        } else if (age == AGE_20.getValue()) {
            return AGE_20;
        } else if (age == AGE_30.getValue()) {
            return AGE_30;
        } else if (age == AGE_40.getValue()) {
            return AGE_40;
        } else if (age == AGE_50.getValue()) {
            return AGE_50;
        } else if (age == AGE_60.getValue()) {
            return AGE_60;
        } else if (age == AGE_70.getValue()) {
            return AGE_70;
        } else if (age == AGE_80.getValue()) {
            return AGE_80;
        } else if (age == AGE_90.getValue()) {
            return AGE_90;
        } else if (age == AGE_00.getValue()) {
            return AGE_00;
        } else {
            return UNKNOWN;
        }
    }

    public static AgeRange getEnum(String rang) {
        if (rang.equals(AGE_10.getName())) {
            return AGE_10;
        } else if (rang.equals(AGE_20.getName())) {
            return AGE_20;
        } else if (rang.equals(AGE_30.getName())) {
            return AGE_30;
        } else if (rang.equals(AGE_40.getName())) {
            return AGE_40;
        } else if (rang.equals(AGE_50.getName())) {
            return AGE_50;
        } else if (rang.equals(AGE_60.getName())) {
            return AGE_60;
        } else if (rang.equals(AGE_70.getName())) {
            return AGE_70;
        } else if (rang.equals(AGE_80.getName())) {
            return AGE_80;
        } else if (rang.equals(AGE_90.getName())) {
            return AGE_90;
        } else if (rang.equals(AGE_00.getName())) {
            return AGE_00;
        } else {
            return UNKNOWN;
        }
    }
}
