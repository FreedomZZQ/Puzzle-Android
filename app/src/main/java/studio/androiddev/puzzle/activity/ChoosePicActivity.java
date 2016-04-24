package studio.androiddev.puzzle.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import butterknife.Bind;
import butterknife.ButterKnife;
import studio.androiddev.puzzle.R;
import studio.androiddev.puzzle.adapter.ChoosePicGridViewAdapter;

public class ChoosePicActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.picContainer)
    GridView picContainer;

    public static final int [] icons={
            R.drawable.default1,
            R.drawable.default2,
            R.drawable.default3,
            R.drawable.default4
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_pic);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        initView();

    }

    private void initView() {

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

//        ArrayList<HashMap<String, Object>> homeModules = new ArrayList<>();
//        for (int icon : icons) {
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("ItemImage", icon);
//            homeModules.add(map);
//        }
//
//        SimpleAdapter gridviewAdapter = new SimpleAdapter(ChoosePicActivity.this, homeModules,
//                R.layout.choose_pic_gridview_item,
//                new String []{"ItemImage"},new int[]{R.id.imageItem});

        picContainer.setAdapter(new ChoosePicGridViewAdapter(ChoosePicActivity.this, R.layout.choose_pic_gridview_item));
        picContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                GameActivity.actionStart(ChoosePicActivity.this, position);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ChoosePicActivity.class);
        context.startActivity(intent);
    }

}
