package learn.advanced.jvm;
public class JvmDemo {
    // 每次递归调用建立自己的栈帧；返回时逐层退出，输入保持很小。
    private static int depth(int n) { return n == 0 ? 0 : 1 + depth(n - 1); }
    public static void main(String[] args) throws Exception {
        // 对象分配与局部引用不同；不能据此推断“所有基本类型都在栈上”。
        Object object = new Object();
        System.out.println("调用深度：" + depth(3));
        System.out.println("对象可访问：" + (object != null));
        System.out.println("线程：" + Thread.currentThread().getName());
        System.out.println("最大堆字节数（环境相关）：" + Runtime.getRuntime().maxMemory());
        if (args.length > 0 && "--wait".equals(args[0])) {
            // 保持自己的进程存活，供另一个终端用 jcmd 检查；按 Enter 退出。
            System.out.println("PID（环境相关）：" + ProcessHandle.current().pid());
            System.out.println("按 Enter 退出");
            System.in.read();
        }
    }
}
