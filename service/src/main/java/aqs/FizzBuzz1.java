package aqs;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;

class FizzBuzz1 {
    Lock lock = new ReentrantLock();
    //fizz����������
    Condition fizz = lock.newCondition();
    //buzz����������
    Condition buzz = lock.newCondition();
    //fizzbuzz����������
    Condition fizzbuzz = lock.newCondition();
    //number����������
    Condition num = lock.newCondition();
    private int n;
    //ѭ�����±�
    private volatile int index;

    public FizzBuzz1(int n) {
        this.n = n;
        this.index = 1;
    }

    // printFizz.run() outputs "fizz".
    public void fizz(Runnable printFizz) throws InterruptedException {
        lock.lock();
        while (index <= n) {
            //���index�ܱ�3�����Ҳ��ܱ�5���� ���������� ��ӡfizz
            if (index % 3 == 0 && index % 5 != 0) {
                printFizz.run();
                //index+1 ���һ��������߳̽�����һ��ѭ�����ж�
                index++;
                buzz.signalAll();
                fizzbuzz.signalAll();
                num.signalAll();
            } else {
                //���������� �����������еȴ�������
                fizz.await();
            }
        }
        lock.unlock();
    }

    // printBuzz.run() outputs "buzz".
    public void buzz(Runnable printBuzz) throws InterruptedException {
        lock.lock();
        while (index <= n) {
            //���index�ܱ�5�����Ҳ��ܱ�3���� ���������� ��ӡbuzz
            if (index % 5 == 0 && index % 3 != 0) {
                printBuzz.run();
                //index+1 ���һ��������߳̽�����һ��ѭ�����ж�
                index++;
                fizz.signalAll();
                fizzbuzz.signalAll();
                num.signalAll();
            } else {
                //���������� �����������еȴ�������
                buzz.await();
            }
        }
        lock.unlock();
    }

    // printFizzBuzz.run() outputs "fizzbuzz".
    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        lock.lock();
        while (index <= n) {
            //���index�ܱ�3������Ҳ�ܱ�5���� ���������� ��ӡfizz
            if (index % 3 == 0 && index % 5 == 0) {
                printFizzBuzz.run();
                //index+1 ���һ��������߳̽�����һ��ѭ�����ж�
                index++;
                fizz.signalAll();
                buzz.signalAll();
                num.signalAll();
            } else {
                //���������� �����������еȴ�������
                fizzbuzz.await();
            }
        }
        lock.unlock();
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void number(IntConsumer printNumber) throws InterruptedException {
        lock.lock();
        while (index <= n) {
            //���index�Ȳ��ܱ�3����Ҳ���ܱ�5���� ���������� ��ӡ����
            if (index % 3 != 0 && index % 5 != 0) {
                printNumber.accept(index);
                //index+1 ���һ��������߳̽�����һ��ѭ�����ж�
                index++;
                fizz.signalAll();
                buzz.signalAll();
                fizzbuzz.signalAll();
            } else {
                //���������� �����������еȴ�������
                num.await();
            }
        }
        lock.unlock();
    }
}