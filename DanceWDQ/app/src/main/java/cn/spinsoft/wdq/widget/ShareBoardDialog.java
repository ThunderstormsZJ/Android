package cn.spinsoft.wdq.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.socialize.bean.SHARE_MEDIA;

import cn.spinsoft.wdq.R;

/**
 * Created by zhoujun on 2016-3-8.
 */
public class ShareBoardDialog extends Dialog {

    private ShareBoardDiaListener mShareBordListener;

    private LinearLayout mShareBordLL;
    private SHARE_MEDIA[] shareMedias;

    public ShareBoardDialog setShare_medias(SHARE_MEDIA[] shareMedias) {
        this.shareMedias = shareMedias;
        return this;
    }

    public ShareBoardDialog(Context context) {
        super(context, R.style.DialogWithTransparentBackground);
    }

    public ShareBoardDialog(Context context, ShareBoardDiaListener shareBorderListener) {
        this(context);
        this.mShareBordListener = shareBorderListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dia_share_border);
        mShareBordLL = (LinearLayout) findViewById(R.id.dia_share_border_root);

        if (shareMedias != null) {
            for (int i = 0; i < shareMedias.length; i++) {
                createImgByShareType(shareMedias[i]);
            }
        }

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.umeng_socialize_shareboard_animation);
    }

    //根据分享的类型来创建图片
    private void createImgByShareType(SHARE_MEDIA shareMedia) {
        TextView tv = new TextView(getContext());
        ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(160, 160);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(12);
        tv.setPadding(10, 10, 10, 10);
        Drawable drawable = null;
        tv.setTag(shareMedia);
        switch (shareMedia) {
            case WEIXIN:
                drawable = getContext().getResources().getDrawable(R.drawable.umeng_socialize_wxcat_sel);
                drawable.setBounds(0, 0, 100, 100);
                tv.setCompoundDrawables(null, drawable, null, null);
                tv.setText("微信好友");
                break;
            case WEIXIN_CIRCLE:
                drawable = getContext().getResources().getDrawable(R.drawable.umeng_socialize_wxcircle_sel);
                drawable.setBounds(0, 0, 100, 100);
                tv.setCompoundDrawables(null, drawable, null, null);
                tv.setText("朋友圈");
                break;
            default:
                break;
        }
        //设置监听事件
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareBordListener.shareBoardOnclickListener(v);
            }
        });
        mShareBordLL.addView(tv, lp);
        TextView lineTx = new TextView(getContext());
        lp = new LinearLayout.LayoutParams(2, ViewGroup.LayoutParams.MATCH_PARENT);
        lineTx.setBackgroundColor(getContext().getResources().getColor(R.color.tintGray));
        mShareBordLL.addView(lineTx, lp);
    }

    public interface ShareBoardDiaListener {
        void shareBoardOnclickListener(View v);
    }
}
