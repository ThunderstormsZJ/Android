//package cn.spinsoft.wdq.education.frament.school;
//
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.GridView;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.google.gson.Gson;
//import com.handmark.pulltorefresh.library.PullToRefreshBase;
//import com.handmark.pulltorefresh.library.PullToRefreshGridView;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.base.BaseFragment;
//import cn.spinsoft.wdq.base.BaseHandler;
//import cn.spinsoft.wdq.education.MyApplication;
//import cn.spinsoft.wdq.education.activity.SchoolActivity;
//import cn.spinsoft.wdq.education.adapter.ZuopinAdapter;
//import cn.spinsoft.wdq.education.bean.Zuopin;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * 作品
// */
//public class ZuopinFragment extends BaseFragment {
//    private String TAG = "ZuopinFragment";
//    private int userid;
//    private boolean is_refresh = true;
//    private int page = 1;
//    private String orgids;
//    private Gson gson = new Gson();
//    private Zuopin zuopin;
//    private ZuopinAdapter adapter;
//    private com.handmark.pulltorefresh.library.PullToRefreshGridView pullToRefreshGridVie;
//
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_zuopin;
//    }
//
//    @Override
//    protected void initViewAndListener(View root, Bundle savedInstanceState) {
//        pullToRefreshGridVie = (PullToRefreshGridView) root.findViewById(R.id.lv_zuopin);
//        pullToRefreshGridVie.
//                setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
//                    @Override
//                    public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
//                        page = 1;
//                        is_refresh = true;
//                        getData(1, true);
//                    }
//
//                    @Override
//                    public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
//                        page++;
//                        is_refresh = false;
//                        getData(page, is_refresh);
//                    }
//                });
//        adapter = new ZuopinAdapter(getActivity());
//        pullToRefreshGridVie.setAdapter(adapter);
//        orgids = ((SchoolActivity) getActivity()).getTrainId();//机构id
//        userid = BaseHandler.watcherUserId;//用户id
//        getData(1, true);
//
//
//    }
//
//    public void getData(final int page, final boolean is_refresh) {
//        {
//            String url = Constants_utl.ZUOPIN;//作品
//            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            Log.e(TAG, "onResponse: " + response);
//                            pullToRefreshGridVie.onRefreshComplete();
//                            zuopin = gson.fromJson(response, Zuopin.class);
//                            if (zuopin.code == 0) {
//                                if (zuopin.videos != null && zuopin.videos.size() != 0) {
//                                    adapter.setData(zuopin.videos, is_refresh);
//                                    adapter.notifyDataSetChanged();
//                                }
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
//                            pullToRefreshGridVie.onRefreshComplete();
//                            error.printStackTrace();
//                            Log.e(TAG, "onErrorResponse: " + error);
//                        }
//                    }
//            ) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    // the POST parameters:
//                    params.put("trainId", orgids);
//                    params.put("pageSize", "10");
//                    params.put("pageNumber", page + "");
//                    return params;
//                }
//            };
//            postRequest.setTag("volleypost");
//            MyApplication.getHttpQueue().add(postRequest);
//        }
//
//    }
//}
