package antx;

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
import util.StringUtil;
import util.file.FileUtil;

/**
 * �����µ�auto-config.xml��
 * @author : gangpeng.wgp
 * @time: 18/1/6
 */
public class AutoconfigPraseUtil {

    public static final String FIELD_TEMPLATE = "template";

    public static final String FIELD_DESTFILE = "destfile";

    public static final String FIELD_CHARSET = "charset";

    /**
     * �Ƿ���Ԥ��������true: ���ɺ���ļ���ֱ���滻ԭ�ļ�, �����ļ��ȶ�
     */
    public static boolean preview = true;

    public static void main(String[] args) throws IOException {

        preview = true;

        List<GenerateConf> generateConfs = AutoconfigPraseUtil.parserConsumerXml(
            "/Users/weigangpeng/IdeaProjects/noah/bundle/war/src/webroot/META-INF/autoconf/auto-config.xml");
        for (GenerateConf generateConf : generateConfs) {
            System.out.println(generateConf);
        }
    }


    public static List<GenerateConf> parserConsumerXml(String fileName) {
        List<GenerateConf> result = new ArrayList<>();
        File inputXml = new File(fileName);
        SAXReader saxReader = new SAXReader();
        saxReader.setValidation(false);
        try {
            saxReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            Document document = saxReader.read(inputXml);
            List<Element> scriptList = document.getRootElement().elements("script");
            for (Element script : scriptList) {
                List<Element> generateList = script.elements("generate");
                for (Element element : generateList) {
                    result.add(toGenerateConf(element));

                }

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    private static GenerateConf toGenerateConf(Element node) {
        GenerateConf config = new GenerateConf();
        config.setTempalte(node.attributeValue("template").trim());
        config.setDestfile(node.attributeValue("destfile").trim());
        config.setCharset(node.attributeValue("charset"));

        return config;
    }

    /**
     * �����ļ�ת��
     *
     * @param autoconfigFile
     * @throws IOException
     */
    public static List<GenerateConf> getConfigList(String autoconfigFile) throws IOException {
        List<GenerateConf> configList = new ArrayList<>();

        GenerateConf generateConf = null;

        int lineNumber = 0;
        try {
            //��ȡ�ļ���ÿһ�з���list��
            List<String> lineList = FileUtil.readAllLines(autoconfigFile);

            for (String line : lineList) {

                String tempLine = line;
                lineNumber += 1;
                //System.out.println(lineNumber + " " + line);

                //�Ƿ�ע�͵�
                if (StringUtil.isEmpty(line) || line.contains("<!--") || line.contains("-->")) {
                    continue;
                }

                if (lineNumber == 1209) {
                    int x = 0;
                }

                //�µ�generate����
                if (line.contains("<generate")) {
                    generateConf = new GenerateConf();
                }

                //����generate���ã�ֱ����ӣ�������
                if (generateConf == null) {
                    continue;
                }

                if (line.contains(FIELD_TEMPLATE + "=")) {
                    if (lineNumber == 1209) {
                        int x = 0;
                    }
                    String template = getXmlFieldValue(line, FIELD_TEMPLATE);
                    generateConf.setTempalte(template);
                }

                if (line.contains(FIELD_CHARSET + "=")) {
                    String value = getXmlFieldValue(line, FIELD_CHARSET);
                    generateConf.setCharset(value);
                }

                if (line.contains(FIELD_DESTFILE + "=")) {
                    if (lineNumber == 1209) {
                        int x = 0;
                    }
                    String value = getXmlFieldValue(line, FIELD_DESTFILE);
                    generateConf.setDestfile(value);
                }

                if (line.contains("/>")) {
                    configList.add(generateConf);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return configList;
    }



    private static String getXmlFieldValue(String line, String field) {
        String temp = line.substring(line.indexOf(field + "=") + (field + "=").length() + 1, line.length());
        int end =  temp.indexOf("\"");
        return temp.substring(0, end);
    }
    
}
