package cn.spinsoft.wdq.user.frag;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.jingchen.pulltorefresh.PullToRefreshLayout;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.user.UserDetailsActivity;
import cn.spinsoft.wdq.user.biz.UserHandler;
import cn.spinsoft.wdq.user.biz.UserVideo;
import cn.spinsoft.wdq.user.biz.UserVideoWithInfo;
import cn.spinsoft.wdq.user.widget.WorksAdapter;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.video.VideoDetailsNewActivity;
import cn.spinsoft.wdq.widget.ConfirmDialog;
import cn.spinsoft.wdq.widget.EmptyView;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by hushujun on 15/12/2.
 */
public class UserWorksFrag extends BaseFragment implements Handler.Callback, RecyclerItemClickListener,
        PullToRefreshLayout.OnPullListener {
    private static final String TAG = UserWorksFrag.class.getSimpleName();

    private PullToRefreshLayout mPtrl;
    private WorksAdapter mWorksAdapter;

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
        emptyView.setEmptyReason("还没有发布过作品");
        mPtrl.setEmptyView(emptyView);
        RecyclerView mWorksRv = (RecyclerView) mPtrl.getPullableView();
        mWorksRv.setLayoutManager(new GridLayoutManager(root.getContext(), 2));
        mWorksAdapter = new WorksAdapter(null, this);
        mWorksRv.setAdapter(mWorksAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler.addCallback(UserHandler.CHILD_WORKS, this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPtrl.autoRefresh();
    }

    @Override
    public boolean handleMessage(Message msg) {
        LogUtil.i(TAG, "handleMessage:" + msg.toString());
        if (msg.what == R.id.msg_user_videos_got) {
            if (msg.obj != null) {
                UserVideoWithInfo videoWithInfo = (UserVideoWithInfo) msg.obj;
                ((UserDetailsActivity) getActivity()).setDiffTxContent(String.valueOf(videoWithInfo.getThumNum()));
                if (UserHandler.Status.pageIdx_works == 1) {
                    mWorksAdapter.setAdapterDataList(videoWithInfo.getDataList());
                    mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    mWorksAdapter.addAdapterDataList(videoWithInfo.getDataList());
                    mPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            } else {
                if (UserHandler.Status.pageIdx_works == 1) {
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
        return true;
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, final int position) {
        UserVideo userVideo = mWorksAdapter.getItem(position);
        switch (view.getId()) {
            case R.id.user_works_item_delete:
                UserHandler.Status.videoId = userVideo.getVideoId();
                new ConfirmDialog(getContext(), ConfirmDialog.Type.DELETE, new ConfirmDialog.OnConfirmClickListenter() {
                    @Override
                    public void onConfirmClick(View v) {
                        if (v.getId() == R.id.dia_confirm_confirm) {
                            mHandler.sendEmptyMessage(R.id.msg_user_delete_video);
                            mWorksAdapter.removeItem(position);
                            Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
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
        UserHandler.Status.pageIdx_works = 1;
        mHandler.sendEmptyMessage(R.id.msg_user_get_videos);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        UserHandler.Status.pageIdx_works++;
        mHandler.sendEmptyMessage(R.id.msg_user_get_videos);
    }
}
