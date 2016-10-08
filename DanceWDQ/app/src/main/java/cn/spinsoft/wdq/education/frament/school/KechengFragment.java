//package cn.spinsoft.wdq.education.frament.school;
//
//
//import android.content.Intent;
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
//import cn.spinsoft.wdq.base.BaseHandler;
//import cn.spinsoft.wdq.education.MyApplication;
//import cn.spinsoft.wdq.education.activity.SchoolActivity;
//import cn.spinsoft.wdq.education.activity.ZengjiakechengActivity;
//import cn.spinsoft.wdq.education.adapter.KechengAdapter;
//import cn.spinsoft.wdq.education.bean.Kecheng;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * 学校_课程
// */
//public class KechengFragment extends BaseFragment implements View.OnClickListener {
//    private TextView tv_kechengshu, tv_zengjiakecheng;
//    private PullToRefreshListView pullToRefreshListView;
//    private ListView listview;
//    private KechengAdapter adapter;
//    private String TAG = "KechengFragment";
//    private Gson gson = new Gson();
//    private int page = 1;
//    private Kecheng kecheng;
//    private boolean is_refresh = true;
//    private int userid;
//    private String trainId;
//    private String schoolname;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_kecheng;
//    }
//
//    @Override
//    protected void initViewAndListener(View root, Bundle savedInstanceState) {
//        schoolname = ((SchoolActivity) getActivity()).getSchoolname();
//        userid = BaseHandler.watcherUserId;
//        trainId = ((SchoolActivity) getActivity()).getTrainId();
//        Log.e(TAG, "initViewAndListener:userid " + userid + trainId);
//        tv_kechengshu = (TextView) root.findViewById(R.id.tv_kechengshu);
//        tv_zengjiakecheng = (TextView) root.findViewById(R.id.tv_zengjiakecheng);
//        tv_zengjiakecheng.setOnClickListener(this);//增加课程
//        pullToRefreshListView = (PullToRefreshListView) root.findViewById(R.id.lv_kecheng);
//        listview = pullToRefreshListView.getRefreshableView();
//        adapter = new KechengAdapter(getActivity());
//        listview.setAdapter(adapter);
//        getData(1, true);
//        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
//        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
//
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase refreshView) {
//                page = 1;
//                is_refresh = true;
//                getData(page, is_refresh);
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase refreshView) {
//                page++;
//                is_refresh = false;
//                getData(page, is_refresh);
//            }
//        });
//
//    }
//
//    public void getData(final int page, final boolean is_refresh) {
//        {
//            String url = Constants_utl.JIGOUKECHENG;//课程
//            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            pullToRefreshListView.onRefreshComplete();
//                            Log.e(TAG, "onResponse: " + response);
//                            kecheng = gson.fromJson(response, Kecheng.class);
//                            if (kecheng.code == 0) {
//                                tv_kechengshu.setText(kecheng.count + "款课程");
//                                //如果两个id一样就可以新增课程
//                                Xinzengkecheng();
//
//                                if (kecheng.courses != null && kecheng.courses.size() != 0) {
//                                    adapter.setData(kecheng.courses, is_refresh, trainId);
//                                    adapter.notifyDataSetChanged();
//                                }
//                                if (kecheng.courses != null && kecheng.courses.size() == 0) {
//                                    Toast.makeText(getActivity(), "该机构暂无课程", Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
////                                Toast.makeText(getActivity(), "请求失败", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        private void Xinzengkecheng() {
//                            //自己是机构就可以增加新课程
////                            if (userid==kecheng.courses.get(0).courseid){
//                            if (true) {
//                                tv_zengjiakecheng.setVisibility(View.VISIBLE);
//                                tv_zengjiakecheng.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        //跳转到新增课程的编辑面页
//                                        Intent intent = new Intent(getActivity(), ZengjiakechengActivity.class);
//                                        intent.putExtra("schoolname", schoolname);
//                                        intent.putExtra("trainid", trainId);
//                                        getActivity().startActivity(intent);
//                                    }
//                                });
//                            } else {
//                                tv_zengjiakecheng.setVisibility(View.INVISIBLE);
//                            }
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
//                    params.put("id", "2");
//                    params.put("trainId", trainId);
//                    params.put("pageNumber", page + "");
//                    params.put("pageSize", "10");
//                    return params;
//                }
//            };
//            postRequest.setTag("volleypost");
//            MyApplication.getHttpQueue().add(postRequest);
//        }
//
//    }
//
//    /**
//     * 点击事件的处理
//     */
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            //增加课程
//            case R.id.tv_zengjiakecheng:
//                break;
//        }
//
//
//    }
//
//    public void shuaxinshuju() {
//        getData(1, true);
//    }
//}
