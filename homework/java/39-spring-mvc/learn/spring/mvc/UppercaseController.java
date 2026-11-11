package learn.spring.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** 练习3：POST /uppercase，空白姓名抛 IllegalArgumentException，由 ApiExceptionHandler 转 400。 */
@RestController
public class UppercaseController {
    public record Input(String name) { }
    public record Output(String message) { }

    @PostMapping("/uppercase")
    @ResponseStatus(HttpStatus.CREATED)
    public Output uppercase(@RequestBody Input input) {
        if (input.name() == null || input.name().isBlank())
            throw new IllegalArgumentException("name 不能为空");
        return new Output(input.name().toUpperCase());
    }
}
