package util;

import java.io.File;
import java.io.IOException;


/**
 * �ļ�ת������
 * ��.vm��׺��xml�ļ�����ת��Ϊxml�ļ�, ���滻autoconfig��ռλ��${xxx_xxx_xxx}Ϊ��׼��ռλ��${xxx.xxx.xxx}
 *
 * @author : gangpeng.wgp
 * @time: 17/9/17
 */
public class UnJar {
    public static String outputPath;

    private static int i = 0;

    /**
     * ����ת��
     *
     * @param sourcePath Ҫ�滻���ļ���Ŀ¼
     */
    public static void unJar(String sourcePath, String outputPath) {
        try {
            if (StringUtil.isEmpty(sourcePath)) {
                System.out.println("��Ŀ¼����Ϊ�գ�");
                return;
            }
            System.out.println("��ʼ��ѹjar��");
            UnJar.outputPath = outputPath;
            File rootFile = new File(sourcePath);
            if (rootFile.exists()) {
                if (rootFile.isDirectory()) {
                    forFileDirectory(rootFile);
                } else if (rootFile.isFile()) {
                    load(rootFile);
                }
            }
            System.out.println("\n��ѹjar�����");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void forFileDirectory(File rootFile) throws Exception {
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
     * �����ļ�ת��
     *
     * @param file
     * @throws IOException
     */
    public static void load(File file) throws Exception {
        String fileNamePath = file.getAbsolutePath();
        if (!fileNamePath.endsWith(".jar")) {
            return;
        }

        File unJarDir = new File(UnJar.outputPath + file.getName() + "/");
        JarUtils.unJar(file, unJarDir);

        i++;
        if (i % 10 == 0) {
            System.out.print(".");
        }
        //System.out.println("��ѹ��" + file.getAbsolutePath());

    }

}
