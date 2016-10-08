package cn.spinsoft.wdq.mine.component;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jingchen.pulltorefresh.PullToRefreshLayout;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.discover.DiscoverDetailActivity;
import cn.spinsoft.wdq.mine.biz.MineParser;
import cn.spinsoft.wdq.mine.biz.RelatedBean;
import cn.spinsoft.wdq.mine.biz.RelatedListWithInfo;
import cn.spinsoft.wdq.mine.widget.RelatedAdapter;
import cn.spinsoft.wdq.user.UserDetailsActivity;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.video.VideoDetailsNewActivity;
import cn.spinsoft.wdq.widget.EmptyView;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by zhoujun on 16/1/12.
 */
public class MineRelatedActivity extends BaseActivity implements PullToRefreshLayout.OnPullListener,
        RecyclerItemClickListener {
    private static final String TAG = MineRelatedActivity.class.getSimpleName();

    private PullToRefreshLayout mPtrl;
    private RelatedAdapter mRelatedAdapter;

    private int watcherId = -1;
    private int pageIdx = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine_related;
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView backTx = (TextView) findViewById(R.id.base_title_back);
        TextView titleTx = (TextView) findViewById(R.id.base_title_name);
        titleTx.setText("与我相关");
        mPtrl = (PullToRefreshLayout) findViewById(R.id.mine_related_content);
        mPtrl.setEmptyView(new EmptyView(this));
        mPtrl.setOnPullListener(this);
        RecyclerView contentRv = (RecyclerView) mPtrl.getPullableView();
        contentRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRelatedAdapter = new RelatedAdapter(null, this);
        contentRv.setAdapter(mRelatedAdapter);

        backTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        watcherId = getIntent().getIntExtra(Constants.Strings.USER_ID, -1);
        mPtrl.autoRefresh();
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        pageIdx = 1;
        new AsyncRelated().execute(watcherId, pageIdx);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        pageIdx++;
        new AsyncRelated().execute(watcherId, pageIdx);
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, int position) {
        RelatedBean relatedBean = mRelatedAdapter.getItem(position);
        switch (view.getId()){
            case R.id.mine_related_item_photo://头像
                Intent uDIntent = new Intent(this, UserDetailsActivity.class);
                uDIntent.putExtra(Constants.Strings.USER_ID,relatedBean.getAnotherUserId());
                uDIntent.putExtra(Constants.Strings.USER_NAME,relatedBean.getAnotherName());
                uDIntent.putExtra(Constants.Strings.USER_PHOTO,relatedBean.getAnotherPhoto());
                startActivity(uDIntent);
                break;
            case R.id.mine_related_item_confirm://确认邀请
                new AsyncInviteComfirm().execute(relatedBean.getRecodeId());
                break;
            default:
                //详细页
                if(relatedBean.getType()<=Constants.Ints.RELATETYPE_VIDE_MAX
                        && relatedBean.getType()>=Constants.Ints.RELATETYPE_VIDE_MIN){//视频类型
                    Intent vDIntent = new Intent(this, VideoDetailsNewActivity.class);
                    vDIntent.putExtra(Constants.Strings.VIDEO_ID,relatedBean.getRelatedId());
                    vDIntent.putExtra(Constants.Strings.OWNER_ID,relatedBean.getCurrUserId());
                    startActivity(vDIntent);
                }else {//发现
                    Intent dDIntent = new Intent(this, DiscoverDetailActivity.class);
                    dDIntent.putExtra(Constants.Strings.DISCOVER_TYPE_ID,relatedBean.getDiscoverType());
                    dDIntent.putExtra(Constants.Strings.DISCOVER_EVENT_ID,relatedBean.getRelatedId());
                    dDIntent.putExtra(Constants.Strings.USER_ID,relatedBean.getCurrUserId());
                    startActivity(dDIntent);
                }
                break;
        }
    }

    class AsyncRelated extends AsyncTask<Integer, Integer, RelatedListWithInfo> {

        @Override
        protected RelatedListWithInfo doInBackground(Integer... params) {
            return MineParser.getRelatedList(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(RelatedListWithInfo listWithInfo) {
            if (listWithInfo != null) {
                if (pageIdx == 1) {
                    mRelatedAdapter.setAdapterDataList(listWithInfo.getDataList());
                    mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    mRelatedAdapter.addAdapterDataList(listWithInfo.getDataList());
                    mPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            } else {
                if (pageIdx == 1) {
                    mPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                    mRelatedAdapter.setAdapterDataList(null);
                } else {
                    mPtrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
            if (mRelatedAdapter.getItemCount() <= 0) {
                mPtrl.showEmptyView(true);
            } else {
                mPtrl.showEmptyView(false);
            }
        }
    }

    class AsyncInviteComfirm extends AsyncTask<Integer,Integer,SimpleResponse>{
        @Override
        protected SimpleResponse doInBackground(Integer... params) {
            return MineParser.submitInviteConfirm(params[0]);
        }
    }
}
