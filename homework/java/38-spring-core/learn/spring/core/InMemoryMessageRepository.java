package learn.spring.core;

/** 第一课先使用内存实现，不需要数据库。 */
public class InMemoryMessageRepository implements MessageRepository {
    @Override
    public String messageFor(String name) {
        return "你好，" + name;
    }
}
