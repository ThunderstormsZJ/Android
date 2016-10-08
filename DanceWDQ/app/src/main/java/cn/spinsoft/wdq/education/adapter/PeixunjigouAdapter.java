//package cn.spinsoft.wdq.education.adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.education.activity.SchoolActivity;
//import cn.spinsoft.wdq.education.bean.Jigouliebiao;
//
//
///**
// * Created by QUCHUANGYE  机构的适配器 on 2015/11/27.
// */
//public class PeixunjigouAdapter extends BaseAdapter {
//    private Context context;
//    private Myholder myholder;
//    private List<Jigouliebiao.DATA> list;
//
//    public PeixunjigouAdapter(Context context) {
//        this.context = context;
//    }
//
//    public void setData(List<Jigouliebiao.DATA> list, boolean is_refresh) {
//        if (is_refresh) {
//            this.list = list;
//        } else {
//            if (null == this.list) {
//                list = new ArrayList<Jigouliebiao.DATA>();
//            } else {
//                this.list.addAll(list);
//            }
//        }
//
//
//    }
//
//
//    @Override
//    public int getCount() {
//        return null == list ? 0 : list.size();
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
//            convertView = View.inflate(context, R.layout.adapter_jigou, null);
//            myholder = new Myholder();
//            myholder.iv_school = (ImageView) convertView.findViewById(R.id.iv_jiaoshi_touxiang);
//            myholder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
//            myholder.tv_teacher = (TextView) convertView.findViewById(R.id.tv_teacher);
//            myholder.tv_guanzhu = (TextView) convertView.findViewById(R.id.tv_guanzhu);
//            myholder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
//            myholder.bt_1 = (Button) convertView.findViewById(R.id.bt_1);
//            myholder.bt_2 = (Button) convertView.findViewById(R.id.bt_2);
//            myholder.bt_3 = (Button) convertView.findViewById(R.id.bt_3);
//            myholder.bt_4 = (Button) convertView.findViewById(R.id.bt_4);
//            myholder.bt_5 = (Button) convertView.findViewById(R.id.bt_5);
//            convertView.setTag(myholder);
//        } else {
//            myholder = (Myholder) convertView.getTag();
//        }
//        for (int i = 0; i < list.size(); i++) {
//            myholder.tv_address.setText(list.get(position).address);
//            myholder.tv_name.setText(list.get(position).orgname);
//            myholder.tv_teacher.setText(list.get(position).teachercount + "位老师");
//            myholder.tv_guanzhu.setText(list.get(position).pageview);
//            if (null == list.get(position).dancenames) {
//                myholder.bt_1.setVisibility(View.GONE);
//            } else {
//                if (list.get(position).dancenames.size() == 1) {
//                    myholder.bt_1.setVisibility(View.VISIBLE);
//                    myholder.bt_1.setText(list.get(position).dancenames.get(0));
//                }
//                if (list.get(position).dancenames.size() == 2) {
//                    myholder.bt_1.setText(list.get(position).dancenames.get(0));
//                    myholder.bt_1.setVisibility(View.VISIBLE);
//                    myholder.bt_2.setVisibility(View.VISIBLE);
//                    myholder.bt_2.setText(list.get(position).dancenames.get(1));
//                }
//                if (list.get(position).dancenames.size() == 3) {
//                    myholder.bt_1.setVisibility(View.VISIBLE);
//                    myholder.bt_2.setVisibility(View.VISIBLE);
//                    myholder.bt_3.setVisibility(View.VISIBLE);
//                    myholder.bt_1.setText(list.get(position).dancenames.get(0));
//                    myholder.bt_2.setText(list.get(position).dancenames.get(1));
//                    myholder.bt_3.setText(list.get(position).dancenames.get(2));
//
//                }
//                if (list.get(position).dancenames.size() == 4) {
//                    myholder.bt_1.setVisibility(View.VISIBLE);
//                    myholder.bt_2.setVisibility(View.VISIBLE);
//                    myholder.bt_3.setVisibility(View.VISIBLE);
//                    myholder.bt_4.setVisibility(View.VISIBLE);
//                    myholder.bt_1.setText(list.get(position).dancenames.get(0));
//                    myholder.bt_2.setText(list.get(position).dancenames.get(1));
//                    myholder.bt_3.setText(list.get(position).dancenames.get(2));
//                    myholder.bt_4.setText(list.get(position).dancenames.get(3));
//                }
//                if (list.get(position).dancenames.size() >= 5) {
//                    myholder.bt_1.setVisibility(View.VISIBLE);
//                    myholder.bt_2.setVisibility(View.VISIBLE);
//                    myholder.bt_3.setVisibility(View.VISIBLE);
//                    myholder.bt_4.setVisibility(View.VISIBLE);
//                    myholder.bt_5.setVisibility(View.VISIBLE);
//                    myholder.bt_1.setText(list.get(position).dancenames.get(0));
//                    myholder.bt_2.setText(list.get(position).dancenames.get(1));
//                    myholder.bt_3.setText(list.get(position).dancenames.get(2));
//                    myholder.bt_4.setText(list.get(position).dancenames.get(3));
//                    myholder.bt_5.setText(list.get(position).dancenames.get(4));
////                    myholder.bt_1.setBackgroundColor(Color.parseColor("#ff867d"));
////                    myholder.bt_2.setBackgroundColor(Color.parseColor("#7dd6ff"));
////                    myholder.bt_3.setBackgroundColor(Color.parseColor("#ffc67d"));
////                    myholder.bt_4.setBackgroundColor(Color.parseColor("#ff867d"));
////                    myholder.bt_5.setBackgroundColor(Color.parseColor("#ffc67d"));
//                }
//            }
//
//            Picasso.with(context).load(list.get(position).headurl).into(myholder.iv_school);//替换图片
//            myholder.iv_school.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context.getApplicationContext(), SchoolActivity.class);
//                    intent.putExtra("orgid", list.get(position).orgid);
//                    context.startActivity(intent);
//                }
//
//            });
//        }
//
//        return convertView;
//    }
//
//    public class Myholder {
//        public ImageView iv_school;
//        public TextView tv_name, tv_address, tv_teacher, tv_guanzhu;
//        public Button bt_1, bt_2, bt_3, bt_4, bt_5;
//
//    }
//
//}
