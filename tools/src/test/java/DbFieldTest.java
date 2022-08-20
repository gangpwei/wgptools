import java.util.List;

import dubbo.HsfUtil;
import org.junit.Test;
import util.file.FileUtil;

/**
 * @author weigangpeng
 * @date 2018/01/23 ÏÂÎç6:00
 */

public class DbFieldTest {
    @Test
    public void getDbField() throws Exception {
        List<String> list = FileUtil.readAllLines("/Users/weigangpeng/IdeaProjects/noah/biz/platform/src/java/com/ali/caesar/platform/service/search/lead_new/table.sql");
        for (String line : list) {
            line = line.trim();
            if(!line.contains(" COMMENT")){
                continue;
            }
            String[] array = line.split(" ");
            if("ds".equalsIgnoreCase(array[0]) ){
                continue;
            }
            //System.out.println(array[0] + ",");
            //System.out.println(array[0].replace("_", "") + ",");
            if(array[0].contains("_")){
                array[0] = array[0] + " as " + array[0].replace("_", "") ;
            }
            System.out.println(array[0] + ",");
        }
    }



}