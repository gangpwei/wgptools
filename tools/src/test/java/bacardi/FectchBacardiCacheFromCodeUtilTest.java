package bacardi;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taobao.diamond.utils.JSONUtils;
import org.junit.Test;

import static bacardi.Cache.INT;
import static bacardi.Cache.STRING;

/**
 * @author gangpeng.wgp
 * @date 2019/01/16 下午2:26
 */

public class FectchBacardiCacheFromCodeUtilTest {

    @Test
    public void setResourceConfigMap() throws Exception {
        Map<String, Cache> resourceMap = new HashMap<>();

        //String path = "/Users/weigangpeng/IdeaProjects/aegean_home/shixi/";

        String path = "/Users/weigangpeng/IdeaProjects/caesar";
        FectchBacardiCacheFromCodeUtil.getResourceConfigMap(new File(path), resourceMap);

        List<Cache> cacheList = new ArrayList<>();
        for (Cache key : resourceMap.values()) {
            cacheList.add(key);
        }

        fenye(cacheList, 100);

    }

    @Test
    public void getIntAndStrConfig() throws Exception {
        Map<String, Cache> resourceMap = new HashMap<>();

        String path = "/Users/weigangpeng/IdeaProjects/caesar";
        FectchBacardiCacheFromCodeUtil.getResourceConfigMap(new File(path), resourceMap);

        List<Cache> cacheList = new ArrayList<>();
        for (Cache key : resourceMap.values()) {
            if(INT.equals(key.getValueType()) || STRING.equals(key.getValueType())){
                cacheList.add(key);
            }
        }
        fenye(cacheList, 100);
    }

    /**
     * 分页打印json, 因为数据量太大 json长度会超出 java 字符串长度的限制
     * @param list
     * @param pagesize
     * @throws Exception
     */
    public static void fenye(List list, int pagesize) throws Exception {
        int totalcount = list.size();
        int pagecount = 0;
        int m = totalcount % pagesize;
        if (m > 0) {
            pagecount = totalcount / pagesize + 1;
        } else {
            pagecount = totalcount / pagesize;
        }
        for (int i = 1; i <= pagecount; i++) {
            if (m == 0) {
                List<Integer> subList = list.subList((i - 1) * pagesize, pagesize * (i));
                System.out.println(JSONUtils.serializeObject(subList));
            } else {
                if (i == pagecount) {
                    List<Integer> subList = list.subList((i - 1) * pagesize, totalcount);
                    System.out.println(JSONUtils.serializeObject(subList));
                } else {
                    List<Integer> subList = list.subList((i - 1) * pagesize, pagesize * (i));
                    System.out.println(JSONUtils.serializeObject(subList));
                }
            }
        }
    }

    @Test
    public void dealFileResourceConfig() throws Exception {

        Map<String, Cache> resourceMap = new HashMap<>();

        //biz/job
        FectchBacardiCacheFromCodeUtil.dealFileResourceConfig(new File("/Users/weigangpeng/IdeaProjects/aegean_home/shixi/biz/platform/src/java/com/ali/caesar/platform/domain/lead/repeatcheck/LeadsDistributeFilterUtil.java"), resourceMap);
        for (String key : resourceMap.keySet()) {
            System.out.println(key + " => " + resourceMap.get(key));
        }
    }

    @Test
    public void getType() throws Exception {
        System.out.println(FectchBacardiCacheFromCodeUtil.getType("\t\t\tList<ResourceDo> resList = (List<ResourceDo>) resourceManager.getResList(cachedType.getType());"));
    }

}