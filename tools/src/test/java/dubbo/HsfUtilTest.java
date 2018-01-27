package dubbo;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author weigangpeng
 * @date 2018/01/23 ÏÂÎç6:00
 */

public class HsfUtilTest {
    @Test
    public void getBeanName() throws Exception {
    }

    @Test
    public void generateHsfXmlFile() throws Exception {
        String filePath="/Users/weigangpeng/IdeaProjects/omega/bundle/war/src/webroot/META-INF/autoconf/platform/biz-contact-provider.xml.vm";
        String version ="contact_hsf_version";
        String serviceNamesStr="com.ali.aurora.biz.contact.ChangeLogBo;com.ali.aurora.biz.contact.ChangeLogFactory;com.ali.aurora.biz.contact.ChangeLogQuery;com.ali.aurora.biz.contact.ContactOperationBo;com.ali.aurora.biz.contact.ContactPermissionService;com.ali.aurora.biz.contact.ContactQueryService;com.ali.aurora.biz.contact.ContactValidatorService;";
        HsfUtil.generateHsfXmlFile(filePath, serviceNamesStr, version);
    }

    @Test
    public void generateHsfXmlFile1() throws Exception {
    }

}