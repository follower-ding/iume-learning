package learn.lesson17_composition;

import learn.lesson15_interfaces.ConsoleSender;
import learn.lesson15_interfaces.RecordingSender;

public class CompositionDemo {
    public static void main(String[] args) {
        // —— 讲义主路径 ——
        RecordingSender sender = new RecordingSender();
        NotificationService service = new NotificationService(sender);
        System.out.println(service.notifyUser("订单已发货"));
        System.out.println("最后消息=" + sender.lastMessage());

        // —— 练习1：ConsoleSender 通知「你好」——
        System.out.println(new NotificationService(new ConsoleSender()).notifyUser("你好"));

        // —— 练习2：改传 RecordingSender 读最后消息 ——
        RecordingSender rec = new RecordingSender();
        NotificationService recService = new NotificationService(rec);
        recService.notifyUser("练习二消息");
        System.out.println("练习2最后消息=" + rec.lastMessage());

        // —— 练习3：welcome 委托 notifyUser ——
        System.out.println(service.welcome("iume"));
    }
}
