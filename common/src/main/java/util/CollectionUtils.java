/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package util;

import java.util.Collection;


public class CollectionUtils {

    public static boolean isEmpty(Collection<?> cc) {
        return cc == null || cc.size() == 0;
    }

    public static boolean isNotEmpty(Collection<?> cc) {
        return !isEmpty(cc);
    }

}
