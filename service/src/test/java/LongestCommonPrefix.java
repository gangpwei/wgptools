import org.junit.Assert;
import org.junit.Test;

/**
 * @author gangpeng.wgp
 * @date 2021/6/16 10:07 PM
 */
public class LongestCommonPrefix {

    public String longestCommonPrefix(String[] strs) {
        String result = "";
        for (int i = 1; i <= strs[0].length() ; i++) {
            String temp = strs[0].substring(0, i);
            int containCount = 1;
            for (int j = 1; j < strs.length; j++) {
                if(!strs[j].startsWith(temp)){
                    break;
                }
                containCount ++;
            }
            if(containCount == strs.length){
                result = temp.length() > result.length() ? temp : result;
            }
        }
        return result;
    }

    @Test
    public void runTest() throws Exception {
        String result = null;
//        result = longestCommonPrefix(123456789);
//        Assert.assertTrue(result == 987654321);

//        result = longestCommonPrefix(-123456789);
//        Assert.assertTrue(result == -987654321);

        result = longestCommonPrefix(new String[]{"flower", "flow", "flight"});
        Assert.assertTrue(result.equals("fl"));

        result = longestCommonPrefix(new String[]{"dog","racecar","car"});
        Assert.assertTrue(result.equals(""));

    }
}
