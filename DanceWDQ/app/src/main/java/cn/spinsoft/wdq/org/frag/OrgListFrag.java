package cn.spinsoft.wdq.org.frag;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.jingchen.pulltorefresh.PullToRefreshLayout;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.browse.biz.BrowseHandler;
import cn.spinsoft.wdq.org.OrgDetailsActivity;
import cn.spinsoft.wdq.org.biz.OrgInfo;
import cn.spinsoft.wdq.org.biz.OrgParser;
import cn.spinsoft.wdq.org.biz.OrgSimpleListWithInfo;
import cn.spinsoft.wdq.org.widget.OrgListAdapter;
import cn.spinsoft.wdq.service.LocationOnMain;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.SimpleItemDataUtils;
import cn.spinsoft.wdq.widget.DropDownSpinner;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;
import cn.spinsoft.wdq.widget.SimpleTextBaseAdapter;

/**
 * Created by zhoujun on 16/1/6.
 */
public class OrgListFrag extends BaseFragment implements PullToRefreshLayout.OnPullListener,
        DropDownSpinner.OnItemSelectedListener, RecyclerItemClickListener {
    private static final String TAG = OrgListFrag.class.getSimpleName();
    private static final int longitude = 0;
    private static final int latitude = 1;
    private static final int danceType = 2;
    private static final int pageIdx = 3;
    private static final int popular = 4;
    private static final int distance = 5;

    private double[] httpParams = new double[]{-1d, -1d, -1d, 1d, 0d, 0d};

    //    private Spinner mDistanceSp, mDanceSp, mPopularSp;
    private DropDownSpinner mDistanceSp, mDanceSp, mPopularSp;
    private PullToRefreshLayout mPtrl;
    private RecyclerView mContentRv;

    private OrgListAdapter listAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_org_list;
    }

    @Override
    protected void initViewAndListener(View root, Bundle savedInstanceState) {
        mPtrl = (PullToRefreshLayout) root.findViewById(R.id.org_main_content);
        mPtrl.setOnPullListener(this);
        mContentRv = (RecyclerView) mPtrl.getPullableView();
        mContentRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        mDistanceSp = (DropDownSpinner) root.findViewById(R.id.org_main_distance);
        mDanceSp = (DropDownSpinner) root.findViewById(R.id.org_main_type);
        mPopularSp = (DropDownSpinner) root.findViewById(R.id.org_main_popular);

        mDistanceSp.setmListAdpter(new SimpleTextBaseAdapter(SimpleItemDataUtils.getDistances()));
        mDanceSp.setmListAdpter(new SimpleTextBaseAdapter(SimpleItemDataUtils.getDanceTypeList(false, getActivity())));
        mPopularSp.setmListAdpter(new SimpleTextBaseAdapter(SimpleItemDataUtils.getPopularSortList()));

        mDistanceSp.setOnItemSelectedListener(this);
        mDanceSp.setOnItemSelectedListener(this);
        mPopularSp.setOnItemSelectedListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listAdapter = new OrgListAdapter(null, this);
        mContentRv.setAdapter(listAdapter);
        mPtrl.autoRefresh();
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        double[] location = LocationOnMain.getInstance().getLocation();
        httpParams[longitude] = location[0];
        httpParams[latitude] = location[1];
        httpParams[pageIdx] = 1d;
        new AsyncOrgList().execute(httpParams[longitude], httpParams[latitude], httpParams[danceType],
                httpParams[pageIdx], httpParams[popular], httpParams[distance]);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        httpParams[pageIdx]++;
        new AsyncOrgList().execute(httpParams[longitude], httpParams[latitude], httpParams[danceType],
                httpParams[pageIdx], httpParams[popular], httpParams[distance]);
    }

    @Override
    public void onItemSelected(ListAdapter parent, TextView view, int position, long id) {
        if (parent instanceof SimpleTextBaseAdapter) {
            SimpleItemData item = ((SimpleTextBaseAdapter) parent).getItem(position);
            BrowseHandler.Status.Friend.pageIdx = 1;
            if (parent == mDistanceSp.getmListAdpter()) {
                if (item.getId() == -1) {
                    mDistanceSp.editText.setText("附近");
                }
                httpParams[distance] = item.getId();
            } else if (parent == mDanceSp.getmListAdpter()) {
                if (item.getId() == -1) {
                    mDanceSp.editText.setText("舞种");
                }
                httpParams[danceType] = item.getId();
            } else if (parent == mPopularSp.getmListAdpter()) {
                if (item.getId() == 0) {
                    mPopularSp.editText.setText("人气");
                }
                httpParams[popular] = item.getId();
            }
            new AsyncOrgList().execute(httpParams[longitude], httpParams[latitude], httpParams[danceType],
                    httpParams[pageIdx], httpParams[popular], httpParams[distance]);
        }
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, int position) {
        OrgInfo infoSimple = listAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), OrgDetailsActivity.class);
        intent.putExtra(Constants.Strings.ORG_ID, infoSimple.getOrgId());
        intent.putExtra(Constants.Strings.USER_ID, infoSimple.getUserId());
        intent.putExtra(Constants.Strings.ORG_NAME, infoSimple.getOrgName());
        intent.putExtra(Constants.Strings.ORG_LOGO, infoSimple.getPhotoUrl());
        intent.putExtra(Constants.Strings.ORG_SIGN, infoSimple.getSignature());
        startActivity(intent);
    }

    class AsyncOrgList extends AsyncTask<Double, Integer, OrgSimpleListWithInfo> {

        @Override
        protected OrgSimpleListWithInfo doInBackground(Double... params) {
            return OrgParser.getOrgList(params[0], params[1], params[2], params[3], params[4], params[5]);
        }

        @Override
        protected void onPostExecute(OrgSimpleListWithInfo listWithInfo) {
            if (listWithInfo != null) {
                if (httpParams[pageIdx] == 1) {
                    listAdapter.setAdapterDataList(listWithInfo.getDataList());
                    mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    listAdapter.addAdapterDataList(listWithInfo.getDataList());
                    mPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            } else {
                if (httpParams[pageIdx] == 1) {
                    mPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                    listAdapter.setAdapterDataList(null);
                } else {
                    mPtrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
            if (listAdapter.getItemCount() <= 0) {
                mPtrl.showEmptyView(true);
            } else {
                mPtrl.showEmptyView(false);
            }
        }
    }

}
