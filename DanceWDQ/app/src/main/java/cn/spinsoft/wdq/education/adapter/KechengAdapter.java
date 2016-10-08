//package cn.spinsoft.wdq.education.adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.google.gson.Gson;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.base.BaseHandler;
//import cn.spinsoft.wdq.education.MyApplication;
//import cn.spinsoft.wdq.education.activity.KechengDetailActivity;
//import cn.spinsoft.wdq.education.bean.Kecheng;
//import cn.spinsoft.wdq.education.bean.Yuyue;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * Created by quchuangye on 2015/11/26.课程的适配器
// */
//public class KechengAdapter extends BaseAdapter {
//    private Context context;
//    private Holder holder;
//    private List<Kecheng.KECHENG> courses;
//    private int userid;
//    private String TAG = "KechengAdapter";
//    private String trainId;
//    private Gson gson = new Gson();
//    private Yuyue yuyue;
//
//
//    public KechengAdapter(Context context) {
//        this.context = context;
//    }
//
//    public void setData(List<Kecheng.KECHENG> courses, Boolean is_refresh, String trainId) {
//        this.trainId = trainId;
//        userid = BaseHandler.watcherUserId;
//        if (is_refresh) {
//            this.courses = courses;
//        } else {
//            if (null == this.courses) {
//                courses = new ArrayList<Kecheng.KECHENG>();
//            } else {
//                this.courses.addAll(courses);
//            }
//        }
//
//    }
//
//
//    @Override
//    public int getCount() {
//        return null == courses ? 0 : courses.size();
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
//            convertView = View.inflate(context, R.layout.adapter_kecheng, null);
//            holder = new Holder();
//            holder.tv_kecheng = (TextView) convertView.findViewById(R.id.tv_kecheng);
//            holder.iv_bianji = (ImageView) convertView.findViewById(R.id.iv_bianji);
//            holder.iv_shanchu = (ImageView) convertView.findViewById(R.id.iv_shanchu);
//            holder.iv_yuyue = (ImageView) convertView.findViewById(R.id.iv_yuyue);
//            holder.rl_kecheng = (RelativeLayout) convertView.findViewById(R.id.rl_kecheng);
//            convertView.setTag(holder);
//        } else {
//            holder = (Holder) convertView.getTag();
//        }
//        for (int i = 0; i < courses.size(); i++) {
//            holder.tv_kecheng.setText(courses.get(position).coursetitle);
//            holder.rl_kecheng.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, KechengDetailActivity.class);
//                    intent.putExtra("courseid", courses.get(position).courseid + "");
//                    Log.e(TAG, "onClick: " + courses.get(position).courseid);
//                    context.startActivity(intent);
//                }
//            });
//            //  如果是游客登陆  0表未预约  1表示已经预约
//            if (userid != courses.get(position).courseid) {
//                holder.iv_yuyue.setVisibility(View.VISIBLE);
//                holder.iv_shanchu.setVisibility(View.GONE);
//                //未预约课程
//                if (courses.get(position).state == 0) {
//                    holder.iv_yuyue.setBackgroundResource(R.mipmap.org_course_order);
//                    holder.iv_yuyue.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            //预约请求
//                            Yuyuekecheng(position);
//
//                        }
//                    });
//                } else {
//                    holder.iv_yuyue.setBackgroundResource(R.mipmap.org_course_ordered);
//                    holder.iv_yuyue.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            //do  nothing
//                        }
//                    });
//
//                }
//
//
//            } else {
//                holder.iv_shanchu.setVisibility(View.VISIBLE);
//                holder.iv_bianji.setVisibility(View.VISIBLE);
//                holder.iv_yuyue.setVisibility(View.GONE);
//                holder.iv_shanchu.setBackgroundResource(R.mipmap.school_shanchu);
//                holder.iv_bianji.setBackgroundResource(R.mipmap.schoo_bianji);
//                holder.iv_shanchu.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //点击删除
//
//
//                    }
//                });
//                holder.iv_bianji.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //点击编辑
//                    }
//                });
//
//
//            }
//
//
//        }
//        return convertView;
//    }
//
//    public class Holder {
//        private TextView tv_kecheng;
//        private ImageView iv_bianji, iv_shanchu, iv_yuyue;
//        private RelativeLayout rl_kecheng;
//    }
//
//    public void Yuyuekecheng(final int position) {
//        String url = Constants_utl.YUYUEKECHENG;//Jigou详情
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e(TAG, "onResponse: " + response);
//                        yuyue = gson.fromJson(response, Yuyue.class);
//                        if (yuyue.code == 0) {
//                            //TODO
////                            holder.iv_yuyue.setBackgroundResource(R.mipmap.org_course_ordered);
//                            Toast.makeText(context, "预约成功", Toast.LENGTH_SHORT).show();
//                        } else {
////                            holder.iv_yuyue.setBackgroundResource(R.mipmap.org_course_order);
//                            Toast.makeText(context, "预约课程失败,请重新尝试!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                        Toast.makeText(context, "预约课程失败,请重新尝试!", Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, "onErrorResponse: " + error);
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                // the POST parameters:
//                params.put("trainId", trainId);
//                params.put("coursId", courses.get(position).courseid + "");
//                params.put("userId", userid + "");
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//    }
//
//}
