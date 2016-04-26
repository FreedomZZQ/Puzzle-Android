package studio.androiddev.puzzle.model;

import java.io.Serializable;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by lenovo on 2016/3/22.
 */
public class User extends BmobObject implements Serializable{

//    Integer user_id;
    String phoneNum;
    String pwd;
    String nickName;
    BmobDate createTime;
    String imgUrl;
    String mailNum;

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public BmobDate getCreateTime() {
        return createTime;
    }

    public void setCreateTime(BmobDate createTime) {
        this.createTime = createTime;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getMailNum() {
        return mailNum;
    }

    public void setMailNum(String mailNum) {
        this.mailNum = mailNum;
    }

    @Override
    public String toString() {
        return "User{" +
                "phoneNum='" + phoneNum + '\'' +
                ", pwd='" + pwd + '\'' +
                ", nickName='" + nickName + '\'' +
                ", createTime=" + createTime +
                ", imgUrl='" + imgUrl + '\'' +
                ", mailNum='" + mailNum + '\'' +
                '}';
    }
}
