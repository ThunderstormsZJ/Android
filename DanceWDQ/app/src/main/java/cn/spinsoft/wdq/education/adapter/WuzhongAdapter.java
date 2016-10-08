//package cn.spinsoft.wdq.education.adapter;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.TextView;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.education.bean.Wuzhongxinxi;
//
///**
// * 舞种的适配器
// */
//public class WuzhongAdapter extends BaseAdapter {
//    public List<Wuzhongxinxi.DANCES> dancelist;
//    private Context context;
//    HashMap hs = new HashMap();
//    public String dances = "";
//
//    public WuzhongAdapter(Context context) {
//        this.context = context;
//    }
//
//    public void setData(List<Wuzhongxinxi.DANCES> dancelist) {
//        this.dancelist = dancelist;
//    }
//
//    @Override
//    public int getCount() {
//        return null == dancelist ? 0 : dancelist.size();
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
//            convertView = View.inflate(context, R.layout.adapter_wuzhong, null);
//            holder = new Mholeder();
//            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
//            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
//            convertView.setTag(holder);
//        } else {
//            holder = (Mholeder) convertView.getTag();
//        }
//        holder.tv_name.setText(dancelist.get(position).dancename);
//        //选取舞种信息
//        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked == true) {
//                    hs.put(position, dancelist.get(position).danceid);
//                } else {
//                    hs.remove(position);
//                }
//                Log.e("集合", "onCheckedChanged: " + hs);
//            }
//        });
//        return convertView;
//    }
//
//    public class Mholeder {
//        TextView tv_name;
//        CheckBox checkBox;
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
//        return "{" + dances + "}";
//    }
//
//
//}
