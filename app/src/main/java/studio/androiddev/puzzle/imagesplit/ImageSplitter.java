package studio.androiddev.puzzle.imagesplit;

/**
 * Created by Administrator on 2016/3/27.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import studio.androiddev.puzzle.dish.DishManager;

public class ImageSplitter {

    private static final String TAG = "puzzle";

    public static List<ImagePiece> split(Bitmap bitmap, int level, int dishWidth, int dishHeight)
            throws FileNotFoundException {
        Bitmap[] bmcover = DishManager.getBitmapCover();
//        Bitmap[] bmcover=new Bitmap[9];
//        BitmapUtils bu=new BitmapUtils();
//        bmcover[0]=bu.decodeSampledBitmapFromPath(rs, R.drawable.cover, display.getWidth()/xPiece, display.getHeight()/yPiece,false);
//        bmcover[1]=bu.decodeSampledBitmapFromPath(rs, R.drawable.cover1, display.getWidth()/xPiece, display.getHeight()/yPiece,false);
//        bmcover[2]=bu.decodeSampledBitmapFromPath(rs, R.drawable.cover2, display.getWidth()/xPiece, display.getHeight()/yPiece,false);
//        bmcover[3]=bu.decodeSampledBitmapFromPath(rs, R.drawable.cover3, display.getWidth()/xPiece, display.getHeight()/yPiece,false);
//        bmcover[4]=bu.decodeSampledBitmapFromPath(rs, R.drawable.cover4, display.getWidth()/xPiece, display.getHeight()/yPiece,false);
//        bmcover[5]=bu.decodeSampledBitmapFromPath(rs, R.drawable.cover_left, display.getWidth()/xPiece, display.getHeight()/yPiece,false);
//        bmcover[6]=bu.decodeSampledBitmapFromPath(rs, R.drawable.cover_right, display.getWidth()/xPiece, display.getHeight()/yPiece,false);
//        bmcover[7]=bu.decodeSampledBitmapFromPath(rs, R.drawable.cover_buttom, display.getWidth()/xPiece, display.getHeight()/yPiece,false);
//        bmcover[8]=bu.decodeSampledBitmapFromPath(rs, R.drawable.cover_top, display.getWidth()/xPiece, display.getHeight()/yPiece,false);
        List<ImagePiece> pieces = new ArrayList<>(level * level);

//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        int pieceWidth = width / xPiece;
//        int pieceHeight = height /yPiece;
        int pieceWidth2 = dishWidth / level;
        int pieceHeight2 = dishHeight / level;
        PorterDuffXfermode pdf = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        Log.d(TAG, "pieceWidth: " + pieceWidth2);

        int k;
        Log.d(TAG, "circle begin");
        for (int i = 0; i < level; i++) {
            for (int j = 0; j < level; j++) {
                if(i == 0) {
                    if (j == 0) {
                        k = DishManager.COVER_TOP_LEFT;
                    } else if(j == level - 1) {
                        k = DishManager.COVER_TOP_RIGHT;
                    }
                    else  {
                        k = DishManager.COVER_TOP;
                    }
                }
                else if(i == level - 1) {
                    if(j == 0) {
                        k = DishManager.COVER_BOTTOM_LEFT;
                    } else if (j == level - 1) {
                        k = DishManager.COVER_BOTTOM_RIGHT;
                    } else{
                        k = DishManager.COVER_BOTTOM;
                    }
                }
                else if(j == 0) {
                    k = DishManager.COVER_LEFT;
                }else if(j == level - 1) {
                    k = DishManager.COVER_RIGHT;
                }else  {
                    k = DishManager.COVER_CENTER;
                }

                Log.d(TAG, "piece " + k + " compute completed");

                ImagePiece piece = new ImagePiece();
                piece.index = j + i * level;
                int xValue, yValue;

                if(j != 0) {
                    xValue = (int) (j * (pieceWidth2 - pieceWidth2 * 0.25));
                }
                else {
                    xValue = j * pieceWidth2;
                }
                if(i != 0) {
                    yValue = (int) (i * (pieceHeight2 - pieceWidth2 * 0.25));
                }
                else {
                    yValue = i * pieceHeight2;
                }

//                xValue = j * pieceWidth2;
//                yValue = i * pieceHeight2;

                Bitmap drawingBitmap = Bitmap.createBitmap(pieceWidth2,
                        pieceHeight2, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(drawingBitmap);
                Paint paint = new Paint();

                //final float scale = DensityUtil.getScale(PuzzleApplication.getAppContext());
                final float scale = (float) pieceWidth2 / bmcover[k].getWidth();
                Matrix matrix = new Matrix();
                matrix.setScale(scale, scale);
                //canvas.drawBitmap(bmcover[k], 0, 0, paint);
                canvas.drawBitmap(bmcover[k], matrix, paint);

                paint.setXfermode(pdf);
                piece.bitmap = Bitmap.createBitmap(bitmap, xValue, yValue, pieceWidth2, pieceHeight2);
                canvas.drawBitmap(piece.bitmap, 0, 0, paint);
                piece.bitmap = drawingBitmap;
                pieces.add(piece);

                Log.d(TAG, "piece " + k + " draw completed");
            }
        }

        return pieces;
    }

}