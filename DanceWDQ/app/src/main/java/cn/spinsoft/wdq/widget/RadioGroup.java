package cn.spinsoft.wdq.widget;

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;


/**
 * Created by hushujun on 15/12/28.
 */
public class RadioGroup extends LinearLayout implements View.OnClickListener {
    private static final String TAG = RadioGroup.class.getSimpleName();

    private OnCheckedChangeListener checkedChangeListener;
    private int checkedChildId = -1;

    public RadioGroup(Context context) {
        super(context);
    }

    public RadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadioGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener checkedChangeListener) {
        this.checkedChangeListener = checkedChangeListener;
    }

    @Override
    protected void onFinishInflate() {
        Log.d(TAG, "onFinishInflate");
        super.onFinishInflate();
        int count = getChildCount();
        if (count > 0) {
            toggleStatus(getChildAt(0), true);
            for (int i = 0; i < count; i++) {
                View view = getChildAt(i);
                if (view.isFocusable() || view.isClickable()) {
                    view.setOnClickListener(this);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (v != view) {
                toggleStatus(view, false);
            } else {
                toggleStatus(view, true);
                if (checkedChangeListener != null) {
                    checkedChangeListener.onCheckedChanged(this, v.getId());
                }
            }
        }
    }

    public int getCheckedChildId() {
        return checkedChildId;
    }

    private void toggleStatus(View view, boolean checked) {
        if (view != null) {
            checkedChildId = view.getId();
            if (view instanceof Checkable) {
                ((Checkable) view).setChecked(checked);
            } else if (view.isClickable()) {
                view.setSelected(checked);
            } else if (view.isFocusable()) {
                view.setSelected(checked);
            }
        }
    }

    /**
     * <p>Interface definition for a callback to be invoked when the checked
     * radio button changed in this group.</p>
     */
    public interface OnCheckedChangeListener {
        /**
         * <p>Called when the checked radio button has changed. When the
         * selection is cleared, checkedId is -1.</p>
         *
         * @param group     the group in which the checked radio button has changed
         * @param checkedId the unique identifier of the newly checked radio button
         */
        void onCheckedChanged(RadioGroup group, @IdRes int checkedId);
    }

}
