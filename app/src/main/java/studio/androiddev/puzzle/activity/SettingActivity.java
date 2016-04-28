package studio.androiddev.puzzle.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import studio.androiddev.puzzle.PuzzleApplication;
import studio.androiddev.puzzle.R;
import studio.androiddev.puzzle.utils.StaticValue;

public class SettingActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.levelChooseButton)
    Button levelChooseButton;
    @Bind(R.id.buttonShare)
    ImageButton buttonShare;

    private int choosedLevel = 0;

    final int[] levels = {
            R.string.setting_level_3,
            R.string.setting_level_4,
            R.string.setting_level_5,
            R.string.setting_level_6};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        levelChooseButton.setText(getString(levels[PuzzleApplication.getLevel() - 3]));

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
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    private void chooseLevel() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);

        final String[] levels = {
                getString(R.string.setting_level_3),
                getString(R.string.setting_level_4),
                getString(R.string.setting_level_5),
                getString(R.string.setting_level_6)};

        builder.setTitle("请选择难度");
        int defaultWhich = 0;

        for (int i = 0; i < levels.length; i++) {
            if (levelChooseButton.getText().toString().equals(levels[i])) {
                defaultWhich = i;
            }
        }

        builder.setSingleChoiceItems(levels, defaultWhich,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choosedLevel = which;
                    }
                });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                levelChooseButton.setText(levels[choosedLevel]);
                changeLevel();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void changeLevel() {

        int level = choosedLevel + 3;
        SharedPreferences.Editor editor = getSharedPreferences(StaticValue.SP_NAME, MODE_PRIVATE).edit();
        editor.putInt(StaticValue.SP_LEVEL, level);
        editor.apply();
        PuzzleApplication.setLevel(level);
    }

    private void shareAction(){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.setting_share));
        shareIntent.setType("text/plain");

        //设置分享列表的标题，并且每次都显示分享列表
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    @OnClick({R.id.levelChooseButton, R.id.buttonShare})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.levelChooseButton:
                chooseLevel();
                break;
            case R.id.buttonShare:
                shareAction();
                break;
        }
    }


}
