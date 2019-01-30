# Android粒子特效--网易云鲸云特效

> 文档没更新，建议阅读原文：https://www.jianshu.com/p/d2996afeb3e1

最近网易云音乐出了一个叫**鲸云音效**东西，效果怎么样不是很清楚，但是播放界面还带了动效，这个就比较炫酷了，感觉比较有意思，所以也想自己做一个，其中一个我觉得比较好看的效果如下（动图的来源也比较有意思，后面会讲）

![G_1107000114.gif-2652.9kB][1]


### 具体思路
首先自定义布局是了解的，可能会用到`surfaceView`去绘制，整个动画可以分为四个部分，第一个是旋转的图片，这个好说；第二个是运动并且透明度渐变的三角形，这个画画也简单；第三个是根据音乐变化而变化的一个曲线吧，这个可能比较难，我也没接触过，不过可以试试看，第四个是模糊的背景，这个简单。


### 具体实现
#### 实现模糊的背景
这个倒是简单，之前也用过一个模糊背景的工具还不错，不过存在一个问题，我是打算自定义一个`surfaceView`，给`surfaceView`画一个背景倒是不难，也遇到两个问题

1.怎么将图片以类似自动裁剪居中的方式画上去，这个想想其实简单，取得画布的大小和bitmap的大小，满足一边进行缩放，裁剪掉多余部分就好了

```java
/**
     * 裁剪图片
     *
     * @param rectBitmap
     * @param rectSurface
     */
    public static void centerCrop(Rect rectBitmap, Rect rectSurface) {
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
```

2.由于我后面画三角形必须得不停地刷新，背景需要重复绘制，感觉有点浪费资源，看了一下局部刷新什么的感觉没什么用，所以就直接先设置为父布局的普通的背景好了，再将surfaceView设置为透明
```java
@Override
public void surfaceCreated(SurfaceHolder surfaceHolder) 
{
    setZOrderOnTop(true);
    getHolder().setFormat(PixelFormat.TRANSLUCENT);
}
```

> Android图片模糊的工具类:https://www.jianshu.com/p/c676fc51f3ef



#### 实现旋转的图片
这个更简单，为了方便也是直接使用一个**ImageView**，通过自带的视图裁剪工具剪裁为圆形，然后通过属性动画来旋转

设置一直旋转的属性动画
```java
objectAnimator = ObjectAnimator.ofFloat(ivShowPic, "rotation", 0f, 360f);
objectAnimator.setDuration(20 * 1000);
objectAnimator.setRepeatMode(ValueAnimator.RESTART);
objectAnimator.setInterpolator(new LinearInterpolator());
objectAnimator.setRepeatCount(-1);
objectAnimator.start();
```

视图裁剪
```java
ivShowPic.setClipToOutline(true);
//小小的封装了一下
ivShowPic.setOutlineProvider(ImageUtil.getOutline(true, 20, 1));
```

#### 实现运动的三角形
为了保证性能，这个就得使用**surfaceView**来做了；大体思路就是随机生成一些三角形，三角形速度大小一样，方向随机，从圆中心向外移动，移动过程将透明度减小到零

三角形有速度不过速度大小都一样就先不用管，有速度方向用角度来代替，也好计算运动后的位置，有三个顶点坐标。

所以三角形的初步定义
```java
public class Triangle {
    
    public Point topPoint1, topPoint2, topPoint3;
    public int moveAngle;
    
    public Triangle(Point topPoint1, Point topPoint2, Point topPoint3) {
        this.topPoint1 = topPoint1;
        this.topPoint2 = topPoint2;
        this.topPoint3 = topPoint3;
        moveAngle = getMoveAngel();
    }

}
```

##### 随机生成了三角形
简单的方法，就是先指定一个坐标区域比如**x**和**y**从-50到50的这个矩形坐标区域内，随机取点，如果构成三角形就为一个随机三角形，到时候移到中心处只需要x和y坐标各加长宽的一半就好了，方向也是-180度到180度取随机数，便于到时候用**斜率**计算移动后的位置


##### 画三角形
自定义surfaceView的通用写法都一样，随便看一下文章
> Android中的SurfaceView详解:https://www.jianshu.com/p/b037249e6d31

我们先清空画布，然后可以随机生成一些三角形，保存所有生成的三角形到一个集合里面，然后设定一个速度，根据每个三角形的方向来计算距离上一次刷新移动到了哪个位置，通过位置计算与中心点的距离来设置透明度，然后画上去

```java

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

mCanvas = mSurfaceHolder.lockCanvas();
mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
manageTriangle((int) (refreshTime * moveSpeed));
for (Triangle triangle : triangleList) {
    drawTriangle(mCanvas, triangle, mPaintColor);
}
mSurfaceHolder.unlockCanvasAndPost(mCanvas);
Thread.sleep(refreshTime);
```
具体代码看项目源码，这里注意需要设定几个值来调整动画效果到最佳，做的过程中也有出现一些很魔性的动画，很有意思


然后发现，**surfaceView**的动画会出现在**imageView**的上面，虽然我把**imageView**的高度调了一下还是没效果，发现是之前设置surfaceView透明的时候`setZOrderOnTop(true)`导致的问题；但是如果不设置**surfaceView**又会遮挡背景，的确是没好办法解决

其实可以简单点，判断三角形的移动距离小于**imageView**的时候设置全透明就好了，做出来大概是这样的效果：

![G_1107013001.gif-2394.6kB][2]

> 视频效果：http://oy5r220jg.bkt.clouddn.com/record__1107012332_1.mp4

其实还是有一点问题的，可以把**Imageview**的旋转在**surfaceView**里面实现，这个应该三角形的出现可以会自然一点，其他解决办法倒是暂时没想到

后面根据音乐变化的曲线暂时没做，也会马上看一下，尽快做出来，因为可能时间一长就没兴趣了



视频转Gif工具实现：https://www.jianshu.com/p/81cb36b610f4
视频的裁剪其实也是上面这个项目的代码，但是暂时没有做功能，会更新
项目地址：https://github.com/tyhjh/Jinyuneffect


  [1]: http://static.zybuluo.com/Tyhj/vld6mdwww3ficqqaqg99kjh6/G_1107000114.gif
  [2]: http://static.zybuluo.com/Tyhj/d0zi4dbm8271fdfu0glpkoww/G_1107013001.gif
