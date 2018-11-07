package com.example.viewlibrary.util;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.ViewOutlineProvider;

import java.util.HashMap;

/**
 * Created by Tyhj on 2017/5/26.
 */

public class ImageUtil {


    //获取bitmap颜色
    public static Palette.Swatch getColor(Bitmap bitmap, int color) {
        // Palette的部分
        Palette palette = Palette.generate(bitmap);
        Palette.Swatch swatche = null;
        if (palette != null) {
            switch (color) {
                case 0:
                    swatche = palette.getVibrantSwatch();
                    break;
                case 1:
                    swatche = palette.getLightVibrantSwatch();
                    break;
                case 2:
                    swatche = palette.getDarkVibrantSwatch();
                    break;
                case 3:
                    swatche = palette.getMutedSwatch();
                    break;
                case 4:
                    swatche = palette.getLightMutedSwatch();
                    break;
                case 5:
                    swatche = palette.getDarkMutedSwatch();
                    break;
                default:
                    swatche = palette.getVibrantSwatch();
                    break;
            }
            if(swatche==null){
                swatche = palette.getVibrantSwatch();
            }
        }
        return swatche;
    }


    public static Bitmap createVideoThumbnail(String url, int width, int height) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }


    //设置控件轮廓
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static ViewOutlineProvider getOutline(boolean b, final int pading, final int circularBead) {
        if (b) {
            return new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, android.graphics.Outline outline) {
                    final int margin = Math.min(view.getWidth(), view.getHeight()) / pading;
                    outline.setOval(margin, margin, view.getWidth() - margin, view.getHeight() - margin);
                }
            };
        } else {
            return new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, android.graphics.Outline outline) {
                    final int margin = Math.min(view.getWidth(), view.getHeight()) / pading;
                    outline.setRoundRect(margin, margin, view.getWidth() - margin, view.getHeight() - margin, circularBead);
                }
            };
        }
    }


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


}
