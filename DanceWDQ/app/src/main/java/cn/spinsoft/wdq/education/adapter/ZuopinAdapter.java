//package cn.spinsoft.wdq.education.adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.education.bean.Zuopin;
//
///**
// * 作品的适配器
// */
//public class ZuopinAdapter extends BaseAdapter {
//    private Context context;
//    private List<Zuopin.ZUOPIN> videos;
//    private Mholder holder;
//
//    public ZuopinAdapter(Context context) {
//        this.context = context;
//    }
//
//    public void setData(List<Zuopin.ZUOPIN> videos, Boolean is_refresh) {
//        if (is_refresh) {
//            this.videos = videos;
//        } else {
//            if (null == this.videos) {
//                videos = new ArrayList<Zuopin.ZUOPIN>();
//            } else {
//                this.videos.addAll(videos);
//            }
//        }
//    }
//
//    @Override
//    public int getCount() {
//        return null == videos ? 0 : videos.size();
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
//            convertView = View.inflate(context, R.layout.adapter_zuopin, null);
//            holder = new Mholder();
//            holder.iv_zuopin = (ImageView) convertView.findViewById(R.id.iv_zuopin);
//            holder.tv_zuopin_name = (TextView) convertView.findViewById(R.id.tv_zuopin_name);
//            holder.tv_zuopin_shijian = (TextView) convertView.findViewById(R.id.tv_zuopin_shijian);
//            convertView.setTag(holder);
//        } else {
//            holder = (Mholder) convertView.getTag();
//        }
//        for (int i = 0; i < videos.size(); i++) {
//            Picasso.with(context).load(videos.get(position).smallimg).into(holder.iv_zuopin);//替换图片
//            holder.tv_zuopin_name.setText(videos.get(position).title);
//            holder.tv_zuopin_shijian.setText(videos.get(position).createtime);
//            holder.iv_zuopin.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View v) {
//                                                        //点击跳转到视频详情页播放
//                                                        Intent intent = new Intent();
//                                                    }
//                                                }
//            );
//        }
//        return convertView;
//    }
//
//    public class Mholder {
//        TextView tv_zuopin_name, tv_zuopin_shijian;
//        ImageView iv_zuopin;
//
//    }
//}
