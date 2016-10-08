package cn.spinsoft.wdq.utils;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.enums.AgeRange;
import cn.spinsoft.wdq.enums.Sex;

/**
 * Created by hushujun on 15/12/8.
 */
public class SimpleItemDataUtils {
    private static final String TAG = SimpleItemDataUtils.class.getSimpleName();

    /**
     * 减少返回结果为空的可能
     *
     * @param hasDef
     * @param context
     * @return
     */
    public static List<SimpleItemData> getDanceTypeList(boolean hasDef, Context context) {
        List<SimpleItemData> danceTypes = SharedPreferencesUtil.getInstance(context).getDanceTypes();
        if (danceTypes == null || danceTypes.isEmpty()) {
            return danceTypes;
        }
        if (!hasDef) {
            List<SimpleItemData> delePositions = new ArrayList<>();
            for (int i = 0; i < danceTypes.size(); i++) {
                if (danceTypes.get(i).getName().contains("舞种")) {
                    delePositions.add(danceTypes.get(i));
                }
            }
            danceTypes.removeAll(delePositions);
        }
        return danceTypes;
    }

//    public static String getDanceName(String danceId) {
//        if (!TextUtils.isEmpty(danceId) && TextUtils.isDigitsOnly(danceId)) {
//            int id = Integer.valueOf(danceId);
//            List<SimpleItemData> danceTypes = GlobalDataExchange.getInstance().getDanceTypes();
//            for (SimpleItemData itemData : danceTypes) {
//                if (itemData.getId() == id) {
//                    return itemData.getNickName();
//                }
//            }
//        }
//        return "未知";
//    }

    public static List<SimpleItemData> getDistances() {
        List<SimpleItemData> itemList = new ArrayList<>();
//        itemList.add(new SimpleItemData("距离", -1));
        itemList.add(new SimpleItemData("不限", -1));
//        itemList.add(new SimpleItemData("附近", 500));
        itemList.add(new SimpleItemData("1千米", 1000));
        itemList.add(new SimpleItemData("2千米", 2000));
        itemList.add(new SimpleItemData("3千米", 3000));
        itemList.add(new SimpleItemData("5千米", 5000));
        return itemList;
    }

    public static List<SimpleItemData> getSexes(boolean hasDef) {
        List<SimpleItemData> itemList = new ArrayList<>();
        if (hasDef) {
//            itemList.add(new SimpleItemData("性别", -1));
            itemList.add(new SimpleItemData("不限", Sex.UNLIMITED.getValue()));
        }
        itemList.add(new SimpleItemData("女", Sex.FEMALE.getValue()));
        itemList.add(new SimpleItemData("男", Sex.MAN.getValue()));
        return itemList;
    }

    public static List<SimpleItemData> getAgeRanges(boolean hasDef) {
        List<SimpleItemData> itemList = new ArrayList<>();
        if (hasDef) {
//            itemList.add(new SimpleItemData("年龄段", -1));
            itemList.add(new SimpleItemData("不限", -1));
        }
        itemList.add(new SimpleItemData("50后", AgeRange.AGE_50.getValue()));
        itemList.add(new SimpleItemData("60后", AgeRange.AGE_60.getValue()));
        itemList.add(new SimpleItemData("70后", AgeRange.AGE_70.getValue()));
        itemList.add(new SimpleItemData("80后", AgeRange.AGE_80.getValue()));
        itemList.add(new SimpleItemData("90后", AgeRange.AGE_90.getValue()));
        itemList.add(new SimpleItemData("00后", AgeRange.AGE_00.getValue()));
        itemList.add(new SimpleItemData("10后", AgeRange.AGE_10.getValue()));
        return itemList;
    }

    public static List<SimpleItemData> getSalaries() {
        List<SimpleItemData> itemList = new ArrayList<>();
        itemList.add(new SimpleItemData("3K~5K", 2999));
        itemList.add(new SimpleItemData("5K~10K", 3000));
        itemList.add(new SimpleItemData("10K~15K", 5000));
        itemList.add(new SimpleItemData("15K~20K", 10000));
        return itemList;
    }

    public static List<SimpleItemData> getTalls() {
        List<SimpleItemData> itemList = new ArrayList<>();
        int tall = 150;
//        itemList.add(new SimpleItemData("2米以上", 201));
        while (tall < 210) {
            itemList.add(new SimpleItemData(tall + "cm", tall));
            tall += 10;
        }
//        itemList.add(new SimpleItemData("1.4米以下", 139));
        return itemList;
    }

    public static List<SimpleItemData> getVisibles() {
        List<SimpleItemData> itemList = new ArrayList<>();
        itemList.add(new SimpleItemData("否", 0));
        itemList.add(new SimpleItemData("是", 1));
        return itemList;
    }

    public static List<SimpleItemData> getSortList() {
        List<SimpleItemData> itemList = new ArrayList<>();
//        itemList.add(new SimpleItemData("排序", 0));
        itemList.add(new SimpleItemData("不限", 0));
        itemList.add(new SimpleItemData("热度排序", 1));
        return itemList;
    }

    public static List<SimpleItemData> getPopularSortList() {
        List<SimpleItemData> itemList = new ArrayList<>();
//        itemList.add(new SimpleItemData("排序", 0));
        itemList.add(new SimpleItemData("默认最热", 0));
        itemList.add(new SimpleItemData("从低到高", 1));
        return itemList;
    }

    public static String listNameToString(List<SimpleItemData> itemDataList) {
        if (itemDataList == null || itemDataList.isEmpty()) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();
            for (SimpleItemData itemData : itemDataList) {
                sb.append(itemData.getName());
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
    }

    public static List<String> stringToListName(String strName) {
        if (strName == null || TextUtils.isEmpty(strName)) {
            return null;
        } else {
            String[] splitName = strName.split(",");
            List<String> listName = new ArrayList<String>();
            for(int i=0;i<splitName.length;i++){
                listName.add(splitName[i]);
            }
            return listName;
        }
    }

    public static String listIdToString(List<SimpleItemData> itemDataList) {
        if (itemDataList == null || itemDataList.isEmpty()) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();
            for (SimpleItemData itemData : itemDataList) {
                sb.append(itemData.getId());
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
    }
}
