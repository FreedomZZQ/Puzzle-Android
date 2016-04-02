package studio.androiddev.puzzle.adapter;

import android.widget.BaseAdapter;

/**
 * Created by Administrator on 2016/3/30.
 */
public abstract class DragAdapter extends BaseAdapter {

    public boolean isOrdered(){return  false;};

    public void onReOrderContent(int fromPos, int toPos,boolean swap) {}
}