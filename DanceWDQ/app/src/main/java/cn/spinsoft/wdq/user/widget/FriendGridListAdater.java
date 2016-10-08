package cn.spinsoft.wdq.user.widget;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.enums.Sex;
import cn.spinsoft.wdq.user.biz.DancerInfo;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by zhoujun on 2016-3-29.
 */
public class FriendGridListAdater extends BaseRecycleAdapter<DancerInfo> {
    private static final String TAG = FriendGridListAdater.class.getSimpleName();

    private RecyclerItemClickListener mOnItemClickListener = null;

    public FriendGridListAdater(List<DancerInfo> adapterDataList, RecyclerItemClickListener itemClickListener, Activity activity) {
        super(adapterDataList, itemClickListener, activity);
        mOnItemClickListener = itemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHold(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_friend_grid_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ViewHold viewHold = (ViewHold) holder;
        DancerInfo dancerInfo = adapterDataList.get(position);
        viewHold.mHeadImg.setImageURI(Uri.parse(dancerInfo.getPhotoUrl()));
      /*  if(!TextUtils.isEmpty(dancerInfo.getPhotoUrl())){
            if(viewHold.mHeadImg.getTag()!=null){
                if(!dancerInfo.getPhotoUrl().equals(viewHold.mHeadImg.getTag())){
                    viewHold.mHeadImg.setImageURI(Uri.parse(dancerInfo.getPhotoUrl()));
                    viewHold.mHeadImg.setTag(dancerInfo.getPhotoUrl());
                }
            }else {
                viewHold.mHeadImg.setImageURI(Uri.parse(dancerInfo.getPhotoUrl()));
                viewHold.mHeadImg.setTag(dancerInfo.getPhotoUrl());
            }
        }*/
        viewHold.mNickNameTx.setText(dancerInfo.getNickName());
        viewHold.mAdmireTx.setText(String.valueOf(dancerInfo.getAdmireCount()));
        if(dancerInfo.getSex()== Sex.FEMALE){
            viewHold.mGenderIv.setImageResource(R.mipmap.user_gender_woman);
        }else {
            viewHold.mGenderIv.setImageResource(R.mipmap.user_gender_man);
        }

        String distance = dancerInfo.getDistance();
        if ("null".equals(dancerInfo.getDistance())) {
            distance = "距离:不详";
        } else {
            Double distanceD = Double.valueOf(distance);
            if (distanceD > 1000) {
                distanceD = distanceD / 1000;
                distance = "距离: " + String.format("%.2fkm", distanceD);
            } else {
                distance = "距离: " + String.format("%.0fm", distanceD);
            }
        }
        viewHold.mLocationTx.setText(distance);

        viewHold.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClicked(FriendGridListAdater.this, v, holder.getLayoutPosition());
                }
            }
        });
    }

    class ViewHold extends RecyclerView.ViewHolder {
        private SimpleDraweeView mHeadImg;
        private TextView mAdmireTx, mLocationTx, mNickNameTx;
        private ImageView mGenderIv;

        public ViewHold(View itemView) {
            super(itemView);
            mHeadImg = (SimpleDraweeView) itemView.findViewById(R.id.friend_list_item_head);
            mLocationTx = (TextView) itemView.findViewById(R.id.friend_list_item_location);
            mNickNameTx = (TextView) itemView.findViewById(R.id.friend_list_item_nickname);
            mAdmireTx = (TextView) itemView.findViewById(R.id.friend_list_item_admireCount);
            mGenderIv = (ImageView) itemView.findViewById(R.id.friend_list_item_gender);

            int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width / 2, width / 2);
            lp.leftMargin = 5;
            lp.rightMargin = 5;
            lp.bottomMargin = 10;
            itemView.setLayoutParams(lp);
        }

    }
}
