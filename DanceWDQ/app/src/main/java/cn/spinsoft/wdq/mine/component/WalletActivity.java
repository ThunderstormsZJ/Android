package cn.spinsoft.wdq.mine.component;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.mine.biz.MineParser;
import cn.spinsoft.wdq.mine.biz.WalletRecodeListWithInfo;
import cn.spinsoft.wdq.mine.widget.WalletRecodeAdapter;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.widget.ConfirmDialog;
import cn.spinsoft.wdq.widget.EmptyView;

/**
 * Created by zhoujun on 16/1/12.
 */
public class WalletActivity extends BaseActivity implements PullToRefreshLayout.OnPullListener,
        View.OnClickListener, UMAuthListener {
    private static final String TAG = WalletActivity.class.getSimpleName();

    private PullToRefreshLayout mPtrl;
    private WalletRecodeAdapter mWalletAdapter;
    private TextView mBalanceTx;
    private UMShareAPI mShareAPI;

    private int watcherId = -1;
    private int pageIdx = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet;
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView backTx = (TextView) findViewById(R.id.mine_wallet_back);
        mBalanceTx = (TextView) findViewById(R.id.mine_wallet_balance);
        Button withdrawTx = (Button) findViewById(R.id.mine_wallet_withdraw);

        mPtrl = (PullToRefreshLayout) findViewById(R.id.mine_wallet_content);
        mPtrl.setEmptyView(new EmptyView(this));
        mPtrl.setOnPullListener(this);
        RecyclerView contentRv = (RecyclerView) mPtrl.getPullableView();
        contentRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mWalletAdapter = new WalletRecodeAdapter(null);
        contentRv.setAdapter(mWalletAdapter);

        mShareAPI = UMShareAPI.get(this);
        backTx.setOnClickListener(this);
        withdrawTx.setOnClickListener(this);
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
        new AsyncWallet().execute(watcherId, pageIdx);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        pageIdx++;
        new AsyncWallet().execute(watcherId, pageIdx);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_wallet_back:
                finish();
                break;
            case R.id.mine_wallet_withdraw://提现
                new ConfirmDialog(this, ConfirmDialog.Type.ADDWEIXI, new ConfirmDialog.OnConfirmClickListenter() {
                    @Override
                    public void onConfirmClick(View v) {
                        if (v.getId() == R.id.dia_confirm_confirm) {//执行微信登录
                            doThirdByWEIXI();
                        }
                    }
                }).show();
                break;
            default:
                break;
        }
    }

    public void doThirdByWEIXI() {
        mShareAPI.deleteOauth(this, SHARE_MEDIA.WEIXIN, this);
        mShareAPI.doOauthVerify(this, SHARE_MEDIA.WEIXIN, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==Constants.Ints.REQUEST_CODE_BALANCE){
                mBalanceTx.setText(data.getStringExtra(Constants.Strings.USER_BALANCE));
                mPtrl.autoRefresh();
            }
        }
    }

    @Override
    public void onComplete(SHARE_MEDIA share_media, int action, Map<String, String> map) {
        if (share_media == SHARE_MEDIA.WEIXIN) {
            if (action == ACTION_AUTHORIZE) {//认证
                mShareAPI.getPlatformInfo(this, share_media, this);
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            }else if(action == ACTION_GET_PROFILE){
                if(map==null || map.get("errcode")!=null){
                    return ;
                }
                Intent intent = new Intent(WalletActivity.this,WalletWithDraw.class);
                intent.putExtra(Constants.Strings.USER_OPENID, map.get("openid"));
                intent.putExtra(Constants.Strings.USER_NAME, map.get("nickname"));
                intent.putExtra(Constants.Strings.USER_PHOTO, map.get("headimgurl"));
                intent.putExtra(Constants.Strings.USER_BALANCE, mBalanceTx.getText());
                intent.putExtra(Constants.Strings.USER_MOBILE, SharedPreferencesUtil.getInstance(this).getLoginUser().getMobile());
                startActivityForResult(intent, Constants.Ints.REQUEST_CODE_BALANCE);
            }
        }
    }

    @Override
    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

    }

    @Override
    public void onCancel(SHARE_MEDIA share_media, int i) {

    }

    class AsyncWallet extends AsyncTask<Integer, Integer, WalletRecodeListWithInfo> {

        @Override
        protected WalletRecodeListWithInfo doInBackground(Integer... params) {
            return MineParser.getWalletRecode(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(WalletRecodeListWithInfo listWithInfo) {
            if (listWithInfo != null) {
                if (TextUtils.isEmpty(listWithInfo.getBalance())) {
                    mBalanceTx.setText("0.00");
                } else {
                    mBalanceTx.setText(listWithInfo.getBalance());
                }
                if (pageIdx == 1) {
                    mWalletAdapter.setAdapterDataList(listWithInfo.getDataList());
                    mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    mWalletAdapter.addAdapterDataList(listWithInfo.getDataList());
                    mPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            } else {
                if (pageIdx == 1) {
                    mPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                    mWalletAdapter.setAdapterDataList(null);
                } else {
                    mPtrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
            if (mWalletAdapter.getItemCount() <= 0) {
                mPtrl.showEmptyView(true);
            } else {
                mPtrl.showEmptyView(false);
            }
        }
    }
}
