package learn.spring.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** 请求进入 DispatcherServlet 后，由映射规则选择这些方法。 */
@RestController
public class GreetingController {
    public record Message(String message) { }
    public record NameInput(String name) { }

    @GetMapping("/greetings")
    public Message greet(@RequestParam("name") String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name 不能为空");
        return new Message("你好，" + name);
    }

    @GetMapping("/greetings/{id}")
    public Message byId(@PathVariable("id") int id) {
        return new Message("你好，" + id + "号访客");
    }

    @PostMapping("/greetings")
    @ResponseStatus(HttpStatus.CREATED)
    public Message fromJson(@RequestBody NameInput input) {
        return greet(input.name());
    }
}
