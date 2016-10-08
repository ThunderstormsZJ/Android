package cn.spinsoft.wdq.mine.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.enums.BookingState;
import cn.spinsoft.wdq.mine.biz.BookingCourseBean;
import cn.spinsoft.wdq.mine.biz.OrgOrderBean;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by hushujun on 16/1/5.
 */
public class MineBookingAdapter extends BaseRecycleAdapter<BookingCourseBean> {
    private static final String TAG = MineBookingAdapter.class.getSimpleName();
    private boolean isOrg = false;

//    public MineBookingAdapter(List<BookingCourseBean> adapterDataList, RecyclerItemClickListener itemClickListener) {
//        super(adapterDataList, itemClickListener);
//    }

    public MineBookingAdapter(List<BookingCourseBean> adapterDataList, RecyclerItemClickListener itemClickListener, boolean isOrg) {
        super(adapterDataList, itemClickListener);
        this.isOrg = isOrg;
    }

    /**
     * @param position
     */
    public void notifyBookingStateChanged(int position, BookingState state) {
        if (position >= 0 && position < getItemCount()) {
            adapterDataList.get(position).setState(state);
            notifyItemChanged(position);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isOrg) {
            return new OrgHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_mine_booking_item, parent, false));
        } else {
            return new PersonHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_mine_booking_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        BookingCourseBean courseBean = adapterDataList.get(position);
//        holder.stateTx.setText(courseBean.getState().getName());
        switch (courseBean.getState()){
            case CONFIRMED:
                holder.stateImg.setImageResource(R.mipmap.order_success);
                break;
            case REJECTED:
                holder.stateImg.setImageResource(R.mipmap.order_org_reject);
                break;
            case CANCELED:
                holder.stateImg.setImageResource(R.mipmap.oreder_cancleed);
                break;
            default:
                holder.stateImg.setImageResource(R.mipmap.order_org_wait_confirm);
                break;
        }
        holder.courseTx.setText(courseBean.getCourseName());
        holder.timeTx.setText("预约时间: " + courseBean.getOrderTime());

        if (courseBean instanceof OrgOrderBean) {
            OrgOrderBean orderBean = (OrgOrderBean) courseBean;
            holder.orgNameTx.setText(orderBean.getCreateTime());
            holder.orgNameTx.setCompoundDrawables(null, null, null, null);
            final OrgHolder oHolder = (OrgHolder) holder;
            oHolder.nameTx.setText(orderBean.getUserName());
            oHolder.phoneTx.setText(orderBean.getUserPhone());
            BookingState state = orderBean.getState();
            if (state == BookingState.REJECTED) {
                oHolder.confirmImg.setVisibility(View.INVISIBLE);
                oHolder.refuseImg.setVisibility(View.VISIBLE);
                oHolder.refuseImg.setBackgroundResource(R.mipmap.booking_item_refused);
                oHolder.refuseImg.setEnabled(false);
            } else if (state == BookingState.CONFIRMED) {
                oHolder.confirmImg.setVisibility(View.INVISIBLE);
                oHolder.refuseImg.setVisibility(View.INVISIBLE);
                oHolder.refuseImg.setBackgroundResource(R.mipmap.booking_item_confirmed);
                oHolder.refuseImg.setEnabled(false);
            } else if (state == BookingState.CANCELED) {
//                LogUtil.e(TAG, "ERR,course order has been canceled must not be appeared at here,order id=" + orderBean.getCourseId());
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
                            itemClickListener.onItemClicked(MineBookingAdapter.this, v, oHolder.getLayoutPosition());
                        }
                    });
                    oHolder.refuseImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClickListener.onItemClicked(MineBookingAdapter.this, v, oHolder.getLayoutPosition());
                        }
                    });
                }
            }
        } else {
            holder.orgNameTx.setText(courseBean.getOrgName());
            final PersonHolder pHolder = (PersonHolder) holder;
            if (courseBean.getState() == BookingState.REJECTED || courseBean.getState() == BookingState.CONFIRMED
                    || courseBean.getState() == BookingState.CANCELED) {
                pHolder.cancelImg.setEnabled(false);
            } else {
                pHolder.cancelImg.setEnabled(true);
                if (itemClickListener != null) {
                    pHolder.cancelImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClickListener.onItemClicked(MineBookingAdapter.this, v, pHolder.getLayoutPosition());
                        }
                    });
                }
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView stateImg;
        private TextView orgNameTx, /*stateTx,*/ courseTx, timeTx;
        protected ViewStub differentVs;

        public ViewHolder(View itemView) {
            super(itemView);
            orgNameTx = (TextView) itemView.findViewById(R.id.mine_booking_item_orgName);
//            stateTx = (TextView) itemView.findViewById(R.id.mine_booking_item_state);
            stateImg = (ImageView) itemView.findViewById(R.id.mine_booking_item_state);
            courseTx = (TextView) itemView.findViewById(R.id.mine_booking_item_course);
            timeTx = (TextView) itemView.findViewById(R.id.mine_booking_item_time);
            differentVs = (ViewStub) itemView.findViewById(R.id.mine_booking_item_different);
        }
    }

    class PersonHolder extends ViewHolder {
        private ImageView cancelImg;

        public PersonHolder(View itemView) {
            super(itemView);
            differentVs.setLayoutResource(R.layout.ly_booking_item_personal);
            cancelImg = (ImageView) differentVs.inflate().findViewById(R.id.mine_booking_item_person_cancel);
        }
    }

    class OrgHolder extends ViewHolder {
        private TextView nameTx, phoneTx;
        private ImageView confirmImg, refuseImg;

        public OrgHolder(View itemView) {
            super(itemView);
            differentVs.setLayoutResource(R.layout.ly_booking_item_org);
            View view = differentVs.inflate();
            nameTx = (TextView) view.findViewById(R.id.booking_item_org_name);
            phoneTx = (TextView) view.findViewById(R.id.booking_item_org_phone);
            confirmImg = (ImageView) view.findViewById(R.id.booking_item_org_confirm);
            refuseImg = (ImageView) view.findViewById(R.id.booking_item_org_refuse);
        }
    }
}
