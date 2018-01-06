import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import util.StringUtil;

/**
 * ������ͳ��Ӧ������ļ�����
 * @author : gangpeng.wgp
 * @time: 17/11/29
 */
public class FileTypeStatisticsUtil {

    
    static TreeMap<String, Integer> fileCountMap = new TreeMap<String, Integer>();

    static String[] specailFolder = new String[]{"biz"};

    static String rootPath;

    public static void main(String[] args) throws IOException {

        try {

            FileTypeStatisticsUtil.process("/Users/weigangpeng/IdeaProjects/aegean_home/shixi/", "java");
            //FileTypeStateUtil.process("/Users/weigangpeng/IdeaProjects/aegean_home/shixi/biz", "java");

            FileTypeStatisticsUtil.process("/Users/weigangpeng/IdeaProjects/aegean_home/shixi/", "xml");
            //FileTypeStateUtil.process("/Users/weigangpeng/IdeaProjects/aegean_home/shixi/biz", "xml");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    /**
     * ����ת��
     * @param sourcePath Ҫ�滻���ļ���Ŀ¼
     */
    public static void process(String sourcePath, String type) {
        fileCountMap = new TreeMap<String, Integer>();

        try {
            if (StringUtil.isEmpty(sourcePath)) {
                System.out.println("��Ŀ¼����Ϊ�գ�");
                return;
            }
            File rootFile = new File(sourcePath);
            if (! rootFile.exists()) {
                System.out.println("��Ŀ¼�����ڣ�");
                return;
            }
            if(!sourcePath.endsWith("/")){
                rootPath = sourcePath + "/";
            }else{
                rootPath = sourcePath;
            }

            forFileDirectory(rootFile, true,  type, null);

            sort(rootFile.getName(), type);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sort(String folderName, String type) {
        // ����Ƚ���
        Comparator<Entry<String, Integer>> valueComparator = new Comparator<Entry<String,Integer>>() {
            @Override
            public int compare(Entry<String, Integer> o1,
                               Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        };

        // mapת����list��������
        List<Entry<String, Integer>> list = new ArrayList<Entry<String,Integer>>(fileCountMap.entrySet());

        // ����
        Collections.sort(list,valueComparator);

        int totalCount = 0;
        for (Entry<String, Integer> entry : list) {
            totalCount = totalCount + entry.getValue();
        }
        System.out.println("\n------------" + folderName + "��" + type + "�ļ�����ͳ��, ������" + totalCount + "��--------------------");
        for (Entry<String, Integer> entry : list) {
            System.out.println(String.format("%1$-15s",entry.getKey()) + entry.getValue());
        }
    }

    public static void forFileDirectory(File rootFile, boolean isRoot , String type, String folder) throws IOException {
        File[] files = rootFile.listFiles();
        for (File file : files) {
            if( file.getName().startsWith(".") || file.getName().startsWith("APP")){
               continue;
            }
            if (file.isFile() && !isRoot) {
                state(file, type, folder);
            } else if (file.isDirectory() ) {
                if(isRoot && !isSpecialFolder(rootPath, file)){
                    folder = file.getName();
                    fileCountMap.put(folder, 0);
                }

                if(isSpecialFolder(rootPath, file)){
                    forFileDirectory(file, true, type, folder);
                }else{
                    forFileDirectory(file, false, type, folder);
                }
            }
        }
    }

    private static boolean isSpecialFolder(String root, File file) {
        for (String s : specailFolder) {
            if(file.getAbsolutePath().equals(root +s)){
                return true;
            }
        }
        return false;
    }

    /**
     * �����ļ�ת��
     * @param file
     * @throws IOException
     */
    public static void state(File file, String type, String folder) throws IOException {

        if(file.getName().endsWith(type)){
            fileCountMap.put(folder, fileCountMap.get(folder) + 1);
        }
    }

}
