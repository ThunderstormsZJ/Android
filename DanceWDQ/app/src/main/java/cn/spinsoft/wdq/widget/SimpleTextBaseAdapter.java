package cn.spinsoft.wdq.widget;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.bean.SimpleItemData;

/**
 * Created by hushujun on 15/11/13.
 */
public class SimpleTextBaseAdapter extends BaseAdapter {
    private static final String TAG = SimpleTextBaseAdapter.class.getSimpleName();
    private List<SimpleItemData> spinnerItems;
    private OnItemSelectedListener mListener;

    public SimpleTextBaseAdapter(List<SimpleItemData> spinnerItems) {
        if (spinnerItems != null) {
            this.spinnerItems = spinnerItems;
        } else {
            this.spinnerItems = new ArrayList<>();
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(ListAdapter adapter, android.widget.TextView view, int position, long id);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public int getCount() {
        return spinnerItems.size();
    }

    @Override
    public SimpleItemData getItem(int position) {
        if (position < 0 || position >= getCount()) {
            return null;
        } else {
            return spinnerItems.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.simple_text_item_normal, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.simple_text_item_text);
            convertView.setTag(R.id.tag_holder, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.tag_holder);
        }
        holder.textView.setText(spinnerItems.get(position).getName());
        holder.textView.setTag(spinnerItems.get(position));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "点击");
                mListener.onItemSelected(SimpleTextBaseAdapter.this, (TextView) v, position, v.getId());
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView textView;
    }
}
