package dubbo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dubbo.model.ServiceConfig;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import util.file.FileUtil;

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
                "/Users/weigangpeng/IdeaProjects/muses_new/banner/bundle/war/src/webroot/META-INF/autoconf/spring/biz-dubbo-service.xml.vm"
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static List<ServiceConfig> getConfig(String[] fileArray){
        List<ServiceConfig> result = new ArrayList<>();
        for (String filePath : fileArray) {
            List<ServiceConfig> list = DubboProviderUtil.parserXml(filePath);
            result.addAll(list);

            //System.out.println(filePath + " size: " + list.size());
            //for (ServiceConfig serviceConfig : list) {
            //    System.out.println("       "  + serviceConfig);
            //}

            HsfUtil.generateHsfXmlFile(getHsfFileName( filePath), list);
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

    private static ServiceConfig toConfig(Element node) {
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setInterfaceName(node.attributeValue("interface").trim());
        serviceConfig.setVersion(getValue(node.attributeValue("version")));
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
        if(filePath.contains("util/dubbo")){
            return filePath.replaceAll("dubbo", "hsf");
        }
        return filePath.replaceAll(".xml", "-hsf.xml");
    }



}
