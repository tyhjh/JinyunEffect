package com.example.viewlibrary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.viewlibrary.other.Triangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class JinyunView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    //三角形移动速度
    private double moveSpeed = 1;

    //刷新时间
    private static int refreshTime = 50;

    //每次添加的三角形数量
    private int addTriangleCount = 2;
    //所有的三角形
    private static List<Triangle> triangleList = new ArrayList<>();

    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;
    private boolean mIsDrawing;
    private int mPaintColor = Color.BLACK;

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
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mIsDrawing = true;
        //开启子线程
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
        try {
            Thread.sleep(refreshTime);
            mCanvas = mSurfaceHolder.lockCanvas();
            mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            manageTriangle((int) (refreshTime * moveSpeed));
            for (Triangle triangle : triangleList) {
                drawTriangle(mCanvas, triangle, mPaintColor);
            }
        } catch (Exception e) {

        } finally {
            if (mCanvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    private void manageTriangle(int distence) {

        Iterator iter = triangleList.iterator();
        while (iter.hasNext()) {
            Triangle triangle = (Triangle) iter.next();
            triangle.move(distence);

        }

        for (int i = 0; i < addTriangleCount; i++) {
            triangleList.add(Triangle.getRandomTriangle(getWidth() / 2, getHeight() / 2));
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
        double distence = Math.sqrt((triangle.topPoint1.x - getWidth() / 2) * (triangle.topPoint1.y - getHeight() / 2));
        if (distence > getWidth() / 3) {
            return (int) ((255 * getWidth() / 3) / distence);
        } else {
            return 255;
        }
    }


}
