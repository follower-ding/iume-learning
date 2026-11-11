package learn.lesson15_interfaces;

public class ConsoleSender implements MessageSender {
    // implements 要求这个普通类提供 MessageSender 的 send 方法。
    @Override
    public String send(String message) {
        return "发送:" + message;
    }
}
