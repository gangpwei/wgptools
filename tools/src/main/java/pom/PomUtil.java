package pom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import util.CollectionUtils;

/**
 * 抽取二方库工具类，用于生成新的二方库pom文件
 * @author : gangpeng.wgp
 * @time: 18/1/6
 */
public class PomUtil {

    static Map<String, Dependency> dependencyMap = new HashMap();


    public static void main(String[] args) throws IOException {
        //String allPomPath = "/Users/weigangpeng/IdeaProjects/wgptools/pom.xml";
        //String modulePomPath = "/Users/weigangpeng/IdeaProjects/wgptools/tools/pom.xml";

        String allPomPath = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/pom.xml";
        String modulePomPath = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/biz/activity/pom.xml";

        process(allPomPath, modulePomPath);
    }

    private static void process(String allPomPath, String modulePomPath) {
        List<Dependency> allPoms = parserAllXml(allPomPath);
        for (Dependency dependency : allPoms) {
            dependencyMap.put( dependency.getGroupId() + ":" + dependency.getArtifactId(), dependency);
        }

        List<Dependency> modulePoms = parserXml(modulePomPath);
        for (Dependency dependency : modulePoms) {
            Dependency dependencyGlobal = dependencyMap.get(dependency.getGroupId() + ":" + dependency.getArtifactId());
            if(dependencyGlobal == null){
                System.err.println(dependency.getGroupId() + ":" + dependency.getArtifactId() + "不存在");
                continue;
            }
            dependency.setVersion(dependencyGlobal.getVersion());
            if(CollectionUtils.isNotEmpty(dependencyGlobal.getExclusions())){
                if(CollectionUtils.isNotEmpty(dependency.getExclusions())){
                    for (Dependency exclusion : dependencyGlobal.getExclusions()) {
                        if(!dependency.getExclusions().contains(exclusion)){
                            dependency.getExclusions().add(exclusion);
                        }
                    }
                }else{
                    dependency.setExclusions(dependencyGlobal.getExclusions());
                }
            }
        }

        int tabCount = 2;
        StringBuilder sb = new StringBuilder();

        sb.append(getTab(tabCount )).append("<dependencies>\n");
        for (Dependency dependency : modulePoms) {
            getDependencyStr(sb, dependency, tabCount + 1, false);
        }
        sb.append(getTab(tabCount )).append("</dependencies>");

        String str = sb.toString();
        str = str.replaceAll("\\$\\{project.version}", "1.0-SNAPSHOT")
            .replaceAll("\\$\\{pom.version}", "1.0-SNAPSHOT")
            .replaceAll("b2b.crm.aegean.biz", "aegean")
            .replaceAll("b2b.crm.aegean", "aegean")
            .replaceAll("com.alibaba.crm.app", "com.alibaba.crm.aegean");
        System.out.println(str);
    }

    private static String getDependencyStr(StringBuilder sb, Dependency dependency, int tabCount, boolean isExclude) {
        if(!isExclude){
            sb.append(getTab(tabCount )).append("<dependency>\n");
        }else{
            sb.append(getTab(tabCount )).append("<exclusion>\n");
        }
        sb.append(getTab(tabCount + 1)).append("<groupId>").append(dependency.getGroupId()).append("</groupId>\n")
        .append(getTab(tabCount + 1)).append("<artifactId>").append(dependency.getArtifactId()).append("</artifactId>\n");
        if(!isExclude){
            sb.append(getTab(tabCount + 1)).append("<version>").append(dependency.getVersion()).append("</version>\n");
        }
        if(CollectionUtils.isNotEmpty(dependency.getExclusions())){
            sb.append(getTab(tabCount + 1)).append("<exclusions>\n");
            for (Dependency exclusion : dependency.getExclusions()) {

                getDependencyStr(sb, exclusion, tabCount + 2, true);

            }

            sb.append(getTab(tabCount + 1)).append("</exclusions>\n");
        }
        if(!isExclude){
            sb.append(getTab(tabCount )).append("</dependency>\n");
        }else{
            sb.append(getTab(tabCount )).append("</exclusion>\n");
        }
        return sb.toString();
    }

    private static String getTab(int tabCount) {
        String result = "";
        for (int i = 0; i < tabCount; i++) {
            result += "   ";
        }
        return result;
    }

    public static List<Dependency> parserXml(String fileName) {
        List<Dependency> result = new ArrayList<>();
        File inputXml = new File(fileName);
        SAXReader saxReader = new SAXReader();
        saxReader.setValidation(false);
        try {
            saxReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            Document document = saxReader.read(inputXml);
            Element employees = document.getRootElement();
            for (Iterator i = employees.elementIterator(); i.hasNext(); ) {
                Element node = (Element) i.next();
                if(node.getName().equals("dependencies")){
                    List<Element> dependencys = node.elements();
                    for (Element dependency : dependencys) {
                        List<Element> dependencyPropeties = dependency.elements();
                        Dependency vo = new Dependency();
                        result.add(vo);
                        for (Element dependencyPropety : dependencyPropeties) {
                            setDependencyInfo(vo, dependencyPropety);

                            if(dependencyPropety.getName().equals("exclusions")){
                                setExclusions(vo, dependencyPropety);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public static List<Dependency> parserAllXml(String fileName) {
        List<Dependency> result = new ArrayList<>();
        File inputXml = new File(fileName);
        SAXReader saxReader = new SAXReader();
        saxReader.setValidation(false);
        try {
            saxReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            Document document = saxReader.read(inputXml);
            Element employees = document.getRootElement();
            for (Iterator i = employees.elementIterator(); i.hasNext(); ) {
                Element node = (Element) i.next();
                if(!node.getName().equals("dependencyManagement")){
                    continue;
                }
                List<Element> nodes = node.elements();
                for (Element childNode : nodes) {
                    if(childNode.getName().equals("dependencies")){
                        List<Element> dependencys = childNode.elements();
                        for (Element dependency : dependencys) {
                            List<Element> dependencyPropeties = dependency.elements();
                            Dependency vo = new Dependency();
                            result.add(vo);
                            for (Element dependencyPropety : dependencyPropeties) {
                                setDependencyInfo(vo, dependencyPropety);

                                if(dependencyPropety.getName().equals("exclusions")){
                                    setExclusions(vo, dependencyPropety);
                                }
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    private static void setExclusions(Dependency dependencyObj, Element node) {
        List<Element> dependencys = node.elements();
        dependencyObj.setExclusions(new ArrayList<Dependency>());
        for (Element dependency : dependencys) {
            List<Element> dependencyPropeties = dependency.elements();
            Dependency vo = new Dependency();
            dependencyObj.getExclusions().add(vo);
            for (Element dependencyPropety : dependencyPropeties) {
                setDependencyInfo(vo, dependencyPropety);
            }
        }
    }

    private static void setDependencyInfo(Dependency vo, Element dependencyPropety) {
        if(dependencyPropety.getName().equals("groupId")){
            vo.setGroupId(dependencyPropety.getStringValue());
        }
        if(dependencyPropety.getName().equals("artifactId")){
            vo.setArtifactId(dependencyPropety.getStringValue());
        }
        if(dependencyPropety.getName().equals("version")){
            vo.setVersion(dependencyPropety.getStringValue());
        }
    }

}
