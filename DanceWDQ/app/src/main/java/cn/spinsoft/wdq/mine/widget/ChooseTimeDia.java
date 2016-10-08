package cn.spinsoft.wdq.mine.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.StringUtils;
import cn.spinsoft.wdq.widget.SimpleTextRecyclerAdapter;
import cn.spinsoft.wdq.widget.wheel.RecycleWheelView;

/**
 * Created by zhoujun on 15/12/22.
 */
public class ChooseTimeDia extends Dialog implements RecycleWheelView.OnSelectItemListener {
    private static final String TAG = ChooseTimeDia.class.getSimpleName();

    private RecycleWheelView mYearWheel, mMonthWheel, mDayWheel, mHourWheel;
    private Button mConfirmBen;
    private OnTimeChooseListener timeChooseListener;

    private int[] miniTimeFields = new int[4];
    private int[] selectedTimeFields = new int[4];
    private static final int YEAR = 0;
    private static final int MONTH = 1;
    private static final int DAY = 2;
    private static final int HOUR = 3;
    private static final int MINUTE = 4;

    private SimpleTextRecyclerAdapter yearAdapter, mothAdapter, dayAdapter, hourAdapter;

    private int where = 0;

    public interface OnTimeChooseListener {
        void onTimeChoose(int where, String timeString);
    }

    public ChooseTimeDia(Context context, OnTimeChooseListener listener) {
        super(context, R.style.DialogWithTransparentBackground);
        this.timeChooseListener = listener;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "onCreate");
        setContentView(R.layout.dia_choose_time);
        mYearWheel = (RecycleWheelView) findViewById(R.id.choose_time_year);
        mMonthWheel = (RecycleWheelView) findViewById(R.id.choose_time_month);
        mDayWheel = (RecycleWheelView) findViewById(R.id.choose_time_day);
        mHourWheel = (RecycleWheelView) findViewById(R.id.choose_time_hour);
        mConfirmBen = (Button) findViewById(R.id.choose_time_confirm);

        yearAdapter = new SimpleTextRecyclerAdapter(null);
        mYearWheel.setAdapter(yearAdapter);
        mothAdapter = new SimpleTextRecyclerAdapter(null);
        mMonthWheel.setAdapter(mothAdapter);
        dayAdapter = new SimpleTextRecyclerAdapter(null);
        mDayWheel.setAdapter(dayAdapter);
        hourAdapter = new SimpleTextRecyclerAdapter(null);
        mHourWheel.setAdapter(hourAdapter);

        mYearWheel.setOnSelectListener(this);
        mMonthWheel.setOnSelectListener(this);
        mDayWheel.setOnSelectListener(this);
        mHourWheel.setOnSelectListener(this);

        mConfirmBen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeChooseListener != null) {
                    timeChooseListener.onTimeChoose(where, StringUtils.formatTime(selectedTimeFields[YEAR],
                            selectedTimeFields[MONTH], selectedTimeFields[DAY], selectedTimeFields[HOUR]));
                    dismiss();
                }
            }
        });

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    private void setCurrentTime(String timeString) {
        long time = StringUtils.formatTime(timeString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        miniTimeFields[YEAR] = calendar.get(Calendar.YEAR);
        miniTimeFields[MONTH] = calendar.get(Calendar.MONTH) + 1;
        miniTimeFields[DAY] = calendar.get(Calendar.DAY_OF_MONTH);
        miniTimeFields[HOUR] = calendar.get(Calendar.HOUR_OF_DAY) + 1;

        notifyTimeAdapters();
    }

    private void notifyTimeAdapters() {
        notifyYearAdapter();
        notifyMonthAdapter(miniTimeFields[MONTH]);
        notifyDayAdapter(miniTimeFields[YEAR], miniTimeFields[MONTH], miniTimeFields[DAY]);
        notifyHourAdapter(miniTimeFields[HOUR]);
    }

    private void notifyYearAdapter() {
        selectedTimeFields[YEAR] = miniTimeFields[YEAR];

        List<SimpleItemData> yearItems = new ArrayList<>();
        yearItems.add(new SimpleItemData(String.valueOf(miniTimeFields[YEAR]), miniTimeFields[YEAR]));
        yearItems.add(new SimpleItemData(String.valueOf(miniTimeFields[YEAR] + 1), miniTimeFields[YEAR] + 1));
        yearAdapter.setAdapterDataList(yearItems);
    }

    private void notifyMonthAdapter(int miniMonth) {
        if (selectedTimeFields[YEAR] == miniTimeFields[YEAR]) {
            miniMonth = miniTimeFields[MONTH];
        }
        selectedTimeFields[MONTH] = miniMonth;

        List<SimpleItemData> monthItems = new ArrayList<>();
        for (int moth = miniMonth; moth <= 12; moth++) {
            monthItems.add(new SimpleItemData(String.valueOf(moth), moth));
        }
        mothAdapter.setAdapterDataList(monthItems);
    }

    private void notifyDayAdapter(int year, int month, int miniDay) {
        if (selectedTimeFields[YEAR] == miniTimeFields[YEAR] &&
                selectedTimeFields[MONTH] == miniTimeFields[MONTH]) {
            miniDay = miniTimeFields[DAY];
        }
        selectedTimeFields[DAY] = miniDay;

        List<SimpleItemData> dayItems = new ArrayList<>();
        int days = getDaysOfMonth(year, month);
        for (int day = miniDay; day <= days; day++) {
            dayItems.add(new SimpleItemData(String.valueOf(day), day));
        }
        dayAdapter.setAdapterDataList(dayItems);
    }

    private void notifyHourAdapter(int miniHour) {
        if (selectedTimeFields[YEAR] == miniTimeFields[YEAR] &&
                selectedTimeFields[MONTH] == miniTimeFields[MONTH] &&
                selectedTimeFields[DAY] == miniTimeFields[DAY]) {
            miniHour = miniTimeFields[HOUR];
        }
        selectedTimeFields[HOUR] = miniHour;

        List<SimpleItemData> hourItems = new ArrayList<>();
        for (int time = miniHour; time < 24; time++) {
            hourItems.add(new SimpleItemData(String.valueOf(time), time));
        }
        hourAdapter.setAdapterDataList(hourItems);
    }

    /**
     * 计算每个月的天数
     *
     * @param year  年份
     * @param month 月份
     * @return days 每个月的天数
     */
    private int getDaysOfMonth(int year, int month) {
        int[] arr = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int days = 0;

        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            arr[1] = 29; // 闰年2月29天
        }

        try {
            days = arr[month - 1];
        } catch (Exception e) {
            e.getStackTrace();
        }
        return days;
    }

    @Override
    public void onSelectChanged(RecycleWheelView wheelView, int position) {
        switch (wheelView.getId()) {
            case R.id.choose_time_year:
                selectedTimeFields[YEAR] = ((SimpleItemData) yearAdapter.getItem(position)).getId();

                notifyMonthAdapter(1);
                notifyDayAdapter(selectedTimeFields[YEAR], selectedTimeFields[MONTH], 1);
                notifyHourAdapter(0);
                break;
            case R.id.choose_time_month:
                selectedTimeFields[MONTH] = ((SimpleItemData) mothAdapter.getItem(position)).getId();

                notifyDayAdapter(selectedTimeFields[YEAR], selectedTimeFields[MONTH], 1);
                notifyHourAdapter(0);
                break;
            case R.id.choose_time_day:
                selectedTimeFields[DAY] = ((SimpleItemData) dayAdapter.getItem(position)).getId();

                notifyHourAdapter(0);
                break;
            case R.id.choose_time_hour:
                selectedTimeFields[HOUR] = ((SimpleItemData) hourAdapter.getItem(position)).getId();
                break;
            default:
                break;
        }
    }

    @Deprecated
    @Override
    public void show() {
//        super.show();
    }

    public void show(int where, String timeString) {
        this.where = where;
        super.show();
        setCurrentTime(timeString);
    }
}
