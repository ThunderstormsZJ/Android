//package cn.spinsoft.wdq.education.frament.edcation;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ListView;
//import android.widget.PopupWindow;
//import android.widget.RelativeLayout;
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
//import cn.spinsoft.wdq.education.adapter.PeixunjigouAdapter;
//import cn.spinsoft.wdq.education.bean.Jigouliebiao;
//import cn.spinsoft.wdq.service.LocationOnMain;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
//
///**
// * 机构列表的主页的fragment
// */
//public class MainFragment extends BaseFragment implements View.OnClickListener {
//    public String TAG = MainFragment.class.getSimpleName();
//    public PullToRefreshListView pullToRefreshListView;
//    private Gson gson = new Gson();
//    private ListView listview;
//    private PeixunjigouAdapter adapter;
//    private int page = 1;
//    private Boolean is_refresh = true;
//    private RelativeLayout rl_fujin, rl_wuzhong, rl_renqi;
//    private Jigouliebiao libiao;
//    private int userid;
//    Double longitude, latitude;//经纬度
//    private PopupWindow popupWindow;//附件 舞种 人气的
//    public String distance = "";//距离初始值
//    public String type = "";   //舞种
//    public String popular = "";//人气
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragement_org;
//    }
//
//    @Override
//    protected void initViewAndListener(View root, Bundle savedInstanceState) {
//        //获取地址的经纬度
//        double[] LOCATION = LocationOnMain.getInstance().getLocation();
//        longitude = LOCATION[0];
//        latitude = LOCATION[1];
//
//        root.findViewById(R.id.videos_back).setOnClickListener(this);//返回键
//        rl_fujin = (RelativeLayout) root.findViewById(R.id.rl_fujin);
//        rl_fujin.setOnClickListener(this);//附近
//        rl_wuzhong = (RelativeLayout) root.findViewById(R.id.rl_wuzhong);
//        rl_wuzhong.setOnClickListener(this);//舞种
//        rl_renqi = (RelativeLayout) root.findViewById(R.id.rl_renqi);
//        rl_renqi.setOnClickListener(this);//人气
//        userid = BaseHandler.watcherUserId;
//        Log.e(TAG, "initViewAndListener:userid " + userid + "经度" + longitude + "-----纬度" + latitude);
//        pullToRefreshListView = (PullToRefreshListView) root.findViewById(R.id.lv_peixun);
//        listview = pullToRefreshListView.getRefreshableView();
//        adapter = new PeixunjigouAdapter(getActivity());
//        listview.setAdapter(adapter);
//        getData(1, true);
//
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
//                getData(page, is_refresh);
//
//            }
//        });
//
//
//    }
//
//    private void getData(final int page, final boolean is_refresh) {
//
//        String url = Constants_utl.JIGOU_LIEBIAO;//请求地址
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        pullToRefreshListView.onRefreshComplete();
//                        Log.e(TAG, "response: " + response);
//                        libiao = gson.fromJson(response, Jigouliebiao.class);
//                        if (libiao.code == 0) {
//                            if (libiao.list != null && libiao.list.size() != 0) {
//                                adapter.setData(libiao.list, is_refresh);
//                                adapter.notifyDataSetChanged();
//                            }
//                        } else {
//                            //do nothing
//                        }
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                        pullToRefreshListView.onRefreshComplete();
//                        Log.e(TAG, "onErrorResponse: " + error);
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                // the POST parameters:
//                params.put("longitude", longitude + "");
//                params.put("latitude", latitude + "");
//                params.put("type", type + "");
//                params.put("name", "");
//                params.put("pageNumber", page + "");
//                params.put("pageSize", "10");
//                params.put("userId", userid + "");
//                params.put("popular", popular + "");
//                params.put("distance", distance);
//                Log.e(TAG, "getParams: distance=" + distance + "----type" + type + "----popular" + popular);
//                return params;
//
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.rl_fujin://附近
//                showpopwindow();
//                break;
//            case R.id.rl_wuzhong://舞种
//                showwuzhongpop();
//                break;
//            case R.id.rl_renqi://人气
//                showRenqi();
//                break;
//            case R.id.videos_back:
//                getActivity().finish();
//                break;
//
//        }
//    }
//
//    private void showRenqi() {
//        // 获取自定义布局文件activity_popupwindow_left.xml的视图
//        View popupWindow_view = View.inflate(getActivity(), R.layout.renqi_popwindow, null);
//        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
//        popupWindow = new PopupWindow(popupWindow_view, 240, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        // 设置动画效果
//        popupWindow.setAnimationStyle(R.style.AnimationFade);
//        // 这里是位置显示方式,在屏幕的左侧
////        popupWindow.showAtLocation(v, Gravity.LEFT, 0, 0);
//        //在控件的下方显示
//        popupWindow.showAsDropDown(rl_renqi);
//        // 点击其他地方消失
//        popupWindow_view.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // TODO Auto-generated method stub
//                if (popupWindow != null && popupWindow.isShowing()) {
//                    popupWindow.dismiss();
//                    popupWindow = null;
//                }
//                return false;
//            }
//        });
//
//        popupWindow_view.findViewById(R.id.bt_fujin_1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//                popular = "";//人气默认
//                getData(1, true);
//            }
//        });
//        popupWindow_view.findViewById(R.id.bt_fujin_2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//                //人气从高到低
//                popular = "0";
//                getData(1, true);
//            }
//        });
//        popupWindow_view.findViewById(R.id.bt_fujin_3).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//                //从低到高
//                popular = "1";
//                getData(1, true);
//            }
//        });
//    }
//
//    private void showwuzhongpop() {
//        // 获取自定义布局文件activity_popupwindow_left.xml的视图
//        View popupWindow_view = View.inflate(getActivity(), R.layout.wuzhong_popwindow, null);
//        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
//        popupWindow = new PopupWindow(popupWindow_view, 240, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        // 设置动画效果
//        popupWindow.setAnimationStyle(R.style.AnimationFade);
//        // 这里是位置显示方式,在屏幕的左侧
////        popupWindow.showAtLocation(v, Gravity.LEFT, 0, 0);
//        //在控件的下方显示
//        popupWindow.showAsDropDown(rl_wuzhong);
//        // 点击其他地方消失
//        popupWindow_view.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // TODO Auto-generated method stub
//                if (popupWindow != null && popupWindow.isShowing()) {
//                    popupWindow.dismiss();
//                    popupWindow = null;
//                }
//                return false;
//            }
//        });
//
//        popupWindow_view.findViewById(R.id.bt_fujin_1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//                type = "";
//                getData(1, true);
//            }
//        });
//        popupWindow_view.findViewById(R.id.bt_fujin_2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//                type = "1";
//                getData(1, true);
//            }
//        });
//        popupWindow_view.findViewById(R.id.bt_fujin_3).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//                type = "2";
//                getData(1, true);
//
//
//            }
//        });
//        popupWindow_view.findViewById(R.id.bt_fujin_4).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//                type = "3";
//                getData(1, true);
//
//
//            }
//        });
//        popupWindow_view.findViewById(R.id.bt_fujin_5).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//                type = "4";
//                getData(1, true);
//
//            }
//        });
//        popupWindow_view.findViewById(R.id.bt_fujin_6).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//                type = "5";
//                getData(1, true);
//
//            }
//        });
//    }
//
//    private void showpopwindow() {
//        // 获取自定义布局文件activity_popupwindow_left.xml的视图
//        View popupWindow_view = View.inflate(getActivity(), R.layout.jigou_fujin_popwindow, null);
//        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
//        popupWindow = new PopupWindow(popupWindow_view, 240, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        // 设置动画效果
//        popupWindow.setAnimationStyle(R.style.AnimationFade);
//        // 这里是位置显示方式,在屏幕的左侧
////        popupWindow.showAtLocation(v, Gravity.LEFT, 0, 0);
//        //在控件的下方显示
//        popupWindow.showAsDropDown(rl_fujin);
//        // 点击其他地方消失
//        popupWindow_view.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // TODO Auto-generated method stub
//                if (popupWindow != null && popupWindow.isShowing()) {
//                    popupWindow.dismiss();
//                    popupWindow = null;
//                }
//                return false;
//            }
//        });
//
//        popupWindow_view.findViewById(R.id.bt_fujin_1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//                distance = "";
//                getData(1, true);
//            }
//        });
//        popupWindow_view.findViewById(R.id.bt_fujin_2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//                distance = "500";
//                getData(1, true);
//            }
//        });
//        popupWindow_view.findViewById(R.id.bt_fujin_3).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//                distance = "1000";
//                getData(1, true);
//            }
//        });
//        popupWindow_view.findViewById(R.id.bt_fujin_4).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//                distance = "2000";
//                getData(1, true);
//            }
//        });
//        popupWindow_view.findViewById(R.id.bt_fujin_5).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//                distance = "3000";
//                getData(1, true);
//            }
//        });
//        popupWindow_view.findViewById(R.id.bt_fujin_6).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//                distance = "5000";
//                getData(1, true);
//            }
//        });
//    }
//
//}
