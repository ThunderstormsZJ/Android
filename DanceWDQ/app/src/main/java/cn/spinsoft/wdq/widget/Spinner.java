package cn.spinsoft.wdq.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.spinsoft.wdq.R;

/**
 * Created by hushujun on 15/11/13.
 */
public class Spinner extends RelativeLayout implements AdapterView.OnItemClickListener {
    private static final String TAG = Spinner.class.getSimpleName();

    public TextView mTextView;
    private ListAdapter mListAdapter;
    private ItemDialog mDialog;
    private OnItemSelectedListener mListener;
    private int layoutRule;

    public interface OnItemSelectedListener {
        void onItemSelected(ListAdapter adapter, android.widget.TextView view, int position, long id);
    }

    public Spinner(Context context) {
        this(context, null);
    }

    public Spinner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Spinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTextView = new TextView(context, attrs, defStyleAttr);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutRule = CENTER_IN_PARENT;
        lp.addRule(layoutRule);
        addView(mTextView, lp);
    }

    public void setAdapter(ListAdapter adapter) {
        mListAdapter = adapter;
    }

    public void setCompoundDrawablesWithIntrinsicBounds(@DrawableRes int left, @DrawableRes int top,
                                                        @DrawableRes int right, @DrawableRes int bottom) {
        mTextView.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    @Override
    public void setGravity(int gravity) {
        LayoutParams lp = (LayoutParams) mTextView.getLayoutParams();
        int rule = layoutRule;
        switch (gravity) {
            case Gravity.LEFT:
                rule = ALIGN_PARENT_LEFT;
                break;
            case Gravity.RIGHT:
                rule = ALIGN_PARENT_RIGHT;
                break;
            case Gravity.TOP:
                rule = ALIGN_PARENT_TOP;
                break;
            case Gravity.BOTTOM:
                rule = ALIGN_PARENT_BOTTOM;
                break;
            case Gravity.CENTER_VERTICAL:
                rule = CENTER_VERTICAL;
                break;
            case Gravity.CENTER_HORIZONTAL:
                rule = CENTER_HORIZONTAL;
                break;
            case Gravity.CENTER:
            default:
                rule = CENTER_IN_PARENT;
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            lp.removeRule(layoutRule);
        } else {

        }
        lp.addRule(rule);
        layoutRule = rule;
        mTextView.setLayoutParams(lp);
    }

    public ListAdapter getAdapter() {
        return mListAdapter;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            show();
        }
        return super.onTouchEvent(event);
    }

    public void show() {
        if (mDialog == null) {
            if (mListAdapter.getCount() > 6) {
                mDialog = new ItemDialog(getContext(), 480,this);
            } else {
                mDialog = new ItemDialog(getContext(), 0,this);
            }
            mDialog.setAdapter(mListAdapter);
        }
        this.setSelected(true);
        mDialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mTextView.setText(((TextView) view).getText());
        mDialog.dismiss();
        if (mListener != null) {
            mListener.onItemSelected(mListAdapter, (TextView) view, position, id);
        }
    }

    class ItemDialog extends AlertDialog {
        private ListView listView;
        private int maxHeight = 0;
        private View spinnerView;

        public ItemDialog(Context context, int maxHeight,View v) {
            super(context, R.style.PullDownDialog);
            this.maxHeight = maxHeight;
            this.spinnerView = v;
        }

        public void setAdapter(ListAdapter adapter) {
            if (listView != null) {
                listView.setAdapter(adapter);
            }
        }

        @Override
        public void dismiss() {
            super.dismiss();
            spinnerView.setSelected(false);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.ly_spinner_container);
            listView = (ListView) findViewById(R.id.ly_spinner_root);
            listView.setOnItemClickListener(Spinner.this);
            if (mListAdapter != null) {
                listView.setAdapter(mListAdapter);
            }

            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.width = getWidth();
            lp.height = maxHeight == 0 ? WindowManager.LayoutParams.WRAP_CONTENT : maxHeight;
            int[] location = new int[2];
            getLocationInWindow(location);
            lp.x = location[0];
            lp.y = location[1] + getStatusBarHeight()/4;
            lp.gravity = Gravity.TOP | Gravity.LEFT;
            getWindow().setAttributes(lp);

        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
