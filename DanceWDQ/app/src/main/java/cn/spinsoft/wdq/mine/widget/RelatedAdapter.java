package cn.spinsoft.wdq.mine.widget;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.mine.biz.RelatedBean;
import cn.spinsoft.wdq.utils.StringUtils;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by zhoujun on 16/1/12.
 */
public class RelatedAdapter extends BaseRecycleAdapter<RelatedBean> {
    private static final String TAG = RelatedAdapter.class.getSimpleName();
    private final int INVITE_TYPE = 1 << 1;
    private final int DEFAULT_TYPE = 1 << 2;

    public RelatedAdapter(List<RelatedBean> adapterDataList, RecyclerItemClickListener itemClickListener) {
        super(adapterDataList, itemClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        RelatedBean relatedBean = adapterDataList.get(position);
        if (relatedBean.getType() == 22 || relatedBean.getType() == 23 || relatedBean.getType() == 24) {
            return INVITE_TYPE;
        } else {
            return DEFAULT_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == INVITE_TYPE) {
            return new InviteHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_mine_related_invite_item, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_mine_related_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        RelatedBean relatedBean = adapterDataList.get(position);
        if (viewHolder.getItemViewType() == DEFAULT_TYPE) {
            final ViewHolder holder = (ViewHolder) viewHolder;
            holder.dateTx.setText(relatedBean.getCreateTime());
            holder.photoSdv.setImageURI(Uri.parse(relatedBean.getAnotherPhoto()));
            holder.anotherTx.setText(relatedBean.getAnotherName());

            String currName = relatedBean.getCurrName();
            String title;
            if (!TextUtils.isEmpty(currName)) {
                title = currName + ":" + relatedBean.getRelatedTitle();
                holder.titleTx.setText(StringUtils.packageHighLightString(title, 0, currName.length(), "#3399ff"));
            } else {
                holder.titleTx.setText(relatedBean.getRelatedTitle());
            }

            if (!TextUtils.isEmpty(relatedBean.getImageUrl())) {
                holder.posterSdv.setVisibility(View.VISIBLE);
                holder.posterSdv.setImageURI(Uri.parse(relatedBean.getImageUrl()));
            } else {
                holder.posterSdv.setVisibility(View.GONE);
            }
            int type = relatedBean.getType();
            if (type == 1 || type == 5 || type == 8 || type == 12 || type == 15) {
                holder.thumbTx.setVisibility(View.VISIBLE);
                holder.commentTx.setVisibility(View.GONE);
            } else {
                holder.thumbTx.setVisibility(View.GONE);
                holder.commentTx.setVisibility(View.VISIBLE);
                holder.commentTx.setText(relatedBean.getComment());
            }

            if (itemClickListener != null) {
                holder.photoSdv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(RelatedAdapter.this, v, holder.getLayoutPosition());
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(RelatedAdapter.this, v, holder.getLayoutPosition());
                    }
                });

//            holder.replyTx.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    itemClickListener.onItemClicked(RelatedAdapter.this, v, holder.getLayoutPosition());
//                }
//            });
            }
        }else {
            final InviteHolder inviteHolder = (InviteHolder) viewHolder;
            inviteHolder.photoImg.setImageURI(Uri.parse(relatedBean.getAnotherPhoto()));
            inviteHolder.anotherTx.setText(relatedBean.getAnotherName());
            inviteHolder.dateTx.setText(relatedBean.getCreateTime());
            if(relatedBean.getType()==23){
                inviteHolder.confirmImg.setSelected(true);
            }
            inviteHolder.photoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(RelatedAdapter.this, v, inviteHolder.getLayoutPosition());
                }
            });
            inviteHolder.confirmImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!inviteHolder.confirmImg.isSelected()){
                        inviteHolder.confirmImg.setSelected(true);
                        itemClickListener.onItemClicked(RelatedAdapter.this, v, inviteHolder.getLayoutPosition());
                    }
                }
            });
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView photoSdv, posterSdv;
        private TextView anotherTx, thumbTx, dateTx/*, replyTx*/, commentTx, titleTx;

        public ViewHolder(View itemView) {
            super(itemView);
            photoSdv = (SimpleDraweeView) itemView.findViewById(R.id.mine_related_item_photo);
            posterSdv = (SimpleDraweeView) itemView.findViewById(R.id.mine_related_item_img);
            anotherTx = (TextView) itemView.findViewById(R.id.mine_related_item_another);
            thumbTx = (TextView) itemView.findViewById(R.id.mine_related_item_thumb);
            dateTx = (TextView) itemView.findViewById(R.id.mine_related_item_date);
//            replyTx = (TextView) itemView.findViewById(R.id.mine_related_item_reply);
            commentTx = (TextView) itemView.findViewById(R.id.mine_related_item_comment);
            titleTx = (TextView) itemView.findViewById(R.id.mine_related_item_title);
        }
    }

    class InviteHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView photoImg;
        private TextView anotherTx, dateTx;
        private ImageView confirmImg;

        public InviteHolder(View itemView) {
            super(itemView);
            photoImg = (SimpleDraweeView) itemView.findViewById(R.id.mine_related_item_photo);
            anotherTx = (TextView) itemView.findViewById(R.id.mine_related_item_another);
            dateTx = (TextView) itemView.findViewById(R.id.mine_related_item_date);
            confirmImg = (ImageView) itemView.findViewById(R.id.mine_related_item_confirm);
        }
    }

}
