package studio.androiddev.puzzle.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.listener.UpdateListener;
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

//    EditText met_oldPwd,met_newPwd,met_conformPwd;
//    Button mbtn_confirmChangePwd;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        ButterKnife.bind(this);
//        initView();
        mUser = (User) getIntent().getSerializableExtra("key");


//        mbtn_confirmChangePwd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String oldPwd = SecurityUtils.MD5_secure(met_oldPwd.getText().toString());
//                String newPwd = met_newPwd.getText().toString();
//                String newConPwd = met_confirmPwd.getText().toString();
//                if (mUser.getPwd().equals(oldPwd)) {
//                    if (newPwd.equals(newConPwd)) {
//                        String s = SecurityUtils.MD5_secure(newPwd);
//                        mUser.setPwd(s);
//                        mUser.update(ChangePwdActivity.this, mUser.getObjectId(), new UpdateListener() {
//                            @Override
//                            public void onSuccess() {
//                                Toast.makeText(ChangePwdActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent();
//                                intent.putExtra("reuser", mUser);
//                                setResult(333, intent);
//                                finish();
//
//                            }
//
//                            @Override
//                            public void onFailure(int i, String s) {
//                                Log.e("main", s);
//                            }
//                        });
//                    } else {
//                        met_confirmPwd.setError("密码输入不一致");
//                    }
//                } else {
//                    Toast.makeText(ChangePwdActivity.this, "原密码输入错误，请重新输入！", Toast.LENGTH_SHORT).show();
//                }
//
//
//            }
//        });
    }


//    private void initView() {
//        mbtn_confirmChangePwd = (Button) findViewById(R.id.btn_changePwd_confirm);
//        met_oldPwd = (EditText) findViewById(R.id.et_pwd_old);
//        met_newPwd = (EditText) findViewById(R.id.et_pwd_new);
//        met_conformPwd = (EditText) findViewById(R.id.et_confirm_new_pwd);
//    }

    @OnClick(R.id.btn_changePwd_confirm)
    public void onClick() {
        String oldPwd = SecurityUtils.MD5_secure(met_oldPwd.getText().toString());
        String newPwd = met_newPwd.getText().toString();
        String newConPwd = met_confirmPwd.getText().toString();
        if (mUser.getPwd().equals(oldPwd)) {
            if (newPwd.equals(newConPwd)) {
                String s = SecurityUtils.MD5_secure(newPwd);
                mUser.setPwd(s);
                mUser.update(ChangePwdActivity.this, mUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(ChangePwdActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("reuser", mUser);
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

    public static void actionStart(Activity context, User loginUser){
        Intent intent = new Intent(context, ChangePwdActivity.class);
        intent.putExtra("key", loginUser);
        context.startActivityForResult(intent, 1234);
    }
}
