package cn.spinsoft.wdq.org;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
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
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.effect.AnimatorEffect;
import cn.spinsoft.wdq.enums.Attention;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.mine.component.OrgInfoActivity;
import cn.spinsoft.wdq.mine.component.SimpleInputActivity;
import cn.spinsoft.wdq.org.biz.OrgHandler;
import cn.spinsoft.wdq.org.biz.OrgInfoDetails;
import cn.spinsoft.wdq.org.frag.CourseFrag;
import cn.spinsoft.wdq.org.frag.OrgDescFrag;
import cn.spinsoft.wdq.org.frag.OrgDynamicFrag;
import cn.spinsoft.wdq.org.frag.OrgWorksFrag;
import cn.spinsoft.wdq.org.frag.TeacherFrag;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SecurityUtils;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.utils.session.SessionHelper;
import cn.spinsoft.wdq.widget.RadioGroup;

/**
 * Created by zhoujun on 16/1/7.
 */
public class OrgDetailsActivity extends BaseActivity implements View.OnClickListener, Observer,
        RadioGroup.OnCheckedChangeListener, Handler.Callback {
    private static final String TAG = OrgDetailsActivity.class.getSimpleName();

    private SimpleDraweeView mOrgLogoSdv;
    private TextView mOrgNameTx/*mOrgSignTx*/;
    private RadioGroup mLabelsRg;
    private TextView mSliderTx;
    private ImageButton mAttenIBnt;
    private OrgInfoDetails mOrgInfoDetail = new OrgInfoDetails();
    //    private TextView mWorkTx, mCourseTx, mDynamicTx, mTeacherTx;
    private TextView mWorkNumTx, mCourseNumTx, mDynamicNumTx, mTeacherNumTx;
    private TextView mCountDescTx, mDiffTx;
    private UserLogin userLogin;
    private ImageView mPrivateMsgImg;
    private boolean isCurUser;

    private BaseFragment mDescFrag, mWorksFrag, mCourseFrag, mDynamicFrag, mTeacherFrag;

    public static void start(Context context, String nickName, String photoUrl, int orgId) {
        Intent intent = new Intent(context, OrgDetailsActivity.class);
        intent.putExtra(Constants.Strings.ORG_ID, orgId);
        intent.putExtra(Constants.Strings.ORG_NAME, nickName);
        intent.putExtra(Constants.Strings.ORG_LOGO, photoUrl);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_org_detail;
    }

    @Override
    protected void initHandler() {
        mHandler = new OrgHandler();

        Intent intent = getIntent();
        mOrgInfoDetail.setHeadurl(intent.getStringExtra(Constants.Strings.ORG_LOGO));
        mOrgInfoDetail.setOrgName(intent.getStringExtra(Constants.Strings.ORG_NAME));
        mOrgInfoDetail.setSignature(intent.getStringExtra(Constants.Strings.ORG_SIGN));
        OrgHandler.Status.orgId = intent.getIntExtra(Constants.Strings.ORG_ID, -1);
        OrgHandler.Status.userId = intent.getIntExtra(Constants.Strings.USER_ID, -1);
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView backTx = (TextView) findViewById(R.id.org_detail_back);
        mOrgLogoSdv = (SimpleDraweeView) findViewById(R.id.org_detail_photo);
        mOrgNameTx = (TextView) findViewById(R.id.org_detail_orgName);
        mSliderTx = (TextView) findViewById(R.id.org_detail_slide);
        mLabelsRg = (RadioGroup) findViewById(R.id.org_detail_labels);
        mAttenIBnt = (ImageButton) findViewById(R.id.org_detail_attention);
        mWorkNumTx = (TextView) findViewById(R.id.org_detail_works_num);
        mCourseNumTx = (TextView) findViewById(R.id.org_detail_course_num);
        mDynamicNumTx = (TextView) findViewById(R.id.org_detail_dynamic_num);
        mTeacherNumTx = (TextView) findViewById(R.id.org_detail_teacher_num);
        mCountDescTx = (TextView) findViewById(R.id.org_label_count);
        mDiffTx = (TextView) findViewById(R.id.org_label_diff);
        mPrivateMsgImg = (ImageView) findViewById(R.id.org_detail_privatemsg);

        backTx.setOnClickListener(this);
        mLabelsRg.setOnCheckedChangeListener(this);
        mAttenIBnt.setOnClickListener(this);
        mPrivateMsgImg.setOnClickListener(this);
        mOrgLogoSdv.setOnClickListener(this);

        mDescFrag = (BaseFragment) changeContentFragment(R.id.org_detail_container, mDescFrag, OrgDescFrag.class.getName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferencesUtil.getInstance(this).addObserver(this);
        userLogin = SharedPreferencesUtil.getInstance(this).getLoginUser();
        mHandler.addCallback(OrgHandler.CHILD_HOST, this);
        mHandler.sendEmptyMessage(R.id.msg_org_get_detail);
        if (userLogin != null) {
            if (userLogin.getOrgId() == OrgHandler.Status.orgId) {
                mAttenIBnt.setVisibility(View.GONE);
                mPrivateMsgImg.setVisibility(View.GONE);
                isCurUser = true;
            } else {
                isCurUser = false;
            }
            OrgHandler.watcherUserId = userLogin.getUserId();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        loadDataToWidget(mOrgInfoDetail);
    }

    @Override
    protected void onDestroy() {
        SharedPreferencesUtil.getInstance(this).deleteObserver(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.org_detail_back:
                finish();
                break;
            case R.id.org_detail_attention://关注
                if (SecurityUtils.isUserValidity(this, SharedPreferencesUtil.getInstance(this).getLoginUser())) {
                    OrgHandler.Status.userId = mOrgInfoDetail.getUserId();
                    mAttenIBnt.setSelected(Attention.getReverse(mOrgInfoDetail.getIsAttention()).isAttented());
                    mOrgInfoDetail.setIsAttention(Attention.getReverse(mOrgInfoDetail.getIsAttention()));
                    mHandler.sendEmptyMessage(R.id.msg_org_report_attention);
                }
                break;
            case R.id.org_detail_photo:
                if (isCurUser) {
                    OrgInfoActivity.modeType = Constants.Strings.EDIT_MODE;
                } else {
                    OrgInfoActivity.modeType = Constants.Strings.NORMAL_MODE;
                }
                Intent intent = new Intent(this, OrgInfoActivity.class);
                intent.putExtra(Constants.Strings.ORG_ID, OrgHandler.Status.orgId);
                startActivityForResult(intent, Constants.Ints.REQUEST_CODE_USER_DETAIL);
                break;
            case R.id.org_detail_privatemsg:
                if (SecurityUtils.isUserValidity(this, OrgHandler.watcherUserId)) {
                    LogUtil.w("LoginStatus:", NIMClient.getStatus().toString());
                    LogUtil.w("FriendsCount:", NimUIKit.getContactProvider().getMyFriendsCount() + "");
                    SessionHelper.startP2PSession(this, String.valueOf("orgid" + OrgHandler.Status.orgId), mOrgInfoDetail.getOrgName());
                }
                break;
            case R.id.org_label_diff://添加课程
                intent = new Intent(this, SimpleInputActivity.class);
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_TILE, "添加课程");
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_LIMIT, 10);
                startActivityForResult(intent, Constants.Ints.REQUEST_CODE_COURSE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.Ints.REQUEST_CODE_BOOK_SUCCESS) {
                if (mCourseFrag != null) {
                    mCourseFrag.onActivityResult(requestCode, resultCode, data);
                }
            } else if (requestCode == Constants.Ints.REQUEST_CODE_COURSE) {
                OrgHandler.Status.course = data.getStringExtra(Constants.Strings.SIMPLE_INPUT_RESULT);
                mHandler.sendEmptyMessage(R.id.msg_org_add_course);
            } else if (requestCode == Constants.Ints.REQUEST_CODE_USER_DETAIL) {
                mOrgLogoSdv.setImageURI(Uri.parse(data.getStringExtra(Constants.Strings.ORG_LOGO)));
                mOrgNameTx.setText(data.getStringExtra(Constants.Strings.ORG_NAME));
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        View view = group.findViewById(checkedId);
        AnimatorEffect.smoothHorizontalSlideTo(mSliderTx, view);
        initMenu(checkedId, mOrgInfoDetail);
        mDiffTx.setVisibility(View.GONE);
        switch (checkedId) {
            case R.id.org_detail_desc:
                mDescFrag = (BaseFragment) changeContentFragment(R.id.org_detail_container, mDescFrag,
                        OrgDescFrag.class.getName());

                mCountDescTx.setText("机构简介");
                break;
            case R.id.org_detail_works:
                mWorksFrag = (BaseFragment) changeContentFragment(R.id.org_detail_container, mWorksFrag,
                        OrgWorksFrag.class.getName());


                mDiffTx.setVisibility(View.VISIBLE);
                mDiffTx.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_friend_admire, 0, 0, 0);
                mDiffTx.setTextColor(getResources().getColor(R.color.bg_btn_orange));
                mCountDescTx.setText(mOrgInfoDetail.getWorkCount() + "部作品");
                break;
            case R.id.org_detail_course:
                mCourseFrag = (BaseFragment) changeContentFragment(R.id.org_detail_container, mCourseFrag,
                        CourseFrag.class.getName());


                if (userLogin != null && userLogin.getOrgId() == OrgHandler.Status.orgId) {
                    mDiffTx.setVisibility(View.VISIBLE);
                    mDiffTx.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    mDiffTx.setTextColor(getResources().getColor(R.color.text_purple));
                    mDiffTx.setText("新增课程");
                    mDiffTx.setOnClickListener(this);
                }
                mCountDescTx.setText(mOrgInfoDetail.getCourseCount() + "款课程可选");
                break;
            case R.id.org_detail_dynamic:
                mDynamicFrag = (BaseFragment) changeContentFragment(R.id.org_detail_container, mDynamicFrag,
                        OrgDynamicFrag.class.getName());

                mCountDescTx.setText(mOrgInfoDetail.getDynamicCount() + "条动态");
                break;
            case R.id.org_detail_teacher:
                mTeacherFrag = (BaseFragment) changeContentFragment(R.id.org_detail_container, mTeacherFrag,
                        TeacherFrag.class.getName());

                mCountDescTx.setText(mOrgInfoDetail.getTeacherCount() + "位老师");
                break;
            default:
                break;
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data != null) {
            OrgHandler.watcherUserId = ((UserLogin) data).getUserId();
        } else {
            OrgHandler.watcherUserId = -1;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.msg_org_detail_got:
                if (msg.obj != null) {
                    OrgInfoDetails orgInfoDetails = (OrgInfoDetails) msg.obj;
                    if (orgInfoDetails.getResponse().getCode() == SimpleResponse.SUCCESS) {
                        mOrgInfoDetail = orgInfoDetails;
                        loadDataToWidget(orgInfoDetails);
                    }
                }
                break;
            case R.id.msg_org_course_added:
                if (mCourseFrag != null && msg.obj != null) {
                    SimpleResponse simpleResponse = (SimpleResponse) msg.obj;
                    if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                        ((CourseFrag) mCourseFrag).onRefresh();
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    //初始化部件信息
    private void loadDataToWidget(OrgInfoDetails orgInfoDetail) {
        mOrgLogoSdv.setImageURI(Uri.parse(orgInfoDetail.getHeadurl()));
        mOrgNameTx.setText(orgInfoDetail.getOrgName());
//        mOrgSignTx.setText(orgInfoDetail.getSignature());
        mCountDescTx.setText("机构简介");
        if (orgInfoDetail.getIsAttention() != null) {
            mAttenIBnt.setSelected(orgInfoDetail.getIsAttention().isAttented());
        }
        //初始化菜单
        initMenu(R.id.org_detail_desc, orgInfoDetail);
    }

    private void initMenu(int checkId, OrgInfoDetails orgInfoDetails) {
        mWorkNumTx.setText(String.valueOf(orgInfoDetails.getWorkCount()));
        mCourseNumTx.setText(String.valueOf(orgInfoDetails.getCourseCount()));
        mDynamicNumTx.setText(String.valueOf(orgInfoDetails.getDynamicCount()));
        mTeacherNumTx.setText(String.valueOf(orgInfoDetails.getTeacherCount()));

        mWorkNumTx.setVisibility(View.VISIBLE);
        mCourseNumTx.setVisibility(View.VISIBLE);
        mDynamicNumTx.setVisibility(View.VISIBLE);
        mTeacherNumTx.setVisibility(View.VISIBLE);
        switch (checkId) {
            case R.id.org_detail_works:
                mWorkNumTx.setVisibility(View.INVISIBLE);
                break;
            case R.id.org_detail_course:
                mCourseNumTx.setVisibility(View.INVISIBLE);
                break;
            case R.id.org_detail_dynamic:
                mDynamicNumTx.setVisibility(View.INVISIBLE);
                break;
            case R.id.org_detail_teacher:
                mTeacherNumTx.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }

        /*String workMenuName = "作品\n" + orgInfoDetails.getWorkCount();
        String courseMenuName = "课程\n" + orgInfoDetails.getCourseCount();
        String dynamicMenuName = "动态\n" + orgInfoDetails.getDynamicCount();
        String teacherMenuName = "教师\n" + orgInfoDetails.getTeacherCount();
        switch (checkId) {
            case R.id.org_detail_works:
                workMenuName = "作品";
                break;
            case R.id.org_detail_course:
                courseMenuName = "课程";
                break;
            case R.id.org_detail_dynamic:
                dynamicMenuName = "动态";
                break;
            case R.id.org_detail_teacher:
                teacherMenuName = "教师";
                break;
            default:
                break;
        }

        mWorkTx.setText(StringUtils.changeTextSize(workMenuName, 18), TextView.BufferType.SPANNABLE);
        mCourseTx.setText(StringUtils.changeTextSize(courseMenuName, 18), TextView.BufferType.SPANNABLE);
        mDynamicTx.setText(StringUtils.changeTextSize(dynamicMenuName, 18), TextView.BufferType.SPANNABLE);
        mTeacherTx.setText(StringUtils.changeTextSize(teacherMenuName, 18), TextView.BufferType.SPANNABLE);*/
    }

    //改变diff的值
    public void setDiffTxContent(String content) {
        mDiffTx.setText(content);
    }
}
