/**
 * 判断回文数
 * @author gangpeng.wgp
 * @date 2022/5/28 2:11 PM
 */
public class IsPalindrome {

    /**
     * 转字符串
     * 头尾2边每次各取1位比较
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
     * 取余法，获取数组右边
     * 循环条件，右边值小于剩下左边值
     * 最后判断右边值等于左边值，或者左边值除10==右边值
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
