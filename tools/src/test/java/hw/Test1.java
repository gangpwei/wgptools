//package hw;
//
///**
// * 8���Ƚ϶�ά��������Сֵ�����һ�������鷵�ء�(ʵ�ֺ����㷨������Ҫʹ��IO)
//
// ���룺intArr = {{5,6,1,16},{7,3,9}}
//
// �����intArrs ={1,3}
//
// * @author gangpeng.wgp
// * @date 2020/01/07 11:37 PM
// */
//
//public class Test1 {
//    public static void main(String[] args) {
//        int[][] ints = {{5,6,1,16},{7,3,9}};
//
//        int[] result = getNewArray(ints);
//        System.out.println(result);
//    }
//
//    private static int[] getNewArray(int[][] ints) {
//        int[] result = new int[ints.length];
//        for (int i = 0; i < ints.length; i++) {
//            result[i] = getMinValue(ints[i]);
//        }
//        return result;
//    }
//
//    private static int getMinValue(int[] anInt) {
//        int temp = anInt[0];
//        for (int i : anInt) {
//            if(temp > i){
//                temp = i;
//            }
//        }
//        return temp;
//    }
//
//    private S
//}
