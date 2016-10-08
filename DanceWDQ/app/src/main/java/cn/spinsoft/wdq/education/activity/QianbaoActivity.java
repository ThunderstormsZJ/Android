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
//import cn.spinsoft.wdq.education.adapter.QianbaoAdapter;
//import cn.spinsoft.wdq.education.bean.Qianbao;
//import cn.spinsoft.wdq.utils.Constants;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * 钱包的类
// */
//public class QianbaoActivity extends BaseActivity implements View.OnClickListener {
//    private PullToRefreshListView pullToRefreshListView;
//    private QianbaoAdapter adapter;
//    private int page = 1;
//    private ListView listview;
//    private boolean is_refresh;
//    private String TAG = "QianbaoActivity";
//    private String userid;
//    private Qianbao qianbao;
//    private Gson gson = new Gson();
//    private TextView tv_qian;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_qianbao;
//    }
//
//    @Override
//    protected void initHandler() {
//
//    }
//
//    @Override
//    protected void initViewAndListener(Bundle savedInstanceState) {
//        userid = String.valueOf(getIntent().getIntExtra(Constants.Strings.USER_ID, 0));//用户id
//        tv_qian = (TextView) findViewById(R.id.tv_qian);
//        findViewById(R.id.ll_qianbao_back).setOnClickListener(this);
//        findViewById(R.id.ll_changjianwenti).setOnClickListener(this);
//        findViewById(R.id.iv_tixian).setOnClickListener(this);
//        pullToRefreshListView = (com.handmark.pulltorefresh.library.PullToRefreshListView) findViewById(R.id.lv_laoshi);
//        listview = pullToRefreshListView.getRefreshableView();
//        adapter = new QianbaoAdapter(this);
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
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.ll_qianbao_back://返回键
//                finish();
//            case R.id.ll_changjianwenti:  //常见问题
//
//                break;
//            case R.id.iv_tixian://提现
//
//                break;
//        }
//
//
//    }
//
//    public void getData(final int page, final boolean is_refresh) {
//        String url = Constants_utl.QIANBAO;//钱包
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        pullToRefreshListView.onRefreshComplete();
//                        Log.e(TAG, "onResponse: " + response);
//                        qianbao = gson.fromJson(response, Qianbao.class);
//                        if (qianbao.code == 0) {
//                            tv_qian.setText(qianbao.count + "");
//                            if (qianbao.page != null && qianbao.page.size() != 0) {
//                                adapter.setData(qianbao.page, is_refresh);
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
//                params.put("userId", userid);
//                params.put("pageNumber", page + "");
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//    }
//}
