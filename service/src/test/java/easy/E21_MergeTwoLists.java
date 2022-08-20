package easy;

import org.junit.Test;

/**
 * �ϲ���������
 * ͷ��ָ�� + ��ʱָ��
 * 2��������Ϊ�գ���ʼ������2��������С��ֵ���ӵ���ʱָ��ĩβ����ʱָ��������
 * ��һ����Ϊ�գ�׷�ӵ���ʱ����ĩβ
 * ����ͷָ���next
 * @author weigangpeng
 * @date 2018/01/21 ����11:24
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

        // �ϲ��� l1 �� l2 ���ֻ��һ����δ���ϲ��꣬����ֱ�ӽ�����ĩβָ��δ�ϲ����������
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