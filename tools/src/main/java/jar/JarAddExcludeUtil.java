package jar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.CollectionUtils;
import util.StringUtil;

import static util.StringUtil.getTab;

/**
 * 自动修改jar pom文件，增加exclude列表
 * @author weigangpeng
 * @date 2017/12/08 下午2:06
 */

public class JarAddExcludeUtil {

    public static void generateNewPom(String pomPath, List<Jar> jarTreeList, String newPomPath){

        StringBuffer newPomFileBuffer = new StringBuffer();

        //需要排除子节点的jar Map
        Map<String, Jar> exludeRootMap = new HashMap<>();
        for (Jar jar : jarTreeList) {
            exludeRootMap.put(jar.getFileName(), jar);
        }

        Jar jar = null;

        Jar parentExcludeJar = null;

        int lineNumber = 0;
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(pomPath), "UTF-8"));
            String line;
            String blankStrBeforeVersion = null;
            boolean inExclude = false;
            StringBuffer excludePomListSb = null;
            StringBuffer beforeBuffer = null;
            StringBuffer afterBuffer = null;
            while ((line = bReader.readLine()) != null) {
                lineNumber += 1;


                //是否被注释掉
                if(StringUtil.isEmpty(line) || line.contains("<!--") || line.contains("-->")){
                    newPomFileBuffer.append(line).append("\n");
                    continue;
                }

                if(lineNumber == 534 ){
                    int x = 0;
                }
                if(line.contains("<dependency>") ){
                    jar = new Jar();
                    inExclude = false;
                }

                if(line.contains("<exclusion>") ){
                    if(parentExcludeJar != null && jar != null){
                        jar.addUnUsedChild(parentExcludeJar);
                    }

                    parentExcludeJar = new Jar();
                    inExclude = true;
                }

                if(line.contains("<exclusions>") && excludePomListSb != null ){
                    excludePomListSb = null;
                    inExclude = true;
                }

                if(!inExclude && jar != null ){
                    if(line.contains("<groupId>") && line.contains("</groupId>")){
                        String group = line.substring(line.indexOf("<groupId>" ) + 9, line.indexOf("</groupId>" ));
                        jar.setGroup(group);
                    }
                    if(line.contains("<artifactId>") && line.contains("</artifactId>")){
                        String artifactId = line.substring(line.indexOf("<artifactId>" ) + 12, line.indexOf("</artifactId>" ));
                        jar.setArtifactId(artifactId);
                    }
                    if(line.contains("<version>") && line.contains("</version>")){
                        String version = line.substring(line.indexOf("<version>" ) + 9, line.indexOf("</version>" ));
                        jar.setVersion(version);
                        blankStrBeforeVersion = line.substring(0, line.indexOf("<version>"));
                    }
                }

                if(inExclude && parentExcludeJar != null && jar != null ){
                    if(line.contains("<groupId>") && line.contains("</groupId>")){
                        String group = line.substring(line.indexOf("<groupId>" ) + 9, line.indexOf("</groupId>" ));
                        parentExcludeJar.setGroup(group);
                    }
                    if(line.contains("<artifactId>") && line.contains("</artifactId>")){
                        String artifactId = line.substring(line.indexOf("<artifactId>" ) + 12, line.indexOf("</artifactId>" ));
                        parentExcludeJar.setArtifactId(artifactId);
                    }
                    if(line.contains("<version>") && line.contains("</version>")){
                        String version = line.substring(line.indexOf("<version>" ) + 9, line.indexOf("</version>" ));
                        parentExcludeJar.setVersion(version);
                    }
                }

                if(line.contains("</exclusions>") && jar != null && exludeRootMap.get(jar.getFileName()) != null ){
                    if(jar.getArtifactId().equals("webx3.core")){
                        int d = 0;
                    }
                    excludePomListSb = new StringBuffer();
                    //是否已经存在exclusions中
                    for (Jar excludeJar : exludeRootMap.get(jar.getFileName()).getUnusedChildren()) {
                        boolean isAlreadyInExcludeList = false;
                        if(CollectionUtils.isNotEmpty(jar.getUnusedChildren())){
                            for (Jar unusedJar : jar.getUnusedChildren()) {
                                if(unusedJar.getGroup().equals(excludeJar.getGroup()) && unusedJar.getArtifactId().equals(excludeJar.getArtifactId())){
                                    isAlreadyInExcludeList = true;
                                }
                            }
                        }
                        if(!isAlreadyInExcludeList){
                            excludePomListSb.append(getExcludePom(blankStrBeforeVersion, excludeJar));
                        }
                    }
                    if(StringUtil.isNotEmpty(excludePomListSb.toString()) ){
                        excludePomListSb.deleteCharAt(excludePomListSb.lastIndexOf("\n"));
                        beforeBuffer = excludePomListSb;
                    }

                }

                if(line.contains("</dependency>") && CollectionUtils.isEmpty(jar.getUnusedChildren()) && exludeRootMap.get(jar.getFileName()) != null && CollectionUtils.isNotEmpty(exludeRootMap.get(jar.getFileName()).getUnusedChildren()) ){
                    StringBuffer tempSb = new StringBuffer();
                    excludePomListSb = new StringBuffer();
                    for (Jar excludeJar : exludeRootMap.get(jar.getFileName()).getUnusedChildren()) {
                        excludePomListSb.append(getExcludePom(blankStrBeforeVersion, excludeJar));
                    }
                    tempSb.append(blankStrBeforeVersion).append("<exclusions>\n").append(excludePomListSb).append(getTab(1)).append(blankStrBeforeVersion).append("</exclusions>");
                    beforeBuffer = tempSb;
                    excludePomListSb = null;
                }

                if(beforeBuffer != null && beforeBuffer.length() > 0){
                    exludeRootMap.remove(jar.getFileName());
                    newPomFileBuffer.append(beforeBuffer).append("\n");
                    //System.err.println(beforeBuffer.toString());
                    beforeBuffer = null;
                    excludePomListSb = null;
                }
                newPomFileBuffer.append(line).append("\n");
                //System.out.println(line);

                //if(afterBuffer != null){
                //    exludeRootMap.remove(jar.getFileName());
                //    newPomFileBuffer.append(afterBuffer);
                //    //System.err.println(afterBuffer.toString());
                //    afterBuffer = null;
                //    excludePomListSb = null;
                //}

                if(line.contains("</dependency>") && excludePomListSb != null ){
                    jar = null;
                }

            }
            bReader.close();

            newPomFileBuffer.deleteCharAt(newPomFileBuffer.lastIndexOf("\n"));

            //BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newPomPath)));
            //bWriter.write(newPomFileBuffer.toString());
            //bWriter.close();

            FileOutputStream out=new FileOutputStream(newPomPath);
            out.write(newPomFileBuffer.toString().getBytes("UTF-8"));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getExcludePom(String blankStrBeforeVersion, Jar excludeJar) {
        int tab = 1;
        final StringBuffer sb = new StringBuffer("");
        sb.append(blankStrBeforeVersion).append(getTab(tab )).append("<exclusion>\n");
        sb.append(blankStrBeforeVersion).append(getTab(tab + 1)).append("<groupId>").append(excludeJar.getGroup()).append("</groupId>\n");
        sb.append(blankStrBeforeVersion).append(getTab(tab  + 1)).append("<artifactId>").append(excludeJar.getArtifactId()).append("</artifactId>\n");
        sb.append(blankStrBeforeVersion).append(getTab(tab )).append("</exclusion>\n");
        return sb.toString();
    }


    private static void sleep(){
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
