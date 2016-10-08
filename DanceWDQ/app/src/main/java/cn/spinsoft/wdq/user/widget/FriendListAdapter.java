package cn.spinsoft.wdq.user.widget;

import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.enums.Attention;
import cn.spinsoft.wdq.user.biz.DancerInfo;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by hushujun on 15/11/18.
 */
public class FriendListAdapter extends BaseRecycleAdapter<DancerInfo> {
    private static final String TAG = FriendListAdapter.class.getSimpleName();

    private int[] labelBg = new int[]{R.drawable.label_blue, R.drawable.label_red,
            R.drawable.label_red, R.drawable.label_yellow};

    public FriendListAdapter(List<DancerInfo> adapterDataList, RecyclerItemClickListener itemClickListener) {
        super(adapterDataList, itemClickListener);
    }

    public void notifyItemAttentionChanged(boolean attention, int position) {
        if (position < getItemCount()) {
            DancerInfo dancerInfo = adapterDataList.get(position);
            if (attention) {
                if (!dancerInfo.getAttention().isAttented())
                    dancerInfo.setAttention(Attention.getEnum(dancerInfo.getAttention().getValue() + 1));
            } else {
                if (dancerInfo.getAttention().isAttented())
                    dancerInfo.setAttention(Attention.getEnum(dancerInfo.getAttention().getValue() - 1));
            }
            notifyItemChanged(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_friend_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        DancerInfo dancerInfo = adapterDataList.get(position);

        holder.photoCImg.setImageURI(Uri.parse(dancerInfo.getPhotoUrl()));
        holder.nickNameTx.setText(dancerInfo.getNickName());

        String signature = dancerInfo.getSignature();
        if (signature.equals("null")) {
            signature = "";
        }
        holder.signatureTx.setText(signature);

        String distance = dancerInfo.getDistance();
        if (distance.equals("null")) {
            distance = "距离:不详";
        } else {
            distance = "距离:" + distance + " 米";
        }
        holder.distanceTx.setText(distance);

        if (dancerInfo.getAttention().isAttented()) {
            holder.optionBtn.setImageResource(R.mipmap.friend_delete);
            holder.optionBtn.setTag(true);
        } else {
            holder.optionBtn.setImageResource(R.mipmap.friend_add);
            holder.optionBtn.setTag(false);
        }

        holder.labelsLL.removeAllViews();
        List<String> labels = dancerInfo.getLabels();
        if (labels != null && !labels.isEmpty()) {
            int size = labels.size() > 4 ? 4 : labels.size();
            LinearLayout.LayoutParams lp = null;
            for (int i = 0; i < size; i++) {
                TextView textView = new TextView(holder.itemView.getContext());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 18);
                textView.setTextColor(Color.WHITE);
                textView.setText(labels.get(i));
                textView.setBackgroundResource(labelBg[i]);
                lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.leftMargin = 5;
                lp.rightMargin = 5;
                lp.gravity = Gravity.CENTER_VERTICAL;
                holder.labelsLL.addView(textView, lp);
            }
        }

        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(FriendListAdapter.this, v, holder.getLayoutPosition());
                }
            });
//            holder.photoCImg.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mListener.onItemClicked(FriendListAdapter.this, v, holder.getLayoutPosition());
//                }
//            });
            holder.optionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(FriendListAdapter.this, v, holder.getLayoutPosition());
                }
            });
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView photoCImg;
        private TextView nickNameTx, signatureTx, distanceTx;
        private ImageView optionBtn;
        private LinearLayout labelsLL;

        public ViewHolder(View itemView) {
            super(itemView);
            photoCImg = (SimpleDraweeView) itemView.findViewById(R.id.friend_list_item_photo);
            nickNameTx = (TextView) itemView.findViewById(R.id.friend_list_item_nickName);
            signatureTx = (TextView) itemView.findViewById(R.id.friend_list_item_signature);
            distanceTx = (TextView) itemView.findViewById(R.id.friend_list_item_distance);
            optionBtn = (ImageView) itemView.findViewById(R.id.friend_list_item_option);
            labelsLL = (LinearLayout) itemView.findViewById(R.id.friend_list_item_labels);
        }
    }
}
