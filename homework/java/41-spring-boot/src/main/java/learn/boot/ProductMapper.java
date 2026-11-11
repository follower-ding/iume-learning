package learn.boot;

import org.apache.ibatis.annotations.Param;

/** Mapper 接口的方法绑定到 resources/learn/boot/ProductMapper.xml。 */
public interface ProductMapper {
    Product findById(@Param("id") long id);
    Product findByName(@Param("name") String name);
    int insert(Product product);
    int update(Product product);
    int delete(@Param("id") long id);
}
