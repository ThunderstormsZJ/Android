//package cn.spinsoft.wdq.education.activity.danweixinxi;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.ListView;
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
//import cn.spinsoft.wdq.education.adapter.WuzhongAdapter;
//import cn.spinsoft.wdq.education.bean.Wuzhongxinxi;
//import cn.spinsoft.wdq.education.bean.Yuyue;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * 舞种选择
// */
//public class WuzhongxuanzeActivity extends BaseActivity implements View.OnClickListener {
//    private String TAG = "WuzhongxuanzeActivity";
//    private Wuzhongxinxi wuzhongxinxi;
//    private Gson gson = new Gson();
//    private PullToRefreshListView pullToRefreshListView;
//    private ListView listview;
//    private WuzhongAdapter adapter;
//    private String orgid;
//    private Yuyue yuyue;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_wuzhongxuanze;
//    }
//
//    @Override
//    protected void initHandler() {
//    }
//
//    @Override
//    protected void initViewAndListener(Bundle savedInstanceState) {
//        orgid = getIntent().getStringExtra("orgid");//机构id
//        findViewById(R.id.ll_back).setOnClickListener(this);
//        findViewById(R.id.tv_wancheng).setOnClickListener(this);
//        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_pull);
//        listview = pullToRefreshListView.getRefreshableView();
//        adapter = new WuzhongAdapter(this);
//        listview.setAdapter(adapter);
//        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
//        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase refreshView) {
//                //下拉刷新
//                getData();
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase refreshView) {
//                //上啦加载
//                getData();
//            }
//        });
//        getData();
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.ll_back:
//                finish();
//                break;
//            case R.id.tv_wancheng:
//                tijisohuju();
//                break;
//        }
//
//    }
//
//    private void getData() {
//        String url = Constants_utl.WUZHONGMINGCHENG;//舞种名称
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        pullToRefreshListView.onRefreshComplete();
//                        Log.e(TAG, "onResponse: " + response);
//                        wuzhongxinxi = gson.fromJson(response, Wuzhongxinxi.class);
//                        if (wuzhongxinxi.code == 0) {
//                            adapter.setData(wuzhongxinxi.dancelist);
//                            adapter.notifyDataSetChanged();
//                        } else {
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                        pullToRefreshListView.onRefreshComplete();
//                        Toast.makeText(getApplicationContext(), "请求失败", Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, "onErrorResponse: " + error);
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//    }
//
//    private void tijisohuju() {
//        String url = Constants_utl.XINXI_XIUGAI;//信息修改
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e(TAG, "onResponse: " + response);
//                        yuyue = gson.fromJson(response, Yuyue.class);
//                        Toast.makeText(WuzhongxuanzeActivity.this, yuyue.msg, Toast.LENGTH_SHORT).show();
//                        Intent data = new Intent();
//                        setResult(RESULT_OK, data);
//                        finish();
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
//                params.put("trainId", orgid);
//                params.put("danceid", adapter.get() + "");
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//    }
//
//}
