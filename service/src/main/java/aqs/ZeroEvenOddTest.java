package aqs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZeroEvenOddTest {
    public static void main(String[] args) {
        //runUntilEnd(6);
        //runUntilEnd(5);

        //run(6);
        run(5);
        run(7);
    }

    private static void runUntilEnd(int n) {
        System.out.println("测试开始，入参：" + n);
        ZeroEvenOdd0 obj = new ZeroEvenOdd0(n);
        //ZeroEvenOdd1 obj = new ZeroEvenOdd1(6);

        List<Callable<Object>> taskList = new ArrayList<>();
        taskList.add(() -> {
            obj.zero(System.out::println);
            return null;
        });

        taskList.add(() -> {
            obj.odd(System.out::println);
            return null;
        });

        taskList.add(() -> {
            obj.even(System.out::println);
            return null;
        });

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        try {
            executorService.invokeAll(taskList);
            executorService.shutdown();
            System.out.println(" \n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void run(int n) {
        System.out.println("测试开始，入参：" + n);
        ZeroEvenOdd0 obj = new ZeroEvenOdd0(n);
        //ZeroEvenOdd1 obj = new ZeroEvenOdd1(6);

        Thread thread1 = new Thread(() -> {
            try {
                obj.zero(System.out::println);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "线程1");
        Thread thread2 = new Thread(() -> {
            try {
                obj.even(System.out::println);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "线程2");
        Thread thread3 = new Thread(() -> {
            try {
                obj.odd(System.out::println);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "线程3");

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        executorService.submit(thread1);
        executorService.submit(thread2);
        executorService.submit(thread3);
        executorService.shutdown();

    }
}