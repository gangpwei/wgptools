package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author weigangpeng
 * @date 2017/12/21 ����8:24
 */

public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);
    
    /**
     * ����byte�����ݴ�С��Ӧ���ı�
     *
     * @param size
     * @return
     */
    public static String getDataSize(long size) {
        DecimalFormat formater = new DecimalFormat("####");
        if (size < 1024) {
            return size + " bytes";
        } else if (size < 1024 * 1024) {
            float kbsize = size / 1024f;
            return formater.format(kbsize) + " KB";
        } else if (size < 1024 * 1024 * 1024) {
            float mbsize = size / 1024f / 1024f;
            return formater.format(mbsize) + " MB";
        } else if (size < 1024 * 1024 * 1024 * 1024) {
            float gbsize = size / 1024f / 1024f / 1024f;
            return formater.format(gbsize) + " GB";
        } else {
            return "size: error";
        }

    }

    //ɾ���ļ���
    //param folderPath �ļ�����������·��
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //ɾ����������������
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); //ɾ�����ļ���
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //ɾ��ָ���ļ����������ļ�
    //param path �ļ�����������·��
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//��ɾ���ļ���������ļ�
                delFolder(path + "/" + tempList[i]);//��ɾ�����ļ���
                flag = true;
            }
        }
        return flag;
    }

    /**
     * ���Ƶ����ļ�
     *
     * @param oldPath String ԭ�ļ�·�� �磺c:/fqf.txt
     * @param newPath String ���ƺ�·�� �磺f:/fqf.txt
     * @return boolean
     */
    public static boolean copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //�ļ�����ʱ
                InputStream inStream = new FileInputStream(oldPath); //����ԭ�ļ�
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //�ֽ��� �ļ���С
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }else{
                log.error("�ļ������ڣ�" + oldPath);
                return false;
            }
        } catch (Exception e) {
            System.out.println("���Ƶ����ļ���������");
            e.printStackTrace();
            return false;
        }
        return true;

    }
}
