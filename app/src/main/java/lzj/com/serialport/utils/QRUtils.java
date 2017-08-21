package lzj.com.serialport.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yunniu on 2017/6/16.
 */

public class QRUtils {

//    /**
//     * 移除扫描到的一些不需要的值
//     * @param collect
//     * @param content
//     * @return
//     */
//    public static int remove(StringBuilder collect,String content){
//        int index=collect.indexOf(content);
//        if (index!=-1){
//            collect.delete(index,index+content.length());
//        }
//
//        return 0;
//    }

    /**
     * 去掉collect里面的无用数据
     * @param collect
     */
    public static void remove(StringBuilder collect,String regex){
        Pattern pattern=Pattern.compile(regex);
        Matcher m = pattern.matcher(collect.toString());
        if (m.find()){
           String str= m.replaceAll("");
            collect.delete(0,collect.length());
            collect.append(str);
        }
    }

    public static String getQRStr(String string){
        Pattern pattern=Pattern.compile("[0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz=]");
        Matcher m = pattern.matcher(string);
        StringBuilder result=new StringBuilder();
        while (m.find()){
            result.append(m.group());
        }
        return result.toString();
    }


//    public static boolean removerepet(String string, String he) {
//        Pattern pattern=Pattern.compile(he);
//        Matcher match = pattern.matcher(string);
//
//        return false;
//    }
}
