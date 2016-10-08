//package cn.spinsoft.wdq.education.adapter;
//
//import android.content.Context;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.education.bean.Qianbao;
//
///**
// * Created by Administrator on 2015/12/11. 钱包的适配器
// */
//public class QianbaoAdapter extends BaseAdapter {
//    private Context context;
//    public List<Qianbao.Page> page;
//    private Holder holder;
//
//
//    public QianbaoAdapter(Context context) {
//        this.context = context;
//    }
//
//    public void setData(List<Qianbao.Page> page, Boolean is_refresh) {
//        if (is_refresh) {
//            this.page = page;
//        } else {
//            if (null == this.page) {
//                page = new ArrayList<Qianbao.Page>();
//            } else {
//                this.page.addAll(page);
//            }
//        }
//
//
//    }
//
//
//    @Override
//    public int getCount() {
//        return null == page ? 0 : page.size();
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
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        if (null == convertView) {
//            convertView = View.inflate(context, R.layout.adapter_qianbao, null);
//            holder = new Holder();
//            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
//            holder.tv_shijian = (TextView) convertView.findViewById(R.id.tv_shijian);
//            holder.tv_qian = (TextView) convertView.findViewById(R.id.tv_qian);
//
//            convertView.setTag(holder);
//        } else {
//            holder = (Holder) convertView.getTag();
//        }
//
//        for (int i = 0; i < page.size(); i++) {
//            if (page.get(position).type == 1) {
//                holder.tv_name.setText("收到赞赏");
//            } else {
//                holder.tv_name.setText("提现");
//            }
//            holder.tv_shijian.setText(page.get(position).createtime + "");
//            holder.tv_qian.setText(page.get(position).money + "");
//
//        }
//        return convertView;
//    }
//
//    public class Holder {
//        TextView tv_name, tv_shijian, tv_qian;
//    }
//}
