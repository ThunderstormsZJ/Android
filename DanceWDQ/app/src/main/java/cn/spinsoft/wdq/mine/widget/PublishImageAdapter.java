package cn.spinsoft.wdq.mine.widget;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.utils.BitmapUtils;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by hushujun on 15/12/18.
 */
public class PublishImageAdapter extends BaseRecycleAdapter<String> {
    private static final String TAG = PublishImageAdapter.class.getSimpleName();
    public static final int TYPE_ADD = 1;

    public PublishImageAdapter(List<String> adapterDataList, RecyclerItemClickListener itemClickListener) {
        super(adapterDataList, itemClickListener);
    }

    public void addAdapterDataEnd(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        int position = adapterDataList.size();
        adapterDataList.add(path);
        if (position == getItemCount() - 1) {
            notifyItemChanged(position);
        } else {
            notifyItemInserted(position);
        }
    }

    public void delAdapterData(int position) {
        if (position >= 0 && position < adapterDataList.size()) {
            adapterDataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public List<String> getAllImagePaths() {
        return adapterDataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(160, 160);
        lp.leftMargin = 5;
        lp.topMargin = 5;
        lp.bottomMargin = 5;
        lp.rightMargin = 5;
        imageView.setLayoutParams(lp);
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        if (holder.getItemViewType() == TYPE_ADD) {
            holder.imageView.setImageResource(R.mipmap.image_add);
        } else {
            Bitmap bm = BitmapUtils.decodeClipBitmap(adapterDataList.get(position), 160, 160);
            holder.imageView.setImageBitmap(bm);
        }
        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(PublishImageAdapter.this, v, holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (adapterDataList.size() < 4) {
            return adapterDataList.size() + 1;
        } else {
            return 4;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (adapterDataList.size() < 4 && position == getItemCount() - 1) {
            return TYPE_ADD;
        } else {
            return super.getItemViewType(position);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
}
