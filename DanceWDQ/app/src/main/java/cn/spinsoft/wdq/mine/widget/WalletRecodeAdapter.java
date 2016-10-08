package cn.spinsoft.wdq.mine.widget;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.mine.biz.WalletRecode;

/**
 * Created by hushujun on 16/1/13.
 */
public class WalletRecodeAdapter extends BaseRecycleAdapter<WalletRecode> {
    private static final String TAG = WalletRecodeAdapter.class.getSimpleName();

    public WalletRecodeAdapter(List<WalletRecode> adapterDataList) {
        super(adapterDataList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_wallet_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        WalletRecode recode = adapterDataList.get(position);
        StringBuffer sb = new StringBuffer("<font color='#632ad2'>" + recode.getTradeName() + "</font>");
        switch (recode.getType()){
            case BALANCEOUT:
                sb.insert(0,"余额赞赏");
                break;
            case WITHDRAW:
                sb.replace(0,sb.length()-1,"提现");
                break;
            case BALANCEIN:
                sb.insert(0,"收到");
                sb.append("的赞赏");
                break;
            case WXOUT:
                sb.insert(0,"微信赞赏");
                break;
            default:
                break;
        }
        holder.reasonTx.setText( Html.fromHtml(sb.toString()));
        holder.dateTx.setText(recode.getCreateTime());
        holder.quantityTx.setText(recode.getQuantity());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView reasonTx, dateTx, quantityTx;

        public ViewHolder(View itemView) {
            super(itemView);
            reasonTx = (TextView) itemView.findViewById(R.id.wallet_item_reason);
            dateTx = (TextView) itemView.findViewById(R.id.wallet_item_date);
            quantityTx = (TextView) itemView.findViewById(R.id.wallet_item_quantity);
        }
    }
}
