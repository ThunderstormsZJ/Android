package cn.spinsoft.wdq.org.frag;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jingchen.pulltorefresh.PullToRefreshLayout;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.org.CourseBookingActivity;
import cn.spinsoft.wdq.org.biz.CourseListWithInfo;
import cn.spinsoft.wdq.org.biz.OrgHandler;
import cn.spinsoft.wdq.org.biz.OrgParser;
import cn.spinsoft.wdq.org.widget.CourseListAdapter;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.SecurityUtils;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.widget.EmptyView;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by hushujun on 16/1/7.
 */
public class CourseFrag extends BaseFragment implements PullToRefreshLayout.OnPullListener, RecyclerItemClickListener {
    private static final String TAG = CourseFrag.class.getSimpleName();

    private PullToRefreshLayout mPtrl;
    private CourseListAdapter mCourseAdapter;
    private UserLogin loginUser;

    private int orgId = -1;
    private int pageIdx;

    private int positionToNext;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_recycler_child;
    }

    @Override
    protected void initViewAndListener(View root, Bundle savedInstanceState) {
        mPtrl = (PullToRefreshLayout) root.findViewById(R.id.recycler_child_content);
        mPtrl.setEmptyView(new EmptyView(root.getContext()));
        mPtrl.setOnPullListener(this);
        RecyclerView contentRv = (RecyclerView) mPtrl.getPullableView();
        contentRv.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));
        mCourseAdapter = new CourseListAdapter(null, this);
        loginUser = SharedPreferencesUtil.getInstance(getActivity()).getLoginUser();
        if(loginUser!=null && loginUser.getOrgId()==OrgHandler.Status.orgId){
            mCourseAdapter.setListType(Constants.Strings.DELETE_MODE);
        }
        contentRv.setAdapter(mCourseAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orgId = getActivity().getIntent().getIntExtra(Constants.Strings.ORG_ID, -1);
        mPtrl.autoRefresh();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            mCourseAdapter.notifyItemStatusChanged(positionToNext);
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        pageIdx = 1;
        new AsyncCourse().execute(orgId, OrgHandler.watcherUserId, pageIdx);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        pageIdx++;
        new AsyncCourse().execute(orgId, OrgHandler.watcherUserId, pageIdx);
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, int position) {
        if (SecurityUtils.isUserValidity(getActivity(), OrgHandler.watcherUserId)) {
            if(loginUser.getOrgId()==OrgHandler.Status.orgId){
                new AsyncDeleteCourse().execute(mCourseAdapter.getItem(position).getCourseId());
                mCourseAdapter.removeItem(position);
            }else {
                positionToNext = position;
                Intent intent = new Intent(getActivity(), CourseBookingActivity.class);
                intent.putExtra(Constants.Strings.ORG_COURSE_ID, mCourseAdapter.getItem(position).getCourseId());
                getActivity().startActivityForResult(intent, Constants.Ints.REQUEST_CODE_BOOK_SUCCESS);
            }
        }
    }

    public void onRefresh(){
        mPtrl.autoRefresh();
    }

    class AsyncCourse extends AsyncTask<Integer, Integer, CourseListWithInfo> {

        @Override
        protected CourseListWithInfo doInBackground(Integer... params) {
            return OrgParser.getCourseList(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(CourseListWithInfo listWithInfo) {
            if (listWithInfo != null) {
                if (pageIdx == 1) {
                    mCourseAdapter.setAdapterDataList(listWithInfo.getDataList());
                    mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    mCourseAdapter.addAdapterDataList(listWithInfo.getDataList());
                    mPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            } else {
                if (pageIdx == 1) {
                    mPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                    mCourseAdapter.setAdapterDataList(null);
                } else {
                    mPtrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
            if (mCourseAdapter.getItemCount() <= 0) {
                mPtrl.showEmptyView(true);
            } else {
                mPtrl.showEmptyView(false);
            }
        }
    }

    class AsyncDeleteCourse extends AsyncTask<Integer,Integer,SimpleResponse>{

        @Override
        protected SimpleResponse doInBackground(Integer... params) {
            return OrgParser.deleteCourse(params[0]);
        }
    }
}
