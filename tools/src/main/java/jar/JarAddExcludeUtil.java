package jar;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CollectionUtils;
import util.file.FileUtil;
import util.ShellUtil;
import util.StringUtil;

import static util.StringUtil.getTab;

/**
 * 自动修改jar pom文件，增加exclude列表
 * @author weigangpeng
 * @date 2017/12/08 下午2:06
 */

public class JarAddExcludeUtil {

    private static final Logger log = LoggerFactory.getLogger(JarAddExcludeUtil.class);
    
    public static void process(String codePath, String warPath, String usedJarFile) throws Exception {
        List<String> unusedJars = JarAnlayzeUtil.getUnusedJarNames(
            codePath + warPath + "/WEB-INF/lib",
            usedJarFile);
        //
        //codePath = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/biz/aegean-common/";
        //codePath = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/biz/aegean-domain/";
        //codePath = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/biz/aegean-adapter/";
        //codePath = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/biz/aegean-framework/";
        //codePath = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/biz/aegean-isearch/";
        //codePath = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/biz/aegean-leads/";
        //codePath = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/biz/aegean-citypartner/";
        //codePath = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/biz/platform/";

        //codePath = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/";

        //codePath = "/Users/weigangpeng/IdeaProjects/git/aegean-adapter/";

        String command = "cd " + codePath;

        //String mavenTreeLog = "ztemp/maventree.log";
        String mavenTreeLog = "maventree.log";

        command += "&& mvn dependency:tree > " + mavenTreeLog;

        ShellUtil.runShell(command);

        List<Jar> jars = JarTreeAnlayzeUtil.getJarDependencyTree(codePath + mavenTreeLog);

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

        JarAddExcludeUtil.generateNewPom(codePath + "pom.xml", excludeRootJars,  unusedJars, codePath + "pom2.xml");
    }



    /**
     * 为误删的jar生成 dependency XML
     * @param oldCodePath
     * @throws Exception
     */
    public static void addDependencyForRemovedByMistakeJar(String libPath, String logFileFolder, String oldCodePath) throws Exception {
        List<String> removedJarsByMisstake = JarAnlayzeUtil.getRemovedJarsByMisstake(libPath, logFileFolder);
        addDependencyForRemovedByMistakeJar(oldCodePath, removedJarsByMisstake);
    }

    /**
     * 为误删的jar生成 dependency XML
     * @param oldCodePath
     * @throws Exception
     */
    public static void addDependencyForRemovedByMistakeJar(String oldCodePath, List<String> removeJarList) throws Exception {

        String command = "cd " + oldCodePath;

        //String mavenTreeLog = "ztemp/maventree.log";
        String mavenTreeLog = "mvntree.log";

        command += "&& mvn dependency:tree > " + mavenTreeLog;

        //ShellUtil.runShell(command);

        List<Jar> jars = JarTreeAnlayzeUtil.getJarDependencyTree(oldCodePath + mavenTreeLog);

        HashMap<String, Jar> jarFileToJarMap = new HashMap<>();
        for (Jar jar : jars) {
            putJarToMap(jar, jarFileToJarMap);
        }

        System.out.println("\n");

        for (String jarName : removeJarList) {
            Jar jar = jarFileToJarMap.get(jarName);
            if(jar == null){
                //log.error("找不原来的jar信息：" + jarName);
                continue;
            }
            System.out.println(jar.toPomString());
        }
    }

    private static void putJarToMap(Jar jar, HashMap<String, Jar> jarFileToJarMap) {
        jarFileToJarMap.put(jar.getFileName(), jar);
        if(CollectionUtils.isNotEmpty(jar.getChildren())){
            for (Jar child : jar.getChildren()) {
                putJarToMap(child, jarFileToJarMap);
            }
        }
    }

    public static void generateNewPom(String pomPath, List<Jar> jarTreeList, List<String> unusedJars, String newPomPath){

        StringBuffer newPomFileBuffer = new StringBuffer();

        //需要排除子节点的一级jar Map
        Map<String, Jar> exludeRootMap = new HashMap<>();
        for (Jar jar : jarTreeList) {
            //exludeRootMap.put(jar.getFileName(), jar);
            exludeRootMap.put(jar.getArtifactId(), jar);
        }

        Jar jar = null;

        Jar parentExcludeJar = null;

        int lineNumber = 0;
        try {
            String blankStrBeforeArtifactId = null;
            boolean inExclude = false;
            StringBuffer excludePomListSb = null;
            StringBuffer beforeBuffer = null;
            ArrayList<String> jarStrList = null;
            //读取文件，每一行放入list中
            List<String> lineList = FileUtil.readAllLines(pomPath);

            for (String line : lineList) {
                lineNumber += 1;

                //是否被注释掉
                if(StringUtil.isEmpty(line) || line.contains("<!--") || line.contains("-->")){
                    newPomFileBuffer.append(line).append("\n");
                    continue;
                }

                if(lineNumber == 646 ){
                    int x = 0;
                }
                if(line.contains("aegean.adapter") ){
                    int x = 0;
                }
                if(line.contains("<dependency>") ){
                    jar = new Jar();
                    inExclude = false;
                    jarStrList = new ArrayList<>();
                }

                if(line.contains("<exclusion>") ){
                    if(parentExcludeJar != null && jar != null){
                        jar.addUnUsedChild(parentExcludeJar);
                    }

                    parentExcludeJar = new Jar();
                    inExclude = true;
                }

                if(line.contains("<exclusions>") && excludePomListSb != null ){
                    excludePomListSb = null;
                    inExclude = true;
                }

                if(!inExclude && jar != null ){
                    if(line.contains("<groupId>") && line.contains("</groupId>")){
                        String group = line.substring(line.indexOf("<groupId>" ) + 9, line.indexOf("</groupId>" ));
                        jar.setGroup(group);
                    }
                    if(line.contains("<artifactId>") && line.contains("</artifactId>")){
                        String artifactId = line.substring(line.indexOf("<artifactId>" ) + 12, line.indexOf("</artifactId>" ));
                        jar.setArtifactId(artifactId);
                        blankStrBeforeArtifactId = line.substring(0, line.indexOf("<artifactId>"));
                    }
                    if(line.contains("<version>") && line.contains("</version>")){
                        String version = line.substring(line.indexOf("<version>" ) + 9, line.indexOf("</version>" ));
                        jar.setVersion(version);
                    }
                }

                if(inExclude && parentExcludeJar != null && jar != null ){
                    if(line.contains("<groupId>") && line.contains("</groupId>")){
                        String group = line.substring(line.indexOf("<groupId>" ) + 9, line.indexOf("</groupId>" ));
                        parentExcludeJar.setGroup(group);
                    }
                    if(line.contains("<artifactId>") && line.contains("</artifactId>")){
                        String artifactId = line.substring(line.indexOf("<artifactId>" ) + 12, line.indexOf("</artifactId>" ));
                        parentExcludeJar.setArtifactId(artifactId);
                    }
                    if(line.contains("<version>") && line.contains("</version>")){
                        String version = line.substring(line.indexOf("<version>" ) + 9, line.indexOf("</version>" ));
                        parentExcludeJar.setVersion(version);
                    }
                }

                if(line.contains("</exclusions>") && jar != null && exludeRootMap.get(jar.getArtifactId()) != null ){
                    if(jar.getArtifactId().equals("webx3.core")){
                        int d = 0;
                    }
                    excludePomListSb = new StringBuffer();
                    //是否已经存在exclusions中
                    for (Jar excludeJar : exludeRootMap.get(jar.getArtifactId()).getUnusedChildren()) {
                        boolean isAlreadyInExcludeList = false;
                        if(CollectionUtils.isNotEmpty(jar.getUnusedChildren())){
                            for (Jar unusedJar : jar.getUnusedChildren()) {
                                if(unusedJar.getGroup().equals(excludeJar.getGroup()) && unusedJar.getArtifactId().equals(excludeJar.getArtifactId())){
                                    isAlreadyInExcludeList = true;
                                }
                            }
                        }
                        if(!isAlreadyInExcludeList){
                            excludePomListSb.append(getExcludePom(blankStrBeforeArtifactId, excludeJar));
                        }
                    }
                    if(StringUtil.isNotEmpty(excludePomListSb.toString()) && ! unusedJars.contains(jar.getFileName())){
                        excludePomListSb.deleteCharAt(excludePomListSb.lastIndexOf("\n"));
                        beforeBuffer = excludePomListSb;
                    }
                }

                if(jar != null && StringUtil.isNotEmpty(jar.getArtifactId()) && line.contains("</dependency>") && CollectionUtils.isEmpty(jar.getUnusedChildren()) && exludeRootMap.get(jar.getArtifactId()) != null && CollectionUtils.isNotEmpty(exludeRootMap.get(jar.getArtifactId()).getUnusedChildren()) && ! unusedJars.contains(jar.getFileName())){
                    StringBuffer tempSb = new StringBuffer();
                    excludePomListSb = new StringBuffer();
                    for (Jar excludeJar : exludeRootMap.get(jar.getArtifactId()).getUnusedChildren()) {
                        excludePomListSb.append(getExcludePom(blankStrBeforeArtifactId, excludeJar));
                    }
                    tempSb.append(blankStrBeforeArtifactId).append("<exclusions>\n").append(excludePomListSb).append(blankStrBeforeArtifactId).append("</exclusions>");
                    beforeBuffer = tempSb;
                    excludePomListSb = null;
                }

                if(jar != null && StringUtil.isNotEmpty(jar.getArtifactId()) && beforeBuffer != null && beforeBuffer.length() > 0){
                    exludeRootMap.remove(jar.getArtifactId());
                    jarStrList.add(beforeBuffer.toString());
                    //System.err.println(beforeBuffer.toString());
                    beforeBuffer = null;
                    excludePomListSb = null;
                }
                if(jarStrList == null){
                    newPomFileBuffer.append(line).append("\n");
                }else{
                    jarStrList.add(line);
                }

                if(line.contains("</dependency>")){

                    if(! unusedJars.contains(jar.getFileName())){
                        newPomFileBuffer.append(getJarStrFormList(jarStrList, false));
                    }else{
                        //如果整个jar都没用，直接把jar注释掉
                        newPomFileBuffer.append(getJarStrFormList(jarStrList, true));
                    }

                    jarStrList = null;
                }

                if(line.contains("</dependency>") && excludePomListSb != null ){
                    jar = null;
                }
            }

            newPomFileBuffer.deleteCharAt(newPomFileBuffer.lastIndexOf("\n"));

            FileOutputStream out=new FileOutputStream(newPomPath);
            out.write(newPomFileBuffer.toString().getBytes("UTF-8"));
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拼jar xml, 如果需要被注释掉，所有XML每一行都会被注释
     * @param jarStrList
     * @param isCommened 是否需要b被注释
     * @return
     */
    private static StringBuffer getJarStrFormList(ArrayList<String> jarStrList, boolean isCommened) {
        StringBuffer sb = new StringBuffer();
        for (String s : jarStrList) {
            if(isCommened){
                sb.append("<!-- ");
            }
            sb.append(s);
            if(isCommened){
                sb.append(" -->");
            }
            sb.append("\n");
        }
        return sb;
    }

    private static String getExcludePom(String blankStrBeforeVersion, Jar excludeJar) {
        int tab = 1;
        final StringBuffer sb = new StringBuffer("");
        sb.append(blankStrBeforeVersion).append(getTab(tab )).append("<exclusion>\n");
        sb.append(blankStrBeforeVersion).append(getTab(tab + 1)).append("<groupId>").append(excludeJar.getGroup()).append("</groupId>\n");
        sb.append(blankStrBeforeVersion).append(getTab(tab  + 1)).append("<artifactId>").append(excludeJar.getArtifactId()).append("</artifactId>\n");
        sb.append(blankStrBeforeVersion).append(getTab(tab )).append("</exclusion>\n");
        return sb.toString();
    }


    private static void sleep(){
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
