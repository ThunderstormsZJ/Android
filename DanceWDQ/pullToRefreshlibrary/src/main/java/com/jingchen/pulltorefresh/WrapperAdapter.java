package com.jingchen.pulltorefresh;

import android.support.v7.widget.RecyclerView;

interface WrapperAdapter {

    RecyclerView.Adapter getWrappedAdapter();
}
