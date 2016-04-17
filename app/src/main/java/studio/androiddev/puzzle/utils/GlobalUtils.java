package studio.androiddev.puzzle.utils;

import java.util.Random;

/**
 * Puzzle
 * Created by ZQ on 2016/3/24.
 */
public class GlobalUtils {

    /**
     * 对接收到的json字符串进行处理，去掉可能的垃圾头和垃圾尾，使其符合json格式
     * @param jsonString 接收到的json字符串
     * @return 处理后的json字符串
     */
    public static String FixJsonString(String jsonString){

        int startIndex = jsonString.indexOf('{');
        int endIndex = jsonString.lastIndexOf('}');

        //LogUtils.d("hehe", jsonString.substring(startIndex, endIndex + 1));
        return jsonString.substring(startIndex, endIndex + 1);
    }

    /**
     *
     * @param total
     * @return
     */
    public static int[] getRamdomList(int total){
        int[] result = new int[total];
        for(int i = 0; i < total; i++){
            result[i] = i;
        }

        Random random = new Random();
        for(int i = 0; i < total; i++){
            int p = random.nextInt(total);
            int temp = result[i];
            result[i] = result[p];
            result[p] = temp;
        }

        random = null;
        return result;
    }

}
