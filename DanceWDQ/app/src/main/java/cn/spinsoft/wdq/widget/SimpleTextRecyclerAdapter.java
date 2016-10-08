package cn.spinsoft.wdq.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.utils.Constants;

/**
 * Created by zhoujun on 15/11/17.
 */
public class SimpleTextRecyclerAdapter<T> extends BaseRecycleAdapter<T> {
    private static final String TAG = SimpleTextRecyclerAdapter.class.getSimpleName();

    private String adapterType = Constants.Strings.NORMAL_ADAPTER;

    public void setAdapterType(String adapterType) {
        this.adapterType = adapterType;
    }

    public SimpleTextRecyclerAdapter(List<T> adapterDataList) {
        super(adapterDataList);
    }

    public SimpleTextRecyclerAdapter(List<T> adapterDataList, RecyclerItemClickListener itemClickListener) {
        super(adapterDataList, itemClickListener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (adapterType == Constants.Strings.SEARCH_ADAPTER) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_text_item_recycler_search, parent, false));
        }
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_text_item_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        T t = adapterDataList.get(position);
        if (t instanceof String) {
            holder.textView.setText((String) t);
        } else if (t instanceof SimpleItemData) {
            holder.textView.setText(((SimpleItemData) t).getName());
        }
        if (itemClickListener != null) {
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(SimpleTextRecyclerAdapter.this, v, holder.getLayoutPosition());
                }
            });
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }
}
