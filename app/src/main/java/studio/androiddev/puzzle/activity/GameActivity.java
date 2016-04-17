package studio.androiddev.puzzle.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import studio.androiddev.puzzle.R;
import studio.androiddev.puzzle.bgm.MusicServer;
import studio.androiddev.puzzle.dish.DishManager;
import studio.androiddev.puzzle.dish.DragImageView;
import studio.androiddev.puzzle.event.GameSuccessEvent;
import studio.androiddev.puzzle.event.PieceMoveSuccessEvent;
import studio.androiddev.puzzle.imagesplit.ImagePiece;
import studio.androiddev.puzzle.imagesplit.ImageSplitter;
import studio.androiddev.puzzle.utils.BitmapUtils;
import studio.androiddev.puzzle.utils.GlobalUtils;

public class GameActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.dish)
    ImageView dish;
    @Bind(R.id.layViewContainer)
    LinearLayout layViewContainer;

    private DishManager dm;
    private Bitmap mBitmap;
    private int mLevel = 4;
    private HashMap<Integer, View> pieceList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        mBitmap = BitmapUtils.decodeSampledBitmapFromResources(getResources(), R.drawable.default1,
                300, 300);
        dm = new DishManager(dish, mBitmap, mLevel);
        initialization();
        EventBus.getDefault().register(this);


    }

    @Override
    protected void onStart(){
        super.onStart();

        Intent intent = new Intent(GameActivity.this, MusicServer.class);
        startService(intent);

    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onStop(){
        super.onStop();
        Intent intent = new Intent(GameActivity.this, MusicServer.class);
        stopService(intent);

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PieceMoveSuccessEvent event){
        int index = event.getIndex();
        DragImageView view = (DragImageView) pieceList.get(index);
        if(view != null){
            view.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GameSuccessEvent event){
        // TODO: 2016/4/17 游戏胜利的逻辑
        Toast.makeText(GameActivity.this, "Congratulations!", Toast.LENGTH_SHORT).show();
    }

    private void initialization() {
        layViewContainer.removeAllViews();
        pieceList.clear();

        Display display = getWindowManager().getDefaultDisplay();

        try{

            ImageSplitter IS = new ImageSplitter();
            List<ImagePiece> IPL = IS.split(mBitmap, getResources(), display, mLevel, mLevel);

            for (int i = 0; i < mLevel; i++) {
                for (int j = 0; j < mLevel; j++) {
                    DragImageView imageView = new DragImageView(this);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    imageView.setImageBitmap(IPL.get(j + i * mLevel).bitmap);

                    imageView.setIndex(j + i * mLevel);
                    pieceList.put(imageView.getIndex(), imageView);


                }
            }

            int[] list = GlobalUtils.getRamdomList(mLevel * mLevel);

            if (layViewContainer != null) {
                for (int aList : list) {
                    layViewContainer.addView(pieceList.get(aList),
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            ));
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, GameActivity.class);
        context.startActivity(intent);
    }

}
