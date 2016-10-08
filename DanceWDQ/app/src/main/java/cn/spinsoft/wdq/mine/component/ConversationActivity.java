package cn.spinsoft.wdq.mine.component;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jingchen.pulltorefresh.PullToRefreshLayout;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.mine.biz.MineParser;
import cn.spinsoft.wdq.mine.biz.PrivateMsgInnerItem;
import cn.spinsoft.wdq.mine.biz.PrivateMsgInnerListWithInfo;
import cn.spinsoft.wdq.mine.widget.ConversationAdapter;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.ContentResolverUtil;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.widget.EmptyView;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by hushujun on 16/1/18.
 */
public class ConversationActivity extends BaseActivity implements RecyclerItemClickListener,
        PullToRefreshLayout.OnPullListener, TextView.OnEditorActionListener {
    private static final String TAG = ConversationActivity.class.getSimpleName();

    private TextView mBackTx, mTitleTx;
    private PullToRefreshLayout mPtrl;
    private RecyclerView mContentRv;
    private ConversationAdapter mAdapter;
//    private SimpleDraweeView mUserPhoto;
    private EditText mInputEt;

    private UserLogin mUserLogin;
    private int pageIdx = 1;
    private int fromUserId = -1;
    private int toUserId = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_conversation;
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        mBackTx = (TextView) findViewById(R.id.conversation_back);
        mTitleTx = (TextView) findViewById(R.id.conversation_title);
        mPtrl = (PullToRefreshLayout) findViewById(R.id.conversation_content);
        mPtrl.setEmptyView(new EmptyView(this));
        mContentRv = (RecyclerView) mPtrl.getPullableView();
        mContentRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ConversationAdapter(null, this);
        mContentRv.setAdapter(mAdapter);
//        mUserPhoto = (SimpleDraweeView) findViewById(R.id.bottom_input_photo);
        mInputEt = (EditText) findViewById(R.id.bottom_input_edit);
        mInputEt.setOnEditorActionListener(this);

        mPtrl.setOnPullListener(this);
        mBackTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        fromUserId = getIntent().getIntExtra(Constants.Strings.FROM_USERID, -1);
        toUserId = getIntent().getIntExtra(Constants.Strings.TO_USERID, -1);
        mTitleTx.setText(getIntent().getStringExtra(Constants.Strings.FROM_WHO));
        mPtrl.autoRefresh();
        mUserLogin = SharedPreferencesUtil.getInstance(this).getLoginUser();
        if (mUserLogin != null) {
//            mUserPhoto.setImageURI(Uri.parse(mUserLogin.getPhotoUrl()));
        }
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, int position) {

    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        pageIdx = 1;
        new AsyncConversation().execute(fromUserId, toUserId, pageIdx);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        pageIdx = 1;
        new AsyncConversation().execute(fromUserId, toUserId, pageIdx);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            String message = mInputEt.getText().toString();
            if (TextUtils.isEmpty(message)) {
                Toast.makeText(this, "请输入您的消息内容", Toast.LENGTH_SHORT).show();
                return true;
            }
            PrivateMsgInnerItem innerItem = new PrivateMsgInnerItem();
            innerItem.setSendBySelf(true);
            innerItem.setContent(message);
            innerItem.setHeadUrl(mUserLogin.getPhotoUrl());
            innerItem.setCreateTime("刚刚");
            mAdapter.insertMessage(innerItem);
            new AsyncSendMessage().execute(String.valueOf(toUserId), String.valueOf(fromUserId), message);

            mInputEt.setText("");
            ContentResolverUtil.hideIMM(v);
            mContentRv.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            return true;
        }
        return false;
    }

    class AsyncConversation extends AsyncTask<Integer, Integer, PrivateMsgInnerListWithInfo> {

        @Override
        protected PrivateMsgInnerListWithInfo doInBackground(Integer... params) {
            return MineParser.getPrivateMsg(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(PrivateMsgInnerListWithInfo listWithInfo) {
            if (listWithInfo != null) {
                if (pageIdx == 1) {
                    mAdapter.setAdapterDataList(listWithInfo.getDataList());
                    mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mContentRv.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                } else {
                    mAdapter.addAdapterDataList(listWithInfo.getDataList());
                    mPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    mContentRv.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                }
//                mContentRv.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        new AsyncConversation().execute(fromUserId, toUserId, pageIdx);
//                    }
//                },2000);
            } else {
                if (pageIdx == 1) {
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
    }

    class AsyncSendMessage extends AsyncTask<String, Integer, SimpleResponse> {

        @Override
        protected SimpleResponse doInBackground(String... params) {
            return MineParser.sendPrivateMsg(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(SimpleResponse simpleResponse) {
            if (simpleResponse != null) {
                Toast.makeText(ConversationActivity.this, simpleResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
