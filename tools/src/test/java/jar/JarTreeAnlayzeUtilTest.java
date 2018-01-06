package jar;

import java.util.List;

/**
 * @author weigangpeng
 * @date 2017/12/16 ����3:32
 */

public class JarTreeAnlayzeUtilTest {
    @org.junit.Test
    public void getUnusedJars() throws Exception {
        List<String> unusedJars = JarAnlayzeUtil.getUnusedJarNames(
            "/Users/weigangpeng/IdeaProjects/muses_new/banner/bundle/war/target/muses.war/WEB-INF/lib",
            "/Users/weigangpeng/Documents/������Ч/muses_jar.log");

        List<Jar> jars = JarTreeAnlayzeUtil.getJarDependencyTree("/Users/weigangpeng/Documents/������Ч/mavntree.log");
        //for (Jar jar : jars) {
        //    System.out.println(jar.toLevelString());
        //}

        JarTreeAnlayzeUtil.generateExcludePom(unusedJars, jars);
    }
}