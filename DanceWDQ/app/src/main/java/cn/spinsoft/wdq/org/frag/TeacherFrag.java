package cn.spinsoft.wdq.org.frag;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.jingchen.pulltorefresh.PullToRefreshLayout;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.org.biz.OrgHandler;
import cn.spinsoft.wdq.org.biz.OrgParser;
import cn.spinsoft.wdq.user.UserDetailsActivity;
import cn.spinsoft.wdq.user.biz.DancerInfo;
import cn.spinsoft.wdq.user.biz.DancerListWithInfo;
import cn.spinsoft.wdq.user.widget.UserItemAdapter;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.SecurityUtils;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.video.biz.DetailParser;
import cn.spinsoft.wdq.widget.EmptyView;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by hushujun on 16/1/7.
 */
public class TeacherFrag extends BaseFragment implements PullToRefreshLayout.OnPullListener, RecyclerItemClickListener {
    private static final String TAG = TeacherFrag.class.getSimpleName();

    private PullToRefreshLayout mPtrl;
    private UserItemAdapter mTeacherAdapter;

    private int orgId = -1;
    private int pageIdx = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_recycler_child;
    }

    @Override
    protected void initViewAndListener(View root, Bundle savedInstanceState) {
        mPtrl = (PullToRefreshLayout) root.findViewById(R.id.recycler_child_content);
        mPtrl.setEmptyView(new EmptyView(root.getContext()));
        mPtrl.setOnPullListener(this);
        RecyclerView teacherRv = (RecyclerView) mPtrl.getPullableView();
        teacherRv.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));
        mTeacherAdapter = new UserItemAdapter(null, this);
        teacherRv.setAdapter(mTeacherAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orgId = getActivity().getIntent().getIntExtra(Constants.Strings.ORG_ID, -1);
        UserLogin loginUser = SharedPreferencesUtil.getInstance(getContext()).getLoginUser();
        if (loginUser != null && loginUser.getOrgId() == orgId) {
            mTeacherAdapter.isCurrOrg(true);
        }
        mPtrl.autoRefresh();
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        pageIdx = 1;
        new AsyncTeacher().execute(orgId, pageIdx);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        pageIdx++;
        new AsyncTeacher().execute(orgId, pageIdx);
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, int position) {
        DancerInfo dancerInfo = mTeacherAdapter.getItem(position);
        if (view.getId() == R.id.user_list_item_option) {
            if (SecurityUtils.isUserValidity(getActivity(), OrgHandler.watcherUserId)) {
                boolean att = (boolean) view.getTag();
                view.setTag(!att);
                mTeacherAdapter.notifyItemAttentionChanged(!att, position);
                new AsyncTeacherAtt().execute(OrgHandler.watcherUserId, dancerInfo.getUserId());
            }
        } else if (view.getId() == R.id.user_list_item_fire) {
            new AsyncFireTeacher().execute(dancerInfo.getUserId(), orgId);
            mTeacherAdapter.removeItem(position);
            Toast.makeText(getActivity(), "解聘成功", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getActivity(), UserDetailsActivity.class);
            intent.putExtra(Constants.Strings.USER_ID, dancerInfo.getUserId());
            intent.putExtra(Constants.Strings.USER_PHOTO, dancerInfo.getPhotoUrl());
            intent.putExtra(Constants.Strings.USER_NAME, dancerInfo.getNickName());
            intent.putExtra(Constants.Strings.USER_SIGN, dancerInfo.getSignature());
            intent.putExtra(Constants.Strings.USER_ATTEN, dancerInfo.getAttention().getValue());
            startActivity(intent);
        }
    }

    class AsyncTeacher extends AsyncTask<Integer, Integer, DancerListWithInfo> {

        @Override
        protected DancerListWithInfo doInBackground(Integer... params) {
            return OrgParser.getTeacherList(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(DancerListWithInfo listWithInfo) {
            if (listWithInfo != null) {
                if (pageIdx == 1) {
                    mTeacherAdapter.setAdapterDataList(listWithInfo.getDataList());
                    mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    mTeacherAdapter.addAdapterDataList(listWithInfo.getDataList());
                    mPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            } else {
                if (pageIdx == 1) {
                    mPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                    mTeacherAdapter.setAdapterDataList(null);
                } else {
                    mPtrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
            if (mTeacherAdapter.getItemCount() <= 0) {
                mPtrl.showEmptyView(true);
            } else {
                mPtrl.showEmptyView(false);
            }
        }
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
            }
        }
    }

    class AsyncFireTeacher extends AsyncTeacherAtt {
        @Override
        protected SimpleResponse doInBackground(Integer... params) {
            return OrgParser.fireTeacher(params[0], params[1]);
        }
    }
}
