package anilist.jeffrey.com.animatedleaftest;

import android.animation.ValueAnimator;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupLeafAnimations();
    }

    public void setupLeafAnimations() {
        ViewGroup layoutContainer = (ViewGroup) findViewById(R.id.layout_container);
        for (int i = 0; i < 10; i++) {
            startAnimation(layoutContainer, R.drawable.leaf1);
            startAnimation(layoutContainer, R.drawable.leaf2);
            startAnimation(layoutContainer, R.drawable.leaf3);
            startAnimation(layoutContainer, R.drawable.leaf5);
        }
    }

    public void startAnimation(@NonNull final ViewGroup parentContainer, @DrawableRes int leafImageId) {
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        final ImageView leafImageView = new ImageView(parentContainer.getContext());
        leafImageView.setImageResource(leafImageId);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(50, 50);
        layoutParams.setMargins((int) (new Random().nextInt(600) / 600.0f * size.x), 0, 0, 0);
        leafImageView.setLayoutParams(layoutParams);
        parentContainer.addView(leafImageView);

        leafImageView.setPivotX(leafImageView.getWidth() / 2);
        leafImageView.setPivotY(leafImageView.getHeight() / 2);
        long delay = new Random().nextInt(6000);
        final ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(10000);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setStartDelay(delay);
        final int mScale = 1;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int movex = new Random().nextInt(size.x);
            int angle = 50 + (int) (Math.random() * 101);

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) (animation.getAnimatedValue());
                leafImageView.setRotation(angle * value);
                leafImageView.setTranslationX((movex - 40) * value);
                leafImageView.setTranslationY((size.y + (150 * mScale)) * value);
                if (value == 1) {
                    parentContainer.removeView(leafImageView);
                }
            }
        });
        animator.start();
    }
}
