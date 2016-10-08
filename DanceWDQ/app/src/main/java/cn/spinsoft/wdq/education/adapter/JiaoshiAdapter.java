//package cn.spinsoft.wdq.education.adapter;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Button;
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
//import cn.spinsoft.wdq.base.BaseHandler;
//import cn.spinsoft.wdq.education.MyApplication;
//import cn.spinsoft.wdq.education.bean.Jiaoshi;
//import cn.spinsoft.wdq.education.bean.Yuyue;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * Created by quchuangye  教师的适配器on 2015/12/4.
// */
//public class JiaoshiAdapter extends BaseAdapter {
//    private Context context;
//    private Mholder holder;
//    public List<Jiaoshi.Teacher> teachers;
//    private int userid;
//    private String TAG = "JiaoshiAdapter";
//    private Gson gson = new Gson();
//    private Yuyue guanzhu;
//    private int isguanzhu = 1;
//
//
//    public JiaoshiAdapter(Context context) {
//        this.context = context;
//    }
//
//    public void setData(List<Jiaoshi.Teacher> teachers, Boolean is_refresh) {
//        if (is_refresh) {
//            this.teachers = teachers;
//        } else {
//            if (null == this.teachers) {
//                teachers = new ArrayList<Jiaoshi.Teacher>();
//            } else {
//                this.teachers.addAll(teachers);
//            }
//        }
//
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
//        if (null == convertView) {
//            convertView = View.inflate(context, R.layout.adapter_jiaoshi, null);
//            holder = new Mholder();
//            holder.tv_jiaoshi_name = (TextView) convertView.findViewById(R.id.tv_jiaoshi_name);
//            holder.tv_jiaoshi_sign = (TextView) convertView.findViewById(R.id.tv_jiashi_sign);
//            holder.tv_juli = (TextView) convertView.findViewById(R.id.tv_juli);//距离
//            holder.iv_jiaoshi = (ImageView) convertView.findViewById(R.id.iv_touxiang);
//            holder.iv_guanzhu = (ImageView) convertView.findViewById(R.id.iv_guanzhu);
//            holder.bt_1 = (Button) convertView.findViewById(R.id.bt_1);
//            holder.bt_2 = (Button) convertView.findViewById(R.id.bt_2);
//            holder.bt_3 = (Button) convertView.findViewById(R.id.bt_3);
//            holder.bt_4 = (Button) convertView.findViewById(R.id.bt_4);
//            holder.bt_5 = (Button) convertView.findViewById(R.id.bt_5);
//            convertView.setTag(holder);
//        } else {
//            holder = (Mholder) convertView.getTag();
//        }
//
//        holder.iv_jiaoshi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //头像点击跳转到个人中心
////TODO
//            }
//        });
//        holder.tv_jiaoshi_name.setText(teachers.get(position).nickname);//昵称
//        holder.tv_jiaoshi_sign.setText(teachers.get(position).signature);//签名
//        Picasso.with(context).load(teachers.get(position).headurl).into(holder.iv_jiaoshi);//替换图片
//        //0 未关注  1已经关注
//        if (teachers.get(position).attention == 0) {
//            holder.iv_guanzhu.setBackgroundResource(R.mipmap.laosho_jia);
//            holder.iv_guanzhu.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //关注
//                    isguanzhu = 1;
//                    guanzhu(position, isguanzhu);
//                }
//            });
//        } else {
//            holder.iv_guanzhu.setBackgroundResource(R.mipmap.laoshi_jian);
//            holder.iv_guanzhu.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //添加关注
//                    isguanzhu = 2;
//                    guanzhu(position, isguanzhu);
//                }
//            });
//        }
//
//        return convertView;
//    }
//
//    public class Mholder {
//        TextView tv_jiaoshi_name, tv_jiaoshi_sign, tv_juli;
//        ImageView iv_jiaoshi, iv_guanzhu;
//        Button bt_1, bt_2, bt_3, bt_4, bt_5;
//    }
//
//    public void guanzhu(final int position, final int isguanzhu) {
//        userid = BaseHandler.watcherUserId;
//        String url = Constants_utl.GUANZHU;//关注与否
//        Log.e(TAG, "quxiaoguanzhu: " + url);
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        guanzhu = gson.fromJson(response, Yuyue.class);
//                        if (guanzhu.code == 0) {
//                            if (isguanzhu == 1) {
//                                holder.iv_guanzhu.setBackgroundResource(R.mipmap.laoshi_jian);
//                            } else {
//                                holder.iv_guanzhu.setBackgroundResource(R.mipmap.laosho_jia);
//                            }
////                            notifyDataSetChanged();
//
//                        }
//
//
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
//                params.put("userid", userid + "");
//                params.put("attuserid", teachers.get(position).userid);
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//    }
//
//}
