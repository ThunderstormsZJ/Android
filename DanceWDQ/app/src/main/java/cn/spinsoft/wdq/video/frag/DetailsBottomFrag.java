package cn.spinsoft.wdq.video.frag;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.bean.CommentListWithInfo;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.ContentResolverUtil;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SecurityUtils;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.video.biz.DanceVideoBean;
import cn.spinsoft.wdq.video.biz.DetailHandler;
import cn.spinsoft.wdq.video.biz.VideoDetailBean;
import cn.spinsoft.wdq.video.widget.CommentsAdapter;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by hushujun on 15/11/6.
 */
public class DetailsBottomFrag extends BaseFragment implements Handler.Callback,
        RecyclerItemClickListener, View.OnClickListener, TextView.OnEditorActionListener {
    private static final String TAG = DetailsBottomFrag.class.getSimpleName();
//    private SimpleDraweeView mWatcherPhotoImg;
    private TextView mCommentsCountTx;
//    private Button mAttentionBtn;
    private TextView mLikeTx, mForwardTx, mCommentTx, mTipsTx;
    private RecyclerView mCommContentsRv;
    private EditText mCommInputEt;

    private CommentsAdapter mCommentsAdapter;
    private DanceVideoBean mVideoBean;

    private OnVideoStateChangeCallBack mVideoStateChangeCallBack;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_video_detail_bottom;
    }

    @Override
    protected void initViewAndListener(View root, Bundle savedInstanceState) {
        mLikeTx = (TextView) root.findViewById(R.id.video_detail_likes);
        mForwardTx = (TextView) root.findViewById(R.id.video_detail_forwards);
        mCommentTx = (TextView) root.findViewById(R.id.video_detail_comments);
        mTipsTx = (TextView) root.findViewById(R.id.video_detail_tips);
        mCommentsCountTx = (TextView) root.findViewById(R.id.video_detail_comment_all);
        mCommContentsRv = (RecyclerView) root.findViewById(R.id.video_detail_comment_newest);
//        mWatcherPhotoImg = (SimpleDraweeView) root.findViewById(R.id.bottom_input_photo);
        mCommInputEt = (EditText) root.findViewById(R.id.bottom_input_edit);
        mCommContentsRv.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));
        mCommentsAdapter = new CommentsAdapter(null, this);
        mCommContentsRv.setAdapter(mCommentsAdapter);

        mLikeTx.setOnClickListener(this);
        mForwardTx.setOnClickListener(this);
        mCommentTx.setOnClickListener(this);
        mTipsTx.setOnClickListener(this);
        mCommInputEt.setOnEditorActionListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler.addCallback(DetailHandler.CHILD_BOTTOM, this);
        mHandler.sendEmptyMessage(R.id.msg_video_get_comment_list);

        mVideoBean = getActivity().getIntent().getParcelableExtra(Constants.Strings.VIDEO_BEAN);
        if (mVideoBean != null) {
            mLikeTx.setText("共 " + mVideoBean.getLikeCount() + " 人喜欢");
            mCommentTx.setText("共 " + mVideoBean.getCommentCount() + " 条评论");
            mLikeTx.setSelected(mVideoBean.isLiked());
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UserLogin userLogin = SharedPreferencesUtil.getInstance(getActivity()).getLoginUser();
        if (userLogin != null) {
//            mWatcherPhotoImg.setImageURI(Uri.parse(userLogin.getPhotoUrl()));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mVideoStateChangeCallBack = (OnVideoStateChangeCallBack) context;
    }

    @Override
    public boolean handleMessage(Message msg) {
        LogUtil.w(TAG, msg.toString());
        switch (msg.what) {
            case R.id.msg_comment_sent_video:
                if (msg.obj != null) {
                    boolean success = (boolean) msg.obj;
                    if (success) {
                        Toast.makeText(getActivity(), "评论成功", Toast.LENGTH_SHORT).show();
                        mHandler.sendEmptyMessage(R.id.msg_video_get_comment_list);
                    } else {
                        Toast.makeText(getActivity(), "评论失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.msg_video_tips_reported:
                // TODO: 15/11/20
                break;
            case R.id.msg_forward_reported_video:
                // TODO: 15/11/20
                break;
            case R.id.msg_like_reported_video:
                int lickCount = msg.arg1;
                if (lickCount >= 0) {
                    Toast.makeText(getActivity(), "喜欢成功", Toast.LENGTH_SHORT).show();
                    mLikeTx.setText(String.valueOf(lickCount));
                    if(mVideoBean!=null){
                        mVideoBean.setLikeCount(lickCount);
                        mVideoStateChangeCallBack.videoStateChangeCallBack(mVideoBean);//回调更新视频的状态信息
                    }
                } else {
                    Toast.makeText(getActivity(), "喜欢失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.msg_attention_reported_video:
                if (msg.obj != null) {
                    SimpleResponse success = (SimpleResponse) msg.obj;
                    if (success.getCode() == SimpleResponse.SUCCESS) {
                        Toast.makeText(getActivity(), "关注成功", Toast.LENGTH_SHORT).show();
                        if(mVideoBean!=null){
                            mVideoStateChangeCallBack.videoStateChangeCallBack(mVideoBean);//回调更新视频的状态信息
                        }
                    } else {
                        Toast.makeText(getActivity(), success.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.msg_video_comment_list_got:
                if (msg.obj != null) {
                    CommentListWithInfo commentWithInfo = (CommentListWithInfo) msg.obj;
                    if (DetailHandler.Status.commentPageIdx == 1) {
                        mCommentsAdapter.setAdapterDataList(commentWithInfo.getDataList());
                    } else {
                        mCommentsAdapter.addAdapterDataList(commentWithInfo.getDataList());
                    }
                    mCommentsCountTx.setText("共 " + commentWithInfo.getTotalRows() + " 条评论");
                    mCommentTx.setText("共 " + commentWithInfo.getTotalRows() + " 条评论");
                    if(mVideoBean!=null){
                        mVideoBean.setCommentCount(commentWithInfo.getTotalRows());
                        mVideoStateChangeCallBack.videoStateChangeCallBack(mVideoBean);//回调更新视频的状态信息
                    }
                }
                break;
            /*case R.id.msg_video_detail_got:
                if(msg.obj!=null){
                    loadDataToWidget((VideoDetailBean) msg.obj);
                }
                break;*/
            default:
                break;
        }
        return true;
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, int position) {
        // TODO: 15/12/3 comment list item
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.video_detail_attention:
                if (SecurityUtils.isUserValidity(getActivity(), DetailHandler.watcherUserId)) {
                    mHandler.sendEmptyMessage(R.id.msg_report_attention_video);

                    mVideoBean.setAttention(Attention.getReverse(mVideoBean.getAttention()));
                    mAttentionBtn.setSelected(mVideoBean.getAttention().isAttented());
                }
                break;*/
            case R.id.video_detail_likes:
                if (SecurityUtils.isUserValidity(getActivity(), DetailHandler.watcherUserId)) {
                    mHandler.sendEmptyMessage(R.id.msg_report_like_video);

                    mVideoBean.setLiked(!mVideoBean.isLiked());
                    mLikeTx.setSelected(mVideoBean.isLiked());
                }
                break;
            case R.id.video_detail_forwards:
                if (SecurityUtils.isUserValidity(getActivity(), DetailHandler.watcherUserId)) {
                    DetailHandler.Status.forwardCase = "wechat";
                    mHandler.sendEmptyMessage(R.id.msg_report_forward_video);

                    mForwardTx.setSelected(true);
                }
                break;
            case R.id.video_detail_comments:
                if (SecurityUtils.isUserValidity(getActivity(), DetailHandler.watcherUserId)) {
                    mCommInputEt.requestFocus();
                    ContentResolverUtil.showIMM(v);
                }
                break;
            case R.id.video_detail_tips:
                if (SecurityUtils.isUserValidity(getActivity(), DetailHandler.watcherUserId)) {
                    DetailHandler.Status.tipsQuantity = 250;
                    mHandler.sendEmptyMessage(R.id.msg_video_report_tips);

                    mTipsTx.setSelected(true);
                }
                break;
            case R.id.bottom_input_edit:
                if (SecurityUtils.isUserValidity(getActivity(), DetailHandler.watcherUserId)) {
                    ContentResolverUtil.showIMM(v);
                }
                break;
//            case R.id.video_detail_comment_send:
//                sendComment();
//                break;
            default:
                break;
        }
    }

    private void sendComment(View v) {
        CharSequence comment = mCommInputEt.getText();
        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(getActivity(), "请输入您的评论内容", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentResolverUtil.hideIMM(v);
        DetailHandler.Status.commentContent = comment.toString();
        mHandler.sendEmptyMessage(R.id.msg_send_comment_video);

        mCommentTx.setSelected(true);

        mCommInputEt.setText("");
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            if (SecurityUtils.isUserValidity(getActivity(), DetailHandler.watcherUserId)) {
                sendComment(v);
                return true;
            }
        }
        return false;
    }

    //加载信息到部件
    public void loadDataToWidget(VideoDetailBean videoDetailBean){
        if(mVideoBean==null){
            mLikeTx.setText("共 " + videoDetailBean.getLikeCount() + " 人喜欢");
            mCommentTx.setText("共 " + videoDetailBean.getCommentCount() + " 条评论");
            mLikeTx.setSelected(videoDetailBean.isLike());
        }
    }

    //当视频的状态改变时的回调函数
    public interface OnVideoStateChangeCallBack{
        //视频状态的改变
        void videoStateChangeCallBack(DanceVideoBean danceVideoBean);
    }
}
