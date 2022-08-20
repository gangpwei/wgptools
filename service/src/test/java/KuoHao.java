import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * 有效括号
 * Map存：反括号 和 正括号
 * 正括号入栈
 * 反括号出栈
 * 最后判断栈是否为空
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

            //反括号
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
            //正括号
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
