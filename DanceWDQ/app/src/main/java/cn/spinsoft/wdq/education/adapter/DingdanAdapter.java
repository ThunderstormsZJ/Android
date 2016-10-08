//package cn.spinsoft.wdq.education.adapter;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.education.MyApplication;
//import cn.spinsoft.wdq.education.bean.Dingdan;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * Created by Administrator on 2015/12/14.  订单的适配器
// */
//public class DingdanAdapter extends BaseAdapter {
//    private Context context;
//    public List<Dingdan.Order> page;
//    String TAG = "DingdanAdapter";
//
//    public DingdanAdapter(Context context) {
//        this.context = context;
//    }
//
//    public void setData(List<Dingdan.Order> page, Boolean is_refresh) {
//        if (is_refresh) {
//            this.page = page;
//        } else {
//            if (null == this.page) {
//                page = new ArrayList<Dingdan.Order>();
//            } else {
//                this.page.addAll(page);
//            }
//        }
//    }
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
//        Mholder holder;
//        if (null == convertView) {
//            convertView = View.inflate(context, R.layout.adapter_dingdan, null);
//            holder = new Mholder();
//            holder.tv_xiadanshijian = (TextView) convertView.findViewById(R.id.tv_xiadanshijian);
//            holder.tv_zhuangtai = (TextView) convertView.findViewById(R.id.tv_zhuangtai);
//            holder.tv_kechengname = (TextView) convertView.findViewById(R.id.tv_kechengname);
//            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
//            holder.tv_tell = (TextView) convertView.findViewById(R.id.tv_tell);
//            holder.tv_shijian = (TextView) convertView.findViewById(R.id.tv_shijian);
//            holder.iv_queren = (ImageView) convertView.findViewById(R.id.iv_queren);
//            holder.iv_jujue = (ImageView) convertView.findViewById(R.id.iv_jujue);
//            convertView.setTag(holder);
//        } else {
//            holder = (Mholder) convertView.getTag();
//        }
//        holder.tv_xiadanshijian.setText("下单时间:" + page.get(position).createtime);
//        if (page.get(position).state == 0) {
//            holder.tv_zhuangtai.setText("等待机构确认");
//            holder.iv_queren.setVisibility(View.VISIBLE);
//            holder.iv_jujue.setVisibility(View.VISIBLE);
//            holder.iv_queren.setBackgroundResource(R.mipmap.booking_item_confirm);
//            holder.iv_jujue.setBackgroundResource(R.mipmap.booking_item_refuse);
//        }
//        if (page.get(position).state == 1) {
//            holder.tv_zhuangtai.setText("已确认");
//        }
//        if (page.get(position).state == 2) {
//            holder.tv_zhuangtai.setText("已拒绝");
//        }
//        if (page.get(position).state == 3) {
//            holder.tv_zhuangtai.setText("已取消");
//        }
//        holder.tv_kechengname.setText(page.get(position).coursetitle);
//        holder.tv_name.setText(page.get(position).name);
//        holder.tv_tell.setText(page.get(position).mobile);
//        holder.tv_shijian.setText("预约时间:" + page.get(position).ordertime);
//        holder.iv_queren.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                queren(position, 1);
//            }
//        });
//
//        holder.iv_jujue.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                queren(position, 2);
//            }
//        });
//
//        return convertView;
//    }
//
//    public class Mholder {
//        TextView tv_xiadanshijian, tv_zhuangtai, tv_kechengname, tv_name, tv_tell, tv_shijian;
//        ImageView iv_queren, iv_jujue;
//    }
//
//    public void queren(final int position, final int A) {
//        String url = Constants_utl.DINGDANQUEREN;//订单确认还是取消
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e(TAG, "onResponse: " + response);
////                            if (dingdan.code == 0) {
////                                if (dingdan.page != null && dingdan.page.size() != 0) {
////                                    adapter.setData(dingdan.page, is_refresh);
////                                    adapter.notifyDataSetChanged();
////                                }
////
////                            } else {
////                                //请求失败
////                            }
////
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                        Log.e(TAG, "onErrorResponse: " + error);
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                // the POST parameters:
//                params.put("type", A + "");
//                params.put("id", page.get(position).id + "");
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//
//    }
//
//}
