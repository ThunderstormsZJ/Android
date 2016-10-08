package cn.spinsoft.wdq.video;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.bean.CommentItem;
import cn.spinsoft.wdq.bean.CommentListWithInfo;
import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.browse.biz.BrowseHandler;
import cn.spinsoft.wdq.browse.biz.BrowseParser;
import cn.spinsoft.wdq.browse.biz.WechatPrepay;
import cn.spinsoft.wdq.enums.Attention;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.org.OrgDetailsActivity;
import cn.spinsoft.wdq.user.UserDetailsActivity;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.ContentResolverUtil;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SecurityUtils;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.utils.UrlManager;
import cn.spinsoft.wdq.video.biz.DanceVideoBean;
import cn.spinsoft.wdq.video.biz.DetailHandler;
import cn.spinsoft.wdq.video.biz.VideoDetailBean;
import cn.spinsoft.wdq.video.widget.TipsChoiceDialog;
import cn.spinsoft.wdq.video.widget.VideoDetailsAdapter;
import cn.spinsoft.wdq.widget.EmptyView;
import cn.spinsoft.wdq.widget.MoreChoiceDialog;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;
import cn.spinsoft.wdq.widget.ShareBoardDialog;

/**
 * Created by zhoujun on 16/2/26.
 */
public class VideoDetailsNewActivity extends BaseActivity implements PullToRefreshLayout.OnPullListener,
        RecyclerItemClickListener, Handler.Callback, Observer, TextView.OnEditorActionListener, View.OnClickListener,
        ShareBoardDialog.ShareBoardDiaListener, TipsChoiceDialog.OnTipsChoiceListener {
    private static final String TAG = VideoDetailsNewActivity.class.getSimpleName();

    private PullToRefreshLayout mPtrl;
    private EditText mCommentEt;
    private VideoDetailsAdapter mVideoDetailsAdapter;
    private DanceVideoBean mDanceVideo;
    private VideoDetailBean mVideoDetail;
    private TipsChoiceDialog mTipDia;
    private RecyclerView contentRv;
    private boolean isSendComment = false;
    private IWXAPI mWXAPI;//微信支付

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
            mHandler.sendEmptyMessage(R.id.msg_report_forward_video);
            mVideoDetail.setForwardCount(mVideoDetail.getForwardCount() + 1);
            mVideoDetailsAdapter.notifyItemChanged(0);
            Toast.makeText(VideoDetailsNewActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Toast.makeText(VideoDetailsNewActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            Toast.makeText(VideoDetailsNewActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_detail;
    }

    @Override
    protected void initHandler() {
        mHandler = new DetailHandler();

        Intent intent = getIntent();
        mDanceVideo = intent.getParcelableExtra(Constants.Strings.VIDEO_BEAN);
        if (mDanceVideo != null) {
            DetailHandler.Status.videoId = mDanceVideo.getVideoId();
            DetailHandler.Status.ownerUserId = mDanceVideo.getUserId();
        } else {
            DetailHandler.Status.videoId = intent.getIntExtra(Constants.Strings.VIDEO_ID, -1);
            DetailHandler.Status.ownerUserId = intent.getIntExtra(Constants.Strings.OWNER_ID, -1);
        }

        UserLogin loginUser = SharedPreferencesUtil.getInstance(this).getLoginUser();
        if (loginUser != null) {
            DetailHandler.watcherUserId = loginUser.getUserId();
        }
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView backTx = (TextView) findViewById(R.id.video_detailInfo_back);
        ImageView moreImg = (ImageView) findViewById(R.id.video_detailInfo_more);
        mPtrl = (PullToRefreshLayout) findViewById(R.id.video_detail_content);
        contentRv = (RecyclerView) mPtrl.getPullableView();
        mPtrl.setOnPullListener(this);
        mPtrl.setPullDownEnable(false);//禁止下拉
        mPtrl.setEmptyView(new EmptyView(this));
        contentRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mVideoDetailsAdapter = new VideoDetailsAdapter(null, this, this);
        contentRv.setAdapter(mVideoDetailsAdapter);

        mCommentEt = (EditText) findViewById(R.id.bottom_input_edit);

        backTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoDetailsNewActivity.this.finish();
            }
        });
        moreImg.setOnClickListener(this);
        mCommentEt.setOnClickListener(this);
        mCommentEt.setOnEditorActionListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        mWXAPI = WXAPIFactory.createWXAPI(this, Constants.Strings.WX_APP_ID);

        mHandler.addCallback(DetailHandler.MAIN_ACTIVITY, this);
        mHandler.sendEmptyMessage(R.id.msg_video_get_detail);
        DetailHandler.Status.commentPageIdx = 1;
        mHandler.sendEmptyMessage(R.id.msg_video_get_comment_list);//获取评论信息
        SharedPreferencesUtil.getInstance(this).addObserver(this);
    }

    @Override
    public void finish() {
        if (mDanceVideo != null) {
            Intent intent = new Intent();
            intent.putExtra(Constants.Strings.VIDEO_BEAN, mDanceVideo);
            setResult(Activity.RESULT_OK, intent);
        }
        super.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        SharedPreferencesUtil.getInstance(this).deleteObserver(this);
        mVideoDetailsAdapter.videoDestroy();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mVideoDetailsAdapter.videoConfigureChange(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            contentRv.requestFocusFromTouch();
            ((LinearLayoutManager) contentRv.getLayoutManager()).scrollToPositionWithOffset(1, 0);
            findViewById(R.id.page_top_title).setVisibility(View.GONE);
            findViewById(R.id.page_bottom).setVisibility(View.GONE);
            VideoDetailsAdapter.VideoHolder videoHolder = (VideoDetailsAdapter.VideoHolder) contentRv.getChildViewHolder(contentRv.getChildAt(1));
            videoHolder.mContainerFl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            findViewById(R.id.page_top_title).setVisibility(View.VISIBLE);
            findViewById(R.id.page_bottom).setVisibility(View.VISIBLE);
            VideoDetailsAdapter.VideoHolder videoHolder = (VideoDetailsAdapter.VideoHolder) contentRv.getChildViewHolder(contentRv.getChildAt(1));
            videoHolder.mContainerFl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300));
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    return true;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.Ints.REQUEST_CODE_ITEM_STATUS) {
                mVideoDetail = data.getParcelableExtra(Constants.Strings.VIDEO_BEAN);
                if (mVideoDetail != null && mDanceVideo != null) {
                    mDanceVideo.setAttention(Attention.getEnumByBn(mVideoDetail.isAttentioned()));
                    mVideoDetailsAdapter.notifyItemChanged(0);
                }
            }
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {

    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        DetailHandler.Status.commentPageIdx++;
        mHandler.sendEmptyMessage(R.id.msg_video_get_comment_list);//获取评论信息
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_input_edit:
                if (SecurityUtils.isUserValidity(this, DetailHandler.watcherUserId)) {
                    ContentResolverUtil.showIMM(v);
                }
                break;
            case R.id.video_detailInfo_more:
                if (SecurityUtils.isUserValidity(VideoDetailsNewActivity.this, DetailHandler.watcherUserId)) {
                    MoreChoiceDialog dia = new MoreChoiceDialog(this);
                    dia.show();
                    dia.addFunction("举报", new MoreChoiceDialog.OnFunctionClickListener() {
                        @Override
                        public void OnFunctionClick(View v) {

                            mHandler.sendEmptyMessage(R.id.msg_video_add_report);
                            Toast.makeText(VideoDetailsNewActivity.this, "举报成功", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, int position) {
        if (position == 0 || position == 1 || position == 2) {
            mVideoDetail = mVideoDetailsAdapter.getVideoDetails();
            if (mVideoDetail == null) {
                return;
            }
            switch (view.getId()) {
                case R.id.video_detail_photo://头像
                    /*Intent intent = new Intent(this, UserDetailsActivity.class);
                    intent.putExtra(Constants.Strings.VIDEO_BEAN, mVideoDetail);
                    intent.putExtra(Constants.Strings.USER_ID, mVideoDetail.getUserId());
                    intent.putExtra(Constants.Strings.USER_NAME, mVideoDetail.getNickName());
                    intent.putExtra(Constants.Strings.USER_PHOTO, mVideoDetail.getPhotoUrl());
                    startActivityForResult(intent, Constants.Ints.REQUEST_CODE_ITEM_STATUS);*/
                    if (mVideoDetail.getOrgId() > 0) {
                        OrgDetailsActivity.start(this, mVideoDetail.getNickName(), mVideoDetail.getPhotoUrl(), mVideoDetail.getOrgId());
                    } else {
                        UserDetailsActivity.start(this, mVideoDetail.getNickName(), mVideoDetail.getPhotoUrl(), mVideoDetail.getUserId());
                    }
                    break;
                case R.id.video_detail_attention://关注
                    if (SecurityUtils.isUserValidity(this, DetailHandler.watcherUserId)) {
                        mHandler.sendEmptyMessage(R.id.msg_report_attention_video);
                        mVideoDetail.setAttentioned(!mVideoDetail.isAttentioned());
                    }
                    break;
                case R.id.video_detail_forward_count://转发
                    if (SecurityUtils.isUserValidity(this, DetailHandler.watcherUserId)) {
                        new ShareBoardDialog(this, this).setShare_medias(Constants.Arrays.SHARDBOARD).show();
//                        mVideoDetail.setForwardCount(mVideoDetail.getForwardCount() + 1);
                    }
                    break;
                case R.id.video_detail_likeCount://喜爱
                    if (SecurityUtils.isUserValidity(this, DetailHandler.watcherUserId)) {
                        DetailHandler.Status.videoId = mVideoDetail.getVideoId();
                        DetailHandler.Status.ownerUserId = mVideoDetail.getUserId();
                        mHandler.sendEmptyMessage(R.id.msg_report_like_withMsg_video);
                        if (mVideoDetail.isLike()) {
                            mVideoDetail.setLikeCount(mVideoDetail.getLikeCount() - 1);
                        } else {
                            mVideoDetail.setLikeCount(mVideoDetail.getLikeCount() + 1);
                        }
                        mVideoDetail.setIsLike(!mVideoDetail.isLike());
                        mVideoDetailsAdapter.notifyItemChanged(2);
                    }
                    break;
                case R.id.video_detail_reword://打赏
                    if (SecurityUtils.isUserValidity(this, DetailHandler.watcherUserId)) {
                        if (mTipDia == null) {
                            mTipDia = new TipsChoiceDialog(this, this);
                        }
                        BrowseHandler.Status.Video.videoId = mVideoDetail.getVideoId();
                        BrowseHandler.Status.Video.ownerUserId = mVideoDetail.getUserId();
                        DetailHandler.Status.videoId = mVideoDetail.getVideoId();
                        DetailHandler.Status.ownerUserId = mVideoDetail.getUserId();
                        mTipDia.setRewardPerson(mVideoDetail.getNickName());
                        mTipDia.show();
                    }
                    break;
                case R.id.video_detail_play://播放
                    mVideoDetail.setWatchCount(mVideoDetail.getWatchCount() + 1);
                    mVideoDetailsAdapter.notifyItemChanged(2);
                    mHandler.sendEmptyMessage(R.id.msg_video_report_watch_count);
                    break;
                default://打赏人的头像
                    int pos = (int) view.getTag();
                    List<SimpleItemData> userAdmires = mVideoDetail.getUserAdmire();
                    if (pos >= 0 && pos < userAdmires.size()) {
                        Intent intentUser = null;
                        if (userAdmires.get(pos).getSubId() > 0) {
                            intentUser = new Intent(this, OrgDetailsActivity.class);
                            intentUser.putExtra(Constants.Strings.ORG_ID, userAdmires.get(pos).getSubId());
                            intentUser.putExtra(Constants.Strings.ORG_LOGO, userAdmires.get(pos).getContent());
                        } else {
                            intentUser = new Intent(this, UserDetailsActivity.class);
                            intentUser.putExtra(Constants.Strings.USER_ID, userAdmires.get(pos).getId());
                            intentUser.putExtra(Constants.Strings.USER_PHOTO, userAdmires.get(pos).getContent());
                        }
                        startActivity(intentUser);
                    }
                    break;
            }
        } else {//评论部分
            switch (view.getId()) {
                case R.id.comment_list_item_photo:
                    CommentItem commentItem = mVideoDetailsAdapter.getItem(position);
                    if (commentItem.getOrgId() > 0) {
                        OrgDetailsActivity.start(this, commentItem.getNickName(), commentItem.getPhotoUrl(), commentItem.getOrgId());
                    } else {
                        UserDetailsActivity.start(this, commentItem.getNickName(), commentItem.getPhotoUrl(), commentItem.getUserId());
                    }
                    break;
            }
        }
    }

    //分享面板点击事件
    @Override
    public void shareBoardOnclickListener(View v) {
        LogUtil.w(TAG, v.getTag().toString());
        UMVideo umVideo = new UMVideo(UrlManager.getUrl(UrlManager.UrlName.VIDEO_WX_GOTO_PAGE) + mVideoDetail.getVideoId());
        umVideo.setThumb(new UMImage(this, mVideoDetail.getPosterUrl()));
        new ShareAction(this)
                .setPlatform((SHARE_MEDIA) v.getTag())
                .setCallback(umShareListener)
                .withMedia(umVideo)
                .withText(mVideoDetail.getTitle()).share();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.msg_video_detail_got://获取详情
                if (msg.obj != null) {
                    mVideoDetailsAdapter.setVideoDetails((VideoDetailBean) msg.obj);
                }
                break;
            case R.id.msg_attention_reported_video://关注
                if (msg.obj != null) {
                    SimpleResponse response = (SimpleResponse) msg.obj;
                    if (response.getCode() == SimpleResponse.SUCCESS) {
//                        Toast.makeText(getApplication(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        if (mDanceVideo != null) {
                            mDanceVideo.setAttention(Attention.getReverse(mDanceVideo.getAttention()));
                        }
                    } else {
//                        Toast.makeText(getApplication(), response.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.msg_like_reported_withMsg_video://喜欢
                if (msg.obj != null) {
                    SimpleResponse response = (SimpleResponse) msg.obj;
                    if (response.getCode() == SimpleResponse.SUCCESS) {
//                        Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
                        if (mDanceVideo != null) {
                            mDanceVideo.setLikeCount(response.getContentInt());
                            mDanceVideo.setLiked(!mDanceVideo.isLiked());
                        }
                        if (mVideoDetail != null) {
                            mVideoDetail.setLikeCount(response.getContentInt());
                        }
                    } else {
//                        Toast.makeText(this, "喜欢失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.msg_video_watch_count_reported://观看次数
                if (DetailHandler.Status.watchCount >= 0) {
                    mVideoDetail.setWatchCount(DetailHandler.Status.watchCount);
                }
                break;
            case R.id.msg_video_comment_list_got://获取评论
                if (msg.obj != null) {
                    CommentListWithInfo commentListWithInfo = (CommentListWithInfo) msg.obj;
                    if (!isSendComment) {
                        if (DetailHandler.Status.commentPageIdx == 1) {
                            mVideoDetailsAdapter.setAdapterDataList(commentListWithInfo.getDataList());
                        } else {
                            mVideoDetailsAdapter.addAdapterDataList(commentListWithInfo.getDataList());
                        }
                        mPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    } else {//发送评论更新列表
                        mVideoDetail = mVideoDetailsAdapter.getVideoDetails();
                        mVideoDetailsAdapter.addCommentIntoList(commentListWithInfo.getDataList().get(0));
                        mVideoDetail.setCommentCount(commentListWithInfo.getDataList().size());
//                        mVideoDetailsAdapter.notifyItemChanged(0);
                        isSendComment = false;
                    }
                } else {
                    mVideoDetailsAdapter.setAdapterDataList(null);
                    mPtrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
                break;
            case R.id.msg_comment_sent_video://发表评论
                if (msg.obj != null) {
                    boolean status = (boolean) msg.obj;
                    if (status) {
//                        Toast.makeText(this, "评论成功", Toast.LENGTH_SHORT).show();
                        isSendComment = true;
                        mHandler.sendEmptyMessage(R.id.msg_video_get_comment_list);
                    } else {
//                        Toast.makeText(this, "评论失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data != null) {
            DetailHandler.watcherUserId = ((UserLogin) data).getUserId();
        } else {
            DetailHandler.watcherUserId = -1;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            if (SecurityUtils.isUserValidity(this, DetailHandler.watcherUserId)) {
                sendComment(v);
                return true;
            }
        }
        return false;
    }

    //发送评论
    private void sendComment(TextView v) {
        mVideoDetail = mVideoDetailsAdapter.getVideoDetails();
        CharSequence content = mCommentEt.getText();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }
        DetailHandler.Status.commentContent = content.toString();
        mVideoDetail.setCommentCount(mVideoDetail.getCommentCount() + 1);
        mVideoDetailsAdapter.notifyItemChanged(1);
        mHandler.sendEmptyMessage(R.id.msg_send_comment_video);
        mCommentEt.setText("");
        ContentResolverUtil.hideIMM(v);
    }

    //打赏回调方法
    @Override
    public void tipsSelected(float tips) {
        new AsynReward().execute(String.valueOf(Math.round(tips * 100)));
    }

    class AsynReward extends AsyncTask<String, Intent, WechatPrepay> {

        @Override
        protected WechatPrepay doInBackground(String... params) {
            String ipAddress = BrowseParser.getDeviceNetIpAddress();
            LogUtil.w(TAG, "ipAddress:" + ipAddress);
            return BrowseParser.wxOrder(DetailHandler.Status.videoId, DetailHandler.watcherUserId,
                    DetailHandler.Status.ownerUserId, ipAddress, "打赏", params[0]);
        }

        @Override
        protected void onPostExecute(WechatPrepay wechatPrepay) {
            if (wechatPrepay == null) {
                return;
            }
            PayReq req = new PayReq();
            req.appId = Constants.Strings.WX_APP_ID;
            req.partnerId = Constants.Strings.WX_APP_PARTENERID;
            req.prepayId = wechatPrepay.getPrepayId();
            req.packageValue = "Sign=WXPay";
            req.nonceStr = SecurityUtils.getNonceStr();
            req.timeStamp = String.valueOf(SecurityUtils.genTimeStamp());

            List<NameValuePair> signParams = new LinkedList<NameValuePair>();
            signParams.add(new BasicNameValuePair("appid", req.appId));
            signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
            signParams.add(new BasicNameValuePair("package", req.packageValue));
            signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
            signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
            signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

            req.sign = SecurityUtils.getAppSign(signParams);

            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            mWXAPI.registerApp(Constants.Strings.WX_APP_ID);
            mWXAPI.sendReq(req);
        }
    }
}
