package cn.spinsoft.wdq.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by hushujun on 15/11/3.
 */
public interface RecyclerItemClickListener {
    /**
     * when item-self clicked callback
     *
     * @param adapter
     * @param view     the view which is clicked
     * @param position the view's index in whole adapter
     */
    void onItemClicked(RecyclerView.Adapter adapter, View view, int position);
}
