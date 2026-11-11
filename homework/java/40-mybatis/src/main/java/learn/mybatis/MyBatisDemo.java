package learn.mybatis;

import java.io.IOException;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

public class MyBatisDemo {
    public static SqlSessionFactory createFactory(String url) {
        var dataSource = new UnpooledDataSource("org.h2.Driver", url, "sa", "");
        var environment = new Environment("lesson", new JdbcTransactionFactory(), dataSource);
        var configuration = new Configuration(environment);
        configuration.setMapUnderscoreToCamelCase(true);
        // XML 从 classpath 读取（练习副本放在 src/main/resources），工厂只建一次。
        try (var input = MyBatisDemo.class.getResourceAsStream("/learn/mybatis/ProductMapper.xml")) {
            if (input == null) throw new IllegalStateException("缺少 ProductMapper.xml");
            new XMLMapperBuilder(input, configuration, "learn/mybatis/ProductMapper.xml", configuration.getSqlFragments()).parse();
        } catch (IOException e) {
            throw new IllegalStateException("无法读取 SQL 映射", e);
        }
        return new SqlSessionFactoryBuilder().build(configuration);
    }

    public static void main(String[] args) {
        var factory = createFactory("jdbc:h2:mem:mybatis_demo;DB_CLOSE_DELAY=-1");
        try (var session = factory.openSession()) {
            var mapper = session.getMapper(ProductMapper.class);
            mapper.createTable();
            mapper.insert(new Product(1, "纸笔", 12));
            session.commit(); // 默认不是自动提交。
        }
        try (var session = factory.openSession()) {
            var mapper = session.getMapper(ProductMapper.class);
            System.out.println("查询: " + mapper.findById(1).getName());
            mapper.update(new Product(1, "钢笔", 18));
            session.rollback(); // 撤销本次修改。
        }
        try (var session = factory.openSession()) {
            System.out.println("回滚后: " + session.getMapper(ProductMapper.class).findById(1).getName());
        }
    }
}
