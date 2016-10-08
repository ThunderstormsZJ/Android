package cn.spinsoft.wdq.home;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.contact.ContactProvider;
import com.netease.nim.uikit.contact.core.query.PinYin;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MessageNotifierCustomization;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.umeng.socialize.PlatformConfig;

import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.PreActivity;
import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.utils.session.NimLocationProvider;
import cn.spinsoft.wdq.utils.session.SessionHelper;
import cn.spinsoft.wdq.utils.sys.SystemUtil;

//import cn.spinsoft.wdq.utils.session.NimLocationProvider;

/**
 * Created by zhoujun on 2016-3-3.
 */
public class AppInitConf extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
        NIMClient.init(this, getLoginInfo(), getOptions());

        if (inMainProcess()) {
            // init pinyin贴图表情解码
            PinYin.init(this);
            PinYin.validate();

            // 初始化UIKit模块
            initUIKit();

            // 初始化消息提醒
            NIMClient.toggleNotification(true);

            // 注册语言变化监听
//            registerLocaleReceiver(true);
        }
    }

    private void initUIKit() {
        // 初始化，需要传入用户信息提供者
        NimUIKit.init(this, infoProvider, contactProvider);

        // 设置地理位置提供者。如果需要发送地理位置消息，该参数必须提供。如果不需要，可以忽略。
        NimUIKit.setLocationProvider(new NimLocationProvider());

        // 会话窗口的定制初始化。
        SessionHelper.init();

        // 通讯录列表定制初始化
//        ContactHelper.init();
    }

    private ContactProvider contactProvider = new ContactProvider() {
        @Override
        public List<UserInfoProvider.UserInfo> getUserInfoOfMyFriends() {
            List<NimUserInfo> nimUsers = NimUserInfoCache.getInstance().getAllUsersOfMyFriend();
            List<UserInfoProvider.UserInfo> users = new ArrayList<>(nimUsers.size());
            if (!nimUsers.isEmpty()) {
                users.addAll(nimUsers);
            }

            return users;
        }

        @Override
        public int getMyFriendsCount() {
            return FriendDataCache.getInstance().getMyFriendCounts();
        }

        @Override
        public String getUserDisplayName(String account) {
            return NimUserInfoCache.getInstance().getUserDisplayName(account);
        }
    };

    public boolean inMainProcess() {
        String packageName = getPackageName();
        String processName = SystemUtil.getProcessName(this);
        return packageName.equals(processName);
    }

    private LoginInfo getLoginInfo() {
        UserLogin loginUser = SharedPreferencesUtil.getInstance(this).getLoginUser();
        if (loginUser != null) {
            String imToken = loginUser.getImToken();
            String imAccount = String.valueOf(loginUser.getUserAccount());
            if (!TextUtils.isEmpty(imToken) && !TextUtils.isEmpty(imAccount)) {
                return new LoginInfo(imAccount, imToken);
            }
        }
        return null;
    }

    private SDKOptions getOptions() {
        SDKOptions options = new SDKOptions();

        // 如果将新消息通知提醒托管给SDK完成，需要添加以下配置。
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        // 点击通知需要跳转到的界面
        config.notificationEntrance = PreActivity.class;
        config.notificationSmallIconId = R.mipmap.ic_launcher;

        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";
        options.statusBarNotificationConfig = config;

        // 配置保存图片，文件，log等数据的目录
        String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim";
        options.sdkStorageRootPath = sdkPath;

        // 配置数据库加密秘钥
        options.databaseEncryptKey = "NETEASE";

        // 配置是否需要预下载附件缩略图
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小，
        options.thumbnailSize = (int) (165.0 / 320.0 * ScreenUtil.screenWidth);

        // 用户信息提供者
        options.userInfoProvider = infoProvider;

        // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
        options.messageNotifierCustomization = messageNotifierCustomization;

        return options;
    }

    private UserInfoProvider infoProvider = new UserInfoProvider() {
        @Override
        public UserInfo getUserInfo(String account) {
            UserInfo user = NimUserInfoCache.getInstance().getUserInfo(account);
            if (user == null) {
                NimUserInfoCache.getInstance().getUserInfoFromRemote(account, null);
            }
            return user;
        }

        @Override
        public int getDefaultIconResId() {
            return R.mipmap.ic_default_user_head;
        }

        @Override
        public Bitmap getTeamIcon(String tid) {
            return null;
        }

        @Override
        public Bitmap getAvatarForMessageNotifier(String account) {
            //为通知栏提供用户头像（一般从本地缓存中取，若未下载或本地不存在，返回null，通知栏将显示默认头像）

            return null;
        }

        @Override
        public String getDisplayNameForMessageNotifier(String account, String sessionId,
                                                       SessionTypeEnum sessionType) {
            String nick = null;
            if (sessionType == SessionTypeEnum.P2P) {
                nick = NimUserInfoCache.getInstance().getAlias(account);
            }

            // 返回null，交给sdk处理。如果对方有设置nick，sdk会显示nick
            if (TextUtils.isEmpty(nick)) {
                return null;
            }
            return nick;
        }
    };

    private MessageNotifierCustomization messageNotifierCustomization = new MessageNotifierCustomization() {
        @Override
        public String makeNotifyContent(String nick, IMMessage message) {
            return null; // 采用SDK默认文案
        }

        @Override
        public String makeTicker(String nick, IMMessage message) {
            return null; // 采用SDK默认文案
        }
    };


    {
        PlatformConfig.setQQZone(Constants.Strings.QQ_APP_ID, Constants.Strings.QQ_APP_KEY);
        PlatformConfig.setWeixin(Constants.Strings.WX_APP_ID, Constants.Strings.WX_APP_SECRET);
    }
}