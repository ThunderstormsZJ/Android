package cn.spinsoft.wdq.video.widget;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.browse.biz.BrowseHandler;
import cn.spinsoft.wdq.enums.Attention;
import cn.spinsoft.wdq.video.biz.AdvertisementInfo;
import cn.spinsoft.wdq.video.biz.DanceVideoBean;
import cn.spinsoft.wdq.widget.EquallyWidthLabel;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by hushujun on 15/11/3.
 */
public class VideoListAdapter extends BaseRecycleAdapter<DanceVideoBean> {
    private static final String TAG = VideoListAdapter.class.getSimpleName();
    private static final int TYPE_ADS = 1 << 1;
    private List<AdvertisementInfo> adsUrls;
    private ImageLoopView.ImageLoopViewListener loopViewListener;

    private boolean isSearchMode = false;

    public VideoListAdapter(List<DanceVideoBean> adapterDataList, RecyclerItemClickListener itemClickListener) {
        super(adapterDataList, itemClickListener);
    }

    public VideoListAdapter(List<DanceVideoBean> adapterDataList, RecyclerItemClickListener itemClickListener, boolean isSearchMode) {
        this(adapterDataList, itemClickListener);
        this.isSearchMode = isSearchMode;
    }

    public void setAdsUrls(List<AdvertisementInfo> adsUrls, ImageLoopView.ImageLoopViewListener loopViewListener) {
        if (adsUrls == null) {
            this.adsUrls = new ArrayList<>();
        } else {
            this.adsUrls = adsUrls;
        }
        this.loopViewListener = loopViewListener;
        notifyItemChanged(1);
    }

    public void notifyItemChanged(DanceVideoBean recodeBean, int position) {
        if (position < getItemCount() && recodeBean != null) {
            if (position > 1 && !isSearchMode) {
                position = position - 1;
                adapterDataList.set(position, recodeBean);
                notifyItemChanged(position + 1);
            } else {
                adapterDataList.set(position, recodeBean);
                notifyItemChanged(position);
            }
        }
    }

    public void notifyItemAttentionChanged(boolean attention, int position) {
        if (position < getItemCount()) {
            int tempPos = position;
            if (tempPos > 1 && !isSearchMode) {
                tempPos = tempPos - 1;
            }
            DanceVideoBean dancerInfo = adapterDataList.get(tempPos);
            if (attention) {
                if (!dancerInfo.getAttention().isAttented())
                    dancerInfo.setAttention(Attention.getEnum(dancerInfo.getAttention().getValue() + 1));
            } else {
                if (dancerInfo.getAttention().isAttented())
                    dancerInfo.setAttention(Attention.getEnum(dancerInfo.getAttention().getValue() - 1));
            }
            notifyItemChanged(position);
        }
    }

    public void notifyItemLikeChanged(boolean like, int position) {
        if (position < getItemCount()) {
            DanceVideoBean videoBean = getItem(position);
            videoBean.setLiked(like);
            videoBean.setLikeCount(like ? videoBean.getLikeCount() + 1 : videoBean.getLikeCount() - 1);
            notifyItemChanged(position);
        }
    }

//    public void notifyItemLikeChanged(int lickCount, int position) {
//        if (position < getItemCount()) {
//            int tempPos = position;
//            if (tempPos > 1 && !isSearchMode) {
//                tempPos = tempPos - 1;
//            }
//            adapterDataList.get(tempPos).setLikeCount(lickCount);
//            notifyItemChanged(position);
//        }
//    }

    public void notifyItemForwardChanged(int position) {
        if (position < getItemCount()) {
            DanceVideoBean videoBean = getItem(position);
            videoBean.setForwardCount(videoBean.getForwardCount() + 1);
            notifyItemChanged(position);
        }
    }

    @Override
    public DanceVideoBean getItem(int position) {
        if (position < getItemCount() && !isSearchMode) {
            if (position < 1) {
                return adapterDataList.get(position);
            } else if (position == 1) {
                return null;
            } else {
                return adapterDataList.get(position - 1);
            }
        } else {
            return adapterDataList.get(position);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ADS) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_video_ads, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_video_list_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        if (holder.getItemViewType() == TYPE_ADS) {

        } else {
            if (position > 1 && !isSearchMode) {
                position = position - 1;
            }
            DanceVideoBean videoBean = adapterDataList.get(position);
            holder.mPhotoCImg.setImageURI(Uri.parse(videoBean.getPhotoUrl()));
            holder.mNameTx.setText(videoBean.getNickName());
            holder.mPosterImg.setImageURI(Uri.parse(videoBean.getPosterUrl()));
            holder.mOriginalImg.setVisibility(videoBean.isOriginal() ? View.VISIBLE : View.GONE);


            holder.mAttentionBtn.setVisibility(View.VISIBLE);
            if (BrowseHandler.watcherUserId == videoBean.getUserId()) {
                holder.mAttentionBtn.setVisibility(View.GONE);
            }
            holder.mAttentionBtn.setSelected(videoBean.getAttention().isAttented());
            holder.mAttentionBtn.setTag(videoBean.getAttention().isAttented());

            holder.mTitleTx.setText(videoBean.getTitle());
            holder.mTimeDiffTx.setText(videoBean.getTimeDiff());

            holder.mLikesTx.setText(videoBean.getLikeCount() == 0 ? "赞" : String.valueOf(videoBean.getLikeCount()));
            holder.mLikesTx.setSelected(videoBean.isLiked());
            holder.mLikesTx.setTag(videoBean.isLiked());

            holder.mForwardTx.setText(videoBean.getForwardCount() == 0 ? "转发" : String.valueOf(videoBean.getForwardCount()));
            holder.mCommentsTx.setText(videoBean.getCommentCount() == 0 ? "评论" : String.valueOf(videoBean.getCommentCount()));
            holder.mTipsTx.setText(videoBean.getAdmireCount() == 0 ? "打赏" : String.valueOf(videoBean.getAdmireCount()));

            holder.itemView.setTag(videoBean);

            if (itemClickListener != null) {
                holder.mPhotoCImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(VideoListAdapter.this, v, holder.getLayoutPosition());
                    }
                });
                holder.mAttentionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(VideoListAdapter.this, v, holder.getLayoutPosition());
                    }
                });
                holder.mLikesTx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(VideoListAdapter.this, v, holder.getLayoutPosition());
                    }
                });
                holder.mForwardTx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(VideoListAdapter.this, v, holder.getLayoutPosition());
                    }
                });
                holder.mCommentsTx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(VideoListAdapter.this, v, holder.getLayoutPosition());
                    }
                });
                holder.mTipsTx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(VideoListAdapter.this, v, holder.getLayoutPosition());
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(VideoListAdapter.this, v, holder.getLayoutPosition());
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (adapterDataList == null || adapterDataList.isEmpty()) {
            return 0;
        } else {
            if (isSearchMode) {
                return adapterDataList.size();
            } else {
                return adapterDataList.size() + 1;//加1为广告位
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 1 && !isSearchMode) {
            return TYPE_ADS;
        } else {
            return super.getItemViewType(position);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView mPhotoCImg;
        private TextView mNameTx, mTitleTx, mTimeDiffTx;
        private EquallyWidthLabel mForwardTx, mCommentsTx, mLikesTx, mTipsTx;
        private Button mAttentionBtn;
        private SimpleDraweeView mPosterImg;
        private ImageView mOriginalImg;
        private ImageLoopView mAdsPager;

        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView instanceof ImageLoopView) {
                mAdsPager = (ImageLoopView) itemView;
                mAdsPager.setImageResources(adsUrls, loopViewListener);
            } else {
                mPhotoCImg = (SimpleDraweeView) itemView.findViewById(R.id.videos_list_item_photo);
                mNameTx = (TextView) itemView.findViewById(R.id.videos_list_item_name);
                mOriginalImg = (ImageView) itemView.findViewById(R.id.videos_list_item_originalImg);
                mTitleTx = (TextView) itemView.findViewById(R.id.videos_list_item_title);
                mTimeDiffTx = (TextView) itemView.findViewById(R.id.videos_list_item_timeDiff);
                mLikesTx = (EquallyWidthLabel) itemView.findViewById(R.id.videos_list_item_likes);
                mForwardTx = (EquallyWidthLabel) itemView.findViewById(R.id.videos_list_item_forwards);
                mCommentsTx = (EquallyWidthLabel) itemView.findViewById(R.id.videos_list_item_comments);
                mTipsTx = (EquallyWidthLabel) itemView.findViewById(R.id.videos_list_item_tips);
                mAttentionBtn = (Button) itemView.findViewById(R.id.videos_list_item_attention);
                mPosterImg = (SimpleDraweeView) itemView.findViewById(R.id.videos_list_item_poster);
            }
        }
    }

}
