//package cn.spinsoft.wdq.education.utils;
//
//import java.util.HashMap;
//
//import cn.spinsoft.wdq.base.BaseFragment;
//import cn.spinsoft.wdq.education.frament.personcenter.AllFragment;
//import cn.spinsoft.wdq.education.frament.personcenter.DaiquerenFragment;
//import cn.spinsoft.wdq.education.frament.personcenter.YijujueFragment;
//
///**
// * Created by Administrator on 2015/12/14.
// */
//public class YuyueFactory {
//    private static final int TAB_0 = 0;
//
//    private static final int TAB_1 = 1;
//
//    private static final int TAB_2 = 2;
//
//    private static final int TAB_3 = 3;
//
//
//    // 设置缓存fragment的数据
//    private static HashMap<Integer, BaseFragment> mFragments = new HashMap<Integer, BaseFragment>();
//
//    public static BaseFragment createFragment(int position) {
//
//        // 从缓存里面取出fragment的数据
//        BaseFragment fragment = mFragments.get(position);
//
//        if (fragment == null) {
//            switch (position) {
//                case TAB_0:
//                    fragment = new AllFragment();
//                    break;
//                case TAB_1:
//                    fragment = new YijujueFragment();
//                    break;
//                case TAB_2:
//                    fragment = new DaiquerenFragment();
//                    break;
//                case TAB_3:
//                    fragment = new YijujueFragment();
//                    break;
//            }
//
//            // 把fragment加入到缓存数据里面，缓存起来。
//            mFragments.put(position, fragment);
//        }
//
//        return fragment;
//    }
//}
