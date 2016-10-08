//package cn.spinsoft.wdq.education.adapter;
//
//import android.content.Context;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.GridView;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.education.bean.Dongtai;
//
///**
// * 动态适配器
// */
//public class DongtaiAdapter extends BaseAdapter {
//    private Context context;
//    public List<Dongtai.DONGTAI> dynamics;
//    private Holder holder;
//    private GridViewAdapter gridViewAdapter;
//
//    public DongtaiAdapter(Context context) {
//        this.context = context;
//    }
//
//    public void setData(List<Dongtai.DONGTAI> dynamics, boolean is_refresh) {
//        if (is_refresh) {
//            this.dynamics = dynamics;
//        } else {
//            if (null == this.dynamics) {
//                dynamics = new ArrayList<Dongtai.DONGTAI>();
//            } else {
//                this.dynamics.addAll(dynamics);
//            }
//        }
//    }
//
//    @Override
//    public int getCount() {
//        return null == dynamics ? 0 : dynamics.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return null;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (null == convertView) {
//            convertView = View.inflate(context, R.layout.adapter_dongtai1, null);
//            holder = new Holder();
//            holder.tv_nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
//            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
//            holder.tv_like = (TextView) convertView.findViewById(R.id.tv_like);
//            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
//            holder.tv_dizhi = (TextView) convertView.findViewById(R.id.tv_dizhi);
//            holder.tv_zhuanfa = (TextView) convertView.findViewById(R.id.tv_zhuanfa);
//            holder.tv_huifu = (TextView) convertView.findViewById(R.id.tv_liuyan);
//            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
//            holder.gradview_img = (GridView) convertView.findViewById(R.id.gradview_img);
//            convertView.setTag(holder);
//        } else {
//            holder = (Holder) convertView.getTag();
//        }
//        for (int i = 0; i < dynamics.size(); i++) {
////            holder.tv_nickname.setText(dynamics.get(position).nickname);
//            holder.tv_content.setText(dynamics.get(position).content);
//            holder.tv_like.setText(dynamics.get(position).admireNum);
//            holder.tv_title.setText(dynamics.get(position).title);
//            holder.tv_dizhi.setText(dynamics.get(position).location);
//            holder.tv_zhuanfa.setText(dynamics.get(position).forwardNum);
//            holder.tv_huifu.setText(dynamics.get(position).commentNum);
//            holder.tv_time.setText(dynamics.get(position).createtime);
//            gridViewAdapter = new GridViewAdapter(context, dynamics.get(position).images);
//            holder.gradview_img.setAdapter(gridViewAdapter);
//            gridViewAdapter.notifyDataSetChanged();
//        }
//        return convertView;
//    }
//
//    public class Holder {
//        TextView tv_time, tv_nickname, tv_content, tv_like, tv_title, tv_dizhi, tv_zhuanfa, tv_huifu;
//        private GridView gradview_img;
//    }
//}
