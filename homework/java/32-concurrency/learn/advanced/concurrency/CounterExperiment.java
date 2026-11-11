package learn.advanced.concurrency;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicInteger;
public final class CounterExperiment {
    private CounterExperiment() { }
    public static int countSafely(int tasks, int increments) throws InterruptedException, ExecutionException {
        validate(tasks, increments);
        AtomicInteger counter = new AtomicInteger();
        ExecutorService executor = Executors.newFixedThreadPool(Math.max(1, Math.min(tasks, 16)));
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < tasks; i++) futures.add(executor.submit(() -> {
                // 原子自增不会丢失更新；每轮还要检查取消请求。
                repeatUntilComplete(increments, counter::incrementAndGet);
            }));
            // get 等待实际完成，不靠 sleep 猜测。
            for (Future<?> future : futures) future.get();
            return counter.get();
        } catch (InterruptedException e) {
            // Future.get 会清除调用者的中断标记，恢复后让上层决定如何退出。
            Thread.currentThread().interrupt();
            throw e;
        } finally { executor.shutdownNow(); }
    }

    public static int countUnsafely(int tasks, int increments) throws InterruptedException, ExecutionException {
        validate(tasks, increments);
        int[] counter = {0}; // 故意共享普通 int：演示竞态，不作为正确实现。
        ExecutorService executor = Executors.newFixedThreadPool(Math.max(1, Math.min(tasks, 16)));
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < tasks; i++) futures.add(executor.submit(() ->
                    repeatUntilComplete(increments, () -> counter[0]++)));
            for (Future<?> future : futures) future.get();
            return counter[0];
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        } finally { executor.shutdownNow(); }
    }

    static void repeatUntilComplete(int increments, Runnable increment) {
        for (int j = 0; j < increments; j++) {
            // shutdownNow 中断工作线程；不能让漫长的 CPU 循环继续运行。
            if (Thread.currentThread().isInterrupted()) throw new CancellationException("计数任务已中断");
            increment.run();
        }
    }

    private static void validate(int tasks, int increments) {
        if (tasks < 0 || tasks > 1000 || increments < 0 || (long) tasks * increments > Integer.MAX_VALUE)
            throw new IllegalArgumentException("tasks 或 increments 超出安全范围");
    }
}
