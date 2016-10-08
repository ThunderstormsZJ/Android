package cn.spinsoft.wdq.mine.component;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.album.PicturesActivity;
import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.mine.biz.MineHandler;
import cn.spinsoft.wdq.mine.biz.UserInfoDetail;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SimpleItemDataUtils;
import cn.spinsoft.wdq.utils.StringUtils;
import cn.spinsoft.wdq.widget.CameraDialog;
import cn.spinsoft.wdq.widget.ChoiceModeDialog;

/**
 * Created by zhoujun on 16/1/13.
 */
public class OrgInfoActivity extends ImageChooseClipActivity implements View.OnClickListener,
        ChoiceModeDialog.OnItemCheckedListener, Handler.Callback {
    private static final String TAG = OrgInfoActivity.class.getSimpleName();

    public static String modeType = Constants.Strings.EDIT_MODE;

    private SimpleDraweeView mLogoSdv;
    private TextView mOrgNameTx, mDancesTx, mContactTx, mDescTx, mAttachTx, mAddressTx;

    private UserInfoDetail mInfoDetail, mInfoTemp;
    private ChoiceModeDialog mDanceDia;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_org_info;
    }

    @Override
    protected void initHandler() {
        mHandler = new MineHandler();
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView backTx = (TextView) findViewById(R.id.base_title_back);
        TextView titleNameTx = (TextView) findViewById(R.id.base_title_name);
        titleNameTx.setText("单位信息");
        LinearLayout headLine = (LinearLayout) findViewById(R.id.org_info_head_line);
        mLogoSdv = (SimpleDraweeView) findViewById(R.id.org_info_logo);
        mOrgNameTx = (TextView) findViewById(R.id.org_info_name);
        mDancesTx = (TextView) findViewById(R.id.org_info_dances);
        mContactTx = (TextView) findViewById(R.id.org_info_contact);
        mDescTx = (TextView) findViewById(R.id.org_info_desc);
        mAttachTx = (TextView) findViewById(R.id.org_info_attachment);
        mAddressTx = (TextView) findViewById(R.id.org_info_address);
        ImageView moreImg = (ImageView) findViewById(R.id.org_info_more);

        backTx.setOnClickListener(this);
        if (modeType == Constants.Strings.EDIT_MODE) {
            headLine.setOnClickListener(this);
            mAddressTx.setOnClickListener(this);
            mOrgNameTx.setOnClickListener(this);
            mDancesTx.setOnClickListener(this);
            mContactTx.setOnClickListener(this);
            mDescTx.setOnClickListener(this);
            mAttachTx.setOnClickListener(this);
        } else if (modeType == Constants.Strings.NORMAL_MODE) {
            moreImg.setVisibility(View.GONE);
            mAddressTx.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mOrgNameTx.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mDancesTx.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mContactTx.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mDescTx.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mAttachTx.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            headLine.setOnClickListener(this);
        }
    }

    @NonNull
    @Override
    protected ImageView getPreviewImg() {
        return mLogoSdv;
    }

    @Override
    protected void uploadResult(@Nullable String imageUrl) {
        mInfoTemp.setPhotoUrl(imageUrl);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        MineHandler.Status.orgId = getIntent().getIntExtra(Constants.Strings.ORG_ID, -1);
        mHandler.addCallback(MineHandler.CHILD_HOST, this);
        mHandler.sendEmptyMessage(R.id.msg_mine_get_info_detail);
    }

    private void loadDataToWidget(UserInfoDetail userInfoDetail) {
        mLogoSdv.setImageURI(Uri.parse(userInfoDetail.getPhotoUrl()));
        mOrgNameTx.setText(userInfoDetail.getNickName());
        mDancesTx.setText(StringUtils.getNoNullStringWithTip(SimpleItemDataUtils.listNameToString(userInfoDetail.getDances())));
        mAddressTx.setText(StringUtils.getNoBlankString(userInfoDetail.getOrgAddress()));
        mAddressTx.setTag(StringUtils.getNoNullStringWithTip(userInfoDetail.getOrgAddress()));
        mContactTx.setText(StringUtils.getNoNullStringWithTip(TextUtils.isEmpty(userInfoDetail.getContactWay()) ? userInfoDetail.getTelephone() : userInfoDetail.getContactWay()));
        mDescTx.setText(StringUtils.getNoNullStringWithTip(userInfoDetail.getOrgIntro()));
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.base_title_back:
                finish();
                break;
            case R.id.org_info_head_line:
                if (modeType == Constants.Strings.EDIT_MODE) {
                    if (mCameraDialog == null) {
                        mCameraDialog = new CameraDialog(this, CameraDialog.Type.IMAGE);
                    }
                    mCameraImageName = "Org" + MineHandler.Status.orgId + "_IMG_" + StringUtils.formatCurrentDateTime() + ".jpg";
                    mCameraDialog.setImageName(mCameraImageName);
                    mCameraDialog.show();
                } else {
                    PicturesActivity.start(this, null, mInfoDetail.getPhotoUrl(), 0);
                }
                break;
            case R.id.org_info_name:
                intent = new Intent(this, SimpleInputActivity.class);
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_TILE, "机构名称");
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_PREVIOUS, mOrgNameTx.getText().toString());
                startActivityForResult(intent, Constants.Ints.REQUEST_CODE_ORGNAME);
                break;
            case R.id.org_info_dances:
                if (mDanceDia == null) {
                    mDanceDia = new ChoiceModeDialog(this, this, true);
                }
                mDanceDia.setCheckedDanceType(SimpleItemDataUtils.stringToListName(mDancesTx.getText().toString()));
                mDanceDia.show();
                break;
            case R.id.org_info_contact:
                intent = new Intent(this, SimpleInputActivity.class);
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_TILE, "联系方式");
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_PREVIOUS, mContactTx.getText().toString());
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_TYPE, InputType.TYPE_CLASS_PHONE);
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_LIMIT, 11);
                startActivityForResult(intent, Constants.Ints.REQUEST_CODE_CONTACT);
                break;
            case R.id.org_info_address:
                intent = new Intent(this, AddressChooseActivity.class);
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_PREVIOUS, mAddressTx.getTag().toString());
                startActivityForResult(intent, Constants.Ints.REQUEST_CODE_ADDRESS);
                break;
            case R.id.org_info_desc:
                intent = new Intent(this, SimpleInputActivity.class);
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_TILE, "机构简介");
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_PREVIOUS, mDescTx.getText().toString());
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_LINE, 6);
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_LIMIT, 120);
                startActivityForResult(intent, Constants.Ints.REQUEST_CODE_DESC);
                break;
            case R.id.org_info_attachment:
                intent = new Intent(this, OrgAttachActivity.class);
                intent.putExtra(Constants.Strings.ORG_ATTACH, mInfoDetail.getAttachments().get(0));
                intent.putExtra(Constants.Strings.ORG_ID, MineHandler.Status.orgId);
                intent.putExtra(Constants.Strings.USER_ID, userId);
                startActivityForResult(intent, Constants.Ints.REQUEST_CODE_ATTACH);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.Ints.REQUEST_CODE_ORGNAME:
                    mOrgNameTx.setText(data.getStringExtra(Constants.Strings.SIMPLE_INPUT_RESULT));
                    break;
                case Constants.Ints.REQUEST_CODE_CONTACT:
                    mContactTx.setText(data.getStringExtra(Constants.Strings.SIMPLE_INPUT_RESULT));
                    break;
                case Constants.Ints.REQUEST_CODE_ADDRESS:
                    LogUtil.w(TAG, data.getStringExtra(Constants.Strings.SIMPLE_INPUT_RESULT));
                    mAddressTx.setText(StringUtils.getNoBlankString(data.getStringExtra(Constants.Strings.SIMPLE_INPUT_RESULT)));
                    mAddressTx.setTag(data.getStringExtra(Constants.Strings.SIMPLE_INPUT_RESULT));
                    break;
                case Constants.Ints.REQUEST_CODE_DESC:
                    mDescTx.setText(data.getStringExtra(Constants.Strings.SIMPLE_INPUT_RESULT));
                    break;
                case Constants.Ints.REQUEST_CODE_ATTACH:
                    String attachUrl = data.getStringExtra(Constants.Strings.ORG_ATTACH);
                    List<String> attachUrls = new ArrayList<>();
                    attachUrls.add(attachUrl);
                    mInfoDetail.setAttachments(attachUrls);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void finish() {
        if (photoUploading) {
            Toast.makeText(this, "正在上传标志,请稍候", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (modeType == Constants.Strings.EDIT_MODE) {
                update();
            }
        }
        if (mInfoTemp != null && mInfoDetail != null) {
            if (mInfoTemp.equals(mInfoDetail)) {
                Intent intent = new Intent();
                intent.putExtra(Constants.Strings.ORG_LOGO, mInfoTemp.getPhotoUrl());
                intent.putExtra(Constants.Strings.ORG_NAME, mInfoTemp.getNickName());
                setResult(RESULT_OK, intent);
            }
        }
        super.finish();
    }

    private boolean update() {
        String orgName = mOrgNameTx.getText().toString();
        if (TextUtils.isEmpty(orgName)) {
            Toast.makeText(this, "机构名称不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        mInfoTemp.setNickName(orgName);

        String dances = mDancesTx.getText().toString();
        if (TextUtils.isEmpty(dances)) {
            Toast.makeText(this, "舞种不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        mInfoTemp.setContactWay(mContactTx.getText().toString());
        mInfoTemp.setOrgIntro(mDescTx.getText().toString());
        mInfoTemp.setOrgAddress((String) mAddressTx.getTag());
        mInfoTemp.setAttachments(mInfoDetail.getAttachments());
        Message msg = mHandler.obtainMessage(R.id.msg_org_update_info);
        msg.obj = mInfoTemp;
        mHandler.sendMessage(msg);
        return true;
    }

    @Override
    public void onItemChecked(List<SimpleItemData> checkedItems) {
        mDancesTx.setText(SimpleItemDataUtils.listNameToString(checkedItems));
        mInfoTemp.setDances(checkedItems);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.msg_mine_info_detail_got:
                if (msg.obj != null) {
                    UserInfoDetail userInfoDetail = (UserInfoDetail) msg.obj;
                    if (userInfoDetail != null && userInfoDetail.getResponse().getCode() == SimpleResponse.SUCCESS) {
                        mInfoDetail = userInfoDetail;
                        mInfoTemp = userInfoDetail;
                        loadDataToWidget(userInfoDetail);
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }
}
