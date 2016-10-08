package cn.spinsoft.wdq.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;

import java.util.Observable;
import java.util.Observer;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.effect.AnimatorEffect;
import cn.spinsoft.wdq.enums.Attention;
import cn.spinsoft.wdq.enums.Sex;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.mine.component.MineInfoActivity;
import cn.spinsoft.wdq.user.biz.UserDetail;
import cn.spinsoft.wdq.user.biz.UserHandler;
import cn.spinsoft.wdq.user.frag.AttentionFrag;
import cn.spinsoft.wdq.user.frag.FansFrag;
import cn.spinsoft.wdq.user.frag.UserDynamicFrag;
import cn.spinsoft.wdq.user.frag.UserWorksFrag;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SecurityUtils;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.utils.session.SessionHelper;
import cn.spinsoft.wdq.video.biz.VideoDetailBean;
import cn.spinsoft.wdq.widget.RadioGroup;

/**
 * Created by zhoujun on 15/11/13.
 */
public class UserDetailsActivity extends BaseActivity implements View.OnClickListener, Handler.Callback,
        RadioGroup.OnCheckedChangeListener, Observer {
    private static final String TAG = UserDetailsActivity.class.getSimpleName();

    private SimpleDraweeView mPhotoImg;
    private TextView mNickNameTx, mSignatureTx/*, mAttestTx*/;
    private ImageButton mAttentionBtn;
    private RadioGroup mPageLabelRg;
    //    private TextView mWorkTx, mDynamicTx, mAttentionTx, mFansTx;
    private TextView mWorkNumTx, mDynamicNumTx, mAttentionNumTx, mFansNumTx;
    private View mLabelSlider;
    private TextView mLabelCountTx, mDiffTx/*, mPrivateMsgTx*/;
    private ImageView mGenderImg,mPrivateMsgImg;

    private UserDetail mUserDetail = new UserDetail();
    private VideoDetailBean mVideoDetail;
    private boolean isCurrUser = false;

    private BaseFragment mWorksFrag, mDynamicFrag, mAttentionFrag, mFansFrag;

    public static void start(Context context, String nickName, String photoUrl, int userId) {
        Intent intent = new Intent(context, UserDetailsActivity.class);
        intent.putExtra(Constants.Strings.USER_ID, userId);
        intent.putExtra(Constants.Strings.USER_NAME, nickName);
        intent.putExtra(Constants.Strings.USER_PHOTO, photoUrl);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_detail;
    }

    @Override
    protected void initHandler() {
        mHandler = new UserHandler();

        Intent intent = getIntent();
        mUserDetail.setUserId(intent.getIntExtra(Constants.Strings.USER_ID, -1));
        mUserDetail.setPhotoUrl(intent.getStringExtra(Constants.Strings.USER_PHOTO));
        mUserDetail.setNickName(intent.getStringExtra(Constants.Strings.USER_NAME));
        mUserDetail.setSignature(intent.getStringExtra(Constants.Strings.USER_SIGN));
        mUserDetail.setAttention(Attention.getEnum(intent.getIntExtra(Constants.Strings.USER_ATTEN,
                Attention.NONE.getValue())));
        mVideoDetail = intent.getParcelableExtra(Constants.Strings.VIDEO_BEAN);

        UserHandler.Status.userId = mUserDetail.getUserId();
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView mBackTx = (TextView) findViewById(R.id.user_detail_back);
        mPhotoImg = (SimpleDraweeView) findViewById(R.id.user_photo);
        mNickNameTx = (TextView) findViewById(R.id.user_nickName);
        mSignatureTx = (TextView) findViewById(R.id.user_signature);
        mAttentionBtn = (ImageButton) findViewById(R.id.user_attention);
        mPageLabelRg = (RadioGroup) findViewById(R.id.user_page_labels);
        mLabelSlider = findViewById(R.id.user_label_slider);
        mLabelCountTx = (TextView) findViewById(R.id.user_label_count);
        mWorkNumTx = (TextView) findViewById(R.id.user_label_works_num);
        mDynamicNumTx = (TextView) findViewById(R.id.user_label_dynamic_num);
        mAttentionNumTx = (TextView) findViewById(R.id.user_label_attention_num);
        mFansNumTx = (TextView) findViewById(R.id.user_label_fans_num);
        mPrivateMsgImg = (ImageView) findViewById(R.id.user_private_msg);
        mDiffTx = (TextView) findViewById(R.id.user_label_diff);
        mGenderImg = (ImageView) findViewById(R.id.user_gender);

        mBackTx.setOnClickListener(this);
        mAttentionBtn.setOnClickListener(this);
        mPageLabelRg.setOnCheckedChangeListener(this);
        mPrivateMsgImg.setOnClickListener(this);
        mPhotoImg.setOnClickListener(this);

        loadDataToWidget();

        mHandler.addCallback(UserHandler.CHILD_HOST, this);
        try {
            mHandler.sendEmptyMessage(R.id.msg_user_get_detail);
        } catch (Exception e) {
            LogUtil.w(TAG, e.toString());
        }

        mWorksFrag = (BaseFragment) changeContentFragment(R.id.user_child_container, mWorksFrag, UserWorksFrag.class.getName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserLogin userLogin = SharedPreferencesUtil.getInstance(this).getLoginUser();
        if (userLogin != null && userLogin.getUserId() == UserHandler.Status.userId) {
            mAttentionBtn.setVisibility(View.GONE);
            mPrivateMsgImg.setVisibility(View.GONE);
            isCurrUser = true;
        } else {
            isCurrUser = false;
        }
        SharedPreferencesUtil.getInstance(this).addObserver(this);
    }

    @Override
    public void finish() {
        if (mVideoDetail != null) {
            Intent intent = new Intent();
            intent.putExtra(Constants.Strings.VIDEO_BEAN, mVideoDetail);
            setResult(Activity.RESULT_OK, intent);
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        SharedPreferencesUtil.getInstance(this).deleteObserver(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_detail_back:
                finish();
                break;
            case R.id.user_attention:
                if (SecurityUtils.isUserValidity(this, UserHandler.watcherUserId)) {
                    toggleAttentionState();
                    mHandler.sendEmptyMessage(R.id.msg_report_attention_video);
                }
                break;
            case R.id.user_photo:
                if (SecurityUtils.isUserValidity(this, UserHandler.watcherUserId)) {
                    if (isCurrUser) {
                        MineInfoActivity.modeType = Constants.Strings.EDIT_MODE;
                    } else {
                        MineInfoActivity.modeType = Constants.Strings.NORMAL_MODE;
                    }
                    Intent miIntent = new Intent(this, MineInfoActivity.class);
                    miIntent.putExtra(Constants.Strings.USER_ID, UserHandler.Status.userId);
                    startActivityForResult(miIntent, Constants.Ints.REQUEST_CODE_USER_DETAIL);
                }
                break;
            case R.id.user_private_msg:
                if (SecurityUtils.isUserValidity(this, UserHandler.watcherUserId)) {
                    LogUtil.w("LoginStatus:", NIMClient.getStatus().toString());
                    LogUtil.w("FriendsCount:", NimUIKit.getContactProvider().getMyFriendsCount() + "");
                    SessionHelper.startP2PSession(this, String.valueOf("userid" + UserHandler.Status.userId), mUserDetail.getNickName());
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.Ints.REQUEST_CODE_USER_DETAIL) {
                mUserDetail.setNickName(data.getStringExtra(Constants.Strings.USER_NAME));
                mUserDetail.setPhotoUrl(data.getStringExtra(Constants.Strings.USER_PHOTO));
                mUserDetail.setSignature(data.getStringExtra(Constants.Strings.USER_SIGN));
                mUserDetail.setSex(Sex.getEnum(data.getIntExtra(Constants.Strings.USER_SEX, -1)));
                loadDataToWidget();
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        TextView view = (TextView) group.findViewById(checkedId);
        AnimatorEffect.smoothHorizontalSlideTo(mLabelSlider, view);
        initMenu(checkedId);
        mDiffTx.setVisibility(View.GONE);
        switch (checkedId) {
            case R.id.user_label_works:
                mWorksFrag = (BaseFragment) changeContentFragment(R.id.user_child_container,
                        mWorksFrag, UserWorksFrag.class.getName());


                mLabelCountTx.setText(mUserDetail.getWorksNum() + " 部作品");
                break;
            case R.id.user_label_dynamic:
                mDynamicFrag = (BaseFragment) changeContentFragment(R.id.user_child_container,
                        mDynamicFrag, UserDynamicFrag.class.getName());

                mLabelCountTx.setText(mUserDetail.getDynamicNum() + " 条动态");
                break;
            case R.id.user_label_attention:
                mAttentionFrag = (BaseFragment) changeContentFragment(R.id.user_child_container,
                        mAttentionFrag, AttentionFrag.class.getName());

                mLabelCountTx.setText(mUserDetail.getAttentNum() + " 位关注");
                break;
            case R.id.user_label_fans:
                mFansFrag = (BaseFragment) changeContentFragment(R.id.user_child_container,
                        mFansFrag, FansFrag.class.getName());

                mLabelCountTx.setText(mUserDetail.getFansNum() + " 位粉丝");
                break;
            default:
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == R.id.msg_user_detail_got) {
            if (msg.obj != null) {
                UserDetail userDetail = (UserDetail) msg.obj;
                if (userDetail != null) {
                    mUserDetail = userDetail;
                    loadDataToWidget();
                }
            }
        }
        return true;
    }

    private void loadDataToWidget() {
        mPhotoImg.setImageURI(Uri.parse(mUserDetail.getPhotoUrl()));
        mNickNameTx.setText(mUserDetail.getNickName());
        if (TextUtils.isEmpty(mUserDetail.getSignature())) {
            mSignatureTx.setText("这个人很懒什么都没有留下");
        } else {
            mSignatureTx.setText(mUserDetail.getSignature());
        }
        if (mUserDetail.getSex() == Sex.FEMALE) {
            mGenderImg.setImageResource(R.mipmap.user_gender_woman);
        } else {
            mGenderImg.setImageResource(R.mipmap.user_gender_man);
        }

        initMenu(R.id.user_label_works);

        changeAttentionState();
        mLabelCountTx.setText(mUserDetail.getWorksNum() + " 部作品");
    }

    private void initMenu(int checkId) {
        mWorkNumTx.setText(String.valueOf(mUserDetail.getWorksNum()));
        mDynamicNumTx.setText(String.valueOf(mUserDetail.getDynamicNum()));
        mAttentionNumTx.setText(String.valueOf(mUserDetail.getAttentNum()));
        mFansNumTx.setText(String.valueOf(mUserDetail.getFansNum()));

        mWorkNumTx.setVisibility(View.VISIBLE);
        mDynamicNumTx.setVisibility(View.VISIBLE);
        mAttentionNumTx.setVisibility(View.VISIBLE);
        mFansNumTx.setVisibility(View.VISIBLE);
        switch (checkId) {
            case R.id.user_label_works:
                mWorkNumTx.setVisibility(View.INVISIBLE);
                mDiffTx.setVisibility(View.VISIBLE);
                mDiffTx.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_friend_admire, 0, 0, 0);
                mDiffTx.setTextColor(getResources().getColor(R.color.bg_btn_orange));
                break;
            case R.id.user_label_dynamic:
                mDynamicNumTx.setVisibility(View.INVISIBLE);
                break;
            case R.id.user_label_attention:
                mAttentionNumTx.setVisibility(View.INVISIBLE);
                break;
            case R.id.user_label_fans:
                mFansNumTx.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
        /*String workMenuName = "作品\n" + mUserDetail.getWorksNum();
        String dynamicMenuName = "动态\n" + mUserDetail.getDynamicNum();
        String attentionMenuName = "关注\n" + mUserDetail.getAttentNum();
        String fansMenuName = "粉丝\n" + mUserDetail.getFansNum();
        switch (checkId) {
            case R.id.user_label_works:
                workMenuName = "作品";
                break;
            case R.id.user_label_dynamic:
                dynamicMenuName = "动态";
                break;
            case R.id.user_label_attention:
                attentionMenuName = "关注";
                break;
            case R.id.user_label_fans:
                fansMenuName = "粉丝";
                break;
            default:
                break;
        }

        mWorkTx.setText(StringUtils.changeTextSize(workMenuName, 18), TextView.BufferType.SPANNABLE);
        mDynamicTx.setText(StringUtils.changeTextSize(dynamicMenuName, 18), TextView.BufferType.SPANNABLE);
        mAttentionTx.setText(StringUtils.changeTextSize(attentionMenuName, 18), TextView.BufferType.SPANNABLE);
        mFansTx.setText(StringUtils.changeTextSize(fansMenuName, 18), TextView.BufferType.SPANNABLE);*/
    }

    @Deprecated
    private void displayAttentionState() {
        if (mUserDetail.getAttention().isAttented()) {
            mAttentionBtn.setBackgroundResource(R.mipmap.friend_delete);
        } else {
            mAttentionBtn.setBackgroundResource(R.mipmap.friend_add);
        }
        //如果用户是当前登录用户隐藏关注按钮
        if (mUserDetail.getUserId() == UserHandler.watcherUserId) {
            mAttentionBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void changeAttentionState() {
        if (mUserDetail.getAttention().isAttented()) {
            mAttentionBtn.setSelected(true);
        } else {
            mAttentionBtn.setSelected(false);
        }
    }

    private void toggleAttentionState() {
        boolean atted = mUserDetail.getAttention().isAttented();
        boolean attTemp = !atted;
        if (attTemp) {
            if (!atted) {
                mUserDetail.setAttention(Attention.getEnum(mUserDetail.getAttention().getValue() + 1));
            }
        } else {
            if (atted) {
                mUserDetail.setAttention(Attention.getEnum(mUserDetail.getAttention().getValue() - 1));
            }
        }
        changeAttentionState();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data != null) {
            UserHandler.watcherUserId = ((UserLogin) data).getUserId();
        } else {
            UserHandler.watcherUserId = -1;
        }
    }

    //改变diff的值
    public void setDiffTxContent(String content) {
        mDiffTx.setText(content);
    }
}
