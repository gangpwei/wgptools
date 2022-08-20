package easy;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * ���������ֵ
 */

public class E_Max {

    /**
     * ������ֵ������2����ȡƽ��ֵ
     *
     * @return
     */
    public int maximum(int a, int b) {
        long c = a, d = b;
        return (int)((Math.abs(c - d) + c + d) / 2);
    }

    @Test
    public void testCase() throws Exception {
        int result = maximum(2, 3);
        assertTrue(result == 3);

    }

}