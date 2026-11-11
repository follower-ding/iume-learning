package learn.mybatis;

/** 练习1 模仿：插入 id=2 的橡皮并提交，换新 session 再查名字。 */
public class PracticeOne {
    public static void main(String[] args) {
        var factory = MyBatisDemo.createFactory("jdbc:h2:mem:practice_one;DB_CLOSE_DELAY=-1");
        try (var session = factory.openSession()) {
            var mapper = session.getMapper(ProductMapper.class);
            mapper.createTable();
            mapper.insert(new Product(2, "橡皮", 5));
            session.commit();
        }
        try (var session = factory.openSession()) {
            System.out.println(session.getMapper(ProductMapper.class).findById(2).getName());
        }
    }
}
