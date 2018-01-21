import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author weigangpeng
 * @date 2018/01/20 ÏÂÎç12:13
 */

public class DeleteUnusedSqlMapFileUtilTest {
    @Test
    public void processForOmega() throws Exception {
        //SqlMapFileUtil.parserXml("/Users/weigangpeng/IdeaProjects/aegean_home/shixi/bundle/war/src/java/sqlmap-config.xml");

        //usedFileSet = getUsedFileList(new String[] {"sqlmap-config.xml", "sqlmap-config-citypartner.xml", "sqlmap-config-leads.xml", "sqlmap-config-message.xml", "sqlmap-config-muses.xml", "sqlmap-config-mysql-aegean.xml", "sqlmap-config-zeus.xml"});

        String warSrcPath = "/Users/weigangpeng/IdeaProjects/omega/bundle/war/src/java/";

        String sqlMapBase = warSrcPath + "sqlmap/";
        String[] fileArray =  new String[] {"sqlmap-config.xml", "sqlmap-config-mysql.xml", "sqlmap-config-alike.xml"};
        DeleteUnusedSqlMapFileUtil.process(warSrcPath, sqlMapBase, fileArray, true);
    }

}