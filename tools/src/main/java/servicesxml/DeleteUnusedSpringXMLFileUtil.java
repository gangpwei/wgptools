package servicesxml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import antx.AutoconfigLoader;
import antx.AutoconfigPraseUtil;
import antx.GenerateConf;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import util.StringUtil;

/**
 * ɾ��û�б����õ�SQLmap�ļ�
 * @author : gangpeng.wgp
 * @time: 17/11/29
 */
public class DeleteUnusedSpringXMLFileUtil {


    static List<File> unusedfileList = new ArrayList<>();

    static List<String> usedFileList = new ArrayList<>();

    static List<String> exclueFileList = Arrays.asList("web.xml.vm", "webapp.xml.vm", "webx-default.xml.vm",
        "log4j.xml.vm","shy.properties.vm", "pipeline.xml.vm","crm.ui.xml.vm","adele.ini.vm","platform/unicorn.properties.vm",
        "index.html.vm","platform/services.xml.vm", "resources-caesar.xml.vm","platform/Scheduler.properties.vm","platform/tddl-rule.xml.vm"
    );


    public static boolean preview = true;

    
    public static void main(String[] args) {
        preview = false;
        String autoconfigPath = "/Users/weigangpeng/IdeaProjects/noah/bundle/war/src/webroot/META-INF/autoconf";


        process(autoconfigPath);
    }

    /**
     * �������
     * @param autoconfigPath warĿ¼
     */
    public static void process(String autoconfigPath) {
        try {

            for (String file : exclueFileList) {
                usedFileList.add(autoconfigPath + "/" + file);

            }

            List<GenerateConf> generateConfs = AutoconfigPraseUtil.parserConsumerXml(
                autoconfigPath + "/auto-config.xml");

            List<String> serviceFileList = ServiceXmlPraseUtil.getConfigList(
                autoconfigPath + "/platform/services.xml.vm");


            for (GenerateConf generateConf : generateConfs) {
                if(exclueFileList.contains(generateConf.getTempalte())){
                    generateConf.setUsed(true);
                }
            }


            for (GenerateConf generateConf : generateConfs) {
                for (String file : serviceFileList) {
                    if(("/" + generateConf.getDestfile()).equals(file)){
                        usedFileList.add(autoconfigPath + "/" + generateConf.getTempalte());
                        generateConf.setUsed(true);
                    }
                }
            }
            usedFileList.add(autoconfigPath + "/auto-config.xml");
            usedFileList.add(autoconfigPath + "/platform/services.xml.vm");

            List<String> unusedAutoConfigKey = new ArrayList<>();
            for (GenerateConf generateConf : generateConfs) {
                //if(generateConf.getTempalte().contains("services.xml")){
                //    System.out.println("");
                //}
                if(!generateConf.isUsed() && !serviceFileList.contains(("/" + generateConf.getDestfile()))){
                    //unusedfileList.add(autoconfigPath + "");
                    System.out.println("autoconfig�д���û�����ã�" + generateConf);
                    unusedAutoConfigKey.add(generateConf.getDestfile());
                }
            }


            AutoconfigLoader.getNewConfigFileByDestfile(autoconfigPath + "/auto-config.xml", unusedAutoConfigKey, !preview);


            //for (GenerateConf generateConf : generateConfs) {
            //    if(!serviceFileList.contains(generateConf.getDestfile())){
            //        unusedfileList.add(autoconfigPath + "")
            //    }
            //}

            batchConvert(autoconfigPath);

            System.out.println("\nû���ļ�����:" + unusedfileList.size() + "\n");
            for (File file : unusedfileList) {

                System.out.println("ɾ���ļ�:" + file.getAbsolutePath());
                if(!preview){
                    file.deleteOnExit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if(file.getName().lastIndexOf(".vm") == -1 || ! file.getName().contains(".xml") ){
            return;
        }

        if(!usedFileList.contains(file.getAbsolutePath())){
            //System.err.println("�ļ�û���õ���" + file.getName());
            unusedfileList.add(file);
        }

        //System.out.println("��ɣ�" + file.getAbsolutePath());

    }


}
