package cn.spinsoft.wdq.mine.frag;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.browse.biz.BrowseHandler;
import cn.spinsoft.wdq.mine.biz.UserInfoSimple;
import cn.spinsoft.wdq.mine.component.MineBookingActivity;
import cn.spinsoft.wdq.mine.component.MineInfoActivity;
import cn.spinsoft.wdq.mine.component.MineRelatedActivity;
import cn.spinsoft.wdq.mine.component.MineStartActivity;
import cn.spinsoft.wdq.mine.component.OrgInfoActivity;
import cn.spinsoft.wdq.mine.component.PrivateMsgListActivity;
import cn.spinsoft.wdq.mine.component.SimpleListActivity;
import cn.spinsoft.wdq.mine.component.WalletActivity;
import cn.spinsoft.wdq.settings.SettingsActivity;
import cn.spinsoft.wdq.user.UserDetailsActivity;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SecurityUtils;

/**
 * Created by hushujun on 15/12/4.
 */
@Deprecated
public class MineFrag extends BaseFragment implements View.OnClickListener, Handler.Callback {
    private static final String TAG = MineFrag.class.getSimpleName();

    private SimpleDraweeView mPhotoImg;
    private TextView mNameTx, mSignatureTx;
    private TextView mMessageTx, mNewMessagesTx;
    private ViewStub mDifferentVs;
    private boolean isViewStubInflated = false;

    private UserInfoSimple watcherUserInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_mine;
    }

    @Override
    protected void initViewAndListener(View root, Bundle savedInstanceState) {
        TextView mBackTx = (TextView) root.findViewById(R.id.mine_back);
        ImageView mMoreImg = (ImageView) root.findViewById(R.id.mine_more);

        mPhotoImg = (SimpleDraweeView) root.findViewById(R.id.mine_photo);
        RelativeLayout mToEditRl = (RelativeLayout) root.findViewById(R.id.mine_to_edit);
        mNameTx = (TextView) root.findViewById(R.id.mine_name);
        mSignatureTx = (TextView) root.findViewById(R.id.mine_signature);

        RelativeLayout mMessageRl = (RelativeLayout) root.findViewById(R.id.mine_message_line);
        mMessageTx = (TextView) root.findViewById(R.id.mine_message);
        mNewMessagesTx = (TextView) root.findViewById(R.id.mine_message_new);
        TextView mSettingTx = (TextView) root.findViewById(R.id.mine_settings);

        TextView mSponsorTx = (TextView) root.findViewById(R.id.mine_sponsor);
        TextView mRelatedTx = (TextView) root.findViewById(R.id.mine_related);
        TextView mWalletTx = (TextView) root.findViewById(R.id.mine_wallet);
        mDifferentVs = (ViewStub) root.findViewById(R.id.mine_different_vs);

        mBackTx.setOnClickListener(this);
        mMoreImg.setOnClickListener(this);
        mToEditRl.setOnClickListener(this);
        mPhotoImg.setOnClickListener(this);
        mMessageRl.setOnClickListener(this);
        mSettingTx.setOnClickListener(this);
        mSponsorTx.setOnClickListener(this);
        mRelatedTx.setOnClickListener(this);
        mWalletTx.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mHandler.addCallback(BrowseHandler.CHILD_PERSONAL, this);
        mHandler.sendEmptyMessage(R.id.msg_mine_get_info_simple);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_back:
                getActivity().finish();
                break;
            case R.id.mine_more:

                break;
            case R.id.mine_to_edit:
                if (SecurityUtils.isUserValidity(getActivity(), BrowseHandler.watcherUserId)) {
                    getActivity().startActivityForResult(new Intent(getActivity(), MineInfoActivity.class),
                            Constants.Ints.REQUEST_CODE_MINE_EDITED);
                }
                break;
            case R.id.mine_photo:
                if (SecurityUtils.isUserValidity(getActivity(), BrowseHandler.watcherUserId)) {
                    Intent userIntent = new Intent(getActivity(), UserDetailsActivity.class);
                    userIntent.putExtra(Constants.Strings.USER_ID, BrowseHandler.watcherUserId);
                    if (watcherUserInfo != null) {
                        userIntent.putExtra(Constants.Strings.USER_PHOTO, watcherUserInfo.getPhotoUrl());
                        userIntent.putExtra(Constants.Strings.USER_NAME, watcherUserInfo.getNickName());
                        userIntent.putExtra(Constants.Strings.USER_SIGN, watcherUserInfo.getSignature());
                    }
                    startActivity(userIntent);
                }
                break;
            case R.id.mine_sponsor:
                if (SecurityUtils.isUserValidity(getActivity(), BrowseHandler.watcherUserId)) {
                    startActivity(new Intent(getActivity(), MineStartActivity.class));
                }
                break;
            case R.id.mine_message_line:
                if (SecurityUtils.isUserValidity(getActivity(), BrowseHandler.watcherUserId)) {
                    Intent intent = new Intent(getActivity(), PrivateMsgListActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.mine_related:
                if (SecurityUtils.isUserValidity(getActivity(), BrowseHandler.watcherUserId)) {
                    Intent intent = new Intent(getActivity(), MineRelatedActivity.class);
                    intent.putExtra(Constants.Strings.USER_ID, BrowseHandler.watcherUserId);
                    startActivity(intent);
                }
                break;
            case R.id.mine_wallet:
                if (SecurityUtils.isUserValidity(getActivity(), BrowseHandler.watcherUserId)) {
                    Intent walletIntent = new Intent(getActivity(), WalletActivity.class);
                    walletIntent.putExtra(Constants.Strings.USER_ID, BrowseHandler.watcherUserId);
                    startActivity(walletIntent);
                }
                break;
            case R.id.mine_attest:
                if (SecurityUtils.isUserValidity(getActivity(), BrowseHandler.watcherUserId)) {
                    Intent attestIntent = new Intent(getActivity(), SimpleListActivity.class);
                    attestIntent.putExtra(Constants.Strings.SIMPLE_LIST_TYPE, SimpleListActivity.PERSONAL_TEACHER_ATTEST);
                    attestIntent.putExtra(Constants.Strings.USER_ID, BrowseHandler.watcherUserId);
                    startActivity(attestIntent);
                }
                break;
            case R.id.mine_booking:
                if (SecurityUtils.isUserValidity(getActivity(), BrowseHandler.watcherUserId)) {
                    Intent bookingIntent = new Intent(getActivity(), MineBookingActivity.class);
                    bookingIntent.putExtra(Constants.Strings.USER_ID, BrowseHandler.watcherUserId);
                    startActivity(bookingIntent);
                }
                break;
            case R.id.mine_org_material:
                if (SecurityUtils.isUserValidity(getActivity(), BrowseHandler.Status.orgId)) {
                    Intent intent = new Intent(getActivity(), OrgInfoActivity.class);
                    intent.putExtra(Constants.Strings.ORG_ID, BrowseHandler.Status.orgId);
                    startActivity(intent);
                }
                break;
            case R.id.mine_org_booking:
                if (SecurityUtils.isUserValidity(getActivity(), BrowseHandler.Status.orgId)) {
                    Intent intent = new Intent(getActivity(), MineBookingActivity.class);
                    intent.putExtra(Constants.Strings.ORG_ID, BrowseHandler.Status.orgId);
                    startActivity(intent);
                }
                break;
            case R.id.mine_org_invite:
                if (SecurityUtils.isUserValidity(getActivity(), BrowseHandler.Status.orgId)) {
                    Intent intent = new Intent(getActivity(), SimpleListActivity.class);
                    intent.putExtra(Constants.Strings.SIMPLE_LIST_TYPE, SimpleListActivity.ORG_TEACHER_INVITATION);
                    intent.putExtra(Constants.Strings.ORG_ID, BrowseHandler.Status.orgId);
                    startActivity(intent);
                }
                break;
            case R.id.mine_org_attest:
                if (SecurityUtils.isUserValidity(getActivity(), BrowseHandler.Status.orgId)) {
                    Intent intent = new Intent(getActivity(), SimpleListActivity.class);
                    intent.putExtra(Constants.Strings.SIMPLE_LIST_TYPE, SimpleListActivity.ORG_TEACHER_ATTEST);
                    intent.putExtra(Constants.Strings.ORG_ID, BrowseHandler.Status.orgId);
                    startActivity(intent);
                }
                break;
            case R.id.mine_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == R.id.msg_mine_info_simple_got) {
            if (msg.obj != null) {
                UserInfoSimple userInfo = (UserInfoSimple) msg.obj;
                loadUserInfo(userInfo);
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.w(TAG, "onActivityResult:" + requestCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.Ints.REQUEST_CODE_MINE_EDITED) {
                mHandler.sendEmptyMessage(R.id.msg_mine_get_info_simple);
            }
        }
    }

    private void loadUserInfo(UserInfoSimple userInfo) {
        if (userInfo == null) {
            return;
        }
        watcherUserInfo = userInfo;
        BrowseHandler.watcherUserId = watcherUserInfo.getUserId();
        BrowseHandler.Status.orgId = watcherUserInfo.getOrgId();

        mPhotoImg.setImageURI(Uri.parse(userInfo.getPhotoUrl()));
        mNameTx.setText(userInfo.getNickName());
        mSignatureTx.setText(userInfo.getSignature());

        if (!isViewStubInflated) {
            if (watcherUserInfo.getOrgId() > 0) {
                mDifferentVs.setLayoutResource(R.layout.ly_mine_different_org);
                View view = mDifferentVs.inflate();
                TextView materialTx = (TextView) view.findViewById(R.id.mine_org_material);
                TextView bookingTx = (TextView) view.findViewById(R.id.mine_org_booking);
                TextView inviteTx = (TextView) view.findViewById(R.id.mine_org_invite);
                TextView attestTx = (TextView) view.findViewById(R.id.mine_org_attest);
                materialTx.setOnClickListener(this);
                bookingTx.setOnClickListener(this);
                inviteTx.setOnClickListener(this);
                attestTx.setOnClickListener(this);
            } else {
                mDifferentVs.setLayoutResource(R.layout.ly_mine_different_personal);
                View view = mDifferentVs.inflate();
                TextView attestTx = (TextView) view.findViewById(R.id.mine_attest);
                TextView arrangeTx = (TextView) view.findViewById(R.id.mine_booking);
                attestTx.setOnClickListener(this);
                arrangeTx.setOnClickListener(this);
            }
            isViewStubInflated = true;
        }
    }
}
