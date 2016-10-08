package cn.spinsoft.wdq.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by hushujun on 15/12/24.
 */
public class RefreshLayout extends SwipeRefreshLayout {
    private static final String TAG = RefreshLayout.class.getSimpleName();

    public interface OnRefreshListener extends SwipeRefreshLayout.OnRefreshListener {
        void onLoad();
    }

    private final int mTouchSlop;
    private OnRefreshListener onRefreshListener;

    private float firstTouchY, lastTouchY;
    private boolean isLoading = false;

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstTouchY = ev.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                lastTouchY = ev.getRawY();
                if (canLoadMore()) {
                    loadData();
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean canLoadMore() {
        return isBottom() && !isLoading && isPullingUp();
    }

    private boolean isBottom() {
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            if (child instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) child;
                RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
                int lastVisiblePosition = getLastVisibleItemPosition(lm);
                int count = recyclerView.getAdapter().getItemCount();
                if (0 == count) {
                    // 没有item的时候也可以上拉加载
                    return true;
                } else if (lastVisiblePosition == (count - 1)) {
                    // 滑到底部了
                    if (lm.findViewByPosition(count - 1).getBottom() <= getMeasuredHeight()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isPullingUp() {
        return (firstTouchY - lastTouchY) >= mTouchSlop;
    }

    private void loadData() {
        if (onRefreshListener != null) {
            setLoading(true);
        }
    }

    @Override
    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        super.setOnRefreshListener(listener);
        if (listener instanceof OnRefreshListener) {
            onRefreshListener = (OnRefreshListener) listener;
        }
    }

    public void setLoading(boolean loading) {
        if (getChildCount() <= 0) {
            return;
        }
        isLoading = loading;
        if (loading) {
            if (isRefreshing()) {
                setRefreshing(false);
            }
            onRefreshListener.onLoad();
        } else {
            firstTouchY = 0;
            lastTouchY = 0;
        }
    }

    /**
     * 获取顶部可见项的位置
     *
     * @return
     */
    private int getFirstVisibleItemPosition(RecyclerView.LayoutManager lm) {
        int firstVisibleItemPosition = 0;
        if (lm instanceof LinearLayoutManager) {
            firstVisibleItemPosition = ((LinearLayoutManager) lm).findFirstVisibleItemPosition();
        } else if (lm instanceof GridLayoutManager) {
            firstVisibleItemPosition = ((GridLayoutManager) lm).findFirstVisibleItemPosition();
        } else if (lm instanceof StaggeredGridLayoutManager) {
            int positions[] = new int[1];
            ((StaggeredGridLayoutManager) lm).findFirstVisibleItemPositions(positions);
            firstVisibleItemPosition = positions[0];
        }
        return firstVisibleItemPosition;
    }

    /**
     * 获取底部可见项的位置
     *
     * @return
     */
    private int getLastVisibleItemPosition(RecyclerView.LayoutManager lm) {
        int lastVisibleItemPosition = 0;
        if (lm instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) lm).findLastVisibleItemPosition();
        } else if (lm instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) lm).findLastVisibleItemPosition();
        } else if (lm instanceof StaggeredGridLayoutManager) {
            int positions[] = new int[1];
            ((StaggeredGridLayoutManager) lm).findFirstVisibleItemPositions(positions);
            ((StaggeredGridLayoutManager) lm).findLastVisibleItemPositions(positions);
            lastVisibleItemPosition = positions[0];
        }
        return lastVisibleItemPosition;
    }
}
