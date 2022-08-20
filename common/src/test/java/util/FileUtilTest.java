package util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import util.file.FileLineReplacer;
import util.file.FileUtil;

import static util.StringUtil.camelToUnderline;

/**
 * @author weigangpeng
 * @date 2018/01/21 ÉÏÎç11:24
 */

public class FileUtilTest {

    private String FIELD_STR = "rs.getString(\"";
    private String FIELD_DATE = "rs.getTimestamp(\"";
    private String ADD_FIELD = "addField(";
    String basePath = "/Users/weigangpeng/IdeaProjects/iprocess/src/java/com/ali/crm/iprocess/noahopp/processor/";
    String[] fileArray = new String[]{
        basePath + "CaeOpportunityProcessor.java",
        basePath + "CaeOpportunityExtProcessor.java",
        basePath + "GlobalCustomerProcessor.java",
        basePath + "GlobalCustomerExtProcessor.java",
        basePath + "CaeScheduleTaskProcessor.java",
        basePath + "CaeLabelProcessor.java",
        basePath + "TMemberProcessor.java",
        basePath + "TMemberHistoryProcessor.java",
        basePath + "CaeVasExtendProcessor.java",

        basePath + "DwGenralCucstBehaviorProcessor.java",
        basePath + "CaeTpYearsProcessor.java",
        basePath + "DwRenewResourceProcessor.java",
        basePath + "DwTradeGradeProcessor.java",
        basePath + "CaeSopRecordProcessor.java",
        basePath + "CaeSopSlrProcessor.java",
        basePath + "CaeCreditScoreProcessor.java"
    };

    @org.junit.Test
    public void readAllLines() throws Exception {


        for (String file : fileArray) {
            String tableName = file.substring(basePath.length(), file.length()).replaceAll("Processor.java", "");
            tableName = camelToUnderline(tableName);
            System.out.println("create table " + tableName + " (");

            List<String> stringList = FileUtil.readAllLines(file);
            Set<String> fieldSet = new HashSet<>();
            for (String line : stringList) {
                String fieldType = null;
                String fieldTypeReplaceStr = null;
                if(line.contains(FIELD_STR)){
                    fieldType = "VARCHAR";
                    fieldTypeReplaceStr = FIELD_STR;
                }else if(line.contains(FIELD_DATE)){
                    fieldType = "TIMEPSTAMP";
                    fieldTypeReplaceStr = FIELD_DATE;
                }else{
                    continue;
                }
                String field = line.substring(line.indexOf(fieldTypeReplaceStr ) + fieldTypeReplaceStr.length());
                field = field.substring(0, field.lastIndexOf("\""));
                if(!fieldSet.contains(field)){
                    System.out.println("    " + field.toLowerCase() + " " + fieldType + ",");
                    fieldSet.add(field);
                }
            }
            System.out.println("    PRIMARY KEY (id),\n"
                + "    PERIOD FOR SYSTEM_TIME\n) with (\n"
                + "    type = 'Tddl',\n"
                + "    appName = 'B2B_CRM_NOAH_APP',\n"
                + "    tableName = '" + tableName + "',\n"
                + "    isSharding = 'false',\n"
                + "    dbGroupKey = 'B2B_CRM_NOAH_GROUP',\n"
                + "    accessKey = '4c9cca4097544eb1a94b16fb01b889e8',\n"
                + "    secretkey = 'lU+P7GR2RLknQOzDaqIecw=='\n"
                + ");\n\n");
        }
    }

    @org.junit.Test
    public void readAllLines2() throws Exception {
        Set<String> fieldSet = new HashSet<>();

        for (String file : fileArray) {
            List<String> stringList = FileUtil.readAllLines(file);
            for (String line : stringList) {

                if(!line.contains(ADD_FIELD)){
                    continue;
                }

                String fieldType = null;
                String fieldTypeReplaceStr = null;
                if(line.contains(FIELD_STR)){
                    fieldType = "VARCHAR";
                    fieldTypeReplaceStr = FIELD_STR;
                }else if(line.contains(FIELD_DATE)){
                    fieldType = "DATE";
                    fieldTypeReplaceStr = FIELD_DATE;
                }else{
                    fieldType = "VARCHAR";
                }

                String field = line.substring(line.indexOf(ADD_FIELD ) + ADD_FIELD.length(), line.length());

                field = field.substring(field.indexOf(".") + 1, field.lastIndexOf(","));
                if(!fieldSet.contains(field)){
                    System.out.println("    " + field + " " + fieldType + "," );
                    fieldSet.add(field);
                }
            }
            System.out.println();
        }
    }


    @org.junit.Test
    public void readAllLines3() throws Exception {

        String file = "/Users/weigangpeng/IdeaProjects/iprocess/src/java/com/ali/crm/iprocess/noahopp/processor/GlobalCustomerExtProcessor.java";
        //String tableName = file.substring(basePath.length(), file.length()).replaceAll("/Users/weigangpeng/IdeaProjects/iprocess/src/java/com/ali/crm/iprocess/noahopp/processor/GlobalCustomerExtProcessor.java", "");
        //tableName = camelToUnderline(tableName);
        //System.out.println("create table " + tableName + " (");

        List<String> stringList = FileUtil.readAllLines(file);
        String EXT_TYPE_STR = "GlobalCustomerExtEnum.";
        String EXT_TYPE_ALIAS_STR = "NoahOppConstant.";
        String ext_type = null;
        String extAlias = null;
        for (String line : stringList) {
            String fieldTypeReplaceStr = null;
            if(line.contains(EXT_TYPE_STR)){
                fieldTypeReplaceStr = EXT_TYPE_STR;
                String field = line.substring(line.indexOf(fieldTypeReplaceStr ) + fieldTypeReplaceStr.length(), line.indexOf(".toString()"));
                ext_type = field;
                extAlias = null;
            }else if(line.contains(EXT_TYPE_ALIAS_STR)){
                fieldTypeReplaceStr = EXT_TYPE_ALIAS_STR;
                String field = line.substring(line.indexOf(fieldTypeReplaceStr ) + fieldTypeReplaceStr.length(), line.indexOf(","));
                extAlias = field;
                System.out.println(extAlias + "  VARCHAR,");
            }else{
                continue;
            }
            if(extAlias != null && ext_type != null){
                //System.out.println("MAX(case when ext_type = '" + ext_type + "' then ext_value else null end )as " + extAlias + ",");
            }

        }
        System.out.println("");

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