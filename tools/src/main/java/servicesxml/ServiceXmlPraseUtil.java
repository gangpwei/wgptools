package servicesxml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import util.StringUtil;
import util.file.FileUtil;

/**
 * ��ȡservices.xml�����õ����е�xml
 * @author : gangpeng.wgp
 * @time: 18/1/6
 */
public class ServiceXmlPraseUtil {



    public static void main(String[] args) throws IOException {

        List<String> generateConfs = ServiceXmlPraseUtil.getConfigList(
            "/Users/weigangpeng/IdeaProjects/noah/bundle/war/src/webroot/META-INF/autoconf/platform/services.xml.vm");
        for (String generateConf : generateConfs) {
            System.out.println(generateConf);
        }
    }


    /**
     * �����ļ�ת��
     *
     * @param autoconfigFile
     * @throws IOException
     */
    public static List<String> getConfigList(String autoconfigFile) throws IOException {
        List<String> serviceList = new ArrayList<>();


        try {
            //��ȡ�ļ���ÿһ�з���list��
            List<String> lineList = FileUtil.readAllLines(autoconfigFile);

            for (String line : lineList) {

                //System.out.println(lineNumber + " " + line);

                //�Ƿ�ע�͵�
                if (StringUtil.isEmpty(line) || line.contains("<!--") || line.contains("-->")) {
                    continue;
                }

                //�µ�generate����
                if (line.contains("<value>") && line.contains("</value>")) {
                    String serviceFile = line.trim().replaceAll("<value>", "").replaceAll("</value>", "");
                    if(serviceFile.contains("$")){
                        continue;
                    }
                    serviceList.add(serviceFile);
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceList;
    }



    private static String getXmlFieldValue(String line, String field) {
        String temp = line.substring(line.indexOf(field + "=") + (field + "=").length() + 1, line.length());
        int end =  temp.indexOf("\"");
        return temp.substring(0, end);
    }
    
}
