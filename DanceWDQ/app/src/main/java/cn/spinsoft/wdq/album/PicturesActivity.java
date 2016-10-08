package cn.spinsoft.wdq.album;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.utils.Constants;

/**
 * Created by zhoujun on 16/1/18.
 */
public class PicturesActivity extends BaseActivity {
    private static final String TAG = PicturesActivity.class.getSimpleName();

    private ViewPager mPicturePager;
    private TextView mTitleTx/*, mDescTx*/;

    private List<String> mPictureUrls;
    private String pictureUrl;
    private int mImgPosition;

    public static void start(Context context, List<String> imgUrls, String imgUrl, int position) {
        Intent picIntent = new Intent(context, PicturesActivity.class);
        picIntent.putStringArrayListExtra(Constants.Strings.PICTURE_URLS, (ArrayList<String>) imgUrls);//大图地址
        picIntent.putExtra(Constants.Strings.PICTURE_URL, imgUrl);//头像地址
        picIntent.putExtra(Constants.Strings.PICTURE_POSITION, (Integer) position);
        context.startActivity(picIntent);
    }


    @Override
    protected int getLayoutId() {
        setTintRes(android.R.color.black);
        return R.layout.activity_pictures;
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        mPicturePager = (HackyViewPager) findViewById(R.id.picture_img_pager);
        mTitleTx = (TextView) findViewById(R.id.base_title_name);
//        mDescTx = (TextView) findViewById(R.id.picture_desc);
        TextView backTx = (TextView) findViewById(R.id.base_title_back);

        backTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Intent intent = getIntent();
        mPictureUrls = intent.getStringArrayListExtra(Constants.Strings.PICTURE_URLS);
        mImgPosition = intent.getIntExtra(Constants.Strings.PICTURE_POSITION, 1);
//        mDescTx.setText(intent.getStringExtra(Constants.Strings.PICTURE_DESC));
        if (mPictureUrls != null && !mPictureUrls.isEmpty() && mImgPosition > 0) {
            mTitleTx.setText(mImgPosition + "/" + mPictureUrls.size());
        } else {
            mTitleTx.setText("头像");
            pictureUrl = intent.getStringExtra(Constants.Strings.PICTURE_URL);
        }
        mPicturePager.setAdapter(new SamplePagerAdapter());
        mPicturePager.setCurrentItem(mImgPosition - 1);
        mPicturePager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTitleTx.setText(position + 1 + "/" + mPictureUrls.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (mPictureUrls != null && !mPictureUrls.isEmpty() && mImgPosition > 0) {
                return mPictureUrls.size();
            } else {
                return 1;
            }
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            SimpleDraweeView simpleDraweeView = new SimpleDraweeView(container.getContext());
//            PhotoView photoView = new PhotoView(container.getContext());
//            photoView.setImageResource(sDrawables[position]);
//            Picasso.with(container.getContext()).load(mPictureUrls.get(position)).into(photoView);

            // Now just add PhotoView to ViewPager and return it
//            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
           /* GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(container.getResources());
            GenericDraweeHierarchy hierarchy = builder
                    .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE)
                    .setProgressBarImage(new ProgressBarDrawable(), ScalingUtils.ScaleType.CENTER_INSIDE)
                    .build();
            simpleDraweeView.setHierarchy(hierarchy);*/
            GenericDraweeHierarchy hierarchy = simpleDraweeView.getHierarchy();
            hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
            hierarchy.setProgressBarImage(new ProgressBarDrawable());
//            hierarchy.setFailureImage(getDrawable(R.mipmap.ic_picture_loading_fail));
            hierarchy.setPlaceholderImage(getResources().getDrawable(R.mipmap.ic_empty_picture), ScalingUtils.ScaleType.CENTER_INSIDE);
            if (mPictureUrls != null && !mPictureUrls.isEmpty() && mImgPosition > 0) {
                simpleDraweeView.setImageURI(Uri.parse(mPictureUrls.get(position)));
            } else {
                simpleDraweeView.setImageURI(Uri.parse(pictureUrl));
            }
            container.addView(simpleDraweeView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            return simpleDraweeView;
//            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    class CustomProgressBar extends Drawable {

        @Override
        public void draw(Canvas canvas) {

        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return 0;
        }

        @Override
        protected boolean onLevelChange(int level) {
            Log.w(TAG, level + "");
            return super.onLevelChange(level);
        }
    }

}
