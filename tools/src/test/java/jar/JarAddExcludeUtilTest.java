package jar;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import util.CollectionUtils;
import util.ShellUtil;

/**
 * @author weigangpeng
 * @date 2017/12/17 下午7:10
 */

public class JarAddExcludeUtilTest {
    @Test
    public void generateNewPom() throws Exception {

        String command = "cd /Users/weigangpeng/IdeaProjects/muses_new/banner/";

        command += "&& mvn dependency:tree > ztemp/maventree.log";
        ShellUtil.runShell(command);

        List<String> unusedJars = JarAnlayzeUtil.getUnusedJarNames(
            "/Users/weigangpeng/IdeaProjects/muses_new/banner/bundle/war/target/muses.war/WEB-INF/lib",
            "/Users/weigangpeng/Documents/开发提效/muses_jar.log");

        List<Jar> jars = JarTreeAnlayzeUtil.getJarDependencyTree("/Users/weigangpeng/IdeaProjects/muses_new/banner/ztemp/maventree.log");

        JarTreeAnlayzeUtil.generateExcludePom(unusedJars, jars);


        List<Jar> excludeRootJars = new ArrayList<>();
        for (Jar jar : jars) {
            if(jar.getDependencyLevel() == 0){
                if(CollectionUtils.isNotEmpty(jar.getChildren())){
                    for (Jar level1Jar : jar.getChildren()) {
                        if(CollectionUtils.isNotEmpty(level1Jar.getUnusedChildren())){

                            if(!excludeRootJars.contains(level1Jar)){
                                excludeRootJars.add(level1Jar);
                            }else{
                                Jar exsitRootJar = excludeRootJars.get(excludeRootJars.indexOf(level1Jar));
                                for (Jar unsedJar : level1Jar.getUnusedChildren()) {
                                    if(exsitRootJar.getUnusedChildren() != null && !exsitRootJar.getUnusedChildren().contains(unsedJar)){
                                        exsitRootJar.getUnusedChildren().add(unsedJar);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        JarAddExcludeUtil.generateNewPom("/Users/weigangpeng/IdeaProjects/muses_new/banner/pom.xml", excludeRootJars, "/Users/weigangpeng/IdeaProjects/muses_new/banner/pom2.xml");
    }

}