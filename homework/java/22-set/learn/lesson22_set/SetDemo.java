package learn.lesson22_set;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public final class SetDemo {
    private SetDemo() {}
    public record Person(String name, int age) {}

    public static List<String> uniqueSortedNames(List<String> names) {
        Set<String> unique = new HashSet<>(names);
        List<String> sorted = new ArrayList<>(unique);
        sorted.sort(Comparator.naturalOrder()); // HashSet 无顺序保证，打印前排序。
        return List.copyOf(sorted);
    }

    public static List<Integer> naturalOrderNumbers() {
        Set<Integer> numbers = new TreeSet<>(); // Integer 自带自然顺序。
        numbers.add(3);
        numbers.add(1);
        numbers.add(2);
        return List.copyOf(numbers);
    }

    public static Set<Person> peopleByAgeOnly() {
        // 先用学过的接口实现类写比较规则；Lambda 到第27课再学。
        Set<Person> people = new TreeSet<>(new Comparator<Person>() {
            @Override public int compare(Person left, Person right) {
                return Integer.compare(left.age(), right.age());
            }
        });
        people.add(new Person("ann", 20));
        people.add(new Person("bob", 20)); // 比较结果 0，被 TreeSet 视为重复。
        return people;
    }

    public static List<String> peopleByAgeThenName() {
        Set<Person> people = new TreeSet<>(new Comparator<Person>() {
            @Override public int compare(Person left, Person right) {
                int byAge = Integer.compare(left.age(), right.age());
                return byAge != 0 ? byAge : left.name().compareTo(right.name());
            }
        });
        people.add(new Person("ann", 20));
        people.add(new Person("bob", 20));
        List<String> names = new ArrayList<>();
        for (Person person : people) names.add(person.name());
        return List.copyOf(names);
    }

    public static void main(String[] args) {
        Set<String> inserted = new LinkedHashSet<>(List.of("bob", "ann", "bob"));
        System.out.println("去重且排序=" + uniqueSortedNames(List.of("bob", "ann", "bob")));
        System.out.println("插入顺序=" + inserted);
        System.out.println("自然顺序=" + naturalOrderNumbers());
        System.out.println("年龄比较数量=" + peopleByAgeOnly().size());
        System.out.println("补充姓名后=" + peopleByAgeThenName());
    }
}
