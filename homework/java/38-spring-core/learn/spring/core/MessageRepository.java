package learn.spring.core;

/** 问候语来源；接口让服务不依赖具体存储方式。 */
@FunctionalInterface
public interface MessageRepository {
    String messageFor(String name);
}
