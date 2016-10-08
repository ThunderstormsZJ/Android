package cn.spinsoft.wdq.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.db.DBOperationUtil;
import cn.spinsoft.wdq.enums.PageType;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.search.biz.SearchHandler;
import cn.spinsoft.wdq.search.frag.HistoryFrag;
import cn.spinsoft.wdq.search.frag.ResultFrag;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.ContentResolverUtil;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.widget.SearchBar;

/**
 * Created by zhoujun on 15/11/12.
 */
public class SearchActivity extends BaseActivity implements TextView.OnEditorActionListener, TextWatcher,
        View.OnClickListener, SearchBar.OnCancelClickListenerCallBack {
    private static final String TAG = SearchActivity.class.getSimpleName();

    private SearchBar mKeyEt;

    private BaseFragment mHistoryFrag, mResultFrag;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initHandler() {
        mHandler = new SearchHandler();

        UserLogin loginUser = SharedPreferencesUtil.getInstance(this).getLoginUser();
        if (loginUser != null) {
            SearchHandler.watcherUserId = loginUser.getUserId();
        }
        Intent intent = getIntent();
        String type = intent.getStringExtra(Constants.Strings.PAGE_TYPE);
        if (TextUtils.isEmpty(type)) {
//            Toast.makeText(this, "未指定搜索类型", Toast.LENGTH_SHORT).show();
        } else {
            SearchHandler.Status.pageType = PageType.getEnum(type);
        }
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView backTx = (TextView) findViewById(R.id.search_back);
        mKeyEt = (SearchBar) findViewById(R.id.search_input);

        mKeyEt.setOnEditorActionListener(this);
        mKeyEt.addTextChangedListener(this);
        backTx.setOnClickListener(this);
        mKeyEt.setOnClickListener(this);
        mKeyEt.setOnCancelClickListener(this);

        mHistoryFrag = (BaseFragment) changeContentFragment(R.id.search_child_container,
                mHistoryFrag, HistoryFrag.class.getName());
    }

    public void setHistoryText(String historyKey) {
        mKeyEt.setText(historyKey);
    }

    private void doSearch(TextView v) {
        ContentResolverUtil.hideIMM(v);
        doSearch(v.getText().toString());
    }

    public void doSearch(String keyWords) {
        if (TextUtils.isEmpty(keyWords)) {
            Toast.makeText(this, "请输入搜索关键字", Toast.LENGTH_SHORT).show();
            return;
        }
        DBOperationUtil.getInstance(this).insertSearchRecode(keyWords, SearchHandler.Status.pageType.getValue());
        // TODO: 15/11/18 to do search
        SearchHandler.Status.keyWord = keyWords;
        changeFragToResult();

        switch (SearchHandler.Status.pageType) {
            case VIDEO:
                mHandler.sendEmptyMessage(R.id.msg_search_get_video_list);
                break;
            case FRIEND:
                mHandler.sendEmptyMessage(R.id.msg_search_get_friend_list);
                break;
            case ORG:
                mHandler.sendEmptyMessage(R.id.msg_search_get_org_list);
                break;
            default:
                Toast.makeText(this, "不支持的搜索类型", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void changeFragToResult() {
        mResultFrag = (BaseFragment) changeContentFragment(R.id.search_child_container,
                mResultFrag, ResultFrag.class.getName());
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            doSearch(v);
            return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0) {
            changeContentFragment(R.id.search_child_container, mHistoryFrag, HistoryFrag.class.getName());
        }
    }

    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(mKeyEt.getText())) {
            if (mPageLast instanceof ResultFrag) {
                changeContentFragment(R.id.search_child_container, mHistoryFrag, HistoryFrag.class.getName());
            } else {
                super.onBackPressed();
            }
        } else {
            mKeyEt.setText("");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_back:
                finish();
                break;
            case R.id.search_input:
                ContentResolverUtil.showIMM(v);
                break;
            default:
                break;
        }
    }

    @Override
    public void OnCancelClickListener(View v) {
        ContentResolverUtil.hideIMM(v);
        if (TextUtils.isEmpty(mKeyEt.getText())) {
            if (mPageLast instanceof ResultFrag) {
                changeContentFragment(R.id.search_child_container, mHistoryFrag, HistoryFrag.class.getName());
            } else {
                finish();
            }
        } else {
            mKeyEt.setText("");
        }
    }
}
