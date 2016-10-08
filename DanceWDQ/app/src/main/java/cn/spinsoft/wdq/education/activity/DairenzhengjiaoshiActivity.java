//package cn.spinsoft.wdq.education.activity;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
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
//import cn.spinsoft.wdq.education.MyApplication;
//import cn.spinsoft.wdq.education.adapter.DairenzhengAdapter;
//import cn.spinsoft.wdq.education.bean.Dairenzhenglaoshi;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * Created by Administrator on 2015/12/11. 待认证教师
// */
//public class DairenzhengjiaoshiActivity extends BaseActivity {
//    private String TAG = "DairenzhengjiaoshiActivity";
//    private int userid;
//    private ListView listview;
//    private boolean is_refresh = true;
//    private int page = 1;
//    private Gson gson = new Gson();
//    private PullToRefreshListView pullToRefreshListView;
//    private DairenzhengAdapter adapter;
//    private String orgids;
//    private Dairenzhenglaoshi laoshi;
//    private TextView tv_xuexiaoname;
//
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_aboutme;
//    }
//
//    @Override
//    protected void initHandler() {
//
//    }
//
//    @Override
//    protected void initViewAndListener(Bundle savedInstanceState) {
//        //返回键
//        findViewById(R.id.ll_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        tv_xuexiaoname = (TextView) findViewById(R.id.tv_xuexiaoname);
//        tv_xuexiaoname.setText("待认证教师");
//
//        orgids = getIntent().getStringExtra("orgid");//机构id
//        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_pull);
//        listview = pullToRefreshListView.getRefreshableView();
//        adapter = new DairenzhengAdapter(DairenzhengjiaoshiActivity.this);
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
//        String url = Constants_utl.DAIRENZHENGLAOSHI;//待认证教师
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        pullToRefreshListView.onRefreshComplete();
//                        Log.e(TAG, "onResponse: " + response);
//                        laoshi = gson.fromJson(response, Dairenzhenglaoshi.class);
//                        if (laoshi.code == 0) {
//                            if (laoshi.page != null && laoshi.page.size() != 0) {
//                                adapter.setData(laoshi.page, is_refresh);
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
//                params.put("trainId", orgids);
//                params.put("pageNumber", page + "");
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//    }
//
//    public void gengxinshuju() {
//        getData(1, true);
//    }
//
//}
//
