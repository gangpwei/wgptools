package jar;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import util.DateUtil;

/**
 * @author weigangpeng
 * @date 2017/12/08 下午2:18
 */

public class JarAnlayzeUtilTest {
    @Test
    public void getUnusedJars() throws Exception {
        //List<Jar> unusedJars = JarAnlayzeUtil.getUnusedJars(
        //    "/Users/weigangpeng/IdeaProjects/muses_new/banner/bundle/war/target/muses.war/WEB-INF/lib",
        //    "/Users/weigangpeng/Documents/开发提效/muses_jar.log");
        //
        //JarAnlayzeUtil.printUnUsedJarFile(unusedJars,
        //    "/Users/weigangpeng/Documents/开发提效/mavntree.log");
    }

    @Test
    public void getRemovedJarsByMisstake() throws Exception {
        List<String> removedJarsByMisstake = JarAnlayzeUtil.getRemovedJarsByMisstake(
            "/Users/weigangpeng/IdeaProjects/muses_new/banner/bundle/war/target/muses.war/WEB-INF/lib",
            "/Users/weigangpeng/Documents/开发提效/muses_jar.log");

    }


    @Test
    public void getJarName() {
        String name = "alipmc-api-1.2.6-20150525.110450-1.jar";
        String foramttedName = name;

        if(name.indexOf("-20") > 0){
            String snapshotStr = name.substring(name.indexOf("-20") + 1, name.indexOf(".jar"));
            String dateStr = snapshotStr.substring(0, snapshotStr.indexOf("."));
            Date snapshotDate = DateUtil.parseDate(dateStr, "yyyyMMdd");
            if(snapshotDate != null){
                System.out.println(snapshotStr);
                foramttedName = name.replaceAll(snapshotStr, "SNAPSHOT");
            }
        }
        System.out.println(foramttedName);
    }



}