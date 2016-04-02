package studio.androiddev.puzzle.utils;

import java.util.regex.Pattern;

/**
 * Created by lenovo on 2016/3/22.
 */
public class RegExUtil {

    //手机号输入验证
    public static Boolean confirmPhone(String phone){

        boolean matches = Pattern.matches("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$", phone);
        return matches;
    }

    //密码格式要求：长度为8-20的数字，下划线或字母
    public static Boolean confirmPwd(String pwd){
        //密码长度为8-20的数字，下划线或字母
        boolean matches = Pattern.matches("[a-zA-Z_0-9]{8,20}", pwd);
        return matches;
    }

}