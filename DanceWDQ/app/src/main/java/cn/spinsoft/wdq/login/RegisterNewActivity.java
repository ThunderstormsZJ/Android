package cn.spinsoft.wdq.login;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.login.frag.ProtocolFrag;
import cn.spinsoft.wdq.login.frag.RegisterFrag;

/**
 * Created by zhoujun on 2016-3-31.
 */
public class RegisterNewActivity extends BaseActivity{
    private final static String TAG = RegisterNewActivity.class.getSimpleName();
    private TextView mBackTx;
    private RegisterFrag mRegisterFrag;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register_new;
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        mBackTx = (TextView) findViewById(R.id.register_back);

        mRegisterFrag = (RegisterFrag) changeContentFragment(R.id.register_child_container,mRegisterFrag,RegisterFrag.class.getName());

        mBackTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportFragmentManager().findFragmentById(R.id.register_child_container) instanceof ProtocolFrag) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.register_child_container, mRegisterFrag).commit();
                }else {
                    RegisterNewActivity.this.finish();
                }
            }
        });
    }

    public void setBackTx(){
        mBackTx.setText("注册");
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.register_child_container) instanceof ProtocolFrag) {
            getSupportFragmentManager().beginTransaction().replace(R.id.register_child_container, mRegisterFrag).commit();
        }else {
            super.onBackPressed();
        }
    }
}
