package studio.androiddev.puzzle.activity;

//<<<<<<< HEAD
import android.os.Bundle;
import android.view.View;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import studio.androiddev.puzzle.R;

public class MainActivity extends BaseActivity {

    @Bind(R.id.beginButton)
    Button beginButton;
    @Bind(R.id.rankButton)
    Button rankButton;
    @Bind(R.id.settingButton)
    Button settingButton;
    @Bind(R.id.exitButton)
    Button exitButton;

    //用于记录两次按下返回键的间隔
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.beginButton, R.id.rankButton, R.id.settingButton, R.id.exitButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.beginButton:
                GameActivity.actionStart(MainActivity.this);
                break;
            case R.id.rankButton:
                RankActivity.actionStart(MainActivity.this);
                break;
            case R.id.settingButton:
                SettingActivity.actionStart(MainActivity.this);
                break;
            case R.id.exitButton:
                ActivityManager.finishAll();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            //在两秒内两次按下返回键退出程序
            if((System.currentTimeMillis() - exitTime) > 2000){
                Toast.makeText(MainActivity.this, getString(R.string.click_again_to_exit), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }else{
                ActivityManager.finishAll();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}
