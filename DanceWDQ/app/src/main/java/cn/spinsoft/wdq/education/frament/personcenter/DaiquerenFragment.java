//package cn.spinsoft.wdq.education.frament.personcenter;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.ListView;
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
//import cn.spinsoft.wdq.base.BaseFragment;
//import cn.spinsoft.wdq.education.MyApplication;
//import cn.spinsoft.wdq.education.activity.YuyueOrderActivity;
//import cn.spinsoft.wdq.education.adapter.DingdanAdapter;
//import cn.spinsoft.wdq.education.bean.Dingdan;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * Created by Administrator on 2015/12/14.
// */
//public class DaiquerenFragment extends BaseFragment {
//    private PullToRefreshListView pullToRefreshListView;
//    private String TAG = "DaiquerenFragment";
//    private ListView listview;
//    private boolean is_refresh = true;
//    private int page = 1;
//    private String orgids;
//    private DingdanAdapter adapter;
//    private Gson gson = new Gson();
//    private Dingdan dingdan;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_allorder;
//    }
//
//    @Override
//    protected void initViewAndListener(View root, Bundle savedInstanceState) {
//        orgids = ((YuyueOrderActivity) getActivity()).getTrainId();//机构id
//        pullToRefreshListView = (PullToRefreshListView) root.findViewById(R.id.lv_dingdan);
//        listview = pullToRefreshListView.getRefreshableView();
//        adapter = new DingdanAdapter(getActivity());
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
//        String url = Constants_utl.YUYUEDINGDAN;//预约订单
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        pullToRefreshListView.onRefreshComplete();
//                        Log.e(TAG, "onResponse: " + response);
//                        dingdan = gson.fromJson(response, Dingdan.class);
//                        if (dingdan.code == 0) {
//                            if (dingdan.page != null && dingdan.page.size() != 0) {
//                                adapter.setData(dingdan.page, is_refresh);
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
//                params.put("type", "0");
//                params.put("pageNumber", page + "");
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//    }
//
//}
