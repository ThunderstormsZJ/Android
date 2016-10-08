package cn.spinsoft.wdq.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;

import cn.spinsoft.wdq.R;

/**
 * Created by hushujun on 16/1/26.
 */
public class VerifyButton extends Button {
    private static final String TAG = VerifyButton.class.getSimpleName();

    public VerifyButton(Context context) {
        this(context, null);
    }

    public VerifyButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerifyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VerifyButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
       /* setBackgroundResource(R.mipmap.register_verify_btn_bg);
        setBackgroundColor(getResources().getColor(R.color.white));*/
//        setTextColor(getResources().getColorStateList(R.color.verify_btn_textcolor_sel));
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        setGravity(Gravity.CENTER);
        setText("获取验证码");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void start() {
        countDownTimer.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        countDownTimer.cancel();
        super.onDetachedFromWindow();
    }

    CountDownTimer countDownTimer = new CountDownTimer(61 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            setText(String.format("重新发送(%d)", millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            setText("获取验证码");
            setEnabled(true);
        }
    };
}
