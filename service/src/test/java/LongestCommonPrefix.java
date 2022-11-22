import org.junit.Assert;
import org.junit.Test;

/**
 * @author gangpeng.wgp
 * @date 2021/6/16 10:07 PM
 */
public class LongestCommonPrefix {
    public String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }
        int length = strs[0].length();
        int count = strs.length;
        for (int i = 0; i < length; i++) {
            char c = strs[0].charAt(i);
            for (int j = 1; j < count; j++) {
                if (i == strs[j].length() || strs[j].charAt(i) != c) {
                    return strs[0].substring(0, i);
                }
            }
        }
        return strs[0];
    }


    @Test
    public void runTest() throws Exception {
        String result = null;

        result = longestCommonPrefix(new String[]{"flower", "flow", "flight"});
        Assert.assertTrue(result.equals("fl"));

        result = longestCommonPrefix(new String[]{"dog","racecar","car"});
        Assert.assertTrue(result.equals(""));

    }
}
