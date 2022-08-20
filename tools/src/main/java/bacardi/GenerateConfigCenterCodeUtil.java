package bacardi;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ali.b2b.crm.base.resource.OptionDo;
import com.ali.martini.common.ResourceDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.file.FileUtil;

import static bacardi.Cache.INT;
import static bacardi.Cache.LIST_OPTION_DO;
import static bacardi.Cache.LIST_RESOURCE_DO;
import static bacardi.Cache.LIST_RESOURCE_DO_GROUP;
import static bacardi.Cache.MAP;
import static bacardi.Cache.MAP_OPTION_DO;
import static bacardi.Cache.MAP_RESOURCE_DO;
import static bacardi.Cache.STRING;
import static util.StringUtil.format;

/**
 * 生成配置中心代码
 *
 * @author gangpeng.wgp
 * @date 2019/01/17 上午11:02
 */

public class GenerateConfigCenterCodeUtil {

    private static final Logger log = LoggerFactory.getLogger(GenerateConfigCenterCodeUtil.class);
    
    public static final String WRAP = "\n";

    public static final String TAB = "    ";

    public static final String START_LIST_RESOURCE_DO = "public static List<ResourceDo> {} = Arrays.asList(";

    public static final String START_LIST_RESOURCE_DO_GROUP = "public static Map<String, List<ResourceDo>> {} = new HashMap<String, List<ResourceDo>>() {\n"
        + "        {";
    public static final String START_LIST_OPTION_DO = "public static List<OptionDo> {} = Arrays.asList(";
    public static final String START_MAP_RESOURCE_DO = "public static Map<String,ResourceDo> {} = new HashMap<String, ResourceDo>() {\n"
        + "        {";
    public static final String START_MAP_OPTION_DO = "public static Map<String, OptionDo> {} = new HashMap<String, OptionDo>() {\n"
        + "        {";
    public static final String START_MAP = "public static Map<String, String> {} = new HashMap<String, String>() {\n"
        + "        {";

    public static final String START_INT = "public static Integer {} = ";
    public static final String START_STRING = "public static String {} = ";

    public static final String END_LIST = ");";
    public static final String END_MAP = "    }\n    };";

    public static String generateConfigCenterCode(String configCenterFile, String cacheStr) {
        List<Cache> caches = CacheParseUtil.paraseJson(cacheStr);

        Set<String> cacheKeySet = new HashSet<>();
        Map<String, String> fieldContentMap = new HashMap<>();
        for (Cache cache : caches) {

            if(cache.getKeyValueParam2() != null && cache.getKeyValueParam2().contains("BREAK_DAYS")){
                System.out.println("");
            }
            if(!cache.isValid()){
                continue;
            }

            if(cacheKeySet.contains(cache.getKey())){
                continue;
            }

            if(cache.getCacheData() == null){
                log.error("cacheData is null, {}", cache);
                continue;
            }

            String start = null;
            String content = null;
            String end = null;
            switch (cache.getValueType()) {
                case LIST_RESOURCE_DO:
                    start = START_LIST_RESOURCE_DO;
                    content = resourceDoListToStr((List)cache.getCacheData(), cache.getValueClass(), 2);
                    end = END_LIST;
                    break;
                case LIST_RESOURCE_DO_GROUP:
                    start = START_LIST_RESOURCE_DO_GROUP;
                    content = resourceDoGroupMapToStr((Map<String, List<ResourceDo>>)cache.getCacheData(), cache.getValueClass());
                    end = END_MAP;
                    break;
                case LIST_OPTION_DO:
                    start = START_LIST_OPTION_DO;
                    content = optionDoListToStr((List)cache.getCacheData());
                    end = END_LIST;
                    break;
                case MAP_OPTION_DO:
                    start = START_MAP_OPTION_DO;
                    content = optionDoMapToStr((Map<String, OptionDo>)cache.getCacheData());
                    end = END_MAP;
                    break;

                case INT:
                    start = START_INT;
                    content = cache.getCacheData() != null ? cache.getCacheData().toString() : null;
                    end = ";";
                    break;
                case STRING:
                    start = START_STRING;
                    content = cache.getCacheData() != null ? "\"" + cache.getCacheData().toString() + "\"" : null;
                    end = ";";
                    break;
                case MAP:
                    start = START_MAP;
                    content = mapToStr((Map<String, String>)cache.getCacheData());
                    end = END_MAP;
                    break;
                case MAP_RESOURCE_DO:
                    start = START_MAP_RESOURCE_DO;
                    content = resourceDoMapToStr((Map<String, ResourceDo>)cache.getCacheData(), cache.getValueClass() );
                    end = END_MAP;
                    break;
                default:
                    ;
            }

            StringBuilder sb = new StringBuilder();
            if(start != null && content!= null && end != null){

                if(cache.getFieldName() == null){
                    System.out.println("");
                }

                if(cache.isConstant()){
                    sb.append(WRAP).append(TAB).append(format(start, cache.getFieldName())).append(content).append(end);
                }else{
                    sb.append(WRAP).append(TAB).append(format(start, cache.getFieldName())).append(WRAP).append(content).append(WRAP).append(TAB).append(end);
                }
            }


            System.out.println(sb.toString());

            fieldContentMap.put(cache.getFieldName(), sb.toString());
            cacheKeySet.add(cache.getKey());
        }

        addCodeToConfigCenter(configCenterFile, fieldContentMap);
        return null;
    }

    private static void addCodeToConfigCenter(String configCenterFile, Map<String, String> fieldContentMap) {
        List<String> allLines = FileUtil.readAllLines(new File(configCenterFile).getPath());
        Set<String> cacheKeySet = new HashSet<>();
        int addIndex = 0;
        for (int i = 0; i < allLines.size(); i++) {
            String line = allLines.get(i);
            if(line.contains("private static ConfigCenter instance = new ConfigCenter();")){
                addIndex = i +1;
            }

            if(!line.trim().startsWith("public static ") || !line.contains("=")){
                continue;
            }

            String temp = line.split("=")[0].trim();
            String field = null;
            try {
                field = temp.substring(temp.lastIndexOf(" ") + 1, temp.length());
            } catch (Exception e) {
                e.printStackTrace();
            }
            cacheKeySet.add(field);
        }

        StringBuilder sb = new StringBuilder();
        for (String key : fieldContentMap.keySet()) {
            if(!cacheKeySet.contains(key)){
                sb.append(fieldContentMap.get(key)).append(WRAP);
            }
        }

        allLines.add(addIndex, sb.toString());

        String targetFilePath = configCenterFile.replace(".java", ".java");
        File newfile = new File(targetFilePath);
        if (newfile.exists()) {
            newfile.delete();
        }
        FileUtil.writeFileGBK(targetFilePath, allLines);

    }

    private static String resourceDoGroupMapToStr(Map<String, List<ResourceDo>> cacheData, String clazName) {
        StringBuilder sb = new StringBuilder();
        for (String key : cacheData.keySet()) {
            List<ResourceDo> item = cacheData.get(key);
            String str = format("put(\"{}\", Arrays.asList(\n{}\n            ))", key, resourceDoListToStr(item, clazName,4));
            sb.append("            ").append(str).append(";\n");
        }
        if(sb.toString().endsWith("\n")){
            sb.delete(sb.length() -1, sb.length());
        }
        return sb.toString();
    }

    private static String resourceDoMapToStr(Map<String, ResourceDo> cacheData, String clazName) {
        StringBuilder sb = new StringBuilder();
        for (String key : cacheData.keySet()) {
            ResourceDo item = cacheData.get(key);
            String str = format("put(\"{}\", {})", key, resourceDoToStr(item, clazName));
            sb.append("            ").append(str).append(";\n");
        }
        if(sb.toString().endsWith("\n")){
            sb.delete(sb.length() -1, sb.length());
        }
        return sb.toString();
    }



    private static String resourceDoListToStr(List<ResourceDo> cacheData, String clazName, int tabCount) {
        StringBuilder sb = new StringBuilder();
        for (ResourceDo resourceDo : cacheData) {
            for (int i = 0; i < tabCount; i++) {
                sb.append("    ");
            }
            sb.append(resourceDoToStr(resourceDo, clazName)).append(",\n");
        }
        if(sb.toString().endsWith(",\n")){
            sb.delete(sb.length() -2, sb.length());
        }
        return sb.toString();
    }

    public static String resourceDoToStr(ResourceDo resourceDo ,String clazName) {
        String clazNameSimple = null;
        //if(clazName != null){
        //    try {
        //        clazNameSimple = clazName.substring(clazName.lastIndexOf(".")+1 , clazName.length());
        //    } catch (Exception e) {
        //        System.out.println("substring error, " + clazName );
        //    }
        //}else{
        //    clazNameSimple = "com.ali.martini.common.ResourceDo";
        //}

        clazNameSimple = "ResourceDo";

        if (resourceDo.getValue1() == null
            && resourceDo.getValue2() == null
            && resourceDo.getValue3() == null
            && resourceDo.getValue4() == null
            && resourceDo.getValue5() == null){
            return format("new {}(\"{}\", \"{}\")", clazNameSimple, resourceDo.getName(), val(resourceDo.getValue()));
        }else if (resourceDo.getValue2() == null
            && resourceDo.getValue3() == null
            && resourceDo.getValue4() == null
            && resourceDo.getValue5() == null){
            return format("new {}(\"{}\", \"{}\", \"{}\")", clazNameSimple, resourceDo.getName(), val(resourceDo.getValue()), val(resourceDo.getValue1()));
        }else if (resourceDo.getValue3() == null
            && resourceDo.getValue4() == null
            && resourceDo.getValue5() == null){
            return format("new {}(\"{}\", \"{}\", \"{}\", \"{}\")", clazNameSimple, resourceDo.getName(), val(resourceDo.getValue()), val(resourceDo.getValue1()), val(resourceDo.getValue2()));
        }
        //else if (resourceDo.getValue4() == null
        //    && resourceDo.getValue5() == null){
        //    return format("new {}(\"{}\", \"{}\", \"{}\", \"{}\", \"{}\")", resourceDo.getName(), val(resourceDo.getValue()), val(resourceDo.getValue1()), val(resourceDo.getValue2()), val(resourceDo.getValue3()));
        //}else if (resourceDo.getValue5() == null){
        //    return format("new {}(\"{}\", \"{}\", \"{}\", \"{}\", \"{}\", \"{}\")", resourceDo.getName(), val(resourceDo.getValue()), val(resourceDo.getValue1()), val(resourceDo.getValue2()), val(resourceDo.getValue3()), val(resourceDo.getValue4()));
        //}
        else {
            return format("new {}(\"{}\", \"{}\", \"{}\", \"{}\", \"{}\", \"{}\", \"{}\")", clazNameSimple, resourceDo.getName(), val(resourceDo.getValue()), val(resourceDo.getValue1()), val(resourceDo.getValue2()), val(resourceDo.getValue3()), val(resourceDo.getValue4()), val(resourceDo.getValue5()));
        }
    }

    private static String optionDoListToStr(List<OptionDo> cacheData) {
        StringBuilder sb = new StringBuilder();
        for (OptionDo optionDo : cacheData) {
            sb.append("        ").append(getOptionDoStr(optionDo)).append(",\n");
        }
        if(sb.toString().endsWith(",\n")){
            sb.delete(sb.length() -2, sb.length());
        }
        return sb.toString();
    }

    private static String getOptionDoStr(OptionDo optionDo) {
        return format("new OptionDo(\"{}\", \"{}\")", optionDo.getKey(), optionDo.getValue());
    }

    private static String optionDoMapToStr(Map<String, OptionDo> cacheData) {
        StringBuilder sb = new StringBuilder();
        for (String key : cacheData.keySet()) {
            OptionDo item = cacheData.get(key);
            String str = format("put(\"{}\", {})", key, getOptionDoStr(item));
            sb.append("            ").append(str).append(";\n");
        }
        if(sb.toString().endsWith("\n")){
            sb.delete(sb.length() -1, sb.length());
        }
        return sb.toString();
    }

    private static String mapToStr(Map<String, String> cacheData) {
        StringBuilder sb = new StringBuilder();
        for (String key : cacheData.keySet()) {
            String item = cacheData.get(key);
            String str = format("put(\"{}\", \"{}\")", key, item);
            sb.append("            ").append(str).append(";\n");
        }
        if(sb.toString().endsWith("\n")){
            sb.delete(sb.length() -1, sb.length());
        }
        return sb.toString();
    }

    public static String val(String str){

        return str == null ? "": str.replaceAll("\"","\\\\\"");
    }
}
