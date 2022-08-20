package easy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * 找到所有数组中消失的数字
 * 尾部扫描取最大值，放入原数组
 * 从2个数组总长度开始遍历
 * @author weigangpeng
 * @date 2018/01/21 上午11:24
 */

public class E448_findDisappearedNumbers {

    public List<Integer> findDisappearedNumbers(int[] nums) {
        int[] array = new int[nums.length];
        for(int n : nums ){
            int x = (n-1)%nums.length;
            array[x] = 1;
        }
        List<Integer> result = new ArrayList<Integer>();
        for(int n = 0; n < nums.length; n ++){
            if(array[n] < 1) {
                result.add(n + 1);
            }
        }
        return result;
    }
    

    @Test
    public void testCase() throws Exception {
        int[] nums1;
        nums1 = new int[]{4,3,2,7,8,2,3,1};
        List<Integer> result = findDisappearedNumbers(nums1);
        System.out.println(result);


    }


}