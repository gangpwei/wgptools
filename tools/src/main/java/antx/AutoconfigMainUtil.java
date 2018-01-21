package antx;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import util.file.FileUtil;
import util.StringUtil;

/**
 * �����µ�auto-config.xml��
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
     * �����ļ�ת��
     * @param oldFile
     * @param outputPath
     * @throws IOException
     */
    public static void convert(String oldFile, String outputPath) throws IOException {
            StringBuffer newFileBuffer = new StringBuffer();

            GenerateConf generateConf = null;

            int lineNumber = 0;
            try {
                StringBuffer gernerateBuffer = null;
                //��ȡ�ļ���ÿһ�з���list��
                List<String> lineList = FileUtil.readAllLines(oldFile);

                for (String line : lineList) {

                    String tempLine = line;
                    lineNumber += 1;
                    //System.out.println(lineNumber + " " + line);

                    //�Ƿ�ע�͵�
                    if(StringUtil.isEmpty(line) || line.contains("<!--") || line.contains("-->")){
                        newFileBuffer.append(line).append("\n");
                        continue;
                    }

                    if(lineNumber == 1209 ){
                        int x = 0;
                    }

                    //�µ�generate����
                    if(line.contains("<generate") ){
                        generateConf = new GenerateConf();
                        gernerateBuffer = new StringBuffer();
                    }

                    //����generate���ã�ֱ����ӣ�������
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
                            System.err.println("����xml, lineNumber:" + lineNumber + " line: " +tempLine);
                        }else{
                            //ȥ��destfile����
                            line =  removeXmlField(line, FIELD_DESTFILE);
                        }
                    }

                    gernerateBuffer.append(line).append("\n");
                    if(line.contains("/>")){
                        newFileBuffer.append(gernerateBuffer);
                        generateConf = null;
                    }

                }

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
