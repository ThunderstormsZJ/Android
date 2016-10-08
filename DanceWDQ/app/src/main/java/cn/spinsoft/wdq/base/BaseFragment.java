package cn.spinsoft.wdq.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hushujun on 15/11/2.
 */
public abstract class BaseFragment extends Fragment {
    protected BaseHandler mHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(getLayoutId(), container, false);
        initViewAndListener(root, savedInstanceState);
        return root;
    }

    protected abstract @LayoutRes int getLayoutId();

    protected abstract void initViewAndListener(View root, Bundle savedInstanceState);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler = ((BaseActivity) getActivity()).getHandler();
    }
}
