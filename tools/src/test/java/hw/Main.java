package hw;

import java.util.HashSet;
import java.util.Set;

/**
 * @author gangpeng.wgp
 * @date 2020/01/09 10:25 PM
 */

public class Main {
    public static void main(String[] args) {
        //if(args == null || args.length == 0){
        //    return;
        //}
        int count = getDiffCharCount("aasssdd");
        System.out.println(count);

    }

    private static Integer getDiffCharCount(String str) {
        char[] chars = str.toCharArray();

        Set<Integer> set = new HashSet<>();
        for (char aChar : chars) {
            Integer ascii = (int)aChar;
            if (ascii >= 0 && ascii<=127){
                set.add(ascii);
            }
        }
        return set.size();
    }
}
