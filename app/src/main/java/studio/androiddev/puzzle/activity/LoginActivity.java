package studio.androiddev.puzzle.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import studio.androiddev.puzzle.PuzzleApplication;
import studio.androiddev.puzzle.R;
import studio.androiddev.puzzle.model.User;
import studio.androiddev.puzzle.utils.RegExUtil;
import studio.androiddev.puzzle.utils.SecurityUtils;
import studio.androiddev.puzzle.utils.StaticValue;

public class LoginActivity extends BaseActivity {
    @Bind(R.id.et_phone)
    EditText met_phone;
    @Bind(R.id.et_pwd)
    EditText met_pwd;
    @Bind(R.id.btn_register)
    Button mbtn_register;
    @Bind(R.id.btn_login)
    Button mbtn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, StaticValue.bmobId);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.btn_register, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                //注册按钮点击事件
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                //登录按钮点击事件
                final String phone = met_phone.getText().toString();
                final String pwd = met_pwd.getText().toString();

                if (RegExUtil.confirmPhone(phone)) {
                    if (RegExUtil.confirmPwd(pwd)) {

                        // 登录验证逻辑函数
                        BmobQuery<User> query = new BmobQuery<User>();
                        query.addWhereEqualTo("phoneNum", phone);
                        query.findObjects(LoginActivity.this, new FindListener<User>() {
                            @Override
                            public void onSuccess(List<User> list) {
                                if (list.size() == 1) {
                                    String pwd_MD5_Bmob = list.get(0).getPwd();
                                    String pwd_MD5_Local = SecurityUtils.MD5_secure(pwd);
                                    if (pwd_MD5_Bmob.equals(pwd_MD5_Local)) {
                                        //登陆成功后要做三件事：
                                        //1.更新Application中的User
                                        //2.启动MainActivity
                                        //3.finish掉LoginActivity
                                        PuzzleApplication.setmUser(list.get(0));
                                        MainActivity.actionStart(LoginActivity.this);
                                        LoginActivity.this.finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "输入密码错误，请重新输入！", Toast.LENGTH_SHORT).show();
                                    }
                                } else if (list.size() == 0) {
                                    Toast.makeText(LoginActivity.this, "不存在该账号，请注册！", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(int i, String s) {
                                Log.e("main", s);
                            }
                        });

                    } else {
                        met_pwd.setError("密码格式错误,请重新输入！");
                    }

                } else {
                    met_phone.setError("手机号格式错误，请重新输入！");
                }
                break;
        }
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
