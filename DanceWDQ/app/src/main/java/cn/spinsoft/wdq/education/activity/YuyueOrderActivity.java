//package cn.spinsoft.wdq.education.activity;
//
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentStatePagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.view.View;
//
//import cn.spinsoft.wdq.R;
//import cn.spinsoft.wdq.base.BaseActivity;
//import cn.spinsoft.wdq.base.BaseFragment;
//import cn.spinsoft.wdq.education.utils.PagerTab;
//import cn.spinsoft.wdq.education.utils.UIUtils;
//import cn.spinsoft.wdq.education.utils.YuyueFactory;
//
///**
// * 预约订单的类
// */
//public class YuyueOrderActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
//
//    private PagerTab tuan_tabs;
//    private ViewPager tuan_pager;
//    public String orgid;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_yuyue_order;
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
//        //初始化viewpager
//        initViewpager();
//
//        //返回按钮点击关闭
//        findViewById(R.id.ll_back).setOnClickListener(this);
//
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
//            tab_names = UIUtils.getStringArray(R.array.yueyue_names);
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
//            return YuyueFactory.createFragment(position);
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
//        BaseFragment fragment = YuyueFactory.createFragment(position);
//    }
//
//
//    @Override
//    public void onPageScrollStateChanged(int state) {
//
//    }
//
//    /**
//     * 点击事件的处理
//     */
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.ll_back:
//                finish();
//                break;
//        }
//    }
//
//    public String getTrainId() {
//        return orgid;
//    }
//}
