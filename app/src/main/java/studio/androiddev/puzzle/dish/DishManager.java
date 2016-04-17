package studio.androiddev.puzzle.dish;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import studio.androiddev.puzzle.PuzzleApplication;
import studio.androiddev.puzzle.event.GameSuccessEvent;
import studio.androiddev.puzzle.event.PieceMoveSuccessEvent;
import studio.androiddev.puzzle.utils.DensityUtil;

/**
 * puzzle
 * 拼盘管理类，用于更新拼盘的显示
 * Created by ZQ on 2016/4/3.
 */
public class DishManager{
    //拼盘
    private ImageView mImageView;
    //游戏难度
    private int mLevel;
    //拼图图片
    private Bitmap mBitmap;
    //拼图数组
    private boolean[] mIndex;
    //拼块数目
    private int mSize;
    //距离游戏胜利还需移动的拼块数目
    private int mLeftSize;
    //拼盘大小
    private static int DISH_HEIGHT = 300;
    private static int DISH_WIDTH = 300;

    //基本款遮罩拼块
    private static Bitmap[] bmcover = new Bitmap[9];
    private static int COVER_CENTER = 0;
    private static int COVER_TOP = 1;
    private static int COVER_BOTTOM = 2;
    private static int COVER_LEFT = 3;
    private static int COVER_RIGHT = 4;
    private static int COVER_TOP_LEFT = 5;
    private static int COVER_TOP_RIGHT = 6;
    private static int COVER_BOTTOM_LEFT = 7;
    private static int COVER_BOTTOM_RIGHT = 8;
    //拼块凹凸半径
    float r;
    //拼块内的矩形长宽
    float rectWidth;
    float rectHeight;
    //拼块的长宽
    float width;
    float height;

    public DishManager(ImageView imageView, Bitmap bitmap, int level){
        mImageView = imageView;
        mBitmap = bitmap;
        mLevel = level;
        mSize = level * level;
        mLeftSize = mSize;
        mIndex = new boolean[mSize];
        for(int i = 0; i < mSize; i++) mIndex[i] = false;
        
        initMask();

        imageView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                int index = (int) event.getLocalState();

                switch (event.getAction()){
                    case DragEvent.ACTION_DRAG_STARTED:

                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:

                        break;
                    case DragEvent.ACTION_DRAG_EXITED:

                        break;
                    case DragEvent.ACTION_DRAG_ENDED:

                        break;
                    case DragEvent.ACTION_DROP:

                        if(judgeDrag(index, x, y)){
                            updatePiece(index);
                        }

                        break;
                    default:
                        break;
                }

                return true;
            }
        });

        refreshDish();
    }

    public void setBitmap(Bitmap bitmap){
        mBitmap = bitmap;
    }

    public void setLevel(int level){
        mLevel = level;
    }


    /**
     * 判断拼块是否被拖动到正确的位置
     * @param i 拼块下标
     * @param x 拖动事件x坐标
     * @param y 拖动事件y坐标
     * @return 判断结果
     */
    public boolean judgeDrag(int i, int x, int y){
        RectF rect = getRectF(i);
        int dpX = DensityUtil.px2dip(PuzzleApplication.getAppContext(), x);
        int dpY = DensityUtil.px2dip(PuzzleApplication.getAppContext(), y);

        return rect.contains(dpX, dpY);
    }

    /**
     * 根据拼块下标计算拼块实际占据的方形大小
     * @param i 拼块下标
     * @return 拼块实际占据的方形大小
     */
    private RectF getRectF(int i){

        return new RectF(
                (i % mLevel) * rectWidth,
                (i / mLevel) * rectHeight,
                (i % mLevel + 1) * rectWidth,
                (i / mLevel + 1) * rectHeight);
    }

    /**
     * 当玩家将拼块移动到正确位置时调用，更新拼盘显示状态
     * @param i 正确的拼块下标
     */
    public void updatePiece(int i){
        if(i < 0 || i > mLevel * mLevel) return;

        mIndex[i] = true;
        refreshDish();
        EventBus.getDefault().post(new PieceMoveSuccessEvent(i));
        mLeftSize--;
        if(mLeftSize == 0){
            EventBus.getDefault().post(new GameSuccessEvent());
        }
    }

    /**
     * 根据拼盘大小和游戏难度初始化基本遮罩拼块
     */
    private void initMask(){

        r = DISH_WIDTH / mLevel / 4;
        rectWidth = DISH_WIDTH / mLevel;
        rectHeight = DISH_HEIGHT / mLevel;
        width = rectWidth + r;
        height = rectHeight + r;
        for(int i = 0; i < 9; i++){
            bmcover[i] = Bitmap.createBitmap(
                    (int) width,
                    (int) height,
                    Bitmap.Config.ARGB_8888);
        }

        Paint p = new Paint();
        p.setColor(Color.RED);

        Paint pOut = new Paint();
        pOut.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        pOut.setAntiAlias(true);
        pOut.setColor(Color.RED);

        Paint pOver = new Paint();
        pOver.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        pOver.setAntiAlias(true);
        pOver.setColor(Color.RED);

        RectF ovalLeft = new RectF(0, rectHeight / 2 - r, 2 * r, rectHeight / 2 + r);
        RectF ovalTop = new RectF(rectWidth / 2 - r, - r, rectWidth / 2 + r, r);
        RectF ovalTopRight = new RectF(rectWidth / 2, - r, rectWidth / 2 + 2 * r, r);
        RectF ovalRight = new RectF(rectWidth - r, rectHeight / 2 - r, rectWidth + r, rectHeight / 2 + r);
        RectF ovalBottom = new RectF(rectWidth / 2 - r, rectHeight - r, rectWidth / 2 + r, rectHeight + r);
        RectF ovalRightRight = new RectF(rectWidth, rectHeight / 2 - r, rectWidth + 2 * r, rectHeight / 2 + r);
        RectF ovalBottomRight = new RectF(rectWidth / 2, rectHeight - r, rectWidth / 2 + 2 * r, rectHeight + r);


        Canvas canvas = new Canvas(bmcover[COVER_TOP_LEFT]);
        canvas.drawRect(0, 0, rectWidth, rectHeight, p);
        canvas.drawArc(ovalRight, 0, 360, true, pOut);
        canvas.drawArc(ovalBottom, 0, 360, true, pOver);
        canvas.save();

        canvas.setBitmap(bmcover[COVER_TOP]);
        canvas.drawRect(r, 0, r + rectWidth, rectHeight, p);
        canvas.drawArc(ovalLeft, 0, 360, true, pOver);
        canvas.drawArc(ovalBottomRight, 0, 360, true, pOver);
        canvas.drawArc(ovalRightRight, 0, 360, true, pOut);
        canvas.save();

        canvas.setBitmap(bmcover[COVER_TOP_RIGHT]);
        canvas.drawRect(r, 0, r + rectWidth, rectHeight, p);
        canvas.drawArc(ovalLeft, 0, 360, true, pOver);
        canvas.drawArc(ovalBottomRight, 0, 360, true, pOver);
        canvas.save();

        canvas.setBitmap(bmcover[COVER_LEFT]);
        canvas.drawRect(0, 0, rectWidth, rectHeight, p);
        canvas.drawArc(ovalBottom, 0, 360, true, pOver);
        canvas.drawArc(ovalTop, 0, 360, true, pOut);
        canvas.drawArc(ovalRight, 0, 360, true, pOut);
        canvas.save();

        canvas.setBitmap(bmcover[COVER_CENTER]);
        canvas.drawRect(r, 0, r + rectWidth, rectHeight, p);
        canvas.drawArc(ovalTopRight, 0, 360, true, pOut);
        canvas.drawArc(ovalRightRight, 0, 360, true, pOut);
        canvas.drawArc(ovalLeft, 0, 360, true, pOver);
        canvas.drawArc(ovalBottomRight, 0, 360, true, pOver);
        canvas.save();

        canvas.setBitmap(bmcover[COVER_BOTTOM_LEFT]);
        canvas.drawRect(0, 0, rectWidth, rectHeight, p);
        canvas.drawArc(ovalTop, 0, 360, true, pOut);
        canvas.drawArc(ovalRight, 0, 360, true, pOut);
        canvas.save();

        canvas.setBitmap(bmcover[COVER_BOTTOM]);
        canvas.drawRect(r, 0, rectWidth + r, rectHeight, p);
        canvas.drawArc(ovalLeft, 0, 360, true, pOver);
        canvas.drawArc(ovalTopRight, 0, 360, true, pOut);
        canvas.drawArc(ovalRightRight, 0, 360, true, pOut);
        canvas.save();

        canvas.setBitmap(bmcover[COVER_BOTTOM_RIGHT]);
        canvas.drawRect(r, 0, rectWidth + r, rectHeight, p);
        canvas.drawArc(ovalLeft, 0, 360, true, pOver);
        canvas.drawArc(ovalTopRight, 0, 360, true, pOut);
        canvas.save();

        canvas.setBitmap(bmcover[COVER_RIGHT]);
        canvas.drawRect(r, 0, rectWidth + r, rectHeight, p);
        canvas.drawArc(ovalTopRight, 0, 360, true, pOut);
        canvas.drawArc(ovalLeft, 0, 360, true, pOver);
        canvas.drawArc(ovalBottomRight, 0, 360, true, pOver);
        canvas.save();



    }

    /**
     * 根据当前游戏进度生成遮罩层
     * @return 遮罩层Bitmap
     */
    private Bitmap getMask(){
        Bitmap mask = Bitmap.createBitmap(DISH_WIDTH, DISH_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mask);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.RED);

//        mIndex[0] = true;
//        mIndex[6] = true;

        for(int i = 0; i < mSize; i += mLevel){
            for(int j = i; j < i + mLevel; j++){
                if(!mIndex[j]) continue;

                //最右一列
                if((j + 1) % mLevel == 0) {
                    //右上角
                    if(j == mLevel - 1)
                        canvas.drawBitmap(bmcover[COVER_TOP_RIGHT], rectWidth * (mLevel - 1) - r, 0, p);
                        //右下角
                    else if (j == mSize - 1)
                        canvas.drawBitmap(
                                bmcover[COVER_BOTTOM_RIGHT],
                                rectWidth * (mLevel - 1) - r,
                                rectHeight * (mLevel - 1), p);
                    else
                        canvas.drawBitmap(bmcover[COVER_RIGHT],
                                rectWidth * (mLevel - 1) - r,
                                rectHeight * (i / mLevel), p);
                }
                //最左一列
                else if(j % mLevel == 0) {
                    //左上角
                    if(j == 0)
                        canvas.drawBitmap(bmcover[COVER_TOP_LEFT], 0, 0, p);
                    //左下角
                    if(j == mSize - mLevel + 1)
                        canvas.drawBitmap(bmcover[COVER_BOTTOM_LEFT],
                                0,
                                rectHeight * (mLevel - 1), p);
                    else
                        canvas.drawBitmap(bmcover[COVER_LEFT],
                                0,
                                rectHeight * (i / mLevel), p);
                }
                //最上一行
                else if(j < mLevel){
                    canvas.drawBitmap(bmcover[COVER_TOP],
                            rectWidth * j - r,
                            0, p);
                }
                //最下一行
                else if(j > (mSize - mLevel)){
                    canvas.drawBitmap(bmcover[COVER_BOTTOM],
                            rectWidth * (j % mLevel) - r,
                            rectHeight * (mLevel - 1), p);
                }
                //中间
                else{
                    canvas.drawBitmap(bmcover[COVER_CENTER],
                            rectWidth * (j % mLevel) - r,
                            rectHeight * (i / mLevel), p);
                }

            }

        }

        canvas.save();

        return mask;
    }

    /**
     * 刷新拼盘显示
     * 首先获取遮罩层，然后将遮罩层与原始图片混合并显示
     */
    private void refreshDish(){
        if(mBitmap == null) return;
        //获取遮罩层
        Bitmap mask = getMask();

        //将遮罩层与原始图片混合并显示在ImageView

        Bitmap output = Bitmap.createBitmap(DISH_WIDTH, DISH_HEIGHT, Bitmap.Config.ARGB_8888);
        Paint p = new Paint();

        p.setAntiAlias(true);
        Canvas canvas = new Canvas(output);
        canvas.drawBitmap(mask, 0, 0, p);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        RectF rect = new RectF(0, 0, DISH_WIDTH, DISH_HEIGHT);
        canvas.drawBitmap(mBitmap, null, rect, p);
        mImageView.setImageBitmap(output);
    }

}
