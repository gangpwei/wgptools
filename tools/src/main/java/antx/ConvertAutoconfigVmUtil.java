package antx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import util.StringUtil;

/**
 * �ļ�ת������
 * ��.vm��׺��xml�ļ�����ת��Ϊxml�ļ�, ���滻autoconfig��ռλ��${xxx_xxx_xxx}Ϊ��׼��ռλ��${xxx.xxx.xxx}
 * @author : gangpeng.wgp
 * @time: 17/9/17
 */
public class ConvertAutoconfigVmUtil {

    public static void main(String[] args) throws IOException {

        ConvertAutoconfigVmUtil.batchConvert("/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/src/webroot/META-INF/autoconf/", "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/src/webroot/META-INF/autoconf2/");

    }

    /**
     * ����ת��
     * @param sourcePath Ҫ�滻���ļ���Ŀ¼
     * @param outputPath �滻����������Ŀ¼
     */
    public static void batchConvert(String sourcePath, String outputPath) {
        try {
            if (StringUtil.isEmpty(sourcePath)) {
                System.out.println("��Ŀ¼����Ϊ�գ�");
                return;
            }
            File rootFile = new File(sourcePath);
            if (rootFile.exists()) {
                if (rootFile.isDirectory()) {
                    forFileDirectory(rootFile, sourcePath, outputPath);
                } else if (rootFile.isFile()) {
                    convert(rootFile, sourcePath, outputPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void forFileDirectory(File rootFile, String rootPath, String outputPath) throws IOException {
        File[] files = rootFile.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                convert(file, rootPath, outputPath);
            } else if (file.isDirectory()) {
                forFileDirectory(file, rootPath, outputPath);
            }
        }
    }

    /**
     * �����ļ�ת��
     * @param oldFile
     * @param rootPath
     * @param outputPath
     * @throws IOException
     */
    public static void convert(File oldFile, String rootPath, String outputPath) throws IOException {
        if(oldFile.getName().lastIndexOf(".vm") == -1){
            return;
        }

        BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(oldFile), "UTF-8"));
        String line;

        File newFile = new File(oldFile.getAbsolutePath().replace(rootPath, outputPath).replace(".vm", ""));
        File parentDir = new File(newFile.getParent());
        if(!parentDir.exists()){
            parentDir.mkdirs();
        }
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), "UTF-8"));

        while ((line = bReader.readLine()) != null) {
            if(line.length()>0){
                if(line.trim().startsWith("##")){
                    continue;
                }
                if(line.contains("${") && line.indexOf("}") > 0){
                    String token = line.substring(line.indexOf("${") + 2, line.indexOf("}"));
                    //System.out.println(token);
                    String tokenFormat = token.replaceAll("_", ".");
                    line = line.replace(token, tokenFormat);
                }
            }
            out.write(line);
            out.newLine();
        }
        out.flush();
        out.close();
        bReader.close();

        //String oldFileName = oldFile.getName();
        //oldFile.delete();
        //newFile.renameTo(oldFile);
        System.out.println("��ɣ�" + oldFile.getAbsolutePath());

    }

}
