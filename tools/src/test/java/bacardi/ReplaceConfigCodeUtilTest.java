package bacardi;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author gangpeng.wgp
 * @date 2019/01/21 обнГ5:28
 */

public class ReplaceConfigCodeUtilTest {
    @Test
    public void replaceConfigCode() throws Exception {
        ReplaceConfigCodeUtil.CONFIG_CENTER_CLASS = "com.ali.common.ConfigCenter";
        ReplaceConfigCodeUtil.replaceConfigCode(GenerateConfigCenterUtilTest.str);
        //ReplaceConfigCodeUtil.replaceConfigCode(GenerateConfigCenterUtilTest.str2);
    }

    @Test
    public void dealFileResourceConfig() throws Exception {
    }

}