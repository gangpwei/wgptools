package jar;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CollectionUtils;
import util.file.FileUtil;

/**
 * @author weigangpeng
 * @date 2017/12/17 下午7:10
 */

public class MoveOldJarToLibTest {

    private static final Logger log = LoggerFactory.getLogger(MoveOldJarToLibTest.class);


    /**
     * 还原删除的jar
     * @throws Exception
     */
    @Test
    public void addDependencyForRemovedByJarForAegean() throws Exception {

        String newCodeHome = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/";
        String oldCodeHome = "/Users/weigangpeng/IdeaProjects/aegean_home/trunk/";
        String oldJarListFolder = "/Users/weigangpeng/Documents/开发提效/aegean_oldjar";
        String logFileFolder = "/Users/weigangpeng/Documents/开发提效/aegeanlog/";
        String libPath = "bundle/war/target/aegean.war/WEB-INF/lib/";
        List<String> removedJarsByMisstake = JarAnlayzeUtil.getRemovedJarsByMisstakeNew(newCodeHome + libPath, logFileFolder, oldJarListFolder);

        addDependencyForRemovedByMistakeJar(
            newCodeHome,
            oldCodeHome,
            libPath,
            removedJarsByMisstake);

    }

    /**
     * 还原删除的jar
     * @throws Exception
     */
    @Test
    public void addDependencyForRemovedByJarForAegean2() throws Exception {

        String newCodeHome = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/";
        String oldCodeHome = "/Users/weigangpeng/IdeaProjects/aegean_home/trunk/";
        String oldJarListFolder = "/Users/weigangpeng/Documents/开发提效/aegean_oldjar";
        String logFileFolder = "/Users/weigangpeng/Documents/开发提效/aegean_oldjar/";
        String libPath = "bundle/war/target/aegean.war/WEB-INF/lib/";
        List<String> removedJarsByMisstake = JarAnlayzeUtil.getRemovedJarsByMisstakeNew(newCodeHome + libPath, logFileFolder, oldJarListFolder);

        addDependencyForRemovedByMistakeJar2(
            newCodeHome,
            oldCodeHome,
            libPath,
            removedJarsByMisstake);

    }

    /**
     * 为误删的jar 从主干lib目录拷贝到新应用lib中
     * @param oldCodePath
     * @throws Exception
     */
    public static void addDependencyForRemovedByMistakeJar2(String newCodeHome, String oldCodePath, String libPath, List<String> removeJarList) throws Exception {

        File libFolder = new File(oldCodePath + libPath);
        File[] files = libFolder.listFiles();
        HashMap<String, Jar> jarFileToJarMap = new HashMap<>();
        for (File file : files) {
            Jar jar = JarUtil.getJarFromJarFile(file);
            jarFileToJarMap.put(jar.getArtifactId(), jar);
        }


        System.out.println("\n");

        int x = 0;
        for (String jarName : removeJarList) {
            Jar jar = JarUtil.getJarFormJarName(jarName);

            Jar jar2 = jarFileToJarMap.get(jar.getArtifactId());
            if(jar2 == null){
                log.error("找不原来的jar信息：" + jarName);
                continue;
            }

            //if(x>50){
            //    return;
            //}

            x++;
            //System.out.println(jarName);
            copyToLib(jar2.getFileName(), newCodeHome, oldCodePath, libPath  );
        }
    }

    /**
     * 为误删的jar生成 dependency XML
     * @param oldCodePath
     * @throws Exception
     */
    public static void addDependencyForRemovedByMistakeJar(String newCodeHome, String oldCodePath, String libPath, List<String> removeJarList) throws Exception {

        String command = "cd " + oldCodePath;

        String mavenTreeLog = "mvntree.log";

        command += "&& mvn dependency:tree > " + mavenTreeLog;

        //ShellUtil.runShell(command);

        List<Jar> jars = JarTreeAnlayzeUtil.getJarDependencyTree(oldCodePath + mavenTreeLog);

        HashMap<String, Jar> jarFileToJarMap = new HashMap<>();
        for (Jar jar : jars) {
            putJarToMap(jar, jarFileToJarMap);
        }

        System.out.println("\n");

        int x = 0;
        for (String jarName : removeJarList) {
            Jar jar = jarFileToJarMap.get(jarName);
            if(jar == null){
                log.error("找不原来的jar信息：" + jarName);
                continue;
            }

            //if(x>50){
            //    return;
            //}

            x++;
            //System.out.println(jarName);
            copyToLib(jar.getFileName(), newCodeHome, oldCodePath, libPath  );
        }
    }

    private static void copyToLib(String jarName, String newCodeHome, String oldCodePath,  String libPath) {
        String oldPath = oldCodePath+ libPath + jarName;
        String newPath = newCodeHome + libPath + jarName;
        boolean result = FileUtil.copyFile( oldPath, newPath);
        if(result){
            log.info("复制：{}", jarName );
        }

        //log.info("复制：{} 到：{}", oldPath, newPath );

    }

    private static void putJarToMap(Jar jar, HashMap<String, Jar> jarFileToJarMap) {
        jarFileToJarMap.put(jar.getFileName(), jar);
        if(CollectionUtils.isNotEmpty(jar.getChildren())){
            for (Jar child : jar.getChildren()) {
                putJarToMap(child, jarFileToJarMap);
            }
        }
    }



}