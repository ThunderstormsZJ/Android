package cn.spinsoft.wdq.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.browse.BrowseNewActivity;
import cn.spinsoft.wdq.enums.Sex;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.mine.biz.MineHandler;
import cn.spinsoft.wdq.mine.biz.UserInfoDetail;
import cn.spinsoft.wdq.mine.component.ImageChooseClipActivity;
import cn.spinsoft.wdq.mine.widget.ChooseAddressDia;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.utils.StringUtils;
import cn.spinsoft.wdq.widget.CameraDialog;

/**
 * Created by zhoujun on 2016-5-5.
 */
public class PerfectInfoActivity extends ImageChooseClipActivity implements View.OnClickListener, TextWatcher,
        Handler.Callback, ChooseAddressDia.OnAddressChooseListener {

    private final static String TAG = PerfectInfoActivity.class.getSimpleName();

    private ViewStub mDiffContainerVs;
    private SimpleDraweeView mHeadImg;
    private EditText mNickNameEt, mPhoneEt, mDetailAddressEt;
    private TextView mAddressTx;
    private TextView mHeadTipTx;
    private TextView mDoCompleteTx;

    private CameraDialog mCameraDialog;
    private ChooseAddressDia mAddressDialog;

    private boolean isOrg = false;
    private Sex mUserSex = Sex.MAN;
    private UserLogin userLogin;
    private UserInfoDetail mUserDetail;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_perfect_info;
    }

    @NonNull
    @Override
    protected ImageView getPreviewImg() {
        return mHeadImg;
    }

    @Override
    protected void uploadResult(@Nullable String imageUrl) {
        mHeadImg.setTag(imageUrl);
        if (isOrg) {
            if (checkOrgInputInfo()) {
                mDoCompleteTx.setEnabled(true);
            } else {
                mDoCompleteTx.setEnabled(false);
            }
        } else {
            if (checkUserInputInfo()) {
                mDoCompleteTx.setEnabled(true);
            } else {
                mDoCompleteTx.setEnabled(false);
            }
        }
    }

    @Override
    protected void initHandler() {
        mHandler = new MineHandler();
        mHandler.addCallback(MineHandler.CHILD_HOST, this);

        Intent intent = getIntent();
        userLogin = intent.getParcelableExtra(Constants.Strings.USER_LOGIN);
        if (userLogin != null) {
            if (userLogin.getOrgId() > 0) {
                MineHandler.Status.orgId = userLogin.getOrgId();
                isOrg = true;
            } else {
                isOrg = false;
            }
            MineHandler.Status.userId = userLogin.getUserId();
        }
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        mDiffContainerVs = (ViewStub) findViewById(R.id.perfect_info_diff);
        TextView title = (TextView) findViewById(R.id.base_title_name);
        title.setText("资料完善");
        TextView back = (TextView) findViewById(R.id.base_title_back);
        back.setVisibility(View.GONE);

        mHeadImg = (SimpleDraweeView) findViewById(R.id.perfect_info_head);
        mHeadTipTx = (TextView) findViewById(R.id.perfect_info_headtip);
        mDoCompleteTx = (TextView) findViewById(R.id.perfect_info_complete);

        if (!isOrg) {
            initUserWidget();
        } else {
            initOrgWidget();
        }

        mHeadImg.setOnClickListener(this);
        mDoCompleteTx.setOnClickListener(this);
    }

    //用户
    private void initUserWidget() {
        mDiffContainerVs.setLayoutResource(R.layout.ly_perfectinfo_diff_user);
        View userDiffV = mDiffContainerVs.inflate();

        mNickNameEt = (EditText) userDiffV.findViewById(R.id.perfect_info_user_nickname);
        RadioGroup sexRg = (RadioGroup) userDiffV.findViewById(R.id.perfect_info_user_sex_rg);

        mNickNameEt.addTextChangedListener(this);
        sexRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.perfect_info_user_sex_man) {
                    mUserSex = Sex.MAN;
                } else if (checkedId == R.id.perfect_info_user_sex_woman) {
                    mUserSex = Sex.FEMALE;
                }
            }
        });
    }

    //机构
    private void initOrgWidget() {
        mHeadImg.setImageResource(R.mipmap.ic_default_org_head);
        mHeadTipTx.setText("上传机构标志");

        mDiffContainerVs.setLayoutResource(R.layout.ly_perfectinfo_diff_org);
        View userDiffV = mDiffContainerVs.inflate();
        mNickNameEt = (EditText) userDiffV.findViewById(R.id.perfect_info_org_nickname);
        mPhoneEt = (EditText) userDiffV.findViewById(R.id.perfect_info_org_phone);
        mDetailAddressEt = (EditText) userDiffV.findViewById(R.id.perfect_info_org_detailaddress);
        mAddressTx = (TextView) userDiffV.findViewById(R.id.perfect_info_org_address);

        mAddressDialog = new ChooseAddressDia(this, null, null, null);
        mAddressDialog.setOnAddressConfirmListener(this);

        mAddressTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddressDialog.show();
            }
        });

        mNickNameEt.addTextChangedListener(this);
        mPhoneEt.addTextChangedListener(this);
        mDetailAddressEt.addTextChangedListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler.sendEmptyMessage(R.id.msg_mine_get_info_detail);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.perfect_info_head:
                if (mCameraDialog == null) {
                    mCameraDialog = new CameraDialog(this, CameraDialog.Type.IMAGE);
                }
                mCameraImageName = "User" + MineHandler.Status.userId + "_IMG_" + StringUtils.formatCurrentDateTime() + ".jpg";
                mCameraDialog.setImageName(mCameraImageName);
                mCameraDialog.show();
                break;
            case R.id.perfect_info_complete:
                if (!isOrg) {
                    doUserComplete();
                } else {
                    doOrgComplete();
                }
                break;
        }
    }

    @Override
    public void OnAddressComfirm(String chooseAddress) {
        LogUtil.w(TAG, chooseAddress);
        mAddressTx.setText(StringUtils.getNoBlankString(chooseAddress));
        mAddressTx.setTag(chooseAddress);
    }

    private void doUserComplete() {
        mUserDetail.setNickName(mNickNameEt.getText().toString());
        mUserDetail.setPhotoUrl(mHeadImg.getTag().toString());
        mUserDetail.setSex(mUserSex);

        userLogin.setNickName(mNickNameEt.getText().toString());
        userLogin.setPhotoUrl(mHeadImg.getTag().toString());

        Message msg = mHandler.obtainMessage(R.id.msg_mine_update_info);
        msg.obj = mUserDetail;
        mHandler.sendMessage(msg);
    }

    private void doOrgComplete() {
        mUserDetail.setNickName(mNickNameEt.getText().toString());
        mUserDetail.setPhotoUrl(mHeadImg.getTag().toString());
        mUserDetail.setContactWay(mPhoneEt.getText().toString());
        mUserDetail.setOrgAddress(mAddressTx.getTag().toString() + " " +
                StringUtils.getNoBlankString(mDetailAddressEt.getText().toString()));
        List<String> attachs = new ArrayList<>();
        attachs.add("");
        mUserDetail.setAttachments(attachs);

        userLogin.setNickName(mNickNameEt.getText().toString());
        userLogin.setPhotoUrl(mHeadImg.getTag().toString());

        Message msg = mHandler.obtainMessage(R.id.msg_org_update_info);
        msg.obj = mUserDetail;
        mHandler.sendMessage(msg);
    }

    private boolean checkUserInputInfo() {
        if (mHeadImg.getTag() != null && !TextUtils.isEmpty(mNickNameEt.getText())) {
            return true;
        }
        return false;
    }

    private boolean checkOrgInputInfo() {
        if (mHeadImg.getTag() != null
                && !TextUtils.isEmpty(mNickNameEt.getText())
                && !TextUtils.isEmpty(mPhoneEt.getText())
                && !TextUtils.isEmpty(mDetailAddressEt.getText())
                && mAddressTx.getTag() != null) {
            return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isOrg) {
            if (checkOrgInputInfo()) {
                mDoCompleteTx.setEnabled(true);
            } else {
                mDoCompleteTx.setEnabled(false);
            }
        } else {
            if (checkUserInputInfo()) {
                mDoCompleteTx.setEnabled(true);
            } else {
                mDoCompleteTx.setEnabled(false);
            }
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.msg_mine_info_detail_got:
                if (msg.obj != null) {
                    UserInfoDetail userInfoDetail = (UserInfoDetail) msg.obj;
                    if (userInfoDetail != null && userInfoDetail.getResponse().getCode() == SimpleResponse.SUCCESS) {
                        mUserDetail = userInfoDetail;
                    }
                }
                break;
            case R.id.msg_mine_info_updated:
                if (msg.obj != null) {
                    if (((SimpleResponse) msg.obj).getCode() == SimpleResponse.SUCCESS) {
                        SharedPreferencesUtil.getInstance(this).saveLoginUser(userLogin);
                        finish();
                        BrowseNewActivity.start(this, null);
                    }
                }
                break;
            case R.id.msg_org_info_updated:
                if (msg.obj != null) {
                    if (((SimpleResponse) msg.obj).getCode() == SimpleResponse.SUCCESS) {
                        SharedPreferencesUtil.getInstance(this).saveLoginUser(userLogin);
                        finish();
                        BrowseNewActivity.start(this, null);
                    }
                }
                break;
        }
        return true;
    }
}
