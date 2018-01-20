package antx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import util.FileUtil;
import util.StringUtil;
import util.UnJar;

/**
 * 找到应用中autoconfig中没用的配置项, 并生成一个新的auto-config.xml文件
 *
 * 原理：把war包里的所有文件，包括jar包，解压到一个临时文件夹。然后搜索其中用到的所有配置项，和总的配置项做比较，剩下的就是没用的。
 * @author : gangpeng.wgp
 * @time: 17/9/17
 */
public class FindUnsedAutoconfigUtil {

    public static HashMap<String, String> autoconfigMap;

    public static HashMap<String, String> fileStrMap = new HashMap<String, String>();

    public static List<String> unusedKeyList = new ArrayList<String>();
    /**
     * 被二方库依赖
     */
    public static List<String> usedInLibKeyList = new ArrayList<String>();

    private static int i = 0;

    public static void main(String[] args) throws IOException {
        //String autoconfigFilePath = "/Users/weigangpeng/IdeaProjects/muses_new/banner/bundle/war/src/webroot/META-INF/autoconf/auto-config.xml";
        //String antxPropertiesFilePath = "/Users/weigangpeng/IdeaProjects/muses_new/banner/ztemp/muses.properties";
        //String warPath = "/Users/weigangpeng/IdeaProjects/muses_new/banner/bundle/war/target/muses.war";

        String autoconfigFilePath = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/src/webroot/META-INF/autoconf/auto-config.xml";
        String antxPropertiesFilePath = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/ztemp/aegean.properties";
        String warPath = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/target/aegean.war";
        warPath = "/Users/weigangpeng/libtemp/";
        process(autoconfigFilePath, antxPropertiesFilePath, warPath);
    }

    /**
     * 找到应用中autoconfig中没用的配置项, 并生成一个新的auto-config.xml文件
     *
     * @param autoconfigFilePath
     * @param warPath
     * @throws IOException
     */
    public static void process(String autoconfigFilePath, String antxPropertiesFilePath, String warPath) throws IOException {
        //autoconfigMap = AutoconfigLoader.parserXml(autoconfigFilePath);

        autoconfigMap = AutoconfigLoader.getConfigFromPropertiesFile(antxPropertiesFilePath);
//TODO
    String temp = "/Users/weigangpeng/aegean_temp/";
    UnJar.unJar(warPath, temp);
    if(1==1){
        return  ;
    }


        //解压jar包
        //UnJar.unJar(warPath, getOutputPath(warPath));

        System.out.println("\n开始分析二方库依赖的配置项:");
        FindUnsedAutoconfigUtil.loadAllFile(getOutputPath(warPath));

        //把解压jar包的临时文件夹删除
        //removeOutputPath(warPath);

        for (Entry<String, String> configMap : autoconfigMap.entrySet()) {
            if (isContains(configMap)) {
                usedInLibKeyList.add(configMap.getKey());
            }
            
            i++;
            if (i % 10 == 0) {
                System.out.print(".");
            }
        }
        System.out.println("\n被二方库依赖的配置项:");

        for (String key : usedInLibKeyList) {
            System.out.println(key);
        }

        List<String> dependencyByOtherKeyList = getDependencyByOtherKeyList(antxPropertiesFilePath);
        System.out.println("\n被其他配置项依赖的配置项:");

        for (String key : dependencyByOtherKeyList) {
            System.out.println(key);
        }

        System.out.println("\n开始解析xml依赖");
        fileStrMap = new HashMap<>();

        FindUnsedAutoconfigUtil.loadAllFile(warPath);

        for (Entry<String, String> configMap : autoconfigMap.entrySet()) {
            //被二方库依赖的配置项 跳过， 被其他配置项依赖的配置项 跳过
            if(usedInLibKeyList.contains(configMap.getKey()) || dependencyByOtherKeyList.contains(configMap.getKey())){
                continue;
            }
            if (!isContains(configMap)) {
                unusedKeyList.add(configMap.getKey());
            }
            i++;
            if (i % 10 == 0) {
                System.out.print(".");
            }
        }

        System.out.println("\n没有被引用的配置项:");
        for (String key : unusedKeyList) {
            System.out.println(key);
        }
        AutoconfigLoader.getNewConfigFile(autoconfigFilePath, unusedKeyList, true);

        System.out.println("\n配置项总数:" + autoconfigMap.size() + ", 二方库中引用配置项数量: " + usedInLibKeyList.size() + ", 被其他配置项依赖的配置项: " + dependencyByOtherKeyList.size() + ", 没有被引用配置项数量: " + unusedKeyList.size()+"\n");


    }

    private static List<String> getDependencyByOtherKeyList(String propertiesFile) {
        BufferedReader bReader = null;
        List<String> dependencyByOtherKeyList = new ArrayList<String>();
        try {
            bReader = new BufferedReader(new InputStreamReader(new FileInputStream(propertiesFile), "UTF-8"));
            String line;

            StringBuilder sb = new StringBuilder();

            while ((line = bReader.readLine()) != null) {
                if (line.length() > 0) {
                    sb.append(line);
                }
                if(line != null && line.contains("service-servlet")){
                    int x = 0;
                }
            }
            bReader.close();

            String allPropertiesStr = sb.toString();
            for (Entry<String, String> configMap : autoconfigMap.entrySet()) {
                if(allPropertiesStr.contains("${" + configMap.getKey() + "}")){
                    dependencyByOtherKeyList.add(configMap.getKey());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dependencyByOtherKeyList;
    }

    /**
     * 把目录下的所有文件内容都放入fileStrMap中
     *
     * @param sourcePath 文件或目录
     */
    public static void loadAllFile(String sourcePath) {
        try {
            if (StringUtil.isEmpty(sourcePath)) {
                System.out.println("根目录不能为空！");
                return;
            }

            File rootFile = new File(sourcePath);
            if (rootFile.exists()) {
                if (rootFile.isDirectory()) {
                    forFileDirectory(rootFile);
                } else if (rootFile.isFile()) {
                    load(rootFile);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getOutputPath(String sourcePath) {
        File rootFile = new File(sourcePath);
        return rootFile.getParentFile().getAbsolutePath() + "/temp/";
    }

    private static void removeOutputPath(String sourcePath) {
        FileUtil.delFolder(getOutputPath(sourcePath));
    }

    public static void forFileDirectory(File rootFile) throws IOException {
        File[] files = rootFile.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                load(file);
            } else if (file.isDirectory()) {
                forFileDirectory(file);
            }
        }
    }

    /**
     * 加载单个文件
     *
     * @param file
     * @throws IOException
     */
    public static void load(File file) throws IOException {
        String fileNamePath = file.getAbsolutePath();
        if (file.getName().equals("auto-config.xml") ||
            file.getName().equals("auto-config_new.xml") ||
            fileNamePath.contains(".idea/") ||
            fileNamePath.contains(".svn/") ||
            fileNamePath.endsWith(".java") ||
            fileNamePath.endsWith(".jar") ||
            fileNamePath.endsWith(".war") ||
            fileNamePath.endsWith(".xsb") ||
            fileNamePath.endsWith(".js") ||
            fileNamePath.endsWith("MANIFEST.MF") ||
            fileNamePath.endsWith(".class")) {
            return;
        }

        BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String line;

        StringBuilder sb = new StringBuilder();

        while ((line = bReader.readLine()) != null) {
            if (line.length() > 0) {
                sb.append(line);
                if(line != null && line.contains("service-servlet")){
                    int x = 0;
                }
            }


        }

        bReader.close();

        fileStrMap.put(file.getAbsolutePath(), sb.toString());
        //System.out.println("完成：" + file.getAbsolutePath());

    }

    /**
     * 判断所有的文件里，是否包含配置项
     * @param configMap
     * @return
     */
    private static boolean isContains(Entry<String, String> configMap) {
        for (Entry<String, String> file : fileStrMap.entrySet()) {
            if (containKey(file.getValue(), configMap.getKey())) {
                return true;
            }
        }
        return false;
    }

    private static boolean containKey(String value, String key) {
        String tokenFormat = key.replaceAll("\\.", "_");
        if (value.contains(key) || value.contains(tokenFormat)) {
            return true;
        }
        return false;
    }
}
