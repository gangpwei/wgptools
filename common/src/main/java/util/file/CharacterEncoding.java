package util.file;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;  
  
import java.io.File;  
import java.nio.charset.Charset;  
  
public class CharacterEncoding {


    public static void main(String[] argv) throws Exception {
        //String filePath = "/Users/weigangpeng/IdeaProjects/omega/bundle/war/src/webroot/META-INF/autoconf/platform/biz-data-source-group.xml.vm";
        String filePath = "/Users/weigangpeng/IdeaProjects/omega/bundle/war/src/webroot/META-INF/autoconf/alike/biz-platform-alike.xml.vm";

        System.out.println("文件编码:" + CharacterEncoding.getFileCharacterEnding(filePath));
    }

    public static String getFileCharacterEnding(String filePath) {  
  
        File file = new File(filePath);  
  
        return getFileCharacterEnding(file);  
    }  
  
    /** 
     * Try to get file character ending. 
     * </p> 
     * <strong>Warning: </strong>use cpDetector to detect file's encoding. 
     *  
     * @param file 
     * @return 
     */  
    public static String getFileCharacterEnding(File file) {  
  
        String fileCharacterEnding = "UTF-8";  
  
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();  
        detector.add(JChardetFacade.getInstance());  
  
        Charset charset = null;  
  
        // File f = new File(filePath);  
  
        try {  
            charset = detector.detectCodepage(file.toURL());  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        if (charset != null) {  
            fileCharacterEnding = charset.name();  
        }


        //特殊处理，工具识别不准确，如果是ASCII，替换成GBK
        if("EUC-KR".equals(fileCharacterEnding)){
            return "GBK";
        }

        return fileCharacterEnding;  
    }  
}  