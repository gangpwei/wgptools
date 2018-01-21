package util;

import util.file.FileLineReplacer;
import util.file.FileUtil;

/**
 * @author weigangpeng
 * @date 2018/01/21 ÉÏÎç11:24
 */

public class FileUtilTest {

    @org.junit.Test
    public void readAllLines() throws Exception {
    }

    @org.junit.Test
    public void writeFile() throws Exception {
        FileUtil.writeFile("/Users/weigangpeng/IdeaProjects/git/wgptools/common/target/1.txt", "hellow ssss");
    }

    @org.junit.Test
    public void getDataSize() throws Exception {
    }

    @org.junit.Test
    public void delFolder() throws Exception {
    }

    @org.junit.Test
    public void delAllFile() throws Exception {
    }

    @org.junit.Test
    public void copyFile() throws Exception {

        String oldFile = "/Users/weigangpeng/IdeaProjects/git/wgptools/common/target/1.txt";
        String newFile = "/Users/weigangpeng/IdeaProjects/git/wgptools/common/target/2.txt";

        FileLineReplacer filter = line -> {
            if(line.length()<=0){
                return "";
            }

            if(line.trim().startsWith("##")){
                return null;
            }

            if(line.contains(".jar")){
                return line.replaceAll("jar", "war");
            }
            return line;
        };

        FileUtil.copyFile(oldFile, newFile, filter);
    }

    @org.junit.Test
    public void copyFile2() throws Exception {

        String oldFile = "/Users/weigangpeng/IdeaProjects/omega/bundle/war/src/webroot/META-INF/autoconf/alike/biz-data-source-alike.xml.vm";
        String newFile = "/Users/weigangpeng/IdeaProjects/omega/bundle/war/src/webroot/META-INF/autoconf2/alike/biz-data-source-alike.xml.vm";

        FileLineReplacer filter = line -> {
            if(line.length()<=0){
                return "";
            }

            if(line.trim().startsWith("##") && !line.trim().startsWith("####")){
                return null;
            }

            if(line.contains("${") && line.indexOf("}") > 0){
                String token = line.substring(line.indexOf("${") + 2, line.indexOf("}"));
                //System.out.println(token);
                String tokenFormat = token.replaceAll("_", ".");
                line = line.replace(token, tokenFormat);
            }
            return line;
        };

        FileUtil.copyFile(oldFile, newFile, filter);
    }

}