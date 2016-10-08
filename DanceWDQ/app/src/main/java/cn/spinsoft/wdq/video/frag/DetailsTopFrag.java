//package cn.spinsoft.wdq.video.frag;
//
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.content.res.Configuration;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.android.tedcoder.wkvideoplayer.model.Video;
//import com.android.tedcoder.wkvideoplayer.model.VideoUrl;
//import com.android.tedcoder.wkvideoplayer.util.DensityUtil;
//import com.android.tedcoder.wkvideoplayer.view.MediaController;
//import com.android.tedcoder.wkvideoplayer.view.SuperVideoPlayer;
//import com.facebook.drawee.view.SimpleDraweeView;
//
//import java.util.ArrayList;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.base.BaseFragment;
//import cn.spinsoft.wdq.user.UserDetailsActivity;
//import cn.spinsoft.wdq.utils.Constants;
//import cn.spinsoft.wdq.utils.LogUtil;
//import cn.spinsoft.wdq.utils.SecurityUtils;
//import cn.spinsoft.wdq.video.biz.DetailHandler;
//import cn.spinsoft.wdq.video.biz.VideoDetailBean;
//
///**
// * Created by hushujun on 15/11/6.
// */
//public class DetailsTopFrag extends BaseFragment implements Handler.Callback, View.OnClickListener {
//    private static final String TAG = DetailsTopFrag.class.getSimpleName();
//    private ImageView mPlayImg,mOriginalImg;
//    private SimpleDraweeView mPosterImg,mPhotoImg;
//    private SuperVideoPlayer mPlayVv;
//    private TextView mDanceNameTx, mTitleTx,mPlayCountTx,mNickNameTx,mTimeDiffTx;
//    private Button mAttenBut;
//
//    private VideoDetailBean mVideoDetail;
//
//    private SuperVideoPlayer.VideoPlayCallbackImpl mVideoPlayCallback = new SuperVideoPlayer.VideoPlayCallbackImpl() {
//        @Override
//        public void onCloseVideo() {
//            mPlayVv.close();
//            mPlayImg.setVisibility(View.VISIBLE);
//            mPosterImg.setVisibility(View.VISIBLE);
//            resetPageToPortrait();
//        }
//
//        @Override
//        public void onSwitchPageType() {
//            if (getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                mPlayVv.setPageType(MediaController.PageType.SHRINK);
//            } else {
//                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                mPlayVv.setPageType(MediaController.PageType.EXPAND);
//            }
//        }
//
//        @Override
//        public void onPlayFinish() {
//            mPlayVv.close();
//            mPlayImg.setVisibility(View.VISIBLE);
//            mPosterImg.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        public void onPausePlay() {
//
//        }
//
//        @Override
//        public void onGoOnPlay() {
//
//        }
//    };
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.frag_video_detail_top;
//    }
//
//    @Override
//    protected void initViewAndListener(View root, Bundle savedInstanceState) {
//        mPosterImg = (SimpleDraweeView) root.findViewById(R.id.video_detail_poster);
//        mPhotoImg = (SimpleDraweeView) root.findViewById(R.id.video_detail_photo);
//        mPlayImg = (ImageView) root.findViewById(R.id.video_detail_play);
//        mOriginalImg = (ImageView) root.findViewById(R.id.video_detail_original);
//        mPlayVv = (SuperVideoPlayer) root.findViewById(R.id.video_detail_videoview);
//        mDanceNameTx = (TextView) root.findViewById(R.id.video_detail_dance_name);
//        mTitleTx = (TextView) root.findViewById(R.id.video_detail_title);
////        mDescTx = (TextView) root.findViewById(R.id.video_detail_desc);
//        mPlayCountTx = (TextView) root.findViewById(R.id.video_detail_play_count);
//        mNickNameTx = (TextView) root.findViewById(R.id.video_detail_nickName);
//        mTimeDiffTx = (TextView) root.findViewById(R.id.videos_detail_timeDiff);
//
//        mAttenBut = (Button) root.findViewById(R.id.video_detail_attention);
//
//        mPlayVv.setVideoPlayCallback(mVideoPlayCallback);
////        mPlayVv.setToggleFullScreenHandler(this);
////        mPlayVv.setListener(this);
//        mPlayImg.setOnClickListener(this);
//        mPhotoImg.setOnClickListener(this);
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mHandler.addCallback(DetailHandler.CHILD_TOP, this);
////        mHandler.sendEmptyMessage(R.id.msg_video_get_detail);
//    }
//
//    @Override
//    public boolean handleMessage(Message msg) {
//        LogUtil.w(TAG, msg.toString());
//        switch (msg.what) {
//            /*case R.id.msg_video_detail_got:
//                if (msg.obj != null) {
//                    loadDataToWidget((VideoDetailBean) msg.obj);
//                }
//                break;*/
//            case R.id.msg_video_watch_count_reported:
//                mPlayCountTx.setText(String.valueOf(DetailHandler.Status.watchCount));
//                break;
//            default:
//                break;
//        }
//        return true;
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        LogUtil.w(TAG, "onConfigurationChanged=====>>" + newConfig.toString());
//        super.onConfigurationChanged(newConfig);
//        if (null == mPlayVv) return;
//        /***
//         * 根据屏幕方向重新设置播放器的大小
//         */
//        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            getActivity().getWindow().getDecorView().invalidate();
//            float height = DensityUtil.getWidthInPx(getActivity());
//            float width = DensityUtil.getHeightInPx(getActivity());
//            mPlayVv.getLayoutParams().height = (int) width;
//            mPlayVv.getLayoutParams().width = (int) height;
//        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//            final WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
//            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            getActivity().getWindow().setAttributes(attrs);
//            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            float width = DensityUtil.getWidthInPx(getActivity());
//            float height = DensityUtil.dip2px(getActivity(), 194.f);
//            mPlayVv.getLayoutParams().height = (int) height;
//            mPlayVv.getLayoutParams().width = (int) width;
//        }
//    }
//
//
//    /***
//     * 恢复屏幕至竖屏
//     */
//    private void resetPageToPortrait() {
//        if (getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            mPlayVv.setPageType(MediaController.PageType.SHRINK);
//        }
//    }
//
//    public void loadDataToWidget(VideoDetailBean detailBean) {
//        mVideoDetail = detailBean;
//
//        mPosterImg.setImageURI(Uri.parse(detailBean.getPosterUrl()));
//        mPhotoImg.setImageURI(Uri.parse(detailBean.getPhotoUrl()));
//        mOriginalImg.setVisibility(detailBean.isOriginal() ? View.VISIBLE : View.GONE);
//
//        mPlayVv.setTag(detailBean.getPlayUrl());
//        mDanceNameTx.setText(detailBean.getDanceName());
//        mTitleTx.setText(detailBean.getTitle());
//        mPlayCountTx.setText(String.valueOf(detailBean.getWatchCount())+"次播放");
//        mNickNameTx.setText(detailBean.getNickName());
//        mTimeDiffTx.setText(detailBean.getTimeDiff());
//
//        mAttenBut.setClickable(detailBean.isAttentioned());
//
//        DetailHandler.Status.watchCount = detailBean.getWatchCount();
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.video_detail_attention:
//                if(SecurityUtils.isUserValidity(getContext(),DetailHandler.watcherUserId)){
////                    mAttenBut.setSelected();
//                }
//                break;
//            case R.id.video_detail_photo:
//                Intent intent = new Intent(getActivity(), UserDetailsActivity.class);
//                intent.putExtra(Constants.Strings.USER_ID, mVideoDetail.getUserId());
//                intent.putExtra(Constants.Strings.USER_NAME, mVideoDetail.getNickName());
//                intent.putExtra(Constants.Strings.USER_PHOTO, mVideoDetail.getPhotoUrl());
//                intent.putExtra(Constants.Strings.USER_ATTEN, mVideoDetail.isAttentioned());
//                startActivity(intent);
//                break;
//            case R.id.video_detail_play:
//                Video video = new Video();
//                VideoUrl videoUrl1 = new VideoUrl();
//                videoUrl1.setFormatUrl((String) mPlayVv.getTag());
//                ArrayList<VideoUrl> arrayList1 = new ArrayList<>();
//                arrayList1.add(videoUrl1);
//                video.setVideoUrl(arrayList1);
//                ArrayList<Video> videoArrayList = new ArrayList<>();
//                videoArrayList.add(video);
//                mPlayVv.loadMultipleVideo(videoArrayList, 0, 0, 0);
//                mHandler.sendEmptyMessage(R.id.msg_video_report_watch_count);
//                mPlayImg.setVisibility(View.INVISIBLE);
//                mPosterImg.setVisibility(View.INVISIBLE);
//                break;
//            default:
//                break;
//        }
//    }
//}
