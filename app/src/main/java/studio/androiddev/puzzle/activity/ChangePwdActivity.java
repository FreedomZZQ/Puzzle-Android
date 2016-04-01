package studio.androiddev.puzzle.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.bmob.v3.listener.UpdateListener;
import studio.androiddev.puzzle.R;
import studio.androiddev.puzzle.model.User;
import studio.androiddev.puzzle.utils.SecurityUtils;
import studio.androiddev.puzzle.utils.StaticValue;

public class ChangePwdActivity extends BaseActivity {

    EditText met_oldPwd,met_newPwd,met_conformPwd;
    Button mbtn_confirmChangePwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        initView();
        final User mUser= (User) getIntent().getSerializableExtra("key");

        mbtn_confirmChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPwd=SecurityUtils.MD5_secure(met_oldPwd.getText().toString());
                String newPwd=met_newPwd.getText().toString();
                String newConPwd=met_conformPwd.getText().toString();
                if(mUser.getPwd().equals(oldPwd)){
                    if(newPwd.equals(newConPwd)){
                        String s = SecurityUtils.MD5_secure(newPwd);
                        mUser.setPwd(s);
                        mUser.update(ChangePwdActivity.this, mUser.getObjectId(),new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(ChangePwdActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent();
                                intent.putExtra("reuser",mUser);
                                setResult(333,intent);
                                finish();

                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Log.e("main",s);
                            }
                        });
                    }else{
                        met_conformPwd.setError("密码输入不一致");
                    }
                }else{
                    Toast.makeText(ChangePwdActivity.this, "原密码输入错误，请重新输入！", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }



    private void initView() {
        mbtn_confirmChangePwd= (Button) findViewById(R.id.btn_changePwd_confirm);
        met_oldPwd= (EditText) findViewById(R.id.et_pwd_old);
        met_newPwd= (EditText) findViewById(R.id.et_pwd_new);
        met_conformPwd= (EditText) findViewById(R.id.et_confirm_new_pwd);
    }
}
