package jar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CollectionUtils;
import util.FileUtil;
import util.StringUtil;

/**
 * @author weigangpeng
 * @date 2017/12/08 下午2:06
 */

public class JarAnlayzeUtil {

    private static final Logger log = LoggerFactory.getLogger(JarAnlayzeUtil.class);

    //public static List<Jar> getUnusedJars(String libPath, String logFile){
    //    List<Jar> unusedJarFile = new ArrayList<>();
    //
    //    //List<File> unusedJarFileList = new ArrayList<>();
    //
    //    List<String> usedJarNameList = getUsedJarList(logFile);
    //
    //    File file = new File(libPath);
    //    File[] files = file.listFiles();
    //    int allJarCount = 0;
    //    for (File jar : files) {
    //        if(jar.getName().endsWith(".jar")){
    //            allJarCount ++;
    //            if(jar.getName().equals("security-3.1-SNAPSHOT.jar")){
    //                int x = 0;
    //            }
    //
    //            String jarName = JarUtil.getFormattedJarName(jar.getName());
    //            Jar tempJar = JarUtil.getJarFromJarFile(jar);
    //            if(!usedJarNameList.contains(jarName) && !unusedJarFile.contains(tempJar)){
    //                tempJar.setConflictList(getConlictList(usedJarNameList, tempJar));
    //                unusedJarFile.add(tempJar);
    //            }
    //        }
    //    }
    //
    //    Collections.sort(unusedJarFile, new Comparator<Jar>() {
    //
    //        @Override
    //        public int compare(Jar o1, Jar o2) {
    //            //降序序排列
    //            if(o2.getFileSize() > o1.getFileSize()){
    //                return 1;
    //            }
    //            if(o1.getFileSize() == o2.getFileSize()){
    //                return 0;
    //            }
    //            return -1;
    //        }
    //    });
    //    for (Jar jar : unusedJarFile) {
    //        String confilcStr = CollectionUtils.isNotEmpty(jar.getConflictList()) ? "   冲突 : " + jar.getConflictList() : "";
    //        System.out.println(StringUtil.formatLen(jar.getFileName(), 40) + " 大小：" + FileUtil.getDataSize(jar.getFileSize()) + confilcStr);
    //    }
    //
    //    log.info( "jar包总数：{}, 没用的jar总数：{}\n", allJarCount,  unusedJarFile.size());
    //    return  unusedJarFile;
    //}

    private static List<Jar> getConlictList(List<String> usedJarNameList, Jar tempJar) {
        List<Jar> result = new ArrayList<>();
        for (String jarName : usedJarNameList) {
            Jar jar = JarUtil.getJarFormJarName(jarName);
            if(StringUtil.isNotEmpty(jar.getArtifactId()) &&
                StringUtil.isNotEmpty(jar.getVersion()) &&
                jar.getArtifactId().equals(tempJar.getArtifactId()) &&
                !jar.getVersion().equals(tempJar.getVersion())){
                result.add(jar);
            }
        }
        if(CollectionUtils.isNotEmpty(result)){
            return result;
        }
        return null;
    }

    public static List<String> getUnusedJarNames(String libPath, String logFile){
        List<String> unusedJarNameList = new ArrayList<>();


        List<String> usedJarNameList = getUsedJarList(logFile);

        List<Jar> unusedJarFile = new ArrayList<>();


        File file = new File(libPath);
        File[] files = file.listFiles();
        int allJarCount = 0;
        for (File jar : files) {
            if(jar.getName().endsWith(".jar")){
                allJarCount ++;
                if(jar.getName().contains("havana.common")){
                    int x = 0;
                }

                String jarName = JarUtil.getFormattedJarName(jar.getName());
                if(!usedJarNameList.contains(jarName) && !unusedJarNameList.contains(jarName)){
                    Jar tempJar = JarUtil.getJarFromJarFile(jar);
                    tempJar.setConflictList(getConlictList(usedJarNameList, tempJar));

                    unusedJarNameList.add(jarName);
                    unusedJarFile.add(tempJar);
                }
            }
        }

        Collections.sort(unusedJarFile, new Comparator<Jar>() {

            @Override
            public int compare(Jar o1, Jar o2) {
                //降序序排列
                if(o2.getFileSize() > o1.getFileSize()){
                    return 1;
                }
                if(o1.getFileSize() == o2.getFileSize()){
                    return 0;
                }
                return -1;
            }
        });
        for (Jar jar : unusedJarFile) {
            String confilcStr = CollectionUtils.isNotEmpty(jar.getConflictList()) ? "   冲突 : " + jar.getConflictList() : "";
            System.out.println(StringUtil.formatLen(jar.getFileName(), 40) + " 大小：" + FileUtil.getDataSize(jar.getFileSize()) + confilcStr);
        }

        log.info( "jar包总数：{}, 没用的jar总数：{}\n", allJarCount,  unusedJarFile.size());
        return  unusedJarNameList;
    }

    /**
     * 原来被应用，后来被误删的jar
     * @param libPath
     * @param logFile
     * @return
     */
    public static List<String> getRemovedJarsByMisstake(String libPath, String logFile){
        List<String> unusedJarFile = new ArrayList<>();

        //之前被用到的jar
        List<String> usedJarFile = getUsedJarListInProject(logFile);

        //现在被用到的jar
        Set<String> currentJarFileSet = new HashSet<>();
        File file = new File(libPath);
        File[] files = file.listFiles();
        for (File jar : files) {
            String jarName = JarUtil.getFormattedJarName(jar.getName());

            if(jar.getName().endsWith(".jar") && !currentJarFileSet.contains(jarName)){
                currentJarFileSet.add(jarName);
            }
        }

        List<String> removedJarList = new ArrayList<>();
        for (String jarName : usedJarFile) {
            if(!currentJarFileSet.contains(jarName)){
                removedJarList.add(jarName);
            }
        }

        log.info( "原来jar包总数：" + usedJarFile.size() + ", 现在jar包总数:" + currentJarFileSet.size() + ", 被删掉的jar总数：" + removedJarList.size() + "\n");

        for (String jarName : removedJarList) {
            log.error("被误删的jar：{}", jarName);
        }
        return  unusedJarFile;
    }

    /**
     * 获取应用用到的所有jar
     * @param logFile
     * @return
     */
    private static List<String> getUsedJarList(String logFile) {
        List<String> usedJarFile = new ArrayList<>();
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile), "UTF-8"));
            String line;
            while ((line = bReader.readLine()) != null) {
                if(line.length()>0){
                    if(!line.trim().endsWith(".jar") || !line.contains("/")){
                        continue;
                    }
                    String jar = line.substring(line.lastIndexOf("/" ) + 1, line.length());
                    jar = JarUtil.getFormattedJarName(jar);
                    if(!usedJarFile.contains(jar)){
                        usedJarFile.add(jar);
                    }
                }
            }
            bReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return usedJarFile;
    }

    ///**
    // * 获取应用用到的所有jar
    // * @param logFile
    // * @return
    // */
    //private static List<String> getUsedJarList(String logFile) {
    //    List<String> usedJarFile = new ArrayList<>();
    //    try {
    //        BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile), "UTF-8"));
    //        String line;
    //        while ((line = bReader.readLine()) != null) {
    //            if(line.length()>0){
    //                if(!line.trim().endsWith(".jar") || !line.contains("/")){
    //                    continue;
    //                }
    //                String jar = line.substring(line.lastIndexOf("/" ) + 1, line.length());
    //                jar = JarUtil.getFormattedJarName(jar);
    //                if(!usedJarFile.contains(jar)){
    //                    usedJarFile.add(jar);
    //                }
    //            }
    //        }
    //        bReader.close();
    //    } catch (IOException e) {
    //        e.printStackTrace();
    //    }
    //    return usedJarFile;
    //}

    /**
     * 获取应用用到的所有jar
     * @param logFile
     * @return
     */
    private static List<String> getUsedJarListInProject(String logFile) {
        List<String> usedJarFile = new ArrayList<>();
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile), "UTF-8"));
            String line;
            while ((line = bReader.readLine()) != null) {
                if(line.length()>0){
                    if(!line.trim().endsWith(".jar") || !line.contains("/") || !line.contains("/WEB-INF/lib")){
                        continue;
                    }
                    String jar = line.substring(line.lastIndexOf("/" ) + 1, line.length());
                    jar = JarUtil.getFormattedJarName(jar);
                    if(!usedJarFile.contains(jar)){
                        usedJarFile.add(jar);
                    }
                }
            }
            bReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return usedJarFile;
    }

    public static void printUnUsedJarFile(List<Jar> unusedJarFile, String mvnTreeFile){
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(mvnTreeFile), "UTF-8"));
            String line;
            while ((line = bReader.readLine()) != null) {
                if(line.length()>0){
                    if(!line.contains(":jar:")){
                        continue;
                    }

                    String includeTag = "+- ";
                    String excludeTag = "\\- ";

                    String startTag = null;
                    if(line.contains(includeTag)){
                        startTag = includeTag;
                    }else if(line.contains(excludeTag)){
                        startTag = excludeTag;
                    }else if(!line.contains(":compile") && line.contains("[INFO] ")){
                        startTag = "[INFO] ";
                    }

                    String newline = line.substring(line.indexOf(startTag) + startTag.length(), line.length());
                    String[] versions = newline.split(":");
                    String group = versions[0];
                    String artifact = versions[1];
                    String version = versions[3];
                    String file = artifact + "-" + version + ".jar";

                    Jar tempJar = new Jar(group, artifact, version);
                    //log.info(file);
                    if(unusedJarFile.contains(tempJar)){
                        System.err.println(line);
                        sleep();
                    }else{
                        log.info(line);
                        sleep();
                    }
                }

            }
            bReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sleep(){
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
