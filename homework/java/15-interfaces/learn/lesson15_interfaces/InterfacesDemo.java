package learn.lesson15_interfaces;

public class InterfacesDemo {
    public static void main(String[] args) {
        // ===== 讲义正文（期望两行）=====
        MessageSender first = new ConsoleSender();
        MessageSender second = new RecordingSender();
        System.out.println(first.send("你好"));
        System.out.println(second.send("你好"));

        // ===== 练习1：模仿——ConsoleSender 发「早上好」=====
        System.out.println("练习1 " + new ConsoleSender().send("早上好"));

        // ===== 练习2：修改——RecordingSender + lastMessage()=====
        RecordingSender recorder = new RecordingSender();
        System.out.println("练习2 send=" + recorder.send("你好"));
        System.out.println("练习2 lastMessage=" + recorder.lastMessage());

        // ===== 练习3：独立——PrefixSender =====
        MessageSender prefix = new PrefixSender();
        System.out.println("练习3 " + prefix.send("开会啦"));
    }
}
