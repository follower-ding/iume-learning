package learn.mybatis;

/**
 * 练习3 独立：先提交书签，再在新 session 改成「金属书签」后回滚。
 * 必须再开一个 session 做 SELECT，才能证明库里仍是书签。
 * 只看同一个 session 里手里的 Java 对象，看到的是内存状态，不是库里的最终结果。
 */
public class PracticeThree {
    public static void main(String[] args) {
        var factory = MyBatisDemo.createFactory("jdbc:h2:mem:practice_three;DB_CLOSE_DELAY=-1");
        try (var session = factory.openSession()) {
            var mapper = session.getMapper(ProductMapper.class);
            mapper.createTable();
            mapper.insert(new Product(4, "书签", 3));
            session.commit();
        }
        try (var session = factory.openSession()) {
            session.getMapper(ProductMapper.class).update(new Product(4, "金属书签", 9));
            session.rollback();
        }
        try (var session = factory.openSession()) {
            var saved = session.getMapper(ProductMapper.class).findById(4);
            System.out.println(saved.getName() + " " + saved.getPrice());
        }
    }
}
