package cn.spinsoft.wdq.browse;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.Observable;
import java.util.Observer;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.browse.biz.BrowseHandler;
import cn.spinsoft.wdq.discover.frag.DiscoverListFrag;
import cn.spinsoft.wdq.enums.PageType;
import cn.spinsoft.wdq.login.LoginNewActivity;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.mine.component.MineBookingActivity;
import cn.spinsoft.wdq.mine.component.MineRelatedActivity;
import cn.spinsoft.wdq.mine.component.MineStartActivity;
import cn.spinsoft.wdq.mine.component.PrivateMsgListActivity;
import cn.spinsoft.wdq.mine.component.SimpleListActivity;
import cn.spinsoft.wdq.mine.component.WalletActivity;
import cn.spinsoft.wdq.mine.widget.PublishTypeChoosePop;
import cn.spinsoft.wdq.org.OrgDetailsActivity;
import cn.spinsoft.wdq.org.frag.OrgListFrag;
import cn.spinsoft.wdq.search.SearchActivity;
import cn.spinsoft.wdq.service.LocationOnMain;
import cn.spinsoft.wdq.service.LocationService;
import cn.spinsoft.wdq.service.PublishOnMain;
import cn.spinsoft.wdq.service.PublishService;
import cn.spinsoft.wdq.settings.AboutActivity;
import cn.spinsoft.wdq.settings.SettingsActivity;
import cn.spinsoft.wdq.user.UserDetailsActivity;
import cn.spinsoft.wdq.user.frag.FriendListFrag;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.ContentResolverUtil;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SecurityUtils;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.utils.session.SessionHelper;
import cn.spinsoft.wdq.video.VideoPublishActivity;
import cn.spinsoft.wdq.video.frag.VideoListFrag;
import cn.spinsoft.wdq.widget.CameraDialog;
import cn.spinsoft.wdq.widget.ConfirmDialog;
import cn.spinsoft.wdq.widget.MoreChoiceDialog;
import cn.spinsoft.wdq.widget.RadioGroup;

/**
 * Created by zhoujun on 16/1/22.
 */
public class BrowseNewActivity extends BaseActivity implements Observer, RadioGroup.OnCheckedChangeListener,
        View.OnClickListener, Handler.Callback {
    private static final String TAG = BrowseNewActivity.class.getSimpleName();

    private DrawerLayout mBrowseDrawer;
    //主布局部分
    private SimpleDraweeView mUserSdv;
    private TextView mTitleTx;
    private ImageView mAddImg, mSearchImg, mMoreImg;
    private RadioGroup mNavigationRg;
    private BaseFragment mVideoFrag, mFriendFrag, mOrgFrag, mDiscoverFrag;
    private PageType mCurrType = PageType.VIDEO;
    private PublishTypeChoosePop mTypeChoosePop;
    private CameraDialog mCameraDia;
    //侧滑布局部分
    private RelativeLayout mMenuHeadRl;
    private SimpleDraweeView mMenuUserSdv;
    private TextView mNickNameTx, mSignatureTx, mLoginHintTx;
    private TextView mMineStarTx, mRelatedMeTx, mWalletTx, mPrivateMsg;//公共项
    private TextView mTeacherAttest, mMineBookingTx;//个人项
    private TextView mOrderBookTx, mInviteTeacherTx, mAuthenticateTeacherTx;//机构项

    private UserLogin mUserLogin;

    public static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, BrowseNewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_browse_new;
    }

    @Override
    protected void initHandler() {
        mHandler = new BrowseHandler();
        mHandler.addCallback(BrowseHandler.CHILD_HOST, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this.getApplicationContext());
        super.onCreate(savedInstanceState);
        onParseIntent();

        //通知栏颜色改变
       /* if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            setTranslucentStatus(true);
            SystemBarTintManager barTintManager = new SystemBarTintManager(this);
            barTintManager.setStatusBarTintEnabled(true);
            barTintManager.setStatusBarTintResource(R.color.bg_page_title_transparent_purple);
        }*/

        checkPermissionAndStartLocationService();

        Intent publishIntent = new Intent(this, PublishService.class);
        bindService(publishIntent, PublishOnMain.getInstance().getServiceConnection(), Context.BIND_AUTO_CREATE);
        startService(publishIntent);

        SharedPreferencesUtil.getInstance(this).addObserver(this);

        mHandler.sendEmptyMessage(R.id.msg_get_app_version);
    }

    private void onParseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
            IMMessage message = (IMMessage) getIntent().getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
            switch (message.getSessionType()) {
                case P2P:
                    SessionHelper.startP2PSession(this, message.getSessionId());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        onParseIntent();
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        mBrowseDrawer = (DrawerLayout) findViewById(R.id.browse_drawer_root);
        mUserSdv = (SimpleDraweeView) findViewById(R.id.browse_user);
        mTitleTx = (TextView) findViewById(R.id.browse_title);
        mAddImg = (ImageView) findViewById(R.id.browse_add);
        mSearchImg = (ImageView) findViewById(R.id.browse_search);
        mMoreImg = (ImageView) findViewById(R.id.browse_more);
        mNavigationRg = (RadioGroup) findViewById(R.id.browse_navigation_root);
        mNavigationRg.setOnCheckedChangeListener(this);

        mUserSdv.setOnClickListener(this);
        mAddImg.setOnClickListener(this);
        mSearchImg.setOnClickListener(this);
        mMoreImg.setOnClickListener(this);

        mMenuHeadRl = (RelativeLayout) findViewById(R.id.browse_menu_head);
        mMenuUserSdv = (SimpleDraweeView) findViewById(R.id.browse_menu_user);
        mNickNameTx = (TextView) findViewById(R.id.browse_menu_nickName);
        mSignatureTx = (TextView) findViewById(R.id.browse_menu_signature);
        mLoginHintTx = (TextView) findViewById(R.id.browse_menu_loginHint);
//        mUserCenterTx = (TextView) findViewById(R.id.browse_menu_userCenter);
        mMineStarTx = (TextView) findViewById(R.id.browse_menu_mineStart);
        mRelatedMeTx = (TextView) findViewById(R.id.browse_menu_relatedMe);
        mWalletTx = (TextView) findViewById(R.id.browse_menu_wallet);
        mPrivateMsg = (TextView) findViewById(R.id.browse_menu_privateMsg);
        mTeacherAttest = (TextView) findViewById(R.id.browse_menu_teacherAttest);
        mMineBookingTx = (TextView) findViewById(R.id.browse_menu_mineBooking);
        TextView mSettingsTx = (TextView) findViewById(R.id.browse_menu_settings);
        TextView mAboutTx = (TextView) findViewById(R.id.browse_menu_about);
        //机构项
        mOrderBookTx = (TextView) findViewById(R.id.browse_menu_orderBook);
        mInviteTeacherTx = (TextView) findViewById(R.id.browse_menu_inviteTeacher);
        mAuthenticateTeacherTx = (TextView) findViewById(R.id.browse_menu_authenticateTeacher);

        mMenuHeadRl.setOnClickListener(this);
        mSettingsTx.setOnClickListener(this);
        mAboutTx.setOnClickListener(this);
//        mUserCenterTx.setOnClickListener(this);
        mMineStarTx.setOnClickListener(this);
        mRelatedMeTx.setOnClickListener(this);
        mWalletTx.setOnClickListener(this);
        mPrivateMsg.setOnClickListener(this);
        mTeacherAttest.setOnClickListener(this);
        mMineBookingTx.setOnClickListener(this);
        //机构
        mOrderBookTx.setOnClickListener(this);
        mInviteTeacherTx.setOnClickListener(this);
        mAuthenticateTeacherTx.setOnClickListener(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mUserLogin = SharedPreferencesUtil.getInstance(this).getLoginUser();
        loadLoginUser();
        mVideoFrag = (BaseFragment) changeContentFragment(R.id.browse_child_container, mVideoFrag,
                VideoListFrag.class.getName());
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.Ints.REQUEST_CODE_VIDEO_CAPTURE:
            case Constants.Ints.REQUEST_CODE_VIDEO_CHOOSE:
                onVideoSelected(data);
                break;
            case Constants.Ints.REQUEST_CODE_ITEM_STATUS:
                if (mVideoFrag != null) {
                    mVideoFrag.onActivityResult(requestCode, resultCode, data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
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

        LogUtil.w(TAG, "onActivityResult:" + path);
        Intent intent = new Intent(this, VideoPublishActivity.class);
        intent.putExtra(Constants.Strings.VIDEO_PATH, path);
        intent.putExtra(Constants.Strings.USER_ID, mUserLogin.getUserId());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        Fresco.shutDown();

        LocationOnMain.getInstance().unRegisterCallback();
        unbindService(LocationOnMain.getInstance().getServiceConnection());

        PublishOnMain.getInstance().unRegisterCallback();
        unbindService(PublishOnMain.getInstance().getServiceConnection());

        SharedPreferencesUtil.getInstance(this).deleteObserver(this);
        super.onDestroy();
    }

    @Override
    public void update(Observable observable, Object data) {
        mUserLogin = (UserLogin) data;
        loadLoginUser();
    }

    private void loadLoginUser() {
        if (mUserLogin != null) {
            int userId = mUserLogin.getUserId();
            int orgId = mUserLogin.getOrgId();
            BrowseHandler.watcherUserId = userId;
            BrowseHandler.Status.orgId = orgId;

            if (orgId <= 0) {//用户
                changeMenuShow(View.VISIBLE, View.GONE);
                mSignatureTx.setText(mUserLogin.getSignature());
            } else {//机构
                changeMenuShow(View.GONE, View.VISIBLE);
                mSignatureTx.setText(mUserLogin.getOrgIntro());
            }

            if (!TextUtils.isEmpty(mUserLogin.getPhotoUrl())) {
                mUserSdv.setImageURI(Uri.parse(mUserLogin.getPhotoUrl()));
                mMenuUserSdv.setImageURI(Uri.parse(mUserLogin.getPhotoUrl()));
            }
            mNickNameTx.setText(mUserLogin.getNickName());
            mLoginHintTx.setVisibility(View.INVISIBLE);
        } else {
            BrowseHandler.watcherUserId = -1;
            BrowseHandler.Status.orgId = -1;

            mUserSdv.setImageURI(Uri.parse("res://cn.spinsoft.wdq/" + R.mipmap.browse_user_default));
            mMenuUserSdv.setImageURI(Uri.parse("res://cn.spinsoft.wdq/" + R.mipmap.browse_user_default));
            mNickNameTx.setText("未登录");
            mSignatureTx.setText("");
            mLoginHintTx.setVisibility(View.VISIBLE);
            //默认隐藏机构菜单
            changeMenuShow(View.VISIBLE, View.GONE);
        }
    }

    //菜单的显示和隐藏操作
    private void changeMenuShow(int userMenu, int orgMenu) {
        mTeacherAttest.setVisibility(userMenu);
        mMineBookingTx.setVisibility(userMenu);
        //机构
        mOrderBookTx.setVisibility(orgMenu);
        mInviteTeacherTx.setVisibility(orgMenu);
        mAuthenticateTeacherTx.setVisibility(orgMenu);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.browse_navigation_video:
                mVideoFrag = (BaseFragment) changeContentFragment(R.id.browse_child_container,
                        mVideoFrag, VideoListFrag.class.getName());
                mCurrType = PageType.VIDEO;
                mAddImg.setVisibility(View.INVISIBLE);
                mSearchImg.setVisibility(View.VISIBLE);
                mMoreImg.setVisibility(View.GONE);
                break;
            case R.id.browse_navigation_friend:
                mFriendFrag = (BaseFragment) changeContentFragment(R.id.browse_child_container,
                        mFriendFrag, FriendListFrag.class.getName());
                mCurrType = PageType.FRIEND;
                mAddImg.setVisibility(View.INVISIBLE);
                mSearchImg.setVisibility(View.VISIBLE);
                mMoreImg.setVisibility(View.VISIBLE);
                break;
            case R.id.browse_navigation_camera:
                if (SecurityUtils.isUserValidity(this, mUserLogin)) {
                    checkPermissionAndShowCameraDia();
                }
                break;
            case R.id.browse_navigation_org:
                mOrgFrag = (BaseFragment) changeContentFragment(R.id.browse_child_container,
                        mOrgFrag, OrgListFrag.class.getName());
                mCurrType = PageType.ORG;
                mAddImg.setVisibility(View.INVISIBLE);
                mSearchImg.setVisibility(View.VISIBLE);
                mMoreImg.setVisibility(View.GONE);
                break;
            case R.id.browse_navigation_discover:
                mDiscoverFrag = (BaseFragment) changeContentFragment(R.id.browse_child_container,
                        mDiscoverFrag, DiscoverListFrag.class.getName());
                mCurrType = PageType.DISCOVER;
                mAddImg.setVisibility(View.VISIBLE);
                mSearchImg.setVisibility(View.GONE);
                mMoreImg.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        mTitleTx.setText(mCurrType.getValue());
    }

    private void checkPermissionAndShowCameraDia() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constants.Ints.PERMISSION_CAMERA);
        } else {
            if (mCameraDia == null) {
                mCameraDia = new CameraDialog(this, CameraDialog.Type.VIDEO);
            }
            mCameraDia.show();
        }
    }

    private void checkPermissionAndStartLocationService() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.Ints.PERMISSION_LOCATION);
        }
        Intent locationIntent = new Intent(this, LocationService.class);
        bindService(locationIntent, LocationOnMain.getInstance().getServiceConnection(), Context.BIND_AUTO_CREATE);
        startService(locationIntent);
        LocationOnMain.getInstance().setUserLogin(SharedPreferencesUtil.getInstance(this).getLoginUser());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.Ints.PERMISSION_CAMERA) {
            for (int index = 0; index < permissions.length; index++) {
                switch (permissions[index]) {
                    case Manifest.permission.CAMERA:
                        if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                            // TODO: 16/1/25 to do nothing here
                            checkPermissionAndShowCameraDia();
                        } else if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                            Toast.makeText(this, "您拒绝了拍照权限，不能使用此功能！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "应用没有拍照权限，不能使用此功能！", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    case Manifest.permission.READ_EXTERNAL_STORAGE:
                        if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                            checkPermissionAndShowCameraDia();
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
        } else if (requestCode == Constants.Ints.PERMISSION_LOCATION) {
            for (int index = 0; index < permissions.length; index++) {
                switch (permissions[index]) {
                    case Manifest.permission.ACCESS_COARSE_LOCATION:
                    case Manifest.permission.ACCESS_FINE_LOCATION:
                        if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                            checkPermissionAndStartLocationService();
                        } else if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                            Toast.makeText(this, "您拒绝了定位权限，舞友和培训功能将受到影响！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "应用没有定位权限，舞友和培训功能将受到影响！", Toast.LENGTH_LONG).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.browse_user:
                if (mBrowseDrawer.isDrawerOpen(Gravity.LEFT)) {
                    mBrowseDrawer.closeDrawer(Gravity.LEFT);
                } else {
                    mBrowseDrawer.openDrawer(Gravity.LEFT);
                }
                break;
            case R.id.browse_add://发布
                if (SecurityUtils.isUserValidity(this, mUserLogin)) {
                    if (mTypeChoosePop == null) {
                        mTypeChoosePop = new PublishTypeChoosePop(this);
                    }
                    mTypeChoosePop.show(getWindow().getDecorView());
                }
                break;
            case R.id.browse_search:
                Intent searchIntent = new Intent(this, SearchActivity.class);
                searchIntent.putExtra(Constants.Strings.PAGE_TYPE, mCurrType.getValue());
                startActivity(searchIntent);
                break;
            case R.id.browse_more:
                MoreChoiceDialog firendDia = new MoreChoiceDialog(BrowseNewActivity.this);
                firendDia.show();
                firendDia.addFunctions(new String[]{"男神", "女神", "不限"}, new int[]{R.id.friend_main_sex_man, R.id.friend_main_sex_woman, R.id.friend_main_sex_no});
                firendDia.setOnFunctionItemClickListener(new MoreChoiceDialog.OnFunctionItemClickListener() {
                    @Override
                    public void OnFunctionItemClick(View v) {
                        ((FriendListFrag) mFriendFrag).sexFilterChoose(v);
                    }
                });
                break;
            case R.id.browse_menu_head:
                if (mUserLogin == null) {//登录
                    startActivity(new Intent(this, LoginNewActivity.class));
                } else {//修改资料
                    if (mUserLogin.getOrgId() > 0) {
                        Intent orgIntent = new Intent(this, OrgDetailsActivity.class);
                        orgIntent.putExtra(Constants.Strings.ORG_ID, mUserLogin.getOrgId());
                        orgIntent.putExtra(Constants.Strings.USER_ID, mUserLogin.getUserId());
                        orgIntent.putExtra(Constants.Strings.ORG_LOGO, mUserLogin.getPhotoUrl());
                        orgIntent.putExtra(Constants.Strings.ORG_NAME, mUserLogin.getNickName());
                        orgIntent.putExtra(Constants.Strings.ORG_SIGN, mUserLogin.getSignature());
                        startActivity(orgIntent);
                    } else {
                        Intent ucIntent = new Intent(this, UserDetailsActivity.class);
                        ucIntent.putExtra(Constants.Strings.USER_ID, mUserLogin.getUserId());
                        ucIntent.putExtra(Constants.Strings.USER_PHOTO, mUserLogin.getPhotoUrl());
                        ucIntent.putExtra(Constants.Strings.USER_NAME, mUserLogin.getNickName());
                        ucIntent.putExtra(Constants.Strings.USER_SIGN, mUserLogin.getSignature());
                        startActivity(ucIntent);
                    }

                }
                mBrowseDrawer.closeDrawer(Gravity.LEFT);
                break;
            case R.id.browse_menu_mineStart://我的发布
                if (SecurityUtils.isUserValidity(this, mUserLogin)) {
                    Intent startIntent = new Intent(this, MineStartActivity.class);
                    startIntent.putExtra(Constants.Strings.USER_ID, mUserLogin.getUserId());
                    startActivity(startIntent);
                    mBrowseDrawer.closeDrawer(Gravity.LEFT);
                }
                break;
            case R.id.browse_menu_relatedMe://与我相关
                if (SecurityUtils.isUserValidity(this, mUserLogin)) {
                    Intent relatedIntent = new Intent(this, MineRelatedActivity.class);
                    relatedIntent.putExtra(Constants.Strings.USER_ID, mUserLogin.getUserId());
                    startActivity(relatedIntent);
                    mBrowseDrawer.closeDrawer(Gravity.LEFT);
                }
                break;
            case R.id.browse_menu_wallet://钱包
                if (SecurityUtils.isUserValidity(this, mUserLogin)) {
                    Intent walletIntent = new Intent(this, WalletActivity.class);
                    walletIntent.putExtra(Constants.Strings.USER_ID, mUserLogin.getUserId());
                    startActivity(walletIntent);
                    mBrowseDrawer.closeDrawer(Gravity.LEFT);
                }
                break;
            case R.id.browse_menu_privateMsg://私信
                if (SecurityUtils.isUserValidity(this, mUserLogin)) {
                    startActivity(new Intent(this, PrivateMsgListActivity.class));
                }
                break;
            case R.id.browse_menu_teacherAttest:
                if (SecurityUtils.isUserValidity(this, mUserLogin)) {
                    Intent attestIntent = new Intent(this, SimpleListActivity.class);
                    attestIntent.putExtra(Constants.Strings.SIMPLE_LIST_TYPE, SimpleListActivity.PERSONAL_TEACHER_ATTEST);
                    attestIntent.putExtra(Constants.Strings.USER_ID, mUserLogin.getUserId());
                    startActivity(attestIntent);
                    mBrowseDrawer.closeDrawer(Gravity.LEFT);
                }
                break;
            case R.id.browse_menu_mineBooking://我的预约
                if (SecurityUtils.isUserValidity(this, mUserLogin)) {
                    Intent bookingIntent = new Intent(this, MineBookingActivity.class);
                    bookingIntent.putExtra(Constants.Strings.USER_ID, mUserLogin.getUserId());
                    startActivity(bookingIntent);
                    mBrowseDrawer.closeDrawer(Gravity.LEFT);
                }
                break;
            case R.id.browse_menu_about:
                if (SecurityUtils.isUserValidity(this, mUserLogin)) {
                    startActivity(new Intent(this, AboutActivity.class));
                }
                break;
            case R.id.browse_menu_settings://设置
                if (SecurityUtils.isUserValidity(this, mUserLogin)) {
                    startActivity(new Intent(this, SettingsActivity.class));
                    mBrowseDrawer.closeDrawer(Gravity.LEFT);
                }
                break;
            case R.id.browse_menu_inviteTeacher://邀请老师入驻
                if (SecurityUtils.isUserValidity(this, mUserLogin)) {
                    Intent attestIntent = new Intent(this, SimpleListActivity.class);
                    attestIntent.putExtra(Constants.Strings.SIMPLE_LIST_TYPE, SimpleListActivity.ORG_TEACHER_INVITATION);
                    attestIntent.putExtra(Constants.Strings.ORG_ID, mUserLogin.getOrgId());
                    startActivity(attestIntent);
                    mBrowseDrawer.closeDrawer(Gravity.LEFT);
                }
                break;
            case R.id.browse_menu_authenticateTeacher://待认证老师
                if (SecurityUtils.isUserValidity(this, mUserLogin)) {
                    Intent attestIntent = new Intent(this, SimpleListActivity.class);
                    attestIntent.putExtra(Constants.Strings.SIMPLE_LIST_TYPE, SimpleListActivity.ORG_TEACHER_ATTEST);
                    attestIntent.putExtra(Constants.Strings.ORG_ID, mUserLogin.getOrgId());
                    startActivity(attestIntent);
                    mBrowseDrawer.closeDrawer(Gravity.LEFT);
                }
                break;
            case R.id.browse_menu_orderBook://预约订单
                if (SecurityUtils.isUserValidity(this, mUserLogin)) {
                    Intent bookingIntent = new Intent(this, MineBookingActivity.class);
                    bookingIntent.putExtra(Constants.Strings.ORG_ID, mUserLogin.getOrgId());
                    startActivity(bookingIntent);
                    mBrowseDrawer.closeDrawer(Gravity.LEFT);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.msg_app_version_got:
                if (msg.obj != null) {
                    SimpleResponse simpleResponse = (SimpleResponse) msg.obj;
                    ConfirmDialog confirmDia = new ConfirmDialog(this, ConfirmDialog.Type.APPUPDATE, new ConfirmDialog.OnConfirmClickListenter() {
                        @Override
                        public void onConfirmClick(View v) {
                            if (v.getId() == R.id.dia_confirm_confirm) {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                intent.setData(Uri.parse(Constants.Strings.UPDATE_URI_YYB));
                                startActivity(intent);
                            }
                        }
                    });
                    confirmDia.setCancelable(false);
                    if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                        try {
                            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                            LogUtil.w(TAG, packageInfo.versionName + "");
                            if (Double.parseDouble(simpleResponse.getContentString()) > Double.parseDouble(packageInfo.versionName)) {
                                confirmDia.show();
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
        return true;
    }
}
