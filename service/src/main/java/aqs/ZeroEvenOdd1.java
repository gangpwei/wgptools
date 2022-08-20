package aqs;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;
class ZeroEvenOdd1 {
    private int n;
    private volatile int flag = 1;
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    
    public ZeroEvenOdd1(int n) {
        this.n = n;
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void zero(IntConsumer printNumber) throws InterruptedException {
        for (int i=1; i<=n; i++) {
            lock.lock();
            printNumber.accept(0);
            flag ++;
            condition.signalAll();
            //奇数和偶数时，flag为偶数，挂起
            while (flag % 2 == 0) {
                condition.await();
            }
            lock.unlock();
        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        for (int i=2; i<=n; i+=2) {
            lock.lock();
            while (flag % 4 != 0) {
                condition.await();
            }
            printNumber.accept(i);
            flag ++;
            condition.signalAll();
            lock.unlock();
        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        for (int i=1; i<=n; i+=2) {
            lock.lock();
            while (flag % 4 != 2) {
                condition.await();
            }
            printNumber.accept(i);
            flag ++;
            condition.signalAll();
            lock.unlock();
        }       
    }
}
