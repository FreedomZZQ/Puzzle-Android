package studio.androiddev.puzzle.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import studio.androiddev.puzzle.PuzzleApplication;
import studio.androiddev.puzzle.R;
import studio.androiddev.puzzle.model.Record;
import studio.androiddev.puzzle.model.User;

public class UserInfoActivity extends BaseActivity {

    private static int CAMERA_REQUEST_CODE = 1;
    private static int GALLERY_REQUEST_CODE = 2;
    private static int CROP_REQUEST_CODE = 3;
    @Bind(R.id.fresco_logo)
    ImageView imageView;
    @Bind(R.id.et_nickname)
    EditText met_nickname;
    @Bind(R.id.met_phone_userinfo)
    EditText met_phone;
    @Bind(R.id.et_mail)
    EditText met_mail;
    @Bind(R.id.et_rank)
    EditText met_rank;
    @Bind(R.id.lv_record)
    ListView lv_record;
    @Bind(R.id.btn_changePwd)
    Button mbtn_changePwd;
    @Bind(R.id.btn_exitAccount)
    Button mbtn_exit;
    @Bind(R.id.toolbar)
    Toolbar toolbar;


    private User user_login; //登陆者账号信息
    View inflate;//解析AlertDialog内部布局的view
    AlertDialog alertDialog; //更换头像时弹出的对话框
    List<Record> mRecordSet;
    RecordAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        initView();//实例化控件
        user_login = PuzzleApplication.getmUser();
        setInfo();//为各个控件填充信息
        mRecordSet = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            Record temp = new Record();
            String type_t = i + " x " + i;
            temp.setType(type_t);
            temp.setTime("1：30");
            mRecordSet.add(temp);
        }
        mAdapter = new RecordAdapter(mRecordSet, UserInfoActivity.this);
        lv_record.setAdapter(mAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);
        inflate = UserInfoActivity.this.getLayoutInflater().inflate(R.layout.layout_alertdiag, null, false);
        builder.setView(inflate);
        builder.setTitle("自定义头像");
        alertDialog = builder.create();

        findViewById(R.id.fresco_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });

        inflate.findViewById(R.id.btn_camera_changePic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                setPicFromCamera();
            }
        });

        inflate.findViewById(R.id.btn_gallery_changePic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                setPicFromGallery();
            }
        });


    }


    private void setPicFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private void setPicFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    private void setInfo() {
        met_phone.setText(user_login.getPhoneNum());
        met_nickname.setText(user_login.getNickName());
        met_mail.setText(user_login.getMailNum());
        if (!user_login.getImgUrl().equals("null")) {


            new Thread(new Runnable() {
                @Override
                public void run() {
                    String imageUrl = user_login.getImgUrl();
                    final Bitmap bitmap = returnBitMap(imageUrl);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    });

                }
            }).start();
        }
    }

    //调用裁剪图片API，传入FileUri
    private void startImageZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 800);
        intent.putExtra("outputY", 800);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    //将 content类型uri 转换成 file类型uri
    private Uri convertUri(Uri uri) {
        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            return saveBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 将 bitmap 图片存到手机内存 并返回 file类型Uri
     */
    private Uri saveBitmap(Bitmap bm) {
        File tempDir = new File(Environment.getExternalStorageDirectory() + "/com.haze.pingtugame");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }

        File image = new File(tempDir.getAbsolutePath() + "/logo_temp.png");

        try {
            FileOutputStream fos = new FileOutputStream(image);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fos);
            fos.flush();
            fos.close();
            return Uri.fromFile(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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


    private void initView() {
        met_mail.setEnabled(false);
        met_rank.setEnabled(false);
        met_phone.setEnabled(false);
        met_nickname.setEnabled(false);
        met_rank.setText("第一名");
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (data == null) {
                return;
            } else {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap bm = extras.getParcelable("data");
                    Uri uri = null;
                    uri = saveBitmap(bm);
                    startImageZoom(uri);// uri必须是file类型的Uri
                }
            }
        } else if (requestCode == GALLERY_REQUEST_CODE) {
            if (data == null) {
                return;
            }
            Uri uri = data.getData();//content类型的uri
            startImageZoom(convertUri(uri));

        } else if (requestCode == CROP_REQUEST_CODE) {
            if (data == null) {
                return;
            }
            Bundle extra = data.getExtras();
            Bitmap bm = extra.getParcelable("data");
            imageView.setImageBitmap(bm);

            /**
             * 将图片存到服务器
             */
            File tempDir = new File(Environment.getExternalStorageDirectory() + "/com.haze.pingtugame");
            if (!tempDir.exists()) {
                tempDir.mkdir();
            }
            File image = new File(tempDir.getAbsolutePath() + "/logo_temp.png");
            if (image.exists()) {
                image.delete();
            }
            saveBitmap(bm);

            String picPath = tempDir.getAbsolutePath() + "/logo_temp.png";
            final BmobFile bmobFile = new BmobFile(new File(picPath));
            bmobFile.uploadblock(UserInfoActivity.this, new UploadFileListener() {

                @Override
                public void onSuccess() {
//                    Log.i("main", bmobFile.getFileUrl(UserInfoActivity.this));

                    user_login.setImgUrl(bmobFile.getFileUrl(UserInfoActivity.this));
                    String obId = user_login.getObjectId();
                    user_login.update(UserInfoActivity.this, obId, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Log.i("main", "更新头像URL成功");
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Log.e("main", s);

                        }
                    });

                }

                @Override
                public void onProgress(Integer value) {
                    // 返回的上传进度（百分比）
                }

                @Override
                public void onFailure(int code, String msg) {
                    Log.e("main", "存储头像失败");
                }
            });

        } else if (requestCode == 1234) {
            if (data == null) {

            } else {
                User mReUser = (User) data.getSerializableExtra("reuser");
                user_login = mReUser;
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuItem_edit) {
            CharSequence title = item.getTitle();
            if (item.getTitle().equals("编辑")) {
                item.setTitle("保存");
                met_nickname.setEnabled(true);
                met_mail.setEnabled(true);

            } else {
                item.setTitle("编辑");
                met_nickname.setEnabled(false);
                met_mail.setEnabled(false);
                if (!(user_login.getMailNum().equals(met_mail.getText().toString()) && user_login.getNickName().equals(met_nickname.getText().toString()))) {
                    user_login.setNickName(met_nickname.getText().toString());
                    user_login.setMailNum(met_mail.getText().toString());
                    user_login.update(UserInfoActivity.this, user_login.getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure(int i, String s) {
                        }
                    });
                }
            }
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.btn_changePwd, R.id.btn_exitAccount})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_changePwd:
                ChangePwdActivity.actionStart(UserInfoActivity.this, user_login);
                break;
            case R.id.btn_exitAccount:
                LoginActivity.actionStart(UserInfoActivity.this);
                finish();
                break;
        }
    }


    class RecordAdapter extends BaseAdapter {

        List<Record> mSet;
        Context mContext;

        public RecordAdapter(List<Record> mSet, Context mContext) {
            this.mSet = mSet;
            this.mContext = mContext;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.layout_record, null);
                viewHolder = new ViewHolder();
                viewHolder.mtv_type = (TextView) convertView.findViewById(R.id.tv_lv_record_type);
                viewHolder.mtv_time = (TextView) convertView.findViewById(R.id.tv_lv_record_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.mtv_time.setText(getItem(position).getTime());
            viewHolder.mtv_type.setText(getItem(position).getType());
            if (position % 2 == 0) {
                convertView.findViewById(R.id.linearlayout_record).setBackgroundColor(Color.GRAY);
            }
            return convertView;
        }


        class ViewHolder {
            TextView mtv_type;
            TextView mtv_time;
        }
    }


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        context.startActivity(intent);
    }
}
