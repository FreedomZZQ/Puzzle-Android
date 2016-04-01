package studio.androiddev.puzzle.model;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by lenovo on 2016/3/31.
 */
public class Record extends BmobObject implements Serializable {
    String type;
    String time;

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

    @Override
    public String toString() {
        return "Record{" +
                "type='" + type + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
