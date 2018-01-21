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
 * �ļ�ת������
 * ��.vm��׺��xml�ļ�����ת��Ϊxml�ļ�, ���滻autoconfig��ռλ��${xxx_xxx_xxx}Ϊ��׼��ռλ��${xxx.xxx.xxx}
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
                //����������
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
     * ����ת��
     * @param sourcePath Ҫ�滻���ļ���Ŀ¼
     */
    public static void batchProcess(String sourcePath) {
        String excludePath = sourcePath + "/target";
        try {
            if (StringUtil.isEmpty(sourcePath)) {
                System.out.println("��Ŀ¼����Ϊ�գ�");
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
     * �����ļ�ת��
     * @param file
     * @throws IOException
     */
    public static void process(File file) throws IOException {
        allFileList.add(file);
    }

}
