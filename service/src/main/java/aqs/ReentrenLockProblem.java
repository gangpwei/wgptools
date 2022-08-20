package aqs;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrenLockProblem {
    public static void main(String[] args) {
        // Ū����Ʒ����
        Product product = new Product();
        // ������������ҲҪnewһ��
        Producer producer = new Producer(product);
        Consumer consumer = new Consumer(product);

        // ���������̣߳���������������
        Thread thread = new Thread(producer, "������1");
        Thread thread1 = new Thread(consumer, "������1");
        Thread thread2 = new Thread(producer, "������2");
        Thread thread3 = new Thread(consumer, "������2");

        //��ʼ��
        thread.start();
        thread1.start();
        thread2.start();
        thread3.start();

    }
}

// ������
class Producer implements Runnable {
    // �õ���Ʒ
    private Product product;

    public Producer(Product product) {
        this.product = product;
    }

    @Override
    public void run() {
        // �Ҳ��ܣ������Ҿ�Ҫһֱ�������ҵļӰ�ѣ�����
        while (true) {
            product.produce();
        }
    }

}

// ������
class Consumer implements Runnable {
    // �϶�Ҫ�õ���Ʒ
    private Product product;

    public Consumer(Product product) {
        this.product = product;
    }

    @Override
    public void run() {
        // �Ҳ��ܣ�����Ǯ���ԣ����ж���������١�
        while (true) {
            product.sale();
        }

    }
}

// ��Ʒ��
class Product {
    // ��Ʒ����û�ˣ��͸�����
    private int count;
    // Ϊ�˱�֤��Ʒ������̷߳��ʵ�ʱ��ͬ����Ҫ������ʽ����
    private ReentrantLock lock = new ReentrantLock();
    // Ϊ�˱�֤��Ʒ��Ҫû����˻�����������˻�����������������������
    private Condition canProduce = lock.newCondition();
    private Condition canSale = lock.newCondition();

    // ������Ʒ
    public void produce() {
        // ����л�����ڣ�ֻ����������һ���ܽ���
        lock.lock();
        // �����꣬��Ĳ���ҲҪ������Ҫ��֤��һ���ᱻ�򿪣�����finally
        try {
            // û�����ǵĿ�����1
            while (count >= 4) {
                // ���ڵ��ڿ�棬����
                System.out.println(Thread.currentThread().getName() + " �������");
                // ��������ߵ��Űɣ���Ҳ���ҷſ�; ��ǰ�̹߳���
                canProduce.await();
            }
            // �������Ļ����Ǿ��Ǵ�ӡһ��������Ĵ��                         
            System.out.println(Thread.currentThread().getName() + " ������1��  " + (++count));
            // ���߸���������Ĺ��ѣ������治�ǿյ��ˣ��������������Ǹ�������һ��
            canSale.signal();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // һ��Ҫ�ͷ���
            lock.unlock();
            System.out.println(Thread.currentThread().getName() + " �ͷ���");
        }
    }

    // ���۷���
    public void sale() {
        // ��������һ��������������˱�BB
        lock.lock();
        try {
            // û����ʱ��
            while (count <= 0) {
                // ��������
                System.out.println(Thread.currentThread().getName()  + " û�����");
                // ����ȥ���ڿĸ����ӣ�������; ��ǰ�̹߳���
                canSale.await();
            }
            // �п��ʹ�ӡ����ʣ�µĿ��
            System.out.println(Thread.currentThread().getName() + " ������1��  " + (--count));
            // �����������������ڲ�����������ˣ����Զ�������
            canProduce.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // �ͷ���������
            lock.unlock();
            System.out.println(Thread.currentThread().getName() + " �ͷ���");
        }
    }
}

