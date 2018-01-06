package antx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * autoconfig加载和生成工具
 * @author : gangpeng.wgp
 * @time: 18/1/6
 */
public class AutoconfigLoader {

    /**
     * 从antx.properties文件获得所有的配置项
     * @param fileName
     * @return
     */
    public static HashMap<String, String> getConfigFromPropertiesFile(String fileName) {
        HashMap<String, String> map = new HashMap<String, String>();

        try {
            Properties pps = new Properties();
            pps.load(new FileInputStream(fileName));
            Enumeration enum1 = pps.propertyNames();
            while(enum1.hasMoreElements()) {
                String strKey = (String) enum1.nextElement();
                String strValue = pps.getProperty(strKey);
                if(strKey.contains("#") || strKey.contains("?")){
                    continue;
                }
                if(strValue == null || strValue.length() == 0){
                    continue;
                }
                //System.out.println(strKey + "=" + strValue);
                map.put(strKey, strValue);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return map;
    }


    public static HashMap<String, String> parserXml(String fileName) {
        HashMap<String, String> map = new HashMap<String, String>();
        File inputXml = new File(fileName);
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(inputXml);
            Element employees = document.getRootElement();
            for (Iterator i = employees.elementIterator(); i.hasNext(); ) {
                Element employee = (Element) i.next();
                for (Iterator j = employee.elementIterator(); j.hasNext(); ) {
                    Element node = (Element) j.next();
                    if("property".equals( node.getName())){
                        //System.out.println( node.attributeValue("name") + ":" + node.attributeValue("defaultValue"));
                        map.put(node.attributeValue("name"), node.attributeValue("defaultValue"));
                    }
                }
            }
        } catch (DocumentException e) {
            System.out.println(e.getMessage());
        }
        return map;
    }

    /**
     * 单个文件转换
     * @param path
     * @throws IOException
     */
    public static void getNewConfigFile(String path, List<String> unusedKeyList, boolean removeUnused) throws IOException {
        File file = new File(path);


        BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String line;

        File newFile = new File(file.getAbsolutePath().replace(".xml", "_new.xml"));
        File parentDir = new File(newFile.getParent());
        if(!parentDir.exists()){
            parentDir.mkdirs();
        }
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), "UTF-8"));

        boolean containKey = false;
        while ((line = bReader.readLine()) != null) {
            if(line.length()>0){

                for (String key : unusedKeyList) {
                    if(line.contains(key) && line.contains("name=\"" + key + "\"")){

                        //System.out.println(line + " 包含key :" + key);
                        containKey = true;
                        break;
                    }
                }
                if(containKey ){
                    if(line.endsWith("/>")){
                        containKey = false;
                    }

                    if(removeUnused){
                        continue;
                    }

                    line = "<!-- " + line + " -->";
                }
            }
            out.write(line);
            out.newLine();
        }
        out.flush();
        out.close();
        bReader.close();

        System.out.println("\n生成新的auto-config.xml：" + newFile.getAbsolutePath());

    }

}
