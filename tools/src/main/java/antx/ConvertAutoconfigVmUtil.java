package antx;

import java.io.File;
import java.io.IOException;

import util.file.FileLineReplacer;
import util.file.FileUtil;
import util.StringUtil;

/**
 * 文件转换工具
 * 把.vm后缀的xml文件批量转换为xml文件, 并替换autoconfig的占位符${xxx_xxx_xxx}为标准的占位符${xxx.xxx.xxx}
 * @author : gangpeng.wgp
 * @time: 17/9/17
 */
public class ConvertAutoconfigVmUtil {

    public static void main(String[] args) throws IOException {

        ConvertAutoconfigVmUtil.batchConvert("/Users/weigangpeng/IdeaProjects/omega/bundle/war/src/webroot/META-INF/autoconf/", "/Users/weigangpeng/IdeaProjects/omega/bundle/war/src/webroot/META-INF/autoconf2/");
    }

    /**
     * 批量转换
     * @param sourcePath 要替换的文件或目录
     * @param outputPath 替换后的问题输出目录
     */
    public static void batchConvert(String sourcePath, String outputPath) {
        try {
            if (StringUtil.isEmpty(sourcePath)) {
                System.out.println("根目录不能为空！");
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
     * 单个文件转换
     * @param oldFile
     * @param rootPath
     * @param outputPath
     * @throws IOException
     */
    public static void convert(File oldFile, String rootPath, String outputPath) throws IOException {
        if(oldFile.getName().lastIndexOf(".vm") == -1){
            return;
        }


        File newFile = new File(oldFile.getAbsolutePath().replace(rootPath, outputPath).replace(".vm", ""));
        File parentDir = new File(newFile.getParent());
        if(!parentDir.exists()){
            parentDir.mkdirs();
        }


        //BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), "UTF-8"));
        //
        ////读取文件，每一行放入list中
        //List<String> lineList = FileUtil.readAllLines(oldFile.getAbsolutePath());
        //
        //for (String line : lineList) {
        //    if(line.length()>0){
        //        if(line.trim().startsWith("##")){
        //            continue;
        //        }
        //        if(line.contains("${") && line.indexOf("}") > 0){
        //            String token = line.substring(line.indexOf("${") + 2, line.indexOf("}"));
        //            //System.out.println(token);
        //            String tokenFormat = token.replaceAll("_", ".");
        //            line = line.replace(token, tokenFormat);
        //        }
        //    }
        //    out.write(line);
        //    out.newLine();
        //}
        //out.flush();
        //out.close();

        FileLineReplacer filter = line -> {
            if(line.length()<=0){
                return "";
            }

            if(line.trim().startsWith("##") && !line.trim().startsWith("####")){
                return null;
            }

            if(line.contains("${") && line.indexOf("}") > 0){
                String token = line.substring(line.indexOf("${") + 2, line.indexOf("}"));
                //System.out.println(token);
                String tokenFormat = token.replaceAll("_", ".");
                line = line.replace(token, tokenFormat);
            }
            return line;
        };

        FileUtil.copyFile(oldFile.getAbsolutePath(), newFile.getAbsolutePath(), filter);

        //String oldFileName = oldFile.getName();
        //oldFile.delete();
        //newFile.renameTo(oldFile);
        System.out.println("完成：" + oldFile.getAbsolutePath());

    }

}
