package dubbo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dubbo.model.ConsumerConfig;
import dubbo.model.MethodConfig;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import util.file.FileUtil;

/**
 * Dubbo消费端工具类
 * @author : gangpeng.wgp
 * @time: 17/9/17
 */
public class DubboConsumerUtil {

    public static final String WARP = "\n";

    public static final String DEFAULT_TIMEOUT = "3000";

    public static void main(String[] args) throws IOException {
        try {

            getConsumerConfig(new String[] {

                //"/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/src/webroot/META-INF/autoconf/platform/biz-dubbo-aegean-client.xml.vm"
                "/Users/weigangpeng/IdeaProjects/muses_new/banner/bundle/war/src/webroot/META-INF/autoconf/spring/biz-dubbo-client.xml.vm"

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static List<ConsumerConfig> getConsumerConfig(String[] fileArray){
        List<ConsumerConfig> result = new ArrayList<>();
        for (String filePath : fileArray) {
            List<ConsumerConfig> list = DubboConsumerUtil.parserConsumerXml(filePath);
            result.addAll(list);

            //System.out.println(filePath + " size: " + list.size());
            //for (ConsumerConfig serviceConfig : list) {
            //    System.out.println("       "  + serviceConfig);
            //
            //}

            generateHsfXmlFile(filePath, list);
        }

        return result;
    }


    /**
     * 将xml配置转为字符串
     * @param filePath
     * @param list
     * @return
     */
    private static String generateHsfXmlFile(String filePath, List<ConsumerConfig> list) {

        StringBuilder sb = new StringBuilder();
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<beans default-autowire=\"byName\"\n"
            + "\txmlns=\"http://www.springframework.org/schema/beans\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + "\txmlns:aop=\"http://www.springframework.org/schema/aop\" xmlns:context=\"http://www.springframework.org/schema/context\"\n"
            + "\txsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd\n"
            + "\thttp://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd\n"
            + "\thttp://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd\">";

        sb.append(header);
        for (ConsumerConfig serviceConfig : list) {
            sb.append(beanItemToXmlString(serviceConfig));
        }
        sb.append("</beans>");

        File hsfXmlFile = new File(DubboProviderUtil.getHsfFileName(filePath));

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

    public static List<ConsumerConfig> parserConsumerXml(String fileName) {
        List<ConsumerConfig> result = new ArrayList<>();
        File inputXml = new File(fileName);
        SAXReader saxReader = new SAXReader();
        saxReader.setValidation(false);
        try {
            saxReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            Document document = saxReader.read(inputXml);
            Element employees = document.getRootElement();
            for (Iterator i = employees.elementIterator(); i.hasNext(); ) {
                Element node = (Element) i.next();
                if("dubbo:reference".equals( node.getQualifiedName())){
                    result.add(toConsumerConfig(node));
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    private static ConsumerConfig toConsumerConfig(Element node) {
        ConsumerConfig config = new ConsumerConfig();
        config.setInterfaceName(node.attributeValue("interface").trim());
        config.setId(node.attributeValue("id").trim());
        if(config.getId().equals("customerBlackListService")){
            System.out.println();
        }
        config.setVersion(getValue(node.attributeValue("version")));
        if(node.attributeValue("timeout") != null){
            config.setTimeout(getValue(node.attributeValue("timeout")));
        }
        if(node.attributeValue("retries") != null){
            config.setRetries(getValue(node.attributeValue("retries")));
        }
        if(node.elements() != null && node.elements().size()>0){
            List<MethodConfig> methodConfigs = new ArrayList<>();
            for (Object o : node.elements()) {
                Element methodNod = (Element)o;
                MethodConfig methodConfig = toMethodConfig(methodNod);
                methodConfigs.add(methodConfig);
            }
            config.setMethods(methodConfigs);
        }

        return config;
    }

    private static MethodConfig toMethodConfig(Element node) {
        MethodConfig config = new MethodConfig();
        config.setName(node.attributeValue("name").trim());

        if(node.attributeValue("timeout") != null){
            config.setTimeout(getValue(node.attributeValue("timeout")));
        }
        if(node.attributeValue("retries") != null){
            config.setRetries(getValue(node.attributeValue("retries")));
        }
        return config;
    }


    private static String getValue(String source) {
        return source.trim().replaceAll("\\$","").replaceAll("\\{","").replaceAll("\\}","");
    }

    public static String beanItemToXmlString(ConsumerConfig config){
        StringBuilder sb = new StringBuilder();
        String className;
        if(config.getRetries() != null){
            className = "com.taobao.hsf.app.api.util.HSFApiConsumerBean";
        }else{
            className = "com.taobao.hsf.app.spring.util.HSFSpringConsumerBean";
        }
        className = "com.taobao.hsf.app.spring.util.HSFSpringConsumerBean";

        sb.append(WARP).append("    <bean name=\"").append(config.getId()).append("\" class=\"" + className + "\" init-method=\"init\">");
        sb.append(WARP).append("        <property name=\"interfaceName\" value=\"").append(config.getInterfaceName()).append("\" />");
        sb.append(WARP).append("        <property name=\"version\" value=\"").append(getConfigValue(config.getVersion())).append("\" />");
        sb.append(WARP).append("        <property name=\"group\" value=\"DUBBO\" />");
        String timeout = DEFAULT_TIMEOUT;
        if(config.getTimeout() != null){
            timeout = config.getTimeout();
        }
        sb.append(WARP).append("        <property name=\"clientTimeout\" value=\"").append(getConfigValue(timeout)).append("\" />");
        if(config.getRetries() != null){
            //sb.append(WARP).append("        <property name=\"retries\" value=\"").append(getConfigValue(config.getRetries())).append("\" />");
        }
        if(config.getMethods() != null && config.getMethods().size()>0){
            sb.append(WARP).append("        <property name=\"methodSpecials\">");
            sb.append(WARP).append("            <list>");
            for (MethodConfig methodConfig : config.getMethods()) {
                sb.append(WARP).append("                <bean class=\"com.taobao.hsf.model.metadata.MethodSpecial\">");
                sb.append(WARP).append("                    <property name=\"methodName\" value=\"").append(methodConfig.getName()).append("\" />");
                sb.append(WARP).append("                    <property name=\"clientTimeout\" value=\"").append(getConfigValue(methodConfig.getTimeout())).append("\" />");
                sb.append(WARP).append("                </bean>");

            }
            sb.append(WARP).append("            </list>");
            sb.append(WARP).append("        </property>");
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
