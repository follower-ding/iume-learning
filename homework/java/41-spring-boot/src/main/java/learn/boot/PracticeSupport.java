package learn.boot;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/** 练习共用：端口 0 + 独立 H2，用完关上下文，不占 8085。 */
final class PracticeSupport {
    private PracticeSupport() { }

    static ConfigurableApplicationContext start(String database) {
        return SpringApplication.run(LearningApplication.class,
            "--server.port=0",
            "--spring.datasource.url=jdbc:h2:mem:" + database + ";DB_CLOSE_DELAY=-1");
    }

    static int port(ConfigurableApplicationContext context) {
        return Integer.parseInt(context.getEnvironment().getProperty("local.server.port"));
    }

    static HttpResponse<String> request(HttpClient client, int port, String method, String path, String body)
        throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
            .timeout(Duration.ofSeconds(5))
            .header("Content-Type", "application/json; charset=utf-8");
        var request = builder.method(method,
            body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
