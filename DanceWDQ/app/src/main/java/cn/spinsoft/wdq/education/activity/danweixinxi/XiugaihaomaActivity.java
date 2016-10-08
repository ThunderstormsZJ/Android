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
// * 修改单位信息的类_修改号码
// */
//public class XiugaihaomaActivity extends BaseActivity implements View.OnClickListener {
//    private EditText ed_name;
//    private String name;
//    private String TAG = "XiugaihaomaActivity";
//    private String orgid;
//    private Yuyue yuyue;
//    private Gson gson = new Gson();
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_xiugaihaoma;
//    }
//
//    @Override
//    protected void initHandler() {
//
//    }
//
//    @Override
//    protected void initViewAndListener(Bundle savedInstanceState) {
//        orgid = getIntent().getStringExtra("orgid");
//        findViewById(R.id.ll_back).setOnClickListener(this);
//        findViewById(R.id.tv_wancheng).setOnClickListener(this);
//        ed_name = (EditText) findViewById(R.id.ed_name);
//    }
//
//    /**
//     * 点击时间的处理
//     */
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.ll_back:
//                finish();
//                break;
//            case R.id.tv_wancheng:
//                name = ed_name.getText().toString();
//                if (name == null || name.length() == 0 || name.equals("")) {
//                    Toast.makeText(this, "请输联系方式", Toast.LENGTH_SHORT).show();
//                } else {
//                    xiugaimingzi();
//                }
//                break;
//        }
//
//    }
//
//    private void xiugaimingzi() {
//        String url = Constants_utl.XINXI_XIUGAI;//信息修改
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e(TAG, "onResponse: " + response);
//                        yuyue = gson.fromJson(response, Yuyue.class);
//                        Toast.makeText(XiugaihaomaActivity.this, yuyue.msg, Toast.LENGTH_SHORT).show();
//                        Intent data = new Intent();
//                        data.putExtra("name", name);
//                        setResult(RESULT_OK, data);
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
//                // the POST parameters:
//                params.put("trainId", orgid);
//                params.put("mobile", name + "");
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//    }
//
//
//}
