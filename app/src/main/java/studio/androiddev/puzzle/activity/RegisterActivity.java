package studio.androiddev.puzzle.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Date;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.SaveListener;
import studio.androiddev.puzzle.R;
import studio.androiddev.puzzle.model.User;
import studio.androiddev.puzzle.utils.RegExUtil;
import studio.androiddev.puzzle.utils.SecurityUtils;

public class RegisterActivity extends BaseActivity {

    Button mbtn_reg;
    EditText met_phone;
    EditText met_pwd;
    EditText met_pwd_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();

        mbtn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(RegExUtil.confirmPhone(met_phone.getText().toString())){
                    if(RegExUtil.confirmPwd(met_pwd.getText().toString())){
                        if(met_pwd.getText().toString().equals(met_pwd_confirm.getText().toString())){

                            User temp=new User();
                            temp.setPhoneNum(met_phone.getText().toString());
                            String pwd_MD5= SecurityUtils.MD5_secure(met_pwd.getText().toString());
                            temp.setPwd(pwd_MD5);
                            temp.setCreateTime(new BmobDate(new Date()));
                            temp.setImgUrl("null");
                            temp.setMailNum("待添加");
                            temp.setNickName("待添加");
                            regPushToBmob(temp);
                            regSaveInLocal(temp);

                        }else{
                            met_pwd_confirm.setError("两次密码输入不一致！");
                        }

                    }else {
                        met_pwd.setError("密码至少为8位数字或字符");
                    }

                }else{
                    met_phone.setError("手机号码格式错误");
                }


            }
        });

    }

    private void regSaveInLocal(User user) {

    }

    private void regPushToBmob(User user) {

        user.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(RegisterActivity.this,"数据存储失败,请检查网络",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initView() {
        mbtn_reg= (Button) findViewById(R.id.btn_register_res);
        met_phone= (EditText) findViewById(R.id.et_phone_reg);
        met_pwd= (EditText) findViewById(R.id.et_pwd_register);
        met_pwd_confirm= (EditText) findViewById(R.id.et_pwd_confirm_register);
    }
}
