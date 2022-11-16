import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author gangpeng.wgp
 * @date 2021/6/15 10:35 PM
 */
public class LengthOfLongestSubstring {

    /**
     * ��������
     * ��ָ�������ƣ��õ�ǰ�ַ����ϴ��ַ����Ƚϣ�����ظ�����ָ�������ƶ�һλ
     * @param s
     * @return
     */
    public int lengthOfLongestSubstring(String s) {
        if(s == null || s.length() == 0){
            return 0;
        }
        char[] chars = s.toCharArray();
        int max = 1;
        for (int i = 0; i < chars.length; i++) {
            String tempStr = s.substring(i, i+1);
            for (int j = i + 1; j < chars.length; j++) {
                String tempChar = String.valueOf(chars[j]);
                boolean noRepeat = !tempStr.contains(tempChar);
                if(noRepeat){
                    tempStr = s.substring(i, j+1);
                    if(tempStr.length() > max){
                        max = tempStr.length();
                    }
                }else {
                    break;
                }
            }
        }
        return max;
    }

    @Test
    public void runTest() throws Exception {
        int length = lengthOfLongestSubstring("abcabcbb");
        Assert.assertTrue(length == 3);

        length = lengthOfLongestSubstring("bbbbb");
        Assert.assertTrue(length == 1);

        length = lengthOfLongestSubstring("pwwkew");
        Assert.assertTrue(length == 3);

        length = lengthOfLongestSubstring("au");
        Assert.assertTrue(length == 2);
    }

}
