package cn.spinsoft.wdq.user.widget;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.org.biz.OrgHandler;
import cn.spinsoft.wdq.user.biz.UserDetail;
import cn.spinsoft.wdq.user.biz.UserHandler;
import cn.spinsoft.wdq.user.biz.UserVideo;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by hushujun on 15/12/2.
 */
public class WorksAdapter extends BaseRecycleAdapter<UserVideo> {
    private static final String TAG = WorksAdapter.class.getSimpleName();

    public WorksAdapter(List<UserVideo> adapterDataList, RecyclerItemClickListener itemClickListener) {
        super(adapterDataList, itemClickListener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_user_works_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        UserVideo userVideo = adapterDataList.get(position);
        holder.posterImg.setImageURI(Uri.parse(userVideo.getPoster()));
        holder.nameTx.setText(userVideo.getTitle());
        if (userVideo.getUserId() == UserHandler.watcherUserId || userVideo.getUserId() == OrgHandler.watcherUserId) {
            holder.mDeleteIBtn.setVisibility(View.VISIBLE);
            holder.mDeleteIBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(WorksAdapter.this, v, holder.getLayoutPosition());
                }
            });
        }

        if (position % 2 == 0) {
            holder.itemView.setPadding(0, 10, 2, 10);
        } else {
            holder.itemView.setPadding(2, 10, 0, 10);
        }

        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(WorksAdapter.this, v, holder.getLayoutPosition());
                }
            });
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView posterImg;
        private TextView nameTx;
        private ImageButton mDeleteIBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            posterImg = (SimpleDraweeView) itemView.findViewById(R.id.user_works_item_poster);
            nameTx = (TextView) itemView.findViewById(R.id.user_works_item_name);
            mDeleteIBtn = (ImageButton) itemView.findViewById(R.id.user_works_item_delete);
        }
    }
}
