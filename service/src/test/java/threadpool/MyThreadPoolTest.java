package threadpool;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


/**
 * @author gangpeng.wgp
 * @date 2021/10/20 5:25 PM
 *
 * 测试结论：
 * 1、线程数达到最大后, 会加入队列
 * 2、队列如果满了，就会抛异常
 */
public class MyThreadPoolTest {

    @Test
    public void testMaxThread(){
        MyThreadPool.init();

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 1; i <= 800; i++) {
            tasks.add(new MyTask("任务" + i));
        }
        for (Runnable task : tasks) {
            new MyThreadPool().getPool().submit(task);
        }
        try {
            Thread.sleep(10000000000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
