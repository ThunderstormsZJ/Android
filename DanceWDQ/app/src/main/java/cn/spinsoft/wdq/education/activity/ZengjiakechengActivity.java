//package cn.spinsoft.wdq.education.activity;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.google.gson.Gson;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.base.BaseActivity;
//import cn.spinsoft.wdq.education.MyApplication;
//import cn.spinsoft.wdq.education.bean.Yuyue;
//import cn.spinsoft.wdq.service.LocationOnMain;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * 增加课程的Activity
// */
//public class ZengjiakechengActivity extends BaseActivity implements View.OnClickListener {
//    private TextView tv_xuexiaoname, tv_weizhi;
//    private EditText ed_biaoti, ed_miaoshu;
//    private String title, details, trainid, schoolname;
//    private String TAG = "ZengjiakechengActivity";
//    private Gson gson = new Gson();
//    private Yuyue yuyue;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_zengjiakecheng;
//    }
//
//    @Override
//    protected void initHandler() {
//
//    }
//
//    @Override
//    protected void initViewAndListener(Bundle savedInstanceState) {
//        trainid = getIntent().getStringExtra("trainid");//机构id
//        schoolname = getIntent().getStringExtra("schoolname");//学校名称
//
//        tv_weizhi = (TextView) findViewById(R.id.tv_weizhi);//位置
//        if (null == LocationOnMain.getInstance().getCity()) {
//            tv_weizhi.setText("定位不成功");
//        } else {
//            tv_weizhi.setText("" + LocationOnMain.getInstance().getCity());
//        }
//
//        tv_xuexiaoname = (TextView) findViewById(R.id.tv_xuexiaoname);
//        tv_xuexiaoname.setText(schoolname);
//
//        findViewById(R.id.tv_quxiao).setOnClickListener(this);//取消按钮
//        findViewById(R.id.tv_fabiao).setOnClickListener(this);//发表按钮
//        findViewById(R.id.iv_tianjiazhaopian).setOnClickListener(this);//点击增加图片
//
//        ed_biaoti = (EditText) findViewById(R.id.ed_biaoti);//标题输入
//        ed_miaoshu = (EditText) findViewById(R.id.ed_miaoshu);//描述
//
//
//    }
//
//    /**
//     * 点击时间的处理
//     */
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.tv_quxiao://取消
//                finish();
//                break;
//            case R.id.tv_fabiao:    //发表
//                title = ed_biaoti.getText().toString();//标题
//                details = ed_miaoshu.getText().toString();//描述
//                if (title.toString() == null || title.length() == 0 || title.equals("")) {
//                    Toast.makeText(ZengjiakechengActivity.this, "请输入标题内容！", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (details.toString() == null || details.length() == 0 || details.equals("")) {
//                    Toast.makeText(ZengjiakechengActivity.this, "请输入课程描述内容！", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                fabiao();
//                break;
//            case R.id.iv_tianjiazhaopian://添加照片
//
//                break;
//        }
//
//    }
//
//    private void fabiao() {
//
//        String url = Constants_utl.ZENGJIAKECHENG;//增加课程
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e(TAG, "onResponse: " + response);
//                        yuyue = gson.fromJson(response, Yuyue.class);
//                        if (yuyue.code == 0) {
//                            Toast.makeText(ZengjiakechengActivity.this, yuyue.msg + "", Toast.LENGTH_SHORT).show();
//                        }
//                        //关闭这个类
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
//                params.put("trainId", trainid);
//                params.put("title", title);
//                params.put("details", details);
//                Log.e(TAG, "getParams: " + title + "----" + details);
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost_fabiao");
//        MyApplication.getHttpQueue().add(postRequest);
//    }
//}
