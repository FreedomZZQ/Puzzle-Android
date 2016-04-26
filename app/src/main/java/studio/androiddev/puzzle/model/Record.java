package studio.androiddev.puzzle.model;

import java.io.Serializable;
import cn.bmob.v3.BmobObject;

/**
 * Created by lenovo on 2016/3/31.
 */

public class Record extends BmobObject implements Serializable {
    String type;
    String time;
    String phoneNum;
    String update_time;
    String pic_url;
    String nickname;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "Record{" +
                "type='" + type + '\'' +
                ", time='" + time + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", update_time='" + update_time + '\'' +
                ", pic_url='" + pic_url + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
