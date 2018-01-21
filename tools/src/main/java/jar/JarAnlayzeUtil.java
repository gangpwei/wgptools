package jar;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CollectionUtils;
import util.file.FileUtil;
import util.StringUtil;

import static util.StringUtil.formatLen;

/**
 * @author weigangpeng
 * @date 2017/12/08 ����2:06
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
    //            //����������
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
    //        String confilcStr = CollectionUtils.isNotEmpty(jar.getConflictList()) ? "   ��ͻ : " + jar.getConflictList() : "";
    //        System.out.println(StringUtil.formatLen(jar.getFileName(), 40) + " ��С��" + FileUtil.getDataSize(jar.getFileSize()) + confilcStr);
    //    }
    //
    //    log.info( "jar��������{}, û�õ�jar������{}\n", allJarCount,  unusedJarFile.size());
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

        List<String> usedJarNameList = getUsedJarListFormLogFolder(logFile);

        List<String> usedJarArtifactIdList = getUsedJarArtifactIdList(usedJarNameList);

        List<Jar> unusedJarFile = new ArrayList<>();

        File file = new File(libPath);
        File[] files = file.listFiles();
        int allJarCount = 0;
        for (File jar : files) {
            if(!jar.getName().endsWith(".jar")){
                continue;
            }
            allJarCount ++;
            if(jar.getName().contains("aegean.isearch")){
                int x = 0;
            }

            String jarName = JarUtil.getFormattedJarName(jar.getName());
            Jar tempJar = JarUtil.getJarFromJarFile(jar);
            if(!usedJarNameList.contains(jarName) && !unusedJarNameList.contains(jarName) && !usedJarArtifactIdList.contains(tempJar.getArtifactId())){
                tempJar.setConflictList(getConlictList(usedJarNameList, tempJar));

                unusedJarNameList.add(jarName);
                unusedJarFile.add(tempJar);
            }
        }

        Collections.sort(unusedJarFile, new Comparator<Jar>() {

            @Override
            public int compare(Jar o1, Jar o2) {
                //����������
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
            String confilcStr = CollectionUtils.isNotEmpty(jar.getConflictList()) ? "   ��ͻ : " + jar.getConflictList() : "";
            System.out.println(StringUtil.formatLen(jar.getFileName(), 40) + " ��С��" + FileUtil.getDataSize(jar.getFileSize()) + confilcStr);
        }

        log.info( "jar��������{}, û�õ�jar������{}\n", allJarCount,  unusedJarFile.size());
        return  unusedJarNameList;
    }

    /**
     * ԭ����Ӧ�ã���������ɾ��jar
     * @param libPath
     * @param logFileFolder
     * @return
     */
    public static List<String> getRemovedJarsByMisstakeNew(String libPath, String logFileFolder , String oldJarListFolder){

        //֮ǰ���õ���jar
        List<String> usedJarNameList = getUsedJarListFormLogFolder(logFileFolder, new String[]{"/WEB-INF/lib"});
        //List<String> usedJarNameList = getUsedJarListFormLogFolder(logFileFolder, null);
        //HashMap<String, Jar> usedByLibJarMap = getUsedJarMap(logFileFolder, new String[]{"/WEB-INF/lib"});

        //֮ǰȫ��jar��������jar����jar��ȫ��
        List<String> allJarNameList = getUsedJarListFormLogFolder(oldJarListFolder, null);
        //֮ǰȫ��jar��������jar����jar��artifactId - Jar���� Map
        HashMap<String, Jar> allJarJarMap = getUsedJarMap(oldJarListFolder, null);

        //֮ǰ��Pandorna�õ���jar
        HashMap<String, Jar> usedByPandornaJarMap = getUsedJarMap(logFileFolder, new String[]{"taobao-hsf.sar/plugins"});

        //֮ǰ��Pandorna�õ���jar
        List<String> usedByPandornaJarList = new ArrayList<>();
        for (String artifactId : usedByPandornaJarMap.keySet()) {
            if(allJarJarMap.containsKey(artifactId)){
                usedByPandornaJarList.add(usedByPandornaJarMap.get(artifactId).getFileName());
            }
        }

        usedJarNameList.addAll(usedByPandornaJarList);

        //List<String> usedJarArtifactIdList = getUsedJarArtifactIdList(usedJarNameList);

        //���ڱ��õ���jar
        Set<String> currentJarFileSet = new HashSet<>();

        //������jar
        Set<String> newAddJarFileSet = new HashSet<>();

        HashMap<String, Jar> currentJarArtifactIdMap = new HashMap<>();
        File file = new File(libPath);
        File[] files = file.listFiles();
        for (File jar : files) {
            if(!jar.getName().endsWith(".jar")){
                continue;
            }
            String jarName = JarUtil.getFormattedJarName(jar.getName());
            Jar tempJar = JarUtil.getJarFromJarFile(jar);
            if( !currentJarFileSet.contains(jarName)){
                currentJarArtifactIdMap.put(tempJar.getArtifactId(), tempJar);
                currentJarFileSet.add(jarName);
            }

            if(!allJarJarMap.containsKey(tempJar.getArtifactId())){
                newAddJarFileSet.add(jarName);
            }
        }

        List<String> removedJarList = new ArrayList<>();
        List<String> updateVersionJarList = new ArrayList<>();
        List<String> lowerVersionJarList = new ArrayList<>();
        for (String jarName : usedJarNameList) {
            if(jarName.startsWith("aegean.")){
                continue;
            }
            Jar oldJar = JarUtil.getJarFormJarName(jarName);

            Jar oldJarInLib = allJarJarMap.get(oldJar.getArtifactId());
            if(jarName.contains("gecko")){
                int y=0;
            }
            Jar newJar = currentJarArtifactIdMap.get(oldJarInLib.getArtifactId());

            if(!currentJarFileSet.contains(jarName)){
                if(currentJarArtifactIdMap.containsKey(oldJarInLib.getArtifactId())){
                    String oldJarName = formatLen(oldJarInLib.getFileName(), 35);
                    String newJarName = formatLen(newJar.getFileName(), 35);

                    String versionLessThanOldVersion = "";

                    int compareResult = newJar.getVersion().replace("-SNAPSHOT", "").compareTo(oldJarInLib.getVersion().replace("-SNAPSHOT", ""));

                    if(compareResult < 0 ){
                        versionLessThanOldVersion = "  �汾���½�";
                        lowerVersionJarList.add(oldJarName + "  �°汾��" + newJarName + versionLessThanOldVersion);
                    }else if(compareResult > 0 ){
                        updateVersionJarList.add(oldJarName + "  �°汾��" + newJarName + versionLessThanOldVersion);
                    }

                }else{
                    removedJarList.add(jarName);
                }
            }
        }

        log.info( "\nԭ��jar��������{}, ��Pandorna�õ���jar������{}������jar������:{}, ���°汾�ŵ�jar��{}, ����ɾ����jar������{}, ����jar��{}\n" ,allJarNameList.size(), usedByPandornaJarList.size(), currentJarFileSet.size() , updateVersionJarList.size() + lowerVersionJarList.size(), removedJarList.size(), newAddJarFileSet.size());

        for (String jarName : updateVersionJarList) {
            if(jarName.startsWith("aegean.")){
                continue;
            }
            log.info("���°汾�ŵ�jar��{}", jarName);
        }
        for (String jarName : lowerVersionJarList) {
            if(jarName.startsWith("aegean.")){
                continue;
            }
            log.info("���°汾�ŵ�jar��{}", jarName);
        }


        System.out.println("\n\n");

        for (String jarName : removedJarList) {
            log.info("����ɾ��jar��{}", jarName);
        }

        System.out.println("\n\n");

        for (String jarName : newAddJarFileSet) {
            log.info("������jar��{}", jarName);
        }
        return  removedJarList;
    }

    /**
     * ԭ����Ӧ�ã���������ɾ��jar
     * @param libPath
     * @param logFileFolder
     * @return
     */
    public static List<String> getRemovedJarsByMisstake(String libPath, String logFileFolder){

        //֮ǰ���õ���jar
        List<String> usedJarNameList = getUsedJarListFormLogFolder(logFileFolder, new String[]{"/WEB-INF/lib"});
        //List<String> usedJarNameList = getUsedJarListFormLogFolder(logFileFolder, null);
        HashMap<String, Jar> usedByLibJarMap = getUsedJarMap(logFileFolder, new String[]{"/WEB-INF/lib"});


        //֮ǰ��Pandorna�õ���jar
        List<String> usedByPandornaJarList = getUsedJarListFormLogFolder(logFileFolder, new String[]{"taobao-hsf.sar/plugins"});

        //List<String> usedJarArtifactIdList = getUsedJarArtifactIdList(usedJarNameList);

        //���ڱ��õ���jar
        Set<String> currentJarFileSet = new HashSet<>();
        HashMap<String, Jar> currentJarMap = new HashMap<>();
        File file = new File(libPath);
        File[] files = file.listFiles();
        for (File jar : files) {
            if(!jar.getName().endsWith(".jar")){
                continue;
            }
            String jarName = JarUtil.getFormattedJarName(jar.getName());
            Jar tempJar = JarUtil.getJarFromJarFile(jar);
            if(jar.getName().endsWith(".jar") && !currentJarFileSet.contains(jarName)){
                currentJarMap.put(tempJar.getArtifactId(), tempJar);
                currentJarFileSet.add(jarName);
            }
        }

        List<String> removedJarList = new ArrayList<>();
        List<String> updateVersionJarList = new ArrayList<>();
        for (String jarName : usedJarNameList) {
            Jar tempJar = JarUtil.getJarFormJarName(jarName);

            if(jarName.contains("spring-2.5.6.jar")){
                int y=0;
            }
            if(!currentJarFileSet.contains(jarName)){
                if(currentJarMap.containsKey(tempJar.getArtifactId())){
                    updateVersionJarList.add(formatLen(jarName, 35) + "  �°汾��" + currentJarMap.get(tempJar.getArtifactId()).getFileName());
                }else{
                    removedJarList.add(jarName);
                }
            }
        }

        log.info( "ԭ��jar��������{}, ����jar������:{}, ���°汾�ŵ�jar��{}, ����ɾ����jar������{}\n" ,usedJarNameList.size(), currentJarFileSet.size() , updateVersionJarList.size(), removedJarList.size());

        for (String jarName : updateVersionJarList) {
            if(jarName.startsWith("aegean.")){
                continue;
            }
            log.info("���°汾�ŵ�jar��{}", jarName);
        }
        log.info("\n\n");

        for (String jarName : removedJarList) {
            log.info("����ɾ��jar��{}", jarName);
        }
        return  removedJarList;
    }

    /**
     * ���õ���jar��־��ҵ��õ���jar Map
     * @param logFileFolder
     * @param includePath
     * @return
     */
    private static HashMap<String,Jar> getUsedJarMap(String logFileFolder, String[] includePath) {
        HashMap<String, Jar> jarMap = new HashMap<>();
        List<String> usedJarNameList = getUsedJarListFormLogFolder(logFileFolder, includePath);
        for (String jarName : usedJarNameList) {
            Jar jar = JarUtil.getJarFormJarName(jarName);
            jarMap.put(jar.getArtifactId(), jar);
        }
        return jarMap;
    }

    /**
     * ��ȡӦ���õ�������jar
     * @param logFile
     * @return
     */
    private static List<String> getUsedJarList(String logFile, String[] includePath) {
        List<String> usedJarFile = new ArrayList<>();
        try {
            //��ȡ�ļ���ÿһ�з���list��
            List<String> lineList = FileUtil.readAllLines(logFile);

            for (String line : lineList) {
                if(line.length()>0){
                    line = line.trim();
                    if(!line.endsWith(".jar") || !isIncludeList(line, includePath)){
                        continue;
                    }
                    String jar = line;
                    if(line.contains("/")){
                        jar = line.substring(line.lastIndexOf("/" ) + 1, line.length());
                    }
                    jar = JarUtil.getFormattedJarName(jar);
                    if(!usedJarFile.contains(jar)){
                        usedJarFile.add(jar);
                        //log.info("[{}]", jar );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usedJarFile;
    }

    /**
     * �ж��ַ����Ƿ����������ĳһ���ַ���
     * @param str
     * @param excludeList
     * @return
     */
    public static boolean isIncludeList(String str, String[] excludeList){
        if(excludeList == null || excludeList.length == 0){
            return true;
        }
        for (String s : excludeList) {
            if(str.contains(s)){
                return true;
            }
        }
        return false;
    }

    /**
     * ��ȡӦ���õ�������jar
     * @param logFile
     * @return
     */
    private static List<String> getUsedJarList(String logFile) {
        return getUsedJarList(logFile, null);
    }

    /**
     * ��ȡӦ���õ�������jar
     * @param logFileFolder
     * @return
     */
    private static List<String> getUsedJarListFormLogFolder(String logFileFolder, String[] includePath) {
        List<String> usedJarFile = new ArrayList<>();
        try {

            File file = new File(logFileFolder);
            File[] files = file.listFiles();
            for (File log : files) {
                List<String> list = getUsedJarList(log.getAbsolutePath(), includePath);
                for (String s : list) {
                    if(!usedJarFile.contains(s)){
                        usedJarFile.add(s);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return usedJarFile;
    }

    /**
     * ��ȡӦ���õ�������jar
     * @param logFileFolder
     * @return
     */
    private static List<String> getUsedJarListFormLogFolder(String logFileFolder) {
        return getUsedJarListFormLogFolder(logFileFolder, null);
    }

    /**
     * ��ȡӦ���õ�������jar ��artifactId
     * @param usedJarNameList
     * @return
     */
    private static List<String> getUsedJarArtifactIdList(List<String> usedJarNameList) {
        List<String> usedJarFile = new ArrayList<>();
        try {
            for (String jarName : usedJarNameList) {
                if(jarName.length()>0){
                    if(!jarName.trim().endsWith(".jar")){
                        continue;
                    }
                    if(jarName.contains("b2b.crm.aegean.biz.platform-1.0")){
                        int x=0;
                    }
                    jarName = JarUtil.getFormattedJarName(jarName);
                    String id = JarUtil.getJarFormJarName(jarName).getArtifactId();
                    if(!usedJarFile.contains(id)){
                        usedJarFile.add(id);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return usedJarFile;
    }

    /**
     * ��ȡӦ���õ�������jar
     * @param logFile
     * @return
     */
    private static List<String> getUsedJarListInProject(String logFile) {
        List<String> usedJarFile = new ArrayList<>();
        try {
            //��ȡ�ļ���ÿһ�з���list��
            List<String> lineList = FileUtil.readAllLines(logFile);

            for (String line : lineList) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usedJarFile;
    }

    public static void printUnUsedJarFile(List<Jar> unusedJarFile, String mvnTreeFile){
        try {
            //��ȡ�ļ���ÿһ�з���list��
            List<String> lineList = FileUtil.readAllLines(mvnTreeFile);

            for (String line : lineList) {
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
        } catch (Exception e) {
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
