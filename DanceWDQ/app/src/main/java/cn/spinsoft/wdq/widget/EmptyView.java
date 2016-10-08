package cn.spinsoft.wdq.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.spinsoft.wdq.R;

/**
 * Created by hushujun on 16/1/27.
 */
public class EmptyView extends LinearLayout {
    private static final String TAG = EmptyView.class.getSimpleName();

    private ImageView emptyImg;
    private TextView emptyReasonTx, emptyDescTx;

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.ly_empty_view, this);
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        emptyImg = (ImageView) findViewById(R.id.empty_img);
        emptyReasonTx = (TextView) findViewById(R.id.empty_reason);
        emptyDescTx = (TextView) findViewById(R.id.empty_desc);
    }

    public void setEmptyImg(@DrawableRes int drawableRes) {
        emptyImg.setImageResource(drawableRes);
    }

    public void setEmptyReason(String reason) {
        emptyReasonTx.setText(reason);
    }

    public void setEmptyDesc(String desc) {
        emptyDescTx.setText(desc);
    }
}
