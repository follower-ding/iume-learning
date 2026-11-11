package learn.spring.core;

/** 依赖从构造器传入；服务只负责使用依赖，不要在内部 new 仓储。 */
public class GreetingService {
    private final MessageRepository repository;

    public GreetingService(MessageRepository repository) {
        this.repository = repository;
    }

    public String greet(String name) {
        return repository.messageFor(name);
    }
}
