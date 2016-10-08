package cn.spinsoft.wdq.user.widget;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.enums.Attention;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.user.biz.DancerInfo;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by hushujun on 15/12/2.
 */
public class UserItemAdapter extends BaseRecycleAdapter<DancerInfo> {
    private static final String TAG = UserItemAdapter.class.getSimpleName();

    private boolean isCurrOrg = false;

    public UserItemAdapter(List<DancerInfo> adapterDataList, RecyclerItemClickListener itemClickListener) {
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

    public void isCurrOrg(boolean isCurrOrg) {
        this.isCurrOrg = isCurrOrg;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_user_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;

        UserLogin loginUser = SharedPreferencesUtil.getInstance(holder.itemView.getContext()).getLoginUser();
        DancerInfo dancerInfo = adapterDataList.get(position);
        holder.optionBtn.setVisibility(View.VISIBLE);
        if(loginUser!=null && dancerInfo.getUserId()==loginUser.getUserId()){
            holder.optionBtn.setVisibility(View.GONE);
        }
//        Picasso.with(holder.itemView.getContext()).load(dancerInfo.getPhotoUrl()).into(holder.photoImg);
        holder.photoImg.setImageURI(Uri.parse(dancerInfo.getPhotoUrl()));
        holder.nickNameTx.setText(dancerInfo.getNickName());
        holder.signatureTx.setText(dancerInfo.getSignature());

        if (dancerInfo.getAttention() == Attention.INITIATIVE || dancerInfo.getAttention() == Attention.MUTUAL) {
            holder.optionBtn.setImageResource(R.mipmap.ic_userdetail_teacher_atten_true);
            holder.optionBtn.setTag(true);
        } else {
            holder.optionBtn.setImageResource(R.mipmap.ic_userdetail_teacher_atten_false);
            holder.optionBtn.setTag(false);
        }

        if(isCurrOrg){
            holder.optionBtn.setVisibility(View.GONE);
            holder.mFireBtn.setVisibility(View.VISIBLE);
            holder.mFireBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(UserItemAdapter.this, v, holder.getLayoutPosition());
                }
            });
        }

        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(UserItemAdapter.this, v, holder.getLayoutPosition());
                }
            });

            holder.optionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(UserItemAdapter.this, v, holder.getLayoutPosition());
                }
            });
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView photoImg;
        private TextView nickNameTx, signatureTx;
        private ImageView optionBtn,mFireBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            photoImg = (SimpleDraweeView) itemView.findViewById(R.id.user_list_item_photo);
            nickNameTx = (TextView) itemView.findViewById(R.id.user_list_item_nickName);
            signatureTx = (TextView) itemView.findViewById(R.id.user_list_item_signature);
            optionBtn = (ImageView) itemView.findViewById(R.id.user_list_item_option);
            mFireBtn = (ImageView) itemView.findViewById(R.id.user_list_item_fire);
        }
    }
}
