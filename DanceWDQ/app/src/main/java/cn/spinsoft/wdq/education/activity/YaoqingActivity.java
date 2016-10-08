//package cn.spinsoft.wdq.education.activity;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.ListView;
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
//import com.handmark.pulltorefresh.library.PullToRefreshListView;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.base.BaseActivity;
//import cn.spinsoft.wdq.education.MyApplication;
//import cn.spinsoft.wdq.education.adapter.LaoshiAdapter;
//import cn.spinsoft.wdq.education.bean.Yaoqinglaoshi;
//import cn.spinsoft.wdq.education.bean.Yuyue;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * 邀请机构老师入驻
// */
//public class YaoqingActivity extends BaseActivity implements View.OnClickListener {
//    private String TAG = "YaoqingActivity";
//    private int userid;
//    private ListView listview;
//    private boolean is_refresh = true;
//    private int page = 1;
//    private Gson gson = new Gson();
//    private PullToRefreshListView pullToRefreshListView;
//    private LaoshiAdapter adapter;
//    private String orgid;
//    private Yaoqinglaoshi laoshi;
//    private TextView tv_xuexiaoname;
//    private Yuyue yuyue;
//
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_yaoqing;
//    }
//
//    @Override
//    protected void initHandler() {
//
//    }
//
//    @Override
//    protected void initViewAndListener(Bundle savedInstanceState) {
//        orgid = getIntent().getStringExtra("orgid");
//        Log.e(TAG, "initViewAndListener: " + orgid);
//
//        //返回键
//        findViewById(R.id.ll_back).setOnClickListener(this);
//        findViewById(R.id.tv_wancheng).setOnClickListener(this);
//        tv_xuexiaoname = (TextView) findViewById(R.id.tv_xuexiaoname);
//        tv_xuexiaoname.setText("邀请老师入驻");
//
//
//        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_pull);
//        listview = pullToRefreshListView.getRefreshableView();
//        adapter = new LaoshiAdapter(this);
//        listview.setAdapter(adapter);
//        getData(1, true);
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
//    }
//
//    public void getData(final int page, final boolean is_refresh) {
//        String url = Constants_utl.YAOQINGLAOSHI;//待认证教师
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        pullToRefreshListView.onRefreshComplete();
//                        Log.e(TAG, "onResponse: " + response);
//                        laoshi = gson.fromJson(response, Yaoqinglaoshi.class);
//                        if (laoshi.code == 0) {
//                            if (laoshi.teachers != null && laoshi.teachers.size() != 0) {
//                                adapter.setData(laoshi.teachers, is_refresh);
//                                adapter.notifyDataSetChanged();
//                            }
//
//                        } else {
//                            //请求失败
//                        }
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        pullToRefreshListView.onRefreshComplete();
//                        error.printStackTrace();
//                        Log.e(TAG, "onErrorResponse: " + error);
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                // the POST parameters:
//                params.put("trainId", orgid + "");
//                params.put("pageNumber", page + "");
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.ll_back:
//                finish();
//                break;
//            case R.id.tv_wancheng:
//                tijiaojieguo();
//                break;
//
//        }
//
//    }
//
//    private void tijiaojieguo() {
//        String url = Constants_utl.TIJIAOYAOQING;//提交邀请
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e(TAG, "onResponse: " + response);
//                        yuyue = gson.fromJson(response, Yuyue.class);
//                        Toast.makeText(YaoqingActivity.this, yuyue.msg, Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                        Log.e(TAG, "onErrorResponse: " + error);
//                        Toast.makeText(YaoqingActivity.this, yuyue.msg, Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                // the POST parameters:
//                params.put("trainId", "1");
//                params.put("userId", adapter.get() + "");
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//    }
//
//}
//
