package cn.spinsoft.wdq.mine.widget;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.mine.biz.PrivateMsgInnerItem;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by hushujun on 16/1/18.
 */
public class ConversationAdapter extends BaseRecycleAdapter<PrivateMsgInnerItem> {
    private static final String TAG = ConversationAdapter.class.getSimpleName();

    public ConversationAdapter(List<PrivateMsgInnerItem> adapterDataList, RecyclerItemClickListener itemClickListener) {
        super(adapterDataList, itemClickListener);
    }

    public void insertMessage(PrivateMsgInnerItem innerItem) {
        if (innerItem != null) {
            adapterDataList.add(innerItem);
            notifyItemInserted(adapterDataList.size());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_conversation_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        PrivateMsgInnerItem innerItem = adapterDataList.get(position);
        holder.timeTx.setText(innerItem.getCreateTime());
        holder.contentTx.setText(innerItem.getContent());
        holder.contentRTx.setText(innerItem.getContent());
        if (innerItem.isSendBySelf()) {
            holder.photoLeftSdv.setVisibility(View.INVISIBLE);
            holder.contentTx.setVisibility(View.INVISIBLE);
            holder.photoRightSdv.setVisibility(View.VISIBLE);
            holder.contentRTx.setVisibility(View.VISIBLE);
            holder.photoRightSdv.setImageURI(Uri.parse(innerItem.getHeadUrl()));
        } else {
            holder.photoRightSdv.setVisibility(View.INVISIBLE);
            holder.contentRTx.setVisibility(View.INVISIBLE);
            holder.photoLeftSdv.setVisibility(View.VISIBLE);
            holder.contentTx.setVisibility(View.VISIBLE);
            holder.photoLeftSdv.setImageURI(Uri.parse(innerItem.getHeadUrl()));
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView timeTx, contentTx,contentRTx;
        private SimpleDraweeView photoLeftSdv, photoRightSdv;

        public ViewHolder(View itemView) {
            super(itemView);
            timeTx = (TextView) itemView.findViewById(R.id.conversation_item_time);
            contentTx = (TextView) itemView.findViewById(R.id.conversation_item_content);
            contentRTx = (TextView) itemView.findViewById(R.id.conversation_item_content_right);
            photoLeftSdv = (SimpleDraweeView) itemView.findViewById(R.id.conversation_item_photo_left);
            photoRightSdv = (SimpleDraweeView) itemView.findViewById(R.id.conversation_item_photo_right);
        }
    }
}
