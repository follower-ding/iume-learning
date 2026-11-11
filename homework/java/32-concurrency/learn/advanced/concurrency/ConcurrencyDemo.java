package learn.advanced.concurrency;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.CountDownLatch;
public class ConcurrencyDemo {
    public static void main(String[] args) throws Exception {
        Thread thread = new Thread(() -> { /* Runnable 描述任务。 */ });
        thread.start(); // run() 仅是当前线程中的普通方法调用。
        thread.join();
        System.out.println("线程已结束：" + !thread.isAlive());
        System.out.println("安全计数：" + CounterExperiment.countSafely(4, 1000));
        System.out.println("不安全计数（结果不固定）：" + CounterExperiment.countUnsafely(4, 1000));
        ExecutorService virtual = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch ready = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        try {
            // 虚拟线程确实等待闩锁；这里代表可能阻塞的任务，不宣称 CPU 加速。
            Future<String> result = virtual.submit(() -> {
                ready.countDown();
                release.await();
                return "阻塞型任务完成";
            });
            ready.await();
            release.countDown();
            System.out.println(result.get());
        } finally {
            release.countDown(); // 即使主线程被中断，也让等待任务有机会退出。
            virtual.shutdownNow();
        }
    }
}
