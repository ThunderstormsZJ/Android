package cn.spinsoft.wdq.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.spinsoft.wdq.R;

/**
 * Created by hushujun on 15/12/31.
 */
public class SearchBar extends FrameLayout {
    private static final String TAG = SearchBar.class.getSimpleName();
    private EditText editText;
    private OnCancelClickListenerCallBack mOnCancelClickListener;

    public void setOnCancelClickListener(OnCancelClickListenerCallBack mOnCancelClickListener) {
        this.mOnCancelClickListener = mOnCancelClickListener;
    }
    public interface OnCancelClickListenerCallBack{
        void OnCancelClickListener(View v);
    }

    public SearchBar(Context context) {
        this(context, null);
    }

    public SearchBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        float density = getResources().getDisplayMetrics().density;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setPadding(10,0,0,0);
        /*lp.bottomMargin = (int) (10 * density);
        lp.rightMargin = (int) (10 * density);
        lp.topMargin = (int) (10 * density);
        lp.leftMargin = (int) (10 * density);*/
        lp.gravity = Gravity.CENTER;
        addView(linearLayout,lp);

        editText = new EditText(context, attrs);
        editText.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.teacher_attest_search, 0, 0, 0);
        editText.setCompoundDrawablePadding((int) (5 * density));
        editText.setPadding((int) (8 * density), 10, 0, 10);
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,1);
        lp.gravity = Gravity.CENTER;
        editText.setTextColor(Color.parseColor("#999999"));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        editText.setBackgroundColor(Color.TRANSPARENT);
        editText.setBackgroundResource(R.drawable.radius_light_gray_bg);
        editText.setSingleLine();
        editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        linearLayout.addView(editText, lp);

        Button cancelBtn = new Button(context,attrs);
        cancelBtn.setText("取消");
        cancelBtn.setTextSize(12);
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnCancelClickListener.OnCancelClickListener(v);
            }
        });
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,6);
        linearLayout.addView(cancelBtn, lp);
    }

    public Editable getText() {
        return editText.getText();
    }

    public void setText(String text) {
        editText.setText(text);
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener listener) {
        editText.setOnEditorActionListener(listener);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        editText.addTextChangedListener(textWatcher);
    }
}
