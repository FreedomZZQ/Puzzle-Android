package studio.androiddev.puzzle.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.FileNotFoundException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import studio.androiddev.puzzle.R;
import studio.androiddev.puzzle.dish.DishManager;
import studio.androiddev.puzzle.dish.DragImageView;
import studio.androiddev.puzzle.imagesplit.ImagePiece;
import studio.androiddev.puzzle.imagesplit.ImageSplitter;
import studio.androiddev.puzzle.utils.BitmapUtils;

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

    }

    private void initialization() {
        layViewContainer.removeAllViews();

        Display display = getWindowManager().getDefaultDisplay();

        try{

            ImageSplitter IS = new ImageSplitter();
            List<ImagePiece> IPL = IS.split(mBitmap, getResources(), display, mLevel, mLevel);

            for (int i = 0; i < mLevel; i++)
                for (int j = 0; j < mLevel; j++) {
                    DragImageView imageView = new DragImageView(this);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    imageView.setImageBitmap(IPL.get(j + i * mLevel).bitmap);

                    imageView.setIndex(j + i * mLevel);

                    if (layViewContainer != null) {
                        layViewContainer.addView(imageView,
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
