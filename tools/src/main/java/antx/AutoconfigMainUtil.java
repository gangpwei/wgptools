package antx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import jar.Jar;
import util.CollectionUtils;
import util.StringUtil;

import static util.StringUtil.getTab;

/**
 * 生成新的auto-config.xml，
 * @author : gangpeng.wgp
 * @time: 18/1/6
 */
public class AutoconfigMainUtil {

    public static final String FIELD_TEMPLATE = "template";

    public static final String FIELD_DESTFILE = "destfile";

    public static final String FIELD_CHARSET = "charset";

    public static void main(String[] args) throws IOException {

        AutoconfigMainUtil.convert("/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/src/webroot/META-INF/autoconf/auto-config.xml",
            "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/src/webroot/META-INF/autoconf/auto-config.xml");

    }

    /**
     * 单个文件转换
     * @param oldFile
     * @param outputPath
     * @throws IOException
     */
    public static void convert(String oldFile, String outputPath) throws IOException {
            StringBuffer newFileBuffer = new StringBuffer();

            GenerateConf generateConf = null;

            int lineNumber = 0;
            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(oldFile), "UTF-8"));
                String line;
                StringBuffer gernerateBuffer = null;
                while ((line = bReader.readLine()) != null) {

                    String tempLine = line;
                    lineNumber += 1;
                    //System.out.println(lineNumber + " " + line);

                    //是否被注释掉
                    if(StringUtil.isEmpty(line) || line.contains("<!--") || line.contains("-->")){
                        newFileBuffer.append(line).append("\n");
                        continue;
                    }

                    if(lineNumber == 1209 ){
                        int x = 0;
                    }

                    //新的generate配置
                    if(line.contains("<generate") ){
                        generateConf = new GenerateConf();
                        gernerateBuffer = new StringBuffer();
                    }

                    //不是generate配置，直接添加，并跳过
                    if(generateConf == null){
                        newFileBuffer.append(line).append("\n");
                        continue;
                    }

                    if(line.contains(FIELD_TEMPLATE + "=") ){
                        if(lineNumber == 1209 ){
                            int x = 0;
                        }
                        String template = getXmlFieldValue(line, FIELD_TEMPLATE);
                        generateConf.setTempalte(template);
                        line = line.replace(".vm", "");
                    }

                    if(line.contains(FIELD_CHARSET + "=") ){
                        String value = getXmlFieldValue(line, FIELD_CHARSET);
                        generateConf.setCharset(value);
                    }

                    if(line.contains(FIELD_DESTFILE + "=") ){
                        if(lineNumber == 1209 ){
                            int x = 0;
                        }
                        String value = getXmlFieldValue(line, FIELD_DESTFILE);
                        generateConf.setDestfile(value);
                        if(!value.contains("WEB-INF/bean")){
                            System.err.println("特殊xml, lineNumber:" + lineNumber + " line: " +tempLine);
                        }else{
                            //去除destfile配置
                            line =  removeXmlField(line, FIELD_DESTFILE);
                        }
                    }

                    gernerateBuffer.append(line).append("\n");
                    if(line.contains("/>")){
                        newFileBuffer.append(gernerateBuffer);
                        generateConf = null;
                    }

                }
                bReader.close();

                newFileBuffer.deleteCharAt(newFileBuffer.lastIndexOf("\n"));
                FileOutputStream out=new FileOutputStream(outputPath);
                out.write(newFileBuffer.toString().getBytes("UTF-8"));
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    private static String getXmlFieldValue(String line, String field) {
        String temp = line.substring(line.indexOf(field + "=") + (field + "=").length() + 1, line.length());
        int end =  temp.indexOf("\"");
        return temp.substring(0, end);
    }

    private static String removeXmlField(String line, String field) {
        String temp = line.substring(line.indexOf(field + "=") + (field + "=").length() + 1, line.length());
        String head = line.substring(0, line.indexOf(field + "=") - 1);
        String end = temp.substring(temp.indexOf("\"") + 1, temp.length());
        return head + end;
    }
}
