package cn.spinsoft.wdq.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.spinsoft.wdq.R;

/**
 * Created by zhoujun on 2016-2-23.
 * -弹出的确认选择框
 */
public class ConfirmDialog extends AlertDialog implements View.OnClickListener{
    private static final String TAG = ConfirmDialog.class.getSimpleName();

    private Type mType = Type.LOGOUT;
    private OnConfirmClickListenter confirmClickListenter;

    public interface OnConfirmClickListenter{
        void onConfirmClick(View v);
    }

    public enum Type {
        LOGOUT,DELETE,ADDWEIXI,CLEARCACHE,APPUPDATE,REWARD
    }

    public ConfirmDialog(Context context, Type type,OnConfirmClickListenter onConfirmClickListenter) {
        this(context);
        this.mType = type;
        this.confirmClickListenter = onConfirmClickListenter;
    }

    private ConfirmDialog(Context context){
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dia_confirm);

        Button mCancelBtn = (Button) findViewById(R.id.dia_confirm_cancel);
        Button mConfirmBtn = (Button) findViewById(R.id.dia_confirm_confirm);
        TextView confirmTip = (TextView) findViewById(R.id.dia_confirm_tip);
        confirmTip.setTextColor(getContext().getResources().getColor(R.color.black));
        if(mType==Type.LOGOUT){
            confirmTip.setText("是否注销");
        }else if(mType==Type.DELETE){
            confirmTip.setText("是否删除");
        }else if(mType==Type.ADDWEIXI){
            confirmTip.setText("使用提现功能需要添加一个\n微信账号");
            mConfirmBtn.setText("添加微信账号");
        }else if(mType==Type.CLEARCACHE){
            confirmTip.setText("是否清除缓存");
        }else if(mType==Type.APPUPDATE){
            confirmTip.setText("发现新版本，请去更新");
            mConfirmBtn.setText("更新");
        }else if (mType==Type.REWARD){
            confirmTip.setText("是否打赏");
        }

        mCancelBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(confirmClickListenter!=null){
            confirmClickListenter.onConfirmClick(v);
            dismiss();
        }
    }
}
