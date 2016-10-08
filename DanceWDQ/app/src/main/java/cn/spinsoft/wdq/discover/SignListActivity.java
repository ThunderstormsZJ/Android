package cn.spinsoft.wdq.discover;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jingchen.pulltorefresh.PullToRefreshLayout;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.discover.biz.DiscoverParser;
import cn.spinsoft.wdq.discover.biz.SignListWithInfo;
import cn.spinsoft.wdq.discover.widget.SignListAdapter;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.widget.EmptyView;

/**
 * Created by zhoujun on 2016-4-13.
 * 报名人数列表
 */
public class SignListActivity extends BaseActivity implements PullToRefreshLayout.OnPullListener {
    private final static String TAG = SignListActivity.class.getSimpleName();

    private PullToRefreshLayout mPtrl;
    private SignListAdapter mSignAdapter;

    private int pageIndex = 1;
    private int eventId = -1;
    private int typeId = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_signlist;
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView backTx = (TextView) findViewById(R.id.base_title_back);
        TextView titleTx = (TextView) findViewById(R.id.base_title_name);
        titleTx.setText("报名人数");

        mPtrl = (PullToRefreshLayout) findViewById(R.id.singn_list_content);
        mPtrl.setEmptyView(new EmptyView(this));
        mPtrl.setOnPullListener(this);
        RecyclerView mContentRv = (RecyclerView) mPtrl.getPullableView();
        mContentRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        mContentRv.addItemDecoration(new RecycleViewDivider(this,LinearLayoutManager.VERTICAL));
        mSignAdapter = new SignListAdapter(null);
        mContentRv.setAdapter(mSignAdapter);

        backTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventId = getIntent().getIntExtra(Constants.Strings.DISCOVER_EVENT_ID, -1);
        typeId = getIntent().getIntExtra(Constants.Strings.DISCOVER_TYPE_ID, -1);
        mPtrl.autoRefresh();
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        pageIndex = 1;
        new AsyncGetSign().execute(eventId, typeId, pageIndex);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        pageIndex++;
        new AsyncGetSign().execute(eventId, typeId, pageIndex);
    }

    class AsyncGetSign extends AsyncTask<Integer, Integer, SignListWithInfo> {

        @Override
        protected SignListWithInfo doInBackground(Integer... params) {
            return DiscoverParser.getSignPerson(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(SignListWithInfo signListWithInfo) {
            if (signListWithInfo != null) {
                if (signListWithInfo.getPageNumber() == 1) {
                    mSignAdapter.setAdapterDataList(signListWithInfo.getDataList());
                    mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    mSignAdapter.addAdapterDataList(signListWithInfo.getDataList());
                    mPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            } else {
                if (signListWithInfo.getPageNumber() == 1) {
                    mSignAdapter.setAdapterDataList(null);
                    mPtrl.refreshFinish(PullToRefreshLayout.FAIL);

                } else {
                    mPtrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
            if (mSignAdapter.getItemCount() <= 0) {
                mPtrl.showEmptyView(true);
            } else {
                mPtrl.showEmptyView(false);
            }

        }
    }
}
