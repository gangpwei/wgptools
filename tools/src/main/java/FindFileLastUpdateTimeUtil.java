import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import util.DateUtil;
import util.StringUtil;

/**
 * 文件转换工具
 * 把.vm后缀的xml文件批量转换为xml文件, 并替换autoconfig的占位符${xxx_xxx_xxx}为标准的占位符${xxx.xxx.xxx}
 * @author : gangpeng.wgp
 * @time: 17/9/17
 */
public class FindFileLastUpdateTimeUtil {

    static List<File> allFileList = new ArrayList<>();


    public static void main(String[] args) throws IOException {

        FindFileLastUpdateTimeUtil.batchProcess("/Users/weigangpeng/IdeaProjects/aegean_home/trunk/biz/isearch");
        getLastModifiedFiles();
    }

    public static void getLastModifiedFiles(){

        Collections.sort(allFileList, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                //降序序排列
                if(o2.lastModified() > o1.lastModified()){
                    return 1;
                }
                if(o1.lastModified() == o2.lastModified()){
                    return 0;
                }
                return -1;
            }
        });

        for (File file : allFileList) {
            System.out.println(DateUtil.format(new Date(file.lastModified())) + "  " + file.getAbsolutePath());
        }
    }
    
    /**
     * 批量转换
     * @param sourcePath 要替换的文件或目录
     */
    public static void batchProcess(String sourcePath) {
        String excludePath = sourcePath + "/target";
        try {
            if (StringUtil.isEmpty(sourcePath)) {
                System.out.println("根目录不能为空！");
                return;
            }
            File rootFile = new File(sourcePath);
            if (rootFile.exists()) {

                if (rootFile.isDirectory() ) {
                    forFileDirectory(rootFile, excludePath);
                } else if (rootFile.isFile()) {
                    process(rootFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void forFileDirectory(File rootFile, String excludePath) throws IOException {
        File[] files = rootFile.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                process(file);
            } else if (file.isDirectory() && !rootFile.getAbsolutePath().equals(excludePath)) {
                forFileDirectory(file, excludePath);
            }
        }
    }

    /**
     * 单个文件转换
     * @param file
     * @throws IOException
     */
    public static void process(File file) throws IOException {
        allFileList.add(file);
    }

}
