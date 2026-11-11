package learn.boot;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 事务放在服务边界：控制器不负责 commit/rollback。 */
@Service
public class ProductService {
    private final ProductMapper mapper;
    public ProductService(ProductMapper mapper) { this.mapper = mapper; }

    public Product find(long id) {
        var product = mapper.findById(id);
        if (product == null) throw new ProductNotFoundException(id);
        return product;
    }

    @Transactional
    public Product create(String name, int price) {
        var product = new Product(name, price);
        mapper.insert(product);
        return product;
    }

    @Transactional
    public Product update(long id, String name, int price) {
        find(id);
        var product = new Product(name, price);
        product.setId(id);
        if (mapper.update(product) == 0) throw new ProductNotFoundException(id);
        return product;
    }

    @Transactional
    public void delete(long id) {
        if (mapper.delete(id) == 0) throw new ProductNotFoundException(id);
    }

    @Transactional
    public void createTwo(String firstName, String secondName, int price) {
        mapper.insert(new Product(firstName, price));
        mapper.insert(new Product(secondName, price));
        // 第二次违反唯一约束时，运行时异常穿过代理，第一次 INSERT 随之回滚。
    }
}
