package learn.lesson15_interfaces;

// 练习3：独立——自定义前缀的 MessageSender
public class PrefixSender implements MessageSender {
    @Override
    public String send(String message) {
        return "通知:" + message;
    }
}
