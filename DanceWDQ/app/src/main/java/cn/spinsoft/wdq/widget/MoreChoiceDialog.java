package cn.spinsoft.wdq.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.spinsoft.wdq.R;

/**
 * Created by zhoujun on 2016-4-11.
 */
public class MoreChoiceDialog extends AlertDialog {
    private final static String TAG = MoreChoiceDialog.class.getSimpleName();

    private LinearLayout mContainerLL;
    private float density;
    private OnFunctionItemClickListener mOnFunctionItemClickListener;

    public interface OnFunctionClickListener {
        void OnFunctionClick(View v);
    }

    public interface OnFunctionItemClickListener {
        void OnFunctionItemClick(View v);
    }

    public MoreChoiceDialog(Context context) {
        this(context, R.style.DialogWithTransparentBackground);
    }

    protected MoreChoiceDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dia_morefunction);

        mContainerLL = (LinearLayout) findViewById(R.id.dia_more_container);
        TextView cancelTx = (TextView) findViewById(R.id.dia_more_cancel);
        cancelTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        DisplayMetrics metrics = new DisplayMetrics();
        Window window = getWindow();
        window.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        density = metrics.density;
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }


    public void addFunction(String functionName, final OnFunctionClickListener mOnFunctionListener) {
        if (!TextUtils.isEmpty(functionName)) {
            Button button = new Button(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (40 * density));
            button.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
            button.setTextSize(18);
            button.setText(functionName);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnFunctionListener.OnFunctionClick(v);
                    dismiss();
                }
            });
            mContainerLL.addView(button, lp);
        }
    }

    public void addFunctions(String[] functionName, int[] ids) {
        if (functionName != null && functionName.length > 0) {
            for (int i = 0; i < functionName.length; i++) {
                Button button = new Button(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (40 * density));
                button.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
                button.setTextSize(18);
                button.setText(functionName[i]);
                button.setId(ids[i]);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnFunctionItemClickListener != null) {
                            mOnFunctionItemClickListener.OnFunctionItemClick(v);
                        }
                        dismiss();
                    }
                });
                mContainerLL.addView(button, lp);
                TextView lineTx = new TextView(getContext());
                lineTx.setBackgroundColor(getContext().getResources().getColor(R.color.gray));
                lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
                mContainerLL.addView(lineTx, lp);
            }
        }
    }

    public void setOnFunctionItemClickListener(OnFunctionItemClickListener onFunctionItemClickListener) {
        mOnFunctionItemClickListener = onFunctionItemClickListener;
    }
}
