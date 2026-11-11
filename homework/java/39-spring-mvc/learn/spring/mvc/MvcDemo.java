package learn.spring.mvc;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class MvcDemo {
    public static Server start(int port) throws Exception {
        Path base = Files.createTempDirectory("mvc-lesson-");
        var tomcat = new Tomcat();
        tomcat.setBaseDir(base.toString());
        tomcat.setHostname("127.0.0.1");
        tomcat.setPort(port);
        tomcat.getConnector().setProperty("address", "127.0.0.1");
        // 传给 DispatcherServlet 的上下文尚未 refresh；它在 Servlet 初始化时启动。
        var webContext = new AnnotationConfigWebApplicationContext();
        webContext.register(WebConfig.class);
        try {
            var context = tomcat.addContext("", base.toString());
            // Maven exec:java 使用独立类加载器；Tomcat 清理时仍需看到其自身类。
            context.setParentClassLoader(MvcDemo.class.getClassLoader());
            Tomcat.addServlet(context, "dispatcher", new DispatcherServlet(webContext));
            context.addServletMappingDecoded("/", "dispatcher");
            tomcat.start();
            return new Server(tomcat, webContext, base, tomcat.getConnector().getLocalPort());
        } catch (Exception e) {
            try { tomcat.stop(); } catch (Exception cleanup) { e.addSuppressed(cleanup); }
            try { tomcat.destroy(); } catch (Exception cleanup) { e.addSuppressed(cleanup); }
            try { webContext.close(); } catch (Exception cleanup) { e.addSuppressed(cleanup); }
            try { deleteTree(base); } catch (Exception cleanup) { e.addSuppressed(cleanup); }
            throw e;
        }
    }

    private static void deleteTree(Path base) throws IOException {
        try (var paths = Files.walk(base)) {
            for (var path : paths.sorted((a, b) -> b.compareTo(a)).toList()) Files.deleteIfExists(path);
        }
    }

    public static final class Server implements AutoCloseable {
        private final Tomcat tomcat;
        private final AnnotationConfigWebApplicationContext context;
        private final Path base;
        private final int port;
        private boolean closed;

        public Server(Tomcat tomcat, AnnotationConfigWebApplicationContext context, Path base, int port) {
            this.tomcat = tomcat;
            this.context = context;
            this.base = base;
            this.port = port;
        }

        public int port() { return port; }
        public Path base() { return base; }

        // 关机 hook 和 main 正常退出可能同时调用；锁保护整个清理过程。
        @Override public synchronized void close() throws Exception {
            if (closed) return;
            closed = true;
            Exception failure = null;
            try { tomcat.stop(); } catch (Exception e) { failure = e; }
            try { tomcat.destroy(); } catch (Exception e) { failure = combine(failure, e); }
            try { context.close(); } catch (Exception e) { failure = combine(failure, e); }
            try { deleteTree(base); } catch (Exception e) { failure = combine(failure, e); }
            if (failure != null) throw failure;
        }

        private static Exception combine(Exception first, Exception next) {
            if (first == null) return next;
            first.addSuppressed(next);
            return first;
        }
    }

    static Thread shutdownHook(Server server) {
        return new Thread(() -> {
            try { server.close(); }
            catch (Exception e) { e.printStackTrace(System.err); }
        }, "mvc-demo-shutdown");
    }

    public static void main(String[] args) throws Exception {
        boolean serve = args.length > 0 && "--serve".equals(args[0]);
        try (var server = start(serve ? 8084 : 0)) {
            System.out.println("MVC 端口: " + server.port());
            if (serve) {
                Thread hook = shutdownHook(server);
                Runtime.getRuntime().addShutdownHook(hook);
                try {
                    System.out.println("按 Ctrl+C 或回车停止");
                    System.in.read();
                } finally {
                    // Ctrl+C 时 JVM 已进入关闭阶段，hook 会负责清理。
                    try { Runtime.getRuntime().removeShutdownHook(hook); }
                    catch (IllegalStateException shuttingDown) { /* hook 已开始执行 */ }
                }
            } else {
                var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + server.port() + "/greetings?name=%E5%B0%8F%E6%98%8E")).timeout(Duration.ofSeconds(5)).GET().build();
                try (var client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()) {
                    var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println("GET /greetings: " + response.statusCode() + " " + response.body());
                }
            }
        }
    }
}
