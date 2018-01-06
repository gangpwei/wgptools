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
 * 按类型统计应用里的文件数量
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
     * 批量转换
     * @param sourcePath 要替换的文件或目录
     */
    public static void process(String sourcePath, String type) {
        fileCountMap = new TreeMap<String, Integer>();

        try {
            if (StringUtil.isEmpty(sourcePath)) {
                System.out.println("根目录不能为空！");
                return;
            }
            File rootFile = new File(sourcePath);
            if (! rootFile.exists()) {
                System.out.println("根目录不存在！");
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
        // 升序比较器
        Comparator<Entry<String, Integer>> valueComparator = new Comparator<Entry<String,Integer>>() {
            @Override
            public int compare(Entry<String, Integer> o1,
                               Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        };

        // map转换成list进行排序
        List<Entry<String, Integer>> list = new ArrayList<Entry<String,Integer>>(fileCountMap.entrySet());

        // 排序
        Collections.sort(list,valueComparator);

        int totalCount = 0;
        for (Entry<String, Integer> entry : list) {
            totalCount = totalCount + entry.getValue();
        }
        System.out.println("\n------------" + folderName + "中" + type + "文件数量统计, 总数：" + totalCount + "个--------------------");
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
     * 单个文件转换
     * @param file
     * @throws IOException
     */
    public static void state(File file, String type, String folder) throws IOException {

        if(file.getName().endsWith(type)){
            fileCountMap.put(folder, fileCountMap.get(folder) + 1);
        }
    }

}
