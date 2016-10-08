package cn.spinsoft.wdq.mine.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by hushujun on 15/12/4.
 */
public class ViewSwitcher extends android.widget.ViewSwitcher implements View.OnClickListener {
    private static final String TAG = ViewSwitcher.class.getSimpleName();

    private TextView mDisPlayTx;
    private LinearLayout mInputLl;
    private EditText mInputEt;
    private Button mConfirmBtn;
    private OnInputConfirmListener confirmListener;

    public interface OnInputConfirmListener {
        void inputConfirm(ViewSwitcher switcher);
    }

    public ViewSwitcher(Context context) {
        this(context, null);
    }

    public ViewSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDisPlayTx = new TextView(context, attrs);
//        mDisPlayTx.setTextColor(Color.parseColor("#999999"));
//        mDisPlayTx.setTextSize(TypedValue.COMPLEX_UNIT_PX, 28);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
//        mDisPlayTx.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.school_more, 0);
//        mDisPlayTx.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        addView(mDisPlayTx, 0, lp);
        mDisPlayTx.setOnClickListener(this);


        mInputLl = new LinearLayout(context);
        lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mInputLl, 1, lp);

        mInputEt = new EditText(context);
        mInputEt.setTextColor(Color.parseColor("#999999"));
        mInputEt.setTextSize(TypedValue.COMPLEX_UNIT_PX, 28);
        LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        lllp.weight = 1;
        mInputEt.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        mInputLl.addView(mInputEt, lllp);

        mConfirmBtn = new Button(context);
        mConfirmBtn.setTextColor(Color.parseColor("#999999"));
        mConfirmBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, 28);
        mConfirmBtn.setText("确定");
        lllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mConfirmBtn.setGravity(Gravity.CENTER);
        mInputLl.addView(mConfirmBtn, lllp);
        mConfirmBtn.setOnClickListener(this);
    }

    public void setText(CharSequence text) {
        mDisPlayTx.setText(text);
    }

    public CharSequence getText() {
        return mDisPlayTx.getText();
    }

    public void setOnIputConfirmListener(OnInputConfirmListener listener) {
        this.confirmListener = listener;
    }

    /**
     * @param inputType {@link android.text.InputType}
     */
    public void setInputType(int inputType) {
        mInputEt.setInputType(inputType);
    }

    @Override
    public void onClick(View v) {
        if (v == mDisPlayTx) {
            mInputEt.setText(mDisPlayTx.getText());
            showNext();
        } else if (v == mConfirmBtn) {
            if (TextUtils.isEmpty(mInputEt.getText())) {
                Toast.makeText(getContext(), "输入的内容不能为空", Toast.LENGTH_SHORT).show();
            } else {
                mDisPlayTx.setText(mInputEt.getText());
                showPrevious();
                if (confirmListener != null) {
                    confirmListener.inputConfirm(this);
                }
            }
        }
    }
}
