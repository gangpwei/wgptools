package easy;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * �ҵ�������������ʧ������
 *
 */

public class E_isUnique {

    /**
     * ���ֹ������±���Ϊ1
     * @param astr
     * @return
     */
    public boolean isUnique(String astr) {
        int[] tempArray = new int[26];
        char[] chars = astr.toCharArray();
        for(int i =0; i<chars.length; i++){
            char c = chars[i];
            int x = c - 'a';
            if(tempArray[x] >= 1){
                return false;
            }
            tempArray[x] ++;
        }
        return true;
    }

    /**
     * λ�Ʒ�
     * @param astr
     * @return
     */
    public boolean isUnique2(String astr) {
        int mark = 0;
        char[] chars = astr.toCharArray();
        for(char c:chars){
            int moveBite = c - 'a';
            //����Ӧλ��ֵ�Ƿ�Ϊ1
            if((mark & 1 << moveBite) != 0){
                return false;
            }else{
                //��Ӧλ��Ϊ1
                mark = mark | (1 << moveBite);
            }
        }
        return true;
    }
    

    @Test
    public void testCase() throws Exception {
        String str = "leetcode";
        boolean result = isUnique(str);
        assertFalse(result);

        str = "leetcode";
        result = isUnique2(str);
        assertFalse(result);

    }


}