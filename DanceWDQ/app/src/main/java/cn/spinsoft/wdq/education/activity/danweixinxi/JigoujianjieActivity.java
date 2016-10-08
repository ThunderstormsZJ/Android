//package cn.spinsoft.wdq.education.activity.danweixinxi;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.EditText;
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
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * Created by Administrator on 2015/12/17.机构简介
// */
//public class JigoujianjieActivity extends BaseActivity implements View.OnClickListener {
//    private String TAG = "JigoujianjieActivity";
//    private EditText tv_jianjie;
//    private String intro;
//    private Gson gson = new Gson();
//    private Yuyue yuyue;
//    private String orgid;
//    private String name;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_jigoujianjie;
//    }
//
//    @Override
//    protected void initHandler() {
//
//    }
//
//    @Override
//    protected void initViewAndListener(Bundle savedInstanceState) {
//        orgid = getIntent().getStringExtra("orgid");//机构id
//        intro = getIntent().getStringExtra("jianjie");
//        tv_jianjie = (EditText) findViewById(R.id.tv_jianjie);
//        tv_jianjie.setText(intro);
//
//
//        findViewById(R.id.ll_back).setOnClickListener(this);
//        findViewById(R.id.tv_wancheng).setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.ll_back:
//                finish();
//                break;
//            case R.id.tv_wancheng:
//                name = tv_jianjie.getText().toString();
//                if (name == null || name.length() == 0 || name.equals("")) {
//                    Toast.makeText(this, "请输入机构名称", Toast.LENGTH_SHORT).show();
//                } else {
//                    xiugaijianjie();
//                }
//
//                break;
//
//
//        }
//
//    }
//
//    public void xiugaijianjie() {
//        {
//            String url = Constants_utl.XINXI_XIUGAI;//信息修改
//            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            Log.e(TAG, "onResponse: " + response);
//                            yuyue = gson.fromJson(response, Yuyue.class);
//                            Toast.makeText(JigoujianjieActivity.this, yuyue.msg, Toast.LENGTH_SHORT).show();
//                            Intent data = new Intent();
//                            setResult(RESULT_OK, data);
//                            finish();
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            error.printStackTrace();
//                            Log.e(TAG, "onErrorResponse: " + error);
//                        }
//                    }
//            ) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    // the POST parameters:
//                    params.put("trainId", orgid);
//                    params.put("intro", tv_jianjie.getText().toString());
//                    return params;
//                }
//            };
//            postRequest.setTag("volleypost");
//            MyApplication.getHttpQueue().add(postRequest);
//        }
//    }
//}
