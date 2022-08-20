package jar;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CollectionUtils;
import util.file.FileUtil;
import util.StringUtil;

import static util.StringUtil.formatLen;
import static util.StringUtil.getTab;

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

    private static final Logger log = LoggerFactory.getLogger(JarTreeAnlayzeUtil.class);

    public static final String V_LINE = "|  ";
    public static final String INFO = "[INFO]";
    public static final String INCLUDE_TAG = "+- ";
    public static final String EXCLUDE_TAG = "\\- ";

    /**
     * ?获得jar包依赖树形结构。 从mvn dependency:tree 日志中分析而来
     *
     * @param mvnTreeFile
     * @return
     */
    public static List<Jar> getJarDependencyTree(String mvnTreeFile) {
        List<Jar> result = new ArrayList<>();
        int lineNumber = 0;
        try {
            Jar tempJar = null;
            //读取文件，每一行放入list中
            List<String> lineList = FileUtil.readAllLines(mvnTreeFile);

            for (String line : lineList) {
                lineNumber += 1;
                if (lineNumber == 411) {
                    int x = 0;
                }
                if (line.length() > 0) {
                    if(line.contains("BUILD FAILURE")){
                        throw new RuntimeException("maven 编译工程错误，请检查：" + mvnTreeFile);
                    }
                    if (!line.contains(":jar:") && !line.contains(":war:") && !line.contains(":libd:") && !line.contains(":pom:")) {
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
                    if (StringUtil.isEmpty(startTag)) {
                        continue;
                    }

                    String newline = line.substring(line.indexOf(startTag) + startTag.length(), line.length());
                    String[] versions = newline.split(":");
                    String group = versions[0];
                    String artifact = versions[1];
                    String version = versions[3];
                    Jar jar = new Jar(group, artifact, version);

                    if (lineNumber == 52) {
                        int y = 0;
                    }
                    int level = getLevel(line);

                    jar.setDependencyLevel(level);

                    if (tempJar != null && level > 0) {
                        Jar parentJar = getParentJar(tempJar, level);

                        if (parentJar == null) {
                            System.err.println("找不到parent：" + tempJar);
                            throw new RuntimeException("找不到parent：" + tempJar + ", Line: " + lineNumber);
                        } else {
                            jar.setParent(parentJar);
                        }
                        parentJar.addChild(jar);
                    }

                    tempJar = jar;
                    if (jar.getDependencyLevel() == 0) {
                        result.add(jar);
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析maven tree日志失败，lineNumber:" + lineNumber, e);
        }

        return result;
    }

    /**
     * maven tree 日志获取日志级别
     *
     * @param line
     * @return
     */
    private static int getLevel(String line) {
        try {
            if (!line.startsWith(INFO)) {
                return 0;
            }

            int endIndex;

            if (line.contains(INCLUDE_TAG)) {
                endIndex = line.indexOf(INCLUDE_TAG);
            } else if (line.contains(EXCLUDE_TAG)) {
                endIndex = line.indexOf(EXCLUDE_TAG);
            } else {
                return 0;
            }

            line = line.substring(INFO.length(), endIndex);
            int level = (line.length() + 2) / 3;
            return level;
        } catch (Exception e) {
            log.error("getLevel error, line = " + line, e);
        }
        return 0;
    }

    //private static int getLevel(String line, boolean include, Jar jar) {
    //    int vLineAppearCount = appearNumber(line, V_LINE);
    //    int slevel = line.contains(INCLUDE_TAG) || line.contains(EXCLUDE_TAG) ? 1 : 0;
    //    jar.setInclude(slevel == 0 || include);
    //    return vLineAppearCount + slevel;
    //}

    private static Jar getParentJar(Jar tempJar, int level) {

        Jar parentJar = null;
        if (level == tempJar.getDependencyLevel() + 1) {
            parentJar = tempJar;
        } else if (level == tempJar.getDependencyLevel()) {
            parentJar = tempJar.getParent();
        //} else if (level < tempJar.getDependencyLevel()) {
        } else {
            while (tempJar.getParent() != null && level <= tempJar.getParent().getDependencyLevel()) {
                tempJar = tempJar.getParent();
            }
            parentJar = tempJar.getParent();
        }
        return parentJar;
    }

    /**
     * 得到显性引入的父级jar
     *
     * @param jar
     * @return
     */
    private static Jar getRootJar(Jar jar) {

        Jar parentJar = null;
        if (1 == jar.getDependencyLevel()) {
            parentJar = jar;
        }
        if (1 < jar.getDependencyLevel()) {
            while (jar.getParent() != null && 1 < jar.getParent().getDependencyLevel()) {
                jar = jar.getParent();
            }
            parentJar = jar.getParent();
        }
        return parentJar;
    }

    /**
     * 生成去除没用jar的pom配置
     *
     * @param unusedJarList 没用的jar
     * @param jarTreeList   jar依赖树
     */
    public static void generateExcludePom(List<String> unusedJarList, List<Jar> jarTreeList) {
        for (Jar jar : jarTreeList) {
            printJar(unusedJarList, jar);
        }

        System.out.println("\n");
        for (Jar jar : jarTreeList) {
            initExcludeJar(jar);
        }

        for (Jar jar : jarTreeList) {
            if (jar.getDependencyLevel() == 0) {
                if (CollectionUtils.isNotEmpty(jar.getChildren())) {
                    for (Jar level1Jar : jar.getChildren()) {
                        if (CollectionUtils.isNotEmpty(level1Jar.getUnusedChildren())) {
                            //System.out.println(level1Jar.toPomString());
                        }
                    }
                }
            }
        }
    }

    /**
     * jar自己没有被用到、子jar也被没用、而父节点有用到， 放到exclude中
     *
     * @param jar
     */
    private static void initExcludeJar(Jar jar) {
        //&& jar.getVersion().equals("0.2.8.0")
        if (jar.getArtifactId().equals("xml.apis.css")) {
            int i = 0;
        }
        boolean isAllChildUnUsed = isAllChildUnUsed(jar);
        //if(!jar.isUsed() && isAllChildUnUsed && jar.getParent() != null && jar.getParent().isUsed() && jar.isInclude()){

        //jar自己没有被用到、子jar也被没用、而父节点有用到
        if (!jar.isUsed()) {

            if (jar.getArtifactId().contains("tpsc.client")) {
                int x = 0;
            }
            Jar rootJar = getRootJar(jar);
            if (rootJar == null) {
                rootJar = getRootJar(jar);
                System.err.println("找不到rootJar：" + jar);
            }
            rootJar.addUnUsedChild(jar);
            if (jar.getArtifactId().equals("xml.apis.css")) {
                System.out.println("root jar :" + rootJar.getFileName() + ", exclude:" + jar.getFileName());

            }
            if (isAllChildUnUsed && jar.getParent() != null && jar.getParent().isUsed()) {
                return;
            }
        }
        if (jar.getChildren() != null) {
            for (Jar child : jar.getChildren()) {
                initExcludeJar(child);
            }
        }
    }

    /**
     * 判断jar的所有子节点是否都没用
     *
     * @param jar
     * @return
     */
    private static boolean isAllChildUnUsed(Jar jar) {
        if (!jar.isInclude()) {
            return true;
        }

        boolean result = true;
        if (jar.getChildren() != null) {
            for (Jar child : jar.getChildren()) {
                boolean isAllChildUnUsed = isAllChildUnUsed(child);
                //if(child.isUsed() || (!isAllChildUnUsed && result)){
                if ((child.isUsed() && child.isInclude()) || (!isAllChildUnUsed && result)) {
                    result = false;
                }
            }
        }
        return result;
    }

    private static void printJar(List<String> unusedJarFile, Jar jar) {
        final StringBuffer sb = new StringBuffer(getTab(jar.getDependencyLevel() * 2));
        sb.append(jar.getDependencyLevel());
        sb.append(jar.isInclude() ? " + " : " - ");
        sb.append(formatLen(jar.getGroup(), 25)).append(" ");
        sb.append(formatLen(jar.getArtifactId(), 25)).append("  ");
        sb.append(formatLen(jar.getVersion(), 10));
        //sb.append(", pareant=").append(pareant);
        //sb.append(", children=").append(children);
        String line = sb.toString();
        if (jar.getArtifactId().equals("snappy-java")) {
            int i=0;
        }
        if (unusedJarFile.contains(jar.getFileName())) {
            System.err.println(line);
            jar.setUsed(false);
            sleep();
        } else {
            System.out.println(line);
            sleep();
            jar.setUsed(true);
        }

        if (jar.getChildren() != null) {
            for (Jar child : jar.getChildren()) {
                printJar(unusedJarFile, child);
            }
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
