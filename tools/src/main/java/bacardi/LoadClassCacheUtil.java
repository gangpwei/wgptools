package bacardi;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.taobao.diamond.utils.JSONUtils;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 把缓存项里的变量，替换成具体的值
 * 例如：BizConstants.UPGRADE_PARAMS_JOIN_REFER => "joinRefer"
 *
 * @author gangpeng.wgp
 * @date 2019/01/17 上午11:02
 */

public class LoadClassCacheUtil {

    private static final Logger log = LoggerFactory.getLogger(LoadClassCacheUtil.class);
    
    public static List<Cache> getCacheList(String cacheStr){
        List<Cache> caches = new ArrayList<Cache>();


        try {
            caches = (List<Cache>)JSONUtils.deserializeObject(cacheStr, new TypeReference<ArrayList<Cache>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Cache cache : caches) {
            try {
                //第一字段
                if(Cache.KEY_CLASS.equals(cache.getKeyType())){
                    Class clz = Class.forName(cache.getKeyClass());
                    Field field = clz.getDeclaredField(cache.getKeyField());
                    field.setAccessible(true);
                    String keyValue;
                    if(Modifier.isStatic(field.getModifiers())){
                        keyValue = String.valueOf(field.get(clz));
                        System.out.println(cache.getKeyValue() + " =>" + field.get(clz) );
                    }else{
                        keyValue = String.valueOf(field.get(clz.newInstance()));
                        System.out.println(cache.getKeyValue() + " =>" + field.get(clz.newInstance()) );
                    }
                    cache.setRealKey(keyValue);
                }

                //第二字段
                if(Cache.KEY_CLASS.equals(cache.getKeyTypeParam2())){
                    Class clz = Class.forName(cache.getKeyClassParam2());
                    Field field = clz.getDeclaredField(cache.getKeyFieldParam2());
                    field.setAccessible(true);
                    String keyValue;
                    if(Modifier.isStatic(field.getModifiers())){
                        keyValue = String.valueOf(field.get(clz));
                        System.out.println(cache.getKeyValueParam2() + " =>" + field.get(clz) );
                    }else{
                        keyValue = String.valueOf(field.get(clz.newInstance()));
                        System.out.println(cache.getKeyValueParam2() + " =>" + field.get(clz.newInstance()) );
                    }
                    cache.setRealKeyParam2(keyValue);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Cache cache : caches) {
            if(Cache.KEY_CLASS.equals(cache.getKeyType()) && cache.getRealKey() == null){
                cache.setRealKey(cache.getKeyField());
            }
        }

        try {
            String str = JSONUtils.serializeObject(caches);
            log.info(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return caches;
    }
}
