package learn.spring.mvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 练习3 独立：MockMvc 断言成功 201 / ABC，空白姓名 400。
 * 必须挂上 ApiExceptionHandler，空白姓名才会变成 JSON 400。
 * 不改课程 GreetingController / WebConfig。
 */
public class PracticeThree {
    public static void main(String[] args) throws Exception {
        var mvc = MockMvcBuilders.standaloneSetup(new UppercaseController())
            .setControllerAdvice(new ApiExceptionHandler())
            .build();
        mvc.perform(post("/uppercase").contentType("application/json").content("{\"name\":\"abc\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value("ABC"));
        mvc.perform(post("/uppercase").contentType("application/json").content("{\"name\":\" \"}"))
            .andExpect(status().isBadRequest());
        System.out.println("POST /uppercase abc -> 201 {\"message\":\"ABC\"}");
        System.out.println("POST /uppercase 空白 -> 400");
    }
}
