//package cn.spinsoft.wdq.browse;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import java.util.Observable;
//import java.util.Observer;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.base.BaseActivity;
//import cn.spinsoft.wdq.base.BaseFragment;
//import cn.spinsoft.wdq.browse.biz.BrowseHandler;
//import cn.spinsoft.wdq.discover.frag.DiscoverListFrag;
//import cn.spinsoft.wdq.enums.PageType;
//import cn.spinsoft.wdq.login.LoginActivity;
//import cn.spinsoft.wdq.login.biz.UserLogin;
//import cn.spinsoft.wdq.mine.frag.MineFrag;
//import cn.spinsoft.wdq.org.frag.OrgListFrag;
//import cn.spinsoft.wdq.user.frag.FriendListFrag;
//import cn.spinsoft.wdq.utils.Constants;
//import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
//import cn.spinsoft.wdq.video.frag.VideoListFrag;
//import cn.spinsoft.wdq.widget.RadioGroup;
//
///**
// * Created by hushujun on 15/11/2.
// */
//public class BrowseActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, Observer {
//    private static final String TAG = BrowseActivity.class.getSimpleName();
//
//    private RadioGroup mNavigationRg;
//
//    private BaseFragment mMainFrag, mDisCoverFrag, mMineFrag;
//    private PageType mCurrType;
//
//    @Override
//    protected int getLayoutId() {
//        setTintRes(R.color.colorTitlePurple);
//        return R.layout.activity_browse;
//    }
//
//    @Override
//    protected void initHandler() {
//        mHandler = new BrowseHandler();
//
//        UserLogin userInfo = SharedPreferencesUtil.getInstance(this).getLoginUser();
//        if (userInfo != null) {
//            BrowseHandler.watcherUserId = userInfo.getUserId();
//            BrowseHandler.Status.orgId = userInfo.getOrgId();
//        }
//        SharedPreferencesUtil.getInstance(this).addObserver(this);
//
//        Intent intent = getIntent();
//        mCurrType = PageType.getEnum(intent.getStringExtra(Constants.Strings.PAGE_TYPE));
//        if (mCurrType == null) {
//            mCurrType = PageType.VIDEO;
//        }
//
////        if (mCurrType == PageType.FRIEND) {
////            LocationOnMain.getInstance().stopLocation();
////            LocationOnMain.getInstance().changLocationModel(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy.getValue(),
////                    Constants.Ints.LOCATION_INTERVAL);
////            LocationOnMain.getInstance().stopLocation();
////        }
//    }
//
//
//    @Override
//    protected void onDestroy() {
////        if (mCurrType == PageType.FRIEND) {
////            LocationOnMain.getInstance().stopLocation();
////            LocationOnMain.getInstance().changLocationModel(AMapLocationClientOption.AMapLocationMode.Battery_Saving.getValue(),
////                    Constants.Ints.LOCATION_INTERVAL_LONG);
////            LocationOnMain.getInstance().startLocation();
////        }
//        SharedPreferencesUtil.getInstance(this).deleteObserver(this);
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case Constants.Ints.REQUEST_CODE_VIDEO_CAPTURE:
//            case Constants.Ints.REQUEST_CODE_VIDEO_CHOOSE:
////            case Constants.Ints.REQUEST_CODE_VIDEO_INFO:
//            case Constants.Ints.REQUEST_CODE_ITEM_STATUS:
//                if (mMainFrag != null) {
//                    mMainFrag.onActivityResult(requestCode, resultCode, data);
//                }
//                break;
//            case Constants.Ints.REQUEST_CODE_MINE_EDITED:
//                if (mMineFrag != null) {
//                    mMineFrag.onActivityResult(requestCode, resultCode, data);
//                }
//                break;
//            default:
//                super.onActivityResult(requestCode, resultCode, data);
//                break;
//        }
//    }
//
//    @Override
//    protected void initViewAndListener(Bundle savedInstanceState) {
//        mNavigationRg = (RadioGroup) findViewById(R.id.page_bottom_navigation);
//        mNavigationRg.setOnCheckedChangeListener(this);
//        changMainFrag();
//    }
//
//    private void changMainFrag() {
//        switch (mCurrType) {
//            case FRIEND:
//                mMainFrag = (BaseFragment) changeContentFragment(R.id.browse_child_container,
//                        mMainFrag, FriendListFrag.class.getName());
//                break;
//            case ORG:
//                mMainFrag = (BaseFragment) changeContentFragment(R.id.browse_child_container,
//                        mMainFrag, OrgListFrag.class.getName());
//                break;
//            case VIDEO:
//            default:
//                mMainFrag = (BaseFragment) changeContentFragment(R.id.browse_child_container,
//                        mMainFrag, VideoListFrag.class.getName());
//                break;
//        }
//    }
//
//    @Override
//    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        switch (checkedId) {
//            case R.id.page_bottom_main:
//                changMainFrag();
//                setTintRes(R.color.colorTitlePurple);
//                break;
//            case R.id.page_bottom_discover:
//                mDisCoverFrag = (BaseFragment) changeContentFragment(R.id.browse_child_container,
//                        mDisCoverFrag, DiscoverListFrag.class.getName());
//                setTintRes(R.color.colorTitlePurple);
//                break;
//            case R.id.page_bottom_personal:
//                if (BrowseHandler.watcherUserId > 0 || BrowseHandler.Status.orgId > 0) {
//                    mMineFrag = (BaseFragment) changeContentFragment(R.id.browse_child_container,
//                            mMineFrag, MineFrag.class.getName());
//                    setTintRes(R.color.colorTitleWhite);
//                } else {
//                    Toast.makeText(this, R.string.toast_user_is_unvalidity, Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(this, LoginActivity.class);
//                    this.startActivity(intent);
//                }
//                break;
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public void update(Observable observable, Object data) {
//        if (data != null) {
//            BrowseHandler.watcherUserId = ((UserLogin) data).getUserId();
//            BrowseHandler.Status.orgId = ((UserLogin) data).getOrgId();
//
//            if(mNavigationRg.getCheckedChildId() == R.id.page_bottom_personal){
//                mMineFrag = (BaseFragment) changeContentFragment(R.id.browse_child_container,
//                        mMineFrag, MineFrag.class.getName());
//                setTintRes(R.color.colorTitleWhite);
//            }
//        }
//    }
//}
