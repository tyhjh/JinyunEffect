package com.example.dhht.jinyuneffect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Myviews extends View {
    
    float touchX=0;
    float touchY=0;
    boolean isTouch;
    Paint paint;
    int width;
    int height;
    List<Mycircle> mycircleList;
    int distance=70;
    float speed=1.7f;
    int count=75;
    float radius=3.5f;
    public Myviews(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public Myviews(Context context) {
        super(context);
    }
    public Myviews(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取布局大小
        width=MeasureSpec.getSize(widthMeasureSpec);
        height=MeasureSpec.getSize(heightMeasureSpec);
        initialize();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint=new Paint();
        paint.setAntiAlias(true);
        //设置颜色
        paint.setColor(Color.BLACK);
        //设置背景
        canvas.drawColor(Color.WHITE);
        for(int i=0;i<mycircleList.size();i++){
            if(mycircleList.get(i).startX>width||mycircleList.get(i).startX<0) {
                mycircleList.get(i).speedX = -mycircleList.get(i).speedX;
            }
            if(mycircleList.get(i).startY>height||mycircleList.get(i).startY<0) {
                mycircleList.get(i).speedY = -mycircleList.get(i).speedY;
            }
            mycircleList.get(i).startX=mycircleList.get(i).startX+mycircleList.get(i).speedX;
            mycircleList.get(i).startY=mycircleList.get(i).startY+mycircleList.get(i).speedY;
            //画圆
            canvas.drawCircle(mycircleList.get(i).startX,mycircleList.get(i).startY,mycircleList.get(i).radius,mycircleList.get(i).paint);
        }
        //画连接线
        for(int i=0;i<mycircleList.size()-1;i++){
            for(int j=i+1;j<mycircleList.size();j++){
                float x=mycircleList.get(i).startX-mycircleList.get(j).startX;
                float y=mycircleList.get(i).startY-mycircleList.get(j).startY;
                if(x*x+y*y<mycircleList.get(i).distance*distance*distance){
                    canvas.drawLine(mycircleList.get(i).startX,mycircleList.get(i).startY,
                            mycircleList.get(j).startX,mycircleList.get(j).startY,mycircleList.get(i).paint);
                }
            }
        }
        //点击时候处理
        if(isTouch){
            canvas.drawCircle(touchX,touchY,5,paint);
            for(int i=0;i<mycircleList.size();i++){
                float x=mycircleList.get(i).startX-touchX;
                float y=mycircleList.get(i).startY-touchY;
                if(x*x+y*y<12*distance*distance){
                    canvas.drawLine(mycircleList.get(i).startX,mycircleList.get(i).startY,
                            touchX,touchY,mycircleList.get(i).paint);
                }
            }
        }
        //刷新会重新执行这个方法，所以一直执行下去------重点
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isTouch=true;
                touchX=event.getX();
                touchY=event.getY();
                Log.e("x,y","DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                isTouch=true;
                touchX=event.getX();
                touchY=event.getY();
                Log.e("x,y","MOVE");
                break;
            case MotionEvent.ACTION_UP:
                isTouch=false;
                touchX=-1;
                touchY=-1;
                Log.e("x,y","UP");
                break;
        }
        return true;
    }

    private void initialize() {
        mycircleList=new ArrayList<Mycircle>();
        Random random = new Random(System.currentTimeMillis());
        for (int i=0;i<count;i++) {
            int distance=1;
            int alpha = random.nextInt(60)+40;
            float radius = this.radius*random.nextFloat()+0.5f;
            float startX = random.nextInt(width);
            float startY = random.nextInt(height);
            float speedX = speed - 2*speed*random.nextFloat();
            float speedY = speed - 2*speed*random.nextFloat();

            if(speedX==0)
                speedX=1;
            if(speedY==0)
                speedY=1;

            if(i%50==0)
                distance=4;
            else if(i%30==0)
                distance=3;
            else if(i%20==0)
                distance=2;

            Mycircle mycircle=new Mycircle(radius,startX,startY,speedX,speedY,alpha,distance);
            mycircleList.add(mycircle);
        }
    }

    class Mycircle{
        int distance;
        Paint paint;
        float radius;
        float startX;
        float startY;
        float speedX;
        float speedY;
        int alpha;
        public Mycircle(float radius, float startX, float startY, float speedX, float speedY, int alpha,int distance) {
            this.radius = radius;
            this.startX = startX;
            this.startY = startY;
            this.speedX = speedX;
            this.speedY = speedY;
            this.alpha = alpha;
            paint=new Paint();
            paint.setAntiAlias(true);
            //设置颜色
            paint.setColor(Color.BLACK);
            paint.setAlpha(alpha);
            this.distance=distance;
        }
    }

}