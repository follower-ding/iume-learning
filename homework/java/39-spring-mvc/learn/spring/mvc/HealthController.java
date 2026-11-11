package learn.spring.mvc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 练习2：新增 GET /health，不改课程 GreetingController / WebConfig。 */
@RestController
public class HealthController {
    @GetMapping("/health")
    public GreetingController.Message health() {
        return new GreetingController.Message("运行中");
    }
}
