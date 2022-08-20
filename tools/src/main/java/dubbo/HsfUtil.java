package dubbo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dubbo.model.BeanConfig;
import dubbo.model.ServiceConfig;
import util.file.FileUtil;

/**
 * @author weigangpeng
 * @date 2018/01/23 下午5:38
 */

public class HsfUtil {

    public static String getBeanName(String clzName) {
        int start = clzName.lastIndexOf(".") + 1;
        String result = clzName.substring(start, start + 1).toLowerCase() + clzName.substring(start + 1, clzName.length());
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
        List<ServiceConfig> configList = new ArrayList<>(list.size());
        for (String serviceName : list) {
            configList.add(new ServiceConfig(serviceName, version, "5000", "HSF", getBeanName(serviceName)));
        }

        return generateHsfXmlFile(filePath, configList);
    }

    /**
     * 生成HSF配置
     *
     * @param filePath
     * @param list
     * @return
     */
    public static String generateHsfXmlFile(String filePath, List<ServiceConfig> list) {

        return generateHsfXmlFile(filePath, list, null);
    }

    public static String generateHsfXmlFile(String filePath, List<ServiceConfig> list, List<BeanConfig> beanList) {
        StringBuilder sb = new StringBuilder();
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<beans default-autowire=\"byName\"\n"
            + "\txmlns=\"http://www.springframework.org/schema/beans\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + "\txsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd\n"
            + "\thttp://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd\n"
            + "\thttp://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd\">";

        sb.append(header);
        sb.append("\n");

        for (BeanConfig beanConfig : beanList) {
            sb.append(beanConfig.toXmlString());
        }
        sb.append("\n");

        for (ServiceConfig serviceConfig : list) {
            sb.append(serviceConfig.toXmlString());
        }
        sb.append("</beans>");

        File hsfXmlFile = new File(filePath);

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
}
