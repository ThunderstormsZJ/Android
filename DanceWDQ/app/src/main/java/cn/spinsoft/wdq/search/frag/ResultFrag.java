package cn.spinsoft.wdq.search.frag;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.ShareAction;
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
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.base.bean.ListDataWithInfo;
import cn.spinsoft.wdq.browse.biz.BrowseHandler;
import cn.spinsoft.wdq.browse.biz.BrowseParser;
import cn.spinsoft.wdq.browse.biz.WechatPrepay;
import cn.spinsoft.wdq.effect.RecyclerItemAnimator;
import cn.spinsoft.wdq.enums.Attention;
import cn.spinsoft.wdq.org.OrgDetailsActivity;
import cn.spinsoft.wdq.org.biz.OrgInfo;
import cn.spinsoft.wdq.org.widget.OrgListAdapter;
import cn.spinsoft.wdq.search.biz.SearchHandler;
import cn.spinsoft.wdq.user.UserDetailsActivity;
import cn.spinsoft.wdq.user.biz.DancerInfo;
import cn.spinsoft.wdq.user.widget.FriendGridListAdater;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SecurityUtils;
import cn.spinsoft.wdq.utils.UrlManager;
import cn.spinsoft.wdq.video.VideoDetailsNewActivity;
import cn.spinsoft.wdq.video.biz.DanceVideoBean;
import cn.spinsoft.wdq.video.widget.TipsChoiceDialog;
import cn.spinsoft.wdq.video.widget.VideoListAdapter;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;
import cn.spinsoft.wdq.widget.ShareBoardDialog;

/**
 * Created by zhoujun on 15/11/18.
 */
public class ResultFrag extends BaseFragment implements Handler.Callback, RecyclerItemClickListener,
        TipsChoiceDialog.OnTipsChoiceListener, ShareBoardDialog.ShareBoardDiaListener {
    private static final String TAG = ResultFrag.class.getSimpleName();

    private RecyclerView mResultRv;
    private BaseRecycleAdapter mResultAdapter;
    private TipsChoiceDialog mChoiceDia;
    private IWXAPI mWXAPI;
    private int mForwardPosition = 0;
    private DanceVideoBean videoBean;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_search_result;
    }

    @Override
    protected void initViewAndListener(View root, Bundle savedInstanceState) {
        mResultRv = (RecyclerView) root.findViewById(R.id.search_result_container);
        mResultRv.setItemAnimator(new RecyclerItemAnimator());

        mResultRv.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));
        switch (SearchHandler.Status.pageType) {
            case VIDEO:
                mResultAdapter = new VideoListAdapter(null, this, true);
                break;
            case FRIEND:
                mResultRv.setLayoutManager(new GridLayoutManager(root.getContext(), 2, LinearLayoutManager.VERTICAL, false));
                mResultAdapter = new FriendGridListAdater(null, this, getActivity());
                break;
            case ORG:
                mResultAdapter = new OrgListAdapter(null, this);
                break;
            default:
                break;

        }
        mResultRv.setAdapter(mResultAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mWXAPI = WXAPIFactory.createWXAPI(getActivity(), Constants.Strings.WX_APP_ID);
        mHandler.addCallback(SearchHandler.CHILD_RESULT, this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.msg_search_video_list_got:
            case R.id.msg_search_friend_list_got:
            case R.id.msg_search_org_list_got:
                if (msg.obj != null) {
                    ListDataWithInfo listDataWithInfo = (ListDataWithInfo) msg.obj;
                    if (listDataWithInfo.getDataList().size() == 0) {
                        Toast.makeText(getContext(), "亲，没有找到", Toast.LENGTH_SHORT).show();
                    } else {
                        if (SearchHandler.Status.pageIdx == 1) {
                            mResultAdapter.setAdapterDataList(listDataWithInfo.getDataList());
                        } else {
                            mResultAdapter.addAdapterDataList(listDataWithInfo.getDataList());
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "亲，没有找到", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, int position) {
        LogUtil.w(TAG, "onItemClicked:" + position);
        switch (SearchHandler.Status.pageType) {
            case VIDEO:
                /*if (position > 0) {
                    position = position + 1;
                }*/
                videoBean = (DanceVideoBean) mResultAdapter.getItem(position);
                switch (view.getId()) {
                    case R.id.videos_list_item_photo:
                        if (videoBean.getOrgId() > 0) {
                            OrgDetailsActivity.start(getContext(), videoBean.getNickName(), videoBean.getPhotoUrl(), videoBean.getOrgId());
                        } else {
                            UserDetailsActivity.start(getContext(), videoBean.getNickName(), videoBean.getPhotoUrl(), videoBean.getUserId());
                        }
                        break;
                    case R.id.videos_list_item_attention:
                        if (SecurityUtils.isUserValidity(getActivity(), SearchHandler.watcherUserId)) {
                            videoBean.setAttention(Attention.getReverse(videoBean.getAttention()));
                            mResultAdapter.notifyItemChanged(position);
                            mHandler = new BrowseHandler();
                            BrowseHandler.Status.Video.videoId = videoBean.getVideoId();
                            BrowseHandler.Status.Video.ownerUserId = videoBean.getUserId();
                            mHandler.sendEmptyMessage(R.id.msg_report_attention_video);
                        }
                        break;
                    case R.id.videos_list_item_likes:
                        if (SecurityUtils.isUserValidity(getActivity(), SearchHandler.watcherUserId)) {
                            ((VideoListAdapter) mResultAdapter).notifyItemLikeChanged(!videoBean.isLiked(), position);
                            mHandler = new BrowseHandler();
                            BrowseHandler.Status.Video.videoId = videoBean.getVideoId();
                            BrowseHandler.Status.Video.ownerUserId = videoBean.getUserId();
                            mHandler.sendEmptyMessage(R.id.msg_report_like_video);
                        }
                        break;
                    case R.id.videos_list_item_tips:
                        if (SecurityUtils.isUserValidity(getActivity(), SearchHandler.watcherUserId)) {
                            if (mChoiceDia == null) {
                                mChoiceDia = new TipsChoiceDialog(getActivity(), this);
                            }
                            mHandler = new BrowseHandler();
                            BrowseHandler.Status.Video.videoId = videoBean.getVideoId();
                            BrowseHandler.Status.Video.ownerUserId = videoBean.getUserId();
                            mChoiceDia.setRewardPerson(videoBean.getNickName());
                            mChoiceDia.show();
                        }
                        break;
                    case R.id.videos_list_item_forwards:
                        if (SecurityUtils.isUserValidity(getActivity(), SearchHandler.watcherUserId)) {
                            BrowseHandler.watcherUserId = SearchHandler.watcherUserId;
                            BrowseHandler.Status.Video.videoId = videoBean.getVideoId();
                            BrowseHandler.Status.Video.ownerUserId = videoBean.getUserId();
                            new ShareBoardDialog(getActivity(), this).setShare_medias(Constants.Arrays.SHARDBOARD).show();
                            mForwardPosition = position;
                        }
                        break;
                    default:
                        Intent detailIntent = new Intent(getActivity(), VideoDetailsNewActivity.class);
                        detailIntent.putExtra(Constants.Strings.VIDEO_ID, videoBean.getVideoId());
                        detailIntent.putExtra(Constants.Strings.OWNER_ID, videoBean.getUserId());
                        startActivity(detailIntent);
                        break;
                }
                break;
            case FRIEND:
                DancerInfo dancerInfo = (DancerInfo) mResultAdapter.getItem(position);
                Intent userIntent = new Intent(getActivity(), UserDetailsActivity.class);
                userIntent.putExtra(Constants.Strings.USER_ID, dancerInfo.getUserId());
                userIntent.putExtra(Constants.Strings.USER_NAME, dancerInfo.getNickName());
                userIntent.putExtra(Constants.Strings.USER_PHOTO, dancerInfo.getPhotoUrl());
                userIntent.putExtra(Constants.Strings.USER_SIGN, dancerInfo.getSignature());
                userIntent.putExtra(Constants.Strings.USER_ATTEN, dancerInfo.getAttention().getValue());
                startActivity(userIntent);
                break;
            case ORG:
                OrgInfo orgInfo = (OrgInfo) mResultAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), OrgDetailsActivity.class);
                intent.putExtra(Constants.Strings.ORG_ID, orgInfo.getOrgId());
                intent.putExtra(Constants.Strings.USER_ID, orgInfo.getUserId());
                intent.putExtra(Constants.Strings.ORG_NAME, orgInfo.getOrgName());
                intent.putExtra(Constants.Strings.ORG_LOGO, orgInfo.getPhotoUrl());
                intent.putExtra(Constants.Strings.ORG_SIGN, orgInfo.getSignature());
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void tipsSelected(float tips) {
        new AsynReward().execute(String.valueOf(Math.round(tips * 100)));
    }

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

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
            mHandler = new BrowseHandler();
            mHandler.sendEmptyMessage(R.id.msg_report_forward_video);
            mResultAdapter.notifyItemChanged(mForwardPosition);
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

    class AsynReward extends AsyncTask<String, Intent, WechatPrepay> {

        @Override
        protected WechatPrepay doInBackground(String... params) {
            String ipAddress = BrowseParser.getDeviceNetIpAddress();
            LogUtil.w(TAG, "ipAddress:" + ipAddress);
            return BrowseParser.wxOrder(BrowseHandler.Status.Video.videoId, SearchHandler.watcherUserId,
                    BrowseHandler.Status.Video.ownerUserId, ipAddress, "打赏", params[0]);
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
