package studio.androiddev.puzzle.dish;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * puzzle
 * Created by ZQ on 2016/4/4.
 */
public class DragImageView extends ImageView {

    private int mIndex;

    public DragImageView(Context context) {
        super(context);
        init(context);
    }

    public DragImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.startDrag(null, new View.DragShadowBuilder(v), mIndex, 0);
                return true;
            }
        });
    }

    public void setIndex(int index) {
        mIndex = index;
    }
}
