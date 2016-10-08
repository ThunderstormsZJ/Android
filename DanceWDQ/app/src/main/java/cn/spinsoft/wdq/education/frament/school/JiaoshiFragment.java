//package cn.spinsoft.wdq.education.frament.school;
//
//
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
//import cn.spinsoft.wdq.base.BaseFragment;
//import cn.spinsoft.wdq.base.BaseHandler;
//import cn.spinsoft.wdq.education.MyApplication;
//import cn.spinsoft.wdq.education.activity.SchoolActivity;
//import cn.spinsoft.wdq.education.adapter.JiaoshiAdapter;
//import cn.spinsoft.wdq.education.bean.Jiaoshi;
//import cn.spinsoft.wdq.service.LocationOnMain;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * 教师的fragment
// */
//public class JiaoshiFragment extends BaseFragment {
//    private PullToRefreshListView pullToRefreshListView;
//    private String TAG = "JiaoshiFragment";
//    private int userid;
//    private ListView listview;
//    private boolean is_refresh = true;
//    private int page = 1;
//    private String orgids;
//    private JiaoshiAdapter adapter;
//    private Gson gson = new Gson();
//    private Jiaoshi jiaoshi;
//    Double longitude, latitude;
//
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_jiaoshi;
//    }
//
//    @Override
//    protected void initViewAndListener(View root, Bundle savedInstanceState) {
//        double[] LOCATION = LocationOnMain.getInstance().getLocation();
//        longitude = LOCATION[0];
//        latitude = LOCATION[1];
//
//        orgids = ((SchoolActivity) getActivity()).getTrainId();//机构id
//        userid = BaseHandler.watcherUserId;//用户id
//
//        Log.e(TAG, "JiaoshiFragment:userid " + userid + "----" + orgids);
//        pullToRefreshListView = (PullToRefreshListView) root.findViewById(R.id.lv_laoshi);
//        listview = pullToRefreshListView.getRefreshableView();
//        adapter = new JiaoshiAdapter(getActivity());
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
//        String url = Constants_utl.JIGOUJIAOSHI;//教师
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        pullToRefreshListView.onRefreshComplete();
//                        Log.e(TAG, "onResponse: " + response);
//                        jiaoshi = gson.fromJson(response, Jiaoshi.class);
//                        if (jiaoshi.code == 0) {
//                            if (jiaoshi.list != null && jiaoshi.list.size() != 0) {
//                                adapter.setData(jiaoshi.list, is_refresh);
//                                adapter.notifyDataSetChanged();
//                            }
//                            if (jiaoshi.list != null && jiaoshi.list.size() == 0) {
//                                Toast.makeText(getActivity(), "该机构暂无教师!", Toast.LENGTH_SHORT).show();
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
//                params.put("userId", userid + "");
//                params.put("pageNumber", page + "");
//                params.put("longitude", longitude + "");
//                params.put("latitude", latitude + "");
//
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//    }
//
//
//    public void shuaxin() {
//        getData(1, true);
//    }
//}
//
