package util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author weigangpeng
 * @date 2017/12/21 ����8:24
 */

public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    /**
     * �ļ�ת��
     * @param filePath
     * @return
     */
    public static List<String> readAllLines(String filePath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("�ļ������ڣ�" + filePath);
            return lines;
        }

        String fileEncode = "UTF-8";
        try {
            //fileEncode = FileCharsetDetector.guessFileEncoding(filePath);
            fileEncode =CharacterEncoding.getFileCharacterEnding(filePath);
        } catch (Exception e) {
            log.error("��ȡ�ļ�����ʧ�ܣ� file = " + filePath, e);
        }

        //log.info("filePath: " + filePath + ", encoding: " + fileEncode );

        try {
            lines = Files.readAllLines(Paths.get(filePath),  Charset.forName(fileEncode));
        } catch (Exception e) {
            log.error("�ļ���ȡ�������쳣��filePath: " + filePath + ", encoding: " + fileEncode , e);
        }
        return lines;
    }

    /**
     * ���ַ���д���ļ���
     * @param file
     * @param content
     * @return
     */
    public static boolean writeFile(String file, String content){
        String fileEncode = "UTF-8";
        writeFile(file, content, fileEncode);
        return true;
    }

    /**
     * ���ַ���д���ļ���
     * @param file
     * @param content
     * @return
     */
    public static boolean writeFile(String file, String content, String charSet){
        try {
            Files.write(Paths.get(file),content.getBytes(charSet), StandardOpenOption.CREATE);
        } catch (Exception e) {
            log.error("д�ļ��쳣��" + file , e);
            return false;
        }
        return true;
    }

    /**
     * ���ַ���д���ļ���
     * @param file
     * @param lines
     * @return
     */
    public static boolean writeFile(String file, List<String> lines){
        try {
            Files.write(Paths.get(file), lines, StandardOpenOption.CREATE);
        } catch (Exception e) {
            log.error("д�ļ��쳣��" + file , e);
            return false;
        }
        return true;
    }

    public static boolean copyFile(String oldFile, String newFile, FileLineReplacer fileLineReplacer){
        File file = new File(oldFile);
        if (!file.exists()) {
            log.error("�ļ������ڣ�" + oldFile);
            return false;
        }


        List<String> lines = readAllLines(oldFile);

        StringBuffer buffer = new StringBuffer();
        int x = 0;
        for (String line : lines) {
            x += 1;
            String newLine = fileLineReplacer.filter(line);
            if(newLine == null){
                continue;
            }
            buffer.append(newLine);

            if(x < lines.size()){
                buffer.append("\n");
            }
        }

        return writeFile(newFile, buffer.toString());
    }

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

    /**
     * ɾ���ļ���
     * @param folderPath
     */
    public static void delFolder(String folderPath) {
        try {
            //ɾ����������������
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            //ɾ�����ļ���
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ɾ��ָ���ļ����������ļ�
     * @param path
     * @return
     */
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
                //��ɾ���ļ���������ļ�
                delAllFile(path + "/" + tempList[i]);
                //��ɾ�����ļ���
                delFolder(path + "/" + tempList[i]);
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
            //�ļ�����ʱ
            if (oldfile.exists()) {
                //����ԭ�ļ�
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    //�ֽ��� �ļ���С
                    bytesum += byteread;
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
