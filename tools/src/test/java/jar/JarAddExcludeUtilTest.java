package jar;

import java.util.List;

import org.junit.Test;

/**
 * @author weigangpeng
 * @date 2017/12/17 下午7:10
 */

public class JarAddExcludeUtilTest {

    @Test
    public void processForAegean() throws Exception {

        String codePath = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/";
        String warPath = "/bundle/war/target/aegean.war";
        //String usedJarFile = "/Users/weigangpeng/Documents/开发提效/aegean_jar.log";
        String usedJarFile = "/Users/weigangpeng/Documents/开发提效/aegeanlog";

        JarAddExcludeUtil.process(codePath, warPath, usedJarFile);
    }

    @Test
    public void addDependencyForRemovedByMistakeJarForAegean() throws Exception {
        JarAddExcludeUtil.addDependencyForRemovedByMistakeJar(
            "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/target/aegean.war/WEB-INF/lib",
            "/Users/weigangpeng/Documents/开发提效/aegeanlog",
            "/Users/weigangpeng/IdeaProjects/aegean_home/trunk/");

    }

    /**
     * 还原删除的jar
     * @throws Exception
     */
    @Test
    public void addDependencyForRemovedByJarForAegean() throws Exception {
        JarAddExcludeUtil.addDependencyForRemovedByMistakeJar(
            "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/target/aegean.war/WEB-INF/lib",
            "/Users/weigangpeng/Documents/开发提效/aegean_oldjar",
            "/Users/weigangpeng/IdeaProjects/aegean_home/trunk/");

    }

    @Test
    public void getRemovedJarsByMisstakeForAegean() throws Exception {
        List<String> removedJarsByMisstake = JarAnlayzeUtil.getRemovedJarsByMisstake(
            "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/target/aegean.war/WEB-INF/lib",
            "/Users/weigangpeng/Documents/开发提效/aegeanlog");
    }

    @Test
    public void processForMuses() throws Exception {

        String codePath = "/Users/weigangpeng/IdeaProjects/muses_new/banner/";
        String warPath = "/bundle/war/target/muses.war";
        String usedJarFile = "/Users/weigangpeng/Documents/开发提效/muses_jar.log";

        JarAddExcludeUtil.process(codePath, warPath, usedJarFile);
    }

}