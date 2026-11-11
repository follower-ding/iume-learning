package learn.lesson09_classes;

/** 练习探针：改一个对象的 name，另一个不受影响。 */
public class IndependenceProbe {
    public static void main(String[] args) {
        Student a = new Student("小明");
        Student b = new Student("小红");
        a.name = "改过的小明";
        System.out.println(a.introduce());
        System.out.println(b.introduce());
        System.out.println(a.greet("老师"));
    }
}
