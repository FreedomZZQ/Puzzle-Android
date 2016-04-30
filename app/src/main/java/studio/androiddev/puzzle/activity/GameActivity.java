package studio.androiddev.puzzle.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.listener.SaveListener;
import studio.androiddev.puzzle.PuzzleApplication;
import studio.androiddev.puzzle.R;
import studio.androiddev.puzzle.bgm.MusicServer;
import studio.androiddev.puzzle.dish.DishManager;
import studio.androiddev.puzzle.dish.DragImageView;
import studio.androiddev.puzzle.event.DishManagerInitFinishEvent;
import studio.androiddev.puzzle.event.GameSuccessEvent;
import studio.androiddev.puzzle.event.PieceMoveSuccessEvent;
import studio.androiddev.puzzle.event.TimeEvent;
import studio.androiddev.puzzle.imagesplit.ImagePiece;
import studio.androiddev.puzzle.imagesplit.ImageSplitter;
import studio.androiddev.puzzle.model.Record;
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
    @Bind(R.id.gameContainer)
    LinearLayout gameContainer;
    @Bind(R.id.timeText)
    TextView timeText;
    @Bind(R.id.viewContainer)
    HorizontalScrollView viewContainer;

    private StaticHandler timeHandler = new StaticHandler(GameActivity.this);
    private GameTimer gameTimer;
    private int time = 0;

    private DishManager dm;
    private Bitmap mBitmap;

    private final int DISH_WIDTH = 300;
    private final int DISH_HEIGHT = 300;
    private HashMap<Integer, View> pieceList = new HashMap<>();
    private static final String PIC_INDEX = "picIndex";

    private List<ImagePiece> IPL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        EventBus.getDefault().register(this);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        gameTimer = new GameTimer(timeHandler);

        initialization();
        EventBus.getDefault().post(new DishManagerInitFinishEvent());

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TimeEvent event) {
        time++;

        int curminute = time / 60;
        int cursecond = time % 60;

        String curTimeString = String.format("%02d:%02d", curminute, cursecond);
        timeText.setText(curTimeString);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(GameActivity.this, MusicServer.class);
        startService(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent = new Intent(GameActivity.this, MusicServer.class);
        stopService(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(mBitmap != null && !mBitmap.isRecycled()){
            mBitmap.recycle();
        }

        dm.recycle();

        if (IPL != null) {
            for (int i = 0; i < IPL.size(); i++) {
                IPL.get(i).recycleBitmap();
            }
            IPL.clear();
            IPL = null;
        }

        if(gameTimer != null){
            gameTimer.recycle();
            gameTimer = null;
        }

        System.gc();
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

        gameTimer.stopTimer();
        Record eTemp = new Record();

        eTemp.setPhoneNum(PuzzleApplication.getmUser().getPhoneNum());
        eTemp.setType(PuzzleApplication.getLevel() + "");
        eTemp.setTime(timeText.getText().toString());
        eTemp.setPic_url(PuzzleApplication.getmUser().getImgUrl());
        eTemp.setNickname(PuzzleApplication.getmUser().getNickName());

        eTemp.save(GameActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                Log.i("main", "数据记录保存成功");
            }

            @Override
            public void onFailure(int i, String s) {
                Log.e("main", "数据记录保存失败" + s);
            }
        });

        showSuccess();

    }

    private void showSuccess() {
        viewContainer.setVisibility(View.GONE);
        View successContainer = ((ViewStub) findViewById(R.id.success_stub)).inflate();
        ImageView finishImage = (ImageView) successContainer.findViewById(R.id.finishImage);
        ImageButton againButton = (ImageButton) successContainer.findViewById(R.id.againButton);

        finishImage.setVisibility(View.VISIBLE);
        againButton.setVisibility(View.VISIBLE);

        againButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DishManagerInitFinishEvent event) {
        gameTimer.startTimer();
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

            // TODO: 2016/4/27 裁剪算法优化基本完成，尚有几像素的偏差，可能是int到float强制转换的精度损失
            //IPL = ImageSplitter.split(mBitmap, PuzzleApplication.getLevel(), DISH_WIDTH, DISH_HEIGHT);
            int dishWidth = DensityUtil.dip2px(PuzzleApplication.getAppContext(), DISH_WIDTH);
            int dishHeight = DensityUtil.dip2px(PuzzleApplication.getAppContext(), DISH_HEIGHT);
            Bitmap tempBitmap = BitmapUtils.createNoRecycleScaleBitmap(
                    mBitmap,
                    dishWidth,
                    dishHeight);
            IPL = ImageSplitter.split(tempBitmap, PuzzleApplication.getLevel(),
                    dishWidth,
                    dishHeight);
            tempBitmap.recycle();
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
                    //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
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

    public static class StaticHandler extends Handler {

        private final WeakReference<Activity> mActivity;

        public StaticHandler(Activity activity){
            mActivity = new WeakReference<Activity>(activity);
        }


        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what) {
                case GameTimer.MESSAGE_TIMER:
                    EventBus.getDefault().post(new TimeEvent());
                    //refreshTimeText();
                    break;

            }
        }
    }
}


