package cn.spinsoft.wdq.org.frag;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jingchen.pulltorefresh.PullToRefreshLayout;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.org.OrgDetailsActivity;
import cn.spinsoft.wdq.org.biz.OrgParser;
import cn.spinsoft.wdq.user.biz.UserVideo;
import cn.spinsoft.wdq.user.biz.UserVideoWithInfo;
import cn.spinsoft.wdq.user.widget.WorksAdapter;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.video.VideoDetailsNewActivity;
import cn.spinsoft.wdq.video.biz.DetailParser;
import cn.spinsoft.wdq.widget.ConfirmDialog;
import cn.spinsoft.wdq.widget.EmptyView;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by hushujun on 16/1/7.
 */
public class OrgWorksFrag extends BaseFragment implements RecyclerItemClickListener, PullToRefreshLayout.OnPullListener {
    private static final String TAG = OrgWorksFrag.class.getSimpleName();

    private PullToRefreshLayout mPtrl;
    private WorksAdapter mWorksAdapter;
    private int pageIdx = 1;
    private int orgId = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_recycler_child;
    }

    @Override
    protected void initViewAndListener(View root, Bundle savedInstanceState) {
        mPtrl = (PullToRefreshLayout) root.findViewById(R.id.recycler_child_content);
        mPtrl.setEmptyView(new EmptyView(root.getContext()));
        mPtrl.setOnPullListener(this);
        RecyclerView mWorksRv = (RecyclerView) mPtrl.getPullableView();
        mWorksRv.setLayoutManager(new GridLayoutManager(root.getContext(), 2));
        mWorksAdapter = new WorksAdapter(null, this);
        mWorksRv.setAdapter(mWorksAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orgId = getActivity().getIntent().getIntExtra(Constants.Strings.ORG_ID, -1);
        mPtrl.autoRefresh();
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, final int position) {
        final UserVideo userVideo = mWorksAdapter.getItem(position);
        switch (view.getId()) {
            case R.id.user_works_item_delete:
                new ConfirmDialog(getContext(), ConfirmDialog.Type.DELETE, new ConfirmDialog.OnConfirmClickListenter() {
                    @Override
                    public void onConfirmClick(View v) {
                        if(v.getId()==R.id.dia_confirm_confirm){
                            mWorksAdapter.removeItem(position);
                            new AsyncDeleteWorks().execute(userVideo.getVideoId());
                        }
                    }
                }).show();
                break;
            default:
                Intent intent = new Intent(getActivity(), VideoDetailsNewActivity.class);
                intent.putExtra(Constants.Strings.VIDEO_ID, userVideo.getVideoId());
                intent.putExtra(Constants.Strings.OWNER_ID, userVideo.getUserId());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        pageIdx = 1;
        new AsyncWorks().execute(orgId, pageIdx);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        pageIdx++;
        new AsyncWorks().execute(orgId, pageIdx);
    }

    class AsyncWorks extends AsyncTask<Integer, Integer, UserVideoWithInfo> {

        @Override
        protected UserVideoWithInfo doInBackground(Integer... params) {
            return OrgParser.getVideos(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(UserVideoWithInfo userVideoWithInfo) {
            if (userVideoWithInfo != null) {
                if (pageIdx == 1) {
                    ((OrgDetailsActivity) getActivity()).setDiffTxContent(String.valueOf(userVideoWithInfo.getThumNum()));
                    mWorksAdapter.setAdapterDataList(userVideoWithInfo.getDataList());
                    mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    mWorksAdapter.addAdapterDataList(userVideoWithInfo.getDataList());
                    mPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            } else {
                if (pageIdx == 1) {
                    mPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                    mWorksAdapter.setAdapterDataList(null);
                } else {
                    mPtrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
            if (mWorksAdapter.getItemCount() <= 0) {
                mPtrl.showEmptyView(true);
            } else {
                mPtrl.showEmptyView(false);
            }
        }
    }

    class AsyncDeleteWorks extends AsyncTask<Integer, Integer, SimpleResponse> {
        @Override
        protected SimpleResponse doInBackground(Integer... params) {
            return DetailParser.deleteVideoWork(params[0]);
        }
    }
}
