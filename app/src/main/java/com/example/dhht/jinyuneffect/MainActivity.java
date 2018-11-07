package com.example.dhht.jinyuneffect;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.viewlibrary.util.BlurUtil;
import com.example.viewlibrary.util.ImageUtil;
import com.example.viewlibrary.view.JinyunView;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {

    ImageView iv_bg, ivShowPic;
    ObjectAnimator objectAnimator;
    JinyunView jinyunView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initView();
        setBackground();
    }

    private void initView() {
        jinyunView = findViewById(R.id.sv_bg);
        ivShowPic = findViewById(R.id.ivShowPic);
        Glide.with(MainActivity.this).load(R.mipmap.ic_show).into(ivShowPic);
        ivShowPic.setClipToOutline(true);
        ivShowPic.setOutlineProvider(ImageUtil.getOutline(true, 20, 1));


        objectAnimator = ObjectAnimator.ofFloat(ivShowPic, "rotation", 0f, 360f);
        objectAnimator.setDuration(20 * 1000);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatCount(-1);
        objectAnimator.start();

        iv_bg = findViewById(R.id.iv_bg);
    }


    private void setBackground() {
        Bitmap bitmap = BlurUtil.doBlur(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_show), 10, 30);
        iv_bg.setImageBitmap(bitmap);
        iv_bg.setDrawingCacheEnabled(true);
        getBitmap();
    }

    public void getBitmap() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //启用DrawingCache并创建位图
                iv_bg.buildDrawingCache();
                while (iv_bg.getDrawingCache() == null) {
                    iv_bg.buildDrawingCache();
                    SystemClock.sleep(10);
                }
                Bitmap bitmap2 = Bitmap.createBitmap(iv_bg.getDrawingCache());
                bitmap2 = Bitmap.createBitmap(bitmap2, 0, jinyunView.getTop(), jinyunView.getWidth(), jinyunView.getHeight());
                jinyunView.setBitmapBg(bitmap2);
                iv_bg.setDrawingCacheEnabled(false);
            }
        }).start();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (objectAnimator.isPaused()) {
            objectAnimator.resume();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (objectAnimator.isRunning()) {
            objectAnimator.pause();
        }
    }
}
