package com.example.viewlibrary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.viewlibrary.other.Triangle;
import com.example.viewlibrary.util.ImageUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class JinyunView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    //三角形移动速度
    private double moveSpeed = 0.4;

    //刷新时间
    private static int refreshTime = 20;

    //添加两次三角形的间隔
    private static int addTriangleInterval = 100;

    //每次添加的数量限制
    private static int addTriangleOnece = 2;

    //总三角形数量
    private int allTriangleCount = 100;
    //所有的三角形
    private static List<Triangle> triangleList = new ArrayList<>();

    private SurfaceHolder mSurfaceHolder;

    private boolean mIsDrawing;
    private int mPaintColor = Color.parseColor("#cabfa3");

    private Bitmap bitmapBg;

    public void setBitmapBg(Bitmap bitmapBg) {
        this.bitmapBg = bitmapBg;
        mPaintColor=ImageUtil.getColor(bitmapBg,0).getRgb();
    }

    public JinyunView(Context context) {
        super(context);
        initView();
    }

    public JinyunView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public JinyunView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mIsDrawing = true;
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        //setZOrderOnTop(true);
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mIsDrawing = false;
    }

    @Override
    public void run() {
        while (mIsDrawing) {
            drawSomething();
        }
    }


    private void drawSomething() {
        Canvas mCanvas = null;
        long t = System.currentTimeMillis();
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            if (bitmapBg != null) {
                mCanvas.drawBitmap(bitmapBg, 0, 0, new Paint());
            }
            manageTriangle((int) (refreshTime * moveSpeed));
            for (Triangle triangle : triangleList) {
                drawTriangle(mCanvas, triangle, mPaintColor);
            }
        } catch (Exception e) {

        } finally {
            if (mCanvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
            SystemClock.sleep(Math.max(refreshTime - (System.currentTimeMillis() - t), 0));
        }
    }

    private static Long startTime = System.currentTimeMillis();


    private void manageTriangle(int distence) {

        Iterator iter = triangleList.iterator();
        while (iter.hasNext()) {
            Triangle triangle = (Triangle) iter.next();
            if (triangle.isOut(getWidth(), getHeight())) {
                iter.remove();
            } else {
                triangle.move(distence);
            }
        }

        if (System.currentTimeMillis() - startTime > addTriangleInterval && triangleList.size() < allTriangleCount) {
            for (int i = 0; i < addTriangleOnece; i++) {
                triangleList.add(Triangle.getRandomTriangle(getWidth() / 2, getHeight() / 2));
            }
            startTime = System.currentTimeMillis();
        }


    }


    public void drawTriangle(Canvas canvas, Triangle triangle, int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAlpha(getAlpha(triangle));
        paint.setStrokeWidth(5);

        Path path = new Path();
        path.moveTo(triangle.topPoint1.x, triangle.topPoint1.y);
        path.lineTo(triangle.topPoint2.x, triangle.topPoint2.y);
        path.lineTo(triangle.topPoint3.x, triangle.topPoint3.y);
        path.close();
        canvas.drawPath(path, paint);
    }

    public int getAlpha(Triangle triangle) {
        double distence1 = Math.sqrt(Math.pow((triangle.topPoint1.x - getWidth() / 2), 2) + Math.pow((triangle.topPoint1.y - getHeight() / 2), 2));
        double distence2 = Math.sqrt(Math.pow((triangle.topPoint2.x - getWidth() / 2), 2) + Math.pow((triangle.topPoint2.y - getHeight() / 2), 2));
        double distence3 = Math.sqrt(Math.pow((triangle.topPoint3.x - getWidth() / 2), 2) + Math.pow((triangle.topPoint3.y - getHeight() / 2), 2));

        double distence = Math.max(Math.max(distence1, distence2), distence3);

        if (distence < getWidth() * (1.5 / 5)) {
            return 255;
        } else {
            double alpha = ((-1275 / (2 * (double) getWidth())) * distence + 1275 / 2) - 280;
            if (alpha < 0) {
                alpha = 0;
            }
            return (int) alpha;
        }

       /* if (distence > getWidth() / 3) {

        } else {
            return 255;
        }*/
    }


}
