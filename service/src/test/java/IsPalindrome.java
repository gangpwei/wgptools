/**
 * �жϻ�����
 * @author gangpeng.wgp
 * @date 2022/5/28 2:11 PM
 */
public class IsPalindrome {

    /**
     * ת�ַ���
     * ͷβ2��ÿ�θ�ȡ1λ�Ƚ�
     * @param x
     * @return
     */
    public boolean isPalindrome1(int x) {
        if(x <0){
            return false;
        }

        if(x ==0){
            return true;
        }

        String str = String.valueOf(x);
        int length = str.length();
        int loopTimes;
        if(length%2 == 0){
            loopTimes = length/2;
        }else{
            loopTimes = (length-1)/2;
        }
        for(int i = 0; i<loopTimes; i++){
            if(str.charAt(i) != str.charAt(length-i-1)){
                return false;
            }
        }
        return true;
    }

    /**
     * ȡ�෨����ȡ�����ұ�
     * ѭ���������ұ�ֵС��ʣ�����ֵ
     * ����ж��ұ�ֵ�������ֵ���������ֵ��10==�ұ�ֵ
     * @param x
     * @return
     */
    public boolean isPalindrome2(int x) {
        if(x <0){
            return false;
        }

        if(x ==0){
            return true;
        }

        int revertNum = 0;
        while (revertNum < x){
            revertNum = revertNum * 10 + x%10;
            x = x/10;
        }

        return revertNum == x || revertNum/10 == x;
    }
}
