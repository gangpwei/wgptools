package jar;

import java.util.List;

import org.junit.Test;

/**
 * @author weigangpeng
 * @date 2017/12/17 ����7:10
 */

public class JarAddExcludeUtilTest {

    @Test
    public void processForAegean() throws Exception {

        String codePath = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/";
        String warPath = "/bundle/war/target/aegean.war";
        //String usedJarFile = "/Users/weigangpeng/Documents/������Ч/aegean_jar.log";
        String usedJarFile = "/Users/weigangpeng/Documents/������Ч/aegeanlog";

        JarAddExcludeUtil.process(codePath, warPath, usedJarFile);
    }

    @Test
    public void addDependencyForRemovedByMistakeJarForAegean() throws Exception {
        JarAddExcludeUtil.addDependencyForRemovedByMistakeJar(
            "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/target/aegean.war/WEB-INF/lib",
            "/Users/weigangpeng/Documents/������Ч/aegeanlog",
            "/Users/weigangpeng/IdeaProjects/aegean_home/trunk/");

    }

    /**
     * ��ԭɾ����jar
     * @throws Exception
     */
    @Test
    public void addDependencyForRemovedByJarForAegean() throws Exception {
        JarAddExcludeUtil.addDependencyForRemovedByMistakeJar(
            "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/target/aegean.war/WEB-INF/lib",
            "/Users/weigangpeng/Documents/������Ч/aegean_oldjar",
            "/Users/weigangpeng/IdeaProjects/aegean_home/trunk/");

    }

    @Test
    public void getRemovedJarsByMisstakeForAegean() throws Exception {
        List<String> removedJarsByMisstake = JarAnlayzeUtil.getRemovedJarsByMisstake(
            "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/target/aegean.war/WEB-INF/lib",
            "/Users/weigangpeng/Documents/������Ч/aegeanlog");
    }

    @Test
    public void processForMuses() throws Exception {

        String codePath = "/Users/weigangpeng/IdeaProjects/muses_new/banner/";
        String warPath = "/bundle/war/target/muses.war";
        String usedJarFile = "/Users/weigangpeng/Documents/������Ч/muses_jar.log";

        JarAddExcludeUtil.process(codePath, warPath, usedJarFile);
    }

}