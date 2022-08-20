package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.helpers.MessageFormatter;

/**
 * @author weigangpeng
 * @date 2017/11/29 上午9:49
 */

public class StringUtil {

    /**
     * 判断字符串是否为空。当字符串为null或者长度为0的时候为true，否则为false
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }

        return false;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 获取指定字符串出现的次数
     *
     * @param srcText 源字符串
     * @param findText 要查找的字符串
     * @return
     */
    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        int index = 0;
        while ((index = srcText.indexOf(findText, index)) != -1) {
            index = index + findText.length();
            count++;
        }
        return count;
    }

    public static String getTab(int tabCount) {
        String result = "";
        for (int i = 0; i < tabCount; i++) {
            result += "   ";
        }
        return result;
    }

    public static String formatLen(String str, int length){
        if(str.length() < length){
            return String.format("%1$-" + length + "s", str);
        }
        return str;
    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public static final char UNDERLINE='_';
    public static String camelToUnderline(String param){
        if (param==null||"".equals(param.trim())){
            return "";
        }
        int len=param.length();
        StringBuilder sb=new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c=param.charAt(i);
            if (Character.isUpperCase(c) && i != 0){
                sb.append(UNDERLINE);
                sb.append(Character.toUpperCase(c));
            }else{
                sb.append(Character.toUpperCase(c));
            }
        }
        return sb.toString();
    }
    public static String underlineToCamel(String param){
        if (param==null||"".equals(param.trim())){
            return "";
        }
        int len=param.length();
        StringBuilder sb=new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c=param.charAt(i);
            if (c==UNDERLINE){
                if (++i<len){
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            }else{
                sb.append(c);
            }
        }
        return sb.toString();
    }
    public static String underlineToCamel2(String param){
        if (param==null||"".equals(param.trim())){
            return "";
        }
        StringBuilder sb=new StringBuilder(param);
        Matcher mc= Pattern.compile("_").matcher(param);
        int i=0;
        while (mc.find()){
            int position=mc.end()-(i++);
            //String.valueOf(Character.toUpperCase(sb.charAt(position)));
            sb.replace(position-1,position+1,sb.substring(position,position+1).toUpperCase());
        }
        return sb.toString();
    }



    /**
     * 格式化字符串
     * @param format
     * @param arguments
     * @return
     */
    public static String format(String format, Object... arguments){
        return MessageFormatter.arrayFormat(format, arguments).getMessage();
    }
}
