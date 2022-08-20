package aqs;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;

/**
 *
 */
public class ZeroEvenOdd0 {
    private int n;

    private AtomicInteger index = new AtomicInteger(1);

    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public ZeroEvenOdd0(int n) {
        this.n = n;
    }

    /**
     * 注意事项：
     * 1、lock unlock 需在循环里面
     * 2、挂起是await 不是wait
     * 3、await之前要判断条件，否则容易出现线程一直挂起
     * 题解链接：https://leetcode.cn/problems/print-zero-even-odd/solution/by-be_a_better_coder-axp4/
     *
     * @param printNumber
     * @throws InterruptedException
     */
    // printNumber.accept(x) outputs "x", where x is an integer.
    public void zero(IntConsumer printNumber) throws InterruptedException {
        while (index.get() <= 2 * n) {
            lock.lock();
            printLock();
            try {
                sleep();
                if (index.get() % 2 != 1) {
                    printAwait();
                    condition.await();
                } else {
                    printNumber.accept(0);
                    index.incrementAndGet();
                    condition.signalAll();
                }
            } finally {
                printUnlock();
                lock.unlock();
            }
        }
    }

    /**
     * 打印奇数
     *
     * @param printNumber
     * @throws InterruptedException
     */
    public void odd(IntConsumer printNumber) throws InterruptedException {

        while (index.get() <= 2 * n) {
            lock.lock();
            printLock();
            try {
                sleep();

                if (index.get() % 4 != 2) {
                    printAwait();
                    condition.await();
                } else {
                    printNumber.accept(index.get() / 2);
                    index.incrementAndGet();
                    condition.signalAll();
                }
            } finally {
                lock.unlock();
                printUnlock();
            }
        }
    }

    /**
     * 打印偶数
     *
     * @param printNumber
     * @throws InterruptedException
     */
    public void even(IntConsumer printNumber) throws InterruptedException {
        while (index.get() <= 2 * n) {
            lock.lock();
            printLock();

            try {
                sleep();
                if (index.get() % 4 != 0) {
                    printAwait();
                    condition.await();
                } else {
                    printNumber.accept(index.get() / 2);
                    index.incrementAndGet();
                    condition.signalAll();
                }
            } finally {
                lock.unlock();
                printUnlock();
            }
        }
    }

    private void sleep() throws InterruptedException {
        //Thread.sleep(50);
    }

    public static void printAwait() {
        //System.out.println(Thread.currentThread().getName() + "挂起");
    }

    public static void printLock() {
        //System.out.println(Thread.currentThread().getName() + "加锁成功");
    }

    public static void printUnlock() {
        //System.out.println(Thread.currentThread().getName() + "释放锁");
    }
}