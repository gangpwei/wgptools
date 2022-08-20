package aqs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FizzBuzzTest {
    public static void main(String[] args) {
        //FizzBuzz obj = new FizzBuzz(5);
        //FizzBuzz obj = new FizzBuzz(3);

        run(31);

    }

    private static void run(int n) {
        FizzBuzz obj = new FizzBuzz(n);
        //FizzBuzz2 obj = new FizzBuzz2(31);

        Thread thread1 = new Thread(() -> {
            try {
                obj.number(System.out::println);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "线程1");
        Thread thread2 = new Thread(() -> {
            try {
                obj.fizz(new Thread(() -> System.out.println("fizz")));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "线程2");
        Thread thread3 = new Thread(() -> {
            try {
                obj.buzz(new Thread(() -> System.out.println("buzz")));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "线程3");
        Thread thread4 = new Thread(() -> {
            try {
                obj.fizzbuzz(new Thread(() -> System.out.println("fizzbuzz")));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "线程4");

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.submit(thread1);
        executorService.submit(thread2);
        executorService.submit(thread3);
        executorService.submit(thread4);
        executorService.shutdown();
    }
}