package cn.spinsoft.wdq.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.spinsoft.wdq.bean.SimpleItemData;

/**
 * Created by zhoujun on 15/11/3.
 */
public class StringUtils {

    public static SpannableString packageHighLightString(CharSequence string, int start, int end, String color) {
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        SpannableString sp = new SpannableString(string);
        int length = string.length();
        if (start == end || length < start) {
            return new SpannableString(string);
        } else if (start <= length && length < end) {
            end = length;
        }
        sp.setSpan(new ForegroundColorSpan(Color.parseColor(color)), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return sp;
    }

    public static SpannableString packageSizeString(CharSequence string, int start, int end, int size) {
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        SpannableString sp = new SpannableString(string);
        int length = string.length();
        if (start == end || length < start) {
            return new SpannableString(string);
        } else if (start <= length && length < end) {
            end = length;
        }
        sp.setSpan(new AbsoluteSizeSpan(size,true), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return sp;
    }

    public static SpannableString packageLinkString(CharSequence string, String url) {
        return packageLinkString(string, 0, 0, url);
    }

    public static SpannableString packageLinkString(CharSequence string, int start, int end, String url) {
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        SpannableString sp = new SpannableString(string);
        if (TextUtils.isEmpty(url)) {
            return sp;
        }
        int length = string.length();
        if (start == end || length < start) {
            start = 0;
            end = length;
        } else if (start <= length && length < end) {
            end = length;
        }
        sp.setSpan(new URLSpan(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }


    @Deprecated
    public static List<SimpleItemData> packageSpinnerItemFromResource(Context context, int strArrayId) {
        String[] strs = context.getResources().getStringArray(strArrayId);
        if (strs == null || strs.length <= 0) {
            return null;
        }
        List<SimpleItemData> itemList = new ArrayList<>();
        for (int i = 0; i < strs.length; i++) {
            SimpleItemData item = new SimpleItemData();
            item.setId(i);
            item.setName(strs[i]);
            itemList.add(item);
        }
        return itemList;
    }

    public static String getApplicationName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            String applicationName = packageManager.getApplicationLabel(applicationInfo).toString();
            return applicationName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getNoNullString(String string) {
        if (TextUtils.isEmpty(string) || "null".equals(string)) {
            return "";
        }
        return string;
    }

    public static String listContentToString(List objectList) {
        if (objectList == null || objectList.isEmpty()) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Object obj : objectList) {
            if (obj instanceof String) {//String 类型过滤掉空字符串
                if (!TextUtils.isEmpty((String) obj)) {
                    sb.append(",");
                    sb.append(obj);
                }
            } else {
                sb.append(",");
                sb.append(obj.toString());
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    /**
     * @param ms
     * @return yyyy年MM月dd日 HH时
     */
    public static String formatTime(long ms) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时");
        return sdf.format(new Date(ms));
    }

    /**
     * @param year
     * @param month
     * @param day
     * @param hour
     * @return %04d年%02d月%02d日 %02d时
     */
    public static String formatTime(int year, int month, int day, int hour) {
        return String.format("%04d年%02d月%02d日 %02d:00", year, month, day, hour);
    }

    /**
     * @param time
     * @return yyyy年MM月dd日 HH时
     */
    public static long formatTime(String time) {
        if (TextUtils.isEmpty(time)) {
            return System.currentTimeMillis();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时");
        try {
            Date date = sdf.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis();
    }

    /**
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String formatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * @return yyyyMMdd_HHmmss
     */
    public static String formatCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date());
    }

    //读取文件中的文本
    public static String getStringFromText(InputStream inputStream) {
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            reader = new InputStreamReader(inputStream, "utf-8");
            bufferedReader = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line=bufferedReader.readLine())!=null){
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    //第二行的缩小
    public static SpannableString changeTextSize(String textContent,int fontSize){
        SpannableString spannableString  = new SpannableString(textContent);
        int index = textContent.indexOf("\n");
        if(index>-1){
            spannableString.setSpan(new AbsoluteSizeSpan(fontSize),index,spannableString.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return  spannableString;
    }

    //为空时添加提示
    public static String getNoNullStringWithTip(String content) {
        if(content==null || TextUtils.isEmpty(content) || "null".equals(content)){
            return "未填写";
        }
        return content;
    }

    /**
     * 去除空格
     */
    public static String getNoBlankString(String content){
        if(content.contains(" ")){
            content = content.replace(" ","");
        }
        return content;
    }
}
