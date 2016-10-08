package cn.spinsoft.wdq.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.bean.SimpleItemData;

/**
 * by default is single mode
 * Created by hushujun on 16/1/7.
 */
public class SimpleChoiceModeAdapter extends BaseRecycleAdapter<SimpleItemData> {
    private static final String TAG = SimpleChoiceModeAdapter.class.getSimpleName();
    private List<Integer> checkedPositions = new ArrayList<>();
    private List<String> checkedDanceType = new ArrayList<String>();
    private boolean mMultiChoice = false;

    public void setCheckedDanceType(List<String> checkedDanceType) {
        this.checkedDanceType = checkedDanceType;
        if(checkedDanceType!=null && checkedDanceType.size()>0){
            for (String danceName : checkedDanceType){
                for(int i=0;i<adapterDataList.size();i++){
                    if(danceName.equals(adapterDataList.get(i).getName())){
                        checkedPositions.add(i);
                    }
                }
            }
        }
    }

    public SimpleChoiceModeAdapter(List<SimpleItemData> adapterDataList) {
        this(adapterDataList, false);
    }

    public SimpleChoiceModeAdapter(List<SimpleItemData> adapterDataList, boolean multiChoice) {
        super(adapterDataList);
        this.mMultiChoice = multiChoice;
    }

    public List<SimpleItemData> getCheckedItems() {
        List<SimpleItemData> checkedItems = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            if (checkedPositions.contains(i)) {
                checkedItems.add(getItem(i));
            }
        }
        return checkedItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_simple_choice_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        SimpleItemData itemData = adapterDataList.get(position);
        holder.nameTx.setText(itemData.getName());
        if (checkedDanceType != null && checkedDanceType.size() > 0) {
            holder.choiceCb.setChecked(checkedDanceType.contains(itemData.getName()));
        }
        holder.choiceCb.setChecked(checkedPositions.contains(position));

        holder.choiceCb.setTag(itemData);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.choiceCb.setChecked(!holder.choiceCb.isChecked());
            }
        });
        holder.choiceCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mMultiChoice) {
                    if (isChecked) {
                        checkedPositions.add(holder.getLayoutPosition());
                    } else {
                        checkedPositions.remove(new Integer(holder.getLayoutPosition()));
                    }
                } else {
                    if (isChecked) {
                        if (checkedPositions.size() < 1) {
                            checkedPositions.add(holder.getLayoutPosition());
                        } else {
                            int previous = checkedPositions.set(0, holder.getLayoutPosition());

                            notifyItemChanged(previous);
                        }
                    } else {
                        checkedPositions.remove(new Integer(holder.getLayoutPosition()));
                    }
                }
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTx;
        private CheckBox choiceCb;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTx = (TextView) itemView.findViewById(R.id.simple_choice_name);
            choiceCb = (CheckBox) itemView.findViewById(R.id.simple_choice_cb);
        }
    }
}
