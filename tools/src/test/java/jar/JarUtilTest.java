package jar;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author weigangpeng
 * @date 2017/12/25 ÏÂÎç2:35
 */

public class JarUtilTest {
    @Test
    public void getFormattedJarName() throws Exception {
    }

    @Test
    public void getJarFromJarName() throws Exception {
        //System.out.println(JarUtil.getJarFormJarName("tomcat-juli.jar"));

        //System.out.println(JarUtil.getJarFormJarName("aegean.common-1.0-20171222.033446-8.jar"));

        System.out.println(JarUtil.getJarFormJarName("fastjson-999-not-exist.jar"));


    }

    @Test
    public void getJarPomStrFormStr() throws Exception {
        //System.out.println(JarUtil.getJarFormJarName("tomcat-juli.jar"));

        //System.out.println(JarUtil.getJarFormJarName("aegean.common-1.0-20171222.033446-8.jar"));

        System.out.println(JarUtil.getJarPomStrFormStr("org.aspectj:aspectjrt:1.8.6"));


    }

}