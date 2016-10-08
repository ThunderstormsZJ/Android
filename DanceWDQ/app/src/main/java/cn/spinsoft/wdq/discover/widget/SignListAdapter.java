package cn.spinsoft.wdq.discover.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.discover.biz.SignInfo;

/**
 * Created by zhoujun on 2016-4-13.
 */
public class SignListAdapter extends BaseRecycleAdapter<SignInfo> {
    private final static String TAG = SignListAdapter.class.getSimpleName();

    public SignListAdapter(List<SignInfo> adapterDataList) {
        super(adapterDataList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SignHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_sign_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SignInfo signInfo = adapterDataList.get(position);
        SignHolder signHolder = (SignHolder) holder;

        signHolder.mNameTx.setText(signInfo.getName());
        signHolder.mTelTx.setText(signInfo.getTelphone());
        signHolder.mPersonNumTx.setText(signInfo.getPersonNum());
        signHolder.mRemarkTx.setText(signInfo.getRemark());
    }

    class SignHolder extends RecyclerView.ViewHolder {
        private TextView mNameTx, mTelTx, mPersonNumTx, mRemarkTx;

        public SignHolder(View itemView) {
            super(itemView);

            mNameTx = (TextView) itemView.findViewById(R.id.sign_item_name);
            mTelTx = (TextView) itemView.findViewById(R.id.sign_item_telephone);
            mPersonNumTx = (TextView) itemView.findViewById(R.id.sign_item_personnum);
            mRemarkTx = (TextView) itemView.findViewById(R.id.sign_item_remark);
        }
    }
}
