package bacardi;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

/**
 * 从应用代码中取缓存配置
 *
 * @author gangpeng.wgp
 * @date 2019/01/16 上午11:09
 */

public class FectchBacardiCacheFromCodeUtil {

    public static final String RESOURCE_CONSTANTS_HELPER_GET = "resourceConstantsHelper.get";

    public static boolean printDetail = false;

    public static Set<String> filterSet = new HashSet<String>() {
        {
            add("CLOT_APP_USER");
            add("CLOT_APP_ROLE");
            add("CLOT_APP_ROLE_BASE_ROLE");
            add("CLOT_APP_MENU");
            add("CLOT_APP_MENU_GROUP");
            add("CLOT_APP_PERMISSION");
            add("CLOT_APP_ROLE_PERMISSION");
            add("CLOT_APP_USER_ROLE_ORG");
            add("CLOT_APP_ORG");
            add("CLOT_APP_RES_CONTROL_RULE");
            add("CLOT_APP_RES_CONTROL");
            add("CLOT_APP_BIZ_SCOPE_MAPPING");
            add("CLOT_APP_USER_GENDER");
            add("CLOT_APP_USER_STATUS");
            add("CLOT_APP_ORG_SITE_MAPPING");

            add("APP_USER");
            add("APP_ROLE");
            add("APP_ROLE_BASE_ROLE");
            add("APP_MENU");
            add("APP_MENU_GROUP");
            add("APP_PERMISSION");
            add("APP_ROLE_PERMISSION");
            add("APP_USER_ROLE_ORG");
            add("APP_ORG");
            add("APP_RES_CONTROL_RULE");
            add("APP_RES_CONTROL");
            add("APP_BIZ_SCOPE_MAPPING");
            add("APP_USER_GENDER");
            add("APP_USER_STATUS");
            add("APP_ORG_SITE_MAPPING");
        }
    };


    public static Map<String, Cache> getResourceConfigMap(File projectRootPath, Map<String, Cache> map) {
        map = map == null ? new HashMap<>(200) : map;
        File[] files = projectRootPath.listFiles();
        if (files == null) {
            return map;
        }
        for (File childFile : files) {
            if (childFile.isDirectory()) {
                getResourceConfigMap(childFile, map);
            } else {
                dealFileResourceConfig(childFile, map);
            }
        }
        return map;
    }

    public static void dealFileResourceConfig(File childFile, Map<String, Cache> map) {
        if (!childFile.getName().endsWith(".java") || childFile.getPath().contains("/java.test/")) {
            return;
        }
        List<String> allLines = FileUtil.readAllLines(childFile.getPath());

        boolean unFinishLine = false;
        String tempLine = null;

        Map<String, String> clzMap = getClzMap(allLines);

        for (String line : allLines) {
            if (!unFinishLine) {
                if (!isBacardiCache(line)) {
                    continue;
                }


                if(line.contains("BizConstants.RESKEY_MOVEOCEANELIMIT")){
                    System.out.println("");
                }
                Cache cache = getCache(line, childFile.getPath(), clzMap);

                if (cache == null) {
                    continue;
                }


                String key = null;
                if(INT.equals(cache.getValueType()) || STRING.equals(cache.getValueType())){
                    key = cache.getKeyValueParam2();
                }else{
                    key = cache.getKeyValue();
                }

                if (map.containsKey(key)) {
                    Cache cacheExisit = map.get(key);
                    if(!cacheExisit.getFiles().contains(childFile.getPath())){
                        cacheExisit.addFile(childFile.getPath());
                    }
                    continue;
                }

                if (key.endsWith(".") || key.length() == 0) {
                    //System.err.println("无法获取key, file:" + childFile);
                    //System.err.println(line);
                    unFinishLine = true;
                    tempLine = line;
                    continue;
                }
                map.put(key, cache);
            }
            //分行写的代码
            else {
                tempLine = tempLine + line.trim();
                if (line.contains(");")) {

                    if (!isBacardiCache(tempLine)) {
                        continue;
                    }

                    Cache cache = getCache(tempLine, childFile.getPath(), clzMap);

                    if (cache == null) {
                        continue;
                    }

                    String key = null;
                    if(INT.equals(cache.getValueType()) || STRING.equals(cache.getValueType())){
                        key = cache.getKeyValueParam2();
                    }else{
                        key = cache.getKeyValue();
                    }

                    if (map.containsKey(key)) {
                        Cache cacheExisit = map.get(key);
                        if(!cacheExisit.getFiles().contains(childFile.getPath())){
                            cacheExisit.addFile(childFile.getPath());
                        }
                        continue;
                    }

                    if (key.length() == 0) {
                        System.err.println("无法获取key, file:" + childFile);
                        System.err.println(tempLine);

                        continue;
                    }
                    map.put(key, cache);

                    unFinishLine = false;
                    tempLine = null;
                }
            }
        }
    }

    public static Map<String, String> getClzMap(List<String> allLines) {
        Map<String, String> clzMap = new HashMap<>();
        for (String line : allLines) {
            if (line.contains("import ") && line.contains(";")) {
                String clzName = line.substring(line.lastIndexOf(".") + 1, line.indexOf(";"));
                String fullClzName = line.substring(line.lastIndexOf("import ") + 7, line.indexOf(";"));
                clzMap.put(clzName, fullClzName);
            }
        }
        return clzMap;
    }

    public static Cache getCache(String line, String file, Map<String, String> clzMap) {

        String type = getType(line);

        if (type == null) {
            return null;
        }

        String key = "";
        String express = "";
        String orginLine = line;
        switch (type) {
            case LIST_RESOURCE_DO:
                express = "resourceManager.getResList";
                break;
            case LIST_RESOURCE_DO_GROUP:
                express = "resourceManager.getResListByGroup";
                break;
            case LIST_OPTION_DO:
                if(line.contains("resourceManager.getOptionList")){
                    express = "resourceManager.getOptionList";
                }else if(line.contains("resourceManager.getResList")){
                    express = "resourceManager.getResList";
                }
                break;
            case MAP_OPTION_DO:
                express = "resourceManager.getResItem";
                break;
            case INT:
            case STRING:
                return getCacheForConstantsHelper(type, line, file, clzMap);
            case MAP:
                if(line.contains("resourceManager.getOptionValue")){
                    express = "resourceManager.getOptionValue";
                }else if(line.contains("resourceManager.getResList")){
                    express = "resourceManager.getResList";
                }
                break;
            case MAP_RESOURCE_DO:
                express = "resourceManager.getResItem";
                break;
            default:
                express = "";
        }

        line = line.substring(line.indexOf(express) + express.length() + 1, line.length());

        //字符串变量
        if (line.startsWith("\"")) {
            String tempLine = line.substring(1, line.length());
            key = line.substring(1, tempLine.indexOf("\"") + 1);
            //过滤权限缓存项
            if (isAppCache(key)) {
                return null;
            }
            return new Cache(KEY_STR, key.trim(), null, null, type, key.trim(), file);
        } else {
            String[] params = line.split(",");
            if (params.length >= 2) {
                key = params[0];

            } else {
                key = line.replaceAll("\\)", "").replaceAll(";", "");
            }

            key = key.replaceAll("\"", "").replaceAll(",", "").replaceAll("\\{", "").replaceAll("\\)", "");

            //不带类名的变量
            if (!key.contains(".")) {
                //小写的跳过，很可能是变量，不是字符串常量
                if (key.length() > 0 && Character.isLowerCase(key.toCharArray()[0])) {
                    //System.err.println("小写的key, " + orginLine );
                    return null;
                }
                //过滤权限缓存项
                if (isAppCache(key)) {
                    return null;
                }

                String clzName = file.substring(file.lastIndexOf("src/java/") + 9, file.length() - 5);
                clzName = clzName.replaceAll("\\/", ".");
                return new Cache(KEY_CLASS, clzName + "." + key, clzName, key.trim(), type, null, file);
            }
            //带类名的变量
            else {

                String fullClzName = clzMap.get(key.substring(0, key.indexOf(".")).trim());
                if (fullClzName == null) {
                    if (printDetail) {
                        //找不到类引入，取当前类的包路径
                        System.err.println("fullClzName return null, " + orginLine);
                    }
                    String packageName = file.substring(file.lastIndexOf("src/java/") + 9, file.lastIndexOf("/"));
                    packageName = packageName.replaceAll("\\/", ".");
                    String field = key.substring(key.indexOf(".") + 1, key.length());

                    String keyValue = packageName + "." + key.trim();
                    String clzName = keyValue.substring(0, keyValue.lastIndexOf("."));
                    return new Cache(KEY_CLASS, keyValue, clzName, field.trim(), type, null, file);
                } else {

                    String field = key.substring(key.indexOf(".") + 1, key.length());

                    //过滤权限缓存项
                    if (isAppCache(field)) {
                        return null;
                    }

                    String keyValue = fullClzName + "." + field;
                    return new Cache(KEY_CLASS, keyValue, fullClzName, field.trim(), type, null, file);
                }
            }
        }
    }

    private static boolean isAppCache(String key) {
        return filterSet.contains(key.trim()) || key.trim().startsWith("APP_");
    }

    private static Cache getCacheForConstantsHelper(String type, String line, String file, Map<String, String> clzMap) {
        if (type == null) {
            return null;
        }

        String key = "";
        String express = "";
        String orginLine = line;
        switch (type) {
            case INT:
                express = "resourceConstantsHelper.getIntValue";
                break;
            case STRING:
                express = "resourceConstantsHelper.getStringValue";
            break;

            default:
                express = "";
        }

        String paramPart = line.substring(line.indexOf(express) + express.length() + 1, line.length());
        //参数数组
        String[] params = paramPart.split(",");
        if(params.length <2){
            return null;
        }

        Cache cache = new Cache();
        key = params[0].trim();

        //字符串变量
        if (key.startsWith("\"")) {
            String tempLine = key.substring(1, key.length());
            key = key.substring(1, tempLine.indexOf("\"") + 1);
            //过滤权限缓存项
            if (isAppCache(key)) {
                return null;
            }
            cache = new Cache(KEY_STR, key.trim(), null, null, type, key.trim(), file);
        } else {


            //不带类名的变量
            if (!key.contains(".")) {
                //小写的跳过，很可能是变量，不是字符串常量
                if (key.length() > 0 && Character.isLowerCase(key.toCharArray()[0])) {
                    //System.err.println("小写的key, " + orginLine );
                    return null;
                }
                //过滤权限缓存项
                if (filterSet.contains(key)) {
                    return null;
                }

                String clzName = file.substring(file.lastIndexOf("src/java/") + 9, file.length() - 5);
                clzName = clzName.replaceAll("\\/", ".");
                cache = new Cache(KEY_CLASS, clzName + "." + key, clzName, key.trim(), type, null, file);
            }
            //带类名的变量
            else {

                String fullClzName = clzMap.get(key.substring(0, key.indexOf(".")).trim());
                if (fullClzName == null) {
                    if (printDetail) {
                        //找不到类引入，取当前类的包路径
                        System.err.println("fullClzName return null, " + orginLine);
                    }
                    String packageName = file.substring(file.lastIndexOf("src/java/") + 9, file.lastIndexOf("/"));
                    packageName = packageName.replaceAll("\\/", ".");
                    String field = key.substring(key.indexOf(".") + 1, key.length());

                    String keyValue = packageName + "." + key.trim();
                    String clzName = keyValue.substring(0, keyValue.lastIndexOf("."));
                    cache = new Cache(KEY_CLASS, keyValue, clzName, field.trim(), type, null, file);
                } else {

                    String field = key.substring(key.indexOf(".") + 1, key.length());

                    //过滤权限缓存项
                    if (filterSet.contains(field)) {
                        return null;
                    }

                    String keyValue = fullClzName + "." + field;
                    cache = new Cache(KEY_CLASS, keyValue, fullClzName, field.trim(), type, null, file);
                }
            }
        }

        key = params[1].trim();

        //字符串变量
        if (key.startsWith("\"")) {
            String tempLine = key.substring(1, key.length());
            key = key.substring(1, tempLine.indexOf("\"") + 1);
            //过滤权限缓存项
            if (isAppCache(key)) {
                return null;
            }
            cache.setKeyTypeParam2(KEY_STR);
            cache.setKeyValueParam2(key.trim());
            cache.setRealKeyParam2(key.trim());
        } else {

            //不带类名的变量
            if (!key.contains(".")) {
                //小写的跳过，很可能是变量，不是字符串常量
                if (key.length() > 0 && Character.isLowerCase(key.toCharArray()[0])) {
                    //System.err.println("小写的key, " + orginLine );
                    return null;
                }
                //过滤权限缓存项
                if (filterSet.contains(key)) {
                    return null;
                }

                String clzName = file.substring(file.lastIndexOf("src/java/") + 9, file.length() - 5);
                clzName = clzName.replaceAll("\\/", ".");

                cache.setKeyTypeParam2(KEY_CLASS);
                cache.setKeyValueParam2(clzName + "." + key);
                cache.setKeyClassParam2(clzName);
                cache.setKeyFieldParam2(key.trim());
            }
            //带类名的变量
            else {

                String fullClzName = clzMap.get(key.substring(0, key.indexOf(".")).trim());
                if (fullClzName == null) {
                    if (printDetail) {
                        //找不到类引入，取当前类的包路径
                        System.err.println("fullClzName return null, " + orginLine);
                    }
                    String packageName = file.substring(file.lastIndexOf("src/java/") + 9, file.lastIndexOf("/"));
                    packageName = packageName.replaceAll("\\/", ".");
                    String field = key.substring(key.indexOf(".") + 1, key.length());

                    String keyValue = packageName + "." + key.trim();
                    String clzName = keyValue.substring(0, keyValue.lastIndexOf("."));

                    cache.setKeyTypeParam2(KEY_CLASS);
                    cache.setKeyValueParam2(keyValue);
                    cache.setKeyClassParam2(clzName);
                    cache.setKeyFieldParam2(field.trim());
                } else {

                    String field = key.substring(key.indexOf(".") + 1, key.length());

                    //过滤权限缓存项
                    if (filterSet.contains(field)) {
                        return null;
                    }

                    String keyValue = fullClzName + "." + field;
                    cache.setKeyTypeParam2(KEY_CLASS);
                    cache.setKeyValueParam2(keyValue);
                    cache.setKeyClassParam2(fullClzName);
                    cache.setKeyFieldParam2(field.trim());
                }
            }
        }

        return cache;
    }

    public static String getType(String line) {
        if (!isBacardiCache(line)) {
            return null;
        }

        if (line.contains(".") && line.contains("()")) {
            return null;
        }

        if (line.contains("resourceManager.getResListByGroup")) {
            return LIST_RESOURCE_DO_GROUP;
        } else if (line.contains("resourceManager.getResList")) {
            if (line.contains("ResourceDo")) {
                return LIST_RESOURCE_DO;
            } else if (line.contains("OptionDo")) {
                return LIST_OPTION_DO;
            }
        } else if (line.contains("resourceManager.getOptionList")) {
            return LIST_OPTION_DO;
        } else if (line.contains("resourceManager.getOptionValue")) {
            return MAP;
        } else if (line.contains("resourceConstantsHelper.getIntValue")) {
            return INT;
        } else if (line.contains("resourceConstantsHelper.getStringValue")) {
            return STRING;
        }else if (line.contains("resourceManager.getResItem")) {
            if (line.contains("ResourceDo")) {
                return MAP_RESOURCE_DO;
            } else if (line.contains("OptionDo")) {
                return MAP_OPTION_DO;
            } else {
                if (printDetail) {
                    System.err.println("自定义 Object : " + line);
                }
            }
        }
        return null;
    }

    public static boolean isBacardiCache(String line){
        if (!line.contains("resourceManager.get") && !line.contains("resourceConstantsHelper.get")) {
            return false;
        }

        if(line.trim().startsWith("//")){
            return false;
        }
        return true;
    }

}
