package cn.spinsoft.wdq.discover;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.bean.CommentItem;
import cn.spinsoft.wdq.bean.CommentListWithInfo;
import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.discover.biz.DiscoverDetail;
import cn.spinsoft.wdq.discover.biz.DiscoverHandler;
import cn.spinsoft.wdq.discover.widget.DiscoverDetailAdapter;
import cn.spinsoft.wdq.discover.widget.SignInDialog;
import cn.spinsoft.wdq.enums.DiscoverType;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.org.OrgDetailsActivity;
import cn.spinsoft.wdq.user.UserDetailsActivity;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.ContentResolverUtil;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SecurityUtils;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.utils.UrlManager;
import cn.spinsoft.wdq.utils.session.SessionHelper;
import cn.spinsoft.wdq.widget.MoreChoiceDialog;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;
import cn.spinsoft.wdq.widget.ShareBoardDialog;

/**
 * Created by zhoujun on 15/12/7.
 */
public class DiscoverDetailActivity extends BaseActivity implements RecyclerItemClickListener, Observer,
        Handler.Callback, SignInDialog.OnSignInConfirmListener, PullToRefreshLayout.OnPullListener,
        ShareBoardDialog.ShareBoardDiaListener, View.OnClickListener {
    private static final String TAG = DiscoverDetailActivity.class.getSimpleName();

    private PullToRefreshLayout mPtrl;
    private TextView mTitleTx;
    private EditText mCommentEt;
    private ImageView moreImg;
    private ViewStub mBottomVs;
    //    private SimpleDraweeView mWatcherPhotoImg;
    private DiscoverDetailAdapter mDetailAdapter;
    private UserLogin watcherUser;
    private DiscoverDetail mDetailInfo;

    private int mForwardPosition;
    private SignInDialog signInDialog;
    private boolean isFirest = true;//首次刷新,刷新所有数据,之后的刷新值刷评论列表
    private UMShareListener mUmShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
            mHandler.sendEmptyMessage(R.id.msg_report_forward_discover);
            mDetailAdapter.notifyItemChanged(mForwardPosition);
            Toast.makeText(DiscoverDetailActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Toast.makeText(DiscoverDetailActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            Toast.makeText(DiscoverDetailActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_discover_details;
    }

    @Override
    protected void initHandler() {
        mHandler = new DiscoverHandler();
        watcherUser = SharedPreferencesUtil.getInstance(this).getLoginUser();
        if (watcherUser != null) {
            DiscoverHandler.watcherUserId = watcherUser.getUserId();
        }

        Intent intent = getIntent();
        DiscoverHandler.Status.typeId = intent.getIntExtra(Constants.Strings.DISCOVER_TYPE_ID, 1);
        DiscoverHandler.Status.eventId = intent.getIntExtra(Constants.Strings.DISCOVER_EVENT_ID, 0);
        DiscoverHandler.Status.ownerId = intent.getIntExtra(Constants.Strings.USER_ID, -1);
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView mBackTx = (TextView) findViewById(R.id.discover_detail_back);
        moreImg = (ImageView) findViewById(R.id.discover_detail_more);
        mTitleTx = (TextView) findViewById(R.id.discover_detail_title);

        mPtrl = (PullToRefreshLayout) findViewById(R.id.discover_detail_content);
        mPtrl.setVisibility(View.INVISIBLE);
        RecyclerView mContentRv = (RecyclerView) mPtrl.getPullableView();
        mPtrl.setOnPullListener(this);
//        mWatcherPhotoImg = (SimpleDraweeView) findViewById(R.id.bottom_input_photo);

        mBottomVs = (ViewStub) findViewById(R.id.page_bottom);
        if (DiscoverHandler.Status.typeId == DiscoverType.RECRUIT.getValue()) {//招聘
            if (DiscoverHandler.Status.ownerId != DiscoverHandler.watcherUserId) {
                mBottomVs.setLayoutResource(R.layout.ly_bottom_contact);
                View bottomView = mBottomVs.inflate();
                TextView contactTx = (TextView) bottomView.findViewById(R.id.discover_detail_contact);
                contactTx.setOnClickListener(this);
            }
        } else {
            mBottomVs.setLayoutResource(R.layout.ly_bottom_input);
            View bottomView = mBottomVs.inflate();
            mCommentEt = (EditText) bottomView.findViewById(R.id.bottom_input_edit);
            mCommentEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEND) {
                        if (SecurityUtils.isUserValidity(DiscoverDetailActivity.this, DiscoverHandler.watcherUserId)) {
                            sendComment(v);
                            return true;
                        }
                    }
                    return false;
                }
            });
            moreImg.setOnClickListener(this);
        }

        mContentRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mDetailAdapter = new DiscoverDetailAdapter(null, this, this);
        mContentRv.setAdapter(mDetailAdapter);

        mBackTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferencesUtil.getInstance(this).addObserver(this);
    }

    @Override
    protected void onDestroy() {
        SharedPreferencesUtil.getInstance(this).deleteObserver(this);
        super.onDestroy();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHandler.addCallback(DiscoverHandler.CHILD_HOST, this);
        UserLogin userLogin = SharedPreferencesUtil.getInstance(this).getLoginUser();
        if (userLogin != null && !TextUtils.isEmpty(userLogin.getPhotoUrl())) {
//            mWatcherPhotoImg.setImageURI(Uri.parse(userLogin.getPhotoUrl()));
        }
        mTitleTx.setText("详情");
        if (DiscoverHandler.Status.typeId == DiscoverType.RECRUIT.getValue()) {
            moreImg.setVisibility(View.GONE);
            mTitleTx.setText("职位详情");
            mHandler.sendEmptyMessage(R.id.msg_discover_get_detail);
            mPtrl.setPullUpEnable(false);
//            mPtrl.setPullDownEnable(false);
        } else {
            mPtrl.autoRefresh();
        }
    }

    private void sendComment(View v) {
        CharSequence comment = mCommentEt.getText();
        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(this, "请输入您的评论内容", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentResolverUtil.hideIMM(v);
        DiscoverHandler.Status.comment = comment.toString();
        mHandler.sendEmptyMessage(R.id.msg_send_comment_discover);

        mCommentEt.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.discover_detail_more:
                MoreChoiceDialog dia = new MoreChoiceDialog(DiscoverDetailActivity.this);
                dia.show();
                dia.addFunction("举报", new MoreChoiceDialog.OnFunctionClickListener() {
                    @Override
                    public void OnFunctionClick(View v) {
                        if (SecurityUtils.isUserValidity(DiscoverDetailActivity.this, DiscoverHandler.watcherUserId)) {
                            mHandler.sendEmptyMessage(R.id.msg_discover_add_report);
                            Toast.makeText(DiscoverDetailActivity.this, "举报成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.discover_detail_contact:
//                Toast.makeText(this,"contact",Toast.LENGTH_SHORT).show();
                if (SecurityUtils.isUserValidity(DiscoverDetailActivity.this, DiscoverHandler.watcherUserId)) {
                    SessionHelper.startP2PSession(this, "orgid" + mDetailAdapter.getDiscoverDetail().getOrgId(), mDetailAdapter.getDiscoverDetail().getNickName());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, int position) {
        if (position == 0) {
            mDetailInfo = mDetailAdapter.getDiscoverDetail();
            if (mDetailInfo == null) return;
            switch (view.getId()) {
                case R.id.discover_detail_photo:
                    Intent intent = new Intent(this, UserDetailsActivity.class);
                    intent.putExtra(Constants.Strings.USER_ID, DiscoverHandler.Status.ownerId);
                    intent.putExtra(Constants.Strings.USER_NAME, mDetailInfo.getNickName());
                    intent.putExtra(Constants.Strings.USER_PHOTO, mDetailInfo.getPhotoUrl());
                    startActivity(intent);
                    break;
                case R.id.discover_detail_forward://转发
                    if (SecurityUtils.isUserValidity(this, DiscoverHandler.watcherUserId)) {
                        DiscoverHandler.Status.ownerId = mDetailInfo.getUserId();
                        new ShareBoardDialog(this, this).setShare_medias(Constants.Arrays.SHARDBOARD).show();
                        mForwardPosition = position;
                    }
                    break;
                case R.id.discover_detail_signIn:
                    if (SecurityUtils.isUserValidity(this, DiscoverHandler.watcherUserId)) {
                        if (signInDialog == null) {
                            signInDialog = new SignInDialog(this, this);
                        }
                        signInDialog.show();
                    }
                    break;
                case R.id.discover_detail_like:
                    if (SecurityUtils.isUserValidity(this, DiscoverHandler.watcherUserId)) {
                        SimpleItemData itemData = new SimpleItemData(watcherUser.getPhotoUrl(), watcherUser.getUserId());
                        if (mDetailInfo.isLike()) {
                            mDetailAdapter.deleteLikeHead(itemData);
                        } else {
                            mDetailAdapter.addLikeHead(itemData);
                        }
                        mHandler.sendEmptyMessage(R.id.msg_report_like_discover);
                    }
                    break;
                default://动态添加的头像,没有指定具体的ID
                    List<SimpleItemData> likeUsers = mDetailInfo.getLikeUsers();
                    int pos = (int) view.getTag();
                    if (pos >= 0 && pos < likeUsers.size()) {
                        Intent intentUser = null;
                        if (likeUsers.get(pos).getSubId() > 0) {
                            intentUser = new Intent(this, OrgDetailsActivity.class);
                            intentUser.putExtra(Constants.Strings.ORG_ID, likeUsers.get(pos).getSubId());
                            intentUser.putExtra(Constants.Strings.USER_ID, likeUsers.get(pos).getId());
                            intentUser.putExtra(Constants.Strings.ORG_LOGO, likeUsers.get(pos).getName());
                        } else {
                            intentUser = new Intent(this, UserDetailsActivity.class);
                            intentUser.putExtra(Constants.Strings.USER_ID, likeUsers.get(pos).getId());
                            intentUser.putExtra(Constants.Strings.USER_PHOTO, likeUsers.get(pos).getName());
                        }
                        startActivity(intentUser);
                    }
                    break;
            }
        } else {
            switch (view.getId()) {
                case R.id.comment_list_item_photo:
                    CommentItem itemData = mDetailAdapter.getItem(position);
                    if (itemData.getOrgId() > 0) {
                        OrgDetailsActivity.start(this, itemData.getNickName(), itemData.getPhotoUrl(), itemData.getOrgId());
                    } else {
                        UserDetailsActivity.start(this, itemData.getNickName(), itemData.getPhotoUrl(), itemData.getUserId());
                    }
                    break;
            }
        }
    }

    @Override
    public void shareBoardOnclickListener(View v) {
        UMImage umImage = null;
        if (mDetailInfo.getSmallImageUrls() != null && mDetailInfo.getSmallImageUrls().size() > 0) {
            umImage = new UMImage(this, mDetailInfo.getSmallImageUrls().get(0));
        }
        new ShareAction(this).setPlatform((SHARE_MEDIA) v.getTag())
                .setCallback(mUmShareListener)
                .withMedia(umImage)
                .withTargetUrl(UrlManager.getUrl(UrlManager.UrlName.DISCOVER_SHARE_GOTO_PAGE)
                        + "?typeId=" + DiscoverHandler.Status.eventId + "&f_type=" + DiscoverHandler.Status.typeId)
                .withTitle(mDetailInfo.getTitle())
                .withText(mDetailInfo.getContent())
                .share();

        DiscoverHandler.Status.forwarWay = v.getTag().toString();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.msg_discover_detail_got:
                if (msg.obj != null) {
                    isFirest = false;//成功获取到详情,即不再是首次刷新
                    mDetailAdapter.setDiscoverDetail((DiscoverDetail) msg.obj);
                    mPtrl.setVisibility(View.VISIBLE);
                    if (DiscoverHandler.Status.typeId == DiscoverType.RECRUIT.getValue()) {
                        mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                    }
                } else {
                    if (DiscoverHandler.Status.typeId == DiscoverType.RECRUIT.getValue()) {
                        mPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                    }
                }
                break;
            case R.id.msg_discover_comments_got:
                if (msg.obj != null) {
                    CommentListWithInfo listWithInfo = (CommentListWithInfo) msg.obj;
                    if (DiscoverHandler.Status.pageIdx == 1) {
                        mDetailAdapter.setAdapterDataList(listWithInfo.getDataList());
                        mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                    } else {
                        mDetailAdapter.addAdapterDataList(listWithInfo.getDataList());
                        mPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    }
                } else {
                    if (DiscoverHandler.Status.pageIdx == 1) {
                        mPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                        mDetailAdapter.setAdapterDataList(null);
                    } else {
                        mPtrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                    }
                }
                break;
            case R.id.msg_like_reported_discover:
                if (msg.obj != null) {
                    SimpleResponse success = (SimpleResponse) msg.obj;
//                    Toast.makeText(DiscoverDetailActivity.this, success.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(DiscoverDetailActivity.this, "喜欢失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.msg_comment_sent_discover:
                if (msg.obj != null) {
                    SimpleResponse response = (SimpleResponse) msg.obj;
                    Toast.makeText(DiscoverDetailActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    if (response.getCode() == SimpleResponse.SUCCESS) {
                        mPtrl.autoRefresh();
                    }
                }
                break;
            case R.id.msg_discover_sign_in_reported:
                if (msg.obj != null) {
                    SimpleResponse response = (SimpleResponse) msg.obj;
                    Toast.makeText(DiscoverDetailActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DiscoverDetailActivity.this, "报名失败,请重试", Toast.LENGTH_SHORT).show();
                }
            case R.id.msg_forward_reported_discover:

                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onSignInConfirm(String[] signParams) {
        LogUtil.i(TAG, "onSignInConfirm:" + Arrays.toString(signParams));
        DiscoverHandler.Status.signParams = signParams;
        mHandler.sendEmptyMessage(R.id.msg_discover_report_sign_in);
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        DiscoverHandler.Status.pageIdx = 1;
        mHandler.sendEmptyMessage(R.id.msg_discover_get_detail);
        if (DiscoverHandler.Status.typeId != DiscoverType.RECRUIT.getValue()) {
            mHandler.sendEmptyMessage(R.id.msg_discover_get_comments);
        }
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        DiscoverHandler.Status.pageIdx++;
        mHandler.sendEmptyMessage(R.id.msg_discover_get_comments);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data != null) {
            DiscoverHandler.watcherUserId = ((UserLogin) data).getUserId();
        } else {
            DiscoverHandler.watcherUserId = -1;
        }
    }
}
