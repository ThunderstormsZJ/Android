//package cn.spinsoft.wdq.education.frament.school;
//
//
//import android.app.AlertDialog;
//import android.app.AlertDialog.Builder;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.GridView;
//import android.widget.LinearLayout;
//import android.widget.ScrollView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.google.gson.Gson;
//import com.handmark.pulltorefresh.library.PullToRefreshBase;
//import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.base.BaseFragment;
//import cn.spinsoft.wdq.base.BaseHandler;
//import cn.spinsoft.wdq.education.MyApplication;
//import cn.spinsoft.wdq.education.activity.SchoolActivity;
//import cn.spinsoft.wdq.education.adapter.ImgAdapter;
//import cn.spinsoft.wdq.education.bean.Xuexiao;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * 简介的Fragment
// */
//public class JianjieFragment extends BaseFragment {
//    private TextView tv_introduce, tv_address, tv_person, tv_tell;
//    private String TAG = "JianjieFragment";
//    private Xuexiao xuexiao;
//    private Gson gson = new Gson();
//    private int userid = BaseHandler.watcherUserId;
//    public String orgids;
//    private LinearLayout ll_xiangqing;
//    private GridView gv_img;
//    private ImgAdapter adapter;
//    private PullToRefreshScrollView PullToRefreshScrollView;
//
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_jianjie;
//    }
//
//    @Override
//    protected void initViewAndListener(View root, Bundle savedInstanceState) {
//        tv_introduce = (TextView) root.findViewById(R.id.tv_introduce);//店铺介绍
//        tv_address = (TextView) root.findViewById(R.id.tv_address);//店铺地址
//        tv_person = (TextView) root.findViewById(R.id.tv_person);//联系人
//        ll_xiangqing = (LinearLayout) root.findViewById(R.id.ll_xiangqing);//图文详情的布局
//        gv_img = (GridView) root.findViewById(R.id.gv_img);
//        tv_tell = (TextView) root.findViewById(R.id.tv_tell);//联系电话
//        root.findViewById(R.id.iv_tell).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Builder是对对话框进行设置
//                Builder builder = new Builder(getActivity());
//                builder.setIcon(android.R.drawable.ic_dialog_alert);//设置图标
//                builder.setTitle("            提示");
//                builder.setMessage("是否拨打客服电话  " + tv_tell.getText().toString())
//                        .setPositiveButton("取消", null)
////				   .setNeutralButton("中性含义", null)
//                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, //就是对话框控件
//                                                int which) {//表示正面,中性，负面
//                                Intent phoneIntent = new Intent("android.intent.action.CALL",
//                                        Uri.parse("tel:" + tv_tell.getText().toString()));
//                                //启动
//                                startActivity(phoneIntent);
//
//                            }
//                        });
//                builder.setCancelable(false);//点击对话框以外的区域，对话框不会关闭
//                AlertDialog d = builder.create();//创建对话框
//                d.show();
//
//
//            }
//        });
//        orgids = ((SchoolActivity) getActivity()).getTrainId();
//        getSchoolMessage();
//        Log.e(TAG, "initViewAndListener:orgids " + orgids);
//        PullToRefreshScrollView = (PullToRefreshScrollView) root.findViewById(R.id.pull_scrollView);
//        PullToRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
//        PullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
//                getSchoolMessage();
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
//                getSchoolMessage();
//            }
//        });
//
//
//    }
//
//    private void getSchoolMessage() {
//        String url = Constants_utl.JIGOUXIANGQING;//Jigou详情
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        PullToRefreshScrollView.onRefreshComplete();
//                        Log.e(TAG, "onResponse: " + response);
//                        xuexiao = gson.fromJson(response, Xuexiao.class);
//                        if (xuexiao.code == 0) {
//                            initView();
//                        } else {
//                            Toast.makeText(getActivity(), "请求失败", Toast.LENGTH_SHORT).show();
//                        }
//
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        PullToRefreshScrollView.onRefreshComplete();
//                        error.printStackTrace();
//                        Log.e(TAG, "onErrorResponse: " + error);
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                // the POST parameters:
//                params.put("trainId", orgids);
//                params.put("id", userid + "");
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//    }
//
//
//    /**
//     * 请求数据后初始化数据
//     */
//    public void initView() {
//        tv_introduce.setText("    " + xuexiao.train.intro);
//        tv_address.setText(xuexiao.train.address);
//        tv_person.setText(xuexiao.train.orgname);
//        tv_tell.setText(xuexiao.train.mobile);
//        if (xuexiao.train.images.size() == 0) {
//            ll_xiangqing.setVisibility(View.GONE);
//        } else {
//            adapter = new ImgAdapter(getActivity(), xuexiao.train.images);
//            gv_img.setAdapter(adapter);
//            adapter.notifyDataSetChanged();
//        }
//    }
//}
