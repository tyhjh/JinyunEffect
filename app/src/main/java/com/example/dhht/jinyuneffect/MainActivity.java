package com.example.dhht.jinyuneffect;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.viewlibrary.util.BlurUtil;
import com.example.viewlibrary.util.ImageUtil;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {

    Bitmap bitmap;
    ImageView iv_bg, ivShowPic;
    ObjectAnimator objectAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        setBackground();
    }

    private void setBackground() {
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                bitmap = BlurUtil.doBlur(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_show), 10, 30);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MainActivity.this).load(bitmap).into(iv_bg);
                    }
                });
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
