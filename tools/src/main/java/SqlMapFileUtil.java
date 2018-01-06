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
 * ����û�б����õ�SQLmap�ļ�
 * @author : gangpeng.wgp
 * @time: 17/11/29
 */
public class SqlMapFileUtil {

    /**
     * SQLMAP ��Ŀ¼
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

            System.out.println("\nû���ļ�����:" + unusedfileList.size() + "\n");
            for (File file : unusedfileList) {

                System.out.println("ɾ���ļ�:" + file.getAbsolutePath());
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
                        System.err.println("�ļ������ڣ�" + xmlFileName);
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
     * ����ת��
     * @param sourcePath Ҫ�滻���ļ���Ŀ¼
     */
    public static void batchConvert(String sourcePath) {
        try {
            if (StringUtil.isEmpty(sourcePath)) {
                System.out.println("��Ŀ¼����Ϊ�գ�");
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
     * �����ļ�ת��
     * @param file
     * @throws IOException
     */
    public static void convert(File file) throws IOException {
        if(file.getName().lastIndexOf(".xml") == -1){
            return;
        }

        if(!usedFileSet.contains(file)){
            //System.err.println("�ļ�û���õ���" + file.getName());
            unusedfileList.add(file);
        }

        //System.out.println("��ɣ�" + file.getAbsolutePath());

    }

}
