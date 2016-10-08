package cn.spinsoft.wdq.video.frag;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
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

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.browse.biz.BrowseHandler;
import cn.spinsoft.wdq.browse.biz.BrowseHandler.Status.Video;
import cn.spinsoft.wdq.browse.biz.BrowseParser;
import cn.spinsoft.wdq.browse.biz.WechatPrepay;
import cn.spinsoft.wdq.discover.DiscoverDetailActivity;
import cn.spinsoft.wdq.enums.DiscoverType;
import cn.spinsoft.wdq.org.OrgDetailsActivity;
import cn.spinsoft.wdq.user.UserDetailsActivity;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SecurityUtils;
import cn.spinsoft.wdq.utils.SimpleItemDataUtils;
import cn.spinsoft.wdq.utils.UrlManager;
import cn.spinsoft.wdq.video.VideoDetailsNewActivity;
import cn.spinsoft.wdq.video.biz.AdvertisementInfo;
import cn.spinsoft.wdq.video.biz.DanceVideoBean;
import cn.spinsoft.wdq.video.biz.DanceVideoListWithInfo;
import cn.spinsoft.wdq.video.widget.ImageLoopView;
import cn.spinsoft.wdq.video.widget.TipsChoiceDialog;
import cn.spinsoft.wdq.video.widget.VideoListAdapter;
import cn.spinsoft.wdq.widget.DropDownSpinner;
import cn.spinsoft.wdq.widget.EmptyView;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;
import cn.spinsoft.wdq.widget.ShareBoardDialog;
import cn.spinsoft.wdq.widget.SimpleTextBaseAdapter;

/**
 * Created by zhoujun on 15/11/2.
 */
public class VideoListFrag extends BaseFragment implements Handler.Callback, RecyclerItemClickListener,
        TipsChoiceDialog.OnTipsChoiceListener,
        ImageLoopView.ImageLoopViewListener, PullToRefreshLayout.OnPullListener, ShareBoardDialog.ShareBoardDiaListener,
        DropDownSpinner.OnItemSelectedListener {
    private static final String TAG = VideoListFrag.class.getSimpleName();
    private DropDownSpinner mTypeSp, mSortSp;
    private CheckBox mAttentionCb;
    private PullToRefreshLayout mPtrl;
    private RecyclerView mContentRv;
    private VideoListAdapter mListAdapter;

    private int mPositionToDetail = 0;
    private int mForwardPosition = 0;
    private TipsChoiceDialog mChoiceDia;//打赏弹窗
    private DanceVideoBean videoBean;

    private IWXAPI mWXAPI;
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
            mHandler.sendEmptyMessage(R.id.msg_report_forward_video);
            mListAdapter.notifyItemForwardChanged(mForwardPosition);
            Toast.makeText(getContext(), "分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Toast.makeText(getContext(), "分享失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            Toast.makeText(getContext(), "分享取消", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.frag_video_list;
    }

    @Override
    protected void initViewAndListener(View root, Bundle savedInstanceState) {
        mTypeSp = (DropDownSpinner) root.findViewById(R.id.videos_main_type);
        mSortSp = (DropDownSpinner) root.findViewById(R.id.videos_main_sort);
        mAttentionCb = (CheckBox) root.findViewById(R.id.videos_main_attention);
        mPtrl = (PullToRefreshLayout) root.findViewById(R.id.videos_main_content);
        mPtrl.setEmptyView(new EmptyView(root.getContext()));
        mPtrl.setOnPullListener(this);
        mContentRv = (RecyclerView) mPtrl.getPullableView();

        mContentRv.setLayoutManager(new LinearLayoutManager(root.getContext(),
                LinearLayoutManager.VERTICAL, false));
        mListAdapter = new VideoListAdapter(null, this);
        mContentRv.setAdapter(mListAdapter);

//        mTypeSp.setAdapter(new SimpleTextBaseAdapter(SimpleItemDataUtils.getDanceTypeList(false, getActivity())));
//        mTypeSp.setOnItemSelectedListener(this);
        mTypeSp.setmListAdpter(new SimpleTextBaseAdapter(SimpleItemDataUtils.getDanceTypeList(false, getActivity())));
        mTypeSp.setOnItemSelectedListener(this);
        mSortSp.setmListAdpter(new SimpleTextBaseAdapter(SimpleItemDataUtils.getSortList()));
        mSortSp.setOnItemSelectedListener(this);

        mAttentionCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (SecurityUtils.isUserValidity(getContext(), BrowseHandler.watcherUserId)) {
                    if (isChecked) {
                        Video.attention = 1;
                    } else {
                        Video.attention = 0;
                    }
                    mPtrl.autoRefresh();
                } else {
                    mAttentionCb.setChecked(false);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler.addCallback(BrowseHandler.CHILD_VIDEO, this);
        mHandler.sendEmptyMessage(R.id.msg_videos_get_ads);

        mWXAPI = WXAPIFactory.createWXAPI(getActivity(), Constants.Strings.WX_APP_ID);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPtrl.autoRefresh();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.msg_videos_video_list_got:
                if (msg.obj != null) {
                    DanceVideoListWithInfo listWithInfo = (DanceVideoListWithInfo) msg.obj;
                    Video.maxPage = listWithInfo.getTotalPages();
                    if (Video.pageNum == 1) {
                        mListAdapter.setAdapterDataList(listWithInfo.getDataList());
                        mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                        mContentRv.smoothScrollToPosition(0);
                    } else {
                        mListAdapter.addAdapterDataList(listWithInfo.getDataList());
                        mPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    }
                } else {
                    if (Video.pageNum == 1) {
                        mPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                        mListAdapter.setAdapterDataList(null);
                    } else {
                        mPtrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                    }
                }
                if (mListAdapter.getItemCount() <= 0) {
                    mPtrl.showEmptyView(true);
                } else {
                    mPtrl.showEmptyView(false);
                }
                break;
            case R.id.msg_videos_ads_got:
                if (msg.obj != null) {
                    List<AdvertisementInfo> adsUrls = (List<AdvertisementInfo>) msg.obj;
                    mListAdapter.setAdsUrls(adsUrls, this);
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, int position) {
        LogUtil.w(TAG, "clicked,view=" + view);
        mPositionToDetail = position;
        videoBean = mListAdapter.getItem(position);
        switch (view.getId()) {
            case R.id.videos_list_item_photo:
                Intent userIntent;
                if (videoBean.getOrgId() > 0) {
                    userIntent = new Intent(getActivity(), OrgDetailsActivity.class);
                    userIntent.putExtra(Constants.Strings.ORG_ID, videoBean.getOrgId());
                    userIntent.putExtra(Constants.Strings.USER_ID, videoBean.getUserId());
                    userIntent.putExtra(Constants.Strings.ORG_LOGO, videoBean.getPhotoUrl());
                    userIntent.putExtra(Constants.Strings.ORG_NAME, videoBean.getNickName());
                } else {
                    userIntent = new Intent(getActivity(), UserDetailsActivity.class);
                    userIntent.putExtra(Constants.Strings.USER_ID, videoBean.getUserId());
                    userIntent.putExtra(Constants.Strings.USER_PHOTO, videoBean.getPhotoUrl());
                    userIntent.putExtra(Constants.Strings.USER_NAME, videoBean.getNickName());
                    userIntent.putExtra(Constants.Strings.USER_ATTEN, videoBean.getAttention().getValue());
                }
                startActivity(userIntent);
                break;
            case R.id.videos_list_item_attention:
                if (SecurityUtils.isUserValidity(getActivity(), BrowseHandler.watcherUserId)) {
                    boolean att = (boolean) view.getTag();
                    att = !att;
                    view.setTag(att);
                    mListAdapter.notifyItemAttentionChanged(att, position);

                    Video.videoId = videoBean.getVideoId();
                    Video.ownerUserId = videoBean.getUserId();
                    mHandler.sendEmptyMessage(R.id.msg_report_attention_video);
                }
                break;
            case R.id.videos_list_item_likes:
                if (SecurityUtils.isUserValidity(getActivity(), BrowseHandler.watcherUserId)) {
                    Boolean like = (Boolean) view.getTag();
                    like = !like;
                    view.setTag(like);
                    mListAdapter.notifyItemLikeChanged(like, position);

                    Video.videoId = videoBean.getVideoId();
                    Video.ownerUserId = videoBean.getUserId();
                    mHandler.sendEmptyMessage(R.id.msg_report_like_video);
                }
                break;
            case R.id.videos_list_item_forwards://转发
                if (SecurityUtils.isUserValidity(getActivity(), BrowseHandler.watcherUserId)) {
                    Video.videoId = videoBean.getVideoId();
                    Video.ownerUserId = videoBean.getUserId();
                    new ShareBoardDialog(getActivity(), this).setShare_medias(Constants.Arrays.SHARDBOARD).show();
                    mForwardPosition = position;
                }
                break;
            case R.id.videos_list_item_tips://打赏
                if (SecurityUtils.isUserValidity(getActivity(), BrowseHandler.watcherUserId)) {
                    if (mChoiceDia == null) {
                        mChoiceDia = new TipsChoiceDialog(getContext(), this);
                    }
                    Video.videoId = videoBean.getVideoId();
                    Video.ownerUserId = videoBean.getUserId();
                    mChoiceDia.setRewardPerson(videoBean.getNickName());
                    mChoiceDia.show();
                }
                break;
            case R.id.videos_list_item_comments:
            default:
                mPositionToDetail = position;
                Intent intent = new Intent(getActivity(), VideoDetailsNewActivity.class);
                intent.putExtra(Constants.Strings.VIDEO_BEAN, videoBean);
                startActivityForResult(intent, Constants.Ints.REQUEST_CODE_ITEM_STATUS);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(getActivity()).onActivityResult(requestCode, resultCode, data);
        LogUtil.w(TAG, "onActivityResult:" + requestCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.Ints.REQUEST_CODE_ITEM_STATUS && mListAdapter != null && data != null) {
                DanceVideoBean danceVideo = data.getParcelableExtra(Constants.Strings.VIDEO_BEAN);
                mListAdapter.notifyItemChanged(danceVideo, mPositionToDetail);
            }
        }
    }

    @Override
    public void onItemSelected(ListAdapter parent, TextView view, int position, long id) {
        if (parent instanceof SimpleTextBaseAdapter) {
            SimpleItemData item = ((SimpleTextBaseAdapter) parent).getItem(position);
            if (parent == mTypeSp.getmListAdpter()) {
                if (item.getId() == -1) {
                    mTypeSp.editText.setText("全部舞种");
                }
                Video.danceType = item.getId();
            } else if (parent == mSortSp.getmListAdpter()) {
                if (item.getId() == 0) {
                    mSortSp.editText.setText("综合排序");
                }
                Video.listRank = item.getId();
            }
            Video.pageNum = 1;
            mHandler.sendEmptyMessage(R.id.msg_videos_get_video_list);
        }
    }

    @Override
    public void tipsSelected(float tips) {
        LogUtil.w(TAG, "tipsSelected:" + tips);
        new AsynReward().execute(String.valueOf(Math.round(tips * 100)));
    }

    @Override
    public void displayLoopImage(String imageURL, SimpleDraweeView imageView) {
        imageView.setImageURI(Uri.parse(imageURL));
    }

    @Override
    public void onLoopImageClick(AdvertisementInfo info, int position, View imageView) {
        LogUtil.d(TAG, "onLoopImageClick");
        if (info.getType() == 1) {//网页跳转
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(info.getSkipUrl());
            intent.setData(content_url);
            startActivity(intent);
        } else if (info.getType() == 2) {//app跳转
            if (info.getSkipType() == DiscoverType.UNKNOWN) {//视频
                Intent intent = new Intent(getActivity(), VideoDetailsNewActivity.class);
                intent.putExtra(Constants.Strings.VIDEO_ID, info.getDiscoverId());
                intent.putExtra(Constants.Strings.OWNER_ID, info.getUserId());
                startActivity(intent);
            } else {//发现
                Intent intent = new Intent(getActivity(), DiscoverDetailActivity.class);
                intent.putExtra(Constants.Strings.DISCOVER_TYPE_ID, info.getSkipType().getValue());
                intent.putExtra(Constants.Strings.DISCOVER_EVENT_ID, info.getDiscoverId());
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        Video.pageNum = 1;
        if (mAttentionCb.isChecked()) {
            mHandler.sendEmptyMessage(R.id.msg_videos_get_video_list);
        } else {
            mHandler.sendEmptyMessage(R.id.msg_videos_get_video_list);
        }
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        if (Video.pageNum < Video.maxPage) {
            Video.pageNum++;
            if (mAttentionCb.isChecked()) {
                mHandler.sendEmptyMessage(R.id.msg_videos_get_video_list);
            } else {
                mHandler.sendEmptyMessage(R.id.msg_videos_get_video_list);
            }
        }
    }

    //分享面板的点击事件
    @Override
    public void shareBoardOnclickListener(View v) {
        LogUtil.w(TAG, v.getTag().toString());
        UMVideo umVideo = new UMVideo(UrlManager.getUrl(UrlManager.UrlName.VIDEO_WX_GOTO_PAGE) + videoBean.getVideoId());
        umVideo.setThumb(new UMImage(getActivity(), videoBean.getPosterUrl()));
        new ShareAction(getActivity())
                .setPlatform((SHARE_MEDIA) v.getTag())
                .setCallback(umShareListener)
                .withMedia(umVideo)
                .withText(videoBean.getTitle()).share();
    }

    class AsynReward extends AsyncTask<String, Intent, WechatPrepay> {

        @Override
        protected WechatPrepay doInBackground(String... params) {
            String ipAddress = BrowseParser.getDeviceNetIpAddress();
            LogUtil.w(TAG, "ipAddress:" + ipAddress);
            return BrowseParser.wxOrder(Video.videoId, BrowseHandler.watcherUserId,
                    Video.ownerUserId, ipAddress, "打赏", params[0]);
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
