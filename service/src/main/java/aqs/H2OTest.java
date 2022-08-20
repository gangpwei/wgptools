package aqs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import threadpool.H2O_1;

public class H2OTest {
    public static void main(String[] args) {
        run();

    }

    private static void run() {
        H2O obj = new H2O();
        //H2O_1 obj = new H2O_1();

        ExecutorService executorService = Executors.newFixedThreadPool(200);
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                try {
                    obj.hydrogen(new Thread(() -> System.out.println("H")));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "线程H");
            executorService.submit(thread);
        }

        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(() -> {
                try {
                    obj.oxygen(new Thread(() -> System.out.println("O")));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "线程O");
            executorService.submit(thread);
        }
        executorService.shutdown();
    }
}