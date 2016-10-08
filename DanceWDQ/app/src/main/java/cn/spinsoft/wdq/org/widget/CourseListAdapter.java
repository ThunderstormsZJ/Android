package cn.spinsoft.wdq.org.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseRecycleAdapter;
import cn.spinsoft.wdq.org.biz.CourseBean;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by hushujun on 16/1/8.
 */
public class CourseListAdapter extends BaseRecycleAdapter<CourseBean> {
    private static final String TAG = CourseListAdapter.class.getSimpleName();
    private String listType = Constants.Strings.NORMAL_MODE;

    public void setListType(String listType){
        this.listType = listType;
    }

    public CourseListAdapter(List<CourseBean> adapterDataList, RecyclerItemClickListener itemClickListener) {
        super(adapterDataList, itemClickListener);
    }

    /**
     * 只有在预约或取消预约操作成功后方可调次方法
     *
     * @param position
     */
    public void notifyItemStatusChanged(int position) {
        if (position >= 0 && position < getItemCount()) {
            CourseBean courseBean = adapterDataList.get(position);
            courseBean.setState(courseBean.getState() == 0 ? 1 : 0);
            notifyItemChanged(position);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ly_org_course_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        CourseBean courseBean = adapterDataList.get(position);
        holder.courseNameTx.setText(courseBean.getCourseName());
        if(listType==Constants.Strings.DELETE_MODE){
            holder.optionImg.setImageResource(R.mipmap.ic_userdetail_delete);
            holder.optionImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(CourseListAdapter.this, v, holder.getLayoutPosition());
                }
            });
        }else {
            holder.optionImg.setImageResource(R.mipmap.ic_userdetail_order);
            if (itemClickListener != null) {
                holder.optionImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(CourseListAdapter.this, v, holder.getLayoutPosition());
                    }
                });
            }
            /*if (courseBean.getState() == 0) {

            } else {
                holder.optionImg.setImageResource(R.mipmap.ic_userdetail_ordered);
            }*/
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView courseNameTx;
        private ImageView optionImg;

        public ViewHolder(View itemView) {
            super(itemView);
            courseNameTx = (TextView) itemView.findViewById(R.id.org_course_item_name);
            optionImg = (ImageView) itemView.findViewById(R.id.org_course_item_opt);
        }
    }
}
