import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * ��Ч����
 * Map�棺������ �� ������
 * ��������ջ
 * �����ų�ջ
 * ����ж�ջ�Ƿ�Ϊ��
 * @author gangpeng.wgp
 * @date 2021/6/16 10:07 PM
 */
public class KuoHao {

    public boolean isValid(String s) {

        Deque<Character> stack = new LinkedList<Character>();

        Map<Character, Character> map = new HashMap<Character, Character>(){{
            put(')', '(');
            put(']', '[');
            put('}', '{');
        }};

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            //������
            if(map.containsKey(c)){
                if(stack.isEmpty()){
                    return false;
                }
                char leftC = stack.pop();
                char extpectLeftC = map.get(c);
                if(leftC != extpectLeftC){
                    return false;
                }
            }
            //������
            else{
                stack.push(c);
            }
        }

        return stack.isEmpty();
    }

    @Test
    public void runTest() throws Exception {

        Assert.assertTrue(isValid("()"));
        Assert.assertTrue(isValid("()[]{}"));
        Assert.assertFalse(isValid("(]"));
        Assert.assertFalse(isValid("([)]"));

    }
}
