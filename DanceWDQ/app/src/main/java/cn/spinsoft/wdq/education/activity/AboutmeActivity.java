//package cn.spinsoft.wdq.education.activity;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
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
//import cn.spinsoft.wdq.education.MyApplication;
//import cn.spinsoft.wdq.education.adapter.AboutmeAdapter;
//import cn.spinsoft.wdq.education.bean.Yuwoxiangguan;
//import cn.spinsoft.wdq.utils.Constants;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * 与我相关的类
// */
//public class AboutmeActivity extends BaseActivity implements View.OnClickListener {
//    private PullToRefreshListView pullToRefreshListView;
//    private String TAG = "AboutmeActivity";
//    private String userid, theuserids, objectids;
//    private ListView listview;
//    private boolean is_refresh = true;
//    private int page = 1;
//    private String orgid;
//    private Gson gson = new Gson();
//    private TextView tv_xuexiaoname;
//    private Yuwoxiangguan yuwoxiangguan;
//    private AboutmeAdapter adapter;
//    public EditText ed_shurukuang;
//    public LinearLayout ll_shurukuang;
//    private String fabuneirong;
//    private int types;
//    private boolean is_from_jigou;
//    private String QINGQIUCANSHU, ID;
//
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_aboutme;
//    }
//
//    @Override
//    protected void initHandler() {
//    }
//
//    @Override
//    protected void initViewAndListener(Bundle savedInstanceState) {
//        //判断从那里进来的 机构中心还是个人中心
//        is_from_jigou = getIntent().getBooleanExtra("FROM_JIGOU", false);
//        if (is_from_jigou) {
//            //来自机构
//            QINGQIUCANSHU = "trainId";
//            ID = String.valueOf(getIntent().getIntExtra(Constants.Strings.USER_ID, -1));
//        } else {
//            QINGQIUCANSHU = "userid";
//            ID = String.valueOf(getIntent().getIntExtra(Constants.Strings.USER_ID, -1));//用户id
//        }
//
//        findViewById(R.id.bt_fabiao).setOnClickListener(this);
//        ll_shurukuang = (LinearLayout) findViewById(R.id.ll_shurukuang);//输入框布局
//        ed_shurukuang = (EditText) findViewById(R.id.ed_shurukuang);//输入框
//        tv_xuexiaoname = (TextView) findViewById(R.id.tv_xuexiaoname);
//        tv_xuexiaoname.setText("与我相关");
//
//        findViewById(R.id.ll_back).setOnClickListener(this);
//        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_pull);
//        listview = pullToRefreshListView.getRefreshableView();
//        adapter = new AboutmeAdapter(AboutmeActivity.this);
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
//        String url = Constants_utl.ABOUTME;//与我相关
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        pullToRefreshListView.onRefreshComplete();
//                        Log.e(TAG, "onResponse: " + response);
//                        yuwoxiangguan = gson.fromJson(response, Yuwoxiangguan.class);
//                        if (yuwoxiangguan.code == 0) {
//                            if (yuwoxiangguan.related != null && yuwoxiangguan.related.size() != 0) {
//                                adapter.setData(yuwoxiangguan.related, is_refresh);
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
////                params.put(QINGQIUCANSHU,ID+ "");
//                //TODO
//                params.put(QINGQIUCANSHU, "4");
//                params.put("pageNumber", page + "");
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.ll_back://返回键
//                finish();
//                break;
//            case R.id.bt_fabiao://发表信息按钮
//                fabuneirong = ed_shurukuang.getText().toString();//发布内容
//                fabuneirong();
//
//                ll_shurukuang.setVisibility(View.GONE);//隐藏输入框
//                //隐藏jianpan
//                InputMethodManager imm = (InputMethodManager) ed_shurukuang.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(ed_shurukuang.getWindowToken(), 0);
//                break;
//
//
//        }
//    }
//
//    /**
//     * 回复
//     */
//    public void huifu(int type, String userid, String theuserid, String objectid) {
//        types = type;
//        theuserids = theuserid;
//        objectids = objectid;
//
//        ll_shurukuang.setVisibility(View.VISIBLE);
//        ed_shurukuang.setText("");//清空上次信息
//        //让对话框获得焦点
//        ed_shurukuang.setFocusable(true);
//        ed_shurukuang.setFocusableInTouchMode(true);
//        ed_shurukuang.requestFocus();
//        ed_shurukuang.findFocus();
//        //打开软键盘
//        InputMethodManager imm = (InputMethodManager) ed_shurukuang.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//    }
//
//
//    private void fabuneirong() {
//
//        String url = Constants_utl.HUIFUNEIRONHG;//回复内容
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e(TAG, "onResponse: " + response);
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
//                params.put(QINGQIUCANSHU, ID + "");
//                params.put("theuserid", theuserids);
//                params.put("type", types + "");
//                params.put("objectId", objectids);
//                params.put("content", fabuneirong + "");
//                Log.e(TAG, "getParams: " + theuserids + "-----" + types + "------" + objectids + "-----" + fabuneirong);
//                return params;
//
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//    }
//
//}
