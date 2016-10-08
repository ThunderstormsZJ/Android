package cn.spinsoft.wdq.discover.widget;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.album.PicturesActivity;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.bean.CommentItem;
import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.discover.SignListActivity;
import cn.spinsoft.wdq.discover.biz.DiscoverDetail;
import cn.spinsoft.wdq.discover.biz.DiscoverHandler;
import cn.spinsoft.wdq.enums.ContestProgessState;
import cn.spinsoft.wdq.enums.DiscoverType;
import cn.spinsoft.wdq.org.OrgDetailsActivity;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by zhoujun on 15/12/8.
 */
public class DiscoverDetailAdapter extends BaseRecycleAdapter<CommentItem> {
    private static final String TAG = DiscoverDetailAdapter.class.getSimpleName();
    private static final int HEAD_ACTIVITY = 1;
    private static final int HEAD_CONTEST = 2;
    private static final int HEAD_RECRUIT = 3;
    private static final int HEAD_OTHER = 4;
    private static final int TYPE_COMMENT = 5;
    private DiscoverDetail discoverDetail;
    private final static int IMG_COUNT = 6;


    public DiscoverDetailAdapter(List<CommentItem> adapterDataList, RecyclerItemClickListener itemClickListener, Activity activity) {
        super(adapterDataList, itemClickListener, activity);
    }

    public DiscoverDetailAdapter(List<CommentItem> adapterDataList, RecyclerItemClickListener itemClickListener) {
        super(adapterDataList, itemClickListener);
    }

    public void setDiscoverDetail(DiscoverDetail detail) {
        if (detail == null) {
            return;
        }
        this.discoverDetail = detail;
        notifyItemChanged(0);
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


    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (discoverDetail != null && discoverDetail.getType() == DiscoverType.RECRUIT) {
            return HEAD_RECRUIT;
        } else {
            if (position == 0) {
                if (discoverDetail != null) {
                    switch (discoverDetail.getType()) {
                        case ACTIVITY:
                            return HEAD_ACTIVITY;
                        case CONTEST:
                            return HEAD_CONTEST;
                        case TOPIC:
                        case OTHER:
                        default:
                            return HEAD_OTHER;
                    }
                } else {
                    return HEAD_OTHER;
                }
            } else {
                return TYPE_COMMENT;
            }
        }
    }

    @Override
    public CommentItem getItem(int position) {
        if (position == 0) {
            return null;
        } else {
            return super.getItem(position - 1);
        }
    }

    public DiscoverDetail getDiscoverDetail() {
        return discoverDetail;
    }

    public void addLikeHead(SimpleItemData itemData) {
        if (discoverDetail == null) {
            return;
        }
        List<SimpleItemData> likeHeads = discoverDetail.getLikeUsers();
        if (likeHeads == null) {
            likeHeads = new ArrayList<>();
        }
        likeHeads.add(0, itemData);
        discoverDetail.setLikeUsers(likeHeads);
        discoverDetail.setLikeCount(discoverDetail.getLikeCount() + 1);
        discoverDetail.setIsLike(true);
        notifyItemChanged(0);
    }

    public void deleteLikeHead(SimpleItemData itemData) {
        if (discoverDetail == null) {
            return;
        }
        List<SimpleItemData> likeHeads = discoverDetail.getLikeUsers();
        if (likeHeads == null || likeHeads.isEmpty()) {
            return;
        }
        boolean success = likeHeads.remove(itemData);
        if (success) {
            discoverDetail.setLikeUsers(likeHeads);
            discoverDetail.setLikeCount(discoverDetail.getLikeCount() - 1);
            discoverDetail.setIsLike(false);
            notifyItemChanged(0);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEAD_ACTIVITY://活动
                return new ActivityHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ly_discover_detail, parent, false));
            case HEAD_CONTEST://赛事
                return new ContestHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ly_discover_detail_contest, parent, false));
            case HEAD_RECRUIT://招聘
                return new RecruitHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ly_discover_detail_recruit, parent, false));
            case HEAD_OTHER://寻宝
                return new OtherHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ly_discover_detail, parent, false));
            default:
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ly_comment_list_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_COMMENT) {
            bindCommentViewHolder((ViewHolder) holder, position - 1);
        } else if (holder.getItemViewType() == HEAD_CONTEST) {
            bindContestViewHolder((ContestHolder) holder, position);
        } else if (holder.getItemViewType() == HEAD_RECRUIT) {
            bindRecruitViewHolder((RecruitHolder) holder, position);
        } else {
            bindHeadViewHolder((HeadHolder) holder, position);
        }
    }

    private void bindHeadViewHolder(HeadHolder holder, int position) {
        if (discoverDetail != null) {
            holder.photoCimg.setImageURI(Uri.parse(discoverDetail.getPhotoUrl()));
            holder.nameTx.setText(discoverDetail.getNickName());
            holder.publishTx.setText(discoverDetail.getPublishTime());
            holder.titleTx.setText(discoverDetail.getTitle());
            holder.descTx.setText(discoverDetail.getContent());
            holder.commentCountTx.setText(discoverDetail.getCommentCount() + "条评论");
            holder.likeCountTx.setText(String.valueOf(discoverDetail.getLikeCount()));

            switch (discoverDetail.getType()) {
                case ACTIVITY:
                    ActivityHolder aHolder = (ActivityHolder) holder;
                    aHolder.orgNameTx.setText(discoverDetail.getOrgName());
                    aHolder.timeStartTx.setText(discoverDetail.getStartTime());
                    aHolder.timeEndTx.setText(discoverDetail.getEndTime());
                    aHolder.addressTx.setText(discoverDetail.getLocation());
                    aHolder.danceTx.setText(discoverDetail.getDanceName());
                    break;
                /*case RECRUIT:
                    RecruitHolder rHolder = (RecruitHolder) holder;
//                    rHolder.orgNameTx.setText(discoverDetail.getOrgName());
                    rHolder.danceTx.setText(discoverDetail.getDanceName());
//                    rHolder.positionTx.setText(discoverDetail.getTitle());
                    rHolder.salaryTx.setText(discoverDetail.getSalary());
                    rHolder.orgDescTx.setText(discoverDetail.getOrgIntro());
                    holder.signUpImg.setVisibility(View.GONE);
                    break;*/
                case TOPIC:
                    OtherHolder tHolder = (OtherHolder) holder;
                    holder.signUpImg.setVisibility(View.GONE);
                    tHolder.danceTx.setText(discoverDetail.getDanceName());
                    break;
                case OTHER:
                    OtherHolder oHolder = (OtherHolder) holder;
                    holder.signUpImg.setVisibility(View.GONE);
                    oHolder.danceTx.setVisibility(View.GONE);
                    oHolder.danceNameTx.setVisibility(View.GONE);
                default:
//                    OtherHolder oHolder = (OtherHolder) holder;
//                    oHolder.danceTx.setVisibility(View.GONE);
                    break;
            }

            final List<String> imageUrls = discoverDetail.getImageUrls();
            List<String> smallImageUrls = discoverDetail.getSmallImageUrls();
            final String dicoverContent = discoverDetail.getContent();
            if (smallImageUrls != null && !smallImageUrls.isEmpty()) {
                holder.imageContainerGL.setVisibility(View.VISIBLE);
                int visible = Math.min(smallImageUrls.size(), IMG_COUNT);
                for (int i = 0; i < IMG_COUNT; i++) {
                    if (i < visible) {
                        holder.imageViews[i].setVisibility(View.VISIBLE);
                        int imgSize = getActivity().getWindowManager().getDefaultDisplay().getWidth() - 40;
                        imgSize = imgSize / 3;
                        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(smallImageUrls.get(i)))
                                .setResizeOptions(new ResizeOptions(imgSize, imgSize)).build();
                        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                                .setOldController(holder.imageViews[i].getController())
                                .setImageRequest(request).build();
                        holder.imageViews[i].setController(controller);
                        holder.imageViews[i].setTag(i + 1);
                        if (itemClickListener != null) {
                            holder.imageViews[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    itemClickListener.onItemClicked(DiscoverDetailAdapter.this, v, 0);
                                    Intent picIntent = new Intent(getActivity(), PicturesActivity.class);
                                    picIntent.putStringArrayListExtra(Constants.Strings.PICTURE_URLS,
                                            (ArrayList<String>) imageUrls);
                                    picIntent.putExtra(Constants.Strings.PICTURE_DESC, dicoverContent);
                                    picIntent.putExtra(Constants.Strings.PICTURE_POSITION, (Integer) v.getTag());
                                    getActivity().startActivity(picIntent);
                                }
                            });
                        }
                    } else {
                        holder.imageViews[i].setVisibility(View.GONE);
                    }
                }
            } else {
                holder.imageContainerGL.setVisibility(View.GONE);
            }

            boolean isLike = discoverDetail.isLike();
            holder.likeImg.setSelected(isLike);
            /*if (isLike) {
                holder.likeImg.setImageResource(R.mipmap.ic_video_like_true);
            } else {
                holder.likeImg.setImageResource(R.mipmap.ic_video_like_false);
            }*/

            //点赞头像
            if (holder.headContainerLl.getChildCount() > 0) {
                holder.headContainerLl.removeAllViews();
            }
            List<SimpleItemData> likeUrls = discoverDetail.getLikeUsers();
            if (likeUrls != null && !likeUrls.isEmpty()) {
                int size = likeUrls.size() > 8 ? 8 : likeUrls.size();
                LinearLayout.LayoutParams lp;
                for (int i = 0; i < size; i++) {
                    GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(holder.itemView.getResources());
                    builder.setRoundingParams(RoundingParams.asCircle());
                    builder.setFailureImage(holder.itemView.getResources().getDrawable(R.mipmap.ic_default_user_head));
                    builder.setPlaceholderImage(holder.itemView.getResources().getDrawable(R.mipmap.ic_default_user_head));
                    SimpleDraweeView img = new SimpleDraweeView(holder.itemView.getContext(), builder.build());
                    lp = new LinearLayout.LayoutParams(60, 60);
                    if (i > 0) {
                        lp.leftMargin = 10;
                    }
                    img.setTag(i);
                    img.setImageURI(Uri.parse(likeUrls.get(i).getName()));
                    holder.headContainerLl.addView(img, lp);
                    if (itemClickListener != null) {
                        img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                itemClickListener.onItemClicked(DiscoverDetailAdapter.this, v, 0);
                            }
                        });
                    }
                }
            }

            if (itemClickListener != null) {
                holder.photoCimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(DiscoverDetailAdapter.this, v, 0);
                    }
                });
                holder.forwardImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(DiscoverDetailAdapter.this, v, 0);
                    }
                });
                holder.signUpImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(DiscoverDetailAdapter.this, v, 0);
                    }
                });
                holder.likeImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(DiscoverDetailAdapter.this, v, 0);
                    }
                });
            }
        }
    }

    private void bindCommentViewHolder(final ViewHolder holder, int position) {
        CommentItem comment = adapterDataList.get(position);
        holder.photoCimg.setImageURI(Uri.parse(comment.getPhotoUrl()));
        holder.nickNameTx.setText(comment.getNickName());
        holder.contentTx.setText(comment.getContent());
        holder.timeTx.setText(comment.getTime());

        if (itemClickListener != null) {
            holder.photoCimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(DiscoverDetailAdapter.this, v, holder.getLayoutPosition());
                }
            });
        }
    }

    //绑定赛事数据
    private void bindContestViewHolder(final ContestHolder holder, int position) {
        if (discoverDetail != null) {
            holder.mPhotoImg.setImageURI(Uri.parse(discoverDetail.getPhotoUrl()));
            holder.mNickNameTx.setText(discoverDetail.getNickName());
            holder.mReleaseTimeTx.setText(discoverDetail.getPublishTime());
            holder.mEndTimeTx.setText(Html.fromHtml("报名时间截止至: <font color='#937e5'>" + discoverDetail.getPublishEndTime() + "</font>"));
            if (discoverDetail.getPeopleNum() == 0) {
                holder.mPeopleNumTx.setText("已有0人报名");
            } else {
                holder.mPeopleNumTx.setText(Html.fromHtml("已有 <font color='#937e5'>" + discoverDetail.getPeopleNum() + "</font> 人报名"));
            }
            if (discoverDetail.getUserId() == DiscoverHandler.watcherUserId) {
                holder.mPeopleNumTx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), SignListActivity.class);
                        intent.putExtra(Constants.Strings.DISCOVER_EVENT_ID, DiscoverHandler.Status.eventId);
                        intent.putExtra(Constants.Strings.DISCOVER_TYPE_ID, DiscoverHandler.Status.typeId);
                        getActivity().startActivity(intent);
                    }
                });
            }
            holder.mTitleTx.setText(discoverDetail.getTitle());
            holder.mProgessTx.setText(discoverDetail.getProgessState().getDesc());
            if (TextUtils.isEmpty(discoverDetail.getPosSmallImg())) {
                holder.mPosImg.setImageURI(Uri.parse(discoverDetail.getSmallImageUrls().get(0)));
            } else {
                holder.mPosImg.setImageURI(Uri.parse(discoverDetail.getPosBigImg()));
            }
            if (TextUtils.isEmpty(discoverDetail.getVideoUrl())) {
            }
            holder.mVideoContainerRl.setVisibility(View.GONE);

            holder.mUnitTx.setText(discoverDetail.getOrgName());
            holder.mAddressTx.setText(discoverDetail.getLocation());
            holder.mDanceTypeTx.setText(discoverDetail.getDanceName());
            holder.mStartTimeTx.setText(discoverDetail.getStartTime());
            holder.mInfoEndTimeTx.setText(discoverDetail.getEndTime());

            holder.mContentTx.setText(discoverDetail.getContent());
            List<String> smallImageUrls = discoverDetail.getSmallImageUrls();
            final List<String> imageUrls = discoverDetail.getImageUrls();
            final String content = discoverDetail.getContent();
            if (smallImageUrls != null && !smallImageUrls.isEmpty()) {
                holder.mImgsContainerGl.setVisibility(View.VISIBLE);
                int visible = Math.min(smallImageUrls.size(), IMG_COUNT);
                for (int i = 0; i < IMG_COUNT; i++) {
                    if (i < visible) {
                        holder.imageViews[i].setVisibility(View.VISIBLE);
                        int size = getActivity().getWindowManager().getDefaultDisplay().getWidth() - 40;
                        size = size / 3;
                        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(smallImageUrls.get(i)))
                                .setResizeOptions(new ResizeOptions(size, size)).build();
                        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                                .setOldController(holder.imageViews[i].getController())
                                .setImageRequest(imageRequest).build();
                        holder.imageViews[i].setController(controller);
                        holder.imageViews[i].setTag(i + 1);
                        if (itemClickListener != null) {
                            holder.imageViews[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent picIntent = new Intent(getActivity(), PicturesActivity.class);
                                    picIntent.putStringArrayListExtra(Constants.Strings.PICTURE_URLS,
                                            (ArrayList<String>) imageUrls);
                                    picIntent.putExtra(Constants.Strings.PICTURE_DESC, content);
                                    picIntent.putExtra(Constants.Strings.PICTURE_POSITION, (Integer) v.getTag());
                                    getActivity().startActivity(picIntent);
                                }
                            });
                        }
                    } else {
                        holder.imageViews[i].setVisibility(View.GONE);
                    }
                }
            } else {
                holder.mImgsContainerGl.setVisibility(View.GONE);
            }

            holder.mLikeImg.setSelected(discoverDetail.isLike());
            holder.mLikeCountTx.setText(discoverDetail.getLikeCount() == 0 ? "" : String.valueOf(discoverDetail.getLikeCount()));
            holder.mCommentCountTx.setText(discoverDetail.getCommentCount() == 0 ? "没有评论" : discoverDetail.getCommentCount() + "条评论");
            //点赞头像
            if (holder.mLikeHeadsLl.getChildCount() > 0) {
                holder.mLikeHeadsLl.removeAllViews();
            }
            List<SimpleItemData> likeUsers = discoverDetail.getLikeUsers();
            if (likeUsers != null && !likeUsers.isEmpty()) {
                int headCount = Math.min(likeUsers.size(), 8);
                LinearLayout.LayoutParams lp;
                for (int i = 0; i < headCount; i++) {
                    GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(holder.itemView.getResources());
                    builder.setRoundingParams(RoundingParams.asCircle());
                    builder.setFailureImage(holder.itemView.getResources().getDrawable(R.mipmap.ic_default_user_head));
                    builder.setPlaceholderImage(holder.itemView.getResources().getDrawable(R.mipmap.ic_default_user_head));
                    SimpleDraweeView headImg = new SimpleDraweeView(holder.itemView.getContext(), builder.build());
                    lp = new LinearLayout.LayoutParams(60, 60);
                    if (i > 0) {
                        lp.leftMargin = 10;
                    }
                    headImg.setImageURI(Uri.parse(likeUsers.get(i).getName()));
                    headImg.setTag(i);
                    holder.mLikeHeadsLl.addView(headImg, lp);
                    if (itemClickListener != null) {
                        headImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                itemClickListener.onItemClicked(DiscoverDetailAdapter.this, v, holder.getLayoutPosition());
                            }
                        });
                    }
                }
            }

            if (itemClickListener != null) {
                holder.mPhotoImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(DiscoverDetailAdapter.this, v, holder.getLayoutPosition());
                    }
                });
                holder.mForwardImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(DiscoverDetailAdapter.this, v, holder.getLayoutPosition());
                    }
                });
                holder.mSignUpImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(discoverDetail.getProgessState()== ContestProgessState.END){
                            Toast.makeText(getActivity(),"比赛已结束",Toast.LENGTH_SHORT).show();
                        }else {
                            itemClickListener.onItemClicked(DiscoverDetailAdapter.this, v, holder.getLayoutPosition());
                        }
                    }
                });
                holder.mLikeImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(DiscoverDetailAdapter.this, v, holder.getLayoutPosition());
                    }
                });
            }


        }
    }

    //绑定招聘
    private void bindRecruitViewHolder(RecruitHolder recruitHolder, int position) {
        recruitHolder.mTitleTx.setText(discoverDetail.getTitle());
        recruitHolder.mUpdateTimeTx.setText(discoverDetail.getPublishTime());
        recruitHolder.mSalaryTx.setText(discoverDetail.getSalary());
        recruitHolder.mCityTx.setText(discoverDetail.getCity());
        recruitHolder.mOrgHeadImg.setImageURI(Uri.parse(discoverDetail.getPhotoUrl()));
        recruitHolder.mOrgNameTx.setText(discoverDetail.getNickName());
        recruitHolder.mDanceTx.setText(discoverDetail.getDanceName());
        recruitHolder.mContentTx.setText(discoverDetail.getContent());
        recruitHolder.mAddressTx.setText(discoverDetail.getLocation());

        recruitHolder.mOrgContainerRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrgDetailsActivity.class);
                intent.putExtra(Constants.Strings.ORG_LOGO, discoverDetail.getPhotoUrl());
                intent.putExtra(Constants.Strings.ORG_NAME, discoverDetail.getNickName());
                intent.putExtra(Constants.Strings.ORG_ID, discoverDetail.getOrgId());
                intent.putExtra(Constants.Strings.USER_ID, discoverDetail.getUserId());
                getActivity().startActivity(intent);
            }
        });
    }

    class HeadHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView photoCimg;
        private TextView nameTx, titleTx, publishTx;
        private TextView descTx, likeCountTx;
        protected ViewStub differentVs;
        private ImageView forwardImg, signUpImg, likeImg;
        private LinearLayout headContainerLl;
        private TextView commentCountTx;
        private GridLayout imageContainerGL;
        private SimpleDraweeView[] imageViews;

        public HeadHolder(View itemView) {
            super(itemView);
            photoCimg = (SimpleDraweeView) itemView.findViewById(R.id.discover_detail_photo);
            nameTx = (TextView) itemView.findViewById(R.id.discover_detail_name);
            titleTx = (TextView) itemView.findViewById(R.id.discover_detail_title);
            publishTx = (TextView) itemView.findViewById(R.id.discover_detail_release_time);
            descTx = (TextView) itemView.findViewById(R.id.discover_detail_desc);
            differentVs = (ViewStub) itemView.findViewById(R.id.discover_detail_different);
            imageContainerGL = (GridLayout) itemView.findViewById(R.id.discover_detail_images);
            forwardImg = (ImageView) itemView.findViewById(R.id.discover_detail_forward);
            signUpImg = (ImageView) itemView.findViewById(R.id.discover_detail_signIn);
            likeImg = (ImageView) itemView.findViewById(R.id.discover_detail_like);
            likeCountTx = (TextView) itemView.findViewById(R.id.discover_detail_likeCount);
            commentCountTx = (TextView) itemView.findViewById(R.id.discover_detail_commentCount);
            headContainerLl = (LinearLayout) itemView.findViewById(R.id.discover_detail_likeHeads);

            //初始化图片控件
            imageViews = new SimpleDraweeView[IMG_COUNT];
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(itemView.getResources());
            builder.setPlaceholderImage(itemView.getResources().getDrawable(android.R.color.holo_blue_dark));
            int imgSize = getActivity().getWindowManager().getDefaultDisplay().getWidth() - 40;
            imgSize = imgSize / 3;
            GridLayout.LayoutParams lp;
            for (int index = 0; index < IMG_COUNT; index++) {
                imageViews[index] = new SimpleDraweeView(itemView.getContext(), builder.build());
                lp = new GridLayout.LayoutParams();
                lp.width = imgSize;
                lp.height = imgSize;
                if (index % 3 != 0) {
                    lp.leftMargin = 3;
                }
                if (index >= 3) {
                    lp.topMargin = 3;
                }
                imageContainerGL.addView(imageViews[index], lp);
            }
        }
    }

    class ActivityHolder extends HeadHolder {
        private TextView timeStartTx, timeEndTx;
        private TextView addressTx, danceTx, orgNameTx;

        public ActivityHolder(View itemView) {
            super(itemView);
            differentVs.setLayoutResource(R.layout.ly_discover_detail_dif_ac);
            View view = differentVs.inflate();
            timeStartTx = (TextView) view.findViewById(R.id.discover_detail_timeStart);
            timeEndTx = (TextView) view.findViewById(R.id.discover_detail_timeEnd);
            addressTx = (TextView) view.findViewById(R.id.discover_detail_address);
            danceTx = (TextView) view.findViewById(R.id.discover_detail_dance);
            orgNameTx = (TextView) view.findViewById(R.id.discover_detail_orgName);
        }
    }

    //赛事视图
    class ContestHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView mPhotoImg, mPosImg;
        private ImageView mForwardImg, mSignUpImg, mLikeImg;
        private TextView mNickNameTx, mReleaseTimeTx, mTitleTx, mEndTimeTx, mPeopleNumTx, mProgessTx;
        private TextView mUnitTx, mAddressTx, mDanceTypeTx, mStartTimeTx, mInfoEndTimeTx;
        private TextView mContentTx, mLikeCountTx, mCommentCountTx;
        private GridLayout mImgsContainerGl;
        private RelativeLayout mVideoContainerRl;
        private LinearLayout mLikeHeadsLl;
        private SimpleDraweeView[] imageViews;

        public ContestHolder(View itemView) {
            super(itemView);
            mPhotoImg = (SimpleDraweeView) itemView.findViewById(R.id.discover_detail_photo);
            mPosImg = (SimpleDraweeView) itemView.findViewById(R.id.discover_detail_posImg);
            mForwardImg = (ImageView) itemView.findViewById(R.id.discover_detail_forward);
            mSignUpImg = (ImageView) itemView.findViewById(R.id.discover_detail_signIn);
            mLikeImg = (ImageView) itemView.findViewById(R.id.discover_detail_like);
            mNickNameTx = (TextView) itemView.findViewById(R.id.discover_detail_name);
            mReleaseTimeTx = (TextView) itemView.findViewById(R.id.discover_detail_release_time);
            mTitleTx = (TextView) itemView.findViewById(R.id.discover_detail_title);
            mProgessTx = (TextView) itemView.findViewById(R.id.discover_detail_progess);
            mEndTimeTx = (TextView) itemView.findViewById(R.id.discover_detail_endTime);
            mPeopleNumTx = (TextView) itemView.findViewById(R.id.discover_detail_peopleNum);

            mUnitTx = (TextView) itemView.findViewById(R.id.discover_detail_unit);
            mAddressTx = (TextView) itemView.findViewById(R.id.discover_detail_address);
            mDanceTypeTx = (TextView) itemView.findViewById(R.id.discover_detail_dancetype);
            mStartTimeTx = (TextView) itemView.findViewById(R.id.discover_detail_startTime);
            mInfoEndTimeTx = (TextView) itemView.findViewById(R.id.discover_detail_infoendtime);

            mContentTx = (TextView) itemView.findViewById(R.id.discover_detail_content);
            mLikeCountTx = (TextView) itemView.findViewById(R.id.discover_detail_likeCount);
            mCommentCountTx = (TextView) itemView.findViewById(R.id.discover_detail_commentCount);
            mImgsContainerGl = (GridLayout) itemView.findViewById(R.id.discover_detail_imagesContainer);
            mLikeHeadsLl = (LinearLayout) itemView.findViewById(R.id.discover_detail_likeHeads);
            mVideoContainerRl = (RelativeLayout) itemView.findViewById(R.id.discover_detail_video_container);

            //初始化图片控件
            imageViews = new SimpleDraweeView[IMG_COUNT];
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(itemView.getResources());
            builder.setPlaceholderImage(itemView.getResources().getDrawable(android.R.color.holo_blue_dark));
            int imgSize = getActivity().getWindowManager().getDefaultDisplay().getWidth() - 40;
            imgSize = imgSize / 3;
            GridLayout.LayoutParams lp;
            for (int index = 0; index < IMG_COUNT; index++) {
                imageViews[index] = new SimpleDraweeView(itemView.getContext(), builder.build());
                lp = new GridLayout.LayoutParams();
                lp.width = imgSize;
                lp.height = imgSize;
                if (index % 3 != 0) {
                    lp.leftMargin = 3;
                }
                if (index >= 3) {
                    lp.topMargin = 3;
                }
                mImgsContainerGl.addView(imageViews[index], lp);
            }
        }
    }

    //招聘
    class RecruitHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTx, mUpdateTimeTx, mCityTx, mSalaryTx;
        private SimpleDraweeView mOrgHeadImg;
        private TextView mOrgNameTx, mDanceTx;
        private TextView mContentTx, mAddressTx;
        private RelativeLayout mOrgContainerRl;

        public RecruitHolder(View itemView) {
            super(itemView);
            mTitleTx = (TextView) itemView.findViewById(R.id.discover_detail_title);
            mUpdateTimeTx = (TextView) itemView.findViewById(R.id.discover_detail_updatetime);
            mCityTx = (TextView) itemView.findViewById(R.id.discover_detail_city);
            mSalaryTx = (TextView) itemView.findViewById(R.id.discover_detail_salary);
            mOrgNameTx = (TextView) itemView.findViewById(R.id.discover_detail_nickname);
            mDanceTx = (TextView) itemView.findViewById(R.id.discover_detail_dancetype);
            mContentTx = (TextView) itemView.findViewById(R.id.discover_detail_content);
            mAddressTx = (TextView) itemView.findViewById(R.id.discover_detail_address);
            mOrgHeadImg = (SimpleDraweeView) itemView.findViewById(R.id.discover_detail_orghead);

            mOrgContainerRl = (RelativeLayout) itemView.findViewById(R.id.discover_detail_orgcontainer);
        }
    }

    class OtherHolder extends HeadHolder {
        private TextView danceTx, danceNameTx;

        public OtherHolder(View itemView) {
            super(itemView);
            differentVs.setLayoutResource(R.layout.ly_discover_detail_dif_to);
            View view = differentVs.inflate();
            danceTx = (TextView) view.findViewById(R.id.discover_detail_dance);
            danceNameTx = (TextView) view.findViewById(R.id.discover_detail_dancename);
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
