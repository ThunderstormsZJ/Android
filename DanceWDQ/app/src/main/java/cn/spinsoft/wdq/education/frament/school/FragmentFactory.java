//package cn.spinsoft.wdq.education.frament.school;
//
//import java.util.HashMap;
//
//import cn.spinsoft.wdq.base.BaseFragment;
//
///**
// * Created by QUCHUANGYE on 2015/11/25.
// */
//public class FragmentFactory {
//
//    private static final int TAB_0 = 0;
//
//    private static final int TAB_1 = 1;
//
//    private static final int TAB_2 = 2;
//
//    private static final int TAB_3 = 3;
//
//    private static final int TAB_4 = 4;
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
//
//                case TAB_0:
//                    fragment = new JianjieFragment();
//                    break;
//                case TAB_1:
//                    fragment = new ZuopinFragment();
//                    break;
//                case TAB_2:
//                    fragment = new KechengFragment();
//                    break;
//                case TAB_3:
//                    fragment = new DongtaiFragment();
//                    break;
//                case TAB_4:
//                    fragment = new JiaoshiFragment();
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
