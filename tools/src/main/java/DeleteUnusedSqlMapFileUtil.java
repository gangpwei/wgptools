import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import util.StringUtil;

/**
 * 删除没有被引用的SQLmap文件
 * @author : gangpeng.wgp
 * @time: 17/11/29
 */
public class DeleteUnusedSqlMapFileUtil {


    static List<File> unusedfileList = new ArrayList<>();

    static Set<File> usedFileSet = null;


    /**
     * 处理入口
     * @param warSrcPath war目录
     * @param sqlMapBase sqlmap目录
     * @param fileArray 主sqlmap文件数组
     * @param deleteFile 是否删除文件
     */
    public static void process(String warSrcPath, String sqlMapBase, String[] fileArray, boolean deleteFile) {
        try {

            usedFileSet = getUsedFileList(warSrcPath, sqlMapBase, fileArray);

            batchConvert(sqlMapBase);

            System.out.println("\n没用文件总数:" + unusedfileList.size() + "\n");
            for (File file : unusedfileList) {

                System.out.println("删除文件:" + file.getAbsolutePath());
                if(deleteFile){
                    file.deleteOnExit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Set<File> getUsedFileList(String warSrcPath, String sqlMapBase, String[] fileArray){
        Set<File> result = new HashSet<>();
        for (String s : fileArray) {
            List<File> list = DeleteUnusedSqlMapFileUtil.parserXml(sqlMapBase,warSrcPath + s);
            result.addAll(list);
        }

        return result;
    }

    public static List<File> parserXml(String sqlMapBase, String fileName) {
        List<File> result = new ArrayList<>();
        File inputXml = new File(fileName);
        SAXReader saxReader = new SAXReader();
        saxReader.setValidation(false);
        try {
            saxReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            Document document = saxReader.read(inputXml);
            Element employees = document.getRootElement();
            for (Iterator i = employees.elementIterator(); i.hasNext(); ) {
                Element node = (Element) i.next();
                if("sqlMap".equals( node.getName())){

                    String line = node.attributeValue("url").trim();

                    line = line.substring("${baseurl}/sqlmap/".length(), line.length());
                    String xmlFileName = line.substring(0, line.length());
                    xmlFileName = sqlMapBase + xmlFileName;
                    //System.out.println(xmlFileName);
                    File file = new File(xmlFileName);
                    if(!file.exists()){
                        System.err.println("文件不存在：" + xmlFileName);
                    }
                    result.add(file);
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    /**
     * 批量转换
     * @param sourcePath 要替换的文件或目录
     */
    public static void batchConvert(String sourcePath) {
        try {
            if (StringUtil.isEmpty(sourcePath)) {
                System.out.println("根目录不能为空！");
                return;
            }
            File rootFile = new File(sourcePath);
            if (rootFile.exists()) {
                if (rootFile.isDirectory()) {
                    forFileDirectory(rootFile);
                } else if (rootFile.isFile()) {
                    convert(rootFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void forFileDirectory(File rootFile) throws IOException {
        File[] files = rootFile.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                convert(file);
            } else if (file.isDirectory()) {
                forFileDirectory(file);
            }
        }
    }


    /**
     * 单个文件转换
     * @param file
     * @throws IOException
     */
    public static void convert(File file) throws IOException {
        if(file.getName().lastIndexOf(".xml") == -1){
            return;
        }

        if(!usedFileSet.contains(file)){
            //System.err.println("文件没有用到：" + file.getName());
            unusedfileList.add(file);
        }

        //System.out.println("完成：" + file.getAbsolutePath());

    }

}
