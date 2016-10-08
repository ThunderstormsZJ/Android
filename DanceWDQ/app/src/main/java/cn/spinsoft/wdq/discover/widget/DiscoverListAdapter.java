package cn.spinsoft.wdq.discover.widget;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.album.PicturesActivity;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.browse.biz.BrowseHandler;
import cn.spinsoft.wdq.discover.SignListActivity;
import cn.spinsoft.wdq.discover.biz.DiscoverItemBean;
import cn.spinsoft.wdq.enums.DiscoverType;
import cn.spinsoft.wdq.org.OrgDetailsActivity;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.StringUtils;
import cn.spinsoft.wdq.widget.EquallyWidthLabel;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by zhoujun on 15/11/24.
 */
public class DiscoverListAdapter extends BaseRecycleAdapter<DiscoverItemBean> {
    private static final String TAG = DiscoverListAdapter.class.getSimpleName();
    private static final int TYPE_WITH_IMG = 1 << 1;
    private static final int TYPE_WITH_POS = 1 << 2;//带有海报
    private static final int TYPE_RECRUIT = 1 << 3;//招聘视图
    private static final int IMG_COUNT = 6;//默认显示图片的个数
    private String listType = Constants.Strings.NORMAL_MODE;

    public void setListType(String listType) {
        this.listType = listType;
    }

    public DiscoverListAdapter(List<DiscoverItemBean> adapterDataList, RecyclerItemClickListener itemClickListener, Activity activity) {
        super(adapterDataList, itemClickListener, activity);
    }

    public DiscoverListAdapter(List<DiscoverItemBean> adapterDataList, RecyclerItemClickListener itemClickListener) {
        super(adapterDataList, itemClickListener);
    }

    public void notifyItemLikeChanged(boolean like, int position) {
        if (position < getItemCount()) {
            DiscoverItemBean videoBean = getItem(position);
            videoBean.setIsLike(like);
            videoBean.setLikeCount(like ? videoBean.getLikeCount() + 1 : videoBean.getLikeCount() - 1);
            notifyItemChanged(position);
        }
    }

    public void notifyItemForwardChanged(int position) {
        if (position < getItemCount()) {
            DiscoverItemBean discoverBean = getItem(position);
            discoverBean.setForwardCount(discoverBean.getForwardCount() + 1);
            notifyItemChanged(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        DiscoverItemBean itemBean = adapterDataList.get(position);
        switch (itemBean.getType()) {
            case CONTEST://赛事
                return TYPE_WITH_POS;
            case RECRUIT://招聘
                return TYPE_RECRUIT;
            default:
                if (itemBean.getSmallImgs() == null || itemBean.getSmallImgs().isEmpty()
                        || TextUtils.isEmpty(itemBean.getSmallImgs().get(0))) {
                    return super.getItemViewType(position);
                } else {
                    return TYPE_WITH_IMG;
                }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_WITH_IMG) {
            return new ViewHolderImg(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ly_discover_list_item_img, parent, false));
        } else if (viewType == TYPE_WITH_POS) {
            return new ViewHolderPos(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ly_discover_list_item_pos, parent, false));
        } else if (viewType == TYPE_RECRUIT) {
            return new ViewHolderRecruit(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ly_discover_list_item_recruit, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ly_discover_list_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final DiscoverItemBean itemBean = adapterDataList.get(position);

        this.listType = Constants.Strings.NORMAL_MODE;
        if (itemBean.getUserId() == BrowseHandler.watcherUserId) {
            this.listType = Constants.Strings.DELETE_MODE;
        }

        if (viewHolder.getItemViewType() == TYPE_WITH_IMG) {
            bindViewHolder((ViewHolder) viewHolder, itemBean);
            bindImgViewHolder((ViewHolderImg) viewHolder, itemBean);
        } else if (viewHolder.getItemViewType() == TYPE_WITH_POS) {
            bindPosViewHolder((ViewHolderPos) viewHolder, itemBean);
        } else if (viewHolder.getItemViewType() == TYPE_RECRUIT) {
            bindRecruitViewHolder((ViewHolderRecruit) viewHolder, itemBean);
        } else {
            bindViewHolder((ViewHolder) viewHolder, itemBean);
        }

    }


    public void bindViewHolder(final ViewHolder holder, final DiscoverItemBean itemBean) {
        holder.photoCimg.setImageURI(Uri.parse(itemBean.getPhotoUrl()));
        holder.nickNameTx.setText(itemBean.getNickName());
        holder.titleTx.setText(itemBean.getTitle());
        holder.timeTx.setText(itemBean.getTime());
        holder.contentTx.setText(itemBean.getContent());

        if (itemBean.getType() == DiscoverType.ACTIVITY) {
            holder.mPersonNumTx.setVisibility(View.VISIBLE);
            holder.mPersonNumTx.setText("报名人数(" + String.valueOf(itemBean.getPersonNum()) + ")");
            if (itemBean.getUserId() == BrowseHandler.watcherUserId) {
                holder.mPersonNumTx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), SignListActivity.class);
                        intent.putExtra(Constants.Strings.DISCOVER_EVENT_ID, itemBean.getEventId());
                        intent.putExtra(Constants.Strings.DISCOVER_TYPE_ID, itemBean.getType().getValue());
                        getActivity().startActivity(intent);
                    }
                });
            }
        } else {
            holder.mPersonNumTx.setVisibility(View.INVISIBLE);
        }
        holder.delBtn.setVisibility(View.GONE);
        if (listType == Constants.Strings.DELETE_MODE) {
            holder.delBtn.setVisibility(View.VISIBLE);
            holder.delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(DiscoverListAdapter.this, v, holder.getLayoutPosition());
                }
            });
        }

        holder.forwardTx.setText(itemBean.getForwardCount() == 0 ? "转发" : String.valueOf(itemBean.getForwardCount()));
        holder.commentTx.setText(itemBean.getCommentCount() == 0 ? "评论" : String.valueOf(itemBean.getCommentCount()));
        holder.likeTx.setText(itemBean.getLikeCount() == 0 ? "赞" : String.valueOf(itemBean.getLikeCount()));
        holder.likeTx.setSelected(itemBean.isLike());

        if (itemClickListener != null) {
            holder.photoCimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(DiscoverListAdapter.this, v, holder.getLayoutPosition());
                }
            });
            holder.likeTx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(DiscoverListAdapter.this, v, holder.getLayoutPosition());
                }
            });
            holder.forwardTx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(DiscoverListAdapter.this, v, holder.getLayoutPosition());
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(DiscoverListAdapter.this, v, holder.getLayoutPosition());
                }
            });
        }
    }

    public void bindImgViewHolder(ViewHolderImg holder, DiscoverItemBean itemBean) {
        //显示6张图
        final List<String> imgUrls = itemBean.getSmallImgs();
        final String content = itemBean.getContent();
        int size = imgUrls.size() > IMG_COUNT ? IMG_COUNT : imgUrls.size();
        int visible = Math.min(size, IMG_COUNT);
        for (int i = 0; i < IMG_COUNT; i++) {
            if (i < visible) {
                holder.imageViews[i].setVisibility(View.VISIBLE);
                int imgSize = getActivity().getWindowManager().getDefaultDisplay().getWidth() - 40;
                imgSize = imgSize / 3;
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imgUrls.get(i)))
                        .setResizeOptions(new ResizeOptions(imgSize, imgSize)).build();
                PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                        .setOldController(holder.imageViews[i].getController())
                        .setImageRequest(request).build();//使用control方式便于内存的重用
                holder.imageViews[i].setController(controller);
                holder.imageViews[i].setTag(i + 1);//图片的位置
                if (itemClickListener != null) {
                    holder.imageViews[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                                itemClickListener.onItemClicked(DiscoverListAdapter.this, v, holder.getLayoutPosition());
                            Intent picIntent = new Intent(getActivity(), PicturesActivity.class);
                            picIntent.putStringArrayListExtra(Constants.Strings.PICTURE_URLS, (ArrayList<String>) imgUrls);//大图地址
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
    }

    //海报
    public void bindPosViewHolder(final ViewHolderPos holder, DiscoverItemBean itemBean) {
        if (TextUtils.isEmpty(StringUtils.getNoNullString(itemBean.getPosSmallImg()))) {
            holder.posImg.setImageURI(Uri.parse(itemBean.getSmallImgs().get(0)));
        } else {
            holder.posImg.setImageURI(Uri.parse(itemBean.getPosSmallImg()));
        }
        holder.mDeleteIBtn.setVisibility(View.GONE);
        if (listType == Constants.Strings.DELETE_MODE) {
            holder.mDeleteIBtn.setVisibility(View.VISIBLE);
            holder.mDeleteIBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(DiscoverListAdapter.this, v, holder.getLayoutPosition());
                }
            });
        }
        holder.mPosTitleTX.setText(itemBean.getTitle());
        if(itemBean.getProgessState()!=null){
            holder.mProgessTx.setText(itemBean.getProgessState().getDesc());
        }
//        holder.mPosTitleTX.setSelected(!itemBean.isProgess());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked(DiscoverListAdapter.this, v, holder.getLayoutPosition());
            }
        });
    }

    //招聘
    private void bindRecruitViewHolder(final ViewHolderRecruit holderRecruit, final DiscoverItemBean discoverItem) {
        holderRecruit.mHeadImg.setImageURI(Uri.parse(discoverItem.getPhotoUrl()));
        holderRecruit.mTitleTx.setText(discoverItem.getTitle());
        holderRecruit.mSalaryTx.setText(discoverItem.getSalary());
        holderRecruit.mDescTx.setText(discoverItem.getCity() + "-" + discoverItem.getNickName());
        holderRecruit.mDeleteImg.setVisibility(View.GONE);
        if (listType == Constants.Strings.DELETE_MODE) {
            holderRecruit.mDeleteImg.setVisibility(View.VISIBLE);
            holderRecruit.mDeleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(DiscoverListAdapter.this, v, holderRecruit.getLayoutPosition());
                }
            });
        }

        holderRecruit.mHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrgDetailsActivity.class);
                intent.putExtra(Constants.Strings.ORG_LOGO, discoverItem.getPhotoUrl());
                intent.putExtra(Constants.Strings.ORG_NAME, discoverItem.getNickName());
                intent.putExtra(Constants.Strings.ORG_ID, discoverItem.getOrgId());
                intent.putExtra(Constants.Strings.USER_ID, discoverItem.getUserId());
                getActivity().startActivity(intent);
            }
        });

        holderRecruit.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "itemview", Toast.LENGTH_SHORT).show();
                itemClickListener.onItemClicked(DiscoverListAdapter.this, v, holderRecruit.getLayoutPosition());
            }
        });
    }


    //默认视图
    class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView photoCimg;
        private TextView nickNameTx, titleTx, timeTx/*, contentTx*/, mPersonNumTx;
        private EquallyWidthLabel likeTx, forwardTx, commentTx;
        private ImageButton delBtn;
        private EllipsizingTextView contentTx;

        public ViewHolder(View itemView) {
            super(itemView);
            photoCimg = (SimpleDraweeView) itemView.findViewById(R.id.discover_list_item_photo);
            nickNameTx = (TextView) itemView.findViewById(R.id.discover_list_item_nickname);
            titleTx = (TextView) itemView.findViewById(R.id.discover_list_item_title);
            timeTx = (TextView) itemView.findViewById(R.id.discover_list_item_time);
            contentTx = (EllipsizingTextView) itemView.findViewById(R.id.discover_list_item_content);
            mPersonNumTx = (TextView) itemView.findViewById(R.id.discover_list_item_personnum);
            likeTx = (EquallyWidthLabel) itemView.findViewById(R.id.discover_list_item_like);
            forwardTx = (EquallyWidthLabel) itemView.findViewById(R.id.discover_list_item_forward);
            commentTx = (EquallyWidthLabel) itemView.findViewById(R.id.discover_list_item_comment);
            delBtn = (ImageButton) itemView.findViewById(R.id.discover_list_item_delete);

            contentTx.setMaxLines(3);
        }
    }

    //带有图片的视图
    class ViewHolderImg extends ViewHolder {
        private SimpleDraweeView[] imageViews;
        private GridLayout imageViewsGL;

        public ViewHolderImg(View itemView) {
            super(itemView);
            imageViewsGL = (GridLayout) itemView.findViewById(R.id.discover_list_item_images);
            //初始化图片个数
            imageViews = new SimpleDraweeView[IMG_COUNT];
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(itemView.getResources());
            builder.setPlaceholderImage(itemView.getResources().getDrawable(R.mipmap.ic_picture_loading_fail));
            int imgSize = getActivity().getWindowManager().getDefaultDisplay().getWidth() - 40;
            imgSize = imgSize / 3;//平均显示各张图片
            GridLayout.LayoutParams lp;
            for (int index = 0; index < IMG_COUNT; index++) {
                lp = new GridLayout.LayoutParams();
                lp.width = imgSize;
                lp.height = imgSize;
                if (index % 3 != 0) {
                    lp.leftMargin = 10;
                }
                if (index >= 3) {
                    lp.topMargin = 10;
                }
                imageViews[index] = new SimpleDraweeView(itemView.getContext(), builder.build());
                imageViewsGL.addView(imageViews[index], lp);
            }
        }
    }

    //带海报的赛事视图
    class ViewHolderPos extends RecyclerView.ViewHolder {
        private SimpleDraweeView posImg;
        private TextView mPosTitleTX;
        private TextView mProgessTx;
        private ImageButton mDeleteIBtn;

        public ViewHolderPos(View itemView) {
            super(itemView);
            posImg = (SimpleDraweeView) itemView.findViewById(R.id.discover_contest_posImg);
            mPosTitleTX = (TextView) itemView.findViewById(R.id.discover_contest_title);
            mProgessTx = (TextView) itemView.findViewById(R.id.discover_contest_progess);
            mDeleteIBtn = (ImageButton) itemView.findViewById(R.id.discover_contest_delete);
        }
    }

    //招聘视图
    class ViewHolderRecruit extends RecyclerView.ViewHolder {
        private SimpleDraweeView mHeadImg;
        private TextView mTitleTx, mDescTx, mSalaryTx;
        private ImageView mDeleteImg;

        public ViewHolderRecruit(View itemView) {
            super(itemView);
            mHeadImg = (SimpleDraweeView) itemView.findViewById(R.id.discover_recruit_head);
            mTitleTx = (TextView) itemView.findViewById(R.id.discover_recruit_title);
            mDescTx = (TextView) itemView.findViewById(R.id.discover_recruit_desc);
            mSalaryTx = (TextView) itemView.findViewById(R.id.discover_recruit_salary);
            mDeleteImg = (ImageView) itemView.findViewById(R.id.discover_recruit_delete);
        }
    }
}
