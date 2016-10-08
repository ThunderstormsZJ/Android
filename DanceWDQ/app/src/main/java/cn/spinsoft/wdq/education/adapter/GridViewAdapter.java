//package cn.spinsoft.wdq.education.adapter;
//
//import android.content.Context;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
//import cn.spinsoft.wdq.R;
//
///**
// * Created by Administrator on 2015/12/7.
// */
//public class GridViewAdapter extends BaseAdapter {
//    private Context context;
//    public List<String> images;
//    private Mholeder holder;
//
//    public GridViewAdapter(Context context, List<String> images) {
//        this.context = context;
//        this.images = images;
//    }
//
//    @Override
//    public int getCount() {
//        return null == images ? 0 : images.size();
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
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (null == convertView) {
//            convertView = View.inflate(context, R.layout.adapter_img, null);
//            holder = new Mholeder();
//            holder.iv_img = (ImageView) convertView.findViewById(R.id.iv_img);
//            convertView.setTag(holder);
//        } else {
//            holder = (Mholeder) convertView.getTag();
//        }
//        for (int i = 0; i < images.size(); i++) {
//            Picasso.with(context).load(images.get(position)).into(holder.iv_img);
//        }
//
//        return convertView;
//    }
//
//    public class Mholeder {
//        ImageView iv_img;
//    }
//}
