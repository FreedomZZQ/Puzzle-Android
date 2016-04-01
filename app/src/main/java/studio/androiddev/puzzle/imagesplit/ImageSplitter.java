package studio.androiddev.puzzle.imagesplit;

/**
 * Created by Administrator on 2016/3/27.
 */

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.Display;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import studio.androiddev.puzzle.R;
import studio.androiddev.puzzle.utils.BitmapUtils;

public class ImageSplitter {

    public static List<ImagePiece> split(Bitmap bitmap, Resources rs, Display display,
                                         int xPiece, int yPiece) throws FileNotFoundException {
        Bitmap[] bmcover=new Bitmap[9];
        BitmapUtils bu=new BitmapUtils();
        bmcover[0]=bu.decodeSampledBitmapFromPath(rs, R.drawable.cover, display.getWidth()/xPiece, display.getHeight()/yPiece,false);
        bmcover[1]=bu.decodeSampledBitmapFromPath(rs, R.drawable.cover1, display.getWidth()/xPiece, display.getHeight()/yPiece,false);
        bmcover[2]=bu.decodeSampledBitmapFromPath(rs, R.drawable.cover2, display.getWidth()/xPiece, display.getHeight()/yPiece,false);
        bmcover[3]=bu.decodeSampledBitmapFromPath(rs, R.drawable.cover3, display.getWidth()/xPiece, display.getHeight()/yPiece,false);
        bmcover[4]=bu.decodeSampledBitmapFromPath(rs, R.drawable.cover4, display.getWidth()/xPiece, display.getHeight()/yPiece,false);
        bmcover[5]=bu.decodeSampledBitmapFromPath(rs, R.drawable.cover_left, display.getWidth()/xPiece, display.getHeight()/yPiece,false);
        bmcover[6]=bu.decodeSampledBitmapFromPath(rs, R.drawable.cover_right, display.getWidth()/xPiece, display.getHeight()/yPiece,false);
        bmcover[7]=bu.decodeSampledBitmapFromPath(rs, R.drawable.cover_buttom, display.getWidth()/xPiece, display.getHeight()/yPiece,false);
        bmcover[8]=bu.decodeSampledBitmapFromPath(rs, R.drawable.cover_top, display.getWidth()/xPiece, display.getHeight()/yPiece,false);
        List<ImagePiece> pieces = new ArrayList<ImagePiece>(xPiece * yPiece);
        PorterDuffXfermode pdf=new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pieceWidth = width / xPiece;
        int pieceHeight = height /yPiece;
        int pieceWidth2=bmcover[0].getWidth();
        int pieceHeight2=bmcover[0].getHeight();
        int k;
        for (int i = 0; i < yPiece; i++) {
            for (int j = 0; j < xPiece; j++) {
                if(i==0) {
                    if (j == 0) {
                        k=1;
                    } else if(j==xPiece-1) k=4;
                    else  k=8;
                }
                else if(i==yPiece-1){
                    if(j==0) {k=2;}
                        else if (j == xPiece - 1) {k=3;}
                    else
                           k=7;
                }
                else if(j==0){
                   k=5;
                }else if(j==xPiece-1){
                    k=6;
                }else  k=0;

                ImagePiece piece = new ImagePiece();
                piece.index = j + i * xPiece;
                int xValue,yValue;
                if(j!=0)
                    xValue =(int) (j *( pieceWidth2-pieceWidth2*0.136));
                else xValue = j * pieceWidth2;
                if(i!=0)
                    yValue = (int) (i *( pieceHeight2-pieceWidth2*0.136));
                else yValue = i * pieceHeight2;
                Bitmap drawingBitmap = Bitmap.createBitmap(bmcover[k].getWidth(),
                        bmcover[k].getHeight(), bmcover[k].getConfig());
                Canvas canvas=new Canvas(drawingBitmap);
                Paint paint=new Paint();
                canvas.drawBitmap(bmcover[k],0,0,paint);
                paint.setXfermode(pdf);
                piece.bitmap = Bitmap.createBitmap(bitmap, xValue, yValue,
                        pieceWidth2, pieceHeight2);
                canvas.drawBitmap(piece.bitmap,0, 0,paint);
                piece.bitmap=drawingBitmap;
                pieces.add(piece);
            }
        }

        return pieces;
    }

}