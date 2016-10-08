package cn.spinsoft.wdq.video.widget;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.bean.CommentItem;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by hushujun on 15/11/9.
 */
public class CommentsAdapter extends BaseRecycleAdapter<CommentItem> {
    private static final String TAG = CommentsAdapter.class.getSimpleName();

    public CommentsAdapter(List<CommentItem> adapterDataList, RecyclerItemClickListener itemClickListener) {
        super(adapterDataList, itemClickListener);
    }

    public void addCommentIntoList(CommentItem comment) {
        if (comment == null) {
            return;
        }
        if (adapterDataList == null) {
            adapterDataList = new ArrayList<>();
        }
        adapterDataList.add(0, comment);
        notifyItemInserted(0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_comment_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        CommentItem comment = adapterDataList.get(position);
        holder.photoCimg.setImageURI(Uri.parse(comment.getPhotoUrl()));
        holder.nickNameTx.setText(comment.getNickName());
        holder.contentTx.setText(comment.getContent());
        holder.timeTx.setText(comment.getTime());
        if (itemClickListener != null) {
            holder.photoCimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(CommentsAdapter.this, v, holder.getLayoutPosition());
                }
            });
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView photoCimg;
        private TextView nickNameTx, contentTx, timeTx, replyTx;

        public ViewHolder(View itemView) {
            super(itemView);
            photoCimg = (SimpleDraweeView) itemView.findViewById(R.id.comment_list_item_photo);
            nickNameTx = (TextView) itemView.findViewById(R.id.comment_list_item_name);
            contentTx = (TextView) itemView.findViewById(R.id.comment_list_item_content);
            timeTx = (TextView) itemView.findViewById(R.id.comment_list_item_time);
            replyTx = (TextView) itemView.findViewById(R.id.comment_list_item_reply);
        }
    }
}
