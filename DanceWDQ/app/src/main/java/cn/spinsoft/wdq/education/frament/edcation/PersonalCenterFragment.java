//package cn.spinsoft.wdq.education.frament.edcation;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.ScrollView;
//import android.widget.TextView;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.google.gson.Gson;
//import com.handmark.pulltorefresh.library.PullToRefreshBase;
//import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
//import com.squareup.picasso.Picasso;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.base.BaseFragment;
//import cn.spinsoft.wdq.base.BaseHandler;
//import cn.spinsoft.wdq.education.MyApplication;
//import cn.spinsoft.wdq.education.activity.AboutmeActivity;
//import cn.spinsoft.wdq.education.activity.DairenzhengjiaoshiActivity;
//import cn.spinsoft.wdq.education.activity.DanweixinxiActivity;
//import cn.spinsoft.wdq.education.activity.QianbaoActivity;
//import cn.spinsoft.wdq.education.activity.SchoolActivity;
//import cn.spinsoft.wdq.education.activity.YaoqingActivity;
//import cn.spinsoft.wdq.education.activity.YuyueOrderActivity;
//import cn.spinsoft.wdq.education.bean.Jigougerenzhongxin;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * Created by Administrator on 2015/12/10.  机构中心
// */
//public class PersonalCenterFragment extends BaseFragment implements View.OnClickListener {
//    private String TAG = "PersonalCenterFragment";
//    private Intent intent;
//    private PullToRefreshScrollView pullToRefreshScrollView;
//    private String orgids, userid;
//    private Jigougerenzhongxin zhongxin;
//    private Gson gson = new Gson();
//    private ImageView iv_jigou_touxiang;
//    private TextView tv_center_name, tv_center_sign;
//
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_personnal;
//    }
//
//    @Override
//    protected void initViewAndListener(View root, Bundle savedInstanceState) {
//
//        orgids = "2";//机构id
//        userid = BaseHandler.watcherUserId + "";//用户id
//
//        pullToRefreshScrollView = (PullToRefreshScrollView) root.findViewById(R.id.pullToRefreshScrollView);
//        pullToRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
//        pullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
//                getData();
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
//                getData();
//            }
//        });
//        iv_jigou_touxiang = (ImageView) root.findViewById(R.id.iv_jigou_touxiang);
//        tv_center_name = (TextView) root.findViewById(R.id.tv_center_name);
//        tv_center_sign = (TextView) root.findViewById(R.id.tv_center_sign);
//
//        root.findViewById(R.id.ll_gerenzhuye).setOnClickListener(this);
//        root.findViewById(R.id.ll_wofaqide).setOnClickListener(this);
//        root.findViewById(R.id.ll_aboutme).setOnClickListener(this);
//        root.findViewById(R.id.ll_qianbao).setOnClickListener(this);
//        root.findViewById(R.id.ll_ziliao).setOnClickListener(this);
//        root.findViewById(R.id.ll_order).setOnClickListener(this);
//        root.findViewById(R.id.ll_yaoqing).setOnClickListener(this);
//        root.findViewById(R.id.ll_dairenzheng).setOnClickListener(this);
//        root.findViewById(R.id.ll_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().finish();
//            }
//        });
//        //请求数据
//        getData();
//
//    }
//
//    private void getData() {
//        String url = Constants_utl.JIGOU_CENTER;//Jigou详情
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        pullToRefreshScrollView.onRefreshComplete();
//                        Log.e(TAG, "onResponse: " + response);
//                        zhongxin = gson.fromJson(response, Jigougerenzhongxin.class);
//                        if (zhongxin.code == 0) {
//                            tv_center_sign.setText(zhongxin.info.signature);
//                            tv_center_name.setText(zhongxin.info.name);
//                            Picasso.with(getActivity()).load(zhongxin.info.headurl).into(iv_jigou_touxiang);//替换图片
//                        } else {
//                            //do nothing
//                        }
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        pullToRefreshScrollView.onRefreshComplete();
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
//                params.put("id", userid);
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//    }
//
//    @Override
//    public void onClick(View v) {
//        try {
//            intent = new Intent();
//            switch (v.getId()) {
//                case R.id.ll_gerenzhuye:
//                    intent.putExtra("IS_FROM_Center", true);
//                    intent.putExtra("orgids", orgids);
//                    intent.setClass(getActivity(), SchoolActivity.class);
//                    break;
//                case R.id.ll_wofaqide://我发起的
//                    //TODO  等待接入
////                    if (zhongxin.info.orgid == null) {
////                    } else {
////                        intent.putExtra("orgid", zhongxin.info.orgid);
////                        intent.setClass(getActivity(), WofaqiActivity.class);
////                    }
//                    break;
//                case R.id.ll_aboutme://与我相关
//                    if (zhongxin.info.orgid == null) {
//                    } else {
//                        intent.putExtra("orgid", zhongxin.info.orgid);
//                        intent.putExtra("FROM_JIGOU", true);
//                        intent.setClass(getActivity(), AboutmeActivity.class);
//                    }
//
//                    break;
//                case R.id.ll_qianbao://钱包
//                    intent.setClass(getActivity(), QianbaoActivity.class);
//                    break;
//                case R.id.ll_ziliao://资料完善
//                    if (zhongxin.info.orgid == null) {
//
//                    } else {
//                        intent.putExtra("orgid", zhongxin.info.orgid);
//                        intent.setClass(getActivity(), DanweixinxiActivity.class);
//                    }
//                    break;
//                case R.id.ll_order://预约订单
//                    if (zhongxin.info.orgid == null) {
//
//                    } else {
//                        intent.putExtra("orgid", zhongxin.info.orgid);
//                        intent.setClass(getActivity(), YuyueOrderActivity.class);
//                    }
//                    break;
//                case R.id.ll_yaoqing://邀请老师入驻
//                    if (zhongxin.info.orgid == null) {
//
//                    } else {
//                        intent.putExtra("orgid", zhongxin.info.orgid);
//                        intent.setClass(getActivity(), YaoqingActivity.class);
//                    }
//                    break;
//                case R.id.ll_dairenzheng://待认证老师
//                    if (zhongxin.info.orgid == null) {
//
//                    } else {
//                        intent.putExtra("orgid", zhongxin.info.orgid);
//                        intent.setClass(getActivity(), DairenzhengjiaoshiActivity.class);
//                    }
//                    break;
//            }
//
//            startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//}
