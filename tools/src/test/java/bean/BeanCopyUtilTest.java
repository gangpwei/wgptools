package bean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

/**
 * @author gangpeng.wgp
 * @date 2019/12/26 10:41 AM
 */

public class BeanCopyUtilTest {
    @Test
    public void generateSetMethod(){
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(FulfillOrder.class);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }

        List<PropertyDescriptor> descriptors = Arrays.stream(beanInfo.getPropertyDescriptors()).filter(p -> {
            String name = p.getName();
            //���˵�����Ҫ�޸ĵ�����
            return !"class".equals(name) && !"id".equals(name);
        }).collect(Collectors.toList());

        for (PropertyDescriptor descriptor : descriptors) {
            //descriptor.getWriteMethod()������Ӧset����
            Method readMethod = descriptor.getReadMethod();
            System.out.println(readMethod.getName());
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void generateSetMethod2() {

        Class claz = FulfillOrder.class;

        Field[] fields = claz.getDeclaredFields();

        //���� 1  ͨ�� ƴ�� get���� ��� �ֶ��е�ֵ

        for (Field field : fields) {
            // ����ַ�������
            String fieldName = field.getName();

            if (fieldName.equals("serialVersionUID")) {
                continue;
            }

            // �ַ�������ĸ��д
            char[] cs = fieldName.toCharArray();
            cs[0] -= 32;
            //System.out.println(String.valueOf(cs));

            // ����get����
            Method method = null;
            try {
                //method = claz.getMethod("get" + String.valueOf(cs));
                System.out.println("get" + String.valueOf(cs));
            } catch (Exception e) {
                System.out.println();

                //e.printStackTrace();
            }

        }
    }

    @Test
    public void generateCopyCode() {

        Class claz = FulfillOrder.class;

        String sourceObjName = "sourceObj";
        String targetObjName = "targetObj";

        generrateCopyCode(claz, sourceObjName, targetObjName);
    }

    private void generrateCopyCode(Class claz, String sourceObjName, String targetObjName) {
        Field[] fields = claz.getDeclaredFields();

        for (Field field : fields) {
            // ����ַ�������
            String fieldName = field.getName();

            if (fieldName.equals("serialVersionUID")) {
                continue;
            }

            // �ַ�������ĸ��д
            char[] cs = fieldName.toCharArray();
            cs[0] -= 32;
            //System.out.println(String.valueOf(cs));

            System.out.println( targetObjName + "." + "set" + String.valueOf(cs) + "(" + sourceObjName + "." + fieldName + ");");

        }
    }

    @Test
    public void generateCopyCode2() {

        Class claz = FulfillOrder.class;

        String sourceObjName = "sourceObj";
        String targetObjName = "targetObj";

        generrateCopyCode2(claz, sourceObjName, targetObjName);
    }


    private void generrateCopyCode2(Class claz, String sourceObjName, String targetObjName) {
        Field[] fields = claz.getDeclaredFields();

        for (Field field : fields) {
            // ����ַ�������
            String fieldName = field.getName();

            if (fieldName.equals("serialVersionUID")) {
                continue;
            }

            // �ַ�������ĸ��д
            char[] cs = fieldName.toCharArray();
            cs[0] -= 32;
            //System.out.println(String.valueOf(cs));

            System.out.println( targetObjName + "." + "set" + String.valueOf(cs) + "();");

        }
    }
}
