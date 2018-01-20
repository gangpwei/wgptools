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
 * �ҵ�Ӧ����autoconfig��û�õ�������, ������һ���µ�auto-config.xml�ļ�
 *
 * ԭ����war����������ļ�������jar������ѹ��һ����ʱ�ļ��С�Ȼ�����������õ���������������ܵ����������Ƚϣ�ʣ�µľ���û�õġ�
 * @author : gangpeng.wgp
 * @time: 17/9/17
 */
public class FindUnsedAutoconfigUtil {

    public static HashMap<String, String> autoconfigMap;

    public static HashMap<String, String> fileStrMap = new HashMap<String, String>();

    public static List<String> unusedKeyList = new ArrayList<String>();
    /**
     * ������������
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
     * �ҵ�Ӧ����autoconfig��û�õ�������, ������һ���µ�auto-config.xml�ļ�
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


        //��ѹjar��
        //UnJar.unJar(warPath, getOutputPath(warPath));

        System.out.println("\n��ʼ����������������������:");
        FindUnsedAutoconfigUtil.loadAllFile(getOutputPath(warPath));

        //�ѽ�ѹjar������ʱ�ļ���ɾ��
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
        System.out.println("\n��������������������:");

        for (String key : usedInLibKeyList) {
            System.out.println(key);
        }

        List<String> dependencyByOtherKeyList = getDependencyByOtherKeyList(antxPropertiesFilePath);
        System.out.println("\n������������������������:");

        for (String key : dependencyByOtherKeyList) {
            System.out.println(key);
        }

        System.out.println("\n��ʼ����xml����");
        fileStrMap = new HashMap<>();

        FindUnsedAutoconfigUtil.loadAllFile(warPath);

        for (Entry<String, String> configMap : autoconfigMap.entrySet()) {
            //�������������������� ������ ������������������������ ����
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

        System.out.println("\nû�б����õ�������:");
        for (String key : unusedKeyList) {
            System.out.println(key);
        }
        AutoconfigLoader.getNewConfigFile(autoconfigFilePath, unusedKeyList, true);

        System.out.println("\n����������:" + autoconfigMap.size() + ", ����������������������: " + usedInLibKeyList.size() + ", ������������������������: " + dependencyByOtherKeyList.size() + ", û�б���������������: " + unusedKeyList.size()+"\n");


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
     * ��Ŀ¼�µ������ļ����ݶ�����fileStrMap��
     *
     * @param sourcePath �ļ���Ŀ¼
     */
    public static void loadAllFile(String sourcePath) {
        try {
            if (StringUtil.isEmpty(sourcePath)) {
                System.out.println("��Ŀ¼����Ϊ�գ�");
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
     * ���ص����ļ�
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
        //System.out.println("��ɣ�" + file.getAbsolutePath());

    }

    /**
     * �ж����е��ļ���Ƿ����������
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
