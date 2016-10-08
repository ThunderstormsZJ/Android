/*
package cn.spinsoft.wdq.video;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Observable;
import java.util.Observer;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.video.biz.DanceVideoBean;
import cn.spinsoft.wdq.video.biz.DetailHandler;
import cn.spinsoft.wdq.video.biz.DetailHandler.Status;
import cn.spinsoft.wdq.video.biz.VideoDetailBean;
import cn.spinsoft.wdq.video.frag.DetailsBottomFrag;
import cn.spinsoft.wdq.video.frag.DetailsTopFrag;
import cn.spinsoft.wdq.widget.BrowseFrameLayout;

*/
/**
 * Created by hushujun on 15/11/6.
 *//*

public class VideoDetailsActivity extends BaseActivity implements GestureDetector.OnGestureListener,
        Observer, DetailsBottomFrag.OnVideoStateChangeCallBack,Handler.Callback,View.OnClickListener{
    private static final String TAG = VideoDetailsActivity.class.getSimpleName();
    private BrowseFrameLayout mBaseFrame, mTopFrame, mBottomFrame;
    private TextView mBackOperationTx;

    private GestureDetector mDetector;

    private DanceVideoBean mDanceVideoBean;
    private VideoDetailBean mVideoDetail;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_details;
    }

    @Override
    protected void initHandler() {
        mHandler = new DetailHandler();

        Intent intent = getIntent();
        DanceVideoBean videoBean = intent.getParcelableExtra(Constants.Strings.VIDEO_BEAN);
        if (videoBean != null) {
            Status.videoId = videoBean.getVideoId();
            Status.ownerUserId = videoBean.getUserId();
        } else {
            Status.videoId = intent.getIntExtra(Constants.Strings.VIDEO_ID, -1);
            Status.ownerUserId = intent.getIntExtra(Constants.Strings.OWNER_ID, -1);
        }

        UserLogin userInfo = SharedPreferencesUtil.getInstance(this).getLoginUser();
        if (userInfo != null) {
            DetailHandler.watcherUserId = userInfo.getUserId();
        }
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        mBaseFrame = (BrowseFrameLayout) findViewById(R.id.video_detail_base_frame);
        mTopFrame = (BrowseFrameLayout) findViewById(R.id.video_detail_base_top_frame);
        mBottomFrame = (BrowseFrameLayout) findViewById(R.id.video_detail_base_bottom_frame);
        mBackOperationTx = (TextView) findViewById(R.id.video_detailInfo_back);

        mBackOperationTx.setOnClickListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.video_detail_base_top_frame, new DetailsTopFrag(),DetailsTopFrag.class.getName()).commit();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.video_detail_base_bottom_frame, new DetailsBottomFrag(),DetailsBottomFrag.class.getName()).commit();

        mDetector = new GestureDetector(this, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler.addCallback(DetailHandler.MAIN_ACTIVITY, this);
        mHandler.sendEmptyMessage(R.id.msg_video_get_detail);
        SharedPreferencesUtil.getInstance(this).addObserver(this);
    }

    @Override
    protected void onDestroy() {
        SharedPreferencesUtil.getInstance(this).deleteObserver(this);
        super.onDestroy();
    }

    float downY;
    int topMargin = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downY = event.getY();
                    topMargin = mBaseFrame.getTop();
                    break;
               */
/* case MotionEvent.ACTION_MOVE:
                    float y = event.getY();
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mBaseFrame.getLayoutParams();
                    int topHeight = mTopFrame.getHeight() + 20;
                    if (lp.topMargin <= 0 && lp.topMargin >= -topHeight) {
                        lp.topMargin = topMargin + (int) (y - downY);
                        if (lp.topMargin < -topHeight) {
                            lp.topMargin = -topHeight;
                        } else if (lp.topMargin > 0) {
                            lp.topMargin = 0;
                        }
                    }
                    mBaseFrame.setLayoutParams(lp);
                    break;*//*

            }

        }
        return super.onTouchEvent(event);
//        return mDetector.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mBaseFrame.getLayoutParams();
        if (lp.topMargin != 0) {
            lp.topMargin = 0;
            mBaseFrame.setLayoutParams(lp);
            return;
        } else {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_detailInfo_back:
                this.finish();break;
            default:break;
        }
    }

    @Override
    public void finish() {
        if (mDanceVideoBean != null) {
            Intent intent = new Intent();
            intent.putExtra(Constants.Strings.VIDEO_BEAN, mDanceVideoBean);
            setResult(Activity.RESULT_OK, intent);
        }
        super.finish();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
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
    public void videoStateChangeCallBack(DanceVideoBean danceVideoBean) {
        mDanceVideoBean = danceVideoBean;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case R.id.msg_video_detail_got:
                mVideoDetail = (VideoDetailBean) msg.obj;
                if(mVideoDetail!=null){
                    DetailsTopFrag detailsTopFrag = (DetailsTopFrag) getSupportFragmentManager().findFragmentByTag(DetailsTopFrag.class.getName());
                    detailsTopFrag.loadDataToWidget(mVideoDetail);
                    DetailsBottomFrag detailsBottomFrag = (DetailsBottomFrag) getSupportFragmentManager().findFragmentByTag(DetailsBottomFrag.class.getName());
                    detailsBottomFrag.loadDataToWidget(mVideoDetail);
                }
                break;
            default:break;
        }
        return false;
    }
}
*/
