//package cn.spinsoft.wdq.education.adapter;
//
//import android.content.Context;
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
//import cn.spinsoft.wdq.education.activity.AboutmeActivity;
//import cn.spinsoft.wdq.education.bean.Yuwoxiangguan;
//
///**
// * Created by Administrator on 2015/12/17.关于我的适配器
// */
//public class AboutmeAdapter extends BaseAdapter {
//    private Context context;
//    public List<Yuwoxiangguan.Related> related;
//
//    public AboutmeAdapter(Context context) {
//        this.context = context;
//    }
//
//    public void setData(List<Yuwoxiangguan.Related> related, Boolean is_refresh) {
//        if (is_refresh) {
//            this.related = related;
//        } else {
//            if (null == this.related) {
//                related = new ArrayList<Yuwoxiangguan.Related>();
//            } else {
//                this.related.addAll(related);
//            }
//        }
//    }
//
//    @Override
//    public int getCount() {
//        return null == related ? 0 : related.size();
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
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        Mholeder holder;
//        if (null == convertView) {
//            convertView = View.inflate(context, R.layout.adapter_aboutme, null);
//            holder = new Mholeder();
//            holder.iv_touxiang = (ImageView) convertView.findViewById(R.id.iv_touxiang);//发起人头像
//            holder.iv_beigaotouxiang = (ImageView) convertView.findViewById(R.id.iv_beigaotouxiang);//被告头像
//            holder.tv_faqiren = (TextView) convertView.findViewById(R.id.tv_faqiren);//发起人
//            holder.tv_shijian = (TextView) convertView.findViewById(R.id.tv_shijian);//时间
//            holder.tv_huifu = (TextView) convertView.findViewById(R.id.tv_huifu);//回复
//            holder.tv_pinglun = (TextView) convertView.findViewById(R.id.tv_pinglun);//评论
//            holder.tv_beigao = (TextView) convertView.findViewById(R.id.tv_beigao);//被告
//            holder.tv_beigaoci = (TextView) convertView.findViewById(R.id.tv_beigaoci);//
//            holder.tv_zan = (TextView) convertView.findViewById(R.id.tv_zan);//赞
//            convertView.setTag(holder);
//        } else {
//            holder = (Mholeder) convertView.getTag();
//        }
//        Picasso.with(context).load(related.get(position).cuhu).into(holder.iv_touxiang);
//
//        if (related.get(position).headurl == null) {
//            holder.iv_beigaotouxiang.setVisibility(View.GONE);
//        } else {
//            Picasso.with(context).load(related.get(position).headurl).into(holder.iv_beigaotouxiang);
//        }
//        holder.tv_faqiren.setText(related.get(position).cuname);
//        holder.tv_shijian.setText(related.get(position).createtime);
//        holder.tv_pinglun.setText(related.get(position).content);
//        holder.tv_beigao.setText(related.get(position).nickname + ":");
//        holder.tv_beigaoci.setText(related.get(position).objcontent);
//        //类型1:活动点赞;2:活动评论;;3:活动转发;5:招聘点赞;6:招聘评论;7:招聘转发;8:比赛点赞;
//        // 9:比赛评论;10:比赛转发; 12:话题点赞;13:话题评论;14:话题转发;15:其他点赞;16:其他评论;17:其他转发
//        if (related.get(position).type == 1 || related.get(position).type == 5 || related.get(position).type == 8 ||
//                related.get(position).type == 12 || related.get(position).type == 15) {
//            holder.tv_zan.setText("赞了我");
//        } else {
//            holder.tv_zan.setText("");
//        }
//
//        //处理回复时间
//        holder.tv_huifu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((AboutmeActivity) context).huifu(related.get(position).type, related.get(position).userid, related.get(position).theuserid,
//                        related.get(position).objectid);
//            }
//        });
//
//
//        return convertView;
//    }
//
//    public class Mholeder {
//        ImageView iv_touxiang, iv_beigaotouxiang;
//        TextView tv_faqiren, tv_shijian, tv_huifu, tv_pinglun, tv_beigao, tv_beigaoci, tv_zan;
//    }
//}
