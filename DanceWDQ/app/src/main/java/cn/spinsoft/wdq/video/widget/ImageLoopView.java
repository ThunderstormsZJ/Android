package cn.spinsoft.wdq.video.widget;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.video.biz.AdvertisementInfo;

/**
 * 广告图片自动轮播控件</br>
 * <p/>
 * <pre>
 *   集合ViewPager和指示器的一个轮播控件，主要用于一般常见的广告图片轮播，具有自动轮播和手动轮播功能
 *   使用：只需在xml文件中使用{@code <cn.spinsoft.wdq.videos.browse.widget.ImageCycleView/>} ，
 *   然后在页面中调用  {@link #setImageResources(List, ImageLoopViewListener) }即可!
 *
 *   另外提供{@link #startImageCycle() } \ {@link #pushImageCycle() }两种方法，用于在Activity不可见之时节省资源；
 *   因为自动轮播需要进行控制，有利于内存管理
 * </pre>
 */
public class ImageLoopView extends LinearLayout {

    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 图片轮播视图
     */
    private LoopViewPager mBannerPager = null;

    /**
     * 滚动图片视图适配器
     */
    private ImageLoopAdapter mAdvAdapter;

    /**
     * 图片轮播指示器控件
     */
    private ViewGroup mGroup;

    /**
     * 图片轮播指示器-个图
     */
    private SimpleDraweeView mImageView = null;

    /**
     * 滚动图片指示器-视图列表
     */
    private SimpleDraweeView[] mImageViews = null;

    /**
     * 图片滚动当前图片下标
     */
    private int mImageIndex = 1;

    /**
     * 手机密度
     */
    private float mScale;

    /**
     * @param context
     */
    public ImageLoopView(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public ImageLoopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mScale = context.getResources().getDisplayMetrics().density;
        LayoutInflater.from(context).inflate(R.layout.view_banner_content, this);
        mBannerPager = (LoopViewPager) findViewById(R.id.pager_banner);
        mBannerPager.setOnPageChangeListener(new GuidePageChangeListener());
        mBannerPager.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        // 开始图片滚动
                        startImageTimerTask();
                        break;
                    default:
                        // 停止图片滚动
                        stopImageTimerTask();
                        break;
                }
                return false;
            }
        });
        // 滚动图片右下指示器视图
        mGroup = (ViewGroup) findViewById(R.id.viewGroup);
    }

    /**
     * 装填图片数据
     *
     * @param infoList
     * @param imageCycleViewListener
     */
    public void setImageResources(List<AdvertisementInfo> infoList, ImageLoopViewListener imageCycleViewListener) {
        if (infoList == null || infoList.isEmpty()) {
            return;
        }
        // 清除所有子视图
        mGroup.removeAllViews();
        // 图片广告数量
        final int imageCount = infoList.size();
        mImageViews = new SimpleDraweeView[imageCount];
        for (int i = 0; i < imageCount; i++) {
            mImageView = new SimpleDraweeView(mContext);
//            int imageParams = (int) (mScale * 20 + 0.5f);// XP与DP转换，适应不同分辨率
//            int imagePadding = (int) (mScale * 5 + 0.5f);
            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layout.setMargins(3, 0, 3, 0);
            mImageView.setLayoutParams(layout);
//            mImageView.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
            mImageViews[i] = mImageView;
            if (i == 0) {
                mImageViews[i].setBackgroundResource(R.mipmap.icon_point_pre);
            } else {
                mImageViews[i].setBackgroundResource(R.mipmap.icon_point);
            }
            mGroup.addView(mImageViews[i]);
        }
        mAdvAdapter = new ImageLoopAdapter(mContext, infoList, imageCycleViewListener);
        mBannerPager.setAdapter(mAdvAdapter);
        startImageTimerTask();
    }

    /**
     * 开始轮播(手动控制自动轮播与否，便于资源控制)
     */
    public void startImageCycle() {
        startImageTimerTask();
    }

    /**
     * 暂停轮播——用于节省资源
     */
    public void pushImageCycle() {
        stopImageTimerTask();
    }

    /**
     * 开始图片滚动任务
     */
    private void startImageTimerTask() {
        stopImageTimerTask();
        // 图片每3秒滚动一次
        mHandler.postDelayed(mImageTimerTask, 3000);
    }

    /**
     * 停止图片滚动任务
     */
    private void stopImageTimerTask() {
        mHandler.removeCallbacks(mImageTimerTask);
    }

    private Handler mHandler = new Handler();

    /**
     * 图片自动轮播Task
     */
    private Runnable mImageTimerTask = new Runnable() {

        @Override
        public void run() {
            if (mImageViews != null) {
                // 下标等于图片列表长度说明已滚动到最后一张图片,重置下标
                if ((++mImageIndex) == mImageViews.length + 1) {
                    mImageIndex = 1;
                }
                mBannerPager.setCurrentItem(mImageIndex);
            }
        }
    };

    /**
     * 轮播图片状态监听器
     *
     * @author minking
     */
    private final class GuidePageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE)
                startImageTimerTask(); // 开始下次计时
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int index) {

            if (index == 0 || index == mImageViews.length + 1) {
                return;
            }
            // 设置图片滚动指示器背景
            mImageIndex = index;
            index -= 1;
            mImageViews[index].setBackgroundResource(R.mipmap.icon_point_pre);
            for (int i = 0; i < mImageViews.length; i++) {
                if (index != i) {
                    mImageViews[i].setBackgroundResource(R.mipmap.icon_point);
                }
            }

        }

    }

    private class ImageLoopAdapter extends PagerAdapter {

        /**
         * 图片视图缓存列表
         */
        private ArrayList<SimpleDraweeView> mImageViewCacheList;

        /**
         * 图片资源列表
         */
        private List<AdvertisementInfo> mAdList = new ArrayList<AdvertisementInfo>();

        /**
         * 广告图片点击监听器
         */
        private ImageLoopViewListener mImageLoopViewListener;

        private Context mContext;

        public ImageLoopAdapter(Context context, List<AdvertisementInfo> adList, ImageLoopViewListener imageCycleViewListener) {
            mContext = context;
            mAdList = adList;
            mImageLoopViewListener = imageCycleViewListener;
            mImageViewCacheList = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return mAdList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            String imageUrl = mAdList.get(position).getImgUrl();
            SimpleDraweeView imageView = null;
            if (mImageViewCacheList.isEmpty()) {
                GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder.setPlaceholderImage(getResources().getDrawable(R.mipmap.ic_video_loading_fail, null));
                } else {
                    builder.setPlaceholderImage(getResources().getDrawable(R.mipmap.ic_video_loading_fail));
                }
                imageView = new SimpleDraweeView(mContext, builder.build());
                imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            } else {
                imageView = mImageViewCacheList.remove(0);
            }
            imageView.setTag(imageUrl);
            container.addView(imageView);
            if (mImageLoopViewListener != null) {
                // 设置图片点击监听
                imageView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mImageLoopViewListener.onLoopImageClick(mAdList.get(position), position, v);
                    }
                });
                mImageLoopViewListener.displayLoopImage(imageUrl, imageView);
            }

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            SimpleDraweeView view = (SimpleDraweeView) object;
            container.removeView(view);
            mImageViewCacheList.add(view);
        }

    }

    /**
     * 轮播控件的监听事件
     *
     * @author minking
     */
    public interface ImageLoopViewListener {

        /**
         * 加载图片资源
         *
         * @param imageURL
         * @param imageView
         */
        void displayLoopImage(String imageURL, SimpleDraweeView imageView);

        /**
         * 单击图片事件
         *
         * @param position
         * @param imageView
         */
        void onLoopImageClick(AdvertisementInfo info, int position, View imageView);
    }

}
