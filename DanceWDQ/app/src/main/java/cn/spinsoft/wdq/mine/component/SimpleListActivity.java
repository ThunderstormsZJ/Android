package cn.spinsoft.wdq.mine.component;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.jingchen.pulltorefresh.PullToRefreshLayout;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.enums.AttestState;
import cn.spinsoft.wdq.mine.biz.MineParser;
import cn.spinsoft.wdq.mine.biz.SimpleUserListWithInfo;
import cn.spinsoft.wdq.mine.widget.SimpleListAdapter;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.ContentResolverUtil;
import cn.spinsoft.wdq.widget.EmptyView;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;
import cn.spinsoft.wdq.widget.SearchBar;
import cn.spinsoft.wdq.widget.SearchBar.OnCancelClickListenerCallBack;

/**
 * Created by hushujun on 15/12/31.
 */
public class SimpleListActivity extends BaseActivity implements View.OnClickListener,
        TextView.OnEditorActionListener, PullToRefreshLayout.OnPullListener, RecyclerItemClickListener
        , OnCancelClickListenerCallBack {
    private static final String TAG = SimpleListActivity.class.getSimpleName();
    public static final int PERSONAL_TEACHER_ATTEST = 0;
    public static final int ORG_TEACHER_INVITATION = 1;
    public static final int ORG_TEACHER_ATTEST = 2;

    private int currType = PERSONAL_TEACHER_ATTEST;

    private PullToRefreshLayout mPtrl;
    private SearchBar mSearchBar;
    private TextView mTitleTx, mConfirmTx;

    private int watcherUerId = -1;
    private int orgId = -1;
    private int pageIdx = 1;

    private SimpleListAdapter attestAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_simple_list;
    }

    @Override
    protected void initHandler() {
        Intent intent = getIntent();
        currType = intent.getIntExtra(Constants.Strings.SIMPLE_LIST_TYPE, PERSONAL_TEACHER_ATTEST);
        if (currType == PERSONAL_TEACHER_ATTEST) {
            watcherUerId = intent.getIntExtra(Constants.Strings.USER_ID, -1);
        } else {
            orgId = intent.getIntExtra(Constants.Strings.ORG_ID, -1);
        }
        if (currType == ORG_TEACHER_ATTEST) {
            attestAdapter = new SimpleListAdapter(null, this, this);
        } else {
            attestAdapter = new SimpleListAdapter(null, null, this);
        }
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView mBackTx = (TextView) findViewById(R.id.simple_list_back);
        mConfirmTx = (TextView) findViewById(R.id.simple_list_confirm);
        mTitleTx = (TextView) findViewById(R.id.simple_list_title);
        mSearchBar = (SearchBar) findViewById(R.id.simple_list_search);
        mPtrl = (PullToRefreshLayout) findViewById(R.id.simple_list_content);
        mPtrl.setEmptyView(new EmptyView(this));
        RecyclerView mAttestContentRv = (RecyclerView) mPtrl.getPullableView();

        mAttestContentRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAttestContentRv.setAdapter(attestAdapter);

        mPtrl.setOnPullListener(this);
        mBackTx.setOnClickListener(this);
        mConfirmTx.setOnClickListener(this);
        mSearchBar.setOnEditorActionListener(this);
        mSearchBar.setOnCancelClickListener(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (currType == PERSONAL_TEACHER_ATTEST) {
            mTitleTx.setText("教师认证");
        } else if (currType == ORG_TEACHER_INVITATION) {
            mTitleTx.setText("邀请老师入驻");
        } else if (currType == ORG_TEACHER_ATTEST) {
            mTitleTx.setText("待认证老师");
            mConfirmTx.setVisibility(View.GONE);
        }
        mPtrl.autoRefresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.simple_list_back:
                finish();
                break;
            case R.id.simple_list_confirm:
                submitChoice();
                break;
            default:
                break;
        }
    }

    @Override
    public void OnCancelClickListener(View v) {
        mSearchBar.setText("");
        ContentResolverUtil.hideIMM(getCurrentFocus());
        mPtrl.autoRefresh();
    }

    private void submitChoice() {
        if(TextUtils.isEmpty(attestAdapter.getChoicedIds())){
            Toast.makeText(this,"请选择",Toast.LENGTH_SHORT).show();
            return;
        }
        if (currType == PERSONAL_TEACHER_ATTEST) {
            new AsyncSubmitPersonalAttest().execute(String.valueOf(watcherUerId), attestAdapter.getChoicedOrgIds());
        } else if (currType == ORG_TEACHER_INVITATION) {
            new AsyncSubmitOrgInvitation().execute(String.valueOf(orgId), attestAdapter.getChoicedIds());
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            mPtrl.autoRefresh();
            ContentResolverUtil.hideIMM(v);
        }
        return false;
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        pageIdx = 1;
        if (currType == PERSONAL_TEACHER_ATTEST) {
            new AsyncPersonalAttestList().execute(String.valueOf(watcherUerId),
                    mSearchBar.getText().toString(), String.valueOf(pageIdx));
        } else if (currType == ORG_TEACHER_INVITATION) {
            new AsyncOrgInviteList().execute(String.valueOf(orgId), mSearchBar.getText().toString(),
                    String.valueOf(pageIdx));
        } else if (currType == ORG_TEACHER_ATTEST) {
            new AsyncOrgAttestList().execute(String.valueOf(orgId), mSearchBar.getText().toString(),
                    String.valueOf(pageIdx));
        }
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        pageIdx++;
        if (currType == PERSONAL_TEACHER_ATTEST) {
            new AsyncPersonalAttestList().execute(String.valueOf(watcherUerId),
                    mSearchBar.getText().toString(), String.valueOf(pageIdx));
        } else if (currType == ORG_TEACHER_INVITATION) {
            new AsyncOrgInviteList().execute(String.valueOf(orgId), mSearchBar.getText().toString(),
                    String.valueOf(pageIdx));
        } else if (currType == ORG_TEACHER_ATTEST) {
            new AsyncOrgAttestList().execute(String.valueOf(orgId), mSearchBar.getText().toString(),
                    String.valueOf(pageIdx));
        }
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, int position) {
        int userId = attestAdapter.getItem(position).getUserId();
        AttestState state = AttestState.UNCONFIRM;
        switch (view.getId()) {
            case R.id.simple_user_item_confirm:
                state = AttestState.CONFIRMED;
                break;
            case R.id.simple_user_item_refuse:
                state = AttestState.REJECTED;
                break;
            default:
                break;
        }
        new AsyncSubmitOrgAttest().execute(String.valueOf(orgId), String.valueOf(userId), String.valueOf(state.getValue()));
        attestAdapter.notifyUserStateChanged(position, state);
    }

    class AsyncPersonalAttestList extends AsyncTask<String, Integer, SimpleUserListWithInfo> {

        @Override
        protected SimpleUserListWithInfo doInBackground(String... params) {
            return MineParser.teacherAttest(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(SimpleUserListWithInfo listWithInfo) {
            if (listWithInfo != null) {
                if (pageIdx == 1) {
                    attestAdapter.setAdapterDataList(listWithInfo.getDataList());
                    mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    attestAdapter.addAdapterDataList(listWithInfo.getDataList());
                    mPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            } else {
                if (pageIdx == 1) {
                    mPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                    attestAdapter.setAdapterDataList(null);
                } else {
                    mPtrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
            if (attestAdapter.getItemCount() <= 0) {
                mPtrl.showEmptyView(true);
            } else {
                mPtrl.showEmptyView(false);
            }
        }
    }

    class AsyncOrgInviteList extends AsyncPersonalAttestList {
        @Override
        protected SimpleUserListWithInfo doInBackground(String... params) {
            return MineParser.teacherInvitation(params[0], params[1], params[2]);
        }
    }

    class AsyncOrgAttestList extends AsyncPersonalAttestList {

        @Override
        protected SimpleUserListWithInfo doInBackground(String... params) {
            return MineParser.orgTeacherAttest(params[0], params[1], params[2]);
        }
    }


    class AsyncSubmitPersonalAttest extends AsyncTask<String, Integer, SimpleResponse> {

        @Override
        protected SimpleResponse doInBackground(String... params) {
            return MineParser.submitTeacherAttest(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(SimpleResponse response) {
            if (response != null) {
                if (response.getContentInt() == SimpleResponse.SUCCESS && response.getCode() != SimpleResponse.SUCCESS) {
                    return;
                }
                if (response.getCode() == SimpleResponse.SUCCESS) {
                    Toast.makeText(SimpleListActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SimpleListActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SimpleListActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class AsyncSubmitOrgInvitation extends AsyncSubmitPersonalAttest {
        @Override
        protected SimpleResponse doInBackground(String... params) {
            return MineParser.submitOrgInvitation(params[0], params[1]);
        }
    }

    class AsyncSubmitOrgAttest extends AsyncSubmitPersonalAttest {

        @Override
        protected SimpleResponse doInBackground(String... params) {
            return MineParser.submitOrgAttest(params[0], params[1], params[2]);
        }
    }
}
