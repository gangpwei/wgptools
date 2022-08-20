package bacardi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.file.FileUtil;

import static bacardi.Cache.INT;
import static bacardi.Cache.KEY_CLASS;
import static bacardi.Cache.KEY_STR;
import static bacardi.Cache.LIST_OPTION_DO;
import static bacardi.Cache.LIST_RESOURCE_DO;
import static bacardi.Cache.LIST_RESOURCE_DO_GROUP;
import static bacardi.Cache.MAP;
import static bacardi.Cache.MAP_OPTION_DO;
import static bacardi.Cache.MAP_RESOURCE_DO;
import static bacardi.Cache.STRING;
import static bacardi.FectchBacardiCacheFromCodeUtil.RESOURCE_CONSTANTS_HELPER_GET;
import static bacardi.FectchBacardiCacheFromCodeUtil.isBacardiCache;
import static util.StringUtil.format;

/**
 * 替换应用中的配置
 *
 * @author gangpeng.wgp
 * @date 2019/01/17 上午11:02
 */

public class ReplaceConfigCodeUtil {

    private static final Logger log = LoggerFactory.getLogger(ReplaceConfigCodeUtil.class);

    public static String CONFIG_CENTER_CLASS  = "com.ali.caesar.platform.config.ConfigCenter";
    private static String IMPORT_OPTION_DO  = "import com.ali.b2b.crm.base.resource.OptionDo;";
    private static String STR_RETRUN;

    public static String replaceConfigCode(String cacheStr) {
        List<Cache> caches = CacheParseUtil.paraseJson(cacheStr);

        for (Cache cache : caches) {

            if (!cache.isValid() || cache.getCacheData() == null || cache.isEmpty()) {
                continue;
            }

            dealCacheConfig(cache);

        }

        return null;
    }

    public static void dealCacheConfig(Cache cache) {
        for (String file : cache.getFiles()) {
            dealFileResourceConfig(cache, file);
        }
    }

    public static void dealFileResourceConfig(Cache cache, String file) {
        if (file.contains("/java/test/")) {
            return;
        }

        //if (!file.contains("CustomerQueryBatchUpdateImpl")) {
        //    return;
        //}

        List<String> allLines = FileUtil.readAllLines(new File(file).getPath());

        boolean unFinishLine = false;
        String tempLine = "";

        int i = 0;
        int importIndex = 0;

        List<String> allLinesNew = new ArrayList<>(allLines.size());
        boolean replaceResult = false;

        for (String line : allLines) {
            if (importIndex == 0 && line.startsWith("import ")) {
                importIndex = i;
            } else {
                i++;
            }

            if(line.contains("RESKEY_MOVEOCEANELIMIT")){
                System.out.println("");
            }

            if (!isBacardiCache(line) && !unFinishLine ) {
                allLinesNew.add(line);
                continue;
            }


            if (!line.trim().endsWith(";")) {
                unFinishLine = true;
            }

            if (!unFinishLine) {

                if(replace(cache, file, allLinesNew, line, importIndex)){
                    replaceResult = true;
                }

            }
            //分行写的代码
            else {
                if(!"".equals(tempLine)){
                    tempLine = tempLine + line.trim();
                }else{
                    tempLine = line;
                }
                if (line.contains(";")) {

                    if (!isBacardiCache(tempLine)) {
                        continue;
                    }

                    if(replace(cache, file, allLinesNew, tempLine, importIndex)){
                        replaceResult = true;
                    }

                    unFinishLine = false;
                    tempLine = "";
                }
            }
        }

        boolean repalcedAll = true;
        for (String line : allLinesNew) {
            if(line == null ){
                for (String s : allLinesNew) {
                    System.out.println(s);
                }
            }
            if(isBacardiCache(line)){
                repalcedAll = false;
                break;
            }
        }
        if(replaceResult ){
            String importStr = "import " + CONFIG_CENTER_CLASS + ";";
            if(!allLinesNew.contains(importStr)){
                allLinesNew.add(importIndex, importStr);
            }
        }


        List<String> allLinesNewFinal = new ArrayList<>(allLines.size());
        if(repalcedAll){
            int j = 0;
            int x = 0;
            for (String line : allLinesNew) {


                //删除resourceManager的定义
                if (line.contains("private ResourceManager ") && line.contains("resourceManager")){
                    continue;
                }

                //删除resourceManager的set 方法
                if (line.contains(" setResourceManager(")){
                    j ++;
                    continue;
                }

                //resourceManager的set 方法开始3行过滤掉
                if(j > 0 && j <2){
                    j ++;
                    continue;
                }else if(j == 2){
                    j = 0;
                    continue;
                }

                //删除resourceManager的get 方法
                if (line.contains(" getResourceManager(")){
                    x ++;
                    continue;
                }

                //resourceManager的get 方法开始3行过滤掉
                if(x > 0 && x <2){
                    x ++;
                    continue;
                }else if(x == 2){
                    x = 0;
                    continue;
                }

                allLinesNewFinal.add(line);

            }

            allLinesNewFinal.remove("import com.ali.b2b.crm.base.resource.ResourceManager;");
        }else{
            allLinesNewFinal = allLinesNew;
        }

        String targetFilePath = file.replace(".java", ".java");
        File newfile = new File(targetFilePath);
        if (newfile.exists()) {
            newfile.delete();
        }
        FileUtil.writeFileGBK(targetFilePath, allLinesNewFinal);

    }

    private static boolean replace(Cache cache, String file, List<String> allLinesNew, String line, int importIndex) {
        String newLine = null;

        Map<String, String> clzMap = FectchBacardiCacheFromCodeUtil.getClzMap(allLinesNew);

        Cache cacheFromLine = FectchBacardiCacheFromCodeUtil.getCache(line, file, clzMap);

        if(!cache.equals(cacheFromLine)){
            allLinesNew.add(line);
            return false;
        }

        if( (KEY_CLASS.equals(cache.getKeyType()) && !line.contains(cache.getKeyField())) ||
            (KEY_STR.equals(cache.getKeyType()) && !line.contains("\"" + cache.getKeyValue() + "\""))){
            allLinesNew.add(line);
            return false;
        }
        switch (cache.getValueType()) {
            case LIST_RESOURCE_DO:
            case LIST_OPTION_DO:

                STR_RETRUN = "return ";
                if (line.contains(STR_RETRUN)) {
                    newLine = format("{}return ConfigCenter.{};", line.split(STR_RETRUN)[0], cache.getFieldName());
                }else if (line.contains("=")) {
                    newLine = format("{}= ConfigCenter.{};", line.split("=")[0], cache.getFieldName());
                }

                if(line.contains("List<Option>")){
                    newLine = newLine.replace("List<Option>", "List<OptionDo>");
                    if(!allLinesNew.contains(IMPORT_OPTION_DO)){
                        allLinesNew.add(importIndex, IMPORT_OPTION_DO);
                    }
                }
                break;
            case LIST_RESOURCE_DO_GROUP:
                if (line.split("=").length < 2 || line.split(",").length < 3) {
                    allLinesNew.add(line);
                    return false;
                }
                newLine = format("{}= ConfigCenter.{}.get({}", line.split("=")[0], cache.getFieldName(), line.split(",")[2].trim());
                break;

            case MAP_OPTION_DO:
            case MAP_RESOURCE_DO:
                if (line.split("=").length < 2 || line.split(",").length < 2) {
                    allLinesNew.add(line);
                    return false;
                }
                newLine = format("{}= ConfigCenter.{}.get({}", line.split("=")[0], cache.getFieldName(), line.split(",")[1].trim());

                break;
            case INT:
            case STRING:
                if (!line.contains("=")) {
                    String temp = line.split(RESOURCE_CONSTANTS_HELPER_GET)[1];
                    String secondStr = temp.substring(temp.indexOf(")") + 1, temp.length());
                    newLine = format("{} ConfigCenter.{}{}", line.split(RESOURCE_CONSTANTS_HELPER_GET)[0], cache.getFieldName(), secondStr);

                }else{
                    newLine = format("{}= ConfigCenter.{};", line.split("=")[0], cache.getFieldName());
                }

                break;
            case MAP:
                //if (!line.contains("resourceManager.getOptionValue(")) {
                //    allLinesNew.add(line);
                //    return false;
                //}
                //
                //int tempIndex = line.indexOf("resourceManager.getOptionValue)");
                //
                //newLine = format("{}= ConfigCenter.{}.get({}", line.split("=")[0], cache.getFieldName(), line.split(",")[1].trim());

                newLine = line;
                break;
            default:
                return false;
        }

        if(newLine == null){
            newLine = line;
        }
        allLinesNew.add(newLine);
        log.info("Replace " + line + "  => " + newLine);
        return true;
    }
}
