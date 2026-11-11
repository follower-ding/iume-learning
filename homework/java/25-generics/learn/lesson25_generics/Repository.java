package learn.lesson25_generics;

import java.util.List;
import java.util.Optional;

/** 仓储契约：实体类型 T + 键类型 ID，调用处拿编译期类型检查。 */
public interface Repository<T, ID> {
    void save(ID id, T value);

    Optional<T> findById(ID id);

    List<T> findAll();

    boolean deleteById(ID id);
}
