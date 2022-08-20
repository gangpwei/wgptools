package searchlog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.StringUtil;
import util.file.FileUtil;

/**
 * 诚信通搜索日志分析
 * @author gangpeng.wgp
 * @date 2018/11/30 上午9:50
 */

public class SearchLogUtil {

    public  static List<String> process(String path){
        List<SearchLog> searchLogs = new ArrayList<>();
        List<String> lines = FileUtil.readAllLines(path);
        for (String line : lines) {
            if(!valid(line)){
                continue;
            }
            searchLogs.add(parseLog(line));
        }

        Map<String , Integer> loginIdCountMap = new HashMap<>();
        Map<String , String> loginIdOrgIdMap = new HashMap<>();
        for (SearchLog searchLog : searchLogs) {
            //System.out.println(searchLog);
            Integer count = loginIdCountMap.get(searchLog.getLoginId());
            if(count == null){
                count = 0;
            }
            count ++;
            loginIdCountMap.put(searchLog.getLoginId(), count);
            loginIdOrgIdMap.put(searchLog.getLoginId(), searchLog.getOrgId());
        }

        List<String> orgIdList = new ArrayList<>();
        for (String loginId : loginIdCountMap.keySet()) {
            if(loginIdCountMap.get(loginId) <3){
                continue;
            }
            orgIdList.add(loginIdOrgIdMap.get(loginId));
        }

        return orgIdList;



    }

    private static SearchLog parseLog(String line) {
        String[] array = line.split("\\|");
        SearchLog log = new SearchLog();
        log.setLoginId(array[5]);
        log.setOrgId(array[6]);
        log.setQueryStr(array[11]);
        return log;
    }

    private static boolean valid(String line) {
        if(StringUtil.isNotEmpty(line) && line.contains("ctp_agent_sales|noah_opp")){
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        //process("/Users/weigangpeng/Desktop/2.log");

        //process("/Users/weigangpeng/Desktop/3.log");

        //process("/Users/weigangpeng/Desktop/4.log");

        List<String> orgIdList = new ArrayList<>();
        File rootFile = new File("/Users/weigangpeng/Desktop/");
        for (File file : rootFile.listFiles()) {
            if(!file.getName().endsWith(".log")){
                continue;
            }

            orgIdList.addAll(process(file.getAbsolutePath()));
        }


        System.out.println(orgIdList);
    }
}
