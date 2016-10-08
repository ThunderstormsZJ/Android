package cn.spinsoft.wdq.video;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.mine.biz.PublishVideoBean;
import cn.spinsoft.wdq.service.LocationOnMain;
import cn.spinsoft.wdq.service.PublishOnMain;
import cn.spinsoft.wdq.utils.BitmapUtils;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.FileUtils;
import cn.spinsoft.wdq.utils.SimpleItemDataUtils;
import cn.spinsoft.wdq.widget.ChoiceModeDialog;

/**
 * Created by zhoujun on 15/12/14.
 */
public class VideoPublishActivity extends BaseActivity implements View.OnClickListener, ChoiceModeDialog.OnItemCheckedListener {
    private static final String TAG = VideoPublishActivity.class.getSimpleName();

    private TextView mCancelTx, mUpLoadTx;
    private EditText mTitleTx;
    private ImageView mPreViewImg;
    private TextView mTimeLengthTx, mSizeLengthTx;
    private TextView /*mLocationTx,*/ mDateTx;
    private TextView mDanceTypeTx;
    private CheckBox mOriginalCb;

    private String videoPath;
    private int watcherUserId = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish_video;
    }

    @Override
    protected void initHandler() {
        Intent intent = getIntent();
        videoPath = intent.getStringExtra(Constants.Strings.VIDEO_PATH);
        watcherUserId = intent.getIntExtra(Constants.Strings.USER_ID, -1);
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        mCancelTx = (TextView) findViewById(R.id.video_publish_cancel);
        mUpLoadTx = (TextView) findViewById(R.id.video_publish_upload);
        mTitleTx = (EditText) findViewById(R.id.video_publish_title);
        mPreViewImg = (ImageView) findViewById(R.id.video_publish_preview);
        mTimeLengthTx = (TextView) findViewById(R.id.video_publish_timeLength);
        mSizeLengthTx = (TextView) findViewById(R.id.video_publish_sizeLength);
//        mLocationTx = (TextView) findViewById(R.id.video_publish_location);
        mDateTx = (TextView) findViewById(R.id.video_publish_date);
        mDanceTypeTx = (TextView) findViewById(R.id.video_publish_danceType);
        mOriginalCb = (CheckBox) findViewById(R.id.video_publish_original);

        mCancelTx.setOnClickListener(this);
        mUpLoadTx.setOnClickListener(this);
        mDanceTypeTx.setOnClickListener(this);
        mOriginalCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mOriginalCb.setTag(1);
                } else {
                    mOriginalCb.setTag(0);
                }
            }
        });
        mOriginalCb.setTag(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_publish_cancel:
                finish();
                break;
            case R.id.video_publish_upload:
                String title = mTitleTx.getText().toString();
                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(this, "请输入视频的标题", Toast.LENGTH_SHORT).show();
                } else {
                    PublishVideoBean videoBean = new PublishVideoBean();
                    videoBean.setTitle(title);
                    videoBean.setVideoUrl(videoPath);
                    videoBean.setUserId(watcherUserId);
                    videoBean.setLocation(LocationOnMain.getInstance().getAddress());
                    if (mDanceTypeTx.getTag() != null) {
                        videoBean.setDanceType((Integer) mDanceTypeTx.getTag());
                    }
                    videoBean.setOriginal((Integer) mOriginalCb.getTag());
                    double[] location = LocationOnMain.getInstance().getLocation();
                    videoBean.setLongitude(location[0]);
                    videoBean.setLatitude(location[1]);
//                    Intent intent = new Intent();
//                    intent.putExtra(Constants.Strings.VIDEO_INFO, (Parcelable) videoBean);
//                    setResult(RESULT_OK, intent);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(Constants.Strings.PUBLISH_VIDEO, videoBean);
                    PublishOnMain.getInstance().prepareToPublish(bundle);
                    Toast.makeText(this, "正在后台上传并发表视频,请稍后……", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case R.id.video_publish_danceType:
                ChoiceModeDialog mDanceDia = new ChoiceModeDialog(this, this, false);
                mDanceDia.setCheckedDanceType(SimpleItemDataUtils.stringToListName(mDanceTypeTx.getText().toString()));
                mDanceDia.show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mPreViewImg.setImageBitmap(BitmapUtils.getMediaFrame(videoPath));
        mTimeLengthTx.setText(FileUtils.getVideoTimeLength(videoPath));
        mSizeLengthTx.setText(FileUtils.getVideoSizeLength(videoPath));
        mTitleTx.setText(FileUtils.getVideoNameNoPostFix(videoPath));
//        mLocationTx.setText(LocationOnMain.getInstance().getAddress());
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        mDateTx.setText(myFmt.format(new Date()));
    }

    @Override
    public void onItemChecked(List<SimpleItemData> checkedItems) {
        mDanceTypeTx.setText(SimpleItemDataUtils.listNameToString(checkedItems));
        mDanceTypeTx.setTag(checkedItems.get(0).getId());
    }
}
