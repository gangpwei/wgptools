import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author weigangpeng
 * @date 2018/01/20 обнГ3:37
 */

public class FileTypeStatisticsUtilTest {
    @Test
    public void processForOmega() throws Exception {
        try {

            String base = "/Users/weigangpeng/IdeaProjects/noah";
            FileTypeStatisticsUtil.process(base, "java");
            FileTypeStatisticsUtil.process(base, "xml");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}