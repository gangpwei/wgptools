package dubbo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import dubbo.model.ConsumerConfig;
import dubbo.model.MethodConfig;
import dubbo.model.ServiceConfig;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import util.StringUtil;
import util.file.FileUtil;

/**
 * Dubbo消费端工具类
 * @author : gangpeng.wgp
 * @time: 17/9/17
 */
public class DubboConsumerUtil {

    public static final String WARP = "\n";


    public static void main(String[] args) throws IOException {
        try {

            getConsumerConfig(new String[] {

                //"/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/src/webroot/META-INF/autoconf/platform/biz-dubbo-aegean-client.xml.vm"
                //"/Users/weigangpeng/IdeaProjects/noah/bundle/war/src/webroot/META-INF/autoconf/platform/biz-crm-member-interface-dubbo-client.xml.vm"
                "/Users/weigangpeng/IdeaProjects/caesar/bundle/war/src/webroot/META-INF/autoconf/biz-crm-member-interface-dubbo-client.xml.vm"

            });

            //String filePath="/Users/weigangpeng/IdeaProjects/aegean_home/trunk/bundle/war/src/resources/platform/biz-contact-client.xml";
            //String version ="contact_hsf_version";
            //String serviceNamesStr="com.ali.aurora.biz.contact.ChangeLogBo;com.ali.aurora.biz.contact.ChangeLogFactory;com.ali.aurora.biz.contact.ChangeLogQuery;com.ali.aurora.biz.contact.ContactOperationBo;com.ali.aurora.biz.contact.ContactPermissionService;com.ali.aurora.biz.contact.ContactQueryService;com.ali.aurora.biz.contact.ContactValidatorService;com.ali.aurora.biz.contact.ContactActionService;com.ali.aurora.biz.common.service.SplitNameService;com.ali.aurora.biz.contact.bean.helper.ContactTypeUtils";
            //generateHsfXmlFile(filePath, serviceNamesStr, version);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static List<ConsumerConfig> getConsumerConfig(String[] fileArray){
        List<ConsumerConfig> result = new ArrayList<>();
        for (String filePath : fileArray) {
            List<ConsumerConfig> list = DubboConsumerUtil.parserConsumerXml(filePath);

            processAnnotation(filePath, list);

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
     * 生成HSF配置
     *
     * @param filePath
     * @param serviceNamesStr 接口名字符串，可以是多个，“;”分隔
     * @return
     */
    public static String generateHsfXmlFile(String filePath, String serviceNamesStr, String version) {
        String[] array = serviceNamesStr.split(";");
        List<String> list = Arrays.asList(array);
        List<ConsumerConfig> configList = new ArrayList<>(list.size());
        for (String serviceName : list) {
            configList.add(new ConsumerConfig(serviceName, version, "5000",  "HSF", HsfUtil.getBeanName(serviceName)));
        }

        return generateHsfXmlFile(filePath, configList);
    }

    /**
     * 将xml配置转为字符串
     * @param filePath
     * @param list
     * @return
     */
    public static String generateHsfXmlFile(String filePath, List<ConsumerConfig> list) {

        StringBuilder sb = new StringBuilder();
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<beans default-autowire=\"byName\"\n"
            + "\txmlns=\"http://www.springframework.org/schema/beans\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + "\txmlns:aop=\"http://www.springframework.org/schema/aop\" xmlns:context=\"http://www.springframework.org/schema/context\"\n"
            + "\txsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd\n"
            + "\thttp://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd\n"
            + "\thttp://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd\">\n";

        sb.append(header);
        for (ConsumerConfig config : list) {
            sb.append(config.toXmlString());
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

    /**
     * 处理注释
     * @param fileName
     * @param beanList
     */
    public static void processAnnotation(String fileName, List<ConsumerConfig> beanList) {
        List<String> allLine = FileUtil.readAllLines(fileName);
        for (ConsumerConfig config : beanList) {
            for (int i = 0; i < allLine.size(); i++) {
                String line = allLine.get(i);
                if("orderStatusService".equals(config.getId())){
                    System.out.println();
                }
                String idExpress = "id=\"" + config.getId() + "\"";
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

    /**
     * 是否是注释
     * @param str
     * @return
     */
    public static boolean isAnnotation(String str) {
        if(StringUtil.isNotEmpty(str) && str.contains("<!--") && str.contains(">")){
            return true;
        }
        return false;
    }

    private static ConsumerConfig toConsumerConfig(Element node) {
        ConsumerConfig config = new ConsumerConfig();
        config.setInterfaceName(node.attributeValue("interface").trim());
        config.setId(node.attributeValue("id").trim());
        if(config.getId().equals("customerBlackListService")){
            System.out.println();
        }
        config.setVersion(node.attributeValue("version"));
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





}
