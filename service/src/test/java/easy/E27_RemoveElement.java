package easy;

import java.util.Arrays;

/**
 * 删除指定元素
 * 快慢指针
 *
 * @author gangpeng.wgp
 * @date 2022/5/29 11:54 AM
 */
public class E27_RemoveElement {
    public int removeDuplicates(int[] nums, int val) {
        if(nums.length == 0){
            return 0;
        }

        int j = 0;
        for(int i=0; i< nums.length; i++){
            if(nums[i] != val){
                nums[j] = nums[i];
                j++;
            }
        }
        return j;
    }

    public static void main(String[] args) {
        int[] nums = new int[]{1,1,2};
        int result = new E27_RemoveElement().removeDuplicates(nums, 2);
        System.out.println(result);
        System.out.println(Arrays.toString(nums));
        for (int i = 0; i < result; i++) {
            System.out.printf(String.valueOf(nums[i]) + " ");
        }
    }
}
