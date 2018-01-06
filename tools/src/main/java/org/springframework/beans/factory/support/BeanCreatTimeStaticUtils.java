package org.springframework.beans.factory.support;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.alibaba.dubbo.common.utils.CollectionUtils;

import com.ali.caesar.platform.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;

/**
 * @author weigangpeng
 * @date 2017/11/19 下午12:57
 */

public class BeanCreatTimeStaticUtils {

    public static final String HSF_CONSUMER = "HSF_CONSUMER";
    public static final String HSF_PROVIDER = "HSF_PROVIDER";
    public static final String DUBBO_CONSUMER = "DUBBO_CONSUMER";
    public static final String DUBBO_PROVIDER = "DUBBO_PROVIDER";
    public static final String NOTIFY_CONSUMER = "NOTIFY_CONSUMER";
    public static final String NOTIFY_PROVIDER = "NOTIFY_PROVIDER";

    private static BeanCreatTimeStaticUtils instance = new BeanCreatTimeStaticUtils();

    private static final Logger log = LoggerFactory.getLogger(BeanCreatTimeStaticUtils.class);

    private BeanCreatTimeStaticUtils() {
    }

    public static BeanCreatTimeStaticUtils getInstance() {
        return instance;
    }

    /**
     * 每个bean创建时间map
     */
    public static Map<String, Integer> beanCreateCountTimeMap = new HashMap<>();

    /**
     * 每种类型的bean创建时间map
     */
    public static Map<String, TypeStatic> beanTypeCreateCountTimeMap;

    static {
        beanTypeCreateCountTimeMap = new TreeMap<>();
        beanTypeCreateCountTimeMap.put(HSF_CONSUMER, new TypeStatic(HSF_CONSUMER));
        beanTypeCreateCountTimeMap.put(HSF_PROVIDER, new TypeStatic(HSF_PROVIDER));
        beanTypeCreateCountTimeMap.put(DUBBO_CONSUMER, new TypeStatic(DUBBO_CONSUMER));
        beanTypeCreateCountTimeMap.put(DUBBO_PROVIDER, new TypeStatic(DUBBO_PROVIDER));
        beanTypeCreateCountTimeMap.put(NOTIFY_CONSUMER, new TypeStatic(NOTIFY_CONSUMER));
        beanTypeCreateCountTimeMap.put(NOTIFY_PROVIDER, new TypeStatic(NOTIFY_PROVIDER));
    }

    public static void logBeanCreatCost(long start, String beanName, RootBeanDefinition mbd) {

        Long cost = System.currentTimeMillis() - start;

        beanCreateCountTimeMap.put(beanName, cost.intValue());

        if ("com.alibaba.tag.TagManageRemoteServiceInterface".equals(beanName)
            || "tagDomain".equals(beanName)
            || "caenirDubboCustomerRepeatInfoService".equals(beanName)

            ) {
            System.out.println("");
        }

        Field[] fields = mbd.getBeanClass().getDeclaredFields();

        Set<String> fieldsSet = new HashSet<>();

        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                fieldsSet.add(field.getName());
            }
        }

        int propertiesCost = 0;
        if (mbd.getPropertyValues() != null && CollectionUtils.isNotEmpty(mbd.getPropertyValues().getPropertyValueList())) {
            String referenceName = null;
            for (Object o : mbd.getPropertyValues().getPropertyValueList()) {
                PropertyValue value = (PropertyValue)o;
                if (value.getValue() instanceof RuntimeBeanReference) {
                    referenceName = ((RuntimeBeanReference)value.getValue()).getBeanName();
                } else {
                    referenceName = value.getName();
                }
                if (!fieldsSet.contains(value.getName())) {
                    fieldsSet.add(referenceName);
                }
            }
        }

        StringBuilder sb = new StringBuilder(beanName);
        for (String fieldName : fieldsSet) {
            if (beanCreateCountTimeMap.get(fieldName) != null) {
                sb.append(" ").append(fieldName).append(":").append(beanCreateCountTimeMap.get(fieldName)).append(",");
                propertiesCost += beanCreateCountTimeMap.get(fieldName);
            }
        }
        if (!sb.toString().equals(beanName)) {
            log.info(sb.toString());
        }

        Long thisCost = cost - propertiesCost;

        thisCost = thisCost < 0 ? cost : thisCost;

        logBeanTypeCost(beanName, mbd, thisCost);

        log.info("=== cost {}ms , total {}ms, create bean {} ", String.format("%1$-6s", thisCost), String.format("%1$-6s", cost), beanName);
    }

    /**
     * 按bean类型统计创建时间
     *
     * @param beanName
     * @param mbd
     * @param thisCost
     */
    public static void logBeanTypeCost(String beanName, RootBeanDefinition mbd, Long thisCost) {
        String beanType = null;
        if (mbd.getBeanClass().getName().equals("com.taobao.hsf.app.spring.util.HSFSpringConsumerBean")) {
            beanType = HSF_CONSUMER;
        } else if (mbd.getBeanClass().getName().equals("com.taobao.hsf.app.spring.util.HSFSpringProviderBean")) {
            beanType = HSF_PROVIDER;
            for (Object o : mbd.getPropertyValues().getPropertyValueList()) {
                PropertyValue value = (PropertyValue)o;
                if (value.getName().equals("serviceInterface")) {
                    if (value.getValue() instanceof TypedStringValue) {
                        beanName = ((TypedStringValue)value.getValue()).getValue();
                    }
                }
            }
        } else if (mbd.getBeanClass().getName().equals("com.alibaba.dubbo.config.spring.ReferenceBean")) {
            beanType = DUBBO_CONSUMER;
        } else if (mbd.getBeanClass().getName().equals("com.alibaba.dubbo.config.spring.ServiceBean")) {
            beanType = DUBBO_PROVIDER;
        } else if (mbd.getBeanClass().getName().equals("com.taobao.notify.remotingclient.NotifyManagerBean")) {
            for (Object o : mbd.getPropertyValues().getPropertyValueList()) {
                PropertyValue value = (PropertyValue)o;
                if (value.getName().equals("subscribeMessages")) {
                    beanType = NOTIFY_CONSUMER;
                } else if (value.getName().equals("publishTopics")) {
                    beanType = NOTIFY_PROVIDER;
                }
            }
        }

        if (StringUtil.isNotEmpty(beanType)) {
            beanTypeCreateCountTimeMap.get(beanType).getBeanCostMap().put(beanName, thisCost.intValue());
        }

        if ("bizBeanFactory".equals(beanName)) {
            for (String beanTypeKey : beanTypeCreateCountTimeMap.keySet()) {
                int totalCost = 0;
                TypeStatic typeStatic = beanTypeCreateCountTimeMap.get(beanTypeKey);

                Map<String, Integer> map = typeStatic.getBeanCostMap();
                for (String beanKey : map.keySet()) {
                    totalCost += map.get(beanKey);
                }

                typeStatic.setTotalCost(totalCost);

                log.info("\n\n");
                log.info("== 统计, 类型:{} 数量: {} 耗时: {}ms", String.format("%1$-20s", beanTypeKey), String.format("%1$-5s", map.size()), String.format("%1$-6s", totalCost));
                //for (String beanKey : map.keySet()) {
                //    log.info("       {}, 耗时： {} ms" , beanKey, map.get(beanKey) );
                //}

                sortAndLogBeanCost(map);
            }

            sortAndLogBeanTypeCost();

        }
    }

    /**
     * 按类型汇总bean的创建时间，并排序、打印日志
     */
    private static void sortAndLogBeanTypeCost() {
        List<Entry<String, TypeStatic>> list = new ArrayList<>(beanTypeCreateCountTimeMap.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list, new Comparator<Entry<String, TypeStatic>>() {
            //降序排序
            public int compare(Entry<String, TypeStatic> o1,
                               Entry<String, TypeStatic> o2) {
                return o2.getValue().getTotalCost().compareTo(o1.getValue().getTotalCost());
            }
        });

        log.info("\n\n");
        for (Entry<String, TypeStatic> mapping : list) {
            TypeStatic typeStatic = mapping.getValue();

            log.info("== 统计, 类型:{} 数量: {} 耗时: {}ms", String.format("%1$-20s", typeStatic.getType()), String.format("%1$-5s", typeStatic.getBeanCostMap().size()), String.format("%1$-8s", typeStatic.getTotalCost()));
        }

        //for (String beanTypeKey : beanTypeCreateCountTimeMap.keySet()) {
        //
        //    TypeStatic typeStatic = beanTypeCreateCountTimeMap.get(beanTypeKey);
        //
        //    log.info("== 统计, 类型:{} 数量:{} 耗时: {}ms" , String.format("%1$-20s", beanTypeKey), String.format("%1$-5s", typeStatic.getBeanCostMap().size()), String.format("%1$-6s", typeStatic.getTotalCost()) );
        //}
    }

    /**
     * 某种类型里，所有bean的创建时间排序、打印日志
     */
    private static void sortAndLogBeanCost(Map<String, Integer> map) {
        List<Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list, new Comparator<Entry<String, Integer>>() {
            //降序排序
            public int compare(Entry<String, Integer> o1,
                               Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }

        });

        for (Entry<String, Integer> mapping : list) {
            log.info("              {}, 耗时： {} ms", mapping.getKey(), mapping.getValue());
        }
    }

    static class TypeStatic {
        /**
         * bean 类型
         */
        private String type;

        /**
         * 所有此类型的bean总耗时
         */
        private Integer totalCost = 0;

        /**
         * bean创建时间map
         */
        Map<String, Integer> beanCostMap = new TreeMap<>();

        public TypeStatic() {
        }

        public TypeStatic(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(Integer totalCost) {
            this.totalCost = totalCost;
        }

        public Map<String, Integer> getBeanCostMap() {
            return beanCostMap;
        }

        public void setBeanCostMap(Map<String, Integer> beanCostMap) {
            this.beanCostMap = beanCostMap;
        }
    }

}
