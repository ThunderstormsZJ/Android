//package cn.spinsoft.wdq.education.activity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentStatePagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.google.gson.Gson;
//import com.squareup.picasso.Picasso;
//import com.umeng.socialize.controller.UMServiceFactory;
//import com.umeng.socialize.controller.UMSocialService;
//import com.umeng.socialize.media.UMImage;
//import com.umeng.socialize.sso.UMSsoHandler;
//import com.umeng.socialize.weixin.controller.UMWXHandler;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.base.BaseActivity;
//import cn.spinsoft.wdq.base.BaseFragment;
//import cn.spinsoft.wdq.base.BaseHandler;
//import cn.spinsoft.wdq.education.MyApplication;
//import cn.spinsoft.wdq.education.bean.Xuexiao;
//import cn.spinsoft.wdq.education.frament.school.FragmentFactory;
//import cn.spinsoft.wdq.education.utils.PagerTab;
//import cn.spinsoft.wdq.education.utils.UIUtils;
//import cn.spinsoft.wdq.utils.Constants_utl;
//
///**
// * 培训机构的详情页
// */
//public class SchoolActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
//    String TAG = "SchoolActivity";
//    private PagerTab tuan_tabs;
//    private ViewPager tuan_pager;
//    public String orgid;
//    private Xuexiao xuexiao;
//    private Gson gson = new Gson();
//    private TextView tv_school, tv_school_jieshao, tv_xuexiaoname;
//    private ImageView iv_touxiang, iv_fenxiang;
//    private int userid = BaseHandler.watcherUserId;
//    private boolean is_from_center;
//    // 首先在您的Activity中添加如下成员变量
//    final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
//// 设置分享内容
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_school;
//    }
//
//
//    @Override
//    protected void initHandler() {
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        /**使用SSO授权必须添加如下代码 */
//        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
//        if (ssoHandler != null) {
//            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
//    }
//
//    @Override
//    protected void initViewAndListener(Bundle savedInstanceState) {
//        is_from_center = getIntent().getBooleanExtra("IS_FROM_Center", false);
//        if (is_from_center) {
//            orgid = getIntent().getStringExtra("orgids");
//            Log.e(TAG, "initViewAndListener: " + orgid + is_from_center);
//        } else {
//            orgid = getIntent().getStringExtra("orgid");
//            Log.e(TAG, "initViewAndListener: " + orgid + is_from_center);
//        }
//        //联网请求数据
//        getData(orgid);
//
//        //初始化viewpager
//        initViewpager();
//
//        //返回按钮点击关闭
//        findViewById(R.id.ll_back).setOnClickListener(this);
//        //分享按钮
//        findViewById(R.id.iv_fenxiang).setOnClickListener(this);
//
//        //找到学校的名称和介绍
//        tv_school = (TextView) findViewById(R.id.tv_school);
//        tv_school_jieshao = (TextView) findViewById(R.id.tv_school_jieshao);
//        iv_touxiang = (ImageView) findViewById(R.id.iv_touxiang);
//        tv_xuexiaoname = (TextView) findViewById(R.id.tv_xuexiaoname);
//
//        String appID = "wxef61dc83ecf7b762";
//        String appSecret = "215c05eac17453570a6c9fa4ff7393c0";
//// 添加微信平台
//        UMWXHandler wxHandler = new UMWXHandler(this, appID, appSecret);
//        wxHandler.addToSocialSDK();
//
//// 添加微信朋友圈
//        UMWXHandler wxCircleHandler = new UMWXHandler(this, appID, appSecret);
//        wxCircleHandler.setToCircle(true);
//        wxCircleHandler.addToSocialSDK();
//
//
//    }
//
//    //点击事件的处理
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.ll_back://返回键关闭
//                finish();
//                break;
//            case R.id.iv_fenxiang:
//                mController.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能，http://www.umeng.com/social");
//// 设置分享图片, 参数2为图片的url地址
//                mController.setShareMedia(new UMImage(this,
//                        "http://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=r%E6%81%A9%E7%89%A9&step_word=&pn=0&spn=0&di=107653472741&pi=&rn=1&tn=baiduimagedetail&is=&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=1399175545%2C2575227695&os=3005227806%2C3948149461&simid=3435461123%2C158288907&adpicid=0&ln=1996&fr=&fmq=1451541291205_R&ic=undefined&s=undefined&se=&sme=&tab=0&width=&height=&face=undefined&ist=&jit=&cg=&bdtype=0&objurl=http%3A%2F%2Fpica.nipic.com%2F2007-12-14%2F20071214105856833_2.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bgtrtv_z%26e3Bv54AzdH3Ffi5oAzdH3F8AzdH3F8dAzdH3Fjlnabjn0uu1bdjva_z%26e3Bip4s&gsm=0"));
//                // 是否只有已登录用户才能打开分享选择页
//                mController.openShare(this, false);
//
//
//                break;
//        }
//    }
//
//    public void getData(final String orgid) {
//        String url = Constants_utl.JIGOUXIANGQING;//Jigou详情
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e(TAG, "onResponse: " + response);
//                        xuexiao = gson.fromJson(response, Xuexiao.class);
//                        if (xuexiao.code == 0) {
//                            tv_xuexiaoname.setText(xuexiao.train.orgname);
//                            tv_school.setText(xuexiao.train.orgname);
//                            tv_school_jieshao.setText(xuexiao.train.signature);
//                            Picasso.with(getApplicationContext()).load(xuexiao.train.headurl).into(iv_touxiang);//替换图片
//                        } else {
//                            Toast.makeText(getApplicationContext(), "请求失败", Toast.LENGTH_SHORT).show();
//                        }
//
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                        Toast.makeText(getApplicationContext(), "请求失败", Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, "onErrorResponse: " + error);
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("trainId", orgid);
//                params.put("id", userid + "");
//                return params;
//            }
//        };
//        postRequest.setTag("volleypost");
//        MyApplication.getHttpQueue().add(postRequest);
//    }
//
//    /**
//     * //初始化viewpager
//     */
//    public void initViewpager() {
//        // 初始化横着滚动的title
//        tuan_tabs = (PagerTab) findViewById(R.id.tuan_tabs);
//
//        // 初始化viewpager
//        tuan_pager = (ViewPager) findViewById(R.id.tuan_pager);
//
//        // 初始化适配器，传入FragmentManager,由于是高版本的，没办法适配.使用getSupportFragmentManager。
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//
//        tuan_pager.setAdapter(adapter);
//
//        // 横着滚动的title和下面的ViewPager绑定在一起
//        tuan_tabs.setViewPager(tuan_pager);
//
//        // 设置左右滑动的监听器
//        tuan_tabs.setOnPageChangeListener(this);
//    }
//
//    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
//        // 滚动条的名字
//        private String[] tab_names;
//
//        public ViewPagerAdapter(FragmentManager fm) {
//            super(fm);
//            tab_names = UIUtils.getStringArray(R.array.tuan_names);
//        }
//
//        /**
//         * 如果想实现title的功能必须实现这个方法
//         */
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return tab_names[position];
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return FragmentFactory.createFragment(position);
//        }
//
//        @Override
//        public int getCount() {
//            return tab_names.length;
//        }
//
//    }
//
//
//    @Override
//    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//    }
//
//    @Override
//    public void onPageSelected(int position) {
//        BaseFragment fragment = FragmentFactory.createFragment(position);
//    }
//
//
//    @Override
//    public void onPageScrollStateChanged(int state) {
//
//    }
//
//    /**
//     * 获得机构ID
//     *
//     * @return
//     */
//    public String getTrainId() {
//        return orgid;
//    }
//
//    /**
//     * 获取机构的名字
//     *
//     * @return
//     */
//    public String getSchoolname() {
//        if (xuexiao.train.orgname == null) {
//            return "";
//        } else {
//            return xuexiao.train.orgname + "";
//        }
//
//    }
//
//
//}
