package learn.mybatis;

import org.apache.ibatis.annotations.Param;

/** 接口方法名必须和 ProductMapper.xml 里的语句 id 一一对应；不用自己写实现。 */
public interface ProductMapper {
    void createTable();
    Product findById(@Param("id") int id);
    int insert(Product product);
    int update(Product product);
    int delete(@Param("id") int id);
}
