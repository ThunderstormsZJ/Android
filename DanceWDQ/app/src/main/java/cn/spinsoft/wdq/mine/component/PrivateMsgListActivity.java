package cn.spinsoft.wdq.mine.component;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.recent.RecentContactsCallback;
import com.netease.nim.uikit.recent.RecentContactsFragment;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.utils.session.SessionHelper;
import cn.spinsoft.wdq.utils.session.extension.GuessAttachment;
import cn.spinsoft.wdq.utils.session.reminder.ReminderManager;

/**
 * Created by zhoujun on 16/1/18.
 */
public class PrivateMsgListActivity extends BaseActivity implements RecentContactsCallback {
    private static final String TAG = PrivateMsgListActivity.class.getSimpleName();

    private TextView mTitleTx;

    private int pageIdx = 1;
    private int watcherId = -1;
    private RecentContactsFragment mContactsFrag;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_private_msg_list;
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView backTx = (TextView) findViewById(R.id.private_msg_back);
        mTitleTx = (TextView) findViewById(R.id.private_msg_title);
        mTitleTx.setText("消息");

        mContactsFrag = new RecentContactsFragment();
        mContactsFrag.setContainerId(R.id.messages_fragment);

        getSupportFragmentManager().beginTransaction().replace(R.id.messages_fragment, mContactsFrag).commit();
        mContactsFrag.setCallback(this);

        backTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        UserLogin userLogin = SharedPreferencesUtil.getInstance(this).getLoginUser();
        if (userLogin != null) {
            watcherId = userLogin.getUserId();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // 最近联系人列表加载完毕
    @Override
    public void onRecentContactsLoaded() {

    }

    @Override
    public void onUnreadCountChange(int unreadCount) {
        ReminderManager.getInstance().updateSessionUnreadNum(unreadCount);
    }

    @Override
    public void onItemClick(RecentContact recent) {
        // 回调函数，以供打开会话窗口时传入定制化参数，或者做其他动作
        switch (recent.getSessionType()) {
            case P2P:
                SessionHelper.startP2PSession(this, recent.getContactId());
                break;
            default:
                break;
        }
    }

    @Override
    public String getDigestOfAttachment(MsgAttachment attachment) {
        // 设置自定义消息的摘要消息，展示在最近联系人列表的消息缩略栏上
        // 当然，你也可以自定义一些内建消息的缩略语，例如图片，语音，音视频会话等，自定义的缩略语会被优先使用。
        if (attachment instanceof GuessAttachment) {
            GuessAttachment guess = (GuessAttachment) attachment;
            return guess.getValue().getDesc();
        }
        return null;
    }

    @Override
    public String getDigestOfTipMsg(RecentContact recent) {
        String msgId = recent.getRecentMessageId();
        List<String> uuids = new ArrayList<>(1);
        uuids.add(msgId);
        List<IMMessage> msgs = NIMClient.getService(MsgService.class).queryMessageListByUuidBlock(uuids);
        if (msgs != null && !msgs.isEmpty()) {
            IMMessage msg = msgs.get(0);
            Map<String, Object> content = msg.getRemoteExtension();
            if (content != null && !content.isEmpty()) {
                return (String) content.get("content");
            }
        }

        return null;
    }
}
