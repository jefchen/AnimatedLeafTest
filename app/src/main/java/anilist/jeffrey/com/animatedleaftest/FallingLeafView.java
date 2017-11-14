package anilist.jeffrey.com.animatedleaftest;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jefchen on 10/22/17.
 */

public class FallingLeafView extends SurfaceView {

    private static final String TAG = FallingLeafView.class.getSimpleName();

    private SurfaceHolder surfaceHolder;
    private Bitmap leaf1;
    private Path leaf1Path;
    private Bitmap leaf2;
    private Bitmap leaf3;
    private Thread animatingThread;
    private boolean animating;

    public List<Pair<Integer, Integer>> positions = new ArrayList<>();

    public FallingLeafView(@NonNull Context context) {
        super(context);
        init();
    }

    public FallingLeafView(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FallingLeafView(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        surfaceHolder = getHolder();
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 3;
        leaf1 = BitmapFactory.decodeResource(getResources(), R.drawable.leaf1, opts);
        leaf2 = BitmapFactory.decodeResource(getResources(), R.drawable.leaf2, opts);
        leaf3 = BitmapFactory.decodeResource(getResources(), R.drawable.leaf3, opts);
        leaf1Path = new Path();
        leaf1Path.moveTo(0, 0);
        leaf1Path.cubicTo(658, 268, 616, 786, 757, 1245);

        setZOrderOnTop(true);
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(TAG, "SURFACE CREATED");
                if (animating) {
                    startAnimation();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder,
                                       int format, int width, int height) {
                Log.d(TAG, "SURFACE CHANGED");

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(TAG, "SURFACE DESTROYED");
                if (animating) {
                    stopAnimation();
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout");
    }

    protected void drawAnimatedLeafs(@NonNull Surface surface, long ellapsedTimeNanos) {
        Canvas canvas = surface.lockCanvas(null);
        try {
            Log.v(TAG, "drawCircleSurface: isHwAcc=" + canvas.isHardwareAccelerated());
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            // leaf1
            canvas.drawBitmap(leaf1, canvas.getWidth() / 2, canvas.getHeight() / 2, null);
        } finally {
            surface.unlockCanvasAndPost(canvas);
        }
    }

    public void startAnimation() {
        final Surface surface = surfaceHolder.getSurface();
        animating = true;

        if (surface == null || !surface.isValid()) {
            Log.w(TAG, "surface is not ready");
            return;
        }

        animatingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long startWhen = System.nanoTime();
                while (true) {
                    if (!animating) { return; }
                    long curTime = System.nanoTime();
                    long elapsedTime = curTime - startWhen;
                    try {
                        drawAnimatedLeafs(surface, elapsedTime);
                    } catch (IllegalArgumentException e) {
                        animating = false;
                        Log.d(TAG, "Exception calling drawAnimatedLeafs");
                        return;
                    }
                    double fps = 1000000000.0 / (System.nanoTime() - curTime);
                    Log.d(TAG, "Drawing at " + fps + " fps");
                }
            }
        });
        animatingThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "Motion event = " + event.getAction());
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            positions.add(new Pair<>((int) event.getX(), (int) event.getY()));
        }
        return super.onTouchEvent(event);
    }

    @MainThread
    public void stopAnimation() {
        animating = false;      // tell thread to stop
        if (animatingThread != null) {
            try {
                animatingThread.join();
            } catch (InterruptedException ignored) {}
        }
        animatingThread = null;
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d(TAG, "onDetachedFromWindow");
        System.out.println();
        System.out.println(positions.toString());
        stopAnimation();
        super.onDetachedFromWindow();
    }
}
