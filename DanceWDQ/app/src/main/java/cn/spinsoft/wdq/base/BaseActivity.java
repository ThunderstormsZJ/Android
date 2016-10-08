package cn.spinsoft.wdq.base;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.utils.SystemBarTintManager;

/**
 * Created by hushujun on 15/11/2.
 */
public abstract class BaseActivity extends FragmentActivity {
    protected BaseHandler mHandler;

    protected BaseFragment mPageLast;
    private Bundle mFragBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mFragBundle = savedInstanceState;
        } else {
            mFragBundle = new Bundle();
        }

        initHandler();

        setContentView(getLayoutId());
        initViewAndListener(savedInstanceState);
    }

    @TargetApi(19)
    protected void setTranslucentStatus(boolean on){
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if(on){
            winParams.flags |= bits;
        }else{
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    protected abstract @LayoutRes int getLayoutId();

    protected void initHandler() {
        mHandler = new BaseHandler();
    }

    @Deprecated
    protected void setTintRes(@DrawableRes int resourceId) {
//        if (resourceId > 0) {
//            tintManager.setTintResource(resourceId);
//        }
    }

    protected abstract void initViewAndListener(Bundle savedInstanceState);

    public BaseHandler getHandler() {
        return mHandler;
    }

    protected Fragment changeContentFragment(int containerResId, Fragment frag, String fragName) {
        try {
            FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
            frag = getSupportFragmentManager().findFragmentByTag(fragName);
            if (frag == null) {
                frag = Fragment.instantiate(this, fragName, mFragBundle);
            }
            if (frag == mPageLast) {
                return frag;
            }
            if (mPageLast != null) {// 隐藏上一个frag
                fr.hide(mPageLast);
            }
            // if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // fr.setCustomAnimations(R.anim.down_in, R.anim.up_out);
            // }
            if (frag.isAdded()) {
                fr.show(frag);
            } else {
                fr.add(containerResId, frag, fragName);
            }
            fr.commitAllowingStateLoss();
            mPageLast = (BaseFragment) frag;
            return frag;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.release();
            mHandler = null;
        }
        super.onDestroy();
    }
}
