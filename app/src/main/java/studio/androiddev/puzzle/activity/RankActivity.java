package studio.androiddev.puzzle.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import studio.androiddev.puzzle.R;
import studio.androiddev.puzzle.model.Record;

public class RankActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    ListView mlv_rank;
    List<Record> mRecSet;
    RankAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        mlv_rank= (ListView) findViewById(R.id.lv_rank);

        BmobQuery<Record> query = new BmobQuery<>();
        query.order("-updatedAt");
        query.setLimit(20);
        query.findObjects(this, new FindListener<Record>() {
            @Override
            public void onSuccess(List<Record> list) {
                mRecSet=list;
                adapter=new RankAdapter(mRecSet,RankActivity.this);
                mlv_rank.setAdapter(adapter);
            }

            @Override
            public void onError(int i, String s) {
                Log.e("main",s);
            }
        });

    }

    class RankAdapter extends BaseAdapter{

        ArrayList<Record> mSet;
        Context context;

        public RankAdapter(List<Record> mSett,Context mcontext) {
            mSet= (ArrayList<Record>) mSett;
            context=mcontext;
        }

        @Override
        public int getCount() {
            return mSet.size();
        }

        @Override
        public Record getItem(int position) {
            return mSet.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.layout_lv_rank, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_t= (ImageView) convertView.findViewById(R.id.iv_user_pic);
                viewHolder.tv_nick= (TextView) convertView.findViewById(R.id.tv_nickname_rank);
                viewHolder.tv_level= (TextView) convertView.findViewById(R.id.tv_level);
                viewHolder.tv_gametime= (TextView) convertView.findViewById(R.id.tv_gametime);
                viewHolder.tv_uptime= (TextView) convertView.findViewById(R.id.tv_uptime);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

//            Log.i("main",mSet.toString());

            if (!mSet.get(position).getPic_url().equals("null")) {

                final ViewHolder finalViewHolder = viewHolder;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String imageUrl = mSet.get(position).getPic_url();
                        final Bitmap bitmap = returnBitMap(imageUrl);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finalViewHolder.iv_t.setImageBitmap(bitmap);
                            }
                        });

                    }
                }).start();

            }

            String nicktt;
            if(!mSet.get(position).getNickname().equals("null")){
                nicktt=mSet.get(position).getNickname();
            }else {
                nicktt=mSet.get(position).getPhoneNum();
            }
            viewHolder.tv_nick.setText(nicktt);

            viewHolder.tv_level.setText(mSet.get(position).getType() +" x "+mSet.get(position).getType());

            viewHolder.tv_gametime.setText(mSet.get(position).getTime());

            viewHolder.tv_uptime.setText(mSet.get(position).getUpdatedAt());


            return convertView;
        }


        class ViewHolder {
            ImageView iv_t;
            TextView tv_nick;
            TextView tv_level;
            TextView tv_gametime;
            TextView tv_uptime;
        }

    }

    public Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
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
        Intent intent = new Intent(context, RankActivity.class);
        context.startActivity(intent);
    }



}
