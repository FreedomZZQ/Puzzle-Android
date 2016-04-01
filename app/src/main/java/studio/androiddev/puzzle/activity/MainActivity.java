package studio.androiddev.puzzle.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.beginButton, R.id.rankButton, R.id.settingButton, R.id.exitButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.beginButton:
                break;
            case R.id.rankButton:
                break;
            case R.id.settingButton:
                break;
            case R.id.exitButton:
                ActivityManager.finishAll();
                break;
        }
    }
}
