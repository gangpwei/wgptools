package util;

public class SleepUtil {

    /**
     * 随机休眠
     * @param maxRandomSleepTime 最大休眠时间，单位毫秒
     */
    public static void randomSleep(long maxRandomSleepTime) {

        if (maxRandomSleepTime <= 0) {
            return;
        }

        try {
            long sleepTime = (long) (Math.random() * maxRandomSleepTime);
            Thread.sleep(sleepTime);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}