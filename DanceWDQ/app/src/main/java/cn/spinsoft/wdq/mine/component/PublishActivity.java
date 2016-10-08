package cn.spinsoft.wdq.mine.component;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.enums.DiscoverType;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.mine.biz.PublishInfoBean;
import cn.spinsoft.wdq.mine.widget.ChooseTimeDia;
import cn.spinsoft.wdq.mine.widget.PublishImageAdapter;
import cn.spinsoft.wdq.mine.widget.SingleChoiceDialog;
import cn.spinsoft.wdq.service.PublishOnMain;
import cn.spinsoft.wdq.utils.BitmapUtils;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.ContentResolverUtil;
import cn.spinsoft.wdq.utils.FileUtils;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.utils.SimpleItemDataUtils;
import cn.spinsoft.wdq.utils.StartActivityUtils;
import cn.spinsoft.wdq.utils.StringUtils;
import cn.spinsoft.wdq.widget.CameraDialog;
import cn.spinsoft.wdq.widget.ChoiceModeDialog;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by zhoujun on 15/12/17.
 */
public class PublishActivity extends BaseActivity implements View.OnClickListener,
        RecyclerItemClickListener, ChooseTimeDia.OnTimeChooseListener, SingleChoiceDialog.OnInfoChooseListener,
        ChoiceModeDialog.OnItemCheckedListener {
    private static final String TAG = PublishActivity.class.getSimpleName();
    private TextView mCancelTx, mTypeNameTx, mSendTx, mDancesTx;
    private EditText mTitleEt /*mDescEt*/;
    private RecyclerView mImagesRv;

    private TextView mAddressNameTx, mDescNameTx, mDescTx, mWordDescTx, mWorkLocationTx;
    private EditText mOrgEt/*, mAddressEt*//*, mOrgDescEt*/;
    private TextView mAddressTx;
    private TextView mTimeAppEndTx, mTimeStartTx, mTimeEndTx, mSalaryTx, mPosImgTx, mVideoTx;
    private ImageView mPosImgIv, mVideoImgIv;
    private ImageButton mPosImgDelBtn, mVideoDelBtn;

    private DiscoverType mPublishType = DiscoverType.ACTIVITY;
    private PublishImageAdapter imageAdapter;
    private int watcherUserId;
    private int imagePosition;
    private String imageType = Constants.Strings.CHOOSE_IMG_NORMAL;

    private CameraDialog cameraDialog;
    private String mCameraImageName = "imageName.jpg";
    private String mImgTempPath = "";
    private ChooseTimeDia timeDia;
    private ChoiceModeDialog danceTypeDia;
    private SingleChoiceDialog infoDia;

    @Override
    protected int getLayoutId() {
        if (mPublishType == DiscoverType.ACTIVITY) {//活动
            return R.layout.activity_publish;
        } else if (mPublishType == DiscoverType.CONTEST) {//比赛
            return R.layout.activity_publish_contest;
        } else if (mPublishType == DiscoverType.RECRUIT) {//招聘
            return R.layout.activity_publish_recruit;
        } else {
            return R.layout.activity_publish_simple;
        }
    }

    @Override
    protected void initHandler() {
        mPublishType = DiscoverType.getEnum(getIntent().getIntExtra(Constants.Strings.DISCOVER_TYPE,
                DiscoverType.ACTIVITY.getValue()));

        UserLogin userInfo = SharedPreferencesUtil.getInstance(this).getLoginUser();
        if (userInfo != null) {
            watcherUserId = userInfo.getUserId();
        }
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        mCancelTx = (TextView) findViewById(R.id.base_title_back);
        mTypeNameTx = (TextView) findViewById(R.id.base_title_name);
        mSendTx = (TextView) findViewById(R.id.base_title_otherfunction);
        mSendTx.setText("发表");
        mTitleEt = (EditText) findViewById(R.id.publish_title);
        mDancesTx = (TextView) findViewById(R.id.publish_dances);
//        mDescEt = (EditText) findViewById(R.id.publish_desc);
        if (mPublishType != DiscoverType.RECRUIT) {
            mDescTx = (TextView) findViewById(R.id.publish_desc);
            mImagesRv = (RecyclerView) findViewById(R.id.publish_images);
            mImagesRv.setLayoutManager(new GridLayoutManager(this, 4));
            imageAdapter = new PublishImageAdapter(null, this);
            mImagesRv.setAdapter(imageAdapter);
            mDescTx.setOnClickListener(this);
        }

        mCancelTx.setOnClickListener(this);
        mSendTx.setOnClickListener(this);
        mDancesTx.setOnClickListener(this);

        matchType(mPublishType);
    }

    private void matchType(DiscoverType type) {
        mTypeNameTx.setText(type.getName());

        switch (type) {
            case ACTIVITY://活动
                mAddressNameTx = (TextView) findViewById(R.id.publish_where_name);
                mTimeStartTx = (TextView) findViewById(R.id.publish_time_start);
                mTimeEndTx = (TextView) findViewById(R.id.publish_time_end);
                mOrgEt = (EditText) findViewById(R.id.publish_organization);
                mAddressTx = (TextView) findViewById(R.id.publish_address);
                mDescNameTx = (TextView) findViewById(R.id.publish_desc_name);
                mDescTx.setTag("活动介绍");
                mAddressTx.setOnClickListener(this);
                mTimeStartTx.setOnClickListener(this);
                mTimeEndTx.setOnClickListener(this);

                mAddressNameTx.setText("活动地址");
                mDescNameTx.setText("活动介绍");
                break;
            case CONTEST://比赛
                mAddressNameTx = (TextView) findViewById(R.id.publish_where_name);
                mTimeAppEndTx = (TextView) findViewById(R.id.publish_time_appstart);
                mTimeStartTx = (TextView) findViewById(R.id.publish_time_start);
                mTimeEndTx = (TextView) findViewById(R.id.publish_time_end);
                mOrgEt = (EditText) findViewById(R.id.publish_organization);
                mAddressTx = (TextView) findViewById(R.id.publish_address);
                mDescNameTx = (TextView) findViewById(R.id.publish_desc_name);
                mPosImgTx = (TextView) findViewById(R.id.publish_pos);
                mPosImgIv = (ImageView) findViewById(R.id.publish_pos_choosed);
                mPosImgDelBtn = (ImageButton) findViewById(R.id.publish_pos_delete);
                mVideoTx = (TextView) findViewById(R.id.publish_video);
                mVideoImgIv = (ImageView) findViewById(R.id.publish_video_choosed);
                mVideoDelBtn = (ImageButton) findViewById(R.id.publish_video_delete);

                mAddressTx.setOnClickListener(this);
                mVideoTx.setOnClickListener(this);
                mVideoDelBtn.setOnClickListener(this);
                mPosImgTx.setOnClickListener(this);
                mTimeAppEndTx.setOnClickListener(this);
                mTimeStartTx.setOnClickListener(this);
                mTimeEndTx.setOnClickListener(this);
                mPosImgDelBtn.setOnClickListener(this);

                mAddressNameTx.setText("比赛地点");
                mDescNameTx.setText("比赛简介");
                break;
            case RECRUIT://招聘
                mSalaryTx = (TextView) findViewById(R.id.publish_salary);
                mWordDescTx = (TextView) findViewById(R.id.publish_work_desc);
                mWorkLocationTx = (TextView) findViewById(R.id.publish_word_location);

                mSalaryTx.setOnClickListener(this);
                mWordDescTx.setOnClickListener(this);
                mWorkLocationTx.setOnClickListener(this);
                break;
            case TOPIC://话题
                mTypeNameTx.setText("舞蹈话题");
                break;
            case OTHER://寻宝
                TableRow danceTypeTr = (TableRow) findViewById(R.id.publish_dance_type_tr);
                danceTypeTr.setVisibility(View.GONE);
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
                case Constants.Ints.REQUEST_CODE_IMAGE_CHOOSE:
                    String imagePath = ContentResolverUtil.getPath(this, data.getData());
                    imagePathSelected(imagePath);
                    break;
                case Constants.Ints.REQUEST_CODE_IMAGE_CAPTURE:
                    String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/"
                            + StringUtils.getApplicationName(this);
                    String path = dir + "/" + mCameraImageName;
                    imagePathSelected(path);
                    break;
                case Constants.Ints.REQUEST_CODE_IMAGE_CLIPPED:
                    onImageClipped(data);
                    break;
                case Constants.Ints.REQUEST_CODE_VIDEO_CAPTURE:
                case Constants.Ints.REQUEST_CODE_VIDEO_CHOOSE:
                    onVideoSelected(data);
                    break;
                case Constants.Ints.REQUEST_CODE_DESC:
                    String desc = data.getStringExtra(Constants.Strings.SIMPLE_INPUT_RESULT);
                    mDescTx.setText(desc);
                    break;
                case Constants.Ints.REQUEST_CODE_WORK_DESC:
                    String workDesc = data.getStringExtra(Constants.Strings.SIMPLE_INPUT_RESULT);
                    mWordDescTx.setText(workDesc);
                    break;
                case Constants.Ints.REQUEST_CODE_ADDRESS:
                    String workAddress = data.getStringExtra(Constants.Strings.SIMPLE_INPUT_RESULT);
                    if (mPublishType == DiscoverType.RECRUIT) {
                        mWorkLocationTx.setText(StringUtils.getNoBlankString(workAddress));
                        mWorkLocationTx.setTag(workAddress);
                    } else if (mPublishType == DiscoverType.ACTIVITY || mPublishType == DiscoverType.CONTEST) {
                        mAddressTx.setText(StringUtils.getNoBlankString(workAddress));
                        mAddressTx.setTag(workAddress);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void imagePathSelected(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            Toast.makeText(this, "照片选择路径为空", Toast.LENGTH_SHORT).show();
            return;
        }
        LogUtil.w(TAG, "onActivityResult:" + imagePath);
        if (imageType == Constants.Strings.CHOOSE_IMG_NORMAL) {
            imageAdapter.addAdapterDataEnd(imagePath);
        } else if (imageType == Constants.Strings.CHOOSE_IMG_POS) {
            mImgTempPath = FileUtils.createImgTempFile(mCameraImageName, this);
            StartActivityUtils.startPhotoZoom(Uri.fromFile(new File(imagePath)), 750, 360, this, Uri.fromFile(new File(mImgTempPath)));
        }
    }

    private void onImageClipped(Intent data) {
        if (data == null) {
            Toast.makeText(this, "裁剪图片失败,请重新裁剪", Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle extras = data.getExtras();
        if (extras == null) {
            Toast.makeText(this, "裁剪图片失败,请重新裁剪", Toast.LENGTH_SHORT).show();
        }
        LogUtil.w(TAG, "onImageClipped:" + mImgTempPath);
        mPosImgTx.setVisibility(View.GONE);
        mPosImgIv.setVisibility(View.VISIBLE);
        mPosImgDelBtn.setVisibility(View.VISIBLE);
        mPosImgIv.setTag(mImgTempPath);
        mPosImgIv.setImageURI(Uri.fromFile(new File(mImgTempPath)));
    }

    private void onVideoSelected(Intent data) {
        if (data == null) {
            Toast.makeText(this, "视频选择路径为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = data.getData();
        if (uri == null) {
            Toast.makeText(this, "视频选择路径为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String path = ContentResolverUtil.getPath(this, uri);
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(this, "视频选择路径为空", Toast.LENGTH_SHORT).show();
            return;
        }
        LogUtil.w(TAG, "onVideoSelected:" + path);
        mVideoImgIv.setTag(R.id.video_choose_path, path);
        mVideoTx.setVisibility(View.GONE);
        mVideoImgIv.setVisibility(View.VISIBLE);
        mVideoDelBtn.setVisibility(View.VISIBLE);
        mVideoImgIv.setImageBitmap(BitmapUtils.getMediaFrame(path));
        mVideoImgIv.setTag(R.id.video_choose_poster_path, BitmapUtils.getMediaFrame(path));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_title_back:
                finish();
                break;
            case R.id.base_title_otherfunction://发表
                checkInputAndPublish();
                break;
            case R.id.publish_pos://选择海报
                cameraDialog = new CameraDialog(this, CameraDialog.Type.IMAGE);
                mCameraImageName = "Pos_IMG_" + StringUtils.formatCurrentDateTime() + ".jpg";
                imageType = Constants.Strings.CHOOSE_IMG_POS;
                cameraDialog.setImageName(mCameraImageName);
                cameraDialog.show();
                break;
            case R.id.publish_video://选择视频
                cameraDialog = new CameraDialog(this, CameraDialog.Type.VIDEO);
                cameraDialog.show();
                break;
            case R.id.publish_time_appstart:
                chooseStartTime(R.id.publish_time_appstart);
                break;
            case R.id.publish_time_start:
                chooseStartTime(R.id.publish_time_start);
                break;
            case R.id.publish_time_end:
                if (TextUtils.isEmpty(mTimeStartTx.getText())) {
                    Toast.makeText(this, "请先选择开始时间", Toast.LENGTH_SHORT).show();
                } else {
                    timeDia.show(R.id.publish_time_end, mTimeStartTx.getText().toString());
                }
                break;
            case R.id.publish_dances:
                chooseDanceType();
                break;
            case R.id.publish_salary:
                chooseSalary();
                break;
            case R.id.publish_work_desc:
                Intent wdIntent = new Intent(this, SimpleInputActivity.class);
                wdIntent.putExtra(Constants.Strings.SIMPLE_INPUT_TILE, "职位描述");
                wdIntent.putExtra(Constants.Strings.SIMPLE_INPUT_PREVIOUS, mWordDescTx.getText().toString());
                wdIntent.putExtra(Constants.Strings.SIMPLE_INPUT_LINE, 6);
                wdIntent.putExtra(Constants.Strings.SIMPLE_INPUT_LIMIT, 50);
                startActivityForResult(wdIntent, Constants.Ints.REQUEST_CODE_WORK_DESC);
                break;
            case R.id.publish_word_location:
                Intent acIntent = new Intent(this, AddressChooseActivity.class);
                acIntent.putExtra(Constants.Strings.SIMPLE_INPUT_PREVIOUS, mWorkLocationTx.getTag() == null ? "" : mWorkLocationTx.getTag().toString());
                startActivityForResult(acIntent, Constants.Ints.REQUEST_CODE_ADDRESS);
                break;
            case R.id.publish_pos_delete://删除海报
                mPosImgTx.setVisibility(View.VISIBLE);
                mPosImgIv.setVisibility(View.GONE);
                mPosImgDelBtn.setVisibility(View.GONE);
                mPosImgIv.setTag("");
                break;
            case R.id.publish_video_delete://删除视频
                mVideoTx.setVisibility(View.VISIBLE);
                mVideoImgIv.setVisibility(View.GONE);
                mVideoDelBtn.setVisibility(View.GONE);
                mVideoImgIv.setTag("");
                break;
            case R.id.publish_desc://介绍
                Intent intent = new Intent(this, SimpleInputActivity.class);
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_TILE, mDescTx.getTag() == null ? "" : mDescTx.getTag().toString());
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_PREVIOUS, mDescTx.getText().toString());
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_LINE, 6);
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_LIMIT, 50);
                startActivityForResult(intent, Constants.Ints.REQUEST_CODE_DESC);
                break;
            case R.id.publish_address://选择地址
                Intent adIntent = new Intent(this, AddressChooseActivity.class);
                adIntent.putExtra(Constants.Strings.SIMPLE_INPUT_PREVIOUS, mAddressTx.getTag() == null ? "" : mAddressTx.getTag().toString());
                startActivityForResult(adIntent, Constants.Ints.REQUEST_CODE_ADDRESS);
                break;
            default:
                break;
        }
    }

    private void chooseStartTime(int where) {
        if (timeDia == null) {
            timeDia = new ChooseTimeDia(this, this);
        }
        timeDia.show(where, StringUtils.formatTime(System.currentTimeMillis()));
    }

    private void chooseDanceType() {
        if (danceTypeDia == null) {
            danceTypeDia = new ChoiceModeDialog(this, this);
            List<SimpleItemData> danceTypeList = SimpleItemDataUtils.getDanceTypeList(false, this);
            for (SimpleItemData daceTypes : danceTypeList) {
                if ("不限".equals(daceTypes.getName())) {
                    danceTypeList.remove(daceTypes);
                    break;
                }
            }
            danceTypeDia.setItemList(danceTypeList);
        }
        danceTypeDia.show();
    }

    private void chooseSalary() {
        if (infoDia == null) {
            infoDia = new SingleChoiceDialog(this, this);
        }
        infoDia.show(R.id.publish_salary, SimpleItemDataUtils.getSalaries());
    }


    private void checkInputAndPublish() {
        String title = mTitleEt.getText().toString();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
           /* Snackbar.make(mTitleEt, "标题不能为空", Snackbar.LENGTH_SHORT).setAction("填写标题", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTitleEt.requestFocus();
                    ContentResolverUtil.showIMM(mTitleEt);
                }
            }).show();*/
            return;
        }

        PublishInfoBean infoBean = new PublishInfoBean();
        infoBean.setUserId(watcherUserId);
        infoBean.setType(mPublishType);
        infoBean.setTitle(title);
//        LogUtil.w(TAG, mVideoImgIv.getTag(R.id.video_choose_path).toString());
//        LogUtil.w(TAG, mVideoImgIv.getTag(R.id.video_choose_poster_path).toString());
        if (mVideoImgIv != null && mVideoImgIv.getTag(R.id.video_choose_path) != null) {
            String videoPath = mVideoImgIv.getTag(R.id.video_choose_path).toString();
            if (videoPath != null && !TextUtils.isEmpty(videoPath)) {
                infoBean.setVideoUri(videoPath);
            }
        }
        if (mPosImgIv != null && mPosImgIv.getTag() != null) {
            infoBean.setPosSmallImg((String) mPosImgIv.getTag());
            infoBean.setPosBigImg((String) mPosImgIv.getTag());
        }
        if (mDancesTx.getTag() != null) {
            String danceType = String.valueOf(mDancesTx.getTag());
            infoBean.setDanceType(danceType);
        }
        if (mDescTx != null) {
            String desc = mDescTx.getText().toString();
            infoBean.setContent(desc);
        }
        if (inputCheckWithType(infoBean)) {
            return;
        }

        if (imageAdapter != null) {
            List<String> imagePaths = imageAdapter.getAllImagePaths();
            infoBean.setImages(imagePaths);
            infoBean.setSmallImg(imagePaths);
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.Strings.PUBLISH_INFO, infoBean);
        PublishOnMain.getInstance().prepareToPublish(bundle);
        Toast.makeText(this, "正在后台发表,请稍候……", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean inputCheckWithType(PublishInfoBean infoBean) {
        switch (mPublishType) {
            case ACTIVITY:
            case CONTEST:
                if (mPosImgIv != null) {
                    if (mPosImgIv.getTag() == null || TextUtils.isEmpty((String) mPosImgIv.getTag())) {
                        Toast.makeText(this, "请选择海报", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                String appEndTime = "";
                if (mTimeAppEndTx != null) {
                    appEndTime = mTimeAppEndTx.getText().toString();
                    if (TextUtils.isEmpty(appEndTime)) {
                        Toast.makeText(this, "报名截止时间不能为空", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                final String startTime = mTimeStartTx.getText().toString();
                if (TextUtils.isEmpty(startTime)) {
                    Toast.makeText(this, "开始时间不能为空", Toast.LENGTH_SHORT).show();
                    return true;
                }

                String endTime = mTimeEndTx.getText().toString();
                if (TextUtils.isEmpty(endTime)) {
                    Toast.makeText(this, "结束时间不能为空", Toast.LENGTH_SHORT).show();
                    return true;
                }

                String orgName = mOrgEt.getText().toString();
                if (TextUtils.isEmpty(orgName)) {
                    Toast.makeText(this, "单位不能为空", Toast.LENGTH_SHORT).show();
                    return true;
                }

//                String address = mAddressEt.getText().toString();
                String address = (String) mAddressTx.getTag();
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(this, "地址不能为空", Toast.LENGTH_SHORT).show();
                    return true;
                }

                infoBean.setAppEndTime(appEndTime);
                infoBean.setStartTime(startTime);
                infoBean.setEndTime(endTime);
                infoBean.setOrgName(orgName);
                infoBean.setLocation(address);
                break;
            case RECRUIT:
                if (TextUtils.isEmpty(infoBean.getDanceType())) {
                    Toast.makeText(this, "舞种不能为空", Toast.LENGTH_SHORT).show();
                    return true;
                }

                String salary = mSalaryTx.getText().toString();
                if (TextUtils.isEmpty(salary)) {
                    Toast.makeText(this, "请选择薪资水平", Toast.LENGTH_SHORT).show();
                    return true;
                }
                infoBean.setSalary(salary);

                String workDesc = mWordDescTx.getText().toString();
                if (TextUtils.isEmpty(workDesc)) {
                    Toast.makeText(this, "职位描述不能为空", Toast.LENGTH_SHORT).show();
                    return true;
                }
                infoBean.setContent(mWordDescTx.getText().toString());

                String workLocation = (String) mWorkLocationTx.getTag();
                if (workLocation == null || TextUtils.isEmpty(workLocation)) {
                    Toast.makeText(this, "请选择工作地址", Toast.LENGTH_SHORT).show();
                    return true;
                }
                infoBean.setLocation(workLocation);
               /* String orgNameRecruit = mOrgEt.getText().toString();
                if (TextUtils.isEmpty(orgNameRecruit)) {
                    Toast.makeText(this, "机构名称不能为空", Toast.LENGTH_SHORT).show();
                    return true;
                }
                infoBean.setOrgName(orgNameRecruit);*/


//                String orgIntro = mOrgDescEt.getText().toString();
//                infoBean.setOrgIntro(orgIntro);

                /*if (TextUtils.isEmpty(infoBean.getContent())) {
                    Toast.makeText(this, "职位描述不能为空", Toast.LENGTH_SHORT).show();
                    return true;
                }*/
                break;
            case TOPIC:
            case OTHER:

                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, int position) {
        if (adapter.getItemViewType(position) == PublishImageAdapter.TYPE_ADD) {
            cameraDialog = new CameraDialog(this, CameraDialog.Type.IMAGE);
            mCameraImageName = "User" + watcherUserId + "_IMG_" + StringUtils.formatCurrentDateTime() + ".jpg";
            imageType = Constants.Strings.CHOOSE_IMG_NORMAL;
            cameraDialog.setImageName(mCameraImageName);
            cameraDialog.show();
            imagePosition = position;
        } else {
            imageAdapter.delAdapterData(position);
        }
    }

    @Override
    public void onTimeChoose(int where, String timeString) {
        if (where == R.id.publish_time_end) {
            mTimeEndTx.setText(timeString);
            mTimeEndTx.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        } else if (where == R.id.publish_time_start) {
            mTimeStartTx.setText(timeString);
            mTimeStartTx.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        } else if (where == R.id.publish_time_appstart) {
            mTimeAppEndTx.setText(timeString);
            mTimeAppEndTx.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        }
    }

    @Override
    public void onInfoChoosed(int where, SimpleItemData itemData) {
        if (where == R.id.publish_dances) {
//            mDancesTx.setText(itemData.getNickName());
//            mDancesTx.setTag(itemData.getId());
//            mDancesTx.setGravity(Gravity.LEFT);
        } else if (where == R.id.publish_salary) {
            mSalaryTx.setText(itemData.getName());
//            mSalaryTx.setTag(itemData.getId());
//            mSalaryTx.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        }
    }

    @Override
    public void onItemChecked(List<SimpleItemData> checkedItems) {
        mDancesTx.setText(SimpleItemDataUtils.listNameToString(checkedItems));
        mDancesTx.setTag(SimpleItemDataUtils.listIdToString(checkedItems));
//        mDancesTx.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    }
}
