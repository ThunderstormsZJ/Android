package cn.spinsoft.wdq.base;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by hushujun on 15/12/3.
 */
public abstract class BaseRecycleAdapter<T> extends RecyclerView.Adapter {
    protected List<T> adapterDataList;
    protected RecyclerItemClickListener itemClickListener;
    private Activity activity;

    public BaseRecycleAdapter(List<T> adapterDataList) {
        this(adapterDataList, null);
    }

    public BaseRecycleAdapter(List<T> adapterDataList, RecyclerItemClickListener itemClickListener) {
        setAdapterDataList(adapterDataList);
        this.itemClickListener = itemClickListener;
    }
    public BaseRecycleAdapter(List<T> adapterDataList, RecyclerItemClickListener itemClickListener,Activity activity) {
        setAdapterDataList(adapterDataList);
        this.itemClickListener = itemClickListener;
        this.activity = activity;
    }

    public void setAdapterDataList(List<T> adapterDataList) {
        if (adapterDataList == null) {
            this.adapterDataList = new ArrayList<>();
        } else {
            this.adapterDataList = adapterDataList;
        }
        notifyDataSetChanged();
    }

    public void addAdapterDataList(List<T> adapterDataList) {
        if (adapterDataList == null) {
            return;
        }
        int start = this.adapterDataList.size();
        this.adapterDataList.addAll(adapterDataList);
        notifyItemRangeInserted(start, adapterDataList.size());
    }

    public void removeItem(int position){
        this.adapterDataList.remove(position);
        notifyItemRemoved(position);
    }

    public T getItem(int position) {
        if (position < getItemCount()) {
            return adapterDataList.get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return adapterDataList.size();
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
