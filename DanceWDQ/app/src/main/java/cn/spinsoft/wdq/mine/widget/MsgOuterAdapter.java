//package cn.spinsoft.wdq.mine.widget;
//
//import android.net.Uri;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.facebook.drawee.view.SimpleDraweeView;
//
//import java.util.List;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.base.BaseRecycleAdapter;
//import cn.spinsoft.wdq.mine.biz.PrivateMsgOuterItem;
//import cn.spinsoft.wdq.mine.component.PrivateMsgListActivity;
//import cn.spinsoft.wdq.utils.LogUtil;
//import cn.spinsoft.wdq.widget.RecyclerItemClickListener;
//
///**
// * Created by hushujun on 16/1/18.
// */
//public class MsgOuterAdapter extends BaseRecycleAdapter<PrivateMsgOuterItem> {
//    private static final String TAG = MsgOuterAdapter.class.getSimpleName();
//    private PrivateMsgListActivity privateMsgListActivity;
//
//    public MsgOuterAdapter(List<PrivateMsgOuterItem> adapterDataList, RecyclerItemClickListener itemClickListener) {
//        super(adapterDataList, itemClickListener);
//    }
//
//    public void notifyUpdateItemStatus(int position) {
//        if (position >= 0 && position < getItemCount()) {
//            adapterDataList.get(position).setUnReadCount(0);
//            notifyItemChanged(position);
//        }
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        privateMsgListActivity = (PrivateMsgListActivity) parent.getContext();
//        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_msg_outer_item, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
//        final ViewHolder holder = (ViewHolder) viewHolder;
//        PrivateMsgOuterItem outerItem = adapterDataList.get(position);
//        holder.photoSdv.setImageURI(Uri.parse(outerItem.getHeadUrl()));
//        holder.nameTx.setText(outerItem.getNickName());
//        holder.timeTx.setText(outerItem.getTime());
//        holder.contentTx.setText(outerItem.getContent());
//        if (outerItem.getUnReadCount() > 0) {
//            holder.countTx.setVisibility(View.VISIBLE);
//            holder.countTx.setText(String.valueOf(outerItem.getUnReadCount()));
//            holder.itemView.setBackgroundResource(R.drawable.msg_outer_bg_blue);
//            privateMsgListActivity.setTitleContent(outerItem.getUnReadCount());
//        } else {
//            holder.countTx.setVisibility(View.INVISIBLE);
//            holder.itemView.setBackgroundResource(R.drawable.msg_outer_bg_gray);
//        }
//        if (itemClickListener != null) {
//            holder.photoSdv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    itemClickListener.onItemClicked(MsgOuterAdapter.this, v, holder.getLayoutPosition());
//                }
//            });
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    itemClickListener.onItemClicked(MsgOuterAdapter.this, v, holder.getLayoutPosition());
//                }
//            });
//        }
//    }
//
//    class ViewHolder extends RecyclerView.ViewHolder {
//        private SimpleDraweeView photoSdv;
//        private TextView nameTx, timeTx, countTx, contentTx;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            photoSdv = (SimpleDraweeView) itemView.findViewById(R.id.msg_outer_item_photo);
//            nameTx = (TextView) itemView.findViewById(R.id.msg_outer_item_name);
//            timeTx = (TextView) itemView.findViewById(R.id.msg_outer_item_time);
//            countTx = (TextView) itemView.findViewById(R.id.msg_outer_item_count);
//            contentTx = (TextView) itemView.findViewById(R.id.msg_outer_item_content);
//        }
//    }
//}
