package cn.spinsoft.wdq.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * Created by zhoujun on 2016-4-14.
 */
public class WdqStyleEdit extends FrameLayout {
    private final static String TAG = WdqStyleEdit.class.getSimpleName();

    private EditText editText;

    public WdqStyleEdit(Context context) {
        super(context);
    }

    public WdqStyleEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WdqStyleEdit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        float density = getResources().getDisplayMetrics().density;
    }
}
