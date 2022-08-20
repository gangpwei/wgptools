package aqs;

import java.util.concurrent.Semaphore;
import java.util.function.IntConsumer;


class FizzBuzz {
    private int n;

    private volatile int index = 1;

    private Semaphore numberSemaphore = new Semaphore(1);
    private Semaphore fizzSemaphore = new Semaphore(0);
    private Semaphore buzzSemaphore = new Semaphore(0);
    private Semaphore fizzbuzzSemaphore = new Semaphore(0);

    public FizzBuzz(int n) {
        this.n = n;
    }

    // printFizz.run() outputs "fizz".
    public void fizz(Runnable printFizz) throws InterruptedException {
        for (int i=1; i <= n; i++) {
            //System.out.println("׼����ȡ�� fizz" + i);
            if (i % 3 == 0 && i % 5 != 0) {
                /**
                 * ע�⣺
                 * numberSemaphore��acquire��������forѭ���ڲ�������Ҫ�ж��Ƿ��ֵ���ִ��
                 * ����������Ҫ���ֵ���ִ��ʱ����ִ��acquire����
                 * ��Ϊ��������number��������
                 */
                fizzSemaphore.acquire();
                printFizz.run();
                index++;
                numberSemaphore.release();
            }
        }
        //System.out.println("fizz end");
    }

    // printBuzz.run() outputs "buzz".
    public void buzz(Runnable printBuzz) throws InterruptedException {
        for (int i=1; i <= n; i++) {
            //System.out.println("׼����ȡ�� buzz" + i);
            if (i % 5 == 0 && i % 3 != 0) {
                buzzSemaphore.acquire();
                printBuzz.run();
                index++;
                numberSemaphore.release();
            }
        }
        //System.out.println("buzz end");
    }

    // printFizzBuzz.run() outputs "fizzbuzz".
    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        for (int i=1; i <= n; i++) {
            //System.out.println("׼����ȡ�� fizzbuzz" + i);
            if (i % 15 == 0 ) {
                fizzbuzzSemaphore.acquire();
                printFizzBuzz.run();
                index++;
                numberSemaphore.release();
            }
        }
        //System.out.println("fizzbuzz end");
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void number(IntConsumer printNumber) throws InterruptedException {
        for (int i=1; i <= n; i++) {
            numberSemaphore.acquire();
            if (i % 15 == 0) {
                fizzbuzzSemaphore.release();
            } else if (i % 3 == 0) {
                fizzSemaphore.release();
            } else if (i % 5 == 0) {
                buzzSemaphore.release();
            } else {
                if(i > n){
                    numberSemaphore.release();
                    return;
                }
                printNumber.accept(index);
                index++;
                numberSemaphore.release();
            }
        }
        //System.out.println("number end");
    }
}

