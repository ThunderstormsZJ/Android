package cn.spinsoft.wdq.effect;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by hushujun on 15/11/9.
 */
public class AnimatorEffect {
    private static final Interpolator DEFAULT_INTERPOLATOR = new AccelerateDecelerateInterpolator();

    public static void smoothHorizontalSlideTo(View animView, View target) {
        if (animView == null || target == null) {
            return;
        }
        ViewWrapper wrapper = new ViewWrapper(animView);
        ObjectAnimator xAni = ObjectAnimator.ofFloat(animView, "x", target.getX());
        ObjectAnimator width = ObjectAnimator.ofInt(wrapper, "width", target.getWidth());
        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(DEFAULT_INTERPOLATOR);
        set.setDuration(150);
        set.play(xAni).with(width);
        set.start();
    }
}
