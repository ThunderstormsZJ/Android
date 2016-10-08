package cn.spinsoft.wdq.mine.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.widget.SimpleTextRecyclerAdapter;
import cn.spinsoft.wdq.widget.wheel.RecycleWheelView;

/**
 * Created by zhoujun on 15/12/8.
 */
public class SingleChoiceDialog extends Dialog implements RecycleWheelView.OnSelectItemListener, View.OnClickListener {
    private static final String TAG = SingleChoiceDialog.class.getSimpleName();

    private SimpleTextRecyclerAdapter<SimpleItemData> recyclerAdapter;
    private OnInfoChooseListener chooseListener;
    private List<SimpleItemData> itemDataList;

    private SimpleItemData mSelectedItem;
    private RecycleWheelView mChooseWheel;
    private int where = 0;

    private boolean isCreated = false;

    private String preInfoData;

    public void setPreInfoData(String preInfoData) {
        this.preInfoData = preInfoData;
    }

    public void smoothScrollCurr(String preInfoData) {
        if (!TextUtils.isEmpty(preInfoData) && preInfoData != null) {
            for (int i = 0; i < itemDataList.size(); i++) {
                if (preInfoData.equals(itemDataList.get(i).getName())) {
                    mChooseWheel.smoothScrollToPosition(i);
                    mSelectedItem = itemDataList.get(i);
                    break;
                }
            }
        }
    }

    public interface OnInfoChooseListener {
        void onInfoChoosed(int where, SimpleItemData itemData);
    }

    public SingleChoiceDialog(Context context, OnInfoChooseListener listener) {
        super(context, R.style.DialogWithTransparentBackground);
        this.chooseListener = listener;
        recyclerAdapter = new SimpleTextRecyclerAdapter<>(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dia_single_choice);
        TextView mConfirmBtn = (TextView) findViewById(R.id.single_choice_confirm);
        TextView mCancelBtn = (TextView) findViewById(R.id.single_choice_cancel);

        mChooseWheel = (RecycleWheelView) findViewById(R.id.single_choice_wheel);
        mChooseWheel.setOnSelectListener(this);
        mConfirmBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
        mChooseWheel.setAdapter(recyclerAdapter);
        mChooseWheel.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                LogUtil.w(TAG, "onGlobalLayout");
                smoothScrollCurr(preInfoData);
                isCreated = true;
//                mChooseWheel.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    @Override
    public void onSelectChanged(RecycleWheelView wheelView, int position) {
        mSelectedItem = recyclerAdapter.getItem(position);
    }

    public void show(int where, List<SimpleItemData> itemDataList) {
        this.where = where;
        this.itemDataList = itemDataList;
        if (itemDataList != null && !itemDataList.isEmpty()) {
            mSelectedItem = itemDataList.get(0);
        } else {
            mSelectedItem = null;
        }
        recyclerAdapter.setAdapterDataList(itemDataList);

        if (isCreated) {
            smoothScrollCurr(preInfoData);
        }
        super.show();
    }

    /**
     * use {@link #show(int, List<SimpleItemData> )} instead
     */
    @Override
    public void show() {
        super.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.single_choice_confirm:
                if (chooseListener != null) {
                    if (mSelectedItem == null) {
                        mSelectedItem = recyclerAdapter.getItem(0);
                    }
                    chooseListener.onInfoChoosed(where, mSelectedItem);
                    dismiss();
                }
                break;
            case R.id.single_choice_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }
}
