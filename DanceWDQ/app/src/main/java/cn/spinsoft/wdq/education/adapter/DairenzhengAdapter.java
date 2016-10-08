//package cn.spinsoft.wdq.education.adapter;
//
//import android.app.Activity;
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
//import com.google.gson.Gson;
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.education.MyApplication;
//import cn.spinsoft.wdq.education.activity.DairenzhengjiaoshiActivity;
//import cn.spinsoft.wdq.education.bean.Dairenzhenglaoshi;
//import cn.spinsoft.wdq.education.bean.Yuyue;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * Created by Administrator on 2015/12/11.
// */
//public class DairenzhengAdapter extends BaseAdapter {
//    private Context context;
//    public List<Dairenzhenglaoshi.PAGEJIAOSHI> page;
//    private Gson gson = new Gson();
//    private String TAG = "DairenzhengAdapter";
//    private Yuyue yuyue;
//    private Activity activity;
//
//    public DairenzhengAdapter(Context context) {
//        this.context = context;
//    }
//
//    public void setData(List<Dairenzhenglaoshi.PAGEJIAOSHI> page, Boolean is_refresh) {
//        if (is_refresh) {
//            this.page = page;
//        } else {
//            if (null == this.page) {
//                page = new ArrayList<Dairenzhenglaoshi.PAGEJIAOSHI>();
//            } else {
//                this.page.addAll(page);
//            }
//        }
//
//
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
//        Holder holder;
//        if (null == convertView) {
//            convertView = View.inflate(context, R.layout.adapter_dairenzheng, null);
//            holder = new Holder();
//            holder.tv_jiaoshi_name = (TextView) convertView.findViewById(R.id.tv_jiaoshi_name);
//            holder.iv_jiaoshi_queren = (ImageView) convertView.findViewById(R.id.iv_jiaoshi_queren);
//            holder.iv_jiaoshi_jujue = (ImageView) convertView.findViewById(R.id.iv_jiaoshi_jujue);
//            holder.iv_jiaoshi_touxiang = (ImageView) convertView.findViewById(R.id.iv_jiaoshi_touxiang);
//            convertView.setTag(holder);
//        } else {
//            holder = (Holder) convertView.getTag();
//        }
//
//
//        holder.tv_jiaoshi_name.setText(page.get(position).nickname + "");
//        Picasso.with(context).load(page.get(position).headurl).into(holder.iv_jiaoshi_touxiang);//替换图片
//        //已经确认
//        if (page.get(position).state == 1) {
//            holder.iv_jiaoshi_queren.setVisibility(View.GONE);
//            holder.iv_jiaoshi_jujue.setBackgroundResource(R.mipmap.booking_item_confirmed);
//        }
//        //已经拒绝
//        if (page.get(position).state == 2) {
//            holder.iv_jiaoshi_queren.setVisibility(View.GONE);
//            holder.iv_jiaoshi_jujue.setBackgroundResource(R.mipmap.booking_item_refused);
//        }
//        //未确认
//        if (page.get(position).state == 3) {
//            holder.iv_jiaoshi_queren.setVisibility(View.VISIBLE);
//            holder.iv_jiaoshi_queren.setBackgroundResource(R.mipmap.booking_item_confirm);
//            holder.iv_jiaoshi_queren.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    querenjiaoshi(position, 1);
//                }
//            });
//            holder.iv_jiaoshi_jujue.setBackgroundResource(R.mipmap.booking_item_refuse);
//            holder.iv_jiaoshi_jujue.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    querenjiaoshi(position, 2);
//                }
//            });
//
//        }
//
//
//        return convertView;
//    }
//
//    public class Holder {
//        TextView tv_jiaoshi_name;
//        ImageView iv_jiaoshi_queren, iv_jiaoshi_jujue, iv_jiaoshi_touxiang;
//    }
//
//
//    public void querenjiaoshi(final int position, final int type) {
//        String url = Constants_utl.QUERENJIEGUO;//确认还是拒绝
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e(TAG, "onResponse: " + response + type + "-----" + "position" + position);
//                        yuyue = gson.fromJson(response, Yuyue.class);
//                        if (yuyue.code == 30026 || yuyue.code == 30027) {
//                            ((DairenzhengjiaoshiActivity) context).gengxinshuju();
//                        } else {
//                            //do nothing
//                        }
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
//                params.put("id", page.get(position).id);
//                params.put("type", type + "");
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//
//    }
//
//
//}
