package cn.spinsoft.wdq.mine.widget;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.enums.DiscoverType;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.mine.component.PublishActivity;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;

/**
 * Created by hushujun on 15/12/25.
 */
public class PublishTypeChoosePop extends PopupWindow implements View.OnClickListener {
    private static final String TAG = PublishTypeChoosePop.class.getSimpleName();

    private Activity mContext;
    private TextView activityTx, topicTx, contestTx, recruitTx, otherTx;

    private Activity context;

    public PublishTypeChoosePop(Activity context) {
        super(context);
        mContext = context;
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_publish_type_choose, null);
        activityTx = (TextView) contentView.findViewById(R.id.publish_type_activity);
        topicTx = (TextView) contentView.findViewById(R.id.publish_type_topic);
        contestTx = (TextView) contentView.findViewById(R.id.publish_type_contest);
        recruitTx = (TextView) contentView.findViewById(R.id.publish_type_recruit);
        otherTx = (TextView) contentView.findViewById(R.id.publish_type_other);

        activityTx.setOnClickListener(this);
        topicTx.setOnClickListener(this);
        contestTx.setOnClickListener(this);
        recruitTx.setOnClickListener(this);
        otherTx.setOnClickListener(this);

        setContentView(contentView);
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.publishTypeChooseAnimationStyle);

        this.context = context;
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, PublishActivity.class);
        DiscoverType type;
        switch (v.getId()) {
            case R.id.publish_type_activity:
                type = DiscoverType.ACTIVITY;
                break;
            case R.id.publish_type_topic:
                type = DiscoverType.TOPIC;
                break;
            case R.id.publish_type_contest:
                type = DiscoverType.CONTEST;
                break;
            case R.id.publish_type_recruit:
                type = DiscoverType.RECRUIT;
                break;
            case R.id.publish_type_other:
            default:
                type = DiscoverType.OTHER;
                break;
        }
        UserLogin loginUser = SharedPreferencesUtil.getInstance(context).getLoginUser();
        if (loginUser.getOrgId() <= 0 && type == DiscoverType.RECRUIT) {
            Toast.makeText(context, "只有机构才拥有此权限", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }
        intent.putExtra(Constants.Strings.DISCOVER_TYPE, type.getValue());
        mContext.startActivity(intent);
        dismiss();
    }

    public void show(View parent) {
        if (!isShowing()) {
            showAtLocation(parent, Gravity.TOP, 0, 0);
        } else {
            dismiss();
        }
    }
}
