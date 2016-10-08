//package cn.spinsoft.wdq.education.adapter;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.education.bean.Yaoqinglaoshi;
//
///**
// * Created by Administrator on 2015/12/18.
// */
//public class LaoshiAdapter extends BaseAdapter {
//    public List<Yaoqinglaoshi.YQOAINGLAOSHI> teachers;
//    private Context context;
//    HashMap hs = new HashMap();
//    public String dances = "";
//
//    public LaoshiAdapter(Context context) {
//        this.context = context;
//    }
//
//    public void setData(List<Yaoqinglaoshi.YQOAINGLAOSHI> teachers, boolean is_refresh) {
//        if (is_refresh) {
//            this.teachers = teachers;
//        } else {
//            if (null == teachers) {
//                teachers = new ArrayList<Yaoqinglaoshi.YQOAINGLAOSHI>();
//            } else {
//                this.teachers.addAll(teachers);
//            }
//        }
//
//    }
//
//    @Override
//    public int getCount() {
//        return null == teachers ? 0 : teachers.size();
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
//        Mholeder holder = null;
//        if (null == convertView) {
//            convertView = View.inflate(context, R.layout.adapter_yaoqing, null);
//            holder = new Mholeder();
//            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
//            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
//            holder.iv_jiaoshi_touxiang = (ImageView) convertView.findViewById(R.id.iv_jiaoshi_touxiang);
//            convertView.setTag(holder);
//        } else {
//            holder = (Mholeder) convertView.getTag();
//        }
//        Picasso.with(context).load(teachers.get(position).headurl).into(holder.iv_jiaoshi_touxiang);//替换图片
//        holder.tv_name.setText(teachers.get(position).nickname);
//        //选取舞种信息
//        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked == true) {
//                    hs.put(position, teachers.get(position).userid);
//                } else {
//                    hs.remove(position);
//                }
//                Log.e("集合", "onCheckedChanged: " + hs);
//            }
//        });
//
//        return convertView;
//    }
//
//    public class Mholeder {
//        TextView tv_name;
//        CheckBox checkBox;
//        ImageView iv_jiaoshi_touxiang;
//    }
//
//    /**
//     * 获取选择的数据
//     *
//     * @return
//     */
//    public String get() {
//        Iterator iter = hs.entrySet().iterator();
//        while (iter.hasNext()) {
//            Map.Entry entry = (Map.Entry) iter.next();
//            Object key = entry.getKey();
//            Object val = entry.getValue();
//            dances += val + ",";
//        }
//        Log.e("233", "get: " + dances);
//        return dances;
//    }
//
//
//}
