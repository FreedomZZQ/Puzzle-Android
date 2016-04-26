package studio.androiddev.puzzle.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.listener.UpdateListener;
import studio.androiddev.puzzle.PuzzleApplication;
import studio.androiddev.puzzle.R;
import studio.androiddev.puzzle.model.User;
import studio.androiddev.puzzle.utils.SecurityUtils;

public class ChangePwdActivity extends BaseActivity {
    @Bind(R.id.et_pwd_old)
    EditText met_oldPwd;
    @Bind(R.id.et_pwd_new)
    EditText met_newPwd;
    @Bind(R.id.et_confirm_new_pwd)
    EditText met_confirmPwd;
    @Bind(R.id.btn_changePwd_confirm)
    Button mbtn_confirmChangePwd;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_changePwd_confirm)
    public void onClick() {
        String oldPwd = SecurityUtils.MD5_secure(met_oldPwd.getText().toString());
        String newPwd = met_newPwd.getText().toString();
        String newConPwd = met_confirmPwd.getText().toString();
        User mUser = PuzzleApplication.getmUser();
        if (mUser.getPwd().equals(oldPwd)) {
            if (newPwd.equals(newConPwd)) {
                String s = SecurityUtils.MD5_secure(newPwd);
                mUser.setPwd(s);
                mUser.update(ChangePwdActivity.this, mUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(ChangePwdActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(333, intent);
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.e("main", s);
                    }
                });
            } else {
                met_confirmPwd.setError("密码输入不一致");
            }
        } else {
            Toast.makeText(ChangePwdActivity.this, "原密码输入错误，请重新输入！", Toast.LENGTH_SHORT).show();
        }
    }

    public static void actionStart(Activity context) {
        Intent intent = new Intent(context, ChangePwdActivity.class);
        context.startActivityForResult(intent, 1234);
    }
}
