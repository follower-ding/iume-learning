package learn.lesson22_set;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * 第22课三级练习（先预测再运行）：
 * 1) LinkedHashSet 连续 add A、B、A 的返回值与顺序
 * 2) peopleByAgeThenName 加 19 岁的人，预测位置
 * 3) Member 按 name 再按 id 的 TreeSet；只按 name 会丢同名
 */
public final class SetExercises {
    private SetExercises() {}

    public record Member(String id, String name) {}
    public record Person(String name, int age) {}

    public static void main(String[] args) {
        // 练习1：预测 true,true,false；顺序 A、B
        Set<String> inserted = new LinkedHashSet<>();
        System.out.println("练习1-addA=" + inserted.add("A"));
        System.out.println("练习1-addB=" + inserted.add("B"));
        System.out.println("练习1-addA再=" + inserted.add("A"));
        System.out.println("练习1-顺序=" + inserted);

        // 练习2：加 19 岁的人，应排在两位 20 岁之前
        Set<Person> people = new TreeSet<>(new Comparator<Person>() {
            @Override public int compare(Person left, Person right) {
                int byAge = Integer.compare(left.age(), right.age());
                return byAge != 0 ? byAge : left.name().compareTo(right.name());
            }
        });
        people.add(new Person("ann", 20));
        people.add(new Person("bob", 20));
        people.add(new Person("cara", 19));
        System.out.print("练习2-顺序=");
        boolean first = true;
        for (Person p : people) {
            if (!first) System.out.print(", ");
            System.out.print(p.name() + "(" + p.age() + ")");
            first = false;
        }
        System.out.println();

        // 练习3：按 name 再按 id；只按 name 会丢同名不同 id
        Set<Member> byNameThenId = new TreeSet<>(new Comparator<Member>() {
            @Override public int compare(Member a, Member b) {
                int byName = a.name().compareTo(b.name());
                return byName != 0 ? byName : a.id().compareTo(b.id());
            }
        });
        byNameThenId.add(new Member("m1", "ann"));
        byNameThenId.add(new Member("m2", "ann")); // 同名不同 id，应保留
        byNameThenId.add(new Member("m3", "bob"));
        System.out.println("练习3-name+id数量=" + byNameThenId.size());

        Set<Member> byNameOnly = new TreeSet<>(new Comparator<Member>() {
            @Override public int compare(Member a, Member b) {
                return a.name().compareTo(b.name());
            }
        });
        byNameOnly.add(new Member("m1", "ann"));
        byNameOnly.add(new Member("m2", "ann")); // 比较为 0，第二个不加入
        byNameOnly.add(new Member("m3", "bob"));
        System.out.println("练习3-只name数量=" + byNameOnly.size());
    }
}
