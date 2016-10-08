package cn.spinsoft.wdq.org.frag;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.discover.DiscoverDetailActivity;
import cn.spinsoft.wdq.discover.biz.DiscoverItemBean;
import cn.spinsoft.wdq.discover.biz.DiscoverListWithInfo;
import cn.spinsoft.wdq.discover.widget.DiscoverListAdapter;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.org.biz.OrgHandler;
import cn.spinsoft.wdq.org.biz.OrgParser;
import cn.spinsoft.wdq.user.UserDetailsActivity;
import cn.spinsoft.wdq.user.biz.UserHandler;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.utils.UrlManager;
import cn.spinsoft.wdq.widget.ConfirmDialog;
import cn.spinsoft.wdq.widget.EmptyView;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;
import cn.spinsoft.wdq.widget.ShareBoardDialog;

/**
 * Created by hushujun on 16/1/7.
 */
public class OrgDynamicFrag extends BaseFragment implements PullToRefreshLayout.OnPullListener, RecyclerItemClickListener, ShareBoardDialog.ShareBoardDiaListener {
    private static final String TAG = OrgDynamicFrag.class.getSimpleName();

    private PullToRefreshLayout mPtrl;
    private DiscoverListAdapter mDynamicAdapter;

    private int orgId = -1;
    private int pageIdx = 1;
    private int mForwardPosition;

    private UMShareListener mUmShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
            mHandler.sendEmptyMessage(R.id.msg_report_forward_discover);
            mDynamicAdapter.notifyItemForwardChanged(mForwardPosition);
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
        return R.layout.frag_recycler_child;
    }

    @Override
    protected void initViewAndListener(View root, Bundle savedInstanceState) {
        mPtrl = (PullToRefreshLayout) root.findViewById(R.id.recycler_child_content);
        mPtrl.setEmptyView(new EmptyView(root.getContext()));
        mPtrl.setOnPullListener(this);
        RecyclerView dynamic = (RecyclerView) mPtrl.getPullableView();
        dynamic.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));
        mDynamicAdapter = new DiscoverListAdapter(null, this, getActivity());
        UserLogin userLogin = SharedPreferencesUtil.getInstance(getActivity()).getLoginUser();
        if (userLogin != null) {
            if (OrgHandler.Status.orgId == userLogin.getOrgId()) {
                mDynamicAdapter.setListType(Constants.Strings.DELETE_MODE);
            }
        }
        dynamic.setAdapter(mDynamicAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orgId = getActivity().getIntent().getIntExtra(Constants.Strings.ORG_ID, -1);
        mPtrl.autoRefresh();
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        pageIdx = 1;
        new AsyncDynamic().execute(orgId, pageIdx);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        pageIdx++;
        new AsyncDynamic().execute(orgId, pageIdx);
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, int position) {
        DiscoverItemBean itemBean = mDynamicAdapter.getItem(position);
        switch (view.getId()) {
            case R.id.discover_list_item_photo:
                Intent userIntent = new Intent(getActivity(), UserDetailsActivity.class);
                userIntent.putExtra(Constants.Strings.USER_ID, itemBean.getUserId());
                userIntent.putExtra(Constants.Strings.USER_PHOTO, itemBean.getPhotoUrl());
                userIntent.putExtra(Constants.Strings.USER_NAME, itemBean.getNickName());
                startActivity(userIntent);
                break;
            case R.id.discover_list_item_like:
                LogUtil.i(TAG, "onItemClicked=====>>like");
                mDynamicAdapter.notifyItemLikeChanged(!itemBean.isLike(), position);
                OrgHandler.Status.eventId = itemBean.getEventId();
                OrgHandler.Status.typeId = itemBean.getType().getValue();
                OrgHandler.Status.userId = itemBean.getUserId();
                mHandler.sendEmptyMessage(R.id.msg_report_like_discover);
                break;
            case R.id.discover_list_item_forward:
                LogUtil.i(TAG, "onItemClicked=====>>forward");
                new ShareBoardDialog(getActivity(), this).setShare_medias(Constants.Arrays.SHARDBOARD).show();
                OrgHandler.Status.eventId = itemBean.getEventId();
                OrgHandler.Status.userId = itemBean.getUserId();
                mForwardPosition = position;
                break;
            case R.id.discover_contest_delete:
            case R.id.discover_list_item_delete://删除
                OrgHandler.Status.typeId = itemBean.getType().getValue();
                OrgHandler.Status.eventId = itemBean.getEventId();
                final int delItemPosition = position;
                ConfirmDialog mConfirmDia = new ConfirmDialog(getActivity(), ConfirmDialog.Type.DELETE, new ConfirmDialog.OnConfirmClickListenter() {
                    @Override
                    public void onConfirmClick(View v) {
                        if (v.getId() == R.id.dia_confirm_confirm) {
                            mDynamicAdapter.removeItem(delItemPosition);
                            mHandler.sendEmptyMessage(R.id.msg_discover_delete_by_id);
                        }
                    }
                });
                mConfirmDia.show();
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

    @Override
    public void shareBoardOnclickListener(View v) {
        UMImage umImage = null;
        DiscoverItemBean itemBean = mDynamicAdapter.getItem(mForwardPosition);
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
        UserHandler.Status.forwarWay = v.getTag().toString();
    }

    class AsyncDynamic extends AsyncTask<Integer, Integer, DiscoverListWithInfo> {

        @Override
        protected DiscoverListWithInfo doInBackground(Integer... params) {
            return OrgParser.getDynamic(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(DiscoverListWithInfo listWithInfo) {
            if (listWithInfo != null) {
                if (pageIdx == 1) {
                    mDynamicAdapter.setAdapterDataList(listWithInfo.getDataList());
                    mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    mDynamicAdapter.addAdapterDataList(listWithInfo.getDataList());
                    mPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            } else {
                if (pageIdx == 1) {
                    mPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                    mDynamicAdapter.setAdapterDataList(null);
                } else {
                    mPtrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
            if (mDynamicAdapter.getItemCount() <= 0) {
                mPtrl.showEmptyView(true);
            } else {
                mPtrl.showEmptyView(false);
            }
        }
    }
}
