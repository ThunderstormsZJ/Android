package cn.spinsoft.wdq.user.frag;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jingchen.pulltorefresh.PullToRefreshLayout;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.org.OrgDetailsActivity;
import cn.spinsoft.wdq.user.UserDetailsActivity;
import cn.spinsoft.wdq.user.biz.DancerInfo;
import cn.spinsoft.wdq.user.biz.DancerListWithInfo;
import cn.spinsoft.wdq.user.biz.UserHandler;
import cn.spinsoft.wdq.user.widget.UserItemAdapter;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.SecurityUtils;
import cn.spinsoft.wdq.video.biz.DetailParser;
import cn.spinsoft.wdq.widget.EmptyView;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by zhoujun on 15/12/2.
 */
public class AttentionFrag extends BaseFragment implements Handler.Callback, RecyclerItemClickListener,
        PullToRefreshLayout.OnPullListener {
    private static final String TAG = AttentionFrag.class.getSimpleName();

    private PullToRefreshLayout mPtrl;
    private UserItemAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_recycler_child;
    }

    @Override
    protected void initViewAndListener(View root, Bundle savedInstanceState) {
        mPtrl = (PullToRefreshLayout) root.findViewById(R.id.recycler_child_content);
        mPtrl.setOnPullListener(this);
        EmptyView emptyView = new EmptyView(root.getContext());
        emptyView.setEmptyImg(R.mipmap.empty);
        emptyView.setEmptyReason("还没有关注的舞友");
        mPtrl.setEmptyView(emptyView);
        RecyclerView mAttentionRv = (RecyclerView) mPtrl.getPullableView();
        mAttentionRv.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new UserItemAdapter(null, this);
        mAttentionRv.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mHandler.addCallback(UserHandler.CHILD_ATTENTION, this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPtrl.autoRefresh();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == R.id.msg_user_attention_got) {
            if (msg.obj != null) {
                DancerListWithInfo listWithInfo = (DancerListWithInfo) msg.obj;
                if (UserHandler.Status.pageIdx_att == 1) {
                    mAdapter.setAdapterDataList(listWithInfo.getDataList());
                    mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    mAdapter.addAdapterDataList(listWithInfo.getDataList());
                    mPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            } else {
                if (UserHandler.Status.pageIdx_att == 1) {
                    mPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                    mAdapter.setAdapterDataList(null);
                } else {
                    mPtrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
            if (mAdapter.getItemCount() <= 0) {
                mPtrl.showEmptyView(true);
            } else {
                mPtrl.showEmptyView(false);
            }
        }
        return true;
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, int position) {
        DancerInfo dancerInfo = mAdapter.getItem(position);
        if (view.getId() == R.id.user_list_item_option) {
            if (SecurityUtils.isUserValidity(getActivity(), UserHandler.watcherUserId)) {
                boolean att = (boolean) view.getTag();
                view.setTag(!att);
                mAdapter.notifyItemAttentionChanged(!att, position);
                new AsyncTeacherAtt().execute(UserHandler.watcherUserId, dancerInfo.getUserId());
            }
        } else {
            Intent intent = null;
            if (dancerInfo.getOrgId() <= 0) {
                intent = new Intent(getActivity(), UserDetailsActivity.class);
                intent.putExtra(Constants.Strings.USER_ID, dancerInfo.getUserId());
                intent.putExtra(Constants.Strings.USER_PHOTO, dancerInfo.getPhotoUrl());
                intent.putExtra(Constants.Strings.USER_NAME, dancerInfo.getNickName());
                intent.putExtra(Constants.Strings.USER_SIGN, dancerInfo.getSignature());
                intent.putExtra(Constants.Strings.USER_ATTEN, dancerInfo.getAttention().getValue());
            } else {
                intent = new Intent(getActivity(), OrgDetailsActivity.class);
                intent.putExtra(Constants.Strings.ORG_ID, dancerInfo.getOrgId());
                intent.putExtra(Constants.Strings.ORG_LOGO, dancerInfo.getPhotoUrl());
            }
            startActivity(intent);
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        UserHandler.Status.pageIdx_att = 1;
        mHandler.sendEmptyMessage(R.id.msg_user_get_attention);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        UserHandler.Status.pageIdx_att++;
        mHandler.sendEmptyMessage(R.id.msg_user_get_attention);
    }

    class AsyncTeacherAtt extends AsyncTask<Integer, Integer, SimpleResponse> {

        @Override
        protected SimpleResponse doInBackground(Integer... params) {
            return DetailParser.attention(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(SimpleResponse simpleResponse) {
            if (simpleResponse != null) {
//                Toast.makeText(getActivity(), simpleResponse.getMessage(), Toast.LENGTH_SHORT).show();
            } else {

            }
        }
    }
}
