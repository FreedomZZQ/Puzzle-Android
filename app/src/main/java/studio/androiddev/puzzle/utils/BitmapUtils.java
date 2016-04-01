package studio.androiddev.puzzle.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileNotFoundException;

public class BitmapUtils {
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height/2 ;
            final int halfWidth = width/2 ;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    //如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响
    public static Bitmap createScaleBitmap(Bitmap src, int dstWidth,
                                           double dstHeight) {
        int k=(int) dstHeight;
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, k, true);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    public static Bitmap createScaleBitmap(Bitmap src, double dstWidth,
                                           int dstHeight) {
        int k=(int) dstWidth;
        Bitmap dst = Bitmap.createScaledBitmap(src, k,dstHeight, true);
        if (src != dst) { //如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    // ��Resources�м���ͼƬ
    public Bitmap decodeSampledBitmapFromPath(String url,  int reqWidth, int reqHeight,boolean change)
            throws FileNotFoundException {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;



        BitmapFactory.decodeFile(url, options);
        int size;
        /*double re=0;
         re= ((double) options.outWidth)/options.outHeight;*/
        if(options.outWidth>options.outHeight)
            size=options.outWidth;
        else size=options.outHeight;
        options.inSampleSize = calculateInSampleSize(options, size,
                size); // ����inSampleSize
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(url, options); 
        /*return src;*/// ����һ���Դ������ͼ

        /*if(change){
        if(options.outWidth>options.outHeight)
        return createScaleBitmap(src, reqWidth, reqWidth/re);
        else
        return createScaleBitmap(src, reqHeight*re, reqHeight);
        }
        else
        	return createScaleBitmap(src, reqWidth, reqWidth/re);*/
        // ��һ���õ�Ŀ���С������ͼ
        return createScaleBitmap(src,(double)reqWidth,reqWidth);
    }

    public Bitmap decodeSampledBitmapFromPath(Resources rs, int bm,  int reqWidth, int reqHeight,boolean change) throws FileNotFoundException {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;



        BitmapFactory.decodeResource(rs, bm, options);
        double re=0;
        re= ((double) options.outWidth)/options.outHeight;
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight); // ����inSampleSize
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeResource(rs, bm, options);
        /*return src;*/// ����һ���Դ������ͼ

        if(change){
            if(options.outWidth>options.outHeight)
                return createScaleBitmap(src, reqWidth, reqWidth/re);
            else
                return createScaleBitmap(src, reqHeight*re, reqHeight);
        }
        else
            return createScaleBitmap(src, reqWidth, reqWidth/re);
        // ��һ���õ�Ŀ���С������ͼ
    }
}
