package util;

import java.io.File;
import java.io.IOException;


/**
 * 文件转换工具
 * 把.vm后缀的xml文件批量转换为xml文件, 并替换autoconfig的占位符${xxx_xxx_xxx}为标准的占位符${xxx.xxx.xxx}
 *
 * @author : gangpeng.wgp
 * @time: 17/9/17
 */
public class UnJar {
    public static String outputPath;

    private static int i = 0;

    /**
     * 批量转换
     *
     * @param sourcePath 要替换的文件或目录
     */
    public static void unJar(String sourcePath, String outputPath) {
        try {
            if (StringUtil.isEmpty(sourcePath)) {
                System.out.println("根目录不能为空！");
                return;
            }
            System.out.println("开始解压jar包");
            UnJar.outputPath = outputPath;
            File rootFile = new File(sourcePath);
            if (rootFile.exists()) {
                if (rootFile.isDirectory()) {
                    forFileDirectory(rootFile);
                } else if (rootFile.isFile()) {
                    load(rootFile);
                }
            }
            System.out.println("\n解压jar包完成");
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
     * 单个文件转换
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
        //System.out.println("解压：" + file.getAbsolutePath());

    }

}
