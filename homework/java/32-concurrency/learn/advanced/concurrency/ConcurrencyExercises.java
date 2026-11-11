package learn.advanced.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 第32课三级练习（自敲，先预测再跑）：
 * 1) countSafely(2, 10) → 20
 * 2) SynchronizedCounter，4×1000 → 4000
 * 3) 两 Future 2+3 → 5，finally shutdown
 */
public final class ConcurrencyExercises {
    private ConcurrencyExercises() {}

    public static void main(String[] args) throws Exception {
        exercise1();
        exercise2();
        exercise3();
    }

    /** 练习1：模仿 — 任务数 2、每任务 +10，预测精确 20 */
    static void exercise1() throws Exception {
        // 预测：2 * 10 = 20
        int result = CounterExperiment.countSafely(2, 10);
        System.out.println("练习1：countSafely(2,10)=" + result);
    }

    /** 练习2：修改 — 用 synchronized 方法替代 AtomicInteger */
    static void exercise2() throws Exception {
        SynchronizedCounter counter = new SynchronizedCounter();
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < 1000; j++) {
                        counter.add();
                    }
                }));
            }
            for (Future<?> future : futures) future.get();
            // 预测：4 * 1000 = 4000
            System.out.println("练习2：SynchronizedCounter=" + counter.get());
        } finally {
            executor.shutdownNow();
        }
    }

    /** 练习3：独立 — 两个有返回值的 Future，汇总后关闭线程池 */
    static void exercise3() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Future<Integer> a = pool.submit(() -> 2);
            Future<Integer> b = pool.submit(() -> 3);
            // 预测：2 + 3 = 5
            System.out.println("练习3：两 Future 汇总=" + (a.get() + b.get()));
        } finally {
            pool.shutdown();
        }
    }

    /** 练习2用：用 synchronized 保护共享 int */
    static final class SynchronizedCounter {
        private int value;

        synchronized void add() {
            value++;
        }

        synchronized int get() {
            return value;
        }
    }
}
