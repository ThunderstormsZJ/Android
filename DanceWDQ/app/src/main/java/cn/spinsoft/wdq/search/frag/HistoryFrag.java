package cn.spinsoft.wdq.search.frag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.db.DBOperationUtil;
import cn.spinsoft.wdq.search.SearchActivity;
import cn.spinsoft.wdq.search.biz.SearchHandler;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;
import cn.spinsoft.wdq.widget.SimpleTextRecyclerAdapter;

/**
 * Created by hushujun on 15/11/18.
 */
public class HistoryFrag extends BaseFragment implements Observer, RecyclerItemClickListener {
    private static final String TAG = HistoryFrag.class.getSimpleName();
    private RecyclerView mContainerRv;
    private SimpleTextRecyclerAdapter<String> mHistoryAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_search_history;
    }

    @Override
    protected void initViewAndListener(View root, Bundle savedInstanceState) {
        mContainerRv = (RecyclerView) root.findViewById(R.id.search_history_container);

        mContainerRv.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));
        List<String> hisKeys = DBOperationUtil.getInstance(root.getContext())
                .querySearchRecodes(SearchHandler.Status.pageType.getValue());
        mHistoryAdapter = new SimpleTextRecyclerAdapter<>(hisKeys, this);
        mHistoryAdapter.setAdapterType(Constants.Strings.SEARCH_ADAPTER);
        mContainerRv.setAdapter(mHistoryAdapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DBOperationUtil.getInstance(view.getContext()).addObserver(this);
    }

    @Override
    public void onDestroyView() {
        DBOperationUtil.getInstance(getActivity()).deleteObserver(this);
        super.onDestroyView();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof String && data.equals(SearchHandler.Status.pageType)) {
            List<String> hisKeys = DBOperationUtil.getInstance(getActivity())
                    .querySearchRecodes(SearchHandler.Status.pageType.getValue());
            mHistoryAdapter.setAdapterDataList(hisKeys);
        }
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, int position) {
        String keyWord = mHistoryAdapter.getItem(position);
        SearchHandler.Status.pageIdx = 1;
        ((SearchActivity) getActivity()).doSearch(keyWord);
        ((SearchActivity) getActivity()).setHistoryText(keyWord);
    }
}
