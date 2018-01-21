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
    public static final String WARP = "\n";



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

            generateHsfXmlFile(filePath, list);
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


    /**
     * 将xml配置转为字符串
     * @param filePath
     * @param list
     * @return
     */
    private static String generateHsfXmlFile(String filePath, List<ServiceConfig> list) {

        StringBuilder sb = new StringBuilder();
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<beans default-autowire=\"byName\"\n"
            + "\txmlns=\"http://www.springframework.org/schema/beans\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + "\txmlns:aop=\"http://www.springframework.org/schema/aop\" xmlns:context=\"http://www.springframework.org/schema/context\"\n"
            + "\txsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd\n"
            + "\thttp://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd\n"
            + "\thttp://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd\">";

        sb.append(header);
        for (ServiceConfig serviceConfig : list) {
            sb.append(beanItemToXmlString(serviceConfig));
        }
        sb.append("</beans>");

        File hsfXmlFile = new File(getHsfFileName(filePath));

        String content = sb.toString();
        try {
            System.out.println("\n生成HSF配置文件:" + hsfXmlFile.getAbsolutePath());
            System.out.println(content);

            //写文件
            FileUtil.writeFile(hsfXmlFile.getAbsolutePath(), content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;
    }

    public static String getHsfFileName(String filePath) {
        if(filePath.contains("util/dubbo")){
            return filePath.replaceAll("dubbo", "hsf");
        }
        return filePath.replaceAll(".xml", "-hsf.xml");
    }

    public static String beanItemToXmlString(ServiceConfig config){
        StringBuilder sb = new StringBuilder();
        sb.append(WARP).append("    <bean class=\"com.taobao.hsf.app.spring.util.HSFSpringProviderBean\" init-method=\"init\">");
        sb.append(WARP).append("        <property name=\"serviceInterface\" value=\"").append(config.getInterfaceName()).append("\" />");
        sb.append(WARP).append("        <property name=\"serviceVersion\" value=\"").append(getConfigValue(config.getVersion())).append("\" />");
        sb.append(WARP).append("        <property name=\"target\" ref=\"").append(getConfigValue(config.getTarget())).append("\" />");
        sb.append(WARP).append("        <property name=\"serviceGroup\" value=\"DUBBO\" />");
        if(config.getTimeout() != null){
            sb.append(WARP).append("        <property name=\"clientTimeout\" value=\"").append(getConfigValue(config.getTimeout())).append("\" />");
        }
        sb.append(WARP).append("    </bean>");
        sb.append(WARP);
        return sb.toString();
    }

    public static String getConfigValue(String source){
        if(source.contains("_")){
            return "${" + source + "}";
        }
        return source;
    }

}
