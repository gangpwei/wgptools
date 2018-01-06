package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author weigangpeng
 * @date 2017/11/29 ����9:49
 */

public class StringUtil {

    /**
     * �ж��ַ����Ƿ�Ϊ�ա����ַ���Ϊnull���߳���Ϊ0��ʱ��Ϊtrue������Ϊfalse
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
     * ��ȡָ���ַ������ֵĴ���
     *
     * @param srcText Դ�ַ���
     * @param findText Ҫ���ҵ��ַ���
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
}
