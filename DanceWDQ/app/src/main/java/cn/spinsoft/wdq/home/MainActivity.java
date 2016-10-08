//package cn.spinsoft.wdq.home;
//
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.facebook.drawee.backends.pipeline.Fresco;
//
//import java.util.Observable;
//import java.util.Observer;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.base.BaseActivity;
//import cn.spinsoft.wdq.browse.BrowseActivity;
//import cn.spinsoft.wdq.enums.PageType;
//import cn.spinsoft.wdq.login.LoginActivity;
//import cn.spinsoft.wdq.login.biz.UserLogin;
//import cn.spinsoft.wdq.service.LocationOnMain;
//import cn.spinsoft.wdq.service.LocationService;
//import cn.spinsoft.wdq.service.PublishOnMain;
//import cn.spinsoft.wdq.service.PublishService;
//import cn.spinsoft.wdq.utils.Constants;
//import cn.spinsoft.wdq.utils.ContentResolverUtil;
//import cn.spinsoft.wdq.utils.LogUtil;
//import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
//
//public class MainActivity extends BaseActivity implements View.OnClickListener, Observer {
//    private static final String TAG = MainActivity.class.getSimpleName();
//    private TextView mLoginRegisterTX;
//    private ImageView mVideosImg, mFriendsImg, mTrainingImg;
//
//    @Override
//    protected int getLayoutId() {
//        setTintRes(R.color.colorTitlePurple);
//        return R.layout.activity_main;
//    }
//
//    @Override
//    protected void initViewAndListener(Bundle savedInstanceState) {
//        mLoginRegisterTX = (TextView) findViewById(R.id.main_login_register);
//        mVideosImg = (ImageView) findViewById(R.id.main_videos);
//        mFriendsImg = (ImageView) findViewById(R.id.main_friends);
//        mTrainingImg = (ImageView) findViewById(R.id.main_training);
//
//        mLoginRegisterTX.setOnClickListener(this);
//        mVideosImg.setOnClickListener(this);
//        mFriendsImg.setOnClickListener(this);
//        mTrainingImg.setOnClickListener(this);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        Fresco.initialize(this.getApplicationContext());
//        super.onCreate(savedInstanceState);
//
//        Intent locationIntent = new Intent(this, LocationService.class);
//        bindService(locationIntent, LocationOnMain.getInstance().getServiceConnection(), Context.BIND_AUTO_CREATE);
//        startService(locationIntent);
//        LocationOnMain.getInstance().setUserLogin(SharedPreferencesUtil.getInstance(this).getLoginUser());
//
//        Intent publishIntent = new Intent(this, PublishService.class);
//        bindService(publishIntent, PublishOnMain.getInstance().getServiceConnection(), Context.BIND_AUTO_CREATE);
//        startService(publishIntent);
//
//        SharedPreferencesUtil.getInstance(this).addObserver(this);
//
//        LogUtil.i(TAG, "1px==>px:" + getResources().getDimensionPixelSize(R.dimen.test_dimen_px));
//        LogUtil.w(TAG, "1dp==>px:" + getResources().getDimensionPixelSize(R.dimen.test_dimen_dp));
//        LogUtil.e(TAG, "1sp==>px:" + getResources().getDimensionPixelSize(R.dimen.test_dimen_sp));
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        LogUtil.w(TAG, ContentResolverUtil.getDeviceIp());
//    }
//
//    @Override
//    public void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        UserLogin userLogin = SharedPreferencesUtil.getInstance(this).getLoginUser();
//        if (userLogin != null) {
//            mLoginRegisterTX.setVisibility(View.INVISIBLE);
//        } else {
//            mLoginRegisterTX.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        Fresco.shutDown();
//
//        LocationOnMain.getInstance().unRegisterCallback();
//        unbindService(LocationOnMain.getInstance().getServiceConnection());
//
//        PublishOnMain.getInstance().unRegisterCallback();
//        unbindService(PublishOnMain.getInstance().getServiceConnection());
//
//        SharedPreferencesUtil.getInstance(this).deleteObserver(this);
//        super.onDestroy();
//    }
//
//    @Override
//    public void onClick(View v) {
//        Intent intent = new Intent();
//        switch (v.getId()) {
//            case R.id.main_login_register:
//                intent.setClass(MainActivity.this, LoginActivity.class);
//                break;
//            case R.id.main_videos:
//                intent.setClass(MainActivity.this, BrowseActivity.class);
//                intent.putExtra(Constants.Strings.PAGE_TYPE, PageType.VIDEO.getValue());
//                break;
//            case R.id.main_friends:
//                intent.setClass(MainActivity.this, BrowseActivity.class);
//                intent.putExtra(Constants.Strings.PAGE_TYPE, PageType.FRIEND.getValue());
//                break;
//            case R.id.main_training:
//                intent.setClass(MainActivity.this, BrowseActivity.class);
//                intent.putExtra(Constants.Strings.PAGE_TYPE, PageType.ORG.getValue());
//                break;
//            default:
//                break;
//        }
//        try {
//            startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void update(Observable observable, Object data) {
//        if (data == null) {
//            mLoginRegisterTX.setVisibility(View.VISIBLE);
//        } else {
//            mLoginRegisterTX.setVisibility(View.INVISIBLE);
//        }
//    }
//}
