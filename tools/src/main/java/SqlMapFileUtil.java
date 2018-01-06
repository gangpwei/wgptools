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
 * 查找没有被引用的SQLmap文件
 * @author : gangpeng.wgp
 * @time: 17/11/29
 */
public class SqlMapFileUtil {

    /**
     * SQLMAP 根目录
     */
    //static String SQLMAP_BASE = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/src/java/sqlmap/";

    static String SQLMAP_CONFIG = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/src/java/";

    static String SQLMAP_BASE = SQLMAP_CONFIG + "sqlmap/";

    static List<File> unusedfileList = new ArrayList<>();

    static Set<File> usedFileSet = null;

    public static void main(String[] args) throws IOException {
        try {

            //SqlMapFileUtil.parserXml("/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/src/java/sqlmap-config.xml");

            //usedFileSet = getUsedFileList(new String[] {"sqlmap-config.xml", "sqlmap-config-citypartner.xml", "sqlmap-config-leads.xml", "sqlmap-config-message.xml", "sqlmap-config-muses.xml", "sqlmap-config-mysql-aegean.xml", "sqlmap-config-zeus.xml"});

            SQLMAP_CONFIG = "/Users/weigangpeng/IdeaProjects/martini_20160823_808046_openarea_1/bundle/war/src/java/";

            SQLMAP_BASE = SQLMAP_CONFIG + "sqlmap/";
            usedFileSet = getUsedFileList(new String[] {"sqlmap-addition-config", "sqlmap-config.xml", "sqlmap-mysql-config.xml"});

            batchConvert(SQLMAP_BASE);

            System.out.println("\n没用文件总数:" + unusedfileList.size() + "\n");
            for (File file : unusedfileList) {

                System.out.println("删除文件:" + file.getAbsolutePath());
                //file.deleteOnExit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Set<File> getUsedFileList(String[] fileArray){
        Set<File> result = new HashSet<>();
        for (String s : fileArray) {
            List<File> list = SqlMapFileUtil.parserXml(SQLMAP_CONFIG + s);
            result.addAll(list);
        }

        return result;
    }

    public static List<File> parserXml(String fileName) {
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
                    xmlFileName = SQLMAP_BASE + xmlFileName;
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
