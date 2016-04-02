package studio.androiddev.puzzle.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import studio.androiddev.puzzle.R;
import studio.androiddev.puzzle.adapter.DragAdapter;
import studio.androiddev.puzzle.intfc.onMoveListener;
import studio.androiddev.puzzle.ui.DragGridView;

public class GameActivity extends BaseActivity {

    private DragGridView dragSortGridView;
    private ColorAdapter colorAdapter;

    class TestData {
        int color;
        int text;
        public  TestData(){
            this.color=0;
            this.text=0;
        }
        public  TestData(int color,int text)
        {
            this.color=color;
            this.text=text;
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ///////////////////////////////////////////////////////////////////
        List<TestData> list = new ArrayList<TestData>();

        for (int i = 0; i < 16; i++) {
            TestData data = new TestData();
            Random random = new Random();
            data.color = Color.rgb(random.nextInt(0xff), random.nextInt(0xff),
                    random.nextInt(0xff));
            data.text = i;
            list.add(data);
        }
        for(int i=0;i<16;i++)
        {
            Random random=new Random();
            int temp=random.nextInt(16);
            TestData data=list.remove(temp);
            //TestData data1=new TestData(data.color,data.text);
            list.add(15,data);
        }
        colorAdapter = new ColorAdapter();
        colorAdapter.setList(list);

        dragSortGridView = (DragGridView) findViewById(R.id.dragSortGridView1);
        dragSortGridView.setAdapter(colorAdapter);
        colorAdapter.setmOnMoveListener(dragSortGridView);
        dragSortGridView.setAppearingDrawable(R.drawable.appearing);
        dragSortGridView.setDisappearingDrawable(R.drawable.disappearing);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    class ColorAdapter extends DragAdapter {

        private List<TestData> list;
        private onMoveListener mOnMoveListener;

        public void setmOnMoveListener(onMoveListener listener) {
            this.mOnMoveListener = listener;
        }

        public void setList(List<TestData> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                TextView textView = new TextView(parent.getContext());
                textView.setGravity(Gravity.CENTER);
                textView.setMinHeight(200);
                convertView = textView;
            }
            TextView textView = (TextView) convertView;
            TestData data = (TestData) getItem(position);
            textView.setBackgroundColor(data.color);
            textView.setText("" + data.text);
            return convertView;
        }

        @Override
        public void onReOrderContent(int fromPos, int toPos, boolean swap) {
            if (swap == false) {
                TestData data = list.remove(fromPos);
                /*if (fromPos > toPos) {
                    for (int i = fromPos; i > toPos; i--) {
                        if (mOnMoveListener != null) {
                            mOnMoveListener.onMove(i-1, i);
                        }
                        list.set(i, list.get(i - 1));
                    }
                }
                else{
                    for (int i = fromPos; i < toPos; i++) {
                        if (mOnMoveListener != null) {
                            mOnMoveListener.onMove(i+1, i);
                        }
                        list.set(i, list.get(i + 1));
                    }
                }*/
                list.add(toPos, data);

            } else {
                TestData data1 = list.get(fromPos);
                TestData data2 = list.get(toPos);
                list.set(toPos, data1);
                list.set(fromPos, data2);
            }
            notifyDataSetChanged();
        }

        @Override
        public boolean isOrdered() {
            for (int i = 0; i < getCount(); i++) {
                if (list.get(i).text != i) return false;
            }
            return true;
        }
    }
        //////////////////////////////////////////////////////////////////////


    public static void actionStart(Context context){
        Intent intent = new Intent(context, GameActivity.class);
        context.startActivity(intent);
    }

}
