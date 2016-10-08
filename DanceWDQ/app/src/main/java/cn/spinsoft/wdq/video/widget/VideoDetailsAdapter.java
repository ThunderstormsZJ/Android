package cn.spinsoft.wdq.video.widget;

import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.bean.CommentItem;
import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.utils.SecurityUtils;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.video.biz.DetailHandler;
import cn.spinsoft.wdq.video.biz.VideoDetailBean;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;
import tcking.github.com.giraffeplayer.GiraffePlayer;

/**
 * Created by zhoujun on 16/1/26.
 */
public class VideoDetailsAdapter extends BaseRecycleAdapter<CommentItem> {
    private static final String TAG = VideoDetailsAdapter.class.getSimpleName();
    private static final int TYPE_HEAD = 1;
    private static final int TYPE_COMMENT = 2;
    private static final int TYPE_CENTER = 3;
    private static final int TYPE_VIDEO = 4;
    private GiraffePlayer mGiraffePlayer;

    private VideoDetailBean mVideoDetails;

    public VideoDetailsAdapter(List<CommentItem> adapterDataList, RecyclerItemClickListener itemClickListener) {
        super(adapterDataList, itemClickListener);
    }

    public VideoDetailsAdapter(List<CommentItem> adapterDataList, RecyclerItemClickListener itemClickListener, BaseActivity activity) {
        super(adapterDataList, itemClickListener, activity);
    }

    public VideoDetailBean getVideoDetails() {
        return mVideoDetails;
    }

    public void setVideoDetails(VideoDetailBean videoDetails) {
        if (videoDetails == null) {
            return;
        }
        mVideoDetails = videoDetails;
        notifyItemChanged(0);
        notifyItemChanged(1);
    }

    public void addCommentIntoList(CommentItem comment) {
        if (comment == null) {
            return;
        }
        if (adapterDataList == null) {
            adapterDataList = new ArrayList<>();
        }
        adapterDataList.add(0, comment);
        notifyItemInserted(1);
    }

    public void addRewordHead(SimpleItemData userHead) {
        if (mVideoDetails == null) {
            return;
        }
        List<SimpleItemData> userAdmire = mVideoDetails.getUserAdmire();
        if (userAdmire == null) {
            userAdmire = new ArrayList<SimpleItemData>();
        }
        if (!userAdmire.contains(userHead)) {
            userAdmire.add(0, userHead);
            mVideoDetails.setAdmireCount(mVideoDetails.getAdmireCount() + 1);
        }
        notifyItemChanged(0);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 3;
    }

    @Override
    public CommentItem getItem(int position) {
        if (position == 0 || position == 1 || position == 2) {
            return null;
        } else {
            return super.getItem(position - 3);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        } else if (position == 1) {
            return TYPE_VIDEO;
        } else if (position == 2) {
            return TYPE_CENTER;
        } else {
            return TYPE_COMMENT;
        }
    }

    public void videoDestroy() {
        if (mGiraffePlayer != null) {
            mGiraffePlayer.onDestroy();
        }
    }

    public void videoConfigureChange(Configuration newConfig) {
        if(mGiraffePlayer!=null){
            mGiraffePlayer.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD) {
            return new HeadHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_video_detail_head, parent, false));
        } else if (viewType == TYPE_VIDEO) {
            return new VideoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_video_detail_video, parent, false));
        } else if (viewType == TYPE_CENTER) {
            return new CenterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_video_detail_center, parent, false));
        } else {
            return new CommentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_comment_list_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEAD) {
            bindHeadHolder((HeadHolder) holder, position);
        } else if (holder.getItemViewType() == TYPE_VIDEO) {
            bindVideoHolder((VideoHolder) holder, position - 1);
        } else if (holder.getItemViewType() == TYPE_CENTER) {
            bindCenterHolder((CenterHolder) holder, position - 2);
        } else {
            bindCommentHolder((CommentHolder) holder, position - 3);
        }
    }

    //绑定数据-评论
    private void bindCommentHolder(final CommentHolder commentHolder, int position) {
        CommentItem commentItem = adapterDataList.get(position);
        commentHolder.photoCimg.setImageURI(Uri.parse(commentItem.getPhotoUrl()));
        commentHolder.nickNameTx.setText(commentItem.getNickName());
        commentHolder.contentTx.setText(commentItem.getContent());
        commentHolder.timeTx.setText(commentItem.getTime());

        //设置监听
        commentHolder.photoCimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked(VideoDetailsAdapter.this, v, commentHolder.getLayoutPosition());
            }
        });
    }

    private void bindCenterHolder(final CenterHolder centerHolder, int position) {
        if (mVideoDetails != null) {
            centerHolder.mOriginalImg.setVisibility(mVideoDetails.isOriginal() ? View.VISIBLE : View.GONE);
            String danceName = mVideoDetails.getDanceName();
            if (danceName == null || TextUtils.isEmpty(danceName)) {
                centerHolder.mDanceNameTx.setVisibility(View.GONE);
            } else {
                centerHolder.mDanceNameTx.setVisibility(View.VISIBLE);
                centerHolder.mDanceNameTx.setText(danceName);
            }
            centerHolder.mVideoTitleTx.setText(mVideoDetails.getTitle());
            centerHolder.mVideoPlayCountTx.setText(String.valueOf(mVideoDetails.getWatchCount()));
            centerHolder.mVideoForwardCountTx.setText(mVideoDetails.getForwardCount() == 0 ? "" : mVideoDetails.getForwardCount() + "");
            centerHolder.mVideoLikeCountTx.setText(mVideoDetails.getLikeCount() == 0 ? "" : mVideoDetails.getLikeCount() + "");
            centerHolder.mVideoLikeCountTx.setSelected(mVideoDetails.isLike());
            Spanned admireSpan = Html.fromHtml("<font color=#937be5>" + mVideoDetails.getAdmireCount() + "</font>人打赏");
            centerHolder.mVideoRewordCountTx.setText(admireSpan);
            centerHolder.mVideoCommentCountTx.setText(mVideoDetails.getCommentCount() + "条评论");

            //打赏人头像
            if (centerHolder.mLikeHeadsLy.getChildCount() > 0) {
                centerHolder.mLikeHeadsLy.removeAllViews();
            }
            List<SimpleItemData> userAdmires = mVideoDetails.getUserAdmire();
            if (userAdmires != null && !userAdmires.isEmpty()) {
                int size = userAdmires.size() > 8 ? 8 : userAdmires.size();
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(60, 60);
                for (int i = 0; i < size; i++) {
                    GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(centerHolder.itemView.getResources());
                    builder.setRoundingParams(RoundingParams.asCircle());
                    builder.setFailureImage(centerHolder.itemView.getResources().getDrawable(R.mipmap.ic_default_user_head));
                    SimpleDraweeView draweeView = new SimpleDraweeView(centerHolder.itemView.getContext(), builder.build());
                    if (i > 0) {
                        lp.leftMargin = 10;
                    }
                    draweeView.setImageURI(Uri.parse(userAdmires.get(i).getContent()));
                    draweeView.setTag(i);
                    centerHolder.mLikeHeadsLy.addView(draweeView, lp);
                    draweeView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClickListener.onItemClicked(VideoDetailsAdapter.this, v, 0);
                        }
                    });
                }
            }

            if (itemClickListener != null) {
                centerHolder.mRewordImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(VideoDetailsAdapter.this, v, 0);
                    }
                });
                centerHolder.mVideoForwardCountTx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(VideoDetailsAdapter.this, v, 0);
                    }
                });
                centerHolder.mVideoLikeCountTx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(VideoDetailsAdapter.this, v, 0);
                    }
                });
            }
        }
    }

    //绑定数据-头部
    private void bindHeadHolder(final HeadHolder headHolder, int position) {
        if (mVideoDetails != null) {
            headHolder.mPhotoImg.setImageURI(Uri.parse(mVideoDetails.getPhotoUrl()));
            headHolder.mNickNameTx.setText(mVideoDetails.getNickName());
            headHolder.mTimeDiffTx.setText(mVideoDetails.getTimeDiff());

            headHolder.mAttentionBut.setSelected(mVideoDetails.isAttentioned());
            if (mVideoDetails.getUserId() == DetailHandler.watcherUserId) {
                headHolder.mAttentionBut.setVisibility(View.GONE);
            }

            //视频播放
      /*      headHolder.mVideoPlayer.setUp(mVideoDetails.getPlayUrl(), mVideoDetails.getPosterUrl(), "");
            headHolder.mVideoPlayer.setOnMediaControlCallBack(new JCVideoPlayer.OnMediaControlCallBack() {
                @Override
                public void onPlayStart(View v) {
                    mVideoDetails.setWatchCount(mVideoDetails.getWatchCount() + 1);
                    headHolder.mVideoPlayCountTx.setText(mVideoDetails.getWatchCount() + "次播放");
                    itemClickListener.onItemClicked(VideoDetailsAdapter.this, v, 0);
                }
            });*/
        /*    final List<VideoInfo> videos=new ArrayList<VideoInfo>();
            VideoInfo videoInfo=new VideoInfo();
            videoInfo.description="标清";
            videoInfo.type=VideoInfo.VideoType.MP4;
            videoInfo.url= mVideoDetails.getPlayUrl();
            videos.add(videoInfo);*/

            if (itemClickListener != null) {
                headHolder.mPhotoImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(VideoDetailsAdapter.this, v, 0);
                    }
                });

                headHolder.mAttentionBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SecurityUtils.isUserValidity(getActivity(), SharedPreferencesUtil.getInstance(getActivity()).getLoginUser())) {
                            headHolder.mAttentionBut.setSelected(!mVideoDetails.isAttentioned());
                            itemClickListener.onItemClicked(VideoDetailsAdapter.this, v, 0);
                        }
                    }
                });
            }
        }
    }

    //绑定数据-视频
    private void bindVideoHolder(final VideoHolder videoHolder, int position) {
        if (mVideoDetails != null) {
            if (videoHolder.mVideoPosterImg.getTag() != null) {
                if (!videoHolder.mVideoPosterImg.getTag().equals(mVideoDetails.getPosterUrl())) {
                    videoHolder.mVideoPosterImg.setImageURI(Uri.parse(mVideoDetails.getPosterUrl()));
                    videoHolder.mVideoPosterImg.setTag(mVideoDetails.getPosterUrl());
                }
            } else {
                videoHolder.mVideoPosterImg.setImageURI(Uri.parse(mVideoDetails.getPosterUrl()));
                videoHolder.mVideoPosterImg.setTag(mVideoDetails.getPosterUrl());
            }

            videoHolder.mVideoPlayOperImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoHolder.mVideoPosterImg.setVisibility(View.GONE);
                    videoHolder.mVideoPlayOperImg.setVisibility(View.GONE);
                    mGiraffePlayer.play(mVideoDetails.getPlayUrl());
//                    headHolder.mVideoPlayer.play(videos);
                    itemClickListener.onItemClicked(VideoDetailsAdapter.this, v, 0);
                }
            });
        }
    }


    //-----conmment------
    class CommentHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView photoCimg;
        private TextView nickNameTx, contentTx, timeTx, replyTx;

        public CommentHolder(View itemView) {
            super(itemView);
            photoCimg = (SimpleDraweeView) itemView.findViewById(R.id.comment_list_item_photo);
            nickNameTx = (TextView) itemView.findViewById(R.id.comment_list_item_name);
            contentTx = (TextView) itemView.findViewById(R.id.comment_list_item_content);
            timeTx = (TextView) itemView.findViewById(R.id.comment_list_item_time);
            replyTx = (TextView) itemView.findViewById(R.id.comment_list_item_reply);
        }
    }

    //-----head------
    class HeadHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView mPhotoImg;
        private TextView mNickNameTx, mTimeDiffTx;
        private Button mAttentionBut;
        //        private JCVideoPlayer mVideoPlayer;
//        private VideoRootFrame mVideoPlayer;


        public HeadHolder(View itemView) {
            super(itemView);
            mPhotoImg = (SimpleDraweeView) itemView.findViewById(R.id.video_detail_photo);
            mNickNameTx = (TextView) itemView.findViewById(R.id.video_detail_nickName);
            mTimeDiffTx = (TextView) itemView.findViewById(R.id.videos_detail_timeDiff);
            mAttentionBut = (Button) itemView.findViewById(R.id.video_detail_attention);
//            mVideoPlayer = (JCVideoPlayer) itemView.findViewById(R.id.video_detail_videoview);
//            mVideoPlayer = (VideoRootFrame) itemView.findViewById(R.id.player);
        }
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView mVideoPosterImg;
        private ImageView mVideoPlayOperImg;
        public FrameLayout mContainerFl;

        public VideoHolder(View itemView) {
            super(itemView);
            mVideoPosterImg = (SimpleDraweeView) itemView.findViewById(R.id.video_detail_poster);
            mVideoPlayOperImg = (ImageView) itemView.findViewById(R.id.video_detail_play);
            mGiraffePlayer = new GiraffePlayer(getActivity(),itemView);
            mContainerFl = (FrameLayout) itemView.findViewById(R.id.video_detail_video_container);
        }
    }

    class CenterHolder extends RecyclerView.ViewHolder {
        private ImageView mOriginalImg, mRewordImg;
        private TextView mDanceNameTx, mVideoTitleTx, mVideoPlayCountTx,
                mVideoForwardCountTx, mVideoLikeCountTx, mVideoRewordCountTx, mVideoCommentCountTx;
        private LinearLayout mLikeHeadsLy;

        public CenterHolder(View itemView) {
            super(itemView);
            mOriginalImg = (ImageView) itemView.findViewById(R.id.video_detail_original);
            mRewordImg = (ImageView) itemView.findViewById(R.id.video_detail_reword);
            mDanceNameTx = (TextView) itemView.findViewById(R.id.video_detail_dance_name);
            mVideoTitleTx = (TextView) itemView.findViewById(R.id.video_detail_title);
            mVideoPlayCountTx = (TextView) itemView.findViewById(R.id.video_detail_play_count);
            mVideoForwardCountTx = (TextView) itemView.findViewById(R.id.video_detail_forward_count);
            mVideoLikeCountTx = (TextView) itemView.findViewById(R.id.video_detail_likeCount);
            mVideoRewordCountTx = (TextView) itemView.findViewById(R.id.video_detail_rewordCount);
            mVideoCommentCountTx = (TextView) itemView.findViewById(R.id.video_detail_commentCount);
            mLikeHeadsLy = (LinearLayout) itemView.findViewById(R.id.video_detail_likeHeads);
        }
    }
}
