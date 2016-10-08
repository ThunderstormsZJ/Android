package cn.spinsoft.wdq.mine.widget;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.enums.AttestState;
import cn.spinsoft.wdq.mine.biz.SimpleUserBean;
import cn.spinsoft.wdq.org.OrgDetailsActivity;
import cn.spinsoft.wdq.user.UserDetailsActivity;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.StringUtils;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by zhoujun on 15/12/31.
 */
public class SimpleListAdapter extends BaseRecycleAdapter<SimpleUserBean> {
    private static final String TAG = SimpleListAdapter.class.getSimpleName();

    private List<Integer> choicedIds = new ArrayList<>();
    private List<Integer> choicedOrgIds = new ArrayList<>();
    private List<Integer> checkedPositions = new ArrayList<>();
    private boolean isChoiceMode = true;//true  单选模式  false  选择模式

    public SimpleListAdapter(List<SimpleUserBean> adapterDataList) {
        super(adapterDataList);
    }

    public SimpleListAdapter(List<SimpleUserBean> adapterDataList, RecyclerItemClickListener itemClickListener, Activity activity) {
        super(adapterDataList, itemClickListener, activity);
        if (itemClickListener != null) {
            isChoiceMode = false;
        }
    }

    @Override
    public void setAdapterDataList(List<SimpleUserBean> adapterDataList) {
        super.setAdapterDataList(adapterDataList);
        if (checkedPositions != null) {
            checkedPositions.clear();
        }
        if (choicedIds != null) {
            choicedIds.clear();
            choicedOrgIds.clear();
        }
    }

    public String getChoicedIds() {
        return StringUtils.listContentToString(choicedIds);
    }

    public String getChoicedOrgIds() {
        return StringUtils.listContentToString(choicedOrgIds);
    }

    public void setChoicedOrgIds(List<Integer> choicedOrgIds) {
        this.choicedOrgIds = choicedOrgIds;
    }

    /**
     * @param position
     */
    public void notifyUserStateChanged(int position, AttestState state) {
        if (position >= 0 && position < getItemCount()) {
            adapterDataList.get(position).setState(state);
            notifyItemChanged(position);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isChoiceMode) {
            return new ChoiceHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_simple_user_item, parent, false));
        } else {
            return new OptionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_simple_user_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        final SimpleUserBean infoSimple = adapterDataList.get(position);
        holder.logoSdv.setImageURI(Uri.parse(infoSimple.getPhotoUrl()));
        holder.nameTx.setText(infoSimple.getNickName());

        holder.logoSdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uDIntent = null;
                if (infoSimple.getOrgId() > 0) {
                    uDIntent = new Intent(getActivity(), OrgDetailsActivity.class);
                    uDIntent.putExtra(Constants.Strings.ORG_ID, infoSimple.getOrgId());
                    uDIntent.putExtra(Constants.Strings.USER_ID, infoSimple.getUserId());
                    uDIntent.putExtra(Constants.Strings.ORG_NAME, infoSimple.getNickName());
                    uDIntent.putExtra(Constants.Strings.ORG_LOGO, infoSimple.getPhotoUrl());
                } else {
                    uDIntent = new Intent(getActivity(), UserDetailsActivity.class);
                    uDIntent.putExtra(Constants.Strings.USER_ID, infoSimple.getUserId());
                    uDIntent.putExtra(Constants.Strings.USER_NAME, infoSimple.getNickName());
                    uDIntent.putExtra(Constants.Strings.USER_PHOTO, infoSimple.getPhotoUrl());
                }
                getActivity().startActivity(uDIntent);
            }
        });
        if (isChoiceMode) {
            final ChoiceHolder cHolder = (ChoiceHolder) holder;
            cHolder.choiceCb.setChecked(infoSimple.getState() == AttestState.CONFIRMED || checkedPositions.contains(position));
            cHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cHolder.choiceCb.isChecked()) {
                        cHolder.choiceCb.setChecked(false);
                    } else {
                        cHolder.choiceCb.setChecked(true);
                    }
                }
            });
            cHolder.choiceCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        checkedPositions.add(cHolder.getLayoutPosition());
                        choicedIds.add(infoSimple.getUserId());
                        choicedOrgIds.add(infoSimple.getOrgId());
                    } else {
                        checkedPositions.remove(Integer.valueOf(cHolder.getLayoutPosition()));
                        choicedIds.remove(Integer.valueOf(infoSimple.getUserId()));
                        choicedOrgIds.remove(Integer.valueOf(infoSimple.getOrgId()));
                    }
                }
            });
        } else {
            final OptionHolder oHolder = (OptionHolder) holder;
            AttestState state = infoSimple.getState();
            if (state == AttestState.REJECTED) {
                oHolder.confirmImg.setVisibility(View.INVISIBLE);
                oHolder.refuseImg.setVisibility(View.VISIBLE);
                oHolder.refuseImg.setBackgroundResource(R.mipmap.booking_item_refused);
                oHolder.refuseImg.setEnabled(false);
            } else if (state == AttestState.CONFIRMED) {
                oHolder.confirmImg.setVisibility(View.INVISIBLE);
                oHolder.refuseImg.setVisibility(View.VISIBLE);
                oHolder.refuseImg.setBackgroundResource(R.mipmap.booking_item_confirmed);
                oHolder.refuseImg.setEnabled(false);
            } else if (state == AttestState.INVITE) {
                LogUtil.e(TAG, "ERR,user was been invited must not be appeared at here,recode id=" + infoSimple.getRecodeId());
                oHolder.confirmImg.setVisibility(View.INVISIBLE);
                oHolder.refuseImg.setVisibility(View.INVISIBLE);
            } else {
                oHolder.confirmImg.setVisibility(View.VISIBLE);
                oHolder.refuseImg.setVisibility(View.VISIBLE);
                oHolder.refuseImg.setEnabled(true);
                oHolder.confirmImg.setBackgroundResource(R.mipmap.booking_item_confirm);
                oHolder.refuseImg.setBackgroundResource(R.mipmap.booking_item_refuse);
                if (itemClickListener != null) {
                    oHolder.confirmImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClickListener.onItemClicked(SimpleListAdapter.this, v, oHolder.getLayoutPosition());
                        }
                    });
                    oHolder.refuseImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClickListener.onItemClicked(SimpleListAdapter.this, v, oHolder.getLayoutPosition());
                        }
                    });
                }
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView logoSdv;
        private TextView nameTx;
        protected ViewStub differentVs;

        public ViewHolder(View itemView) {
            super(itemView);
            logoSdv = (SimpleDraweeView) itemView.findViewById(R.id.simple_user_item_logo);
            nameTx = (TextView) itemView.findViewById(R.id.simple_user_item_name);
            differentVs = (ViewStub) itemView.findViewById(R.id.simple_user_item_different);
        }
    }

    class ChoiceHolder extends ViewHolder {
        private CheckBox choiceCb;

        public ChoiceHolder(View itemView) {
            super(itemView);
            differentVs.setLayoutResource(R.layout.ly_simple_user_chocie);
            View view = differentVs.inflate();
            choiceCb = (CheckBox) view.findViewById(R.id.simple_user_item_cb);
        }
    }

    class OptionHolder extends ViewHolder {
        private ImageView confirmImg, refuseImg;

        public OptionHolder(View itemView) {
            super(itemView);
            differentVs.setLayoutResource(R.layout.ly_simple_user_option);
            View view = differentVs.inflate();
            confirmImg = (ImageView) view.findViewById(R.id.simple_user_item_confirm);
            refuseImg = (ImageView) view.findViewById(R.id.simple_user_item_refuse);
        }
    }
}
