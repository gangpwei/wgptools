import org.junit.Assert;
import org.junit.Test;

/**
 * @author gangpeng.wgp
 * @date 2021/6/16 10:07 PM
 */
public class Reverse {

    public int reverse(int x) {
        int rev = 0;
        while (x != 0) {
            int pop = x % 10;
            x /= 10;
            if (rev > Integer.MAX_VALUE/10 || (rev == Integer.MAX_VALUE / 10 && pop > 7)) return 0;
            if (rev < Integer.MIN_VALUE/10 || (rev == Integer.MIN_VALUE / 10 && pop < -8)) return 0;
            rev = rev * 10 + pop;
        }
        return rev;
    }

    @Test
    public void runTest() throws Exception {
        int result = 0;
//        result = longestCommonPrefix(123456789);
//        Assert.assertTrue(result == 987654321);

//        result = longestCommonPrefix(-123456789);
//        Assert.assertTrue(result == -987654321);

        result = reverse(1534236469);
        Assert.assertTrue(result == 0);

    }
}
