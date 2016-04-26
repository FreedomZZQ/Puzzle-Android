package studio.androiddev.puzzle.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import studio.androiddev.puzzle.PuzzleApplication;
import studio.androiddev.puzzle.R;
import studio.androiddev.puzzle.bgm.MusicServer;
import studio.androiddev.puzzle.dish.DishManager;
import studio.androiddev.puzzle.dish.DragImageView;
import studio.androiddev.puzzle.event.DishManagerInitFinishEvent;
import studio.androiddev.puzzle.event.DishManagerInitStartEvent;
import studio.androiddev.puzzle.event.GameSuccessEvent;
import studio.androiddev.puzzle.event.PieceMoveSuccessEvent;
import studio.androiddev.puzzle.imagesplit.ImagePiece;
import studio.androiddev.puzzle.imagesplit.ImageSplitter;
import studio.androiddev.puzzle.utils.BitmapUtils;
import studio.androiddev.puzzle.utils.DensityUtil;
import studio.androiddev.puzzle.utils.GameTimer;
import studio.androiddev.puzzle.utils.GlobalUtils;

public class GameActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.dish)
    ImageView dish;
    @Bind(R.id.layViewContainer)
    LinearLayout layViewContainer;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.gameContainer)
    LinearLayout gameContainer;
    @Bind(R.id.timeText)
    TextView timeText;

    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GameTimer.MESSAGE_TIMER:
                    refreshTimeText();
                    break;

            }
        }
    };
    private GameTimer gameTimer;
    private int time = 0;

    private DishManager dm;
    private Bitmap mBitmap;

    private final int DISH_WIDTH = 300;
    private final int DISH_HEIGHT = 300;
    private HashMap<Integer, View> pieceList = new HashMap<>();
    private static final String PIC_INDEX = "picIndex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        EventBus.getDefault().register(this);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        gameTimer = new GameTimer(timeHandler);

        EventBus.getDefault().post(new DishManagerInitStartEvent());
        initialization();
        EventBus.getDefault().post(new DishManagerInitFinishEvent());
    }

    private void refreshTimeText(){
        time++;

        int curminute = time / 60;
        int cursecond = time % 60;

        String curTimeString = String.format("%02d:%02d", curminute, cursecond);
        timeText.setText(curTimeString);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Intent intent = new Intent(GameActivity.this, MusicServer.class);
        //startService(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Intent intent = new Intent(GameActivity.this, MusicServer.class);
        //stopService(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PieceMoveSuccessEvent event) {
        int index = event.getIndex();
        DragImageView view = (DragImageView) pieceList.get(index);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GameSuccessEvent event) {
        // TODO: 2016/4/24 游戏胜利的逻辑 根据结束时的时间更新排行
        Toast.makeText(GameActivity.this, "Congratulations!", Toast.LENGTH_SHORT).show();
        gameTimer.stopTimer();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DishManagerInitFinishEvent event) {
        showProgress(false);
        gameTimer.startTimer();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DishManagerInitStartEvent event) {
        showProgress(true);
    }

    private void initialization() {
        Log.d(TAG, "init begin");
        layViewContainer.removeAllViews();
        pieceList.clear();

        int picIndex = getIntent().getIntExtra(PIC_INDEX, 0);
        mBitmap = BitmapUtils.decodeSampledBitmapFromResources(getResources(),
                ChoosePicActivity.icons[picIndex],
                DISH_WIDTH, DISH_HEIGHT);
        dm = PuzzleApplication.getDishManager();

        if (dm == null) return;

        dm.initNewGame(mBitmap, dish);

        Log.d(TAG, "DishManager init finish");

        try {

            // TODO: 2016/4/24 这里切割图片有bug 需要继续优化算法
            List<ImagePiece> IPL = ImageSplitter.split(mBitmap, PuzzleApplication.getLevel(),
                    DensityUtil.dip2px(GameActivity.this, DISH_WIDTH),
                    DensityUtil.dip2px(GameActivity.this, DISH_HEIGHT));

//            List<ImagePiece> IPL = ImageSplitter.split(mBitmap, mLevel, DISH_WIDTH, DISH_HEIGHT);

            Log.d(TAG, "split finish");

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMarginEnd(20);

            int mLevel = PuzzleApplication.getLevel();

            for (int i = 0; i < mLevel; i++) {
                for (int j = 0; j < mLevel; j++) {
                    DragImageView imageView = new DragImageView(this);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setImageBitmap(IPL.get(j + i * mLevel).bitmap);

                    imageView.setIndex(j + i * mLevel);
                    pieceList.put(imageView.getIndex(), imageView);
                }
            }

            int[] list = GlobalUtils.getRamdomList(mLevel * mLevel);

            if (layViewContainer != null) {
                for (int aList : list) {
                    layViewContainer.addView(pieceList.get(aList), layoutParams);
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "init finish");
    }

    /**
     * Shows the progress UI and hides the dish and the pieceContainer.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            gameContainer.setVisibility(show ? View.GONE : View.VISIBLE);
            gameContainer.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    gameContainer.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            gameContainer.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void actionStart(Context context, int picIndex) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(PIC_INDEX, picIndex);
        context.startActivity(intent);
    }

}
