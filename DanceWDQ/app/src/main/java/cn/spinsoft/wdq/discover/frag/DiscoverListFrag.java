package cn.spinsoft.wdq.discover.frag;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.browse.biz.BrowseHandler;
import cn.spinsoft.wdq.discover.DiscoverDetailActivity;
import cn.spinsoft.wdq.discover.biz.DiscoverItemBean;
import cn.spinsoft.wdq.discover.biz.DiscoverListWithInfo;
import cn.spinsoft.wdq.discover.widget.DiscoverListAdapter;
import cn.spinsoft.wdq.effect.AnimatorEffect;
import cn.spinsoft.wdq.enums.DiscoverType;
import cn.spinsoft.wdq.org.OrgDetailsActivity;
import cn.spinsoft.wdq.user.UserDetailsActivity;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.SecurityUtils;
import cn.spinsoft.wdq.utils.SimpleItemDataUtils;
import cn.spinsoft.wdq.utils.UrlManager;
import cn.spinsoft.wdq.widget.ConfirmDialog;
import cn.spinsoft.wdq.widget.DropDownSpinner;
import cn.spinsoft.wdq.widget.EmptyView;
import cn.spinsoft.wdq.widget.RadioGroup;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;
import cn.spinsoft.wdq.widget.ShareBoardDialog;
import cn.spinsoft.wdq.widget.SimpleTextBaseAdapter;

/**
 * Created by zhoujun on 15/11/3.
 */
public class DiscoverListFrag extends BaseFragment implements RadioGroup.OnCheckedChangeListener,
        Handler.Callback, RecyclerItemClickListener, DropDownSpinner.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener, PullToRefreshLayout.OnPullListener, ShareBoardDialog.ShareBoardDiaListener {
    private static final String TAG = DiscoverListFrag.class.getSimpleName();
    private CheckBox mAttentionCb, mSortCb;
    //    private Spinner mTypeSp /*mSortSp*/;
    private DropDownSpinner mTypeSp;
    private RadioGroup mLabelsRg;
    private View mLabelsSlide;
    private PullToRefreshLayout mPtrl;
    private RecyclerView mContentRv;

    private int mForwardPosition = 0;

    private DiscoverListAdapter mListAdapter;
    private DiscoverItemBean itemBean;
    private UMShareListener mUmShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
            mHandler.sendEmptyMessage(R.id.msg_report_forward_discover);
            mListAdapter.notifyItemForwardChanged(mForwardPosition);
            Toast.makeText(getContext(), "分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Toast.makeText(getContext(), "分享失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            Toast.makeText(getContext(), "取消分享", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.frag_discover_list;
    }

    @Override
    protected void initViewAndListener(View root, Bundle savedInstanceState) {
        mLabelsRg = (RadioGroup) root.findViewById(R.id.discover_labels);
        mLabelsSlide = root.findViewById(R.id.discover_slide);
        mPtrl = (PullToRefreshLayout) root.findViewById(R.id.discover_content);
        mPtrl.setEmptyView(new EmptyView(root.getContext()));
        mPtrl.setOnPullListener(this);
        mContentRv = (RecyclerView) mPtrl.getPullableView();

        mTypeSp = (DropDownSpinner) root.findViewById(R.id.discover_dance_type);
        mAttentionCb = (CheckBox) root.findViewById(R.id.discover_attention);
        mSortCb = (CheckBox) root.findViewById(R.id.discover_sort);
//        mSortSp = (Spinner) root.findViewById(R.id.discover_sort);

        mContentRv.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));
        mListAdapter = new DiscoverListAdapter(null, this, getActivity());
        mContentRv.setAdapter(mListAdapter);

        mAttentionCb.setOnCheckedChangeListener(this);
        mSortCb.setOnCheckedChangeListener(this);
        mLabelsRg.setOnCheckedChangeListener(this);

        mTypeSp.setmListAdpter(new SimpleTextBaseAdapter(SimpleItemDataUtils.getDanceTypeList(false, getActivity())));
//        mSortSp.setAdapter(new SimpleTextBaseAdapter(SimpleItemDataUtils.getSortList()));
        mTypeSp.setOnItemSelectedListener(this);
//        mSortSp.setOnItemSelectedListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler.addCallback(BrowseHandler.CHILD_DISCOVER, this);
        BrowseHandler.Status.Discover.sort = 0;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPtrl.autoRefresh();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        View view = group.findViewById(checkedId);
        AnimatorEffect.smoothHorizontalSlideTo(mLabelsSlide, view);

        mContentRv.smoothScrollToPosition(0);
        DiscoverType type;
        switch (checkedId) {
            case R.id.discover_activities:
                type = DiscoverType.ACTIVITY;
                break;
            case R.id.discover_topics:
                type = DiscoverType.TOPIC;
                break;
            case R.id.discover_contest:
                type = DiscoverType.CONTEST;
                break;
            case R.id.discover_recruit:
                type = DiscoverType.RECRUIT;
                break;
            case R.id.discover_other:
                type = DiscoverType.OTHER;
                break;
            default:
                type = DiscoverType.UNKNOWN;
                break;
        }
        BrowseHandler.Status.Discover.typeId = type.getValue();
        mPtrl.autoRefresh();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.msg_discover_list_got:
            case R.id.msg_discover_att_list_got:
                if (msg.obj != null) {
                    DiscoverListWithInfo listWithInfo = (DiscoverListWithInfo) msg.obj;
                    if (BrowseHandler.Status.Discover.pageNum == 1) {
                        mListAdapter.setAdapterDataList(listWithInfo.getDataList());
                        mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                    } else {
                        mListAdapter.addAdapterDataList(listWithInfo.getDataList());
                        mPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    }
                } else {
                    if (BrowseHandler.Status.Discover.pageNum == 1) {
                        mPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                        mListAdapter.setAdapterDataList(null);
                    } else {
                        mPtrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                    }
                }
                if (mListAdapter.getItemCount() <= 0) {
                    mPtrl.showEmptyView(true);
                } else {
                    mPtrl.showEmptyView(false);
                }
                break;
            case R.id.msg_like_reported_discover:
                if (msg.obj != null) {
                    SimpleResponse response = (SimpleResponse) msg.obj;
//                    Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, final int position) {
        itemBean = mListAdapter.getItem(position);
        switch (view.getId()) {
            case R.id.discover_list_item_photo:
                Intent userIntent;
                if (itemBean.getOrgId() > 0) {
                    userIntent = new Intent(getActivity(), OrgDetailsActivity.class);
                    userIntent.putExtra(Constants.Strings.ORG_ID, itemBean.getOrgId());
                    userIntent.putExtra(Constants.Strings.USER_ID, itemBean.getUserId());
                    userIntent.putExtra(Constants.Strings.ORG_LOGO, itemBean.getPhotoUrl());
                    userIntent.putExtra(Constants.Strings.ORG_NAME, itemBean.getNickName());
                } else {
                    userIntent = new Intent(getActivity(), UserDetailsActivity.class);
                    userIntent.putExtra(Constants.Strings.USER_ID, itemBean.getUserId());
                    userIntent.putExtra(Constants.Strings.USER_PHOTO, itemBean.getPhotoUrl());
                    userIntent.putExtra(Constants.Strings.USER_NAME, itemBean.getNickName());
                }

                startActivity(userIntent);
                break;
            case R.id.discover_list_item_like:
                if (SecurityUtils.isUserValidity(getActivity(), BrowseHandler.watcherUserId)) {
                    mListAdapter.notifyItemLikeChanged(!itemBean.isLike(), position);
                    BrowseHandler.Status.Discover.eventId = itemBean.getEventId();
                    BrowseHandler.Status.Discover.ownerId = itemBean.getUserId();
                    mHandler.sendEmptyMessage(R.id.msg_report_like_discover);
                }
                break;
            case R.id.discover_list_item_forward://转发
                if (SecurityUtils.isUserValidity(getActivity(), BrowseHandler.watcherUserId)) {
                    new ShareBoardDialog(getContext(), this).setShare_medias(Constants.Arrays.SHARDBOARD).show();
                    BrowseHandler.Status.Discover.eventId = itemBean.getEventId();
                    BrowseHandler.Status.Discover.ownerId = itemBean.getUserId();
//                    itemBean.setForwardCount(itemBean.getForwardCount() + 1);
                    mForwardPosition = position;
                }
                break;
            case R.id.discover_recruit_delete:
            case R.id.discover_list_item_delete:
            case R.id.discover_contest_delete:
                BrowseHandler.Status.Discover.eventId = itemBean.getEventId();
                BrowseHandler.Status.Discover.typeId = itemBean.getType().getValue();
                new ConfirmDialog(getContext(), ConfirmDialog.Type.DELETE, new ConfirmDialog.OnConfirmClickListenter() {
                    @Override
                    public void onConfirmClick(View v) {
                        if (v.getId() == R.id.dia_confirm_confirm) {
                            mListAdapter.removeItem(position);
                            mHandler.sendEmptyMessage(R.id.msg_discover_delete_by_id);
                        }
                    }
                }).show();
                break;
            default:
                Intent intent = new Intent(getActivity(), DiscoverDetailActivity.class);
                intent.putExtra(Constants.Strings.DISCOVER_TYPE_ID, itemBean.getType().getValue());
                intent.putExtra(Constants.Strings.DISCOVER_EVENT_ID, itemBean.getEventId());
                intent.putExtra(Constants.Strings.USER_ID, itemBean.getUserId());
                startActivity(intent);
                break;
        }
    }

    //分享面板点击事件
    @Override
    public void shareBoardOnclickListener(View v) {
        UMImage umImage = null;
        if (itemBean.getSmallImgs() != null && itemBean.getSmallImgs().size() > 0) {
            umImage = new UMImage(getActivity(), itemBean.getSmallImgs().get(0));
        }
        new ShareAction(getActivity()).setPlatform((SHARE_MEDIA) v.getTag())
                .setCallback(mUmShareListener)
                .withMedia(umImage)
                .withTargetUrl(UrlManager.getUrl(UrlManager.UrlName.DISCOVER_SHARE_GOTO_PAGE)
                        + "?typeId=" + itemBean.getForwardId() + "&f_type=" + itemBean.getForwarType())
                .withTitle(itemBean.getTitle())
                .withText(itemBean.getContent())
                .share();
        BrowseHandler.Status.Discover.forwarWay = v.getTag().toString();
    }

    @Override
    public void onItemSelected(ListAdapter adapter, TextView view, int position, long id) {
        if (adapter instanceof SimpleTextBaseAdapter) {
            SimpleTextBaseAdapter textBaseAdapter = (SimpleTextBaseAdapter) adapter;
            SimpleItemData simpleItemData = textBaseAdapter.getItem(position);
            if (textBaseAdapter == mTypeSp.getmListAdpter()) {
                if (simpleItemData.getId() == -1) {
                    mTypeSp.editText.setText("舞种");
                }
                BrowseHandler.Status.Discover.danceType = simpleItemData.getId();
            } /*else if (textBaseAdapter == mSortSp.getAdapter()) {
                BrowseHandler.Status.Discover.sort = simpleItemData.getId();
                if (simpleItemData.getId() >= 0) {
                    mSortSp.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_discover_sort_purple, 0);
                } else {
                    mSortSp.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_discover_sort_gray, 0);
                }
            }*/
            BrowseHandler.Status.Discover.pageNum = 1;
            mHandler.sendEmptyMessage(R.id.msg_discover_get_list);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        BrowseHandler.Status.Discover.pageNum = 1;
        if (buttonView.getId() == R.id.discover_attention) {
            if (SecurityUtils.isUserValidity(getContext(), BrowseHandler.watcherUserId)) {
                if (isChecked) {
                    mHandler.sendEmptyMessage(R.id.msg_discover_att_get_list);
                } else {
                    mHandler.sendEmptyMessage(R.id.msg_discover_get_list);
                }
            } else {
                mAttentionCb.setChecked(false);
            }
        } else if (buttonView.getId() == R.id.discover_sort) {
            if (isChecked) {
                BrowseHandler.Status.Discover.sort = 1;
            } else {
                BrowseHandler.Status.Discover.sort = 0;
            }
            mHandler.sendEmptyMessage(R.id.msg_discover_get_list);
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        BrowseHandler.Status.Discover.pageNum = 1;
        if (mAttentionCb.isChecked()) {
            mHandler.sendEmptyMessage(R.id.msg_discover_att_get_list);
        } else {
            mHandler.sendEmptyMessage(R.id.msg_discover_get_list);
        }
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        BrowseHandler.Status.Discover.pageNum++;
        if (mAttentionCb.isChecked()) {
            mHandler.sendEmptyMessage(R.id.msg_discover_att_get_list);
        } else {
            mHandler.sendEmptyMessage(R.id.msg_discover_get_list);
        }
    }

}
