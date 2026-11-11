package learn.mybatis;

/** 练习2 修改：插入尺子后改成「长尺」并提交，再读出、删除、提交，确认查到 null。 */
public class PracticeTwo {
    public static void main(String[] args) {
        var factory = MyBatisDemo.createFactory("jdbc:h2:mem:practice_two;DB_CLOSE_DELAY=-1");
        try (var session = factory.openSession()) {
            var mapper = session.getMapper(ProductMapper.class);
            mapper.createTable();
            mapper.insert(new Product(3, "尺子", 8));
            mapper.update(new Product(3, "长尺", 10));
            session.commit();
        }
        try (var session = factory.openSession()) {
            var mapper = session.getMapper(ProductMapper.class);
            Product saved = mapper.findById(3);
            System.out.println(saved.getName() + " " + saved.getPrice());
            mapper.delete(3);
            session.commit();
        }
        try (var session = factory.openSession()) {
            System.out.println(session.getMapper(ProductMapper.class).findById(3) == null);
        }
    }
}
