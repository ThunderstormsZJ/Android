package cn.spinsoft.wdq.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.InputStream;

import cn.spinsoft.wdq.utils.LogUtil;

/**
 * Created by hushujun on 16/1/19.
 */
public class EquallyWidthLabel extends FrameLayout {
    private static final String TAG = EquallyWidthLabel.class.getSimpleName();
    private TextView mTextView;
    private int imgResId;
    private int windowWidth, windowHight;

    public EquallyWidthLabel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EquallyWidthLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
//        LogUtil.w("EquallyWidthLabel", metrics.density + "");

        setWillNotDraw(false);
        imgResId = attrs.getAttributeResourceValue(null, "leftSrc", 0);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EquallyWidthLabel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        if (imgResId > 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgResId);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (windowHight > 0 && windowWidth > 0) {
                canvas.drawBitmap(bitmap, windowWidth / 3 - width / 2 - 5, windowHight / 2 - height / 2 + 4, paint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        windowWidth = getMeasuredWidth();
        windowHight = getMeasuredHeight();
    }

    private void init(Context context, AttributeSet attrs) {
        mTextView = new TextView(context, attrs);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        addView(mTextView, lp);
    }

    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    public void setCompoundDrawablesWithIntrinsicBounds(@DrawableRes int start, @DrawableRes int top,
                                                        @DrawableRes int end, @DrawableRes int bottom) {
        mTextView.setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom);
    }
}
