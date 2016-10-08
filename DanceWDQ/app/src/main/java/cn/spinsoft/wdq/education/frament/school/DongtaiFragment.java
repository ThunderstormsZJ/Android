//package cn.spinsoft.wdq.education.frament.school;
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
//import cn.spinsoft.wdq.base.BaseFragment;
//import cn.spinsoft.wdq.education.MyApplication;
//import cn.spinsoft.wdq.education.activity.SchoolActivity;
//import cn.spinsoft.wdq.education.adapter.DongtaiAdapter;
//import cn.spinsoft.wdq.education.bean.Dongtai;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * 机构_动态页
// */
//public class DongtaiFragment extends BaseFragment {
//    private PullToRefreshListView pullToRefreshListView;
//    private TextView tv_dongtai;
//    private int page = 1;
//    private boolean is_refresh = true;
//    private String TAG = "DongtaiFragment";
//    private ListView listview;
//    private Gson gson = new Gson();
//    private String orgids;
//    private Dongtai dongtai;
//    private DongtaiAdapter adapter;
//
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_dongtai;
//    }
//
//    @Override
//    protected void initViewAndListener(View root, Bundle savedInstanceState) {
//        tv_dongtai = (TextView) root.findViewById(R.id.tv_dongtai);
//        pullToRefreshListView = (PullToRefreshListView) root.findViewById(R.id.lv_dongtai);
//        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
//        listview = pullToRefreshListView.getRefreshableView();
//        orgids = ((SchoolActivity) getActivity()).getTrainId();
//        adapter = new DongtaiAdapter(getActivity());
//        listview.setAdapter(adapter);
//        getData(1, true);
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
//                getData(page, is_refresh);
//
//            }
//        });
//
//
//    }
//
//    public void getData(final int page, final boolean is_refresh) {
//        String url = Constants_utl.DONGTAI;//动态
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        pullToRefreshListView.onRefreshComplete();
//                        Log.e(TAG, "onResponse: " + response);
//                        dongtai = gson.fromJson(response, Dongtai.class);
//                        if (dongtai.code == 0) {
//                            if (dongtai.dynamics != null && dongtai.dynamics.size() != 0) {
//                                adapter.setData(dongtai.dynamics, is_refresh);
//                                adapter.notifyDataSetChanged();
//                                tv_dongtai.setText(dongtai.dynamics.size() + "条动态");
//                            }
//                            if (dongtai.dynamics != null && dongtai.dynamics.size() == 0) {
//                                Toast.makeText(getActivity(), "该机构暂无动态!", Toast.LENGTH_SHORT).show();
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
//                params.put("trainId", "1");
//                params.put("pageNumber", page + "");
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//    }
//
//}
