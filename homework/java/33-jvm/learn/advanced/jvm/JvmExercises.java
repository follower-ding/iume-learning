package learn.advanced.jvm;

/**
 * 第33课三级练习（自敲，先预测再跑）：
 * 1) depth(4) → 调用深度：4
 * 2) --wait + jcmd（见笔记记录）
 * 3) 解释 main 停在 System.in.read
 */
public final class JvmExercises {
    private JvmExercises() {}

    private static int depth(int n) {
        return n == 0 ? 0 : 1 + depth(n - 1);
    }

    public static void main(String[] args) {
        // 练习1：模仿 — depth(4)，预测「调用深度：4」
        System.out.println("练习1：调用深度：" + depth(4));
        Object object = new Object();
        System.out.println("对象可访问：" + (object != null));
        System.out.println("线程：" + Thread.currentThread().getName());
        System.out.println("最大堆字节数（环境相关）：" + Runtime.getRuntime().maxMemory());
    }
}
