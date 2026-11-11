package learn.lesson10_constructors;

public class ConstructorsDemo {
    public static void main(String[] args) {
        Book blank = new Book();
        Book named = new Book("Java入门");
        System.out.println(blank.title());
        System.out.println(named.title());

        // 练习1：再造一本「算法」并打印
        System.out.println(new Book("算法").title());

        // 练习3：带版次
        System.out.println(new Book("Java", 2).title());
    }
}
