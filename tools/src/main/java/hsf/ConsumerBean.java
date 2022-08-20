package hsf;

import java.util.Date;
import static util.StringUtil.formatLen;

/**
 * @author weigangpeng
 * @date 2018/04/26 ÏÂÎç2:41
 */

public class ConsumerBean {

    private String serviceName;

    private Date startTime;

    private Date endTime;

    private String startTimeStr;

    private String endTimeStr;

    private long cost;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("");
        sb.append("serviceName='").append(formatLen(serviceName, 40)).append('\'');
        sb.append(", cost=").append(formatLen(String.valueOf(cost), 5)).append("ms");
        sb.append(", startTimeStr='").append(startTimeStr).append('\'');
        sb.append(", endTimeStr='").append(endTimeStr).append('\'');
        return sb.toString();
    }
}
