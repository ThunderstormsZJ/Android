package cn.spinsoft.wdq.discover.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.utils.ContentResolverUtil;

/**
 * Created by hushujun on 15/12/16.
 */
public class SignInDialog extends Dialog implements TextWatcher{
    private static final String TAG = SignInDialog.class.getSimpleName();
    private EditText mNameEt, mTeleEt, mCountEt, mMarkEt;
    private Button mConfirmBtn;
    private OnSignInConfirmListener signInListener;

    public interface OnSignInConfirmListener {
        void onSignInConfirm(String[] signParams);
    }

    public SignInDialog(Context context, OnSignInConfirmListener listener) {
        super(context, R.style.PullDownDialog);
        this.signInListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dia_sign_in);
        mNameEt = (EditText) findViewById(R.id.dia_sign_in_name);
        mTeleEt = (EditText) findViewById(R.id.dia_sign_in_phone);
        mCountEt = (EditText) findViewById(R.id.dia_sign_in_count);
        mMarkEt = (EditText) findViewById(R.id.dia_sign_in_mark);
        mConfirmBtn = (Button) findViewById(R.id.dia_sign_in_confirm);
        mConfirmBtn.setEnabled(false);
        mConfirmBtn.setBackgroundColor(getContext().getResources().getColor(R.color.divider_gray));

        mNameEt.addTextChangedListener(this);
        mTeleEt.addTextChangedListener(this);
        mCountEt.addTextChangedListener(this);

        mMarkEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    checkInputAndConfirm();
                    return true;
                }
                return false;
            }
        });
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInputAndConfirm();
            }
        });

        Window window = getWindow();
        window.setGravity(Gravity.TOP);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(!TextUtils.isEmpty(mNameEt.getText()) && !TextUtils.isEmpty(mTeleEt.getText()) && !TextUtils.isEmpty(mCountEt.getText())){
            mConfirmBtn.setEnabled(true);
            mConfirmBtn.setBackgroundColor(getContext().getResources().getColor(R.color.bg_btn_orange));
        }else {
            mConfirmBtn.setEnabled(false);
            mConfirmBtn.setBackgroundColor(getContext().getResources().getColor(R.color.divider_gray));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void checkInputAndConfirm() {
        String name = mNameEt.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String tele = mTeleEt.getText().toString();
        if (TextUtils.isEmpty(tele)) {
            Toast.makeText(getContext(), "联系方式不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String count = mCountEt.getText().toString();
        String mark = mMarkEt.getText().toString();
        if (signInListener != null) {
            signInListener.onSignInConfirm(new String[]{name, tele, count, mark});
        }
        ContentResolverUtil.hideIMM(mConfirmBtn);
        dismiss();
    }
}
