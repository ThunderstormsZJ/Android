package cn.spinsoft.wdq.mine.biz;

import android.os.Message;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseHandler;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.login.biz.LoginParser;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.login.biz.UserThird;
import cn.spinsoft.wdq.utils.ThreadManager;

/**
 * Created by hushujun on 15/12/7.
 */
public class MineHandler extends BaseHandler {
    private static final String TAG = MineHandler.class.getSimpleName();
    public static final int CHILD_HOST = 1;
    public static final int CHILD_LOGIN = 1 << 0;
    private static UserInfoDetail infoDetail;
    private static UserThird userRegist;

    private Runnable getUserInfoRun() {
        return new Runnable() {
            @Override
            public void run() {
                UserInfoDetail userInfo = MineParser.getUserInfoDetail(Status.userId, Status.orgId);
                Message msg = MineHandler.this.obtainMessage(R.id.msg_mine_info_detail_got);
                msg.obj = userInfo;
                MineHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable updateUserInfoRun() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse success = MineParser.updateUserInfo(infoDetail);
                Message msg = MineHandler.this.obtainMessage(R.id.msg_mine_info_updated);
                msg.obj = success;
                MineHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable updateOrgInfoRun() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse success = MineParser.updateOrgInfo(infoDetail);
                Message msg = MineHandler.this.obtainMessage(R.id.msg_org_info_updated);
                msg.obj = success;
                MineHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getThirdLoginInfo() {
        return new Runnable() {
            @Override
            public void run() {
                UserLogin userLogin = LoginParser.getThirdLoginInfo(userRegist);
                Message msg = MineHandler.this.obtainMessage(R.id.msg_third_loginInfo_got);
                msg.obj = userLogin;
                MineHandler.this.sendMessage(msg);
            }
        };
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.msg_mine_get_info_detail:
                ThreadManager.exectueSingleTask(getUserInfoRun());
                break;
            case R.id.msg_mine_update_info:
                infoDetail = (UserInfoDetail) msg.obj;
                ThreadManager.exectueSingleTask(updateUserInfoRun());
                break;
            case R.id.msg_org_update_info:
                infoDetail = (UserInfoDetail) msg.obj;
                ThreadManager.exectueSingleTask(updateOrgInfoRun());
                break;
            case R.id.msg_get_third_loginInfo://通过第三方登录获取登录信息
                userRegist = (UserThird) msg.obj;
                ThreadManager.exectueSingleTask(getThirdLoginInfo());
                break;
            case R.id.msg_mine_info_updated:
            case R.id.msg_org_info_updated:
            case R.id.msg_mine_info_detail_got:
                handleCallbackMessage(CHILD_HOST, msg);
                break;
            case R.id.msg_third_loginInfo_got:
                handleCallbackMessage(CHILD_LOGIN,msg);
                break;
            default:
                break;
        }
    }

    @Override
    public void release() {
        super.release();
        Status.release();
    }

    public static class Status {
        public static int orgId = -1;
        public static int userId = -1;

        private static void release() {
            orgId = -1;
            userId = -1;
        }
    }
}
