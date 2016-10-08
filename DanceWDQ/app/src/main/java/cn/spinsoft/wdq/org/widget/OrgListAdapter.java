package cn.spinsoft.wdq.org.widget;

import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.org.biz.OrgInfo;
import cn.spinsoft.wdq.org.frag.OrgListFrag;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by zhoujun on 16/1/6.
 */
public class OrgListAdapter extends BaseRecycleAdapter<OrgInfo> {
    private static final String TAG = OrgListFrag.class.getSimpleName();

    private int[] labelBg = new int[]{R.drawable.label_blue, R.drawable.label_red,
            R.drawable.label_red, R.drawable.label_yellow};

    public OrgListAdapter(List<OrgInfo> adapterDataList, RecyclerItemClickListener itemClickListener) {
        super(adapterDataList, itemClickListener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_org_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        OrgInfo orgInfo = adapterDataList.get(position);
        holder.photoSdv.setImageURI(Uri.parse(orgInfo.getPhotoUrl()));
        holder.teacherNumTx.setText(orgInfo.getTeacherCount() + " 位老师");
        holder.viewNumTx.setText(String.valueOf(orgInfo.getPageViews()));
        holder.orgNameTx.setText(orgInfo.getOrgName());
        String address = orgInfo.getAddress();
        holder.addressTx.setText("位置：" + address);
        double distance = orgInfo.getDistance();
        if (distance <= 1000) {
            holder.distanceTx.setText("距离：" + String.format("%.0fm", distance));
        } else {
            distance = distance / 1000;
            holder.distanceTx.setText("距离：" + String.format("%.2fkm", distance));
        }

        holder.labelsLl.removeAllViews();
        List<String> labels = orgInfo.getLabels();
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
                holder.labelsLl.addView(textView, lp);
            }
        }

        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(OrgListAdapter.this, v, holder.getLayoutPosition());
                }
            });
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView photoSdv;
        private TextView teacherNumTx, viewNumTx, orgNameTx, addressTx, distanceTx;
        private LinearLayout labelsLl;

        public ViewHolder(View itemView) {
            super(itemView);
            photoSdv = (SimpleDraweeView) itemView.findViewById(R.id.org_list_item_photo);
            teacherNumTx = (TextView) itemView.findViewById(R.id.org_list_item_teacherNUm);
            viewNumTx = (TextView) itemView.findViewById(R.id.org_list_item_viewNum);
            orgNameTx = (TextView) itemView.findViewById(R.id.org_list_item_orgName);
            addressTx = (TextView) itemView.findViewById(R.id.org_list_item_address);
            distanceTx = (TextView) itemView.findViewById(R.id.org_list_item_distance);
            labelsLl = (LinearLayout) itemView.findViewById(R.id.org_list_item_labels);
        }
    }
}
