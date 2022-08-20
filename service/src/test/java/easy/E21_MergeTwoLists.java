package easy;

import org.junit.Test;

/**
 * 合并有序链表
 * 头部指针 + 临时指针
 * 2个链表都不为空，开始遍历，2个链表最小的值，加到临时指针末尾，临时指针往后移
 * 任一链表为空，追加到临时链表末尾
 * 返回头指针的next
 * @author weigangpeng
 * @date 2018/01/21 上午11:24
 */

public class E21_MergeTwoLists {

    public ListNode mergeTwoLists2(ListNode l1, ListNode l2) {
        ListNode prehead = new ListNode(-1);

        ListNode prev = prehead;
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                prev.next = l1;
                l1 = l1.next;
            } else {
                prev.next = l2;
                l2 = l2.next;
            }
            prev = prev.next;
        }

        // 合并后 l1 和 l2 最多只有一个还未被合并完，我们直接将链表末尾指向未合并完的链表即可
        prev.next = l1 == null ? l2 : l1;

        return prehead.next;
    }



    @Test
    public void jia() throws Exception {
        ListNode result = null;

        result = mergeTwoLists2(ListNode.getListNode(new int[]{1, 2, 4}), ListNode.getListNode(new int[]{1, 3, 4}));
        result.printListNode();

        result = mergeTwoLists2(ListNode.getListNode(new int[]{5}), ListNode.getListNode(new int[]{1, 2, 4}));
        result.printListNode();
    }


}