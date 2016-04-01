package studio.androiddev.puzzle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import studio.androiddev.puzzle.R;
import studio.androiddev.puzzle.model.User;
import studio.androiddev.puzzle.utils.RegExUtil;
import studio.androiddev.puzzle.utils.SecurityUtils;
import studio.androiddev.puzzle.utils.StaticValue;

public class LoginActivity extends BaseActivity {

    EditText met_phone,met_pwd;
    Button mbtn_login,mbtn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, StaticValue.bmobId);
        setContentView(R.layout.activity_login);
        initView();

        //注册按钮点击事件
        mbtn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //登录按钮点击事件
        mbtn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String phone = met_phone.getText().toString();
                final String pwd = met_pwd.getText().toString();

                if (RegExUtil.confirmPhone(phone)) {
                    if (RegExUtil.confirmPwd(pwd)) {

                        // 登录验证逻辑函数
                        BmobQuery<User> query = new BmobQuery<User>();
                        query.addWhereEqualTo("phoneNum",phone);
                        query.findObjects(LoginActivity.this, new FindListener<User>() {
                            @Override
                            public void onSuccess(List<User> list) {
                                if (list.size() == 1) {
                                    String pwd_MD5_Bmob=list.get(0).getPwd();
                                    String pwd_MD5_Local= SecurityUtils.MD5_secure(pwd);
                                    if (pwd_MD5_Bmob.equals(pwd_MD5_Local)) {
                                        Intent intent=new Intent(LoginActivity.this,UserInfoActivity.class);
                                        intent.putExtra("user",list.get(0));// 向下一Activity传递用户信息类
                                        met_pwd.setText("");
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(LoginActivity.this, "输入密码错误，请重新输入！", Toast.LENGTH_SHORT).show();
                                    }
                                }else if(list.size()==0){
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
            }
        });



    }

    private void initView() {
        met_phone= (EditText) findViewById(R.id.et_phone);
        met_pwd= (EditText) findViewById(R.id.et_pwd);
        mbtn_login= (Button) findViewById(R.id.btn_login);
        mbtn_register= (Button) findViewById(R.id.btn_register);
    }
}
