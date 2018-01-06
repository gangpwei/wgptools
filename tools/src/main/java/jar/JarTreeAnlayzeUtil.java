package jar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import util.CollectionUtils;

import static util.StringUtil.*;

/**
 * jar包分析工具，用来生产去除没用的jar的pom配置
 *
 * 第一步，通过jar包分析agent工具在线上运行，生成用到的jar日志
 * 第二步，编译本地代码，得到所有的jar包
 * 第三步，通过JarAnlayzeUtil分析第一步日志，再比对第二步所有的jar，即可得出没用的jar
 * 第四步，手工执行 mvn dependency:tree 命令，生产maven依赖树日志
 * 第五步，通过本程序，通过第三步和第四部的结果，生产去除没用的jar的pom配置
 *
 * @author weigangpeng
 * @date 2017/12/08 下午2:06
 */

public class JarTreeAnlayzeUtil {

    public static final String V_LINE = "|  ";
    public static final String INCLUDE_TAG = "+- ";
    public static final String EXCLUDE_TAG = "\\- ";

    /**
     * ?获得jar包依赖树形结构。 从mvn dependency:tree 日志中分析而来
     * @param mvnTreeFile
     * @return
     */
    public static List<Jar> getJarDependencyTree(String mvnTreeFile) {
        List<Jar> result = new ArrayList<>();
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(mvnTreeFile), "UTF-8"));
            String line;
            int lineNumber = 0;
            Jar tempJar = null;
            while ((line = bReader.readLine()) != null) {
                lineNumber += 1;
                if (line.length() > 0) {
                    if (!line.contains(":jar:") && !line.contains(":libd:") ) {
                        continue;
                    }

                    boolean include = true;
                    String startTag = null;
                    if (line.contains(INCLUDE_TAG)) {
                        startTag = INCLUDE_TAG;
                    } else if (line.contains(EXCLUDE_TAG)) {
                        startTag = EXCLUDE_TAG;
                        //include = false;
                    } else if (!line.contains(":compile") && line.contains("[INFO] ")) {
                        startTag = "[INFO] ";
                    }

                    String newline = line.substring(line.indexOf(startTag) + startTag.length(), line.length());
                    String[] versions = newline.split(":");
                    String group = versions[0];
                    String artifact = versions[1];
                    String version = versions[3];
                    Jar jar = new Jar(group, artifact, version);

                    if(lineNumber == 368){
                        int y = 0;
                    }
                    int vLineAppearCount = appearNumber(line, V_LINE);
                    int slevel = line.contains(INCLUDE_TAG) || line.contains(EXCLUDE_TAG) ? 1 : 0;
                    jar.setInclude(slevel == 0 || include);
                    int level = vLineAppearCount + slevel;
                    jar.setDependencyLevel(level);


                    if(tempJar != null && level > 0){
                        Jar parentJar = getParentJar(tempJar, level);

                        if(parentJar == null) {
                            System.err.println("找不到parent：" + tempJar);
                            throw new RuntimeException("找不到parent：" + tempJar);
                        }else {
                            jar.setParent(parentJar);
                        }
                        parentJar.addChild(jar);
                    }

                    tempJar = jar;
                    if(jar.getDependencyLevel() == 0){
                        result.add(jar);
                    }

                }

            }
            bReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static Jar getParentJar(Jar tempJar, int level) {

        Jar parentJar = null;
        if(level == tempJar.getDependencyLevel() + 1){
            parentJar = tempJar;
        }else if(level == tempJar.getDependencyLevel()){
            parentJar = tempJar.getParent();
        }else if(level < tempJar.getDependencyLevel()){
            while (tempJar.getParent() != null && level <= tempJar.getParent().getDependencyLevel()){
                tempJar = tempJar.getParent();
            }
            parentJar = tempJar.getParent();
        }
        return parentJar;
    }

    /**
     * 得到显性引入的父级jar
     * @param jar
     * @return
     */
    private static Jar getRootJar(Jar jar) {

        Jar parentJar = null;
        if(1 == jar.getDependencyLevel()){
            parentJar = jar;
        }if(1 < jar.getDependencyLevel()){
            while (jar.getParent() != null && 1 < jar.getParent().getDependencyLevel()){
                jar = jar.getParent();
            }
            parentJar = jar.getParent();
        }
        return parentJar;
    }

    /**
     * 生成去除没用jar的pom配置
     * @param unusedJarList 没用的jar
     * @param jarTreeList   jar依赖树
     */
    public static void generateExcludePom(List<String> unusedJarList, List<Jar> jarTreeList){
        for (Jar jar : jarTreeList) {
            printJar(unusedJarList, jar);
        }

        System.out.println("\n");
        for (Jar jar : jarTreeList) {
            initExcludeJar(jar);
        }

        for (Jar jar : jarTreeList) {
            if(jar.getDependencyLevel() == 0){
                if(CollectionUtils.isNotEmpty(jar.getChildren())){
                    for (Jar level1Jar : jar.getChildren()) {
                        if(CollectionUtils.isNotEmpty(level1Jar.getUnusedChildren())){
                            //System.out.println(level1Jar.toPomString());
                        }
                    }
                }
            }
        }
    }

    /**
     * jar自己没有被用到、子jar也被没用、而父节点有用到， 放到exclude中
     * @param jar
     */
    private static void initExcludeJar(Jar jar) {
//&& jar.getVersion().equals("0.2.8.0")
        if(jar.getArtifactId().equals("xml.apis.css") ){
            int i=0;
        }
        boolean isAllChildUnUsed = isAllChildUnUsed(jar);
        //if(!jar.isUsed() && isAllChildUnUsed && jar.getParent() != null && jar.getParent().isUsed() && jar.isInclude()){

        //jar自己没有被用到、子jar也被没用、而父节点有用到
        if(!jar.isUsed() && isAllChildUnUsed && jar.getParent() != null && jar.getParent().isUsed() && jar.isInclude()){

            Jar rootJar = getRootJar(jar);
            if(rootJar == null) {
                rootJar = getRootJar(jar);
                System.err.println("找不到rootJar：" + jar);
            }
            rootJar.addUnUsedChild(jar);
            if(jar.getArtifactId().equals("xml.apis.css") ){
                System.out.println("root jar :" + rootJar.getFileName() + ", exclude:" + jar.getFileName() );

            }
            return;
        }
        if(jar.getChildren() != null){
            for (Jar child : jar.getChildren()) {
                initExcludeJar(child);
            }
        }
    }

    /**
     * 判断jar的所有子节点是否都没用
     * @param jar
     * @return
     */
    private static boolean isAllChildUnUsed(Jar jar) {
        if(!jar.isInclude()){
            return true;
        }

        boolean result = true;
        if(jar.getChildren() != null ){
            for (Jar child : jar.getChildren()) {
                boolean isAllChildUnUsed = isAllChildUnUsed(child);
                //if(child.isUsed() || (!isAllChildUnUsed && result)){
                if((child.isUsed() && child.isInclude()) || (!isAllChildUnUsed && result)){
                        result = false ;
                }
            }
        }
        return result;
    }

    private static void printJar(List<String> unusedJarFile, Jar jar) {
        final StringBuffer sb = new StringBuffer(getTab(jar.getDependencyLevel()));
        sb.append(jar.isInclude() ? " + " : " - ");
        sb.append(formatLen(jar.getGroup(), 25)).append(" ");
        sb.append(formatLen(jar.getArtifactId(), 25)).append("  ");
        sb.append(formatLen(jar.getVersion(), 10));
        //sb.append(", pareant=").append(pareant);
        //sb.append(", children=").append(children);
        String line = sb.toString();
        if(jar.getArtifactId().equals("xml.apis.css")){
            //int i=0;
        }
        if(unusedJarFile.contains(jar.getFileName())){
            System.err.println(line);
            jar.setUsed(false);
            sleep();
        }else{
            System.out.println(line);
            sleep();
            jar.setUsed(true);
        }
        
        if(jar.getChildren() != null){
            for (Jar child : jar.getChildren()) {
                printJar(unusedJarFile, child);
            }
        }
    }

    private static void sleep(){
        //try {
        //    Thread.sleep(10L);
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
    }

}
