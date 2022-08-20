package hsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DateUtil;
import util.file.FileUtil;

/**
 * @author weigangpeng
 * @date 2018/04/26 下午2:38
 */

public class HsfConsumerTime {

    private static final Logger log = LoggerFactory.getLogger(HsfConsumerTime.class);

    public static void main(String[] args) throws IOException {

        convert(
            "/Users/weigangpeng/Desktop/bean2.log");
    }

    /**
     * 单个文件转换
     * @param autoconfigFile
     * @throws IOException
     */
    public static void convert(String autoconfigFile) throws IOException {

        List<ConsumerBean> beanList = new ArrayList<>();
        try {
            //读取文件，每一行放入list中
            List<String> lineList = FileUtil.readAllLines(autoconfigFile);

            for (String line : lineList) {

                if(! line.contains("HSF CONSUMER:")){
                    continue;
                }

                beanList.add(parseBean(line));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        // 升序比较器
        Comparator<ConsumerBean> valueComparator = new Comparator<ConsumerBean>() {
            @Override
            public int compare(ConsumerBean o1,
                               ConsumerBean o2) {
                return (int)(o2.getCost() - o1.getCost());
            }
        };


        // 排序
        beanList.sort(valueComparator);

        for (ConsumerBean consumerBean : beanList) {
            log.info(consumerBean.toString());
        }
    }

    private static ConsumerBean parseBean(String line) {
        line = line.trim();
        String logTime = line.substring(0, line.indexOf(" INFO")).trim();
        String serviceName = line.substring(line.indexOf("HSF CONSUMER:") + 13, line.indexOf(" start:")).trim();
        String startTime = line.substring(line.indexOf(" start:") + 7, line.indexOf(", end:")).trim();
        String endTime = line.substring(line.indexOf(", end:") + 6, line.length()).trim();
        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setServiceName(serviceName);
        consumerBean.setStartTimeStr(startTime);
        consumerBean.setEndTimeStr(endTime);
        consumerBean.setStartTime(DateUtil.parseDate(startTime, "yyyy-MM-dd HH:mm:ss.SSS"));
        consumerBean.setEndTime(DateUtil.parseDate(endTime, "yyyy-MM-dd HH:mm:ss.SSS"));
        consumerBean.setCost(consumerBean.getEndTime().getTime() - consumerBean.getStartTime().getTime());
        return consumerBean;
    }
}
