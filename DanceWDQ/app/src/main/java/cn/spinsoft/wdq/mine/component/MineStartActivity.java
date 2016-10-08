package cn.spinsoft.wdq.mine.component;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.browse.biz.BrowseHandler;
import cn.spinsoft.wdq.discover.DiscoverDetailActivity;
import cn.spinsoft.wdq.discover.biz.DiscoverHandler;
import cn.spinsoft.wdq.discover.biz.DiscoverItemBean;
import cn.spinsoft.wdq.discover.biz.DiscoverListWithInfo;
import cn.spinsoft.wdq.discover.biz.DiscoverParser;
import cn.spinsoft.wdq.discover.widget.DiscoverListAdapter;
import cn.spinsoft.wdq.effect.AnimatorEffect;
import cn.spinsoft.wdq.enums.DiscoverType;
import cn.spinsoft.wdq.mine.biz.MineParser;
import cn.spinsoft.wdq.mine.widget.PublishTypeChoosePop;
import cn.spinsoft.wdq.user.UserDetailsActivity;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SecurityUtils;
import cn.spinsoft.wdq.utils.UrlManager;
import cn.spinsoft.wdq.widget.ConfirmDialog;
import cn.spinsoft.wdq.widget.EmptyView;
import cn.spinsoft.wdq.widget.RadioGroup;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;
import cn.spinsoft.wdq.widget.ShareBoardDialog;

/**
 * Created by zhoujun on 15/12/16.
 */
public class MineStartActivity extends BaseActivity implements RecyclerItemClickListener, View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, PullToRefreshLayout.OnPullListener, Handler.Callback, ShareBoardDialog.ShareBoardDiaListener {
    private static final String TAG = MineStartActivity.class.getSimpleName();
    private View mLabelsSlide;
    private PullToRefreshLayout mPtrl;

    private DiscoverListAdapter mListAdapter;
    private DiscoverItemBean mItemBean;

    private int watcherUserId = 0;
    private int pageIdx = 1;
    private int mForwardPosition;
    private DiscoverType discoverType = DiscoverType.ACTIVITY;
    private PublishTypeChoosePop typeChoosePop;
    private ConfirmDialog mConfirmDia;
    private UMShareListener mUmShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
            mHandler.sendEmptyMessage(R.id.msg_report_forward_discover);
            mListAdapter.notifyItemForwardChanged(mForwardPosition);
            Toast.makeText(MineStartActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Toast.makeText(MineStartActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            Toast.makeText(MineStartActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine_start;
    }

    @Override
    protected void initHandler() {
        mHandler = new DiscoverHandler();
        mHandler.addCallback(DiscoverHandler.CHILD_HOST, this);
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView mBackTx = (TextView) findViewById(R.id.mine_start_back);
        ImageView mAddImg = (ImageView) findViewById(R.id.mine_start_add);
        mAddImg.setVisibility(View.GONE);
        RadioGroup mLabelsRg = (RadioGroup) findViewById(R.id.mine_start_labels);
        mLabelsSlide = findViewById(R.id.mine_start_slide);
        mPtrl = (PullToRefreshLayout) findViewById(R.id.mine_start_content);
        mPtrl.setEmptyView(new EmptyView(this));
        mPtrl.setOnPullListener(this);
        RecyclerView mContentRv = (RecyclerView) mPtrl.getPullableView();

        mContentRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mListAdapter = new DiscoverListAdapter(null, this, this);
        mListAdapter.setListType(Constants.Strings.DELETE_MODE);
        mContentRv.setAdapter(mListAdapter);

        mBackTx.setOnClickListener(this);
//        mAddImg.setOnClickListener(this);
        mLabelsRg.setOnCheckedChangeListener(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        watcherUserId = getIntent().getIntExtra(Constants.Strings.USER_ID, -1);
        mPtrl.autoRefresh();
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, int position) {
        mItemBean = mListAdapter.getItem(position);
        switch (view.getId()) {
            case R.id.discover_list_item_photo:
                Intent userIntent = new Intent(this, UserDetailsActivity.class);
                userIntent.putExtra(Constants.Strings.USER_ID, mItemBean.getUserId());
                userIntent.putExtra(Constants.Strings.USER_PHOTO, mItemBean.getPhotoUrl());
                userIntent.putExtra(Constants.Strings.USER_NAME, mItemBean.getNickName());
                startActivity(userIntent);
                break;
            case R.id.discover_list_item_like:
                LogUtil.i(TAG, "onItemClicked=====>>like");
                if (SecurityUtils.isUserValidity(this, BrowseHandler.watcherUserId)) {
                    mListAdapter.notifyItemLikeChanged(!mItemBean.isLike(), position);
                    new AsyncLike().execute(mItemBean.getType().getValue(), mItemBean.getEventId(),
                            mItemBean.getUserId(), mItemBean.getUserId());//浏览者跟所有者相同
                }
                break;
            case R.id.discover_list_item_forward://转发
                LogUtil.i(TAG, "onItemClicked=====>>forward");
                if (SecurityUtils.isUserValidity(this, BrowseHandler.watcherUserId)) {
                    new ShareBoardDialog(this, this).setShare_medias(Constants.Arrays.SHARDBOARD).show();
//                    mItemBean.setForwardCount(mItemBean.getForwardCount() + 1);
                    mForwardPosition = position;
                }
                break;
            case R.id.discover_recruit_delete:
            case R.id.discover_contest_delete:
            case R.id.discover_list_item_delete://删除
                if (SecurityUtils.isUserValidity(this, BrowseHandler.watcherUserId)) {
                    DiscoverHandler.Status.typeId = mItemBean.getType().getValue();
                    DiscoverHandler.Status.eventId = mItemBean.getEventId();
                    final int delItemPosition = position;
                    mConfirmDia = new ConfirmDialog(this, ConfirmDialog.Type.DELETE, new ConfirmDialog.OnConfirmClickListenter() {
                        @Override
                        public void onConfirmClick(View v) {
                            if (v.getId() == R.id.dia_confirm_confirm) {
                                mListAdapter.removeItem(delItemPosition);
                                mHandler.sendEmptyMessage(R.id.msg_discover_delete_by_id);
                            }
                        }
                    });
                    mConfirmDia.show();
                }
                break;
            default:
                Intent intent = new Intent(this, DiscoverDetailActivity.class);
                intent.putExtra(Constants.Strings.DISCOVER_TYPE_ID, mItemBean.getType().getValue());
                intent.putExtra(Constants.Strings.DISCOVER_EVENT_ID, mItemBean.getEventId());
                intent.putExtra(Constants.Strings.USER_ID, mItemBean.getUserId());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void shareBoardOnclickListener(View v) {
        UMImage umImage = new UMImage(this, mItemBean.getSmallImgs().get(0));
        new ShareAction(this).setPlatform((SHARE_MEDIA) v.getTag())
                .setCallback(mUmShareListener)
                .withMedia(umImage)
                .withTargetUrl(UrlManager.getUrl(UrlManager.UrlName.DISCOVER_SHARE_GOTO_PAGE)
                        + "?typeId=" + mItemBean.getForwardId() + "&f_type=" + mItemBean.getForwarType())
                .withTitle(mItemBean.getTitle())
                .withText(mItemBean.getContent())
                .share();

        DiscoverHandler.Status.eventId = mItemBean.getEventId();
        DiscoverHandler.Status.typeId = mItemBean.getType().getValue();
        DiscoverHandler.Status.ownerId = mItemBean.getUserId();
        DiscoverHandler.Status.forwarWay = v.getTag().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_start_back:
                finish();
                break;
            case R.id.mine_start_add:
                if (typeChoosePop == null) {
                    typeChoosePop = new PublishTypeChoosePop(this);
                }
                typeChoosePop.show(getWindow().getDecorView());
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        View view = group.findViewById(checkedId);
        AnimatorEffect.smoothHorizontalSlideTo(mLabelsSlide, view);

        DiscoverType type;
        switch (checkedId) {
            case R.id.mine_start_activities:
                type = DiscoverType.ACTIVITY;
                break;
            case R.id.mine_start_topics:
                type = DiscoverType.TOPIC;
                break;
            case R.id.mine_start_contest:
                type = DiscoverType.CONTEST;
                break;
            case R.id.mine_start_recruit:
                type = DiscoverType.RECRUIT;
                break;
            case R.id.mine_start_other:
                type = DiscoverType.OTHER;
                break;
            default:
                type = DiscoverType.UNKNOWN;
                break;
        }
        if (type != discoverType) {
            discoverType = type;
            mPtrl.autoRefresh();
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        pageIdx = 1;
        new AsyncMineStart().execute(watcherUserId, pageIdx, discoverType.getValue());
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        pageIdx++;
        new AsyncMineStart().execute(watcherUserId, pageIdx, discoverType.getValue());
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.msg_discover_by_id_delete:
                if (msg.obj != null) {
                    SimpleResponse response = (SimpleResponse) msg.obj;
                    Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;

        }
        return true;
    }

    class AsyncMineStart extends AsyncTask<Integer, Integer, DiscoverListWithInfo> {

        @Override
        protected DiscoverListWithInfo doInBackground(Integer... params) {
            return MineParser.getMineStartList(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(DiscoverListWithInfo listWithInfo) {
            if (listWithInfo != null) {
                if (pageIdx == 1) {
                    mListAdapter.setAdapterDataList(listWithInfo.getDataList());
                    mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    mListAdapter.addAdapterDataList(listWithInfo.getDataList());
                    mPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            } else {
                if (pageIdx == 1) {
                    mPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                    mListAdapter.setAdapterDataList(null);
                } else {
                    mPtrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
            if (mListAdapter.getItemCount() <= 0) {//没有信息显示空白页面
                mPtrl.showEmptyView(true);
            } else {
                mPtrl.showEmptyView(false);
            }

        }
    }

    class AsyncLike extends AsyncTask<Integer, Integer, SimpleResponse> {

        @Override
        protected SimpleResponse doInBackground(Integer... params) {
            return DiscoverParser.like(params[0], params[1], params[2], params[3]);
        }

        @Override
        protected void onPostExecute(SimpleResponse simpleResponse) {
            if (simpleResponse != null) {
                Toast.makeText(MineStartActivity.this, simpleResponse.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MineStartActivity.this, "点赞失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
