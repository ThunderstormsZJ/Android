//package cn.spinsoft.wdq.education.activity;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.PopupWindow;
//import android.widget.TextView;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.google.gson.Gson;
//import com.squareup.picasso.Picasso;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.base.BaseActivity;
//import cn.spinsoft.wdq.base.BaseHandler;
//import cn.spinsoft.wdq.education.MyApplication;
//import cn.spinsoft.wdq.education.activity.danweixinxi.DanweimingchengActivity;
//import cn.spinsoft.wdq.education.activity.danweixinxi.JigoujianjieActivity;
//import cn.spinsoft.wdq.education.activity.danweixinxi.WuzhongxuanzeActivity;
//import cn.spinsoft.wdq.education.activity.danweixinxi.XiugaihaomaActivity;
//import cn.spinsoft.wdq.education.bean.Yuyue;
//import cn.spinsoft.wdq.education.bean.Zhongxinxiangqing;
//import cn.spinsoft.wdq.education.utils.UIUtils;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * 单位信息的类
// */
//public class DanweixinxiActivity extends BaseActivity implements View.OnClickListener {
//    private ImageView iv_jigou_touxiang;
//    private TextView tv_fujian, tv_jigou, tv_wuzhong, tv_tell, tv_jigoujianjie;
//    private Intent intent;
//    private String orgid, userid;
//    private String TAG = "DanweixinxiActivity";
//    private Gson gson = new Gson();
//    private Zhongxinxiangqing zhongxinxiangqing;
//    public String name = "";
//    private PopupWindow popupWindow;
//    private Uri cacheUri;
//    private Yuyue yuyue;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_danweixinxi;
//    }
//
//    @Override
//    protected void initHandler() {
//    }
//
//    @Override
//    protected void initViewAndListener(Bundle savedInstanceState) {
//
//        userid = BaseHandler.watcherUserId + "";//用户id
//        orgid = getIntent().getStringExtra("orgid");
//        iv_jigou_touxiang = (ImageView) findViewById(R.id.iv_jigou_touxiang);//单位头像
//
//        tv_jigou = (TextView) findViewById(R.id.tv_jigou);//机构名称
//        tv_wuzhong = (TextView) findViewById(R.id.tv_wuzhong);//舞种
//        tv_tell = (TextView) findViewById(R.id.tv_tel);//联系方式
//        tv_jigoujianjie = (TextView) findViewById(R.id.tv_jigoujianjie);//机构简介
//        tv_fujian = (TextView) findViewById(R.id.tv_fujian);//附件
//
//        findViewById(R.id.rl_biaozhi).setOnClickListener(this);
//        findViewById(R.id.rl_jigoumingcheng).setOnClickListener(this);
//        findViewById(R.id.rl_wuzhong).setOnClickListener(this);
//        findViewById(R.id.rl_lianxifangshi).setOnClickListener(this);
//        findViewById(R.id.rl_jigoujianjie).setOnClickListener(this);
//        findViewById(R.id.rl_fujian).setOnClickListener(this);
//        findViewById(R.id.ll_back).setOnClickListener(this);//返回键
//
//        //联网请求单位信息
//        getDanweixinxi();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case 101:
//                    String name = data.getStringExtra("name");
//                    tv_jigou.setText(name);
//                    break;
//                case 102:
//                    getDanweixinxi();
//                    break;
//                case 103:
//                    getDanweixinxi();
//                    break;
//                case 104:
//                    getDanweixinxi();
//                    break;
//                case 105: //相机
//                    startPhotoZoom(cacheUri, 107);
//                    break;
//
//                case 106://相册
//                    Uri selectedImage = data.getData();
//                    startPhotoZoom(selectedImage, 107);
//                    break;
//                case 107:
//                    Bitmap bit = data.getExtras().getParcelable("data");
//                    String picturePath = UIUtils.saveBitmap(bit);//这里得到的是图片的地址
//                    Log.e(TAG, "onActivityResult: " + picturePath);
//                    //上传到服务器
////                    wocao();
//
//
//                    break;
//
//
//            }
//
//        }
//
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.ll_back://返回
//                finish();
//                break;
//            case R.id.rl_biaozhi://修改头像
//                showpopwindow();
//                break;
//            case R.id.rl_jigoumingcheng://修改单位名称
//                intent = new Intent(this, DanweimingchengActivity.class);
//                intent.putExtra("orgid", orgid);
//                startActivityForResult(intent, 101);
//                break;
//            case R.id.rl_wuzhong://修改舞种
//                intent = new Intent(this, WuzhongxuanzeActivity.class);
//                intent.putExtra("orgid", orgid);
//                startActivityForResult(intent, 102);
//                break;
//            case R.id.rl_lianxifangshi://修改联系方式
//                if (orgid != null) {
//                    intent = new Intent(this, XiugaihaomaActivity.class);
//                    intent.putExtra("orgid", orgid);
//                    startActivityForResult(intent, 103);
//                }
//
//                break;
//            case R.id.rl_jigoujianjie:
//                if (zhongxinxiangqing.info.intro != null) {
//                    intent = new Intent(this, JigoujianjieActivity.class);
//                    intent.putExtra("jianjie", zhongxinxiangqing.info.intro);
//                    intent.putExtra("orgid", orgid);
//                    startActivityForResult(intent, 104);
//                } else {
//                    //do nothing
//                }
//
//                break;
//            case R.id.rl_fujian:// TODO: 2015/12/16
//                break;
//
//
//        }
//
//    }
//
//    private void getDanweixinxi() {
//        String url = Constants_utl.JIGOU_CENTER_DETAIL;//
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        zhongxinxiangqing = gson.fromJson(response, Zhongxinxiangqing.class);
//                        Log.e(TAG, "onResponse: " + response);
//                        if (zhongxinxiangqing.code == 0) {
//                            tv_jigou.setText(zhongxinxiangqing.info.name);//机构名称
//                            tv_tell.setText(zhongxinxiangqing.info.mobile + "");//电话号码
//                            tv_jigoujianjie.setText(zhongxinxiangqing.info.intro);//结构简介
//                            Picasso.with(DanweixinxiActivity.this).load(zhongxinxiangqing.info.headurl).into(iv_jigou_touxiang);//替换图片
//                            name = "";
//                            for (int i = 0; i < zhongxinxiangqing.info.dancenames.size(); i++) {
//                                name += zhongxinxiangqing.info.dancenames.get(i).dancename + "  ";
//                            }
//                            tv_wuzhong.setText(name);
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
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//    }
//
//    private void showpopwindow() {
//        // 获取自定义布局文件activity_popupwindow_left.xml的视图
//        View popupWindow_view = View.inflate(this, R.layout.adapter_touxiang, null);
//        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
//        popupWindow = new PopupWindow(popupWindow_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        // 设置动画效果
//        popupWindow.setAnimationStyle(R.style.AnimationFade);
//        // 这里是位置显示方式,在屏幕的底部
//        popupWindow.showAtLocation(findViewById(R.id.ll_view), Gravity.BOTTOM, 0, 0);
//        // 点击其他地方消失
//        popupWindow_view.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (popupWindow != null && popupWindow.isShowing()) {
//                    popupWindow.dismiss();
//                    popupWindow = null;
//                }
//                return false;
//            }
//        });
//
//        popupWindow_view.findViewById(R.id.bt_1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //相册
//                popupWindow.dismiss();
//                goSdcard();
//            }
//        });
//        popupWindow_view.findViewById(R.id.bt_2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //拍照
//                popupWindow.dismiss();
//                goCamera();
//
//            }
//        });
//        popupWindow_view.findViewById(R.id.bt_3).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //  取消
//                popupWindow.dismiss();
//
//            }
//        });
//    }
//
//    private void goCamera() {
//        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        File file = new File(UIUtils.getCacheFileName());
//        if (file.exists())
//            file.delete();
//        cacheUri = Uri.fromFile(file);
//        intent.putExtra("output", cacheUri);
//        startActivityForResult(intent, 105);
//
//    }
//
//
//    private void goSdcard() {
//        intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, 106);
//    }
//
//    /**
//     * 裁剪图片
//     *
//     * @param uri
//     */
//    public void startPhotoZoom(Uri uri, int requestCode) {
//        Intent in = new Intent("com.android.camera.action.CROP");
//        in.setDataAndType(uri, "image/*");
//        in.putExtra("crop", "true");// 设置裁剪
//        in.putExtra("aspectX", 1);// aspectX aspectY 是宽高的比例
//        in.putExtra("aspectY", 1);
//        in.putExtra("outputX", 320);// outputX outputY 是裁剪图片宽高
//        in.putExtra("outputY", 320);
//        in.putExtra("return-data", true);
//        startActivityForResult(in, requestCode);
//    }
//
//    private void wocao() {
//        String url = Constants_utl.SHANGCHUANTOUXIANG;//上传头像
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e(TAG, "onResponse: " + response);
//                        yuyue = gson.fromJson(response, Yuyue.class);
//                        Picasso.with(DanweixinxiActivity.this).load(yuyue.filePath).into(iv_jigou_touxiang);//替换图片
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
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//
//    }
//
//}
