package threadpool;

import util.SleepUtil;

/**
 * @author gangpeng.wgp
 * @date 2021/10/20 5:28 PM
 */
public class MyTask implements Runnable {

    private String taskName;

    public MyTask(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public void run() {
        System.out.println(taskName + " 开始执行");
        System.err.println("       队列大小:" + MyThreadPool.getPool().getQueue().size());
        SleepUtil.randomSleep(3000);
        System.out.println("               " + taskName + " 执行结束");

    }
}
