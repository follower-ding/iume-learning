package learn.spring.mvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 练习2 修改：用 MockMvc standaloneSetup 断言 /health，
 * 不必把 HealthController 注册进课程 WebConfig。
 */
public class PracticeTwo {
    public static void main(String[] args) throws Exception {
        MockMvcBuilders.standaloneSetup(new HealthController()).build()
            .perform(get("/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("运行中"));
        System.out.println("GET /health: 200 {\"message\":\"运行中\"}");
    }
}
