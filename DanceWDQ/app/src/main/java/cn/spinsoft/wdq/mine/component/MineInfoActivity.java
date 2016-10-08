package cn.spinsoft.wdq.mine.component;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.album.PicturesActivity;
import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.enums.AgeRange;
import cn.spinsoft.wdq.enums.Sex;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.mine.biz.MineHandler;
import cn.spinsoft.wdq.mine.biz.UserInfoDetail;
import cn.spinsoft.wdq.mine.widget.SingleChoiceDialog;
import cn.spinsoft.wdq.mine.widget.ViewSwitcher;
import cn.spinsoft.wdq.utils.CheckPermissionUtils;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.utils.SimpleItemDataUtils;
import cn.spinsoft.wdq.utils.StringUtils;
import cn.spinsoft.wdq.widget.CameraDialog;
import cn.spinsoft.wdq.widget.ChoiceModeDialog;

/**
 * Created by zhoujun on 15/12/4.
 */
public class MineInfoActivity extends ImageChooseClipActivity implements Handler.Callback, View.OnClickListener,
        SingleChoiceDialog.OnInfoChooseListener, ViewSwitcher.OnInputConfirmListener,
        ChoiceModeDialog.OnItemCheckedListener {
    private static final String TAG = MineInfoActivity.class.getSimpleName();
    public static String modeType = Constants.Strings.EDIT_MODE;

    private SimpleDraweeView mPhotoImg;
    private TextView mNameVs, mSignatureVs, mPhoneVs, mTitleTx;
    private TextView mTallTx, mAgeTx, mSexTx, mDanceTx, mVisibleTx;

    private UserInfoDetail mInfoDetail, mInfoTemp;
    private SingleChoiceDialog mInfoDia;
    private ChoiceModeDialog mDanceDia;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine_info;
    }

    @Override
    protected void initHandler() {
        mHandler = new MineHandler();
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView mBackTx = (TextView) findViewById(R.id.base_title_back);
        mTitleTx = (TextView) findViewById(R.id.base_title_name);
        LinearLayout mHeadLine = (LinearLayout) findViewById(R.id.mine_info_head_line);
        mPhotoImg = (SimpleDraweeView) findViewById(R.id.mine_info_photo);
        mNameVs = (TextView) findViewById(R.id.mine_info_name);
        mSignatureVs = (TextView) findViewById(R.id.mine_info_signature);
        mPhoneVs = (TextView) findViewById(R.id.mine_info_phone);
        mTallTx = (TextView) findViewById(R.id.mine_info_tall);
        mAgeTx = (TextView) findViewById(R.id.mine_info_age);
        mSexTx = (TextView) findViewById(R.id.mine_info_sex);
        mDanceTx = (TextView) findViewById(R.id.mine_info_dance);
        mVisibleTx = (TextView) findViewById(R.id.mine_info_visible);
        ImageView moreImg = (ImageView) findViewById(R.id.mine_info_more);

        mBackTx.setOnClickListener(this);
        mHeadLine.setOnClickListener(this);
        if (modeType == Constants.Strings.EDIT_MODE) {
            mTallTx.setOnClickListener(this);
            mAgeTx.setOnClickListener(this);
            mSexTx.setOnClickListener(this);
            mDanceTx.setOnClickListener(this);
            mVisibleTx.setOnClickListener(this);

            mNameVs.setOnClickListener(this);
            mSignatureVs.setOnClickListener(this);
            mPhoneVs.setOnClickListener(this);
        } else if (modeType == Constants.Strings.NORMAL_MODE) {
            moreImg.setVisibility(View.GONE);
            mNameVs.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mSignatureVs.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mTallTx.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mAgeTx.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mSexTx.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mDanceTx.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mPhoneVs.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mVisibleTx.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mPhoneVs.setOnClickListener(this);
        }

        mInfoDia = new SingleChoiceDialog(this, this);
        mDanceDia = new ChoiceModeDialog(this, this, true);

        mHandler.addCallback(MineHandler.CHILD_HOST, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserLogin userLogin = SharedPreferencesUtil.getInstance(this).getLoginUser();
        if (userLogin != null) {
            MineHandler.watcherUserId = userLogin.getUserId();
            mTitleTx.setText("用户信息");
        }
        MineHandler.Status.userId = getIntent().getIntExtra(Constants.Strings.USER_ID, -1);
        mHandler.sendEmptyMessage(R.id.msg_mine_get_info_detail);//加载基本数据
    }

    @NonNull
    @Override
    protected ImageView getPreviewImg() {
        return mPhotoImg;
    }

    @Override
    protected void uploadResult(@Nullable String imageUrl) {
        mInfoTemp.setPhotoUrl(imageUrl);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == R.id.msg_mine_info_detail_got) {
            mInfoTemp = new UserInfoDetail();
            if (msg.obj != null) {
                mInfoDetail = (UserInfoDetail) msg.obj;
                userId = mInfoDetail.getUserId();
                mInfoTemp.copy(mInfoDetail);
                loadUserDetail(mInfoDetail);
            }
        } else if (msg.what == R.id.msg_mine_info_updated) {
            if (msg.obj != null) {
                SimpleResponse response = (SimpleResponse) msg.obj;
                if (response.getCode() == SimpleResponse.SUCCESS) {
                    super.finish();
                }
            }
        }
        return true;
    }

    private void loadUserDetail(UserInfoDetail infoDetail) {
        if (infoDetail == null) {
            return;
        }
        mPhotoImg.setImageURI(Uri.parse(infoDetail.getPhotoUrl()));
        mNameVs.setText(infoDetail.getNickName());
        mSignatureVs.setText(StringUtils.getNoNullStringWithTip(infoDetail.getSignature()));
        mTallTx.setText(infoDetail.getTall() == "null" ? "未填写" : infoDetail.getTall() + "cm");
        mAgeTx.setText(StringUtils.getNoNullStringWithTip(infoDetail.getAgeRange().getName()));
        mSexTx.setText(infoDetail.getSex().getName());
        mPhoneVs.setText(StringUtils.getNoNullStringWithTip(infoDetail.getTelephone()));
        mDanceTx.setText(StringUtils.getNoNullStringWithTip(SimpleItemDataUtils.listNameToString(infoDetail.getDances())));
        mVisibleTx.setText(infoDetail.isVisible() ? "是" : "否");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.Ints.REQUEST_CODE_NICKNAME:
                    String name = data.getStringExtra(Constants.Strings.SIMPLE_INPUT_RESULT);
                    mNameVs.setText(name);
                    mInfoTemp.setNickName(name);
                    break;
                case Constants.Ints.REQUEST_CODE_SIGNATURE:
                    String signature = data.getStringExtra(Constants.Strings.SIMPLE_INPUT_RESULT);
                    mSignatureVs.setText(signature);
                    mInfoTemp.setSignature(signature);
                    break;
                case Constants.Ints.REQUEST_CODE_CONTACT:
                    String phone = data.getStringExtra(Constants.Strings.SIMPLE_INPUT_RESULT);
                    mPhoneVs.setText(phone);
                    mInfoTemp.setContactWay(phone);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void finish() {
        if (photoUploading) {
            Toast.makeText(this, "正在上传头像,请稍候", Toast.LENGTH_SHORT).show();
            return;
        } else {
            update();
        }
        if (mInfoDetail != null && mInfoTemp != null) {
            if (!mInfoTemp.equals(mInfoDetail)) {
                Intent intent = new Intent();
                intent.putExtra(Constants.Strings.USER_NAME, mInfoTemp.getNickName());
                intent.putExtra(Constants.Strings.USER_SIGN, mInfoTemp.getSignature());
                intent.putExtra(Constants.Strings.USER_SEX, mInfoTemp.getSex().getValue());
                intent.putExtra(Constants.Strings.USER_PHOTO, mInfoTemp.getPhotoUrl());
                setResult(RESULT_OK, intent);
            }
        }
        super.finish();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.base_title_back:
                finish();
                break;
            case R.id.mine_info_head_line:
                if (modeType == Constants.Strings.EDIT_MODE) {
                    boolean isCheck = CheckPermissionUtils.checkCameraAndRWPermission(this);
                    if (isCheck) {
                        if (mCameraDialog == null) {
                            mCameraDialog = new CameraDialog(this, CameraDialog.Type.IMAGE);
                        }
                        mCameraImageName = "User" + mInfoDetail.getUserId() + "_IMG_" + StringUtils.formatCurrentDateTime() + ".jpg";
                        mCameraDialog.setImageName(mCameraImageName);
                        mCameraDialog.show();
                    }
                } else {
                    PicturesActivity.start(this, null, mInfoDetail.getPhotoUrl(), 0);
                }
                break;
            case R.id.mine_info_tall:
                mInfoDia.setPreInfoData(mTallTx.getText().toString());
                mInfoDia.show(R.id.mine_info_tall, SimpleItemDataUtils.getTalls());
                break;
            case R.id.mine_info_age:
                mInfoDia.setPreInfoData(mAgeTx.getText().toString());
                mInfoDia.show(R.id.mine_info_age, SimpleItemDataUtils.getAgeRanges(false));
                break;
            case R.id.mine_info_sex:
                mInfoDia.setPreInfoData(mSexTx.getText().toString());
                mInfoDia.show(R.id.mine_info_sex, SimpleItemDataUtils.getSexes(false));
                break;
            case R.id.mine_info_dance:
                mDanceDia.setCheckedDanceType(SimpleItemDataUtils.stringToListName(mDanceTx.getText().toString()));
                mDanceDia.show();
                break;
            case R.id.mine_info_visible:
                mInfoDia.show(R.id.mine_info_visible, SimpleItemDataUtils.getVisibles());
                break;
            case R.id.mine_info_name:
                intent = new Intent(this, SimpleInputActivity.class);
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_TILE, "昵称");
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_PREVIOUS, mNameVs.getText().toString());
                startActivityForResult(intent, Constants.Ints.REQUEST_CODE_NICKNAME);
                break;
            case R.id.mine_info_signature:
                intent = new Intent(this, SimpleInputActivity.class);
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_TILE, "个性签名");
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_PREVIOUS, mSignatureVs.getText().toString());
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_LINE, 6);
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_LIMIT, 50);
                startActivityForResult(intent, Constants.Ints.REQUEST_CODE_SIGNATURE);
                break;
            case R.id.mine_info_phone:
                intent = new Intent(this, SimpleInputActivity.class);
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_TILE, "联系方式");
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_PREVIOUS, mPhoneVs.getText().toString());
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_TYPE, InputType.TYPE_CLASS_PHONE);
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_LIMIT, 11);
                startActivityForResult(intent, Constants.Ints.REQUEST_CODE_CONTACT);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isCameraOPen = false;
        boolean isRWStorage = false;
        if (requestCode == Constants.Ints.PERMISSION_CAMERA) {
            for (int index = 0; index < permissions.length; index++) {
                switch (permissions[index]) {
                    case Manifest.permission.CAMERA:
                        if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                            isCameraOPen = true;
                        } else if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                            Toast.makeText(this, "您拒绝了拍照权限，不能使用此功能！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "应用没有拍照权限，不能使用此功能！", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    case Manifest.permission.READ_EXTERNAL_STORAGE:
                        if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                            isRWStorage = true;
                        } else if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                            Toast.makeText(this, "您拒绝了SD卡读写权限，不能使用此功能！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "应用没有SD卡读写权限，不能使用此功能！", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        //显示选择框
        if (isCameraOPen && isRWStorage) {
            if (mCameraDialog == null) {
                mCameraDialog = new CameraDialog(this, CameraDialog.Type.IMAGE);
            }
            mCameraImageName = "User" + mInfoDetail.getUserId() + "_IMG_" + StringUtils.formatCurrentDateTime() + ".jpg";
            mCameraDialog.setImageName(mCameraImageName);
            mCameraDialog.show();
        }
    }

    @Override
    public void onInfoChoosed(int where, SimpleItemData itemData) {
        LogUtil.w(TAG, "onInfoChoosed:" + itemData);
        if (itemData == null || mInfoDetail == null) {
            return;
        }
        switch (where) {
            case R.id.mine_info_tall:
                mTallTx.setText(itemData.getName());
                mInfoTemp.setTall(String.valueOf(itemData.getId()));
                break;
            case R.id.mine_info_age:
                mAgeTx.setText(itemData.getName());
                mInfoTemp.setAgeRange(AgeRange.getEnum(itemData.getId()));
                break;
            case R.id.mine_info_sex:
                mSexTx.setText(itemData.getName());
                mInfoTemp.setSex(Sex.getEnum(itemData.getId()));
                break;
            case R.id.mine_info_visible:
                mVisibleTx.setText(itemData.getName());
                mInfoTemp.setVisible(itemData.getId() == 1);
                break;
            case R.id.mine_info_dance:
//                mDanceTx.setText(itemData.getNickName());
//                mInfoTemp.setDanceIds(String.valueOf(itemData.getId()));
                break;
            default:
                break;
        }
//        update();
    }

    @Override
    public void onItemChecked(List<SimpleItemData> checkedItems) {
        mDanceTx.setText(SimpleItemDataUtils.listNameToString(checkedItems));
        mInfoTemp.setDances(checkedItems);
    }

    private void update() {
        if (mInfoDetail != null && mInfoTemp != null) {
            if (!mInfoTemp.equals(mInfoDetail)) {

                UserLogin userLogin = SharedPreferencesUtil.getInstance(this).getLoginUser();
                if (userLogin != null) {//更新本地已登录的用户信息
                    userLogin.setNickName(mInfoTemp.getNickName());
                    userLogin.setSignature(mInfoTemp.getSignature());
                    userLogin.setMobile(mInfoTemp.getContactWay());
                    userLogin.setPhotoUrl(mInfoTemp.getPhotoUrl());
                    SharedPreferencesUtil.getInstance(this).saveLoginUser(userLogin);
                }

                Message msg = mHandler.obtainMessage(R.id.msg_mine_update_info);
                msg.obj = mInfoTemp;
                mHandler.sendMessage(msg);
            } else {
//                Toast.makeText(this, "您未修改任何信息", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void inputConfirm(ViewSwitcher switcher) {
        switch (switcher.getId()) {
            case R.id.mine_info_name:
                mInfoTemp.setNickName(mNameVs.getText().toString());
                break;
            case R.id.mine_info_signature:
                mInfoTemp.setSignature(mSignatureVs.getText().toString());
                break;
            case R.id.mine_info_phone:
                mInfoTemp.setContactWay(mPhoneVs.getText().toString());
                break;
            default:
                break;
        }
//        update();
    }
}
