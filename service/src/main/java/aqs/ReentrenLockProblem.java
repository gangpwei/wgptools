package aqs;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrenLockProblem {
    public static void main(String[] args) {
        // 弄个产品出来
        Product product = new Product();
        // 生产者消费者也要new一下
        Producer producer = new Producer(product);
        Consumer consumer = new Consumer(product);

        // 整了四条线程，俩生产，俩消费
        Thread thread = new Thread(producer, "生产者1");
        Thread thread1 = new Thread(consumer, "消费者1");
        Thread thread2 = new Thread(producer, "生产者2");
        Thread thread3 = new Thread(consumer, "消费者2");

        //开始！
        thread.start();
        thread1.start();
        thread2.start();
        thread3.start();

    }
}

// 生产者
class Producer implements Runnable {
    // 拿到产品
    private Product product;

    public Producer(Product product) {
        this.product = product;
    }

    @Override
    public void run() {
        // 我不管，我穷我就要一直生产，我的加班费！！！
        while (true) {
            product.produce();
        }
    }

}

// 消费者
class Consumer implements Runnable {
    // 肯定要拿到产品
    private Product product;

    public Consumer(Product product) {
        this.product = product;
    }

    @Override
    public void run() {
        // 我不管，我有钱任性，你有多少我买多少。
        while (true) {
            product.sale();
        }

    }
}

// 产品类
class Product {
    // 产品名都没了，就个数量
    private int count;
    // 为了保证产品被多个线程访问的时候同步，要锁，显式的锁
    private ReentrantLock lock = new ReentrantLock();
    // 为了保证产品不要没库存了还卖，超库存了还生产，搞俩监视器，盯着
    private Condition canProduce = lock.newCondition();
    private Condition canSale = lock.newCondition();

    // 生产产品
    public void produce() {
        // 外人谢绝入内，只有我们生产一线能进来
        lock.lock();
        // 生产完，别的部门也要进来，要保证锁一定会被打开，必须finally
        try {
            // 没错，我们的库存就是1
            while (count >= 4) {
                // 大于等于库存，报警
                System.out.println(Thread.currentThread().getName() + " 库存满了");
                // 这个生产线等着吧，锁也给我放开; 当前线程挂起
                canProduce.await();
            }
            // 不到库存的话，那就是打印一下生产完的存库                         
            System.out.println(Thread.currentThread().getName() + " 生产了1个  " + (++count));
            // 告诉隔壁销售组的工友，这里库存不是空的了，反正至少有我们刚生产的一个
            canSale.signal();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 一定要释放锁
            lock.unlock();
            System.out.println(Thread.currentThread().getName() + " 释放锁");
        }
    }

    // 销售方法
    public void sale() {
        // 我们销售一组的人卖，其他人别BB
        lock.lock();
        try {
            // 没库存的时候
            while (count <= 0) {
                // 发出警报
                System.out.println(Thread.currentThread().getName()  + " 没库存了");
                // 我们去隔壁磕个瓜子，等生产; 当前线程挂起
                canSale.await();
            }
            // 有库存就打印卖完剩下的库存
            System.out.println(Thread.currentThread().getName() + " 消费了1个  " + (--count));
            // 告诉生产，至少现在不会是满库存了，可以动起来了
            canProduce.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 释放锁不能忘
            lock.unlock();
            System.out.println(Thread.currentThread().getName() + " 释放锁");
        }
    }
}

