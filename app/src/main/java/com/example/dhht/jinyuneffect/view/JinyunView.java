package com.example.dhht.jinyuneffect.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.dhht.jinyuneffect.R;

import java.util.Random;

public class JinyunView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;
    private boolean mIsDrawing;
    private Bitmap bitmap;
    Paint paint = new Paint();


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
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bg);
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
            //drawSomething();
        }
    }


    private void drawSomething() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas(new Rect(0, 0, 200, 200));
            mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            Rect rectBitmap = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            Rect rectSurface = mSurfaceHolder.getSurfaceFrame();
            //centerCrop(rectBitmap, rectSurface);
            //mCanvas.drawBitmap(bitmap, rectBitmap, rectSurface, paint);
        } catch (Exception e) {

        } finally {
            if (mCanvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    /**
     * 裁剪图片
     *
     * @param rectBitmap
     * @param rectSurface
     */
    private void centerCrop(Rect rectBitmap, Rect rectSurface) {
        int verticalTimes = rectBitmap.height() / rectSurface.height();
        int horizontalTimes = rectBitmap.width() / rectSurface.width();
        if (verticalTimes > horizontalTimes) {
            rectBitmap.left = 0;
            rectBitmap.right = rectBitmap.right;
            rectBitmap.top = (rectBitmap.height() - (rectSurface.height() * rectBitmap.width() / rectSurface.width())) / 2;
            rectBitmap.bottom = rectBitmap.bottom - rectBitmap.top;
        } else {
            rectBitmap.top = 0;
            rectBitmap.bottom = rectBitmap.bottom;
            rectBitmap.left = (rectBitmap.width() - (rectSurface.width() * rectBitmap.height() / rectSurface.height())) / 2;
            rectBitmap.right = rectBitmap.right - rectBitmap.left;
        }


    }
}