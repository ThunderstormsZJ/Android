package cn.spinsoft.wdq.user.frag;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jingchen.pulltorefresh.PullToRefreshLayout;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.browse.biz.BrowseHandler;
import cn.spinsoft.wdq.user.UserDetailsActivity;
import cn.spinsoft.wdq.user.biz.DancerInfo;
import cn.spinsoft.wdq.user.biz.DancerListWithInfo;
import cn.spinsoft.wdq.user.widget.FriendGridListAdater;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.widget.DropDownSpinner;
import cn.spinsoft.wdq.widget.EmptyView;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by zhoujun on 15/11/11.
 */
public class FriendListFrag extends BaseFragment implements RecyclerItemClickListener, Handler.Callback,
        DropDownSpinner.OnItemSelectedListener, PullToRefreshLayout.OnPullListener {
    private static final String TAG = FriendListFrag.class.getSimpleName();

    //    private Spinner mDistanceSp, mTypeSp, mSexSp, mAgeSp;
//    private DropDownSpinner mDistanceSp, mTypeSp, mSexSp, mAgeSp;
    private RecyclerView mContentRv;
    private PullToRefreshLayout mPtrl;

    //    private FriendListAdapter mFriendAdapter;
    private FriendGridListAdater mFriendAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_friend_list;
    }

    @Override
    protected void initViewAndListener(View root, Bundle savedInstanceState) {
       /* mDistanceSp = (DropDownSpinner) root.findViewById(R.id.friend_main_distance);
        mTypeSp = (DropDownSpinner) root.findViewById(R.id.friend_main_type);
        mSexSp = (DropDownSpinner) root.findViewById(R.id.friend_main_sex);
        mAgeSp = (DropDownSpinner) root.findViewById(R.id.friend_main_age);*/
        mPtrl = (PullToRefreshLayout) root.findViewById(R.id.friend_main_content);
        mPtrl.setOnPullListener(this);
        mPtrl.setEmptyView(new EmptyView(root.getContext()));
        mContentRv = (RecyclerView) mPtrl.getPullableView();

//        mContentRv.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));
        mContentRv.setLayoutManager(new GridLayoutManager(root.getContext(), 2, LinearLayoutManager.VERTICAL, false));
//        mFriendAdapter = new FriendListAdapter(null, this);
        mFriendAdapter = new FriendGridListAdater(null, this, getActivity());
        mContentRv.setAdapter(mFriendAdapter);

       /* mTypeSp.setmListAdpter(new SimpleTextBaseAdapter(SimpleItemDataUtils.getDanceTypeList(false, getActivity())));
        mDistanceSp.setmListAdpter(new SimpleTextBaseAdapter(SimpleItemDataUtils.getDistances()));
        mSexSp.setmListAdpter(new SimpleTextBaseAdapter(SimpleItemDataUtils.getSexes(true)));
        mAgeSp.setmListAdpter(new SimpleTextBaseAdapter(SimpleItemDataUtils.getAgeRanges(true)));

        mDistanceSp.setOnItemSelectedListener(this);
        mTypeSp.setOnItemSelectedListener(this);
        mSexSp.setOnItemSelectedListener(this);
        mAgeSp.setOnItemSelectedListener(this);*/
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler.addCallback(BrowseHandler.CHILD_FRIEND, this);

//        mHandler.sendEmptyMessage(R.id.msg_get_dance_type);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPtrl.autoRefresh();
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, int position) {
        switch (view.getId()) {
          /*  case R.id.friend_list_item_option:
                if (SecurityUtils.isUserValidity(getActivity(), BrowseHandler.watcherUserId)) {
                    boolean att = (boolean) view.getTag();
                    view.setTag(!att);
                    mFriendAdapter.notifyItemAttentionChanged(!att, position);
                    BrowseHandler.Status.Friend.ownerUserId = mFriendAdapter.getItem(position).getUserId();
                    mHandler.sendEmptyMessage(R.id.msg_report_friend_att);
                }
                break;*/
            case R.id.friend_list_item_photo:
            default:
                DancerInfo dancerInfo = mFriendAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), UserDetailsActivity.class);
                intent.putExtra(Constants.Strings.USER_ID, dancerInfo.getUserId());
                intent.putExtra(Constants.Strings.USER_PHOTO, dancerInfo.getPhotoUrl());
                intent.putExtra(Constants.Strings.USER_NAME, dancerInfo.getNickName());
                intent.putExtra(Constants.Strings.USER_SIGN, dancerInfo.getSignature());
                intent.putExtra(Constants.Strings.USER_ATTEN, dancerInfo.getAttention().getValue());
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.msg_friend_list_got:
                if (msg.obj != null) {
                    DancerListWithInfo listWithInfo = (DancerListWithInfo) msg.obj;
                    if (BrowseHandler.Status.Friend.pageIdx == 1) {
                        mFriendAdapter.setAdapterDataList(listWithInfo.getDataList());
                        mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                    } else {
                        mFriendAdapter.addAdapterDataList(listWithInfo.getDataList());
                        mPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    }
                } else {
                    if (BrowseHandler.Status.Friend.pageIdx == 1) {
                        mPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                        mFriendAdapter.setAdapterDataList(null);
                    } else {
                        mPtrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                    }
                }
                if (mFriendAdapter.getItemCount() <= 0) {
                    mPtrl.showEmptyView(true);
                } else {
                    mPtrl.showEmptyView(false);
                }
                break;
            case R.id.msg_dance_type_got:
//                if (msg.obj != null) {
//                    List<SimpleItemData> typeBeanList = (List<SimpleItemData>) msg.obj;
//                    mTypeSp.setAdapter(new SimpleTextBaseAdapter(typeBeanList));
//                }
                break;
            case R.id.msg_friend_att_reported:
                if (msg.obj != null) {
                    SimpleResponse success = (SimpleResponse) msg.obj;
                    Toast.makeText(getActivity(), success.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "操作失败,刷新后请重试", Toast.LENGTH_SHORT).show();
                }
                // TODO: 15/11/20 do nothing 
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onItemSelected(ListAdapter parent, TextView view, int position, long id) {
        /*if (parent instanceof SimpleTextBaseAdapter) {
            SimpleItemData item = ((SimpleTextBaseAdapter) parent).getItem(position);
            BrowseHandler.Status.Friend.pageIdx = 1;
            if (parent == mDistanceSp.getmListAdpter()) {
                if (item.getId() == -1) {
                    mDistanceSp.editText.setText("附近");
                }
                BrowseHandler.Status.Friend.distance = item.getId();
            } else if (parent == mTypeSp.getmListAdpter()) {
                if (item.getId() == -1) {
                    mTypeSp.editText.setText("舞种");
                }
                BrowseHandler.Status.Friend.danceId = item.getId();
            } else if (parent == mSexSp.getmListAdpter()) {
                if (item.getId() == 0) {
                    mSexSp.editText.setText("性别");
                }
                BrowseHandler.Status.Friend.sex = item.getId();
            } else if (parent == mAgeSp.getmListAdpter()) {
                if (item.getId() == -1) {
                    mAgeSp.editText.setText("年龄");
                }
                BrowseHandler.Status.Friend.ageRange = item.getId();
            }
            mHandler.sendEmptyMessage(R.id.msg_friend_get_list);
        }*/
    }

    public void sexFilterChoose(View v) {
        switch (v.getId()) {
            case R.id.friend_main_sex_man:
                BrowseHandler.Status.Friend.sex = 2;
                break;
            case R.id.friend_main_sex_woman:
                BrowseHandler.Status.Friend.sex = 1;
                break;
            case R.id.friend_main_sex_no:
                BrowseHandler.Status.Friend.sex = 0;
                break;
        }
        mHandler.sendEmptyMessage(R.id.msg_friend_get_list);
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        BrowseHandler.Status.Friend.pageIdx = 1;
        mHandler.sendEmptyMessage(R.id.msg_friend_get_list);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        BrowseHandler.Status.Friend.pageIdx++;
        mHandler.sendEmptyMessage(R.id.msg_friend_get_list);
    }
}
