package jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gangpeng.wgp
 * @date 2022/11/11 8:41 обнГ
 */
public class GcDemo {
    static class OOMObject{

    }

    public static void main(String[] args) {
        List<OOMObject> list = new ArrayList<OOMObject>();
        while(true) {
            list.add(new OOMObject());
        }
    }
}
