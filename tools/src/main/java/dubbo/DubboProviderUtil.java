package dubbo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dubbo.model.BeanConfig;
import dubbo.model.ConsumerConfig;
import dubbo.model.ServiceConfig;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import util.StringUtil;
import util.file.FileUtil;

import static dubbo.DubboConsumerUtil.isAnnotation;

/**
 * Dubbo服务端工具类
 * @author : gangpeng.wgp
 * @time: 17/9/17
 */
public class DubboProviderUtil {



    public static void main(String[] args) throws IOException {
        try {

            getConfig(new String[] {
                //"/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/src/webroot/META-INF/autoconf/platform/biz-dubbo-aegean-server.xml.vm"
                //"/Users/weigangpeng/IdeaProjects/omega/bundle/war/src/resources/platform/biz-dubbo.xml"
                "/Users/weigangpeng/IdeaProjects/caesar/bundle/war/src/webroot/META-INF/autoconf/biz-dubbo.xml.vm"

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 处理注释
     * @param fileName
     * @param beanList
     */
    public static void processAnnotation(String fileName, List<ServiceConfig> beanList) {
        List<String> allLine = FileUtil.readAllLines(fileName);
        for (ServiceConfig config : beanList) {
            for (int i = 0; i < allLine.size(); i++) {
                String line = allLine.get(i);
                if("orderStatusService".equals(config.getInterfaceName())){
                    System.out.println();
                }
                String idExpress = "interface=\"" + config.getInterfaceName() + "\"";
                if(StringUtil.isNotEmpty(line) && line.contains(idExpress)){
                    int lastLineIndex = i - 1;
                    String lastLineStr = allLine.get(lastLineIndex);
                    if(isAnnotation(lastLineStr)){
                        config.setAnnotation(lastLineStr);
                    }
                }

            }
        }
    }

    public static List<ServiceConfig> getConfig(String[] fileArray){
        List<ServiceConfig> result = new ArrayList<>();
        for (String filePath : fileArray) {
            List<ServiceConfig> list = DubboProviderUtil.parserXml(filePath);

            processAnnotation(filePath, list);

            List<BeanConfig> beanList = DubboProviderUtil.parserXmlBean(filePath);
            result.addAll(list);

            //System.out.println(filePath + " size: " + list.size());
            //for (ServiceConfig serviceConfig : list) {
            //    System.out.println("       "  + serviceConfig);
            //}

            HsfUtil.generateHsfXmlFile(getHsfFileName( filePath), list, beanList);
        }

        return result;
    }



    public static List<ServiceConfig> parserXml(String fileName) {
        List<ServiceConfig> result = new ArrayList<>();
        File inputXml = new File(fileName);
        SAXReader saxReader = new SAXReader();
        saxReader.setValidation(false);
        try {
            saxReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            Document document = saxReader.read(inputXml);
            Element employees = document.getRootElement();
            for (Iterator i = employees.elementIterator(); i.hasNext(); ) {
                Element node = (Element) i.next();
                if("dubbo:service".equals( node.getQualifiedName())){
                    result.add(toConfig(node));
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public static List<BeanConfig> parserXmlBean(String fileName) {
        List<BeanConfig> result = new ArrayList<>();
        File inputXml = new File(fileName);
        SAXReader saxReader = new SAXReader();
        saxReader.setValidation(false);
        try {
            saxReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            Document document = saxReader.read(inputXml);
            Element employees = document.getRootElement();
            for (Iterator i = employees.elementIterator(); i.hasNext(); ) {
                Element node = (Element) i.next();
                if("bean".equals( node.getQualifiedName())){
                    result.add(toBeanConfig(node));
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    private static BeanConfig toBeanConfig(Element node) {
        return new BeanConfig(node.attributeValue("id").trim(), node.attributeValue("class").trim());
    }

    private static ServiceConfig toConfig(Element node) {
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setInterfaceName(node.attributeValue("interface").trim());
        serviceConfig.setVersion(node.attributeValue("version"));
        serviceConfig.setTarget(getValue(node.attributeValue("ref")));
        if(node.attributeValue("timeout") != null){
            serviceConfig.setTimeout(getValue(node.attributeValue("timeout")));
        }
        return serviceConfig;
    }


    private static String getValue(String source) {
        return source.trim().replaceAll("\\$","").replaceAll("\\{","").replaceAll("\\}","");
    }


    public static String getHsfFileName(String filePath) {
        //if(filePath.contains("util/dubbo")){
        //    return filePath.replaceAll("dubbo", "hsf");
        //}
        //return filePath.replaceAll(".xml", "-hsf.xml");
        int add = filePath.lastIndexOf("/");
        String backFolder = filePath.substring(0, add) + "/bak";
        File file = new File(backFolder);
        if (!file.exists()) {
            file.mkdir();
        }

        filePath = backFolder + filePath.substring(add, filePath.length());
        return filePath;
    }



}
