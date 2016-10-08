//package cn.spinsoft.wdq.education.activity;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.google.gson.Gson;
//import com.handmark.pulltorefresh.library.PullToRefreshBase;
//import com.handmark.pulltorefresh.library.PullToRefreshListView;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.base.BaseActivity;
//import cn.spinsoft.wdq.base.BaseHandler;
//import cn.spinsoft.wdq.education.MyApplication;
//import cn.spinsoft.wdq.education.adapter.JiaoshiAdapter;
//import cn.spinsoft.wdq.education.bean.Kechengxiangqing;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * 课程详情的类
// */
//public class KechengDetailActivity extends BaseActivity implements View.OnClickListener {
//    private TextView tv_kecheng, tv_jianjie, tv_school;
//    private LinearLayout headview;
//    private PullToRefreshListView pullToRefreshListView;
//    private int page;
//    private boolean is_refresh;
//    private String TAG = "KechengDetailActivity";
//    private int userid;
//    private String courseid;
//    private Kechengxiangqing kechengxiangqing;
//    private ListView listview;
//    private JiaoshiAdapter adapter;
//    private Gson gson = new Gson();
//
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_kecheng_detail;
//    }
//
//    @Override
//    protected void initHandler() {
//        //nothing to do
//    }
//
//    @Override
//    protected void initViewAndListener(Bundle savedInstanceState) {
//        courseid = getIntent().getStringExtra("courseid");
//        userid = BaseHandler.watcherUserId;//用户id
//        headview = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.kecheng_headview, null);//列表头
//        Log.e(TAG, "initViewAndListener: " + courseid + "-----" + userid);
//        headview.findViewById(R.id.iv_kecheng_zhunfa).setOnClickListener(this);//转发
//        headview.findViewById(R.id.iv_kecheng_baoming).setOnClickListener(this);//报名
//        headview.findViewById(R.id.iv_kecheng_xihuan).setOnClickListener(this);//喜欢
//        headview.findViewById(R.id.iv_kecheng_liuyan).setOnClickListener(this);//喜欢
//
//        findViewById(R.id.videos_back).setOnClickListener(this);//返回键
//        tv_kecheng = (TextView) headview.findViewById(R.id.tv_kecheng);//课程名称
//        tv_jianjie = (TextView) headview.findViewById(R.id.tv_jianjie);//简介
//        tv_school = (TextView) findViewById(R.id.tv_shchool2);//学校名称
//        getData(1, true);
//
//        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_kecheng);
//        listview = pullToRefreshListView.getRefreshableView();
//        listview.addHeaderView(headview);
//        adapter = new JiaoshiAdapter(this);
//        listview.setAdapter(adapter);
//        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
//        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase refreshView) {
//                //下拉刷新
//                page = 1;
//                is_refresh = true;
//                getData(page, is_refresh);
//
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase refreshView) {
//                //上啦加载
//                page++;
//                is_refresh = false;
//                getData(page, is_refresh);
//
//            }
//        });
//
//
//    }
//
//    public void getData(final int page, boolean is_refresh) {
//        {
//            String url = Constants_utl.KECHENGXIANGQIANG;//课程详情
//            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            pullToRefreshListView.onRefreshComplete();
//                            kechengxiangqing = gson.fromJson(response, Kechengxiangqing.class);
//                            Log.e(TAG, "onResponse: " + response);
//                            if (kechengxiangqing.code == 0) {
//                                tv_kecheng.setText(kechengxiangqing.course.coursetitle + "");
//                                tv_jianjie.setText(kechengxiangqing.course.details + "");
//                                tv_school.setText(kechengxiangqing.course.coursetitle + "");
//                                //TODO
//
//                            } else {
//                                //请求失败
//                            }
//
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            pullToRefreshListView.onRefreshComplete();
//                            error.printStackTrace();
//                            Log.e(TAG, "onErrorResponse: " + error);
//                        }
//                    }
//            ) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    // the POST parameters:
//                    params.put("courseId", courseid);
//                    params.put("userId", userid + "");
//                    return params;
//                }
//            };
//            postRequest.setTag("volleypost");
//            MyApplication.getHttpQueue().add(postRequest);
//        }
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.iv_kecheng_zhunfa://转发
//
//                break;
//            case R.id.iv_kecheng_baoming://报名
//
//                break;
//            case R.id.iv_kecheng_xihuan://喜欢
//
//                break;
//            case R.id.iv_kecheng_liuyan://留言
//
//                break;
//            case R.id.videos_back:
//                finish();
//                break;
//        }
//    }
//}
