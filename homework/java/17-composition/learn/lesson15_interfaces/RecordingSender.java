package learn.lesson15_interfaces;

public class RecordingSender implements MessageSender {
    private String lastMessage;

    // 同一接口可以有另一种实现：这里仅记录消息。
    @Override
    public String send(String message) {
        lastMessage = message;
        return "已记录:" + message;
    }

    public String lastMessage() {
        return lastMessage;
    }
}
