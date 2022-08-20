package bacardi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ali.b2b.crm.base.resource.OptionDo;
import com.ali.martini.common.ResourceDo;
import com.taobao.diamond.utils.JSONUtils;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static bacardi.Cache.LIST_OPTION_DO;
import static bacardi.Cache.LIST_RESOURCE_DO;
import static bacardi.Cache.LIST_RESOURCE_DO_GROUP;
import static bacardi.Cache.MAP_OPTION_DO;
import static bacardi.Cache.MAP_RESOURCE_DO;

/**
 * json 转 Cache 对象工具类
 * @author gangpeng.wgp
 * @date 2019/01/18 下午5:09
 */

public class CacheParseUtil {

    private static final Logger log = LoggerFactory.getLogger(CacheParseUtil.class);

    public static List<Cache> paraseJson(String cacheStr) {
        List<Cache> caches = new ArrayList<Cache>();

        try {
            caches = (List<Cache>)JSONUtils.deserializeObject(cacheStr, new TypeReference<ArrayList<Cache>>() {});

            for (Cache cache : caches) {
                if(!cache.isValid()){
                    continue;
                }

                if(cache.getCacheData() == null){
                    log.error("cacheData is null, {}", cache);
                    continue;
                }

                String cacheDataJsonStr;
                switch (cache.getValueType()) {
                    case LIST_RESOURCE_DO:
                        cacheDataJsonStr = JSONUtils.serializeObject(cache.getCacheData());
                        cache.setCacheData(JSONUtils.deserializeObject(cacheDataJsonStr, new TypeReference<ArrayList<ResourceDo>>() {}));
                        break;
                    case LIST_RESOURCE_DO_GROUP:
                        Map<String, List<ResourceDo>> resourceDoMap = new HashMap<>();
                        Map<String, List<Map<String, String>>> originCacheData = (Map<String, List<Map<String, String>>>)cache.getCacheData();
                        for (String key : originCacheData.keySet()) {
                            resourceDoMap.put(key, (List<ResourceDo>)JSONUtils.deserializeObject(JSONUtils.serializeObject(originCacheData.get(key)), new TypeReference<ArrayList<ResourceDo>>() {}));
                        }
                        cache.setCacheData(resourceDoMap);
                        break;
                    case LIST_OPTION_DO:
                        cacheDataJsonStr = JSONUtils.serializeObject(cache.getCacheData());
                        cache.setCacheData(JSONUtils.deserializeObject(cacheDataJsonStr, new TypeReference<ArrayList<OptionDo>>() {}));
                        break;
                    case MAP_RESOURCE_DO:
                        cacheDataJsonStr = JSONUtils.serializeObject(cache.getCacheData());
                        cache.setCacheData(JSONUtils.deserializeObject(cacheDataJsonStr, new TypeReference<Map<String, ResourceDo>>() {}));
                        break;
                    case MAP_OPTION_DO:
                        try {
                            cacheDataJsonStr = JSONUtils.serializeObject(cache.getCacheData());
                            Map<String, OptionDo> map = (Map<String, OptionDo>)JSONUtils.deserializeObject(cacheDataJsonStr, new TypeReference<Map<String, OptionDo>>() {});
                            cache.setCacheData(map);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        ;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return caches;
    }
}
