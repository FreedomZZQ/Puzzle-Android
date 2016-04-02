package studio.androiddev.puzzle.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import studio.androiddev.puzzle.adapter.DragAdapter;
import studio.androiddev.puzzle.intfc.onMoveListener;

/**
 * Created by Administrator on 2016/3/26.
 */
public class DragGridView extends GridView implements onMoveListener{


    private static final boolean DEBUG = true;
    private static final String TAG = "DragSortGridView";
    private static final SparseArray<String> DRAG_EVENT_ACTION;

    static {
        if (DEBUG) {
            DRAG_EVENT_ACTION = new SparseArray<String>();
            DRAG_EVENT_ACTION.put(DragEvent.ACTION_DRAG_STARTED,
                    "ACTION_DRAG_STARTED");
            DRAG_EVENT_ACTION.put(DragEvent.ACTION_DRAG_ENTERED,
                    "ACTION_DRAG_ENTERED");
            DRAG_EVENT_ACTION.put(DragEvent.ACTION_DRAG_LOCATION,
                    "ACTION_DRAG_LOCATION");
            DRAG_EVENT_ACTION.put(DragEvent.ACTION_DRAG_EXITED,
                    "ACTION_DRAG_EXITED");
            DRAG_EVENT_ACTION.put(DragEvent.ACTION_DRAG_ENDED,
                    "ACTION_DRAG_ENDED");
            DRAG_EVENT_ACTION.put(DragEvent.ACTION_DROP, "ACTION_DROP");
        }
    }

    private static final int FLAG_DRAW_APPEARING = 0x1;
    private static final int FLAG_DRAW_DISAPPEARING = 0x2;

    private boolean mDragSortEnabled;//用来表示DragGridView中的视图是否可以长按拖动

    private int mLastDragPosition;
    private int mLastTargetPosition;
    private int mNewDragPositoion;//拖动的图片位置
    private int mNewTargetPosition;//图片拖到的位置
    private int mAppearingPosition;//拖动图片到达位置，用来指示特别效果显示的位置
    private int mDisappearingPosition;//拖动图片的起始拖动位置，用来指示特别效果显示的位置
    private int mMovingChildViewsNum;//总的需要移动的视图数目
    private int mPrivateFlags;//用来指示拖动图片时特效的出现与消失

    private Drawable mAppearingDrawable;//拖动图片到目标位置时显示的特效
    private Drawable mDisappearingDrawable;//拖动图片离开起始位置时显示的特效
    private Animation mFadeOutAnimation;//拖动图片时图片隐藏的动画
    private Handler mHandler = new Handler();//用来后台处理图片拖动后通知资源列表重新排序
    private DragAdapter mDragAdapter;//拖动视图的适配器
    private  View mDragView;//获取被拖动的视图，用来在后面拖动完成后取消动画
    private boolean swap;//拖动后视图的重排方式，true为交换方式，false为插入方式

    public DragGridView(Context context) {
        super(context);
        init(context);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    //初始化DragGridView
    private final void init(final Context context) {

        mDragSortEnabled = true;
        swap=false;
        mLastDragPosition=-1;
        mLastTargetPosition=-1;
        mNewDragPositoion=-1;
        mNewTargetPosition=-1;
        mAppearingPosition=-1;
        mDisappearingPosition=-1;
        mMovingChildViewsNum=0;

        mFadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        mFadeOutAnimation.setDuration(150);
        mFadeOutAnimation.setFillEnabled(true);
        mFadeOutAnimation.setFillAfter(true);
        mFadeOutAnimation.setInterpolator(new AccelerateInterpolator());
        //设置DragGridView的长按监听器
        setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                if (mDragSortEnabled) {
                    view.startDrag(null, new View.DragShadowBuilder(view),
                            position, 0);
                    return true;
                }
                return false;
            }

        });
        //设置拖动监听器
        setOnDragListener(new OnDragListener() {

            @Override
            public boolean onDrag(View v, DragEvent event) {
                //获取拖动时的x和y坐标，由于onDrag回调函数会被不停的调用，因此在拖动图片的过程中会不断的获取变化的x和y坐标
                int x = Math.round(event.getX());
                int y = Math.round(event.getY());
                //获取拖动事件的动作
                final int action = event.getAction();
                //调试时显示的日志
                if (DEBUG) {
                    Log.d("adapter",
                            "event action:" + DRAG_EVENT_ACTION.get(action)
                                    + "---------x:" + x + "y:" + y);
                }
                //获取该DragGridView的适配器
                ListAdapter adapter = getAdapter();

                switch (action) {
                    //在拖动事件开始时
                    case DragEvent.ACTION_DRAG_STARTED:

                        if (adapter instanceof DragAdapter) {
                            if(mDragAdapter==null) {
                                mDragAdapter = (DragAdapter) adapter;
                            }
                        }
                        mLastDragPosition=mNewDragPositoion;
                        mNewDragPositoion = (Integer) event.getLocalState();//获取拖动的图片的起始位置
                        View dragView =mDragView= getView(mNewDragPositoion);
                        dragView.startAnimation(mFadeOutAnimation);

                        mPrivateFlags |= FLAG_DRAW_DISAPPEARING;

                        mDisappearingPosition = mNewDragPositoion;

                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    //拖动结束后的处理
                    case DragEvent.ACTION_DRAG_ENDED:

                        if(DEBUG) {
                            Log.d("ACTION_DRAG_ENDED","  fromPos:" + mNewDragPositoion+  " toPos:" + mNewTargetPosition);
                        }

                        mDragView.clearAnimation();//拖动结束后清除被拖动的视图的动画，让图片完整显示在新位置
                        //创建executor通知后台线程重排资源列表
                        NotifyExecutor executor = new NotifyExecutor(mNewDragPositoion, mNewTargetPosition);
                        mHandler.post(executor);

                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        break;
                    //当用户释放鼠标的时候激发
                    case DragEvent.ACTION_DROP:
                        if (mMovingChildViewsNum > 0) {
                            break;
                        }

                        mLastTargetPosition=mNewTargetPosition;
                        //取得释放鼠标出的位置
                        mNewTargetPosition= getOriginalPos(x, y);

                        if (-1 != mNewTargetPosition && mNewTargetPosition != mNewDragPositoion) {

                            if (DEBUG) {
                                Log.d(TAG, "targetPos:" + mNewTargetPosition);
                            }

                            // 如果目标位置和拖动位置相同，则不在目标显示Appearing
                            if (mNewTargetPosition != mNewDragPositoion) {
                                mPrivateFlags &= ~FLAG_DRAW_DISAPPEARING;
                                mPrivateFlags |= FLAG_DRAW_APPEARING;
                                mAppearingPosition = mNewTargetPosition;
                            }
                             //释放鼠标后重新排序子视图
                            reOrderViews(mNewDragPositoion, mNewTargetPosition,swap);

                        }
                        break;
                }

                return true;
            }
        });

    }

    @Override
    //绘制每一个子视图
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d(TAG, "onDraw");

        if ((mPrivateFlags & FLAG_DRAW_APPEARING) != 0) {

            Drawable drawable = mAppearingDrawable;
            int position = mAppearingPosition;
            if (position != -1 && drawable != null) {

                drawIndicator(canvas, position, drawable);
            }
        } else if ((mPrivateFlags & FLAG_DRAW_DISAPPEARING) != 0) {
            Drawable drawable = mDisappearingDrawable;
            int position = mDisappearingPosition;
            if (position != -1 && drawable != null) {

                drawIndicator(canvas, position, drawable);
            }
        }
    }

    // 在position位置显示将要添加item的标志
    protected void showAppearingAtPosition(int position) {
        mPrivateFlags |= FLAG_DRAW_APPEARING;
        this.mAppearingPosition = position;
    }

    // 在position位置显示将要移除item的标志
    protected void showDisappearingAtPosition(int position) {
        mPrivateFlags |= FLAG_DRAW_DISAPPEARING;
        this.mDisappearingPosition = position;
    }

    protected void hideAppearing() {
        mPrivateFlags &= ~FLAG_DRAW_APPEARING;
    }

    protected void hideDisappearing() {
        mPrivateFlags &= ~FLAG_DRAW_DISAPPEARING;
    }
//设置DragGridView能否被拖动
    public void setDragSortEnabled(boolean enabled) {
        this.mDragSortEnabled = enabled;
    }
    //绘制视图
    protected void drawIndicator(Canvas canvas, int position, Drawable drawable) {
        if (position == -1) {
            return;
        }
        View view = getView(position);
        if (view == null) {
            return;
        }
        canvas.save();
        int l = view.getLeft();
        int t = view.getTop();
        canvas.translate(l, t);
        drawable.setBounds(0, 0, view.getWidth(), view.getHeight());
        drawable.draw(canvas);
        canvas.restore();
    }

    // 在动画完成后发出顺序改变的通知
    private final void notifyAfterMoveView(int fromPos, int toPos) {

            if (-1 == toPos || fromPos == toPos) {
                return;
            }
            else {
                if (mDragAdapter == null) {
                    mDragAdapter = (DragAdapter) getAdapter();
                }
               //通知mDragAdapter重新排序资源列表
                mDragAdapter.onReOrderContent(fromPos, toPos,swap);
                if (mDragAdapter.isOrdered() == true) {
                    Toast toast = Toast.makeText(getContext(), "恭喜你过关了", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    //通知执行器，封装了开启新线程执行通知后台处理事务的函数
    private class NotifyExecutor implements Runnable {

        private int mFromPos;
        private int mToPos;

        public NotifyExecutor(int fromPos, int toPos) {
            this.mFromPos = fromPos;
            this.mToPos = toPos;
        }

        @Override
        public void run() {

            if (mMovingChildViewsNum > 0) {
                mHandler.postDelayed(this, 200);
            } else {
                mPrivateFlags &= ~FLAG_DRAW_APPEARING;
                notifyAfterMoveView(mFromPos, mToPos);
            }
        }

    }
    //重新排序视图
    private void reOrderViews(int fromPos, int toPos,boolean swap) {

        Log.d(TAG, "reOrder:" + fromPos + "<-->" + toPos);

        if (fromPos == toPos) {
            return;
        }

        // 在移动View子前，取得拖动的View，以及目标位置
        final View fromView = getView(fromPos);
        final View toView = getView(toPos);
        final Rect toRect = new Rect();
        getRect(toView, toRect);

        //mDragAdapter.onReOrderContent(fromPos,toPos,false);
        //插入方式重排
        if(swap==false) {
            if (fromPos > toPos) {
                for (int i = fromPos; i > toPos; i--) {
                    onMove(i - 1, i);
                }
            } else {
                for (int i = fromPos; i < toPos; i++) {
                    onMove(i + 1, i);
                }
            }
        }
        //交换方式重排
        else{
            onMove(toPos,fromPos);
        }
        fromView.layout(toRect.left, toRect.top, toRect.right, toRect.bottom);
       // mReorderArray.reOrder(fromPos, toPos);
    }

    @Override
    //子视图的移动，从fromPos到toPos位置
    public void onMove(int fromPos, int toPos) {

        if (DEBUG) {
            Log.d(TAG, fromPos + " move to " + toPos);
        }

        moveView(fromPos, toPos);

    }

    // 传入实际的都是实际位置
    private void moveView(int fromPos, int toPos) {

        final View fromView = getView(fromPos);
        final View toView = getView(toPos);

        final Rect fromRect = new Rect();
        final Rect toRect = new Rect();

        getRect(fromView, fromRect);
        getRect(toView, toRect);

        int tx = toRect.left - fromRect.left;
        int ty = toRect.top - fromRect.top;

        Animation translate = new TranslateAnimation(0, tx, 0, ty);

        translate.setDuration(300);
        translate.setFillEnabled(true);
        translate.setFillAfter(true);
        translate.setAnimationListener(new MoveViewAnimationListener(fromView,
                toView.getLeft(), toView.getTop()));

        fromView.startAnimation(translate);
        mMovingChildViewsNum++;
    }

    // 返回实际position的View对象
    protected View getView(int actualPos) {
        // 将实际position转换成原始position
        return getChildAt(actualPos - getFirstVisiblePosition());
    }
    //获取一个视图的轮廓矩形，也就是绘制时的矩形外框
    protected void getRect(View view, Rect rect) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(),
                view.getBottom());
    }

    // 返回坐标点View对象的原始位置
    private int getOriginalPos(int x, int y) {
        return pointToPosition(x, y);
    }


    public void setAppearingDrawable(int id) {
        mAppearingDrawable = getResources().getDrawable(id);
    }

    public void setDisappearingDrawable(int id) {
        mDisappearingDrawable = getResources().getDrawable(id);
    }

    protected Drawable getAppearingDrawable() {
        return mAppearingDrawable;
    }

    protected Drawable getDisappearingDrawable() {
        return mDisappearingDrawable;
    }

    //子视图移动的时的监听器，有视图处理视图移动开始，重复和结束的函数
    private class MoveViewAnimationListener implements
            Animation.AnimationListener {

        private View mTarget;
        private int mNewX, mNewY;

        public MoveViewAnimationListener(View target, int newX, int newY) {
            this.mTarget = target;
            this.mNewX = newX;
            this.mNewY = newY;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mTarget.layout(mNewX, mNewY, mNewX + mTarget.getWidth(), mNewY
                    + mTarget.getHeight());
            mTarget.clearAnimation();
            mMovingChildViewsNum--;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

    }
}
