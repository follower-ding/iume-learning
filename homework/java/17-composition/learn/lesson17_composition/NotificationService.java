package learn.lesson17_composition;

import learn.lesson15_interfaces.MessageSender;

public class NotificationService {
    private final MessageSender sender;

    public NotificationService(MessageSender sender) {
        if (sender == null) {
            throw new IllegalArgumentException("发送器不能为空");
        }
        // 保存协作者引用，之后把发送工作交给它。
        this.sender = sender;
    }

    public String notifyUser(String message) {
        return sender.send(message);
    }

    // 练习3：welcome 内部委托 notifyUser
    public String welcome(String name) {
        return notifyUser("欢迎，" + name);
    }
}
